package org.l3eta.tt.command;

import org.l3eta.tt.Bot;
import org.l3eta.tt.User;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.user.Rank;

public abstract class Command {
	private String name;
	private Rank rank = Rank.USER;
	protected Bot bot;

	public Command(String name) {
		this.name = name;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public Rank getRank() {
		return rank;
	}

	public void setBot(Bot bot) {
		this.bot = bot;
	}

	public String getName() {
		return name;
	}

	public boolean canExecute(User user) {
		return user.getRank().equals(rank);
	}

	public void load() {
	}

	public void unload() {
	}

	public abstract void execute(User user, String[] args, ChatType type);
}
