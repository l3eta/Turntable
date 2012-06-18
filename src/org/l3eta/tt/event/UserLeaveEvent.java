package org.l3eta.tt.event;

import org.l3eta.tt.User;

public class UserLeaveEvent extends UserEvent {

	public UserLeaveEvent(User user) {
		super(user);
	}
}
