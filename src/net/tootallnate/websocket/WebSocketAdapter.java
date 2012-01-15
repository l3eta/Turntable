package net.tootallnate.websocket;

import java.io.IOException;

public class WebSocketAdapter implements WebSocketListener {

	@Override
	public HandshakeBuilder onHandshakeRecievedAsServer(WebSocket conn,
			Draft draft, Handshakedata request) throws IOException {
		return new HandshakedataImpl1();
	}

	@Override
	public boolean onHandshakeRecievedAsClient(WebSocket conn,
			Handshakedata request, Handshakedata response) throws IOException {
		return true;
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
	}

	@Override
	public void onError(Throwable ex) {
		if (WebSocket.DEBUG)
			ex.printStackTrace();
	}

	@Override
	public void onMessage(WebSocket conn, byte[] blob) {
	}

	@Override
	public void onPong() {
	}

	@Override
	public String getFlashPolicy(WebSocket conn) {
		return "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\""
				+ conn.getPort() + "\" /></cross-domain-policy>\0";
	}

}
