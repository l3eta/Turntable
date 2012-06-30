package org.l3eta.tt;

import static org.l3eta.util.Crypto.hash;
import static org.l3eta.util.Crypto.randHash;

import java.util.ArrayList;
import java.util.List;

import org.l3eta.tt.Enums.Laptop;
import org.l3eta.tt.Enums.LogLevel;
import org.l3eta.tt.Enums.Status;
import org.l3eta.tt.Enums.TurntableCommand;
import org.l3eta.tt.Enums.Vote;
import org.l3eta.tt.Room.Users;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.db.Database;
import org.l3eta.tt.db.DefaultDatabase;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.event.DjEvent;
import org.l3eta.tt.event.EndSongEvent;
import org.l3eta.tt.event.ModEvent;
import org.l3eta.tt.event.NoSongEvent;
import org.l3eta.tt.event.RoomChangeEvent;
import org.l3eta.tt.event.RoomUpdateEvent;
import org.l3eta.tt.event.SnagEvent;
import org.l3eta.tt.event.SongEvent;
import org.l3eta.tt.event.UserBootEvent;
import org.l3eta.tt.event.UserJoinEvent;
import org.l3eta.tt.event.UserLeaveEvent;
import org.l3eta.tt.event.UserUpdateEvent;
import org.l3eta.tt.event.VoteEvent;
import org.l3eta.tt.manager.BotWindow;
import org.l3eta.tt.manager.CommandManager;
import org.l3eta.tt.manager.EventManager;
import org.l3eta.tt.task.RepeatingTask;
import org.l3eta.tt.user.Profile;
import org.l3eta.tt.util.BotMessage;
import org.l3eta.tt.util.BotMessage.MessageCallback;
import org.l3eta.tt.util.DirectoryGraph;
import org.l3eta.tt.util.DirectoryGraph.DirectoryGraphCallback;
import org.l3eta.tt.util.External;
import org.l3eta.tt.util.External.ExternalAPICallback;
import org.l3eta.tt.util.Message;
import org.l3eta.tt.util.Timestamp;
import org.l3eta.tt.ws.WebSocket;

import com.mongodb.BasicDBList;

public class Bot {
	public static final String version = "3.0.1";
	private String auth, roomid, clientid, userid;
	private int msgid, debugLevel;
	private boolean externalPMs, debug, closed, loaded;

	private Database database;
	private Room room = null;
	private List<BotMessage> requests;
	private List<Song> playlist;
	private List<String> fanOf;
	private char[] commandStarts;
	private User self;
	private WebSocket ws;
	private EventManager eventManager;
	private CommandManager commandManager;
	private BotWindow window;
	private Timestamp lastActivity, lastHeartbeat, startTime;
	private RepeatingTask presenceTask;

	public final int DEFAULT_DEBUG = 0, RAW_DEBUG = 1, API_DEBUG = 5;
	public final int STRICT = 10, STRICT_API = STRICT + API_DEBUG, STRICT_RAW = RAW_DEBUG + STRICT;

	public Bot() {
		if (room == null) {
			room = new Room(this);
			Song.setRoom(room);
		}
		commandStarts = new char[] { '/', '*', '-', '.', '!', '~', '^' };
		playlist = new ArrayList<Song>();
		fanOf = new ArrayList<String>();
		requests = new ArrayList<BotMessage>();
		eventManager = new EventManager();
		commandManager = new CommandManager(this);
		msgid = 0;
		database = new DefaultDatabase("default");
	}

	public Bot(String auth, String userid, String roomid) {
		this();
		this.auth = auth;
		this.userid = userid;
		this.roomid = roomid;
		lastHeartbeat = Timestamp.now();
		lastActivity = Timestamp.now();
		startTime = Timestamp.now();
		clientid = System.currentTimeMillis() + "-" + Math.random();
		ws = getWebSocket();
		ws.start();
	}

