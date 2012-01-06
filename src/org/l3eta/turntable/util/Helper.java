package org.l3eta.turntable.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.l3eta.turntable.tt.User.Rank;

public class Helper {
	public static String complete(String main, int start) {
		String[] args = main.split(" ");
		StringBuilder string = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			string.append(args[i] + " ");
		}
		return string.toString().substring(0, string.length() - 1);
	}

	public static void log(Object obj) {
		System.out.println(String.valueOf(obj));
	}

	public static String formatTimeDiff(long time) {
		long t = System.currentTimeMillis() - time;
		int m = (int) (t / (1000 * 60)) % 60;
		int h = (int) (t / (1000 * 60 * 60)) % 24;
		return String.format("%d.%d", h, m);
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, Rank> setCommands(Object list, Object commands) {
		if (list instanceof ArrayList<?>) {
			if (commands instanceof HashMap<?, ?>) {
				ArrayList<?> l = ((ArrayList<?>) list);
				HashMap<String, Rank> c = ((HashMap<String, Rank>) commands);
				for (int i = 0; i < l.size(); i++) {
					Object ld = l.get(i);
					if (ld instanceof String[]) {
						String[] data = ((String[]) ld);
						c.put(data[0], Rank.valueOf(data[1]));
					}
				}
				return c;
			}
		}
		return null;
	}
}
