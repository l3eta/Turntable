package org.l3eta.tt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l3eta.tt.task.DelayedTask;
import org.l3eta.tt.user.Rank;
import org.l3eta.tt.util.Message;

import com.mongodb.BasicDBList;

public final class Room {
	public Users users;
	private List<User> userList, djs;
	private Map<String, String> banList;
	private Bot bot;
	private Song currentSong;
	private boolean locked = false;
	private String id;
	private String name = "room.name", lockmessage = "lock.message";

	public Room(Bot bot) {
		this.bot = bot;
		users = new Users();
		userList = new ArrayList<User>();
		djs = new ArrayList<User>();
		banList = new HashMap<String, String>();
	}

	protected void setRoomID(String id) {
		this.id = id;
	}

	public void lockRoom(String message) {
		bot.debug("Locking room.", 0);
		lockmessage = message;
		locked = true;
		bot.debug("Locking complete. Remove users.", 0);
		removeUsers();
	}

	public void removeUsers() {
		new Thread() {
			public void run() {
				for (final User user : users.getList()) {
					// TODO make a shouldBoot(User);
					if (!Rank.ADMIN.compare(user.getRank()) && !user.equals(bot.getSelf()) && user.getAccessLevel() == 0) {
						new DelayedTask(new Runnable() {
							public void run() {
								bot.boot(user, lockmessage);
							}
						}, 500).start();
					}
				}
				bot.debug("Locking completed & users removed.", 0);
			}
		}.start();
	}

	public void unlockRoom() {
		this.locked = false;
	}

	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the DJs list.
	 * 
	 * @return User array of the dj's.
	 */
	public User[] getDjs() {
		return djs.toArray(new User[0]);
	}

	public void addDj(User user) {
		djs.add(user);
		user.setDj(true);
	}

	public void remDj(User user) {
		djs.remove(user);
		user.setDj(false);
	}

	/**
	 * @return Room Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the current song.
	 * 
	 * @return The room's current song.
	 */
	public Song getCurrentSong() {
		return currentSong;
	}

	public class Users {
		/**
		 * Gets all the users to an Array of user
		 * 
		 * @return Array of Users
		 */
		public User[] getList() {
			return userList.toArray(new User[0]);
		}

		/**
		 * Adds User to the room.
		 * 
		 * @param user
		 *            The User to add.
		 */
		public void addUser(final User user) {
			if (userList.contains(user))
				return;
			user.setRank(bot.getDatabase().getUserRank(user));
			if (locked) {
				if (!Rank.ADMIN.compare(user.getRank()) && user.getAccessLevel() == 0)
					new DelayedTask(new Runnable() {
						public void run() {
							bot.speak("@" + user.getName() + ", You will be booted in 30 seconds, The room is currently locked..");
							bot.boot(user, lockmessage);
						}
					}, 30000).start();
				return;
			}
			userList.add(user);
		}

		/**
		 * Removes User from the room.
		 * 
		 * @param user
		 *            User to remove.
		 */
		public void removeUser(User user) {
			if (userList.contains(user)) {
				userList.remove(getIndex(user));
			}
			// Tried to remove a user that was never added to list
		}

		/**
		 * Gets a user by their name.
		 * 
		 * @param name
		 *            Name of the user.
		 * @return The User
		 */
		public User getByName(String name) {
			User user = null;
			for (int i = 0; i < userList.size(); i++) {
				if (name.equals(userList.get(i).getName()))
					user = userList.get(i);
			}
			if (name.startsWith("@") && user == null) {
				user = getByName(name.substring(1));
			}
			return user.isNull() ? new User() : user;
		}

		/**
		 * Gets a user by there UserID
		 * 
		 * @param line
		 *            Line containing there UserID
		 * @return The User
		 */
		public User getByID(Message message) {
			try {
				return getByID(message.getString("userid"));
			} catch (Exception ex) {
			}
			return getByID(message.toString());
		}

		/**
		 * Gets a user by there UserID
		 * 
		 * @param userid
		 *            Their UserID
		 * @return The User
		 */
		public User getByID(String userid) {
			for (int i = 0; i < userList.size(); i++) {
				if (userid.equals(userList.get(i).getID()))
					return userList.get(i);
			}
			return new User();
		}

		public boolean inRoom(String userid) {
			return !getByID(userid).isNull();
		}

		/**
		 * Gets the index of the user in the UserList Array.
		 * 
		 * @param user
		 *            User to find.
		 * @return Index of the user.
		 */
		public int getIndex(User user) {
			return userList.indexOf(user);
		}

		// Ranking

		// Banning
		public String getBanReason(User user) {
			return banList.get(user.getID());
		}

		public boolean isBanned(User user) {
			return isBanned(user.getID());
		}

		public boolean isBanned(String userid) {
			return banList.containsKey(userid);
		}

		public void ban(String name, String reason) {
			ban(getByName(name), reason);
		}

		public void ban(User user, String reason) {
			if (user.isNull())
				return;
			banList.put(user.getID(), reason);
			bot.boot(user, reason);
		}

		/**
		 * Gets the total number of User's in the room.
		 * 
		 * @return Number of User's
		 */
		public int getCount() {
			return userList.size() - 1;
		}

		public void addUsers(BasicDBList list) {
			addUsers(list.toArray(new User[0]));
		}

		public void addUsers(User[] users) {
			for (User user : users) {
				addUser(user);
			}
		}
	}

	protected void setCurrentSong(Song song) {
		this.currentSong = song;
	}

	public Users getUsers() {
		return users;
	}

	public String getID() {
		return id;
	}

	public void saveUser(User user) {
		bot.getDatabase().saveUser(user);
	}
}
