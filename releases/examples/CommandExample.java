import org.l3eta.tt.Bot;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.event.Event.EventMethod;

public class CommandExample extends Bot {

	public static void main(String[] args) {
		new CommandExample();
	}

	public CommandExample() {
		super("auth", "userid", "roomid");
		getEventManager().registerListener(new CommandEventHandler(this));

	}

	public class CommandEventHandler extends EventListener {

		public CommandEventHandler(Bot bot) {
			super(bot);
		}

		@EventMethod
		public void speak(ChatEvent event) {
			String text = event.getText();
			if (text.startsWith("/")) {
				String[] args = text.substring(1).split(" ");
				String cmd = args[0].toLowerCase();
				// Lower case it because you have no use for upper case
				// commands.
				if (cmd.equals("hello") || cmd.equals("hi")) {
					getBot().speak(String.format("Hi %s!", event.getUser()));
				}
			}
		}

	}
}
