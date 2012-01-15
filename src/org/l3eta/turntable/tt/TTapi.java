package org.l3eta.turntable.tt;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tootallnate.websocket.WebSocketClient;

import org.l3eta.turntable.util.Line;

@SuppressWarnings("unused")
public class TTapi {
	private final String register = "registered";
	private final String deregister = "deregistered";
	private final String addDj = "add_dj";
	private final String speak = "speak";
	private final String votes = "update_votes";
	private final String nosong = "nosong";
	private final String newsong = "newsong";
	private final String booted = "booted_user";
	private final String update = "update_user";
	private final String remDj = "rem_dj";
	private final String newMod = "new_moderator";
	private final String remMod = "rem_moderator";
	private final String snag = "snagged";
	private String currentSongID = null;
	private String currentDjID = null;
	private TTapi self = this;
	private String[] fanOf = {};
	private String auth = "";
	private String userid = "";
	private String roomid = "";
	private boolean debug = false;
	private String clientId = "";
	private Date lastActivity = null;
	private Date lastHeartbeat = null;
	private int _msgId = 0;
	private ArrayList<Object[]> _cmds = new ArrayList<Object[]>();
	private boolean _isConnected;
	public String[] chatServers = { "chat2.turntable.fm", "chat3.turntable.fm" };
	private WebSocketClient ws;
	private Bot bot;

	public TTapi(String auth, String userid, String roomid, Bot bot) {
		this.auth = auth;
		this.userid = userid;
		this.roomid = roomid;
		this.debug = false;
		this.lastHeartbeat = new Date();
		this.lastActivity = new Date();
		this.clientId = new Date().getTime() + "-0.59633534294921572";
		this._isConnected = false;
		this._msgId = 0;
		this.bot = bot;
		ws = getWebSocket(chatServers[0]);
		ws.connect();
	}

	public WebSocketClient getWebSocket(String room) {
		WebSocketClient ws = new WebSocketClient("ws://" + room
				+ ":80/socket.io/websocket") {
			public void onMessage(String message) {
				self.onMessage(new Line(message));
			}
		};
		this._msgId = 0;
		return ws;
	}

