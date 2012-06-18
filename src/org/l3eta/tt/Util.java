package org.l3eta.tt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Util {
	private final static Random random = new Random();


	public static int random(int max) {
		return random(0, max);
	}

	public static int random(int min, int max) {
		return random.nextInt(max - min) + min;
	}

	public static String complete(String main, int start) {
		return complete(main, " ", start);
	}

	public static boolean match(String string, String regex) {
		Matcher match = Pattern.compile(regex).matcher(string);
		return match.find();
	}

	public static String matchFind(String string, String regex, int index) {
		Matcher match = Pattern.compile(regex).matcher(string);
		while (match.find()) {
			return match.group(index);
		}
		return null;
	}

	public static String complete(String string, String sep, int start) {
		String[] args = string.split(sep);
		StringBuilder str = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			str.append(args[i] + sep);
		}
		return str.toString().substring(0, str.toString().length() - 1);
	}

	
	
	public static String getTextFromFile(InputStream in) {
		String data = "";
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String l;			
			while ((l = r.readLine()) != null) {
				data += " " + l.replaceAll("\t", "");
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return data.trim();
	}
	
	public static String getTextFromFile(File file) {
		try {
			return getTextFromFile(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
