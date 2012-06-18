package bot.examples;

import org.l3eta.turntable.tt.Bot;
import org.l3eta.turntable.tt.Room.Song;
import org.l3eta.turntable.tt.User;

/**
 * 
 * @warning turntable no longer allows auto awesome bots, This code is meant to
 *          be an example of how to vote on new song. Only for educational use.
 * 
 */
public class AutoBopExample extends Bot {

	public void init() {
		this.start("auth", "userid", "roomid");
	}

	// The wrong way that TurnTable does not approve of.
	@Override
	public void onNewSong(User user, Song song) {
		vote(true);
	}

	// This is the correct way to do it.
	@Override
	public void onSpeak(User user, String text) {
		if (text.startsWith("/")) {
			String[] args = text.substring(1).split(" ");
			String cmd = args[0];
			if (cmd.equals("dance")) {
				doAction("dances"); //Not always needed.
				vote(true);
			}
		}
	}
}
