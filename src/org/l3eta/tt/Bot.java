package org.l3eta.tt;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.l3eta.tt.Enums.Laptop;
import org.l3eta.tt.Enums.LogLevel;
import org.l3eta.tt.Enums.Status;
import org.l3eta.tt.Enums.TurntableCommand;
import org.l3eta.tt.Enums.Vote;
import org.l3eta.tt.Song;
import org.l3eta.tt.Room.Users;
import org.l3eta.tt.manager.BotWindow;
import org.l3eta.tt.manager.EventManager;
import org.l3eta.tt.task.RepeatingTask;
import org.l3eta.tt.user.Profile;
import org.l3eta.tt.db.Database;
import org.l3eta.tt.db.DefaultDatabase;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.event.DjEvent;
import org.l3eta.tt.event.EndSongEvent;
import org.l3eta.tt.event.ModEvent;
import org.l3eta.tt.event.NoSongEvent;
import org.l3eta.tt.event.RoomChangeEvent;
import org.l3eta.tt.event.RoomChangeEvent.RoomData;
import org.l3eta.tt.event.RoomUpdateEvent;
import org.l3eta.tt.event.SnagEvent;
import org.l3eta.tt.event.SongEvent;
import org.l3eta.tt.event.UserBootEvent;
import org.l3eta.tt.event.UserJoinEvent;
import org.l3eta.tt.event.UserLeaveEvent;
import org.l3eta.tt.event.UserUpdateEvent;
import org.l3eta.tt.event.VoteEvent;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.util.BotMessage;
import org.l3eta.tt.util.BotMessage.MessageCallback;
import org.l3eta.tt.util.External;
import org.l3eta.tt.util.External.ExternalAPICallback;
import org.l3eta.tt.util.Message;
import org.l3eta.tt.util.Timestamp;
import org.l3eta.tt.ws.WebSocket;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class Bot {
	public static final String version = "3.0.0";

	private String auth, roomid, clientid, userid;
	private int msgid, debugLevel;
	private boolean allowExternalPM, debug;

	private Database database;
	private Room room = null;
	private List<BotMessage> requests;
	private List<Song> playlist;
	private List<String> fanOf;
	private User self;
	private WebSocket ws;
	private EventManager eventManager;
	private BotWindow window;
	private Timestamp lastActivity, lastHeartbeat, startTime;

	public Bot() {
		if (room == null) {
			room = new Room(this);
			Song.setRoom(room);
		}
		playlist = new ArrayList<Song>();
		fanOf = new ArrayList<String>();
		requests = new ArrayList<BotMessage>();
		eventManager = new EventManager();
		msgid = 0;
		database = new DefaultDatabase("default");
	}

	public Bot(String auth, String userid, String roomid) {
		this();
		this.auth = auth;
		this.userid = userid;
		this.roomid = roomid;
		room.setRoomID(roomid);
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
				debug("> " + message, 1);
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

	public final void send(Message rq, MessageCallback callback) {
		rq.append("msgid", msgid);
		rq.append("clientid", clientid);
		if (!rq.containsField("userid"))
			rq.append("userid", userid);
		rq.append("userauth", auth);
		debug("< " + rq, 1);
		ws.send("~m~" + rq.length() + "~m~" + rq);
		requests.add(new BotMessage(msgid, rq, callback));
		msgid++;
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
					debug(String.format("%s, has %s a fan!", user.getName(), fan), 0);
				} else if (field.equals("name")) {
					String name = uue.getValue().toString();
					debug(user.getName() + " changed their name to '" + name + "'", 0);
					user.setName(name);
				} else if (field.equals("avatar")) {
					int id = getInt(uue.getValue());
					user.setAvatar(id);
					debug(user.getName() + " changed their avatar to '" + id + "'.", 0);
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
				if (allowExternalPM && !getUsers().inRoom(userid)) {
					ExternalAPICallback callback = new ExternalAPICallback() {
						public final void run(Message message, boolean success) {
							if (success)
								eventManager.sendEvent(new ChatEvent(new User(message), stemp, ChatType.PM));
						}
					};
					External.getUserInfo(callback, userid);
				} else if (getUsers().inRoom(userid)) {
					user = getUsers().getByID(userid);
					if (user.getStatus() == Status.NO_PM) {
						info(user.getName() + " is using an OS that does not support Private Messaging");
					} else
						eventManager.sendEvent(new ChatEvent(user, stemp, ChatType.PM));
				}
				break;
			case ROOM_CHANGED:
				RoomChangeEvent rce = new RoomChangeEvent(msg);
				RoomData data = rce.getRoomData();
				getUsers().addUsers(data.getUsers());
				if (data.getDjCount() > 0) {
					for (String d : data.getDjs()) {
						room.addDj(getUsers().getByID(d));
					}
				}
				room.setCurrentSong(data.getSong());
				eventManager.sendEvent(rce);
				break;
			case OTHER:
				debug(msg, 1);
				break;
		}
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

	private int getInt(Object value) {
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

	private final String randHash(String al) {
		return hash(Math.random(), al);
	}

	private boolean hasWindow() {
		return window != null;
	}

	private void sendBeat(String line) {
		ws.send("~m~" + line.length() + "~m~" + line);
		debug("HeartBeat: " + ("~m~" + line.length() + "~m~" + line), 0);
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

	public final void modifyRoom(String descript) {
		modifyRoom(descript, null);
	}

	public final void modifyRoom(String descript, MessageCallback callback) {
		Object[] _rq = { "api", "room.modify", "roomid", roomid, "description", descript };
		send(makeMessage(_rq), callback);
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
				new RepeatingTask(new Runnable() {
					public final void run() {
						updatePresence();
					}
				}, 20000).start();
				getPlaylist(new MessageCallback() {
					public final void run(Message reply) {
						debug("Loading Playlist", 0);
						for (Message song : reply.getMessageList("list")) {
							playlist.add(new Song(song, true));
						}
						debug("Loaded " + playlist.size() + " songs.", 0);
						getFanList(new MessageCallback() {
							public final void run(Message reply) {
								debug("Loading fan list.", 0);
								for (String userid : reply.getStringList("fanof")) {
									if (!fanOf.contains(userid))
										fanOf.add(userid);
								}
								debug("Loaded " + fanOf.size() + " fans.", 0);
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

	public final void getBuddies() {
		getBuddies(null);
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

	public final void getGraph(MessageCallback callback) {
		// TODO
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

	public final void snag() {
		snag(false);
	}

	public final void snag(boolean addSong) {
		snag(addSong, null);
	}

	public final void snag(MessageCallback callback) {
		snag(false, callback);
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

	public final void addSong(String songid) {
		addSong(songid, null);
	}

	public final void addSong(String songid, MessageCallback callback) {
		Object[] _rq = { "api", "playlist.add", "playlist_name", "default", "song_dict", new BasicDBObject().append("fileid", songid),
				"index", playlist.size() };
		send(makeMessage(_rq), callback);
	}

	public final void removeSong() {
		removeSong(null);
	}

	public final void removeSong(MessageCallback callback) {
		removeSong(0, callback);
	}

	public final void removeSong(int index, MessageCallback callback) {
		Object[] _rq = { "api", "playlist.remove", "playlist_name", "default", "index", index };
		send(makeMessage(_rq), callback);
	}

	public final void userInfo() {
		userInfo(null);
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

	public final void getFanList() {
		getFanList(null);
	}

	public final void getFanList(MessageCallback callback) {
		Object[] _rq = { "api", "user.get_fan_of" };
		send(makeMessage(_rq), callback);
	}

	public final void getProfile() {
		getProfile(null);
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

	public final void playlistToTop(int index) {
		playlistReorder(index, 0);
	}

	public final void unFan(String userid) {
		unFan(userid, null);
	}

	public final void unFan(String userid, MessageCallback callback) {
		Object[] _rq = { "api", "user.remove_fan", "djid", userid };
		send(makeMessage(_rq), callback);
	}

	public final void playlistReorder(int iF, int iT) {
		playlistReorder(iF, iT, null);
	}

	public final void playlistReorder(int iF, int iT, MessageCallback callback) {
		Object[] _rq = { "api", "playlist.reorder", "playlist_name", "default", "index_from", iF, "index_to", iT };
		send(makeMessage(_rq), callback);
	}

	public final void listRooms(int skip) {
		listRooms(skip, null);
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
		authUser(null);
	}

	public final void authUser(MessageCallback callback) {
		Object[] _rq = { "api", "user.authenticate", "roomid", roomid };
		send(makeMessage(_rq), callback);
		joinRoom(roomid, new MessageCallback() {
			public final void run(Message reply) {
				if (reply.getBoolean("success")) {
					roomInfo();
				}
			}
		});
	}

	public final void getPlaylist() {
		getPlaylist("default", null);
	}

	public final void getPlaylist(MessageCallback callback) {
		getPlaylist("default", callback);
	}

	public final void getPlaylist(String name, MessageCallback callback) {
		Object[] _rq = { "api", "playlist.all", "playlist_name", name };
		send(makeMessage(_rq), callback);
	}

	private String hash(Object o, String al) {
		StringBuilder key = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance(al);
			md.update(String.valueOf(o).getBytes());
			for (byte b : md.digest()) {
				key.append(Integer.toHexString((0xff & b) | 0xffffff00).substring(6));
			}
		} catch (Exception ex) {
		}
		return key.toString();
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

	public final void close() {
		ws.close();
	}

	// Logging
	public final void setDebug(boolean debug) {
		setDebug(debug, 0);
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
		if (debug && level >= debugLevel)
			log(LogLevel.DEBUG, data.toString());
	}

	private void log(LogLevel level, String data) {
		if (!hasWindow())
			System.out.println(String.format("%s %s: %s", Timestamp.now(), level, data));
		else
			window.log(level, data);
	}

}