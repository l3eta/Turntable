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

	public void setData(RoomData data) {
		users.addUsers(data.getUsers());
		if (data.getDjCount() > 0) {
			for (String d : data.getDjs()) {
				addDj(getUsers().getByID(d));
			}
		}
		setCurrentSong(data.getSong());
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

		public void ban(User user, String reason) {
			if (user.isNull())
				return;

		}

		public void ban(String userid, String reason) {
			banList.put(userid, reason);
			bot.boot(userid, reason);
		}

		public void addUsers(BasicDBList list) {
			addUsers(list.toArray(new User[0]));
		}

		public void addUsers(User[] users) {
			for (User user : users) {
				addUser(user);
			}
		}

		public void unban(User user) {
			if (user.isNull())
				return;
			unban(user.getID());
		}

		public void unban(String id) {
			banList.remove(id);
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

	// TODO RoomData
	public static class RoomData {
		private String roomid, name, name_lower, shortcut, privacy, currentDj;
		private String[] mods, djs;
		private double created;
		private boolean djFull;
		private int downvotes, pointLimit, maxDjs, upvotes, listeners, djCount, maxUsers;
		private ArrayList<User> users;
		private User creator;
		private Song song;
		private String chatServer;
		private int chatPort;

		public RoomData(Message json) {
			this(json, false);
		}

		public RoomData(Message json, boolean isDGR) {
			// TODO add in sticker placements
			users = new ArrayList<User>();
			if (!isDGR) {
				if (json.getBoolean("success")) {
					Message to;
					for (Message user : json.getMessageList("users")) {
						users.add(new User(user));
					}
					to = new Message(json.get("room"));
					name = to.getString("name");
					name_lower = to.getString("name_lower");
					shortcut = to.getString("shortcut");
					roomid = to.getString("roomid");
					created = to.getDouble("created");
					to = to.getSubObject("metadata");
					creator = new User(to.getSubObject("creator"));
					try {
						song = new Song(to.getSubObject("current_song"), false);
					} catch (Exception ex) {
						song = null;
					}
					djFull = to.getBoolean("dj_full");
					downvotes = to.getInt("downvotes");
					privacy = to.getString("privacy");
					upvotes = to.getInt("upvotes");
					pointLimit = to.getInt("djthreshold");
					mods = to.getStringList("moderator_id");
					djs = to.getStringList("djs");
					maxDjs = to.getInt("max_djs");
					currentDj = to.getString("current_dj");
					listeners = to.getInt("listeners");
					djCount = to.getInt("djcount");
					maxUsers = to.getInt("max_size");
					// votelog = temp.get("votelog");
					// stickerPlacements = temp.get("sticker_placements");
					// songlog = temp.get("songlog");

				}
			} else {
				Message to;
				BasicDBList tl;
				for (Message user : json.getMessageList("users")) {
					users.add(new User(user));
				}
				to = new Message(json.get("room"));
				tl = to.getList("chatserver");
				chatServer = tl.get(0).toString();
				chatPort = Integer.parseInt(tl.get(1).toString());

				name = to.getString("name");
				name_lower = to.getString("name_lower");
				shortcut = to.getString("shortcut");
				roomid = to.getString("roomid");
				created = to.getDouble("created");
				to = to.getSubObject("metadata");
				creator = new User(to.getSubObject("creator"));
				try {
					song = new Song(to.getSubObject("current_song"), false);
				} catch (Exception ex) {
					song = null;
				}
				djFull = to.getBoolean("dj_full");
				downvotes = to.getInt("downvotes");
				privacy = to.getString("privacy");
				upvotes = to.getInt("upvotes");
				pointLimit = to.getInt("djthreshold");
				mods = to.getStringList("moderator_id");
				djs = to.getStringList("djs");
				maxDjs = to.getInt("max_djs");
				currentDj = to.getString("current_dj");
				listeners = to.getInt("listeners");
				djCount = to.getInt("djcount");
				maxUsers = to.getInt("max_size");
				// votelog = temp.get("votelog");
				// stickerPlacements = temp.get("sticker_placements");
				// songlog = temp.get("songlog");

			}

		}

		public String[] getMods() {
			return mods;
		}

		public String[] getDjs() {
			return djs;
		}

		public String getName() {
			return name;
		}

		public String getNameLower() {
			return name_lower;
		}

		public String getID() {
			return roomid;
		}

		public String getShortcut() {
			return shortcut;
		}

		public String getPrivacy() {
			return privacy;
		}

		public String getCurrentDj() {
			return currentDj;
		}

		public String getChatServer() {
			return chatServer;
		}

		public double getCreated() {
			// TODO change this to a timestamp;
			return created;
		}

		public int getChatPort() {
			return chatPort;
		}

		public int getUpVotes() {
			return upvotes;
		}

		public int getMaxUsers() {
			return maxUsers;
		}

		public int getDownVotes() {
			return downvotes;
		}

		public int getPointLimit() {
			return pointLimit;
		}

		public int getDjCount() {
			return djCount;
		}

		public int getListeners() {
			return listeners;
		}

		public int getMaxDjs() {
			return maxDjs;
		}

		public boolean isDeckFull() {
			return djFull;
		}

		public User getCreator() {
			return creator;
		}

		public User[] getUsers() {
			return users.toArray(new User[0]);
		}

		public Song getSong() {
			return song;
		}

	}
}