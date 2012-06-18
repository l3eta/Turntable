package org.l3eta.tt.util;

public class BotMessage {
	private int msgid;
	private Message json;
	private MessageCallback callback;

	public BotMessage(int msgid, Message json, MessageCallback callback) {
		this.msgid = msgid;
		this.json = json;
		this.callback = callback;
	}

	public String getAPI() {
		return json.getString("api");
	}

	public int getMsgID() {
		return msgid;
	}

	public Message getMessage() {
		return json;
	}

	public void runCallback(Message message) {
		callback.run(message);
	}

	public boolean hasCallback() {
		return callback != null;
	}

	public interface MessageCallback {
		public void run(Message message);
	}
}
