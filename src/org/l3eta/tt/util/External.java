package org.l3eta.tt.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.l3eta.tt.User;

import com.mongodb.BasicDBList;

public final class External {
	private static User externalUser;
	private static String externalChatServer;

	public static void runAPI(ExternalAPICallback callback, String api,
			String... params) {
		Message json = null;
		String data = null;
		boolean s = false;
		try {
			URL url = new URL("http://turntable.fm/api/" + api + "?"
					+ toRequest(params));
			data = new BufferedReader(new InputStreamReader(url.openStream()))
					.readLine();
			s = data.startsWith("[true,");
			json = new Message(data, 1);
		} catch (Exception ex) {
			ex.printStackTrace();
			json = new Message();
			data = "null";
			s = false;
		}
		callback.run(json, s);
	}

	// Custom Calls

	public static User getUser(String name) {
		externalUser = new User();
		ExternalAPICallback callback = new ExternalAPICallback() {
			public void run(Message json, boolean success) {
				if (success) {
					ExternalAPICallback uiCallback = new ExternalAPICallback() {
						public void run(Message json, boolean success) {
							if (success) {
								externalUser = new User(json);
							}
						}
					};
					getUserInfo(uiCallback, json.getString("userid"));
				}
			}
		};
		getUserID(callback, name);
		return externalUser;
	}

	public static String getChatServer(String roomid) {
		externalChatServer = null;
		ExternalAPICallback callback = new ExternalAPICallback() {
			public void run(Message json, boolean success) {
				if (success) {
					externalChatServer = ((BasicDBList) json.get("chatserver"))
							.get(0).toString();
				}
			}
		};
		runAPI(callback, "room.which_chatserver", "roomid", roomid);
		return externalChatServer;
	}

	// Default calls

	public static void getUserID(ExternalAPICallback callback, String name) {
		runAPI(callback, "user.get_id", "name", name);
	}

	public static void getUserInfo(ExternalAPICallback callback, String userid) {
		runAPI(callback, "user.info", "userid", userid);
	}

	private static String toRequest(String... values) {
		StringBuilder request = new StringBuilder();
		for (int i = 0; i < values.length; i += 2) {
			request.append((i == 0 ? "" : "&") + values[i] + "="
					+ values[i + 1]);
		}
		return request.toString();
	}

	public static interface ExternalAPICallback {
		public void run(Message json, boolean successful);
	}
}
