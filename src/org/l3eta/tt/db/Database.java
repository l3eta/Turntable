package org.l3eta.tt.db;

import org.l3eta.tt.User;
import org.l3eta.tt.util.Message;

public interface Database {

	// User functions
	public void saveUser(User user);

	public void saveUsers(User[] users);

	public User loadUser(String userid);

	// Settings
	public void putSetting(String setting, Message map);

	public Message getSetting(String setting);
	
	public boolean hasSetting(String setting);
	
	public String getName();
}
