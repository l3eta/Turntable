package bot.examples;

import org.l3eta.turntable.tt.Bot;
import org.l3eta.turntable.tt.Room;
import org.l3eta.turntable.util.BotManager.BotWindow;
import org.l3eta.turntable.util.Line;

public class CommandExample extends Bot {
	private Room room = new Room("Room name");
	private String name = "Example Bot";
	private String[] info = { "auth", "userid", "roomid" };

	public void init() {
		start(info);
	}

	public Room getRoom() {
		return room;
	}

	public String getName() {
		return name;
	}

	public void onSnag(Line line) {
		// TODO Add your own code here.
	}

	public void onRemMod(Line line) {
		// TODO Add your own code here.
	}

	public void onNewMod(Line line) {
		// TODO Add your own code here.
	}

	public void onRemDJ(Line line) {
		// TODO Add your own code here.
	}

	public void onUpdate(Line line) {
		// TODO Add your own code here.
	}

	public void onBooted(Line line) {
		// TODO Add your own code here.
	}

	public void onNewSong(Line line) {
		// TODO Add your own code here.
	}

	public void onNoSong(Line line) {
		// TODO Add your own code here.
	}

	public void onAddDJ(Line line) {
		// TODO Add your own code here.
	}

	public void onDeregister(Line line) {
		// TODO Add your own code here.
	}

	public void onVotes(Line line) {
		// TODO Add your own code here.
	}

	public void onRegister(Line line) {
		// TODO Add your own code here.
	}

	public void onSpeak(Line line) {
		String name = line.getString("name"), text = line.getString("text");
		if (text.startsWith("!")) {
			text = text.substring(1);
			String[] args = text.split(" ");
			Line cmd = new Line(args[0]);
			if (cmd.equals("hello")) {
				speak(String.format("Hi %s, How are you?", name));
			}
		}
	}

	public void onOther(Line line) {
		// TODO Add your own code here.
	}

	public void reload() {
		// TODO Add your own code here.

	}

	@Override
	public void setBotWindow(BotWindow window) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndSong() {
		// TODO Auto-generated method stub

	}
}
