package org.l3eta.turntable.util.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.l3eta.turntable.tt.Commands;
import org.l3eta.turntable.util.Line;

public class Client {
	private Commands handler;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	
	public Client(int sleep) {
		this(sleep, "127.0.0.1", 23);
	}
	
	public Client(int sleep, String addr, int port) {
		try {
			Thread.sleep(sleep);
			Socket sock = new Socket(addr, port);
			in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					sock.getOutputStream()));			
			new Sender(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void start(Commands handler) {		
		try {
			this.handler = handler;
			String line;
			System.out.println("Started Bot!");
			while ((line = in.readLine()) != null) {
				handleLine(new Line(line));				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String text) {
		try {
			out.write(text + "\n");
			out.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void handleLine(Line line) {
		if (isCommand(line)) {
			handler.handleCommand(getCommand(line), line);
		} else {
			handler.handleCommand("other", line);
		}
	}

	private String getCommand(Line line) {
		return line.getString("command");
	}

	private boolean isCommand(Line line) {
		try {
			return line.substring(1, line.indexOf(":")).equals("command");
		} catch (Exception ex) {
		}
		return false;
	}

	public void ping() {
		this.write("ping: Future");
	}
}
