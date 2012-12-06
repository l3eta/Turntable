package org.l3eta.tt.db;

import java.util.HashMap;
import java.util.Map;

import org.l3eta.tt.User;
import org.l3eta.tt.user.Rank;
import org.l3eta.tt.util.Message;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDB implements Database {
	private String database;
	private Mongo mongo;
	private DB db;
	private Map<String, DBCollection> collections;

	public MongoDB(String database, MongoData data) {
		collections = new HashMap<String, DBCollection>();
		try {
			mongo = new Mongo(data.toString());
			db = mongo.getDB(data.getDatabase());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Message[] getBannedUsers() {
		return null; //Something like getSetting("bannedUsers").getMessageList(
	}

	@Override
	public void saveUser(User user) {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveUsers(User[] users) {
		// TODO Auto-generated method stub

	}

	@Override
	public User loadUser(String userid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putSetting(String setting, Message map) {

	}

	@Override
	public Message getSetting(String setting) {
		if (collections.containsKey("settings")) {
			DBCollection col = collections.get("settings");
		}
		return null;
	}

	private DBCursor find(DBCollection col, Object... o) {
		DBObject ref = new BasicDBObject();
		for (int i = 0; i < o.length; i += 2) {
			ref.put(o[i].toString(), o[i + 1]);
		}
		return col.find(ref);
	}

	@Override
	public boolean hasSetting(String setting) {
		return find(collections.get("settings"), "setting", setting).size() == 1;
	}

	@Override
	public String getName() {
		return database;
	}

	public static class MongoData {
		private Map<String, Object> map;

		public MongoData(String database) {
			map = new HashMap<String, Object>();
			map.put("username", null);
			map.put("password", null);
			map.put("host", "127.0.0.1");
			map.put("port", 27017);
			map.put("database", database);
		}

		public String getDatabase() {
			return map.get("database").toString();
		}

		public void setAuth(String username, String password) {
			map.put("username", username);
			map.put("password", password);
		}

		public void setDatabase(String database) {
			map.put("database", database);
		}

		public void setHost(String host) {
			map.put("host", host);
		}

		public void setPort(int port) {
			map.put("port", port);
		}

		public String toString() {
			StringBuilder url = new StringBuilder();
			url.append("mongodb://");
			boolean auth = !(isNull(map.get("username")) && isNull(map.get("password")));
			if (auth) {
				url.append(map.get("username") + ":" + map.get("password") + "@");
			}
			url.append(map.get("host") + ":" + map.get("port"));
			if (!isNull(map.get("database"))) {
				url.append("/" + map.get("database"));
			}
			return url.toString();
		}

		private boolean isNull(Object o) {
			return o == null;
		}
	}

	@Override
	public Rank getUserRank(String userid) {
		// TODO Auto-generated method stub
		return null;
	}
}
