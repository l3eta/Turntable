package org.l3eta.tt.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Match {
	private String[] groups = {};
	private boolean matches = false;

	public Match(String string, String regex) {
		Matcher match = Pattern.compile(regex).matcher(string);
		matches = match.find();
		if (!matches)
			return;
		groups = new String[match.groupCount()];
		for (int i = 0; i < match.groupCount(); i++) {
			groups[i] = match.group(i);
		}
	}

	public String getGroup(int group) {
		return groups[group];
	}

	public boolean matches() {
		return this.matches;
	}
	
	public String[] getGroups() {
		return groups;
	}
}