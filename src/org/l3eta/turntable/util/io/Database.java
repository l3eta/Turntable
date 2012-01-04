package org.l3eta.turntable.util.io;

import java.io.*;
import java.util.ArrayList;

public class Database {
	private File db;
	private ArrayList<String> Lines = new ArrayList<String>();
	private boolean hasRead = false;

	public Database(String name) {
		db = new File("data/db/", name + ".db");
		this.read();
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
					Lines.add(line);
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
		for(int i = 0; i < Lines.size(); i++) {
			String line = Lines.get(i);
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
		Lines.add(o);
		clean();
	}

}