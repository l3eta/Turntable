package org.l3eta.tt.event;

import org.l3eta.tt.Room.RoomData;
import org.l3eta.tt.util.Message;

public final class RoomChangeEvent extends Event {
	private Message json;
	private RoomData roomData;

	public RoomChangeEvent(Message json) {
		this.json = json;
		roomData = new RoomData(json);
	}

	public RoomData getRoomData() {
		return roomData;
	}

	public Message getMessage() { 
		return json;
	}
}