	private final WebSocket getWebSocket() {
		msgid = 0;
		WebSocket ws = new WebSocket(this) {
			public void onMessage(String msg) {
				if (msg.equals("~m~10~m~no_session")) {
					authUser();
					return;
				}
				if (Util.match(msg, "~m~[0-9]+~m~(~h~[0-9]+)")) {
					sendBeat(Util.matchFind(msg, "~m~[0-9]+~m~(~h~[0-9]+)", 1));
					lastHeartbeat = Timestamp.now();
					return;
				}
				Message message = new Message(msg);
				lastActivity = Timestamp.now();
				debug("> " + message, RAW_DEBUG);
				for (BotMessage bm : getMessages()) {
					if (message.containsField("msgid")) {
						if (bm.getMsgID() == message.getInt("msgid")) {
							if (bm.hasCallback())
								bm.runCallback(message);
							requests.remove(msg);
						}
					}
				}
				Bot.this.onMessage(message);
			}
		};
		return ws;
	}

	public final void setCommandStarts(char[] c) {
		this.commandStarts = c;
	}

	public final CommandManager getCommandManager() {
		return commandManager;
	}

	public final void setExternalPMs(boolean on) {
		this.externalPMs = on;
	}

	public final void send(Message rq, MessageCallback callback) {
		rq.append("msgid", msgid);
		rq.append("clientid", clientid);
		if (!rq.containsField("userid"))
			rq.append("userid", userid);
		rq.append("userauth", auth);
		debug("< " + rq, RAW_DEBUG);
		ws.send("~m~" + rq.length() + "~m~" + rq);
		requests.add(new BotMessage(msgid, rq, callback));
		msgid++;
	}

	public final String[] trimArray(String[] o, int index) {
		List<String> ox = new ArrayList<String>();
		for (int i = index; i < o.length; i++) {
			ox.add(o[i]);
		}
		return ox.toArray(new String[0]);
	}

