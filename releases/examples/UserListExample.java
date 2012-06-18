import org.l3eta.tt.Bot;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.event.UserJoinEvent;
import org.l3eta.tt.event.UserLeaveEvent;

public class UserListExample extends Bot {

	public UserListExample() {
		super("auth", "userid", "roomid");
		getEventManager().registerListener(new UserListEvents(this));
	}

	public class UserListEvents extends EventListener {
		public UserListEvents(Bot bot) {
			super(bot);
		}
		
		@EventMethod
		public void onChat(ChatEvent event) {
			if(event.getType() == ChatType.MAIN) {
				if(event.getText().startsWith("/")) {
					String cmd = event.getText().substring(1);
					if(cmd.equals("users")) {
						/*
						 * ----- Note -----
						 * You can do speak("string");
						 * But the reason I have getBot(). is so if you put it in a
						 * different class you know what to do with it.
						 */
						getBot().speak(event.getUser().getName() + " There are " + getUsers().getList().length + " users!");
					}
				}
			}
		}

		@EventMethod
		public void onLeave(UserLeaveEvent event) {
			System.out.println(event.getUser().getName()
					+ " has left the room!");
		}

		@EventMethod
		public void onJoin(UserJoinEvent event) {
			System.out.println(event.getUser().getName()
					+ " has joined the room!");
		}
	}
}
