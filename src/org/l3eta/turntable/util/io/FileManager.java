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

import org.l3eta.turntable.tt.User;

public class FileManager {
	private static String data = "data\\";
	private static String logs = data + "logs\\";
	private static String db = data + "db\\";
	private static String users = db + "users\\";

	public static void log(String file, String data) {
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

	public static void addToDatabase(String file, String data) {
		new Database(file).append(data);
	}

	public static User loadUser(String userid) {
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

	public static void saveUser(User user) {
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

	public static ArrayList<String[]> loadCommands(File file) {
		ArrayList<String[]> commands = new ArrayList<String[]>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("command:")) {
					commands.add(line.replace("command:", "").split(":"));
				}
			}
			return commands;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
