package org.l3eta.turntable.util.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public abstract class TCPServer {
	ServerSocket server;
	private boolean listening = false;
	private ArrayList<Socket> sockets = new ArrayList<Socket>();

	public void listen(int port) {
		try {
			server = new ServerSocket(port);
			listening = true;
			while (listening) {
				new TCPClient(server.accept()).start();
			}
		} catch (Exception ex) {
			if (!ex.getMessage().equals("socket closed") && !server.isClosed())
				ex.printStackTrace();
		}
	}

	public class TCPClient extends Thread {
		Socket socket;

		public TCPClient(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			sockets.add(socket);
			onConnect(socket);
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					if (!listening)
						return;
					onMessage(socket, line);
				}
			} catch (Exception ex) {
				if(!ex.getMessage().equals("socket closed"))
				ex.printStackTrace();
			}
			sockets.remove(getIndex(socket));
			onClose(socket);
		}
	}

	public int getIndex(Socket socket) {
		for (int i = 0; i < sockets.size(); i++) {
			Socket e = sockets.get(i);
			if (e == socket)
				return i;
		}
		return -1;
	}

	public void close() {
		try {
			this.listening = false;
			for (Socket e : sockets.toArray(new Socket[0])) {
				writeTo(e, "Server Closing..");
				e.close();
			}
			server.close();
			System.out.println("Server Closed");
		} catch (IOException e) {
			if (!e.getMessage().equals("socket closed"))
				e.printStackTrace();
		}
	}

	public void writeTo(Socket socket, String data) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			out.write(data + "\n");
			out.flush();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	public abstract void onMessage(Socket socket, String line);

	public abstract void onConnect(Socket socket);

	public abstract void onClose(Socket socket);
}
