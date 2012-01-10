package bot.examples;

import java.util.ArrayList;

import org.l3eta.turntable.tt.Bot;
import org.l3eta.turntable.tt.Room;
import org.l3eta.turntable.util.Line;
import org.l3eta.turntable.util.net.Sender;

public class BlackListExample implements Bot {
	private Room room = new Room("Room name");
	private String name = "Example Bot";
	private String[] info = { "auth", "userid", "roomid" };
	private ArrayList<String> blackList = new ArrayList<String>();

	public void init() {
		Sender.start(info);
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
		String userid = line.getString("userid");
		if (blackList.contains(userid)) {
			Sender.Mod.boot(userid, "You are blacklist!");
		}
	}

	public void onSpeak(Line line) {
		// TODO Add your own code here.
	}

	public void onOther(Line line) {
		// TODO Add your own code here.
	}

	public void reload() {
		// TODO Add your own code here.

	}
}
