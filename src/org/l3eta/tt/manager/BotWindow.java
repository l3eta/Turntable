package org.l3eta.tt.manager;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTabbedPane;

import org.l3eta.tt.Bot;
import org.l3eta.tt.Enums.LogLevel;
import org.l3eta.tt.Song;

import java.io.InputStream;
import java.net.URL;

public class BotWindow extends JPanel {
	private static final long serialVersionUID = 4857881216782181279L;
	private int maxLogs = 300;
	private PlaylistTab playlist;
	private ConsoleTab console;
	private Bot bot;

	public BotWindow(Bot bot) {
		bot.setWindow(this);
		this.bot = bot;
		JTabbedPane botTabs = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(botTabs, GroupLayout.DEFAULT_SIZE, 495,
								Short.MAX_VALUE).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(botTabs, GroupLayout.DEFAULT_SIZE, 392,
								Short.MAX_VALUE).addContainerGap()));

		botTabs.addTab("Console", null, console = new ConsoleTab(), null);
		botTabs.addTab("Playlist", null, playlist = new PlaylistTab(), null);
		setLayout(groupLayout);
	}

	public static InputStream getStream(String file) {
		return BotWindow.class.getResourceAsStream(file);
	}

	public static URL getURL(String file) {
		return BotWindow.class.getResource(file);
	}

	public void log(LogLevel level, String message) {
		
	}

	class PlaylistTab extends JPanel {
		private static final long serialVersionUID = 6674011455285774971L;

		private PlaylistTab() {
			
		}

		public void addSong(Song song) {
			// TODO
		}
	}

	class ConsoleTab extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8357298451120234013L;
	
	}

	public void close() {
		bot.close();
	}
}
