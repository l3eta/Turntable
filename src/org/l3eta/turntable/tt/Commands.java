package org.l3eta.turntable.tt;

import org.l3eta.turntable.util.Line;

public class Commands {
	private Bot bot;
	
	public Commands(Bot bot) {
		this.bot = bot;
		this.bot.init();
	}

	private final String register = "registered";
	private final String deregister = "deregistered";
	private final String addDj = "add_dj";
	private final String speak = "speak";
	private final String votes = "update_votes";
	private final String nosong = "nosong";
	private final String newsong = "newsong";
	private final String booted = "booted_user";
	private final String update = "update_user";
	private final String remDj = "rem_dj";
	private final String newMod = "new_moderator";
	private final String remMod = "rem_moderator";
	private final String snag = "snagged";
	private final String other = "other";

	public void handleCommand(String command, Line line) {
		switch (command) {
		case speak:
			bot.onSpeak(line);
			break;
		case register:
			bot.onRegister(line);
			break;
		case deregister:
			bot.onDeregister(line);
			break;
		case votes:
			bot.onVotes(line);
			break;
		case addDj:
			bot.onAddDJ(line);
			break;
		case nosong:
			bot.onNoSong(line);
			break;
		case newsong:
			bot.onNewSong(line);
			break;
		case booted:
			bot.onBooted(line);
			break;
		case update:
			bot.onUpdate(line);
			break;
		case remDj:
			bot.onRemDJ(line);
			break;
		case newMod:
			bot.onNewMod(line);
			break;
		case remMod:
			bot.onRemMod(line);
			break;
		case snag:
			bot.onSnag(line);
			break;
		case other: 
			bot.onOther(line);
			break;
		}
	}
	
	public Room getRoom() {
		return bot.getRoom();
	}
}
