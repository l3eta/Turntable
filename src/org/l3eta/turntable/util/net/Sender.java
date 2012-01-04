package org.l3eta.turntable.util.net;

import java.util.ArrayList;
import java.util.Iterator;

public class Sender {
	private static ArrayList<String> Queue = new ArrayList<String>();
	private static Client client;

	public Sender(Client client) {
		Sender.client = client;
	}
	
	public static void debug() {
		addQueue("playlist:debug:");
	}

	public static class Talk {
		public static void sendSong(Object data) {
			Talk.doAction("say:song", data);
		}

		public static void speak(String data) {
			Talk.doAction("say", data);
		}

		public static void action(String data) {
			Talk.speak("/me " + data);
		}

		private static void doAction(String command, Object data) {
			addQueue(String.format("%s:%s", command, String.valueOf(data)));
		}
	}

	public static class Other {
		public static void vote(boolean up) {
			Other.doAction("vote", up ? "up" : "down");
		}

		private static void doAction(String command, String data) {
			addQueue(String.format("%s:%s", command, data));
		}

		public static void ping() {
			Other.doAction("ping", "");			
		}
	}

	public static class Dj {
		public static void dj() {
			Dj.doAction("dj");
		}

		public static void skipSong() {
			Dj.doAction("skip");
		}

		public static void remDJ() {
			Dj.doAction("rdj");
		}

		public static void removeDJ(String data) {
			Dj.doAction("rdj", data);
		}

		private static void doAction(String command) {
			Dj.doAction(command, "");
		}

		private static void doAction(String command, String data) {
			addQueue(String.format("dj:%s:%s", command, data));
		}
	}

	public static class Mod {
		public static void boot(String userid, String reason) {
			Mod.doAction("boot", String.format("%s-%s", userid, reason));
		}

		public static void addMod(String userid) {
			Mod.doAction("add", userid);
		}

		public static void remMod(String userid) {
			Mod.doAction("rem", userid);
		}

		private static void doAction(String command, String data) {
			addQueue(String.format("mod:%s:%s", command, data));
		}
	}

	public static class Playlist {
		public static void addSong(String data) {
			Playlist.sendAction("addsong", data);
		}

		public static void removeSong(String data) {
			Playlist.sendAction("remove", data);
		}

		public static void test(String data) {
			Playlist.sendAction("s", "");
		}

		private static void sendAction(String command, String data) {
			addQueue(String.format("playlist:%s:%s", command, data));
		}
	}

	private static void addQueue(String data) {
		Queue.add(data);
		sendData();
	}

	private static void sendData() {
		Iterator<String> i = Queue.iterator();
		while (i.hasNext()) {
			String out = i.next();
			client.write(out);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Queue.clear();
	}

	public Client getClient() {
		return client;
	}

	public static void start(String[] info) {
		Sender.addQueue(String.format("start:%s:%s:%s", info[0], info[1], info[2]));
	}
}
