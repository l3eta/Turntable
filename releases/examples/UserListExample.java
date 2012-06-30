import org.l3eta.tt.Bot;
import org.l3eta.tt.User;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.event.UserJoinEvent;
import org.l3eta.tt.event.UserLeaveEvent;

public class UserListExample extends EventListener {
	public UserListExample(Bot bot) {
		super(bot);
		bot.getCommandManager().addCommand(new Users("users"));
	}
	
	@EventMethod
	public void onLeave(UserLeaveEvent event) {
		bot.info(event.getUser().getName() + " has left the room!");
	}

	@EventMethod
	public void onJoin(UserJoinEvent event) {
		bot.info(event.getUser().getName() + " has joined the room!");
	}

	public class Users extends Command {

		public Users(String name) {
			super(name);
		}

		@Override
		public void execute(User user, String[] args, ChatType type) {
			bot.speak(user.getName() + " There are " + bot.getUserlist().length + " users!");
		}

	}
}
