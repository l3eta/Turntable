package bot.examples;

import org.l3eta.turntable.tt.Bot;
import org.l3eta.turntable.tt.Room;
import org.l3eta.turntable.util.Line;
import org.l3eta.turntable.util.net.Sender;

public class CommandExample implements Bot {
	private Room room = new Room("Room name");
	
	private String[] info = { "auth", "userid", "roomid" };
	
	public void init() {
		Sender.start(info);
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		String name = line.getString("name"), text = line.getString("text");
		if(text.startsWith("!")) {
			text = text.substring(1);
			String[] args = text.split(" ");
			Line cmd = new Line(args[0]);
			if(cmd.equals("hello")) {
				Sender.Talk.speak(String.format("Hi %s, How are you?", name));
			}
		}
	}

	@Override
	public void onOther(Line line) {
		// TODO Auto-generated method stub
	}
}