	private final void onMessage(final Message msg) {
		if (msg.containsField("err"))
			error(msg.toString());
		String cmd = "other";
		if (msg.containsField("command"))
			cmd = msg.getString("command");
		TurntableCommand command = TurntableCommand.get(cmd);
		User user;
		Message temp;
		Song song;
		final String stemp;
		switch (command) {
			case KILLDASHNINE:
				error("Session killed by Turntable.fm");
				System.exit(0);
				break;
			case SPEAK:
				stemp = msg.getString("text").trim();
				user = getUsers().getByID(msg.getString("userid"));
				if (isCommand(stemp)) {
					String[] args = stemp.substring(1).split(" ");
					if (commandManager.hasCommand(args[0])) {
						Command c = commandManager.getCommand(args[0]);
						if (c.canExecute(user))
							c.execute(user, trimArray(args, 1), ChatType.MAIN);
					}
				}
				eventManager.sendEvent(new ChatEvent(user, stemp, ChatType.MAIN));
				break;
			case UPDATE_ROOM:
				eventManager.sendEvent(new RoomUpdateEvent(msg.getString("description")));
				break;
			case REGISTERED:
				temp = new Message(((BasicDBList) msg.get("user")).get(0));
				user = new User(temp);
				getUsers().addUser(user);
				if (temp.getString("userid").equals(userid))
					self = user;
				eventManager.sendEvent(new UserJoinEvent(user));
				break;
			case DEREGISTERED:
				temp = new Message(((BasicDBList) msg.get("user")).get(0));
				user = getUsers().getByID(temp);
				eventManager.sendEvent(new UserLeaveEvent(user));
				getUsers().removeUser(user);
				break;
			case UPDATE_VOTES:
				eventManager.sendEvent(new VoteEvent(room, msg.getSubObject("room", "metadata")));
				break;
			case ADD_DJ:
				temp = new Message(((BasicDBList) msg.get("user")).get(0));
				user = getUsers().getByID(temp);
				room.addDj(user);
				eventManager.sendEvent(new DjEvent(user, true));
				break;
			case NOSONG:
				eventManager.sendEvent(new EndSongEvent(getCurrentSong()));
				room.setCurrentSong(null);
				eventManager.sendEvent(new NoSongEvent(msg));
				break;
			case NEWSONG:
				if (getCurrentSong() != null)
					eventManager.sendEvent(new EndSongEvent(getCurrentSong()));
				temp = msg.getSubObject("room", "metadata", "current_song");
				user = getUsers().getByID(temp.getString("djid"));
				song = new Song(temp, false);
				room.setCurrentSong(song);
				eventManager.sendEvent(new SongEvent(user, song));
				break;
			case BOOTED_USER:
				user = getUsers().getByID(msg);
				User mod = getUsers().getByID(msg.getString("modid"));
				eventManager.sendEvent(new UserBootEvent(user, mod, msg.getString("reason")));
				break;
			case UPDATE_USER:
				user = getUsers().getByID(msg);
				UserUpdateEvent uue = new UserUpdateEvent(user, msg);
				String field = uue.getField();
				if (field.equals("fans")) {
					String fan = getInt(uue.getValue()) > 0 ? "gained" : "lost";
					debug(String.format("%s, has %s a fan!", user.getName(), fan), DEFAULT_DEBUG);
				} else if (field.equals("name")) {
					String name = uue.getValue().toString();
					debug(user.getName() + " changed their name to '" + name + "'", DEFAULT_DEBUG);
					user.setName(name);
				} else if (field.equals("avatar")) {
					int id = getInt(uue.getValue());
					user.setAvatar(id);
					debug(user.getName() + " changed their avatar to '" + id + "'.", DEFAULT_DEBUG);
				}
				eventManager.sendEvent(uue);
				break;
			case REM_DJ:
				temp = new Message(((BasicDBList) msg.get("user")).get(0));
				user = getUsers().getByID(temp);
				room.remDj(user);
				DjEvent de = new DjEvent(user, false);
				if (msg.has("modid"))
					de = new DjEvent(user, getUsers().getByID(msg.getString("modid")));
				eventManager.sendEvent(de);
				break;
			case NEW_MODERATOR:
				user = getUsers().getByID(msg);
				eventManager.sendEvent(new ModEvent(user, true));
				break;
			case REM_MODERATOR:
				user = getUsers().getByID(msg);
				eventManager.sendEvent(new ModEvent(user, false));
				break;
			case SNAGGED:
				user = getUsers().getByID(msg);
				getCurrentSong().addSnag();
				eventManager.sendEvent(new SnagEvent(user, getCurrentSong()));
				break;
			case PMMED:
				String userid = msg.getString("senderid");
				stemp = msg.getString("text").trim();
				if (externalPMs && !getUsers().inRoom(userid)) {
					ExternalAPICallback callback = new ExternalAPICallback() {
						public final void run(Message message, boolean success) {
							if (success) {
								User user = new User(message);
								if (isCommand(stemp)) {
									String[] args = stemp.substring(1).split(" ");
									if (commandManager.hasCommand(args[0])) {
										Command c = commandManager.getCommand(args[0]);
										if (c.canExecute(user))
											c.execute(user, (String[]) trimArray(args, 1), ChatType.PM);
									}
								}
								eventManager.sendEvent(new ChatEvent(user, stemp, ChatType.PM));
							}
						}
					};
					External.getUserInfo(callback, userid);
				} else if (getUsers().inRoom(userid)) {
					user = getUsers().getByID(userid);
					if (user.getStatus() == Status.NO_PM) {
						info(user.getName() + " is using an OS that does not support Private Messaging");
					} else {
						if (isCommand(stemp)) {
							String[] args = stemp.substring(1).split(" ");
							if (commandManager.hasCommand(args[0])) {
								Command c = commandManager.getCommand(args[0]);
								if (c.canExecute(user))
									c.execute(user, (String[]) trimArray(args, 1), ChatType.PM);
							}
						}
						eventManager.sendEvent(new ChatEvent(user, stemp, ChatType.PM));
					}

				}
				break;
			case ROOM_CHANGED:
				RoomChangeEvent rce = new RoomChangeEvent(msg);
				room.setData(rce.getRoomData());
				eventManager.sendEvent(rce);
				break;
			case OTHER:
				debug(msg, 2); // TODO name this value
				break;
			default:
				debug(msg, API_DEBUG);
				break;
		}
	}

