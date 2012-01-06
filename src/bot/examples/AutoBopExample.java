package bot.examples;

import org.l3eta.turntable.tt.Bot;
import org.l3eta.turntable.tt.Room;
import org.l3eta.turntable.util.Line;
import org.l3eta.turntable.util.net.Sender;

public class AutoBopExample implements Bot {
	private Room room = new Room("Room name");

	private String[] info = { "auth", "userid", "roomid" };

	public void init() {
		Sender.start(info);
	}

	@Override
	public Room getRoom() {
		return room;
	}

	@Override
	public void onSnag(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRemMod(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNewMod(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRemDJ(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpdate(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onBooted(Line line) {
		Sender.Other.vote(true);
	}

	@Override
	public void onNewSong(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onNoSong(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAddDJ(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeregister(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onVotes(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRegister(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSpeak(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onOther(Line line) {
		// TODO Auto-generated method stub
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}
}
