package org.l3eta.tt.event;

import java.util.ArrayList;

import org.l3eta.tt.Song;
import org.l3eta.tt.User;
import org.l3eta.tt.util.Message;

public final class RoomChangeEvent extends Event {
	private Message json;
	private RoomData roomData;

	public RoomChangeEvent(Message json) {
		this.json = json;
		roomData = new RoomData();
	}

	public RoomData getRoomData() {
		return roomData;
	}

	public Message getMessage() {
		return json;
	}

	public class RoomData {
		private String roomid, name, name_lower, shortcut, privacy, currentDj;
		private String[] mods, djs;
		private double created;
		private boolean djFull;
		private int downvotes, pointLimit, maxDjs, upvotes, listeners, djCount,
				maxUsers;
		private ArrayList<User> users;
		private User creator;
		private Song song;

		private RoomData() {
			// TODO add in sticker placements
			if (json.getBoolean("success")) {
				users = new ArrayList<User>();
				for (Message user : json.getMessageList("users")) {
					users.add(new User(user));
				}
				Message temp = new Message(json.get("room"));
				name = temp.getString("name");
				name_lower = temp.getString("name_lower");
				shortcut = temp.getString("shortcut");
				roomid = temp.getString("roomid");
				created = temp.getDouble("created");
				temp = temp.getSubObject("metadata");
				creator = new User(temp.getSubObject("creator"));
				try {
					song = new Song(temp.getSubObject("current_song"), false);
				} catch(Exception ex) {
					song = new Song();
				}
				
				djFull = temp.getBoolean("dj_full");
				downvotes = temp.getInt("downvotes");
				privacy = temp.getString("privacy");
				upvotes = temp.getInt("upvotes");
				pointLimit = temp.getInt("djthreshold");
				mods = temp.getStringList("moderator_id");
				djs = temp.getStringList("djs");
				maxDjs = temp.getInt("max_djs");
				currentDj = temp.getString("current_dj");
				listeners = temp.getInt("listeners");
				djCount = temp.getInt("djcount");
				maxUsers = temp.getInt("max_size");
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

		public double getCreated() {
			// TODO change this to a timestamp;
			return created;
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
