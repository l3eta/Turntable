package org.l3eta.turntable;

import org.l3eta.turntable.tt.Bot;
import org.l3eta.turntable.tt.Commands;
import org.l3eta.turntable.util.net.Client;

public class Starter {
	// private String test = "4ef912560c4cc82f749d5b2f";
	private String tfod = "4ee5efa014169c52d27c9bdb";

	public static void main(String[] args) {
		new Starter();
	}

	public Starter() {		
		try {
			run("Future");
			Bot b = (Bot) bot.examples.CommandExample.class.newInstance();
			new Client(tfod, 1500).start(new Commands(b));			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void run(String botName) {
		try {
			Runtime.getRuntime().exec("cmd.exe /C start " + botName + ".bat");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
