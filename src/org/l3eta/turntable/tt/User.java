package org.l3eta.turntable.tt;

import java.io.Serializable;
import java.util.Date;

import org.l3eta.turntable.util.Line;
import org.l3eta.turntable.util.io.FileManager;

@SuppressWarnings("serial")
public class User implements Serializable {
	protected String name, userid, laptop, lastseen;
	protected Integer points, fans, avatar;
	protected Rank rank = Rank.User;
	protected Stats stats;
	protected Date created;
	protected Long activity;
	// acl, created;

	public User() {
		this.name = "Blank";
		this.laptop = "pc";
		this.userid = "Blank";
		this.points = -10;
		this.fans = -10;
		this.avatar = -1;
		this.rank = Rank.User;
		this.stats = new Stats();
		this.activity = System.currentTimeMillis();
	}

	public User(String userid) {
		User user = FileManager.loadUser(userid);
		this.name = user.getName();
		this.avatar = user.getAvatar();
		this.rank = user.getRank();
		this.fans = user.getFans();
		this.points = user.getPoints();
		this.stats = user.getStats();
		this.userid = user.getUserID();
		this.activity = System.currentTimeMillis();
	}

	public User(Line line) {
		this.name = line.getString("name");
		this.laptop = line.getString("laptop");
		this.userid = line.getString("userid");
		this.points = line.getInt("points");
		this.fans = line.getInt("fans");
		this.avatar = line.getInt("avatarid");
		this.stats = new Stats();
		this.activity = System.currentTimeMillis();
	}
	
	public void updateActivity() {
		this.activity = System.currentTimeMillis();
	}

	public Long getActivity() {
		return this.activity;
	}
	
	public boolean isBlank() {
		return this.userid.equals("Blank") && this.name.equals("Blank");
	}
	
	public boolean setRank(Rank rank) {
		boolean promoted = false;
		if(rank.compareTo(this.getRank()) > 0) {
			promoted = false;
		} else if(rank.compareTo(this.getRank()) < 0){
			promoted = true;
		}
		this.rank = rank;
		return promoted;
	}

	public void save() {
		FileManager.saveUser(this);
	}

	public String getUserID() {
		return userid;
	}

	public String getName() {
		return name;
	}

	public String getLaptop() {
		return laptop;
	}

	public int getPoints() {
		return points;
	}

	public int getFans() {
		return fans;
	}

	public int getAvatar() {
		return avatar;
	}

	public void setAavatr(int avatar) {
		this.avatar = avatar;
	}

	public void setSeen(Date date) {
		this.lastseen = date.toString();
	}

	public void addPoint() {
		points++;
	}

	public void addFan() {
		fans++;
	}

	public Stats getStats() {
		return this.stats;
	}

	public Rank getRank() {
		return rank;
	}

	public String toString() {
		String stats = this.getStats().toString();
		return String
				.format("{User:{ name: %s, userid: %s, laptop: %s, points: %d, fans: %d, avatar: %d, rank: %s { %s }}}",
						name, userid, laptop, points, fans, avatar,
						rank.toString(), stats);
	}

	public class Stats implements Serializable {
		public int[] data = { 0, 0, 0, 0, 0, 0 };
		private int points = data[0], fans = data[1], plays = data[2];
		private int names = data[3], avatars = data[4], snags = data[5];
		private int[] votes = { 0, 0, 0 };

		public Stats() {
			data = new int[] { 0, 0, 0, 0, 0, 0 };
			votes = new int[] { 0, 0, 0 };
		}

		public Stats(Line line) {
			points = line.getInt("points");
			fans = line.getInt("fans");
			plays = line.getInt("plays");
			snags = line.getInt("snags");
			String[] v = line.getString("votes").split(" ");
			votes = new int[] { Integer.parseInt(v[0]), Integer.parseInt(v[1]),
					Integer.parseInt(v[2]) };
			avatars = line.getInt("avatarid");
		}

		public void addPoint() {
			this.points++;
		}

		public void addFan() {
			this.fans++;
		}

		public void addNameChange() {
			this.names++;
		}

		public void addAvatarChange() {
			this.avatars++;
		}

		public void addSnag() {
			this.snags++;
		}

		public void setVotes(int up, int down, int total) {
			this.votes = new int[] { up, down, total };
		}

		public void addPlay() {
			this.plays++;
		}

		public String toString() {
			return String
					.format("Stats:{ points: %d, fans: %d, plays: %d, snags: %d, names: %d, votes: \"%s\", avatarid: %d}",
							points, fans, plays, snags, names, votesToString(),
							avatars);
		}

		public String votesToString() {
			return String.format("%d %d %d", votes[0], votes[1], votes[2]);
		}

		public int getAvatarChanges() {
			return this.avatars;
		}

		public int getSnags() {
			return this.snags;
		}

		public int getNameChanges() {
			return this.names;
		}

		public int[] getVotes() {
			return votes;
		}

		public int getTotalAwesomes() {
			return this.votes[0];
		}

		public int getTotalLames() {
			return this.votes[1];
		}

		public int getTimesVoted() {
			return this.votes[2];
		}

		public int getPlays() {
			return this.plays;
		}

		public int getFans() {
			return this.fans;
		}

		public int getPoints() {
			return this.points;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Rank.Admin.compareTo(Rank.Mod));
	}

	public enum Rank {
		User, Producer, Friend, Mod, Admin, Owner;

		public boolean compare(Rank rank) {
			if (this == Owner)
				return this.compareTo(rank) <= 0;
			else if (this == Admin)
				return this.compareTo(rank) <= 0;
			else if (this == Mod)
				return this.compareTo(rank) <= 0;
			else if (this == Friend)
				return this.compareTo(rank) <= 0;
			else if (this == Producer)
				return this.compareTo(rank) <= 0;
			return false;
		}

		public static Rank parseLine(Line line) {
			if (line.equals("owner")) {
				return Owner;
			} else if (line.equals("admin")) {
				return Admin;
			} else if (line.equals("mod")) {
				return Mod;
			} else if (line.equals("friend")) {
				return Friend;
			} else if (line.equals("producer")) {
				return Producer;
			} else {
				return User;
			}
		}
		
		public static Rank parseObject(Object object) {
			return parseLine(new Line(String.valueOf(object)));
		}
	}

}