	public boolean isCommand(String text) {
		if (text.length() == 0)
			return false;
		for (char c : commandStarts) {
			if (c == text.charAt(0))
				return true;
		}
		return false;
	}

	public final boolean isEmpty(String text) {
		return text.trim().equals("");
	}

	public final Timestamp getStartTime() {
		return startTime;
	}

	public final Timestamp getLastActivity() {
		return lastActivity;
	}

	public final Timestamp getLastHeartbeat() {
		return lastHeartbeat;
	}

	public final void setMessageID(int msgid) {
		this.msgid = msgid;
	}

	public final EventManager getEventManager() {
		return eventManager;
	}

	public final String getServer() {
		return External.getChatServer(roomid);
	}

	@SuppressWarnings("unchecked")
	private List<BotMessage> getMessages() {
		return (List<BotMessage>) ((ArrayList<BotMessage>) requests).clone();
	}

	public final List<Song> getSonglist() {
		return playlist;
	}

	public final List<String> getFanOfList() {
		return fanOf;
	}

	public final int getInt(Object value) {
		return Integer.parseInt(value.toString());
	}

	private Song getCurrentSong() {
		return room.getCurrentSong();
	}

	public final void setDatabase(Database database) {
		this.database = database;
	}

	public final Database getDatabase() {
		return database;
	}

	public final User getSelf() {
		return self;
	}

	public final Room getRoom() {
		return room;
	}

	public final String format(String text, Object... o) {
		return String.format(text, o);
	}

	public final boolean hasWindow() {
		return window != null;
	}

	private void sendBeat(String line) {
		ws.send("~m~" + line.length() + "~m~" + line);
		debug("HeartBeat: " + ("~m~" + line.length() + "~m~" + line), DEFAULT_DEBUG);
		msgid++;
	}

	public final void unBlock(String userid) {
		unBlock(userid, null);
	}

	public final void unBlock(String userid, MessageCallback callback) {
		Object[] _rq = { "api", "block.remove", "blockedid", userid };
		send(makeMessage(_rq), callback);
	}

	public final void getBlockList(MessageCallback callback) {
		Object[] _rq = { "api", "block.list_all" };
		send(makeMessage(_rq), callback);
	}

	public final void getPMHistory(String userid, MessageCallback callback) {
		Object[] _rq = { "api", "pm.history", "receiverid", userid };
		send(makeMessage(_rq), callback);
	}

	public final void sendPM(User user, String text) {
		sendPM(user, text, null);
	}

	public final void sendPM(User user, String text, MessageCallback callback) {
		sendPM(user.getID(), text, callback);
	}

	public final void sendPM(String userid, String text) {
		sendPM(userid, text, null);
	}

	public final void sendPM(String userid, String text, MessageCallback callback) {
		Object[] _rq = { "api", "pm.send", "receiverid", userid, "text", text };
		send(makeMessage(_rq), callback);
	}

	public final void block(String userid) {
		block(userid, null);
	}

	public final void block(String userid, MessageCallback callback) {
		Object[] _rq = { "api", "block.add", "blockedid", userid };
		send(makeMessage(_rq), callback);
	}

	public final void getUserID(String name) {
		getUserID(name, null);
	}

	public final void getUserID(String name, MessageCallback callback) {
		Object[] _rq = { "api", "user.get_id", "name", name };
		send(makeMessage(_rq), callback);
	}

	public final void updatePresence() {
		sendPresence(null);
	}

