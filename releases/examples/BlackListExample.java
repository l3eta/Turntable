import org.l3eta.tt.Bot;
import org.l3eta.tt.User;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.event.UserJoinEvent;
import org.l3eta.tt.user.Rank;
import org.l3eta.tt.util.External;
import org.l3eta.util.StringUtil;

public class BlackListExample extends EventListener {
	private Rank banRank = Rank.ADMIN;

	public BlackListExample(Bot bot) {
		super(bot);
		// TODO make a loading factor for databases
	}

	@EventMethod
	public void join(UserJoinEvent event) {
		User user = event.getUser();
		if (bot.getUsers().isBanned(user)) {
			bot.boot(user.getID(), "You're banned!");
			return; // Stop doing anything else below here.
		}
		// TODO do other user join base stuff
		// For instance greeting or something.
	}

	class Ban extends Command {
		public Ban() {
			super("ban");
			setRank(banRank);
		}

		// TODO add in checks to tell them the reason needs to be last
		// TODO add this to the db settings.
		@Override
		public void execute(User user, String[] args, ChatType type) {
			int ri = StringUtil.indexOf(args, "-r");
			String name = "", reason = "Requested.";
			if (ri != -1) {
				name = StringUtil.complete(args, 0, ri);
				reason = StringUtil.complete(args, ri);
			} else {
				name = StringUtil.complete(args);
			}
			user = bot.getUsers().getByName(name);
			if (user.isNull()) {
				doError(user, type, "Could not find '" + name + "'.");
				return;
			}
			bot.getUsers().ban(user, reason);
		}

		private void doError(User user, ChatType type, String error) {
			if (type == ChatType.MAIN) {
				bot.speak(bot.format("%s, %s", user.getName(), error));
			} else if (type == ChatType.PM) {
				bot.sendPM(user, error);
			}
		}
	}

	class Unban extends Command {
		public Unban() {
			super("unban");
			setRank(banRank);
		}

		@Override
		public void execute(User user, String[] args, ChatType type) {
			String name = StringUtil.complete(args);
			User banned = External.getUser(name);
			if (user.isNull()) {
				doError(user, type, "Could not find '" + name + "'.");
				return;
			}
			bot.getUsers().unban(banned.getID());
		}

		private void doError(User user, ChatType type, String error) {
			if (type == ChatType.MAIN) {
				bot.speak(bot.format("%s, %s", user.getName(), error));
			} else if (type == ChatType.PM) {
				bot.sendPM(user, error);
			}
		}
	}
}
