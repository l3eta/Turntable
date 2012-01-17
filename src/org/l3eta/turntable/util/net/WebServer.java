package org.l3eta.turntable.util.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public abstract class WebServer {
	// TODO Improve to what ever is needed... I want to do more but i can't
	// think of anything.

	private String root = "./";
	private ServerSocket server;
	private boolean listening = false;
	private ArrayList<Socket> sockets = new ArrayList<Socket>();

	public void listen(int port) {
		try {
			server = new ServerSocket(port);
			listening = true;
			while (listening) {
				new HTTPClient(server.accept()).start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public class HTTPClient extends Thread {
		private Socket socket;
		private BufferedReader in;
		private DataOutputStream output;

		public HTTPClient(Socket socket) {
			sockets.add(socket);
			this.socket = socket;
		}

		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				output = new DataOutputStream(socket.getOutputStream());
				String line, path = root;
				int method = 0;
				while ((line = in.readLine()) != null) {
					String temp = line.toUpperCase();
					if (temp.startsWith("GET")) {
						method = 1;
						String p = line.split(" ")[1];
						path = root + (root.endsWith("/") ? p.substring(1) : p);
					} else if (temp.startsWith("HEAD"))
						method = 2;
					if (method == 0) {
						onError(this, 501);
						return;
					}
					File file = new File(clean(path));
					String[] f = file.getName().split("\\.");
					if (!file.exists()) {
						onError(this, 404);
						return;
					}
					if (method == 1) {
						setHeader(200, "." + f[f.length - 1]);
						writeFile(file);
					}
					return;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void close() {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void writeFile(File file) {
			try {
				FileInputStream req = new FileInputStream(file);
				byte[] buffer = new byte[1024];
				int b = 0;
				while ((b = req.read(buffer, 0, 1024)) != -1) {
					output.write(buffer, 0, b);
				}
				req.close();
				output.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void write(String data) {
			try {
				output.writeBytes(data);
				output.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public String clean(String toClean) {
			toClean = toClean.replace("%20", "");
			return toClean;
		}

		public String setHeader(int status, String file) {
			StringBuilder header = new StringBuilder();
			switch (status) {
				case 200:
					header.append("HTTP/1.1 200 OK\n");
					break;
				case 400:
					header.append("HTTP/1.1 400 Bad Request\n");
					break;
				case 403:
					header.append("HTTP/1.1 403 Forbidden\n");
					break;
				case 404:
					header.append("HTTP/1.1 404 Not Found\n");
					break;
				case 500:
					header.append("HTTP/1.1 500 Internal Server Error\n");
					break;
				case 501:
					header.append("HTTP/1.1 501 Not Implemented\n");
					break;
			}
			header.append("Connection: Close\n");
			header.append("Server: WebServer Turntable API by l3eta\n");
			switch (file) {
				case ".html":
				case ".htm":
				case ".htmls":
				case ".htx":
				case ".shtml":
				case ".acgi":
					header.append("Content-Type: text/html\n");
					break;
				case ".png":
					header.append("Content-Type: image/png\n");
					break;
				case ".gif":
					header.append("Content-Type: image/gif\n");
					break;
				default:
					header.append("Content-Type: text/plain\n");
					break;
			}
			header.append("\n");// TODO Check if needed.
			return header.toString();
		}
	}

	public abstract void onError(HTTPClient client, int error);

	public abstract void onConnect(HTTPClient client);
}