	public final void sendPresence(MessageCallback callback) {
		Object[] _rq = { "api", "presence.update", "status", self.getStatus().toString() };
		send(makeMessage(_rq), callback);
	}

	public final void modifyRoom(String info) {
		modifyRoom(info, null);
	}

	public final void modifyRoom(String info, MessageCallback callback) {
		Object[] _rq = { "api", "room.modify", "roomid", roomid, "description", info };
		send(makeMessage(_rq), callback);
	}

	public void fan(User user) {
		fan(user, null);
	}

	public void fan(User user, MessageCallback callback) {
		fan(user.getID(), callback);
	}

	public final void fan(String userid) {
		fan(userid, null);
	}

	public final void fan(String userid, MessageCallback callback) {
		Object[] _rq = { "api", "user.become_fan", "djid", userid };
		send(makeMessage(_rq), callback);
	}

	public final void joinRoom() {
		joinRoom(roomid);
	}

	public final void joinRoom(String roomid) {
		joinRoom(roomid, null);
	}

	public final void joinRoom(String roomid, MessageCallback callback) {
		Object[] _rq = { "api", "room.register", "roomid", roomid };
		send(makeMessage(_rq), callback);
	}

	public final void roomInfo() {
		roomInfo(roomid, new MessageCallback() {
			public final void run(Message reply) {
				onMessage(reply.append("command", "room.changed"));
				presenceTask = new RepeatingTask(new Runnable() {
					public final void run() {
						updatePresence();
					}
				}, 20000);
				presenceTask.start();
				getPlaylist(new MessageCallback() {
					public final void run(Message reply) {
						debug("Loading Playlist", DEFAULT_DEBUG);
						for (Message song : reply.getMessageList("list")) {
							playlist.add(new Song(song, true));
						}
						debug("Loaded " + playlist.size() + " songs.", DEFAULT_DEBUG);
						getFanList(new MessageCallback() {
							public final void run(Message reply) {
								debug("Loading fan list", DEFAULT_DEBUG);
								for (String userid : reply.getStringList("fanof")) {
									if (!fanOf.contains(userid))
										fanOf.add(userid);
								}
								debug("Loaded " + fanOf.size() + " fans.", DEFAULT_DEBUG);
								Bot.this.loaded = true;
							}
						});

					}
				});
			}
		});
	}

	public final void roomInfo(MessageCallback callback) {
		roomInfo(roomid, callback);
	}

	public final void roomInfo(String roomid, MessageCallback callback) {
		Object[] _rq = { "api", "room.info", "roomid", roomid, "extended", true };
		send(makeMessage(_rq), callback);
	}

	public final void leaveRoom() {
		leaveRoom(null);
	}

	public final void leaveRoom(MessageCallback callback) {
		Object[] _rq = { "api", "room.deregister", "roomid", roomid };
		send(makeMessage(_rq), callback);
	}

	public final void getFavorites() {
		getFavorites(null);
	}

	public final void getFavorites(MessageCallback callback) {
		Object[] _rq = { "api", "room.get_favorites" };
		send(makeMessage(_rq), callback);
	}

	public final void favorite(String roomid) {
		favorite(roomid, null);
	}

	public final void favorite(MessageCallback callback) {
		favorite(roomid, callback);
	}

	public final void favorite(String roomid, MessageCallback callback) {
		Object[] _rq = { "api", "room.add_favorite", "roomid", roomid };
		send(makeMessage(_rq), callback);
	}

	public final void getBuddies(MessageCallback callback) {
		Object[] _rq = { "api", "user.get_buddies" };
		send(makeMessage(_rq), callback);
	}

	public final void speak(String text) {
		speak(text, null);
	}

	public final void speak(String text, MessageCallback callback) {
		Object[] _rq = { "api", "room.speak", "roomid", roomid, "text", text };
		send(makeMessage(_rq), callback);
	}

	public final void unFavorite() {
		unFavorite(roomid);
	}

