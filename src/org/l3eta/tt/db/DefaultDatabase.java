package org.l3eta.tt.db;

import java.io.File;

import org.l3eta.tt.User;
import org.l3eta.tt.user.Rank;
import org.l3eta.tt.util.Message;
import org.l3eta.util.FileUtil;

public class DefaultDatabase extends Database {
	private File dbRoot = new File(System.getProperty("user.home") + "/databases");
	private File dbUsers;
	private File dbSettings;

	public DefaultDatabase(String dbName) {
		super(dbName);
		dbRoot = new File(dbRoot, dbName);
		if (!dbRoot.exists())
			dbRoot.mkdir();
		dbUsers = new File(dbRoot, "users");
		if (!dbUsers.exists())
			dbUsers.mkdir();
		dbSettings = new File(dbRoot, "settings");
		if (!dbSettings.exists())
			dbSettings.mkdir();
	}

	@Override
	public Rank getUserRank(User user) {
		return Rank.USER;
	}

	@Override
	public void saveUser(User user) {
		FileUtil.writeTo(new File(dbUsers, user.getID() + ".sav"), user.toString());
	}

	@Override
	public void saveUsers(User[] users) {
		for (User user : users) {
			saveUser(user);
		}
	}

	@Override
	public User getUser(String userid) {
		File file = new File(dbUsers, userid + ".sav");
		if (file.exists()) {
			return new User(new Message(FileUtil.read(file)));
		}
		return null;
	}

	@Override
	public void putSettings(String col, Message map) {
		FileUtil.writeTo(new File(dbSettings, col + ".map"), map.toString());
	}

	@Override
	public Message getSettings(String col) {
		File file = new File(dbSettings, col + ".map");
		if(file.exists()) {
			return new Message(FileUtil.read(file));
		}
		return null;
	}
}
