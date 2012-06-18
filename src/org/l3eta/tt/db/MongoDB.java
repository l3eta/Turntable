package org.l3eta.tt.db;


import java.util.HashMap;
import java.util.Map;

import org.l3eta.tt.User;
import org.l3eta.tt.user.Rank;


import com.mongodb.BasicDBObject;
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

	public MongoDB(String db, String ip) {
		this(db, ip, 27017);
	}

	public MongoDB(String name, String ip, int port) {
		super(name);
		collections = new HashMap<String, DBCollection>();
		try {
			mongo = new Mongo(ip, port);
			this.db = mongo.getDB(name);
			
			collections.put("commands", db.getCollection("commands"));
			collections.put("users", db.getCollection("users"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Rank getUserRank(User user) {

		return Rank.USER;
	}

	@Override
	public Rank getCommandRank(String command) {
		return Rank.USER;
	}

	@Override
	public void addCommand(String command, Rank rank) {
		
	}

	@Override
	public void saveUser(User user) {
		
	}

	@Override
	public void saveUsers(User[] users) {
		for(User user : users) {
			saveUser(user);
		}
	}

	@Override
	public boolean hasCommand(String command) {
		return false;
	}
	
	@Override
	public User getUser(String userid) {
		return null;
	}
}