	public void _send(DataLine rq, Object callback) {
		rq.set("msgid", self._msgId);
		rq.set("clientid", self.clientId);
		rq.set("userid", self.userid);
		rq.set("userauth", self.auth);

		String msg = rq.toString();
		if (debug) {
			System.out.println("< " + rq.toString());
		}
		this.ws.send("~m~" + msg.length() + "~m~" + msg);
		try {
			this._cmds.add(new Object[] { self._msgId, rq.parse(), callback });
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		this._msgId++;
	}

	public void becomeFan(String userid) {
		Object[][] _rq = new Object[][] { { "api", "user.become_fan" },
				{ "djid", userid } };
		DataLine rq = new DataLine(_rq);
		this._send(rq, null);
	};

	public void onMessage(Line data) {
		String heart = "~m~[0-9]+~m~(~h~[0-9]+)";
		if (data.contains("wrong chat server")) {
			handleWrongChannel(data);
			return;
		}
		if (data.match(heart)) {
			self._heartbeat(data);
			self.lastHeartbeat = new Date();
			self.roomNow();
			return;
		}
		if (data.equals("~m~10~m~no_session")) {
			self.userAuthenticate();
			self.join();
			return;
		}
		this.lastActivity = new Date();
		if (debug)
			System.out.println("> " + new DataLine(data.toString()).parse());
		DataLine msg = new DataLine(data.toString());
		Line json = new Line(msg.parse());
		try {
			for (int i = 0; i < self._cmds.size(); i++) {
				int id = (Integer) self._cmds.get(i)[0];
				Line rq = new Line(new DataLine(self._cmds.get(i)[1]).parse());
				if (json.getInt("msgid") == 2) {
					Song song;
					if (json.contains("current_song: {")) {
						Line l = json.substring(
								json.indexOf("current_song: {"),
								json.indexOf("}, creator"));
						this.currentDjID = json.getString("djid");
						this.currentSongID = json.getString("_id");
					}
				} else if (id == json.getInt("msgid")) {
					switch (rq.getString("api")) {
					case "room.info":
						if (json.successful()) {
							// TODO fix.
						}
						break;
					case "room.register":
						if (json.successful()) {
							self.roomid = rq.getString("roomid");
							self.roomInfo(true);
						}
						break;
					}
				}
				self._cmds.remove(i);
			}

		} catch (Exception ex) {
			// ex.printStackTrace(); //Array issues.. Ignore it.
		}
		json = json.substring(json.indexOf("{"));
		if (isCommand(json)) {
			handleCommand(getCommand(json), json);
		} else {
			handleCommand("other",
					new Line(new DataLine(data.toString()).parse()));
		}

	}

	public void debug() {
		this.debug = !this.debug;
	}

	private String getCommand(Line line) {
		return line.getString("command");
	}

	private boolean isCommand(Line line) {
		try {
			return line.substring(1, line.indexOf(":")).equals("command");
		} catch (Exception ex) {
		}
		return false;
	}

	public void handleCommand(String command, Line json) {

		switch (command) {
		case speak:
			bot.onSpeak(json);
			break;
		case register:
			bot.onRegister(json);
			break;
		case deregister:
			bot.onDeregister(json);
			break;
		case votes:
			bot.onVotes(json);
			break;
		case addDj:
			bot.onAddDJ(json);
			break;
		case nosong:
			self.currentDjID = null;
			self.currentSongID = null;
			bot.onEndSong();
			bot.onNoSong(json);
			break;
		case newsong:
			if (self.currentSongID != null) {
				bot.onEndSong();
			}
			this.currentDjID = json.getString("djid");
			this.currentSongID = json.getString("_id");
			bot.onNewSong(json);
			break;
		case booted:
			bot.onBooted(json);
			break;
		case update:
			bot.onUpdate(json);
			break;
		case remDj:
			bot.onRemDJ(json);
			break;
		case newMod:
			bot.onNewMod(json);
			break;
		case remMod:
			bot.onRemMod(json);
			break;
		case snag:
			bot.onSnag(json);
			break;
		default:
			json = json.substring(json.indexOf("{"));
			bot.onOther(json);
			break;
		}
	}

	private void roomRegister(String roomid) {
		DataLine rq = new DataLine();
		rq.setAPI("room.register");
		if (roomid != null) {
			rq.set("roomid", roomid);
			this.ws.close();
			this.ws = getWebSocket(chatServers[0]);
		} else {
			rq.set("roomid", this.roomid);
		}
		this._send(rq, null);
	}

	public void roomInfo() {
		roomInfo(false);
	}

	public void roomInfo(boolean extend) {
		DataLine rq = new DataLine();
		rq.setAPI("room.info");
		rq.set("roomid", roomid);
		if (extend)
			rq.set("extended", extend);

		this._send(rq, null);
	}

	public void roomDeregister() {
		Object[][] _rq = { { "api", "room.deregister" }, { "roomid", roomid } };
		DataLine rq = new DataLine(_rq);
		this._send(rq, null);
	}

	public void getFavorites() {
		Object[][] _rq = { { "api", "room.get_favorites" } };
		DataLine rq = new DataLine(_rq);
		this._send(rq, null);
	}

	public void addFavorite(String roomid) {
		Object[][] _rq = { { "api", "room.add_favorite" }, { "roomid", roomid } };
		DataLine rq = new DataLine(_rq);
		this._send(rq, null);
	}

	public void speak(String text) {
		Object[][] _rq = { { "api", "room.speak" }, { "roomid", self.roomid },
				{ "text", text } };
		DataLine rq = new DataLine(_rq);
		self._send(rq, null);
	}

	public void remFavorite(String roomid) {
		Object[][] _rq = { { "api", "room.rem_favorite" }, { "roomid", roomid } };
		DataLine rq = new DataLine(_rq);
		self._send(rq, null);
	}

	public void addModerator(String userid) {
		Object[][] _rq = new Object[][] { { "api", "room.add_moderator" },
				{ "roomid", this.roomid }, { "target_userid", userid } };
		DataLine rq = new DataLine(_rq);
		this._send(rq, null);
	}

	public void remModerator(String userid) {
		Object[][] _rq = new Object[][] { { "api", "room.rem_moderato" },
				{ "roomid", roomid }, { "target_userid", userid } };
		DataLine rq = new DataLine(_rq);
		this._send(rq, null);
	}

	public void addDj() {
		System.out.println("Derp");
		Object[][] _rq = new Object[][] { { "api", "room.add_dj" },
				{ "roomid", roomid } };
		DataLine rq = new DataLine(_rq);
		System.out.println(rq.toString());
		this._send(rq, null);
	}

	public void remDj() {
		remDj(null);
	}

	public void remDj(String userid) {
		Object[][] _rq = { { "api", "room.rem_dj" }, { "roomid", roomid } };
		DataLine rq = new DataLine(_rq);

		if (userid != null) {
			rq.set("djid", userid);
		}
		this._send(rq, null);
	}

	public void skipSong() {
		Object[][] _rq = { { "api", "room.stop_song" }, { "roomid", roomid } };
		DataLine rq = new DataLine(_rq);
		this._send(rq, null);
	}

	public void bootUser(String userid, String reason) {
		DataLine rq = new DataLine();
		rq.setAPI("room.boot_user");
		rq.set("roomid", roomid);
		rq.set("target_userid", userid);
		rq.set("reason", reason);
		self._send(rq, null);
	}

	public void snag() {
		// TODO
	}

	public void vote(boolean up) {
		System.out.println("Derp: " + this.currentSongID);
		String vh = sha(this.roomid + (up ? "up" : "down") + this.currentSongID);
		String th = sha(String.valueOf(Math.random()));
		String ph = sha(String.valueOf(Math.random()));
		Object[][] _rq = { { "api", "room.vote" }, { "roomid", this.roomid },
				{ "val", (up ? "up" : "down") }, { "vh", vh }, { "th", th },
				{ "ph", ph } };
		DataLine rq = new DataLine(_rq);
		this._send(rq, null);
	}

	public void join() {
		this.roomRegister(null);
	}

	public void listRooms(Object skip) {
		DataLine rq = new DataLine();
		rq.setAPI("room.list_rooms");
		rq.set("skip", skip != null ? skip : 0);
		this._send(rq, null);
	}

	private void roomNow() {
		DataLine rq = new DataLine();
		rq.setAPI("room.now");
		self._send(rq, null);
	}

	private void handleWrongChannel(Line line) {
		ws.close();
		Line newServer = line.substring(line.indexOf("use ") + 4,
				line.indexOf(":80"));
		ws = getWebSocket(newServer.toString());
		ws.connect();
	}

	private void _heartbeat(Line line) {
		this.ws.send(line.toString());
		this._msgId++;
	}

	private void userAuthenticate() {
		Object[][] _rq = { { "api", "user.authenticate" },
				{ "roomid", self.roomid } };
		DataLine rq = new DataLine(_rq);
		self._isConnected = true;
		self._send(rq, null);
	}

	public String sha(String text) {
		StringBuilder key = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("sha1");
			md.update(text.getBytes());

			for (byte b : md.digest()) {
				key.append(Integer.toHexString((0xff & b) | 0xffffff00)
						.substring(6));
			}
		} catch (Exception ex) {

		}
		return key.toString();
	}

