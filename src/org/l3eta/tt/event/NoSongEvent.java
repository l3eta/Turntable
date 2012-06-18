package org.l3eta.tt.event;

import org.l3eta.tt.util.Message;

public class NoSongEvent extends Event {
	private Message message;

	public NoSongEvent(Message message) {
		this.message = message;
	}
	
	public Message getMessage() {
		return message;
	}
}
