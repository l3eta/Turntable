package org.l3eta.turntable.tt;

import org.l3eta.turntable.util.Line;

public interface Bot {

	public void init();
	
	public void reload();
	
	public Room getRoom();
	
	public String getName();

	public void onSnag(Line line);

	public void onRemMod(Line line);

	public void onNewMod(Line line);

	public void onRemDJ(Line line);

	public void onUpdate(Line line);

	public void onBooted(Line line);

	public void onNewSong(Line line);

	public void onNoSong(Line line);

	public void onAddDJ(Line line);

	public void onDeregister(Line line);

	public void onVotes(Line line);

	public void onRegister(Line line);

	public void onSpeak(Line line);

	public void onOther(Line line);
}
