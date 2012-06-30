package org.l3eta.tt;

import org.l3eta.tt.util.Message;

public class Song {
	private static Room room;
	private User user;
	private String id;
	private Integer up, down, snag;

	// Meta / Useless
	private String artist, genre, name, album;

	public Song(Message message, boolean isPlaylistSong) {
		if(!isPlaylistSong)
			this.user = room.getUsers().getByID(message.getString("djid"));
		this.id = message.getString("_id");
		message = message.getSubObject("metadata");
		this.name = message.getString("song");
		this.artist = message.getString("artist");
		this.album = message.getString("album");
		this.genre = message.getString("genre");
		snag = 0;
		up = 0;
		down = 0;
	}

	public String getID() {
		return id;
	}

	public String getUserID() {
		return user.getID();
	}

	public String getName() {
		return name;
	}

	public String getGenre() {
		return genre;
	}

	public String getAlbum() {
		return album;
	}

	public String getArtist() {
		return artist;
	}

	public Integer getUpVotes() {
		return up;
	}

	public Integer getDownVotes() {
		return down;
	}

	public Integer getSnags() {
		return snag;
	}

	public User getUser() {
		return user;
	}

	// Bot functions
	protected void setUp(int up) {
		this.up = up;
	}

	protected void setDown(int down) {
		this.down = down;
	}

	protected void setSnag(int snag) {
		this.snag = snag;
	}

	protected static void setRoom(Room room) {
		Song.room = room;
	}
	
	protected void addSnag() {
		snag++;
	}
}
