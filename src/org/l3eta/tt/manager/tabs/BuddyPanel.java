package org.l3eta.tt.manager.tabs;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.l3eta.tt.Enums.Status;
import org.l3eta.tt.User;
import org.l3eta.tt.images.Texture;
import org.l3eta.tt.images.Texture.TextureType;

public class BuddyPanel extends JPanel {
	private String pmImagesURL = "https://s3.amazonaws.com/static.turntable.fm/images/pm/status_indicators_flat.png";
	private static final long serialVersionUID = 766189015161232857L;
	private JLabel name, status, room;
	private Status userStatus;
	private User user;
	
	
	public BuddyPanel() {		
		this(getDefault());
	}
	
	private static User getDefault() {
		User user = new User();
		user.setName("l3eta");
		user.setStatus(Status.AVAILABLE);
		return user;
	}

	public BuddyPanel(User user) {
		setLayout(null);
		userStatus = user.getStatus();
		add(status = new JLabel(Texture.get(pmImagesURL, TextureType.URL).getSubIcon(userStatus.getLocation())));
		add(name = new JLabel(user.getName()));		
		add(room = new JLabel());
		name.setForeground(new Color(0x282828));
		room.setForeground(Color.gray);
		name.setFont(new Font("SansSerif", Font.PLAIN, 12));
		room.setFont(new Font("SansSerif", Font.PLAIN, 11));
		status.setBounds(4, 25 / 2, 9, 14);		
		name.setBounds(23, 5, 134, 15);
		room.setBounds(23, 20, 134, 14);
	}
	
	private void updateStatusIcon() {
		status.setIcon(Texture.get(pmImagesURL, TextureType.URL).getSubIcon(userStatus.getLocation()));
	}
	
	public Status getUserStatus() {
		return userStatus;
	}
	
	public void setUserStatus(Status status) {
		if(userStatus == status)
			return;
		userStatus = status;
		updateStatusIcon();
	}
	
	public void setRoom(String name) {
		room.setText(name);
	}
	
	public User getUser() {
		return user;
	}	
}
