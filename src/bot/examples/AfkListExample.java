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

	private HashMap<String, Long> list = new HashMap<String, Long>();

	public void init() {
		Sender.start(info);
	}

	@Override
	public Room getRoom() {
		return room;
	}

	@Override
	public void onSnag(Line line) {
		String uid = line.getString("userid");
		list.put(uid, System.currentTimeMillis());
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
		String userid = line.getString("userid");
		this.updateActivity(userid);
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
		String userid = line.getString("userid");
		this.updateActivity(userid);
	}

	@Override
	public void onDeregister(Line line) {
		User user = new User(line,
				Users.getRankFromID(line.getString("userid")));
		list.remove(user.getUserID());
		room.Users.removeUser(Users.getIndex(user));
		System.out.println(user.getName() + " has left the room!");
	}

	@Override
	public void onVotes(Line line) {
		String userid = line.getString("userid");
		this.updateActivity(userid);
	}

	@Override
	public void onRegister(Line line) {
		User user = new User(line,
				Users.getRankFromID(line.getString("userid")));
		Users.addUser(user);
		list.put(user.getUserID(), System.currentTimeMillis());
		System.out.println(user.getName() + " has joined the room!");
	}

	@Override
	public void onSpeak(Line line) {
		String userid = line.getString("userid");
		this.updateActivity(userid);
	}

	@Override
	public void onOther(Line line) {
		// TODO Clean up this code a bit.
		if (line.contains("users:{ ")) {
			String[] users = Pattern.compile("[0-9]\\:\\{").split(
					line.substring(line.indexOf("users:{ ")));
			for (String user : users) {
				if (user.startsWith(" name:")) {
					User u = new User(new Line("command: \"registered\" "
							+ user));
					this.updateActivity(u.getUserID());
					Users.addUser(new User(new Line("command: \"registered\" "
							+ user), Users.getRankFromID(u.getUserID())));
				}
			}
		}
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}
	
	public void updateActivity(String userid) {
		list.put(userid, System.currentTimeMillis());
	}
}
