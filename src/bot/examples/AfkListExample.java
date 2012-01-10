package bot.examples;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.l3eta.turntable.tt.Bot;
import org.l3eta.turntable.tt.Room;
import org.l3eta.turntable.tt.User;
import org.l3eta.turntable.tt.Room.Users;
import org.l3eta.turntable.util.Line;
import org.l3eta.turntable.util.net.Sender;

public class AfkListExample implements Bot {
	private Room room = new Room("Room name");
	public Users Users = room.Users;
	private String[] info = { "auth", "userid", "roomid" };
	private String name = "Example Bot";

	public void init() {
		Sender.start(info);
	}

	public String getName() {
		return name;
	}

	public Room getRoom() {
		return room;
	}

	public void onSnag(Line line) {
		Users.getByID(line).updateActivity();
	}

	public void onRemMod(Line line) {
		// TODO Add your own code here.
	}

	public void onNewMod(Line line) {
		// TODO Add your own code here.
	}

	public void onRemDJ(Line line) {
		Users.getByID(line).updateActivity();
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
		Users.getByID(line).updateActivity();
	}

	public void onDeregister(Line line) {
		User user = new User(line);
		Users.removeUser(user);
		System.out.println(user.getName() + " has left the room!");
	}

	public void onVotes(Line line) {
		Users.getByID(line).updateActivity();
	}

	public void onRegister(Line line) {
		User user = new User(line);
		Users.addUser(user);
		System.out.println(user.getName() + " has joined the room!");
	}

	public void onSpeak(Line line) {
		Users.getByID(line).updateActivity();
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
}
