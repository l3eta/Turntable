package org.l3eta.tt.db;

import org.l3eta.tt.User;
import org.l3eta.tt.user.Rank;

public class DefaultDatabase extends Database {

	public DefaultDatabase(String dbName) {
		super(dbName);
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
	public boolean hasCommand(String command) {
		return false;
	}

	@Override
	public void addCommand(String command, Rank rank) {
	}

	@Override
	public void saveUser(User user) {
	}

	@Override
	public void saveUsers(User[] users) {
	}

	@Override
	public User getUser(String userid) {
		return null;
	}	
}
