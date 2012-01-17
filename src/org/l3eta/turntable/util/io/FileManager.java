package org.l3eta.turntable.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.l3eta.turntable.tt.Room;
import org.l3eta.turntable.tt.User;
import org.l3eta.turntable.tt.User.Rank;
import org.l3eta.turntable.util.Line;

public class FileManager {
	private String data;
	private String logs = data + "logs\\";
	private String db = data + "db\\";
	private String users = db + "users\\";

	public FileManager(String data) {
		this.data = "data\\" + data + "\\";
		logs = this.data + "logs\\";
		db = this.data + "db\\";
		users = db + "users\\";
		makeDirs();
	}
	
	public String getDataFolder() {
		return data;
	}
	
	public void makeDirs() {
		new File(data).mkdirs();
		new File(logs).mkdir();
		new File(db).mkdir();
		new File(users).mkdir();
	}

	public void log(String file, String data) {
		File f = new File(logs, file + ".log");
		try {
			if (!f.exists())
				f.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
			out.write(data + "\r\n");
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addToDatabase(String file, String data) {
		new Database(db, file).append(data);
	}

	public User loadUser(String userid) {
		File f = new File(users, userid + ".sav");
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			Object o = in.readObject();
			if (o instanceof User) {
				return ((User) o);
			}
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new User();
	}

	public void saveUser(User user) {
		File f = new File(users, user.getUserID() + ".sav");
		try {
			if (!f.exists())
				f.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(f));
			out.writeObject(user);
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	

	public Line[] readFile(File file) {
		ArrayList<Line> lines = new ArrayList<Line>();
		try {
			if(!file.exists())
				file.createNewFile();
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while ((line = in.readLine()) != null) {
				lines.add(new Line(line));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lines.toArray(new Line[0]);
	}
	
	public void loadCommands(HashMap<String, Rank> commands) {
		File file = new File(data, "bot.settings");
		Line[] lines = readFile(file);
		for (Line line : lines) {
			if (line.startsWith("command:")) {
				Line[] data = line.substring(8).split(":");
				commands.put(data[0].toString(), Rank.parseLine(data[1]));
			}
		}
	}
	

	public void loadMods(Room room) {
		File file = new File(data, "bot.settings");
		Line[] lines = readFile(file);
		for (Line line : lines) {
			if (line.startsWith("mod:")) {
				Line[] data = line.substring(4).split(":");
				room.addMod(data[0].toString(), Rank.parseLine(data[1]));
			}
		}
	}
}
