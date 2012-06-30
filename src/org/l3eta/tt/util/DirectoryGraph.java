package org.l3eta.tt.util;

import java.util.ArrayList;
import java.util.List;

import org.l3eta.tt.Room.RoomData;

import com.mongodb.BasicDBList;

public class DirectoryGraph {
	private int roomCount = 0;
	private List<RoomData> rooms;

	public DirectoryGraph(Message message) {
		rooms = new ArrayList<RoomData>();
		BasicDBList roomList = (BasicDBList) message.get("rooms");
		roomCount = roomList.size();
		for (Object msg : roomList) {
			if (msg instanceof BasicDBList) {
				Message m = getRoom((BasicDBList) msg);
				System.out.println(m);
				rooms.add(new RoomData(m, true));
			}
		}
	}

	private Message getRoom(BasicDBList msg) {
		return new Message().append("room", new Message(msg.get(0))).append("users", msg.get(1));
	}

	public int getRoomCount() {
		return roomCount;
	}

	public interface DirectoryGraphCallback {
		public void run(DirectoryGraph graph);
	}

	public RoomData[] getRooms() {
		return rooms.toArray(new RoomData[0]);
	}
}
