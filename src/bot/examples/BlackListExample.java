package bot.examples;

import java.util.ArrayList;

import org.l3eta.turntable.tt.Bot;
import org.l3eta.turntable.tt.Room;
import org.l3eta.turntable.util.Line;
import org.l3eta.turntable.util.net.Sender;

public class BlackListExample implements Bot {
	private Room room = new Room("Room name");
	
	private String[] info = { "auth", "userid", "roomid" };
	private ArrayList<String> blackList = new ArrayList<String>();
	
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
		String userid = line.getString("userid");
		if(blackList.contains(userid)) {
			Sender.Mod.boot(userid, "You are blacklist!");
		}
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
