import org.l3eta.tt.Bot;
import org.l3eta.tt.Enums.Vote;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;

public class DanceCommandExample extends Bot {
	public static void main(String[] args) {
		new DanceCommandExample();
	}

	public DanceCommandExample() {
		super("auth", "userid", "roomid");

	}

	public class DanceCommandHandler extends EventListener {

		public DanceCommandHandler(Bot bot) {
			super(bot);
		}

		@EventMethod
		public void speak(ChatEvent event) {
			String text = event.getText();
			if (text.startsWith("/")) {
				String[] args = text.substring(1).split(" ");
				String cmd = args[0].toLowerCase();
				if (cmd.equals("dance")) {
					/*
					 * ----- Note -----
					 * You can do doAction("string");
					 * But the reason I have getBot(). is so if you put it in a
					 * different class you know what to do with it.
					 */
					getBot().doAction("dances"); // Not needed but helpful
					getBot().vote(Vote.UP);
				}
			}
		}

	}
}
