package org.l3eta.util;

import java.security.MessageDigest;

public final class Crypto {
	public static String randHash(String al) {
		return hash(Math.random(), al);
	}
	
	public static String hash(Object o, String al) {
		StringBuilder key = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance(al);
			md.update(String.valueOf(o).getBytes());
			for (byte b : md.digest()) {
				key.append(Integer.toHexString((0xff & b) | 0xffffff00).substring(6));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return key.toString();
	}
}