	public class DataLine {
		private StringBuilder sb;

		public DataLine() {
			this.sb = new StringBuilder();
		}

		public DataLine(Object[][] o) {
			this.sb = new StringBuilder();
			for (int i = 0; i < o.length; i++) {
				Object _o1 = o[i][0];
				Object _o2 = o[i][1];
				set(_o1, _o2);
			}
		}

		public DataLine(Object data) {
			this.sb = new StringBuilder();
			this.sb.append(String.valueOf(data));
		}

		public void setAPI(String api) {
			set("api", api);
		}

		private void set(Object o, Object v) {
			String _o = String.valueOf(o);
			String sV = "";
			if (_o.equals("msgid")) {
				sV = String.valueOf(v);
			} else {
				sV = "\"" + String.valueOf(v) + "\"";
			}
			String oV = "\"" + String.valueOf(o) + "\"";
			sb.append(oV + ": " + sV + ", ");
		}

		public String toString() {
			return "{" + sb.toString().substring(0, sb.toString().length() - 2)
					+ "}";
		}

		public String parse() {
			String data = sb.toString();
			Matcher m = Pattern.compile(
					"\"([a-z]+|(([a-z]+\\_[a-z]+)|\\_[a-z]+))\":")
					.matcher(data);
			while (m.find()) {
				String found = m.group();
				data = data.replace(found, found.replace("\"", ""));
			}
			return data;
		}
	}
}
