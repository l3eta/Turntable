package org.l3eta.tt.util;

import static com.mongodb.util.JSON.parse;

import java.util.ArrayList;

import org.bson.BasicBSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class Message extends BasicBSONObject {
	private static final long serialVersionUID = -7803141080791022213L;

	public Message() {
		super();
	}

	public Message(Object o) {
		super(((BasicBSONObject) o).toMap());
	}

	public Message(String json) {
		this((BasicDBObject) parse(json.substring(json.indexOf("{"))));
	}

	public Message(String json, int cutoff) {
		this(json.substring(0, json.length() - cutoff));
	}

	public BasicDBList getList(String name) {
		return (BasicDBList) get(name);
	}

	public BasicDBList getSubList(String name) {
		return getSubList(get(name));
	}

	public BasicDBList getSubList(Object list) {
		return getSubList(list, 0);
	}

	public BasicDBList getSubList(Object list, int index) {
		return (BasicDBList) ((BasicDBList) list).get(index);
	}

	public Message getSubObject(String... subs) {
		Message temp = this;
		for (String sub : subs) {
			temp = new Message(temp.get(sub));
		}
		return temp;
	}

	public Message[] getMessageList(String name) {
		ArrayList<Message> message = new ArrayList<Message>();
		for (BasicDBObject msg : getList(name).toArray(new BasicDBObject[0])) {
			message.add(new Message(msg));
		}
		return message.toArray(new Message[0]);
	}

	public String[] getStringList(String name) {
		return getList(name).toArray(new String[0]);
	}

	public Message append(String key, Object value) {
		return (Message) super.append(key, value);
	}

	public boolean has(String field) {
		return this.containsField(field);
	}

	public int length() {
		return toString().length();
	}

	// public BasicDBObject getSub
}
