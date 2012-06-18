package org.l3eta.tt.event;

import org.l3eta.tt.User;

public class ModEvent extends UserEvent {
	private boolean isMod;
	
	public ModEvent(User user, boolean isMod) {
		super(user);
		this.isMod = isMod;
	}
	
	public boolean isMod() {
		return isMod;
	}
}
