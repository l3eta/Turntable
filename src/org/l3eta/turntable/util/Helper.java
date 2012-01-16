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

	public static String formatTimeDiff(long last) {
		long time = System.currentTimeMillis() - last;	
		
		final int sec = (int) (time / 1000), h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":"
				+ (s < 10 ? "0" + s : s);
	}
	
	
}
