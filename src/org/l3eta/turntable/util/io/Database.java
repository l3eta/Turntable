package org.l3eta.turntable.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.l3eta.turntable.util.Line;


public class Database {
	private File db;
	private ArrayList<String> lines = new ArrayList<String>();
	private boolean hasRead = false;

	public Database(String name) {
		db = new File("data/db/", name + ".db");
		this.read();
	}	
	
	public String findString(String key, String value, String find) {
		for(int i = 0; i < lines.size(); i++) {
			Line line = new Line(lines.get(i));
			if(line.getString(key).equals(value)) {
				return line.getString(find);
			}
		}
		return null;
	}
	
	public String getRandomLine() {
		return lines.get(new Random().nextInt(lines.size()));
	}

	public void read() {
		if(db.exists()) {
			if (hasRead)
				return;
			try {
				FileReader f = new FileReader(db);
				BufferedReader in = new BufferedReader(f);
				String line;
				while ((line = in.readLine()) != null) {
					lines.add(line);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			hasRead = true;
		} else {
			db.mkdirs();
			try {
				db.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}			
			read();
		}		
	}

	public void clean() {
		ArrayList<String> Temp = new ArrayList<String>();
		for(int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if(!Temp.contains(line)) {
				Temp.add(line);
			}
		}
		writeArrayList(Temp);
	}
	
	public void writeArrayList(ArrayList<String> list) {
		try {
			FileWriter w = new FileWriter(db);
			for(int i = 0; i < list.size(); i++) {
				w.write(list.get(i) + "\n");
				w.flush();
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void append(String o) {
		lines.add(o);
		clean();
	}

}