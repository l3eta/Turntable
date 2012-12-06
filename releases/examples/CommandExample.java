import org.l3eta.tt.Enums.Vote;
import org.l3eta.tt.User;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.event.ChatEvent.ChatType;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.manager.CommandManager;

public class CommandExample extends EventListener {

	public CommandExample() {
		
	}
	
	public void loaded() {
		CommandManager cm = bot.getCommandManager();
		cm.addCommand(new Hello("hello"));
		cm.addCommand(new Dance("dance"));
	}

	public class Dance extends Command {

		public Dance(String name) {
			super(name);
		}

		@Override
		public void execute(User user, String[] args, ChatType type) {
			bot.doAction("dances");
			bot.vote(Vote.UP);
		}
	}

	public class Hello extends Command {

		public Hello(String name) {
			super(name);
		}

		@Override
		public void execute(User user, String[] args, ChatType type) {
			bot.speak(String.format("Hello %s!", user.getName()));
		}
	}
}
