package org.l3eta.tt.manager;

import java.util.HashMap;
import java.util.Map;

import org.l3eta.tt.Bot;
import org.l3eta.tt.command.Command;

public class CommandManager {
	private Map<String, Command> commands;
	private Bot bot;

	public CommandManager(Bot bot) {
		commands = new HashMap<String, Command>();
		this.bot = bot;
	}

	public void addCommands(Command... commands) {
		for(Command command : commands) {
			addCommand(command);
		}
	} 

	public void addCommand(Command command) {
		if (!commands.containsKey(command.getName())) {
			command.setBot(bot);
			commands.put(command.getName(), command);
			command.load();
		} else {
			bot.error("Command Exists: " + command.getName());
		}
	}
	
	public void remove(String name) {
		//TODO getCommand(name) , .unload();
	}

	public boolean hasCommand(String name) {
		return commands.containsKey(name);
	}

	public Command getCommand(String name) {
		return commands.get(name);
	}
}
