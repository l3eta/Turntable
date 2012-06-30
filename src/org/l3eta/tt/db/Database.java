package org.l3eta.tt.db;

import org.l3eta.tt.User;
import org.l3eta.tt.user.Rank;
import org.l3eta.tt.util.Message;

public abstract class Database {
	
	public Database(String dbName) {
	}

	public abstract Rank getUserRank(User user);

	public abstract void saveUser(User user);
	
	public abstract void saveUsers(User[] users);
	
	public abstract User getUser(String userid);
	
	public abstract void putSettings(String col, Message map);
	
	public abstract Message getSettings(String col);
}
