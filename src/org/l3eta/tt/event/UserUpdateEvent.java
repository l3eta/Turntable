package org.l3eta.tt.event;

import org.l3eta.tt.User;
import org.l3eta.tt.util.Message;

public class UserUpdateEvent extends UserEvent {
	private String field;
	private Object val;

	public UserUpdateEvent(User user, Message msg) {
		super(user);
		field = UserUpdateEvent.getField(msg);
		val = msg.get(field);
	}

	public Object getValue() {
		return val;
	}

	public String getField() {
		return field;
	}

	public static String getField(Message msg) {
		return msg.has("fans") ? "fans" : msg.has("name") ? "name" : msg
				.has("avatarid") ? "avatarid" : null;
	}
}
