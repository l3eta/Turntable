package org.l3eta.tt.event;

import org.l3eta.tt.Bot;

public class EventListener {
	private Bot bot;
	
	public EventListener(Bot bot) {
		this.bot = bot;
	}
	
	public final Bot getBot() {
		return bot;
	}
}
