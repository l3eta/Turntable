package org.l3eta.turntable.tt;

import org.l3eta.turntable.util.Line;

public interface Bot {
	public abstract void init();

	public abstract Room getRoom();

	public abstract void onSnag(Line line);

	public abstract void onRemMod(Line line);

	public abstract void onNewMod(Line line);

	public abstract void onRemDJ(Line line);

	public abstract void onUpdate(Line line);

	public abstract void onBooted(Line line);

	public abstract void onNewSong(Line line);

	public abstract void onNoSong(Line line);

	public abstract void onAddDJ(Line line);

	public abstract void onDeregister(Line line);

	public abstract void onVotes(Line line);

	public abstract void onRegister(Line line);

	public abstract void onSpeak(Line line);

	public abstract void onOther(Line line);
}