	public final void unFavorite(String roomid) {
		unFavorite(roomid, null);
	}

	public final void unFavorite(String roomid, MessageCallback callback) {
		Object[] _rq = { "api", "room.rem_favoraite", "roomid", roomid };
		send(makeMessage(_rq), callback);
	}

	public final void mod(User user) {
		mod(user.getID());
	}

	public final void mod(User user, MessageCallback callback) {
		mod(user.getID(), callback);
	}

	public final void mod(String userid) {
		mod(userid, null);
	}

	public final void mod(String userid, MessageCallback callback) {
		Object[] _rq = { "api", "room.add_moderator", "roomid", roomid, "target_userid", userid };
		send(makeMessage(_rq), callback);
	}

	public final void unMod(String userid) {
		unMod(userid, null);
	}

	public final void unMod(String userid, MessageCallback callback) {
		Object[] _rq = { "api", "room.rem_moderator", "roomid", roomid, "target_userid", userid };
		send(makeMessage(_rq), callback);
	}

	public final void getGraph(final DirectoryGraphCallback callback) {
		MessageCallback cb = new MessageCallback() {
			public void run(Message message) {
				callback.run(new DirectoryGraph(message));
			}
		};
		Object[] rq = { "api", "room.directory_graph" };
		send(makeMessage(rq), callback == null ? null : cb);
	}

	public final void getPurchasedStickers(MessageCallback callback) {
		Object[] rq = { "api", "sticker.get_purchased_stickers" };
		send(makeMessage(rq), callback);
	}

	public final void getStickerPlacements(MessageCallback callback) {
		Object[] rq = { "api", "sticker.get_placements" };
		send(makeMessage(rq), callback);
	}

	public final void dj() {
		dj(null);
	}

	public final void dj(MessageCallback callback) {
		Object[] _rq = { "api", "room.add_dj", "roomid", roomid };
		send(makeMessage(_rq), callback);
	}

	public final void remDj() {
		remDj(getUsers().getByID(userid));
	}

	public final void remDj(User user) {
		remDj(user, null);
	}

	public final void remDj(User user, MessageCallback callback) {
		Object[] _rq = { "api", "room.rem_dj", "roomid", roomid, "djid", user.getID() };
		user.setDj(false);
		send(makeMessage(_rq), callback);
	}

	public final void skipSong() {
		skipSong(null);
	}

	public final void skipSong(MessageCallback callback) {
		Object[] _rq = { "api", "room.stop_song", "roomid", roomid };
		send(makeMessage(_rq), callback);
	}

	public final void boot(String userid, String reason) {
		boot(userid, reason, null);
	}

	public final void boot(User user, String reason) {
		boot(user, reason, null);
	}

	public final void boot(User user, String reason, MessageCallback callback) {
		boot(user.getID(), reason, callback);
	}

	public final void boot(String userid, String reason, MessageCallback callback) {
		Object[] _rq = { "api", "room.boot_user", "roomid", roomid, "target_userid", userid, "reason", reason };
		send(makeMessage(_rq), callback);
	}

	public final void snag(boolean add, MessageCallback callback) {
		Song song = getCurrentSong();
		String sh = randHash("sha1"), i = String.format("%s/%s/%s/%s/%s/%s/%s/%s", userid, song.getUserID(), song.getID(), roomid, "queue",
				"board", "false", sh);
		Object[] _rq = { "api", "snag.add", "djid", song.getUserID(), "songid", song.getID(), "roomid", roomid, "site", "queue",
				"location", "board", "in_queue", false, "vh", hash(i, "sha1"), "sh", sh, "fh", randHash("sha1") };
		send(makeMessage(_rq), callback);
		if (add)
			addSong();
	}

	public final void vote(Vote vote) {
		vote(vote, null);
	}

