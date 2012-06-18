package org.l3eta.tt.db;

import org.l3eta.tt.User;
import org.l3eta.tt.user.Rank;

public abstract class Database {
	
	public Database(String dbName) {
	}

	public abstract Rank getUserRank(User user);

	public abstract Rank getCommandRank(String command);
	
	public abstract boolean hasCommand(String command);

	public abstract void addCommand(String command, Rank rank);

	public abstract void saveUser(User user);
	
	public abstract void saveUsers(User[] users);
	
	public abstract User getUser(String userid);
}
