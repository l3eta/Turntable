package org.l3eta.tt;

import org.l3eta.tt.Enums.Laptop;
import org.l3eta.tt.Enums.Status;
import org.l3eta.tt.user.Rank;
import org.l3eta.tt.util.Message;
import org.l3eta.tt.util.Timestamp;

import com.mongodb.BasicDBObject;

public class User {
	private String name, id;
	private double points, fans, avatar, acl;
	private boolean isDj;
	private long created;
	private Rank rank;
	private Status status;
	private Laptop laptop;
	private Room room;
	private Timestamp activity;

	public User() {
		this.laptop = Laptop.PC;
		this.points = -1D;
		this.fans = -1D;
		this.avatar = -1D;
		this.acl = 0D;
		this.rank = Rank.USER;
		this.activity = Timestamp.now();
	}

	public User(Message json) {
		this.name = json.getString("name");
		this.laptop = Laptop.get(json.getString("laptop"));
		this.id = json.getString("userid");
		this.points = json.getDouble("points");
		this.fans = json.getDouble("fans");
		this.avatar = getAvatar(json.get("avatarid"));
		this.acl = json.getDouble("acl");
		this.rank = Rank.USER;
		this.activity = Timestamp.now();
		this.isDj = false;
		if (laptop == Laptop.IPHONE || laptop == Laptop.ANDROID)
			this.status = Status.NO_PM;
		else
			this.status = Status.AVAILABLE;
	}

	public BasicDBObject toDBObject() {
		BasicDBObject user = new BasicDBObject();
		user.put("name", name);
		user.put("userid", id);
		user.put("points", points);
		user.put("laptop", laptop);
		user.put("fans", fans);
		user.put("rank", rank.toString());
		user.put("acl", acl);
		return user;
	}

	private double getAvatar(Object o) {
		return Double.parseDouble(String.valueOf(o).replace("\"", ""));
	}

	public void save() {
		if (room != null)
			room.saveUser(this);
	}

	public int getAccessLevel() {
		return (int) acl;
	}

	public void updateActivity() {
		this.activity = Timestamp.now();
	}

	public boolean isDj() {
		return this.isDj;
	}

	public void setDj(boolean isDj) {
		this.isDj = isDj;
	}

	public Timestamp getActivity() {
		return this.activity;
	}

	public boolean isNull() {
		return id == null;
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Laptop getLaptop() {
		return laptop;
	}

	public int getPoints() {
		return (int) points;
	}

	public int getFans() {
		return (int) fans;
	}

	public int getAvatar() {
		return (int) avatar;
	}

	public void addPoint() {
		points++;
	}

	public void addFan() {
		fans++;
	}

	public Rank getRank() {
		return rank;
	}

	public void addPoints(int i) {
		points += i;
	}

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public String toString() {
		return toDBObject().toString();
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
