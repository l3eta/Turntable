package org.l3eta.tt.event;

import org.l3eta.tt.Enums.Vote;
import org.l3eta.tt.Room;
import org.l3eta.tt.User;
import org.l3eta.tt.util.Message;

import com.mongodb.BasicDBList;

public final class VoteEvent extends Event {
	private Vote vote;
	private String userid;
	private int up, down, listeners;
	private User user;
	private boolean hasUser;

	public VoteEvent(Room room, Message message) {
		BasicDBList voteLog = message.getSubList("votelog");
		vote = Vote.get(voteLog.get(1));
		userid = voteLog.get(0).toString();
		if(!userid.equals("")) {
			hasUser = true;
			user = room.getUsers().getByID(userid);
		}
		up = message.getInt("upvotes");
		down = message.getInt("downvotes");
		listeners = message.getInt("listeners");
	}

	public Vote getVote() {
		return vote;
	}
	
	public boolean hasUser() {
		return hasUser;
	}
	
	public User getUser() {
		return user;
	}

	public String getUserID() {
		return userid;
	}

	public int getDown() {
		return down;
	}

	public int getUp() {
		return up;
	}
	
	public int getListeners() {
		return listeners;
	}
}
