package org.l3eta.tt;

import java.util.HashMap;
import java.util.Map;

import org.l3eta.tt.Enums.Laptop;
import org.l3eta.tt.Enums.Status;
import org.l3eta.tt.user.Rank;
import org.l3eta.tt.util.Message;
import org.l3eta.util.Timestamp;

public class User {
	private String name, id;
	private double points, fans, avatar, acl, created;
	private boolean isDj, isRemote;
	private Rank rank;
	private Status status;
	private Laptop laptop;
	private Room room;
	private Timestamp activity;
	private Map<String, Object> attachments;

	public User() {		
	}
	
	public User(Message json) {
		this(false, json);
	}

	public User(boolean isRemote, Message json) {
		this.isRemote = isRemote;
		name = json.getString("name");
		laptop = Laptop.get(json.getString("laptop"));
		id = json.getString("userid");
		points = json.getDouble("points");
		fans = json.getDouble("fans");
		avatar = getAvatar(json.get("avatarid"));
		acl = json.getDouble("acl");
		created = json.getDouble("created");
		rank = Rank.USER;
		activity = Timestamp.now();
		isDj = false;
		if (json.has("status")) {
			status = Status.get(json.getString("status"));
		} else {
			status = laptop.isPhone() ? Status.NO_PM : Status.AVAILABLE;
		}
		if(!isRemote)
		attachments = new HashMap<String, Object>();
	}

	private double getAvatar(Object o) {
		return Double.parseDouble(String.valueOf(o).replace("\"", ""));
	}

	public String getName() {
		return name;
	}

	public String getID() {
		return id;
	}

	public double getPoints() {
		return points;
	}

	public double getFans() {
		return fans;
	}

	public double getAvatar() {
		return avatar;
	}

	public double getAcl() {
		return acl;
	}

	public double getCreated() {
		return created;
	}

	public boolean isDj() {
		return isDj;
	}
	
	public boolean isRemote() {
		return isRemote;
	}

	public Status getStatus() {
		return status;
	}

	public Laptop getLaptop() {
		return laptop;
	}

	public Room getRoom() {
		return room;
	}

	public Message toDBObject() {
		return null;
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

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setDj(boolean isDj) {
		this.isDj = isDj;
	}

	public void updateActivity() {
		activity = Timestamp.now();
	}

	public Timestamp getActivity() {
		return activity;
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

	public void save() {
		if (room != null)
			room.saveUser(this);
	}

	public void setAttachment(String key, Object value) {
		attachments.put(key, value);
	}

	public Object getAttachment(String key) {
		if (attachments.containsKey(key)) {
			return attachments.get(key);
		}
		return null;
	}

	public boolean hasAttachment(String key) {
		return attachments.containsKey(key);
	}
}