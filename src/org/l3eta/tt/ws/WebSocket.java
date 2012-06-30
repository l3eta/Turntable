package org.l3eta.tt.ws;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.l3eta.tt.Bot;

public abstract class WebSocket extends Thread {
	private Bot bot;
	private WebSocketHandler wsh;
	private Channel channel;
	private ChannelFuture future;
	private ClientBootstrap cbs;

	public WebSocket(Bot bot) {
		this.bot = bot;
	}

	public void run() {
		cbs = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		wsh = new WebSocketHandler(WebSocket.this);
		cbs.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decoder", new HttpResponseDecoder());
				pipeline.addLast("encoder", new HttpRequestEncoder());
				pipeline.addLast("ws-handler", wsh);
				return pipeline;
			}
		});
		connect(getURI());		
	}

	public void connect(URI uri) {
		try {
			wsh.setHandshake(uri);
			future = cbs.connect(new InetSocketAddress(uri.getHost(), uri.getPort()));
			future.awaitUninterruptibly().rethrowIfFailed();
			channel = future.getChannel();
			wsh.handshake(channel).awaitUninterruptibly().rethrowIfFailed();

			channel.getCloseFuture().awaitUninterruptibly();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (channel != null) {
				channel.close();
			}
			cbs.releaseExternalResources();
		}
	}

	private URI getURI() {
		try {
			return new URI("ws://" + bot.getServer() + ":80/socket.io/websocket");
		} catch (Exception e) {
		}
		return null;
	}

	public void send(String text) {
		if (channel.isConnected())
			channel.write(new TextWebSocketFrame(text));
		else
			close(); 
	}

	public abstract void onMessage(String message);

	public void close() {
		//TODO add in normal close;
		try {			
			channel.close();
		}  catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}

	public Bot getBot() {
		return bot;
	}
}
