package org.l3eta.tt.ws;

import java.net.URI;
import java.util.HashMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.jboss.netty.util.CharsetUtil;

public class WebSocketHandler extends SimpleChannelUpstreamHandler {

	private WebSocketClientHandshaker handshaker;
	private final WebSocket ws;

	public WebSocketHandler(WebSocket ws) {
		this.ws = ws;
	}

	public void setHandshake(URI uri) {
		handshaker = new WebSocketClientHandshakerFactory().newHandshaker(uri, WebSocketVersion.V13, null, false,
				new HashMap<String, String>());
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		ws.getBot().debug("WebSocket Client disconnected!", 0);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		Channel ch = ctx.getChannel();
		if (!handshaker.isHandshakeComplete()) {
			handshaker.finishHandshake(ch, (HttpResponse) e.getMessage());
			ws.getBot().debug("WebSocket Client connected!", 0);
			return;
		}

		if (e.getMessage() instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) e.getMessage();
			throw new Exception("Unexpected HttpResponse (status=" + response.getStatus() + ", content="
					+ response.getContent().toString(CharsetUtil.UTF_8) + ")");
		}

		WebSocketFrame frame = (WebSocketFrame) e.getMessage();
		if (frame instanceof TextWebSocketFrame) {
			TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
			ws.onMessage(textFrame.getText());
		} else if (frame instanceof PongWebSocketFrame) {
			ws.getBot().debug("WebSocket Client received pong", 0);
		} else if (frame instanceof CloseWebSocketFrame) {
			ws.getBot().info("WebSocket Client received closing");
			ws.close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		ws.close();
	}

	public ChannelFuture handshake(Channel channel) throws Exception {
		return handshaker.handshake(channel);
	}
}