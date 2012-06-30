package org.l3eta.tt;

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
}
