package org.l3eta.tt.event;

import org.l3eta.tt.Song;

public class EndSongEvent extends Event {
	private Song song;

	public EndSongEvent(Song song) {
		this.song = song;
	}
	
	public Song getSong() {
		return song;
	}
}
