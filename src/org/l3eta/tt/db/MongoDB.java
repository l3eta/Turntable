package org.l3eta.tt.db;

import java.util.HashMap;
import java.util.Map;

import org.l3eta.tt.User;
import org.l3eta.tt.user.Rank;
import org.l3eta.tt.util.Message;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class MongoDB extends Database {
	private Mongo mongo;
	private DB db;
	private Map<String, DBCollection> collections;

	public MongoDB(String db) {
		this(db, "127.0.0.1");
	}

	public MongoDB(String db, String host) {
		this(db, host, 27017);
	}

	public MongoDB(String db, String host, int port) {
		this(db, host, "", "", port);
	}

	public MongoDB(String db, String host, String user, String pass) {
		this(db, host, user, pass, -1);
	}

	public MongoDB(String db, String host, String user, String pass, int port) {
		super(db);
		boolean useAuth = !(user.equals("") && pass.equals(""));
		collections = new HashMap<String, DBCollection>();
		try {
			mongo = new Mongo("mongodb://" + (useAuth ? user + ":" + pass + "@" : "") + host + (port == -1 ? "" : ":" + port) + "/" + db);
			this.db = mongo.getDB(db);
			collections.put("commands", this.db.getCollection("commands"));
			collections.put("users", this.db.getCollection("users"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Rank getUserRank(User user) {
		return Rank.USER;
	}

	@Override
	public void saveUser(User user) {

	}

	@Override
	public void saveUsers(User[] users) {
		for (User user : users) {
			saveUser(user);
		}
	}

	@Override
	public User getUser(String userid) {
		return null;
	}

	@Override
	public void putSettings(String col, Message map) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message getSettings(String col) {
		// TODO Auto-generated method stub
		return null;
	}
}
