package bot.examples;

import org.l3eta.turntable.tt.Bot;
import org.l3eta.turntable.tt.User;

public class AfkListExample extends Bot {

	public void init() {
		this.start("auth", "userid", "roomid");
	}

	@Override
	public void onSnag(User user) {
		user.updateActivity();
	}

	@Override
	public void onVotes(Votelog votelog) {
		if (!votelog.getUserID().equals("null"))
			users.getByID(votelog.getUserID()).updateActivity();
	}

	@Override
	public void onSpeak(User user, String text) {
		user.updateActivity();
	}
}
