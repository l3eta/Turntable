package org.l3eta.tt.event;

import org.l3eta.tt.Song;

public class EndSongEvent extends UserEvent {
	private Song song;
	//TODO fix the NPE 

	public EndSongEvent(Song song) {
		super(song.getUser());
		this.song = song;
	}
	
	public Song getSong() {
		return song;
	}
}
