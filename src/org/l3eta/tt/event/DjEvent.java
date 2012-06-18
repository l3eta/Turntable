package org.l3eta.tt.event;

import org.l3eta.tt.User;

public final class DjEvent extends UserEvent {
	private boolean djing;
	private User mod;

	public DjEvent(User user, boolean djing) {
		super(user);
		this.djing = djing;
	}
	
	public DjEvent(User user, User mod) {
		this(user, false);
		this.mod = mod;
	}

	public boolean isDjing() {
		return djing;
	}

	public User getMod() {
		return mod;
	}

	public boolean isForced() {
		return mod != null;
	}
}
