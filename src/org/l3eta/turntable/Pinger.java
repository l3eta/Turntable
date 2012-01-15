package org.l3eta.turntable;

public class Pinger extends Thread {
	private int failed = 0;

	// private Client client;

	public void received() {
		failed = 0;
	}

	public void run() {
		do {
			if (failed >= 3) {

			}

		} while (true);
	}

	public static void main(String[] args) {
		System.out.println("≈ßøøze Cruise≈");
	}
}
