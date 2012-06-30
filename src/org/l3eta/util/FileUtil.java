package org.l3eta.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileUtil {
	
	public static String read(File file) {
		String data = "";		
		try {
			BufferedReader r = new BufferedReader(new FileReader(file));
			String l;			
			while ((l = r.readLine()) != null) {
				data += " " + l;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return data.trim();
	}
	
	public static void writeTo(File file, String data) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(data);
			bw.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
