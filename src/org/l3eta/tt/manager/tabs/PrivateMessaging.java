package org.l3eta.tt.manager.tabs;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.GroupLayout.Alignment;

import org.l3eta.gui.GeneratedLayout;
import org.l3eta.tt.Enums.Status;
import org.l3eta.tt.User;
import org.l3eta.tt.images.Texture;
import org.l3eta.tt.images.Texture.TextureType;
import org.l3eta.tt.manager.BotWindow;

import javax.swing.JTabbedPane;

public class PrivateMessaging extends JPanel {
	private static final long serialVersionUID = -6390956194578846693L;
	private BotWindow window;
	private String pmImagesURL = "https://s3.amazonaws.com/static.turntable.fm/images/pm/status_indicators_flat.png";
	private JPanel friends;
	private JTabbedPane chats;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setContentPane(new PrivateMessaging(null));
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(3);
		frame.setVisible(true);
	}

	public PrivateMessaging(BotWindow window) {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		chats = new JTabbedPane(JTabbedPane.BOTTOM);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE).addGap(18)
						.addComponent(chats, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE).addContainerGap()));
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

		friends = new JPanel();
		scrollPane.setViewportView(friends);
		GeneratedLayout glF = new GeneratedLayout(friends);
		glF.set();
		friends.setLayout(glF);
		setLayout(groupLayout);
		add();
	}

	public void add() {
		User user = new User();
		user.setName("01234567890123456789");
		user.setStatus(Status.AVAILABLE);
		for (int i = 0; i < 101; i++) {
			((GeneratedLayout) friends.getLayout()).addSingle(new BuddyPanel(user), 160, 24);
		}
	}

	class BuddyPanel extends JPanel {
		private static final long serialVersionUID = 766189015161232857L;
		private JLabel name, status;
		private User user;
		private MouseAdapter mouse = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//TODO open new chat window if not
			}
			
			public void mouseEntered(MouseEvent e) {
	            Component component = e.getComponent();
	            if (component instanceof JPanel) {
	            	
	            }
	        }
	 
	        public void mouseExited(MouseEvent e) {
	        	Component component = e.getComponent();
	            if (component instanceof JPanel) {
	            	
	            }
	        }
		};

		public BuddyPanel(User user) {
			add(status = new JLabel(Texture.get(pmImagesURL, TextureType.URL).getSubIcon(user.getStatus().getLocation())));
			add(name = new JLabel(user.getName()));
			addMouseListener(mouse);
		}

		public User getUser() {
			return user;
		}
		
		
	}

	class ChatTab extends JPanel {
		private static final long serialVersionUID = 3168829996758372587L;

		public ChatTab(User user) {
			int index = chats.indexOfTab(user.getName());
			if (index != -1) {
				
			}
		}

	}
}
