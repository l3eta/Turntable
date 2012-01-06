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
		/**
		 * Tells the bot you vote
		 * 
		 * @param up 	Weather to vote up or down.
		 */
		public static void vote(boolean up) {
			Other.doAction("vote", up ? "up" : "down");
		}

		private static void doAction(String command, String data) {
			addQueue(String.format("%s:%s", command, data));
		}
	}

	public static class Dj {
		/**
		 * Tells the bot you want him to be a DJ.
		 */
		public static void dj() {
			Dj.doAction("dj");
		}
		
		/**
		 * Tells the bot you want him to skip his song.
		 */
		public static void skipSong() {
			Dj.doAction("skip");
		}

		/** 
		 * Tells the bot you want him to remove himself from DJ.
		 */
		public static void remDJ() {
			Dj.doAction("rdj");
		}
		
		/**
		 * Tells the bot you want to boot someone from DJ.
		 * @param data	UserID to boot from DJing.
		 */
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
		/**
		 * Tells the bot you want to boot a user.
		 * @param userid	UserID of the user you want to boot.
		 * @param reason	Reason of why your booting the user.
		 */
		public static void boot(String userid, String reason) {
			Mod.doAction("boot", String.format("%s-%s", userid, reason));
		}

		/**
		 * Tells the bot you want to add a room mod.
		 * @param userid	UserID of User to make room mod.
		 */
		public static void addMod(String userid) {
			Mod.doAction("add", userid);
		}

		/**
		 * Tells the bot you want to remove a room mod.
		 * @param userid	UserID of User to make room mod.
		 */
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
