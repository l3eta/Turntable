import org.l3eta.tt.Bot;
import org.l3eta.tt.User;
import org.l3eta.tt.Util;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.event.UserJoinEvent;
import org.l3eta.tt.util.External;

public class BlackListExample extends Bot {

	public static void main(String[] args) {
		new BlackListExample();
	}

	public BlackListExample() {
		super("auth", "userid", "roomid");

	}

	public class BlackListEventHandler extends EventListener {
		public BlackListEventHandler(Bot bot) {
			super(bot);
		}

		@EventMethod
		public void speak(ChatEvent event) {
			// Handlers main chat banning
			// For pm do ChatType.PM
			if (event.getType() == ChatType.MAIN) {
				String text = event.getText();
				if (text.startsWith("/")) {
					String[] args = text.substring(1).split(" ");
					if (args.length > 2) {
						if (args[0].equals("ban")) {
							User user = new User();
							String param = args[1];
							if (param.equals("-u") || param.equals("-unban")) {
								String name = Util.complete(text, 2);
								user = External.getUser(name);
								if (user.isNull()) {
									return;
								}
								// TODO add in a way to run ban by name.
							} else if (param.equals("-a")
									|| param.equals("-add")) {
								String name = Util.complete(text, 2);
								user = getBot().getUsers().getByName(name);
								if (user.isNull()) {
									return;
								}
								getBot().getUsers().ban(user, "Banned.");
							} else {
								// TODO insert bad ban command param
							}
						}
					}
				}
			}
		}

		@EventMethod
		public void join(UserJoinEvent event) {
			User user = event.getUser();
			if (getBot().getUsers().isBanned(user)) {
				boot(user.getID(), "You're banned!");
				return;
			}
			// TODO do other user join base stuff
			// For instance greeting or something.
		}
	}
}
