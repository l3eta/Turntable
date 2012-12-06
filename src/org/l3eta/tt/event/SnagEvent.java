package org.l3eta.tt.event;

import org.l3eta.tt.Song;
import org.l3eta.tt.User;

public class SnagEvent extends UserEvent {
	private Song song;
	
	public SnagEvent(User user, Song song) {
		super(user);
		this.song = song;
	}
	
	public Song getSong() {
		return song;
	}

}
