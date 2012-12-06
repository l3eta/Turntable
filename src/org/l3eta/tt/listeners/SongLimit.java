package org.l3eta.tt.listeners;

import java.util.HashMap;
import java.util.Map;

import org.l3eta.tt.User;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.event.DjEvent;
import org.l3eta.tt.event.EndSongEvent;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.event.RoomChangeEvent;
import org.l3eta.tt.event.UserJoinEvent;
import org.l3eta.tt.event.UserLeaveEvent;
import org.l3eta.tt.manager.CommandManager;

public class SongLimit extends EventListener {
	private int waitLimit = 1, songLimit = 4;
	private boolean limitRoom = true;
	private Map<String, SongData> users;
	
	public void loaded() {
		users = new HashMap<String, SongData>();
		CommandManager cm = bot.getCommandManager();
		cm.addCommand(new SongLimitCommands());
		cm.addCommand(new DJsCommand());
		cm.addCommand(new WaitCommand());
	}

	@EventMethod
	public void speak(ChatEvent event) {
		User user = event.getUser();
		if (user.getName().equals("l3eta")) {
			String text = event.getText();
			if (text.startsWith("set")) {
				String[] o = text.split(" ");
				SongData sd = (SongData) user.getAttachment("songLimit");
				sd.setSongs(bot.getInt(o[1]));
				sd.setWait(bot.getInt(o[2]));
			}
		}
	}

	@EventMethod
	public void changed(RoomChangeEvent event) {
		for (User user : bot.getUserlist()) {
			if (!user.hasAttachment("songLimit")) {
				if (users.containsKey(user.getID())) {
					user.setAttachment("songLimit", users.get(user.getID()));
				} else {
					user.setAttachment("songLimit", new SongData());
				}
			}
		}
	}

	@EventMethod
	public void join(UserJoinEvent event) {
		User user = event.getUser();
		if (users.containsKey(user.getID())) {
			user.setAttachment("songLimit", users.get(user.getID()));
		} else {
			user.setAttachment("songLimit", new SongData());
		}
	}

	@EventMethod
	public void dj(DjEvent event) {
		if (limitRoom) {
			User user = event.getUser();
			if (user.isDj()) {
				SongData sd = (SongData) user.getAttachment("songLimit");
				if (sd.getWait() > 0) {
					bot.remDj(user);
					bot.speak(format("@%s, You must wait %d songs!", user.getName(), sd.getWait()));
				}
			}
		}
	}

	public final String format(String text, Object... o) {
		return String.format(text, o);
	}

	@EventMethod
	public void endSong(EndSongEvent event) {
		if (limitRoom) {
			removeSongFromAll();
			User user = event.getUser();
			SongData sd = (SongData) user.getAttachment("songLimit");
			sd.addSong();
			if (sd.getSongs() >= songLimit) {
				bot.speak(format("%s, You have played %s songs! You have to wait %d songs.", user.getName(), songLimit, waitLimit));
				bot.remDj(user);
				sd.setSongs(0);
				sd.setWait(waitLimit);
			}
		}
	}

	public void removeSongFromAll() {
		for (User user : bot.getUserlist()) {
			if (user.hasAttachment("songLimit")) {
				SongData sd = (SongData) user.getAttachment("songLimit");
				if (sd.getWait() > 0) {
					sd.setWait(sd.getWait() - 1);
				}
			}
		}
	}

	public void clearAll() {
		for (User user : bot.getUserlist()) {
			if (user.hasAttachment("songLimit")) {
				SongData sd = (SongData) user.getAttachment("songLimit");
				sd.setWait(0);
				sd.setSongs(0);
			}
		}
	}

	@EventMethod
	public void leave(UserLeaveEvent event) {
		if (limitRoom) {
			User user = event.getUser();
			SongData sd = (SongData) user.getAttachment("songLimit");
			if (sd.getSongs() != 0 || sd.getWait() != 0) {
				users.put(user.getID(), sd);
			} else if (sd.getSongs() == 0 && sd.getWait() == 0) {
				if (users.containsKey(user.getID())) {
					users.remove(sd);
				}
			}
		}
	}

	private class SongData {
		private int songs, wait;

		public SongData() {
			songs = 0;
			wait = 0;
		}
		
		public void reset() {
			songs = 0;
			wait = 0;
		}

		public void addSong() {
			addSong(1);
		}

		public void addSong(int add) {
			songs += add;
		}

		public void setSongs(int songs) {
			this.songs = songs;
		}

		public void setWait(int wait) {
			this.wait = wait;
		}

		public int getSongs() {
			return songs;
		}

		public int getWait() {
			return wait;
		}
	}

	public class SongLimitCommands extends Command {
		public SongLimitCommands() {
			super("sl");
		}

		@Override
		public void execute(User user, String[] args, ChatType type) {
			String cmd = args[0];
			if (cmd.equals("-reset")) {
				// TODO
			} else if (cmd.equals("-resetall")) {
				clearAll();
			} else if (cmd.equals("-set")) {
				// TODO
			} else if (cmd.equals("-toggle")) {
				clearAll();
				limitRoom = !limitRoom;
			}
		}
	}

	public class DJsCommand extends Command {
		public DJsCommand() {
			super("djs");
		}

		@Override
		public void execute(User user, String[] args, ChatType type) {			
			if (limitRoom) {
				String djs = "";
				for (User u : bot.getUserlist()) {
					if (u.hasAttachment("songLimit") && u.isDj()) {
						SongData d = (SongData) u.getAttachment("songLimit");
						if (d.getSongs() > 0) {
							djs += " " + u.getName() + ": " + d.getSongs() + ", ";
						}
					}
				}

				if (djs.equals("")) {
					bot.speak("Could not make DJ list, No users with more then 0 songs played.");
				} else {
					djs = djs.trim();
					bot.speak("DJs: " + djs.substring(0, djs.length() - 2));
				}
			} else {
				bot.speak("Song limit is currently off-line");
			}
		}
	}

	public class WaitCommand extends Command {

		public WaitCommand() {
			super("wait");
		}

		@Override
		public void execute(User user, String[] args, ChatType type) {
			String wait = "";
			for (User u : bot.getUserlist()) {
				if (u.hasAttachment("songLimit")) {
					SongData d = (SongData) u.getAttachment("songLimit");
					if (d.getWait() > 0) {
						wait += " " + u.getName() + ": " + d.getWait() + ", ";
					}
				}
			}
			if (limitRoom) {
				if (wait.equals("")) {
					bot.speak("No one has to wait.");
				} else {
					wait = wait.trim();
					bot.speak("Wait List: " + wait.substring(0, wait.length() - 2));
				}
			} else {
				bot.speak("Song limit is currently off-line");
			}
		}
	}
}
