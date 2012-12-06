package org.l3eta.tt.db;

import java.io.File;

import org.l3eta.tt.User;
import org.l3eta.tt.util.Message;
import org.l3eta.util.FileUtil;

public class DefaultDatabase implements Database {
	private String database;
	private File dbRoot = new File(System.getProperty("user.home") + "/turntable/databases");
	private File dbUsers;
	private File dbSettings;

	public DefaultDatabase(String database) {
		this.database = database;
		if (!dbRoot.exists())
			dbRoot.mkdirs();
		dbRoot = new File(dbRoot, database);
		if (!dbRoot.exists())
			dbRoot.mkdir();
		dbUsers = new File(dbRoot, "users");
		if (!dbUsers.exists())
			dbUsers.mkdir();
		dbSettings = new File(dbRoot, "settings");
		if (!dbSettings.exists())
			dbSettings.mkdir();
	}

	public void saveUser(User user) {
		FileUtil.writeTo(new File(dbUsers, user.getID() + ".sav"), user.toString());
	}

	public void saveUsers(User[] users) {
		for (User user : users)
			saveUser(user);
	}

	public User loadUser(String userid) {
		File file = new File(dbUsers, userid + ".sav");
		if (file.exists())
			return new User(new Message(FileUtil.read(file)));
		return null;
	}

	public void putSetting(String setting, Message map) {
		FileUtil.writeTo(new File(dbSettings, setting + ".map"), map.toString());
	}

	public Message getSetting(String setting) {
		File file = new File(dbSettings, setting + ".map");
		if(file.exists())
			return new Message(FileUtil.read(file));
		return null;
	}
	
	public boolean hasSetting(String setting) {
		return new File(dbSettings, setting + ".map").exists();
	}

	public String getName() {
		return database;
	}
}
