package org.l3eta.tt.user;

public class Rank {
	public static final Rank USER = new Rank(0);
	public static final Rank MOD = new Rank(1);
	public static final Rank ADMIN = new Rank(2);
	public static final Rank OWNER = new Rank(3);
	
	private int rank;
	
	private Rank(int rank) {
		this.rank = rank;
	}
	
	public boolean compare(Rank rank) {
		return this.rank >= rank.rank;
	}
}
