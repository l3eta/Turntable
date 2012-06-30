package org.l3eta.tt;

import java.util.HashMap;
import java.util.Map;

import org.l3eta.tt.Enums.Laptop;
import org.l3eta.tt.Enums.Status;
import org.l3eta.tt.user.Rank;
import org.l3eta.tt.util.Message;
import org.l3eta.tt.util.Timestamp;

public class User {
	private String name, id;
	private double points, fans, avatar, acl, created;
	private boolean isDj, isNull;
	private Rank rank;
	private Status status;
	private Laptop laptop;
	private Room room;
	private Timestamp activity;
	private Map<String, Object> attachments;

	public User() {
		isNull = true;
	}

	public User(Message json) {
		attachments = new HashMap<String, Object>();
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
		if(json.has("status")) {
			status = Status.get(json.getString("status"));
		} else {
			status = laptop.isPhone() ? Status.NO_PM : Status.AVAILABLE;
				
		}		
	}

	public Message toDBObject() {
		Message user = new Message();		
		user.put("name", name);
		user.put("laptop", laptop.name());
		user.put("userid", id);
		user.put("points", points);
		user.put("fans", fans);
		user.put("avatar", avatar);
		user.put("status", status.name());
		user.put("created", created);
		user.put("acl", acl);
		user.put("rank", rank);		
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
		activity = Timestamp.now();
	}
	
	public double getCreated() {
		return created;
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
		return isNull;
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
	
	public void setAttachment(String key, Object value) {
		attachments.put(key, value);
	}
	
	public Object getAttachment(String key) {
		if(attachments.containsKey(key)) {
			return attachments.get(key);
		}
		return null;
	}

	public boolean hasAttachment(String key) {
		return attachments.containsKey(key);
	}
}
