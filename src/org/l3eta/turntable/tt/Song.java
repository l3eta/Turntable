package org.l3eta.turntable.tt;

import java.util.ArrayList;

import org.l3eta.turntable.util.Line;

public class Song {
	private User dj;
	private String artist, song, fileid;
	private int[] votes = { 0, 0 };
	private int snags = 0, length = 0;
	private ArrayList<String> snaggers = new ArrayList<String>();

	public Song(User dj, Line line) {
		this.dj = dj;
		this.artist = line.getString("artist");
		this.song = line.getString("song");
		this.fileid = line.getString("_id");
		this.resetData();

	}

	private void resetData() {
		this.snaggers.clear();
		this.snags = 0;
		this.votes = new int[] { 0, 0 };
	}

	public void setVotes(int up, int down) {
		votes = new int[] { up, down };
	}

	public void addSnag(String uid) {
		snaggers.add(uid);
		snags++;
	}

	public ArrayList<String> getSnaggers() {
		return this.snaggers;
	}

	public String getSong() {
		return song;
	}

	public int getLength() {
		return this.length;
	}

	public String getFileID() {
		return fileid;
	}

	public String getArtist() {
		return artist;
	}

	public int getSnags() {
		return snags;
	}

	public User getDj() {
		return dj;
	}

	public String getDjName() {
		return this.dj.getName();
	}

	public String toDataSring() {
		return String.format(
				"{Song:{ artist: \"%s\", song: \"%s\", _id: \"%s\"}}", artist,
				song, fileid);
	}

	public String getSongData() {
		return String
				.format(" [+%d, -%d, (SNAG)%d]", votes[0], votes[1], snags);
	}

	public String toConsole() {
		return String.format("%s started playing %s by %s.", this.getDjName(),
				this.getSong(), this.getArtist());
	}

	public String toString() {
		return String.format("%s Played %s by %s. %s", this.getDjName(),
				this.getSong(), this.getArtist(), this.getSongData());
	}
}
