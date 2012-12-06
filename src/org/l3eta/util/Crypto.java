package org.l3eta.util;

import java.security.MessageDigest;

public final class Crypto {
	public static final String SHA1 = "sha1";
	public static final String MD5 = "md5";
	
	public static String randHash(String al) {
		return hash(Math.random(), al);
	}
	
	public static String hash(Object data, String al) {
		return hash(data, "", al);
	}
	
	public static String hash(Object data, Object salt, String al) {
		StringBuilder key = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance(al);
			md.update(data.toString().getBytes());
			String s = salt.toString().trim();
			if(!s.equals(""))
				md.update(s.getBytes());
			for (byte b : md.digest()) {
				key.append(Integer.toHexString((0xff & b) | 0xffffff00).substring(6));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return key.toString();
	}
}
