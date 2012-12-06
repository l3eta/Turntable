import java.util.ArrayList;

import org.l3eta.tt.User;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.event.UserJoinEvent;
import org.l3eta.tt.user.Rank;
import org.l3eta.util.StringUtil;
import org.l3eta.tt.util.*;

import java.util.*;

/*
 * 
 * Notes about this:
 * 
 * You'll need to either make it save when unloaded or save when it edits itself.
 */

public class BlackListExample extends EventListener {
	private Rank banRank = Rank.ADMIN;
	private HashMap<String, Long> bannedUsers;

	public BlackListExample() {
		bannedUsers = new HashMap<String, Long>();
	}
	
	public void loaded() {
		//TODO add in db
		bannedUsers.clear();
		for(Message u : bot.getDatabase().getBannedUsers()) 
			bannedUsers.put(u.getString("userid"), u.getLong("expires"));
	}

	@EventMethod
	public void join(UserJoinEvent event) {
		User user = event.getUser();
		if (bannedUsers.get(user.getID()) != null) {
			//TODO check current time with the expire time
			bot.boot(user.getID(), "You're banned!");
			return; // Stop doing anything else below here.
		}
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
			if (user == null) {
				doError(user, type, "Could not find '" + name + "'.");
				return;
			}
			ban(user, reason);
		}
		
		private void ban(User user, String reason) {
			//TODO add in kick function and then make a expires argument.
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
			if (user == null) {
				doError(user, type, "Could not find '" + name + "'.");
				return;
			}
			//Maybe add a check to use external.
			unban(banned.getID());
		}
		
		private void unban(String userid) {
			//Write a checker for if is banned if so remove it from the list
			
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
