package org.l3eta.tt.manager;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.l3eta.tt.Bot;

public class BotManager extends JFrame {
	private static final long serialVersionUID = 2666644139929830374L;
	private JPanel contentPane;
	private JTabbedPane bots;

	public static void main(String[] args) {
		new BotManager().setVisible(true);
	}

	public BotManager() {
		setTitle("Bot Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 519, 381);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				for (int b = 0; b < bots.getTabCount(); b++) {
					((BotWindow) bots.getTabComponentAt(b)).close();
				}
			}
		});
		JMenuItem openBot = new JMenuItem("Open Bot");
		mnNewMenu.add(openBot);
		openBot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// You'll need a file selection window
				// Then just do addBot(loadClass(selectedFile));
			}
		});

		JMenuItem mntmCloseBot = new JMenuItem("Close Bot");
		mnNewMenu.add(mntmCloseBot);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnNewMenu.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		bots = new JTabbedPane(JTabbedPane.TOP);
		bots.setBackground(Color.BLACK);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(bots,
				GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(bots, GroupLayout.DEFAULT_SIZE,
				312, Short.MAX_VALUE));
		contentPane.setLayout(gl_contentPane);
	}

	public Bot loadClass(File file) {
		try {
			URL url = new URL("file://" + file.getPath().replace(file.getName(), ""));
			ClassLoader cl = new URLClassLoader(new URL[] { url });
			System.out.println(url);
			return (Bot) cl.loadClass(file.getName().replace(".class", "")).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addBot(Bot bot) {	
		bots.addTab("New Bot", null, new BotWindow(bot), null);
	}
}
