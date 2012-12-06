package org.l3eta.tt.event;

import org.l3eta.tt.Bot;
import org.l3eta.tt.event.Event.Listener;

@Listener
public class EventListener {
	protected Bot bot;
	
	public EventListener() {	
	}
	
	public void setBot(Bot bot) {
		this.bot = bot;
		loaded();
	}
	
	public void loaded() {
	}
}
