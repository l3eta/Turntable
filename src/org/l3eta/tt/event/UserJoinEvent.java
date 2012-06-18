package org.l3eta.tt.event;

import org.l3eta.tt.User;

public class UserJoinEvent extends UserEvent {

	public UserJoinEvent(User user) {
		super(user);
	}
}
