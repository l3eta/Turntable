package org.l3eta.tt.user;

import java.util.HashMap;
import java.util.Map;

public class Rank {
	private static Map<String, Rank> ranks;

	public static final Rank USER = create("user", 0);
	public static final Rank MOD = create("mod", 1);
	public static final Rank ADMIN = create("admin", 2);
	public static final Rank OWNER = create("owner", 3);

	private int rank;
	private String name;

	private Rank(String name, int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}

	public String getName() {
		return name;
	}

	public boolean equals(Object o) {
		if (o instanceof Rank)
			return compare((Rank) o);
		return false;
	}

	public boolean compare(Rank rank) {
		return this.rank >= rank.rank;
	}

	public static Rank get(String name) {
		checkMap();
		if (ranks.containsKey(name)) {
			return ranks.get(name);
		}
		return null;
	}

	private static void checkMap() {
		if (ranks == null)
			ranks = new HashMap<String, Rank>();
	}

	public static Rank create(String name, int rank) {
		checkMap();
		if (get(name) != null)
			return get(name);
		Rank r = new Rank(name, rank);
		ranks.put(name, r);
		return r;
	}
}