	public final void vote(Vote vote, MessageCallback callback) {
		if (getCurrentSong() == null) {
			info("Current Song is NULL we can't vote");
			return;
		}
		String val = vote.toString();
		String vh = hash(roomid + val + getCurrentSong().getID(), "sha1");
		Object[] _rq = { "api", "room.vote", "roomid", roomid, "val", val, "vh", vh, "th", randHash("sha1"), "ph", randHash("sha1") };
		send(makeMessage(_rq), callback);
	}

	public final void addSong() {
		addSong(null);
	}

	public final void addSong(String id) {
		addSong(id, null);
	}

	public final void addSong(String id, MessageCallback callback) {
		addSong(id, playlist.size(), "default", callback);
	}

	public final void addSong(String id, int idx, String playlist, MessageCallback callback) {
		Object[] _rq = { "api", "playlist.add", "playlist_name", playlist, "song_dict", new Message().append("fileid", id), "index", idx };
		send(makeMessage(_rq), callback);
	}

	public final void delSong() {
		delSong(null);
	}

	public final void delSong(MessageCallback callback) {
		delSong(0, callback);
	}

	public final void delSong(int idx, MessageCallback callback) {
		delSong(idx, "default", callback);
	}

	public final void delSong(int idx, String playlist, MessageCallback callback) {
		Object[] _rq = { "api", "playlist.remove", "playlist_name", playlist, "index", idx };
		send(makeMessage(_rq), callback);
	}

	public final void userInfo(MessageCallback callback) {
		Object[] _rq = { "api", "user.info" };
		send(makeMessage(_rq), callback);
	}

	public final void editProfile(Profile profile) {
		editProfile(profile, null);
	}

	public final void editProfile(Profile profile, MessageCallback callback) {
		Object[] _rq = { "api", "user.modify_profile", "name", profile.getName(), "twitter", profile.getTwitter(), "facebook",
				profile.getFacebook(), "website", profile.getWebsite(), "about", profile.getAbout(), "topartists", profile.getTopartists(),
				"hangout", profile.getHangout() };
		send(makeMessage(_rq), callback);
	}

	public final void getFanList(MessageCallback callback) {
		Object[] _rq = { "api", "user.get_fan_of" };
		send(makeMessage(_rq), callback);
	}

	public final void getProfile(MessageCallback callback) {
		getProfile(userid, callback);
	}

	public final void getProfile(String userid, MessageCallback callback) {
		Object[] _rq = { "api", "user.get_profile", "userid", userid };
		send(makeMessage(_rq), callback);
	}

	public final void changeLaptop(Laptop laptop) {
		changeLaptop(laptop, null);
	}

	public final void changeLaptop(Laptop laptop, MessageCallback callback) {
		Object[] _rq = { "api", "user.modify", "laptop", laptop.toString() };

		send(makeMessage(_rq), callback);
	}

	public final void changeName(String name) {
		changeName(name, null);
	}

	public final void changeName(String name, MessageCallback callback) {
		Object[] _rq = { "api", "user.modify", "name", name };
		send(makeMessage(_rq), callback);
	}

	public final void changeAvatar(int avatar) {
		changeAvatar(avatar, null);
	}

	public final void changeAvatar(int avatar, MessageCallback callback) {
		Object[] _rq = { "api", "user.set_avatar", "avatarid", avatar };
		send(makeMessage(_rq), callback);
	}

	public final void setStatus(Status status) {
		self.setStatus(status);
	}

	public final void playlistToTop(int idx) {
		playlistToTop(idx, null);
	}

	public final void playlistToTop(int idx, MessageCallback callback) {
		playlistReorder(idx, 0, callback);
	}

	public final void playlistToTop(String playlist, int idx, MessageCallback callback) {
		playlistReorder(playlist, idx, 0, callback);
	}

	public final void playlistToBottom(int idx) {
		playlistToBottom(idx, null);
	}

	public final void playlistToBottom(int idx, MessageCallback callback) {
		playlistToBottom("default", idx, callback);
	}

