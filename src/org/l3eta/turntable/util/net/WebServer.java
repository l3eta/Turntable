package org.l3eta.turntable.util.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class WebServer {
	private Socket socket;
	@SuppressWarnings("unused")
	private BufferedReader in;
	private BufferedWriter out;
	
	public WebServer() {
		try {
			socket = new Socket("127.0.0.1", 24);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String data) {
		try {
			out.write(data + "\n");
			out.flush();
		} catch(Exception ex) {			
		}
	}
}
