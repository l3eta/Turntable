package org.l3eta.tt.event;

import org.l3eta.tt.User;

public class UserBootEvent extends UserEvent {
	private User mod;
	private String reason;
	
	public UserBootEvent(User user, User mod, String reason) {
		super(user);
		this.mod = mod;
		this.reason = reason;
	}
	
	public User getMod() {
		return mod;
	}
	
	public String getReason() {
		return reason;
	}
}
