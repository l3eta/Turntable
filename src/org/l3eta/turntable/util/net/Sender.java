package org.l3eta.turntable.util.net;

import java.util.ArrayList;
import java.util.Iterator;

public class Sender {
	private static ArrayList<String> Queue = new ArrayList<String>();
	private static Client client;

	public Sender(Client client, String room) {
		Sender.client = client;
		Sender.addQueue("starter:" + room);
	}
	
	public static void debug() {
		addQueue("playlist:debug:");
	}

	public void setClient(Client client) {
		Sender.client = client;
	}

	public static Talk Talk = new Talk();
	public static Other Other = new Other();
	public static Mod Mod = new Mod();
	public static Playlist Playlist = new Playlist();
	public static Dj Dj = new Dj();

	public static class Talk {
		public void sendSong(Object data) {
			this.doAction("say:song", data);
		}

		public void speak(String data) {
			this.doAction("say", data);
		}

		public void action(String data) {
			this.speak("/me " + data);
		}

		private void doAction(String command, Object data) {
			addQueue(String.format("%s:%s", command, String.valueOf(data)));
		}
	}

	public static class Other {
		public void vote(boolean up) {
			this.doAction("vote", up ? "up" : "down");
		}

		private void doAction(String command, String data) {
			addQueue(String.format("%s:%s", command, data));
		}

		public void ping() {
			this.doAction("ping", "");			
		}
	}

	public static class Dj {
		public void dj() {
			this.doAction("dj");
		}

		public void skipSong() {
			this.doAction("skip");
		}

		public void remDJ() {
			this.doAction("rdj");
		}

		public void removeDJ(String data) {
			this.doAction("rdj", data);
		}

		private void doAction(String command) {
			this.doAction(command, "");
		}

		private void doAction(String command, String data) {
			addQueue(String.format("dj:%s:%s", command, data));
		}
	}

	public static class Mod {
		public void boot(String userid, String reason) {
			this.doAction("boot", String.format("%s-%s", userid, reason));
		}

		public void addMod(String userid) {
			this.doAction("add", userid);
		}

		public void remMod(String userid) {
			this.doAction("rem", userid);
		}

		private void doAction(String command, String data) {
			addQueue(String.format("mod:%s:%s", command, data));
		}
	}

	public static class Playlist {
		public void addSong(String data) {
			this.sendAction("addsong", data);
		}

		public void removeSong(String data) {
			sendAction("remove", data);
		}

		public void test(String data) {
			this.sendAction("s", "");
		}

		private void sendAction(String command, String data) {
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
}
