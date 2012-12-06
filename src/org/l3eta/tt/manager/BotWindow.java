package org.l3eta.tt.manager;

import java.io.InputStream;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.l3eta.tt.Bot;
import org.l3eta.tt.Enums.LogLevel;

public class BotWindow extends JPanel {
	private static final long serialVersionUID = 4857881216782181279L;
	private Bot bot;

	public BotWindow(Bot bot) {
		bot.setWindow(this);
		this.bot = bot;
		JTabbedPane botTabs = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addContainerGap().addComponent(botTabs, GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addContainerGap().addComponent(botTabs, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
						.addContainerGap()));
		setLayout(groupLayout);
	}

	public static InputStream getStream(String file) {
		return BotWindow.class.getResourceAsStream(file);
	}

	public static URL getURL(String file) {
		return BotWindow.class.getResource(file);
	}

	public void log(LogLevel level, String message) {
		// TODO
	}

	public void close() {
		if (!bot.isClosed()) {
			bot.close();
		}

	}

	public Bot getBot() {
		return bot;
	}
}
