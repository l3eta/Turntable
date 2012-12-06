import org.l3eta.tt.Bot;


public class BotExample extends Bot {
	public BotExample() {
		super("auth", "userid", "roomid");
		getEventManager().addListener(new AFKExample());
		getEventManager().addListener(new BlackListExample());
		getEventManager().addListener(new CommandExample());
		getEventManager().addListener(new UserListExample());
	}
}
