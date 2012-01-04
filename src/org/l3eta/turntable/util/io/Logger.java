package org.l3eta.turntable.util.io;

import java.io.*;

import org.l3eta.turntable.tt.User;

public class Logger {
	static String data = "data\\";
	static String logs = data + "logs\\";
	static String db = data + "db\\";
	public static String users = db + "users\\";

	public static void log(String file, String data) {
		File f = new File(logs, file + ".log");
		try {
			if (!f.exists())
				f.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
			out.write(data + "\r\n");
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addToDatabase(String file, String data) {
		Database database = new Database(file);
		database.append(data);
	}
	
	public static User loadUser(String userid) {
		File f = new File(users, userid + ".sav");
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
			Object o = in.readObject();
			if(o instanceof User) {
				return ((User) o);
			}
			in.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return new User();
	}
	
	public static void saveUser(User user) {
		File f = new File(users, user.getUserID() + ".sav");
		try {
			if (!f.exists())
				f.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			out.writeObject(user);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
