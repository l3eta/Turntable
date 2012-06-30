package org.l3eta.util;

public class StringUtil {

	public static String complete(String string, int index) {
		return complete(string, " ", index);
	}

	public static String complete(String string, String sep, int index) {
		return complete(string.split(sep), sep, index);
	}

	public static String complete(String[] args) {
		return complete(args, " ", 0);
	}

	public static String complete(String[] args, int index) {
		return complete(args, " ", index);
	}
	
	public static String complete(String[] args, int index, int end) {
		return complete(args, " ", index, end);
	}

	public static String complete(String[] args, String sep, int index) {
		return complete(args, sep, index, args.length);
	}
	
	public static String complete(String[] args, String sep, int index, int end) {
		//TODO add in check for failed to end / start
		String str = "";
		for (int i = index; i < end; i++) {
			str += args[i] + sep;
		}
		return str.substring(0, str.length() - sep.length());
	}

	public static boolean contains(String[] args, String string) {
		for (String str : args) {
			if (str.equals(string))
				return true;
		}
		return false;
	}

	public static int indexOf(String[] args, String string) {
		if(contains(args, string)) {
			for(int i = 0; i < args.length; i++) {
				if(args[i].equals(string))
					return i;
			}
		}		
		return -1;
	}
}
