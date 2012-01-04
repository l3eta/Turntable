package org.l3eta.turntable.util;

public class Helper {	
	public static String complete(String main, int start) {
		String[] args = main.split(" ");
		StringBuilder string = new StringBuilder();
		for(int i = start; i < args.length; i++) {
			string.append(args[i] + " ");
		}
		return string.toString().substring(0, string.length() - 1);
	}
	
	public static void log(Object obj) {
		System.out.println(String.valueOf(obj));
	}
}
