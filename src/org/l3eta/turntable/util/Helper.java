package org.l3eta.turntable.util;

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
}
