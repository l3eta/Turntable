import org.l3eta.tt.Bot;


public class BotExample extends Bot {
	public BotExample() {
		super("auth", "userid", "roomid");
		getEventManager().addListener(new AFKExample(this));
		getEventManager().addListener(new BlackListExample(this));
		getEventManager().addListener(new CommandExample(this));
		getEventManager().addListener(new UserListExample(this));
	}
}
