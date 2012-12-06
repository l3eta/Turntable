package org.l3eta.tt.manager.tabs;

import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import org.l3eta.gui.GeneratedLayout;
import org.l3eta.tt.Bot;
import org.l3eta.tt.Enums.Status;
import org.l3eta.tt.Room.RoomData;
import org.l3eta.tt.User;
import org.l3eta.tt.manager.BotWindow;
import org.l3eta.tt.task.RepeatingTask;
import org.l3eta.tt.util.DirectoryGraph;
import org.l3eta.tt.util.DirectoryGraph.DirectoryGraphCallback;

public class PrivateMessaging extends JPanel {
	private static final long serialVersionUID = -6390956194578846693L;
	private Map<String, BuddyPanel> buddies;
	private RepeatingTask buddyTask;
	private GeneratedLayout friendsLayout;
	private BotWindow window;
	private JTabbedPane chats;

	public PrivateMessaging(BotWindow window) {
		this.window = window;
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		chats = new JTabbedPane(JTabbedPane.BOTTOM);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE).addGap(18)
						.addComponent(chats).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout.createParallelGroup(Alignment.TRAILING)
										.addComponent(chats, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
										.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
						.addContainerGap()));
		JPanel friends = new JPanel();
		scrollPane.setViewportView(friends);
		friendsLayout = new GeneratedLayout(friends);
		setLayout(groupLayout);
		awaitBotLoad(); //TODO put this after debugging is done
	}

	public void awaitBotLoad() {
		new Thread() {
			public void run() {
				Bot bot = PrivateMessaging.this.window.getBot();
				while (!bot.isLoaded()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				startTask();
			}
		};
	}
	
	private void startTask() {
		buddyTask = new RepeatingTask(new Runnable() {
			public void run() {
				window.getBot().getGraph(new DirectoryGraphCallback() {
					public void run(DirectoryGraph graph) {
						for(RoomData room : graph.getRooms()) {
							String room_name = room.getName();
							for(User user : room.getUsers()) {
								//This is slightly done all that is needed is to have it add to the user list
							}
						}
					}
				});
			}
		}, 60000);
		buddyTask.start();		
	}
	
	public void update(User user, String room) {
		
	}

	public void add() {
		User user = new User();
		user.setName("01234567890123456789");
		user.setStatus(Status.AVAILABLE); //This is testing so make sure it worked.
		for (int i = 0; i < 101; i++) {
			friendsLayout.addSingle(new BuddyPanel(user), 170, 39);
		}
	}

	public void add(BuddyPanel bp) {
		String id = bp.getUser().getID();
		if (!buddies.containsKey(id)) {
			buddies.put(id, bp);
			friendsLayout.addSingle(bp, 170, 39);
		}
	}
}
