package org.l3eta.tt.event;


public class RoomUpdateEvent extends Event {
	private String update;

	public RoomUpdateEvent(String update) {
		this.update = update;
	}
	
	public String getUpdate() {
		return update;
	}
}