	public final void playlistToBottom(String playlist, int idx, MessageCallback callback) {
		playlistReorder(playlist, idx, this.playlist.size(), callback);
	}

	public final void unFan(String userid) {
		unFan(userid, null);
	}

	public final void unFan(String userid, MessageCallback callback) {
		Object[] _rq = { "api", "user.remove_fan", "djid", userid };
		send(makeMessage(_rq), callback);
	}

	public final void playlistReorder(int idxFrom, int idxTo) {
		playlistReorder(idxFrom, idxTo, null);
	}

	public final void playlistReorder(int idxFrom, int idxTo, MessageCallback callback) {
		playlistReorder("default", idxFrom, idxTo, callback);
	}

	public final void playlistReorder(String playlist, int idxFrom, int idxTo, MessageCallback callback) {
		Object[] _rq = { "api", "playlist.reorder", "playlist_name", playlist, "index_from", idxFrom, "index_to", idxTo };
		send(makeMessage(_rq), callback);
	}

	public final void listRooms(int skip, MessageCallback callback) {
		Object[] _rq = { "api", "room.list_rooms", "skip", skip };
		send(makeMessage(_rq), callback);
	}

	public final void roomNow() {
		roomNow(null);
	}

	public final void roomNow(MessageCallback callback) {
		send(makeMessage(new Object[] { "api", "room.now" }), callback);
	}

	public final void authUser() {
		authUser(new MessageCallback() {
			public void run(Message reply) {
				if (reply.getBoolean("success")) {
					joinRoom(roomid, new MessageCallback() {
						public final void run(Message reply) {
							if (reply.getBoolean("success")) {
								roomInfo();
							}
						}
					});
				}
			}
		});
	}

	public final void authUser(MessageCallback callback) {
		Object[] _rq = { "api", "user.authenticate", "roomid", roomid };
		send(makeMessage(_rq), callback);
	}

	public final void getPlaylist(MessageCallback callback) {
		getPlaylist("default", callback);
	}

	public final void getPlaylist(String name, MessageCallback callback) {
		Object[] _rq = { "api", "playlist.all", "playlist_name", name };
		send(makeMessage(_rq), callback);
	}

	public final Message makeMessage(Object... o) {
		Message reply = new Message();
		for (int i = 0; i < o.length; i += 2) {
			reply.put(o[i].toString(), o[i + 1]);
		}
		return reply;
	}

	public final Users getUsers() {
		return room.getUsers();
	}

	public final User[] getUserlist() {
		return getUsers().getList();
	}

	public final void doAction(String action) {
		speak("/me " + action);
	}

	public final void setWindow(BotWindow window) {
		this.window = window;
	}

	public final RepeatingTask getPresenceTask() {
		return presenceTask;
	}

	public final void close() {
		if (presenceTask != null && presenceTask.isRunning()) {
			presenceTask.quit();
		}
		ws.close();
		closed = true;
		if (hasWindow()) {
			window.close();
		}

	}

	public final boolean isClosed() {
		return closed;
	}

	// Logging
	public final void setDebug(boolean debug) {
		setDebug(debug, DEFAULT_DEBUG);
	}

	public final void setDebug(boolean debug, int level) {
		this.debug = true;
		this.debugLevel = level;
	}

	public final void info(Object data) {
		log(LogLevel.INFO, data.toString());
	}

	public final void error(String error) {
		log(LogLevel.ERROR, error);
	}

	public final void debug(Object data, int level) {
		if ((debugLevel & STRICT) != 0) {
			if (level == (debugLevel ^ STRICT))
				log(LogLevel.DEBUG, data.toString());
			return;
		}
		if (debug && (level <= debugLevel))
			log(LogLevel.DEBUG, data.toString());
	}

	private void log(LogLevel level, String data) {
		if (!hasWindow())
			System.out.println(String.format("%s %s: %s", Timestamp.now(), level, data));
		else
			window.log(level, data);
	}

	public boolean isLoaded() {
		return loaded;
	}
}