package org.l3eta.turntable.tt;

import java.util.ArrayList;
import java.util.HashMap;

import org.l3eta.turntable.tt.User.Rank;
import org.l3eta.turntable.util.Line;
import org.l3eta.turntable.util.net.Sender;

public class Room {
	public Users Users = new Users();

	private ArrayList<User> userList = new ArrayList<User>();
	private HashMap<String, Rank> modList = new HashMap<String, Rank>();
	private HashMap<String, String> banList = new HashMap<String, String>();
	// private ArrayList<User> djs = new ArrayList<User>(); //TODO LATER
	private Song currentSong;
	private String name = "Default Room Name";
	private Object[] greeting = { "Welcome to %s, %s. Please enjoy your stay!",
			new Object[] { getName(), "Filler" } };

	public Room(String name) {
		this.name = name;
	}

	/**
	 * @return Room Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param userid
	 *            UserID of the User you want to make mod
	 * @param rank
	 *            The rank you want to make the User.
	 * 
	 * @see User.Rank for Rank Listing.
	 */
	public void addMod(String userid, Rank rank) {
		if (modList.containsKey(userid)) {
			System.out.println("[Room] This User is alread mod.");
			return;
		}
		modList.put(userid, rank);
	}

	public void updateModRank(String userid, Rank rank) {
		if (modList.containsKey(userid)) {
			if (modList.get(userid).equals(rank)) {
				System.out.println("[Room] This User is already this rank..");
				return;
			}
			modList.put(userid, rank);
		}
	}

	/**
	 * 
	 * @param user
	 *            The User to greet.
	 * @return Formatted String of User.getName() and Room.getName()
	 */
	public String getGreeting(User user) {
		return String.format(String.valueOf(greeting[0]), greeting[1]);
	}

	public void setGretting(String greeting, Object... objs) {
		this.greeting = new Object[] { greeting, new Object[] { objs } };
	}

	/**
	 * Gets the current song.
	 * 
	 * @return The room's current song.
	 */
	public Song getCurrentSong() {
		return currentSong;
	}

	/**
	 * Sets the current room song.
	 * 
	 * @param currentSong
	 *            The current song.
	 */
	public void setSong(Song currentSong) {
		this.currentSong = currentSong;
	}

	public class Users {
		/**
		 * Adds User to the room.
		 * 
		 * @param user
		 *            The User to add.
		 */
		public void addUser(User user) {
			if (userList.contains(user))
				return;
			user.setRank(getRank(user));
			user.save();
			userList.add(user);
		}

		/**
		 * Removes User from the room.
		 * 
		 * @param user
		 *            User to remove.
		 */
		public void removeUser(User user) {
			user.save();
			userList.remove(getIndex(user));
		}

		public User getByName(String name) {
			for (int i = 0; i < userList.size(); i++) {
				if (name.equals(userList.get(i).getName()))
					return userList.get(i);
			}
			return new User();
		}

		public User getByID(Line line) {
			return getByID(line.getString("userid"));
		}
		
		public User getByID(String userid) {
			for (int i = 0; i < userList.size(); i++) {
				if (userList.get(i).getUserID().equals(userid))
					return userList.get(i);
			}
			return new User();
		}

		public int getIndex(User user) {
			for (int i = 0; i < userList.size(); i++) {
				User user1 = userList.get(i);
				if (user1.getUserID().equals(user.getUserID()))
					return i;
			}
			return -1;
		}

		// Ranking
		public Rank getRank(User user) {
			return getRank(user.getUserID());
		}
		
		public Rank getRank(Line line) {
			return getRank(line.getString("userid"));
		}
		
		public Rank getRank(String userid) {
			if (modList.containsKey(userid)) {
				return modList.get(userid);
			}
			return Rank.User;
		}

		// Banning
		public String getBanReason(User user) {
			return banList.get(user.getUserID());
		}

		public boolean isBanned(User user) {
			return banList.containsKey(user.getUserID());
		}

		public void banUser(String name, String reason) {
			User user = getByName(name);
			if (user.isBlank())
				return;
			banList.put(user.getUserID(), reason);
			Sender.Mod.boot(user.getUserID(), reason);
		}

		public int getCount() {
			return userList.size() - 1;
		}
	}
}
