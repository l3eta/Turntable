package net.tootallnate.websocket;

import java.io.IOException;

interface WebSocketListener {

	public HandshakeBuilder onHandshakeRecievedAsServer(WebSocket conn,
			Draft draft, Handshakedata request) throws IOException;

	public boolean onHandshakeRecievedAsClient(WebSocket conn,
			Handshakedata request, Handshakedata response) throws IOException;

	public void onMessage(WebSocket conn, String message);

	public void onMessage(WebSocket conn, byte[] blob);

	public void onError(Throwable ex);

	public void onPong();

	public String getFlashPolicy(WebSocket conn);
}
