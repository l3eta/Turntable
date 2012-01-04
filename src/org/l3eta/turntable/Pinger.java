package org.l3eta.turntable;

import org.l3eta.turntable.util.net.Client;

public class Pinger extends Thread {
	private int failed = 0;
	//private Client client;
	
	public Pinger(Client client) {
		//this.client = client;
	}
	
	public void received() {
		failed = 0;
	}
	
	public void run() {
		do {
			if(failed >= 3) {
				
			}
			
		} while(true);
	}
}
