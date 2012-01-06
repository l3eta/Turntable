package org.l3eta.turntable.tt;

import java.util.ArrayList;
import java.util.HashMap;

import org.l3eta.turntable.tt.User.Rank;
import org.l3eta.turntable.util.net.Sender;

public class Room {
	private ArrayList<User> userlist = new ArrayList<User>();
	private HashMap<String, Rank> modlist = new HashMap<String, Rank>();
	private HashMap<String, String> banlist = new HashMap<String, String>();
	//private ArrayList<User> djs = new ArrayList<User>(); //TODO LATER
	
	private Song currentSong;
	public Users Users = new Users();
	private String name = "Default Room Name";
	
	public Room(String name) {
		this.name = name;
	}
	
	public void addMod(String userid, Rank rank) {
		if(modlist.containsKey(userid)) {
			System.out.println("[Room] This userid is already in the mod list.");
			return;
		}
		modlist.put(userid, rank);
	}
	
	public void addMods(Object[][] list) {
		for(Object[] o : list) {
			this.addMod(String.valueOf(o[0]), Rank.parseObject(o[1]));
		}
	}

	public String getGreeting(User user) {
		return String
				.format("Welcome to %s, %s. Please enjoy your stay!",
						this.name, user.getName());
	}

	public Song getCurrentSong() {
		return currentSong;
	}

	public void setSong(Song currentSong) {
		this.currentSong = currentSong;
	}

	public class Users {

		public void addUser(User user) {
			if (userlist.contains(user))
				return;
			user.save();
			userlist.add(user);
		}

		public void removeUser(int userIndex) {
			userlist.get(userIndex).save();
			userlist.remove(userIndex);
		}

		public User getByName(String name) {
			for (int i = 0; i < userlist.size(); i++) {
				if (name.equals(userlist.get(i).getName()))
					return userlist.get(i);
			}
			return new User();
		}

		public User getByID(String userid) {
			for (int i = 0; i < userlist.size(); i++) {
				if (userlist.get(i).getUserID().equals(userid))
					return userlist.get(i);
			}
			return new User();
		}

		public int getIndex(User user) {
			for (int i = 0; i < userlist.size(); i++) {
				User user1 = userlist.get(i);
				if (user1.getUserID().equals(user.getUserID()))
					return i;
			}
			return -1;
		}

		// Ranking
		public Rank getRankFromID(String userid) {
			if (modlist.get(userid) == null)
				return Rank.User;
			return modlist.get(userid);
		}

		// Banning
		public String getBanReason(User user) {
			return banlist.get(user.getUserID());
		}

		public boolean isBanned(User user) {
			return banlist.containsKey(user.getUserID());
		}

		public void banUser(String name, String reason) {
			User user = getByName(name);
			if (user.isBlank())
				return;
			banlist.put(user.getUserID(), reason);
			Sender.Mod.boot(user.getUserID(), reason);
		}
	}
}
