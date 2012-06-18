package org.l3eta.tt.event;

import org.l3eta.tt.User;

public class UserEvent extends Event {
	private User user;

	public UserEvent(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
