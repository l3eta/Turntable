package bot.examples;

import java.util.regex.Pattern;

import org.l3eta.turntable.tt.Bot;
import org.l3eta.turntable.tt.Room;
import org.l3eta.turntable.tt.Room.Users;
import org.l3eta.turntable.tt.User;
import org.l3eta.turntable.util.BotManager.BotWindow;
import org.l3eta.turntable.util.Line;

public class UserListExample extends Bot {
	private Room room = new Room("Room name");
	private Users Users = room.Users;
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
		User user = new User(line);
		Users.removeUser(user);
		System.out.println(user.getName() + " has left the room!");
	}

	public void onVotes(Line line) {
		// TODO Add your own code here.
	}

	public void onRegister(Line line) {
		User user = new User(line);
		Users.addUser(user);
		System.out.println(user.getName() + " has joined the room!");
	}

	public void onSpeak(Line line) {
		// TODO Add your own code here.
	}

	public void onOther(Line line) {
		if (line.contains("users:{ ")) {
			Line[] lines = line.split(Pattern.compile("[0-9]\\:\\{"),
					line.substring(line.indexOf("users:{ ")));
			for (Line l : lines) {
				if (l.startsWith(" name:")) {
					Users.addUser(new User(l));
				}
			}
		}
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
