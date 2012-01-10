package org.l3eta.turntable.util.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.l3eta.turntable.tt.User;

public class WebServer {
	private String server = "127.0.0.1";
	private int port = 24;
	private Socket socket;
	@SuppressWarnings("unused")
	private BufferedReader in;
	private BufferedWriter out;

	public WebServer() {
		try {
			socket = new Socket(server, port);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String data) {
		try {
			out.write(data + "\n");
			out.flush();
		} catch (Exception ex) {
		}
	}

	public void sendChat(String text, User user) {
		Object[] _data = { user.getName(), user.getRank().toString(), text };
		String data = String.format("{ \"api\": \"chat\", \"name\": \"%s\", \"rank\": \"%s\", \"text\": \"%s\" }", _data);		
		write(data);
	}
}
