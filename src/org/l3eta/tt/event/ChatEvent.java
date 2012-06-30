package org.l3eta.tt.event;

import org.l3eta.tt.User;

public final class ChatEvent extends UserEvent {
	private String text;
	private ChatType type;

	public ChatEvent(User user, String text, ChatType type) {
		super(user);
		this.text = text;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public ChatType getType() {
		return type;
	}

	public enum ChatType {
		PM, MAIN;
		
		public boolean isMain() {
			return this == MAIN;
		}
	}
}
