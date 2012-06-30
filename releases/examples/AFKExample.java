
import java.util.ArrayList;
import java.util.List;

import org.l3eta.tt.Bot;
import org.l3eta.tt.User;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.event.DjEvent;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.event.RoomChangeEvent;
import org.l3eta.tt.event.SnagEvent;
import org.l3eta.tt.event.VoteEvent;
import org.l3eta.tt.task.RepeatingTask;

public class AFKExample extends EventListener{
	private List<User> afks = new ArrayList<User>();
	private final int checkerWait = 60; //Time in seconds
	private RepeatingTask checker;
	//TODO update this.
	public AFKExample(Bot bot) {
		super(bot);
	}
	
	@EventMethod
	public void roomChange(RoomChangeEvent e) {
		checker = new RepeatingTask(new Runnable() {
			public void run() {
				//TODO
			}
		}, checkerWait * 1000);
		checker.start();
	}
	
	@EventMethod
	public void snag(SnagEvent e) {		
		e.getUser().updateActivity();
	}
	
	@EventMethod
	public void dj(DjEvent e) {
		
	}
	
	@EventMethod
	public void speak(ChatEvent e) {
		if(e.getType() == ChatType.MAIN) {
			e.getUser().updateActivity();
		}
	}	
	
	@EventMethod
	public void vote(VoteEvent e) {
		if(e.hasUser()) {
			e.getUser();
		}
	}
}
