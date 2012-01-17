package org.l3eta.turntable.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.Document;

import org.l3eta.turntable.tt.Bot;

public class BotManager extends JFrame {
	private static final long serialVersionUID = 1L;
	private Image icon;
	private JPanel contentPane;
	private JDesktopPane desktopPane;
	private ArrayList<Bot> bots = new ArrayList<Bot>();
	private JCheckBoxMenuItem settingsItem;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BotManager frame = new BotManager();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void killAll() {
		for (int i = 0; i < bots.size(); i++) {
			bots.get(i).close();
		}
	}

	public BotManager() {
		try {
			icon = ImageIO.read(new File("./images/", "icon.png"));
			TrayIcon tray = new TrayIcon(icon);
			tray.setImageAutoSize(true);
			tray.setToolTip("Turntable Bot Manager");
			tray.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent arg0) {
					if (!isVisible()) {
						setVisible(true);
						setState(0);
					}
				}
			});
			SystemTray.getSystemTray().add(tray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setIconImage(icon);
		setTitle("Bot Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				killAll();
			}
		});
		addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				System.out.println(e.getNewState());
				int n = e.getNewState();
				if ((n == 1 || n == 7) && settingsItem.isSelected()) {
					setVisible(false);
				}
			}
		});
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		JMenuItem mntmNewConsole = new JMenuItem("New Bot");
		mntmNewConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new FileDialog().setVisible(true);
			}
		});
		mnNewMenu.add(mntmNewConsole);

		settingsItem = new JCheckBoxMenuItem("Minimize to tray");
		settingsItem.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				setTray();
			}			
		});
		mnNewMenu.add(settingsItem);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnNewMenu.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		desktopPane = new JDesktopPane();
		contentPane.add(desktopPane, BorderLayout.CENTER);
	}

	public void setTray() {
		settingsItem.setSelected(!settingsItem.isSelected());
	}

	public int showDialog(String msg, String title) {
		return JOptionPane.showConfirmDialog(null, msg, title, 0);
	}

	public void loadBot(Bot bot) {
		BotWindow window = new BotWindow(bot.getName(), bot);
		bot.setBotWindow(window);
		bots.add(bot);
		window.setBounds(53, 11, 316, 189);
		desktopPane.add(window);
	}

	public class BotWindow extends JInternalFrame {
		private static final long serialVersionUID = 682783358630156701L;
		private JTextField textField;
		private JTextPane textPane;
		private JScrollPane scrollPane;
		private Bot bot;

		public BotWindow(String title, Bot bot) {
			super(title);
			this.bot = bot;

			setClosable(true);
			setResizable(true);
			setIconifiable(true);
			setMaximizable(true);
			textField = new JTextField();
			textField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent ze) {
					if (ze.getKeyCode() == KeyEvent.VK_ENTER) {
						handleCommand(textField.getText());
						textField.setText("");
					}
				}
			});
			textField.setForeground(Color.WHITE);
			textField.setBackground(Color.BLACK);
			getContentPane().add(textField, BorderLayout.SOUTH);
			textField.setColumns(10);

			scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			textPane = new JTextPane();
			textPane.setForeground(Color.WHITE);
			textPane.setBackground(Color.BLACK);
			scrollPane.setViewportView(textPane);
			setVisible(true);
			bot.init();
		}

		private void handleCommand(String command) {
			if (command.equals("end")) {
				close(false);
			} else if (command.equals("restart")) {
				close(true);
			} else if (command.startsWith("ban")) {
				bot.getRoom().Users.banUser(command.substring(4), "Banned");
				bot.bootUser(bot.getRoom().Users
						.getByName(command.substring(4)).getUserID(), "Banned");
			}
		}

		private void close(boolean restart) {
			bot.close();
			if (!restart) {
				dispose();
				bots.remove(bot);
				return;
			}
			// TODO handle restart
		}

		public void addToConsole(String data) {
			try {
				Document doc = textPane.getDocument();
				doc.insertString(doc.getLength(), data + "\n", null);
				textPane.select(doc.getLength() - 1, doc.getLength() - 1);
			} catch (Exception ex) {

			}
		}
	}

	public class FileDialog extends JFrame {
		private static final long serialVersionUID = 1268888563939797628L;
		private String path = System.getProperty("user.home")
				.replace("\\", "/");
		private File botFile;
		private JPanel contentPane;
		private JTextField textPath;
		private JTable FileView;
		private DefaultTableModel model;

		public FileDialog() {
			setIconImage(icon);
			setTitle("Bot Loader");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 450, 300);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPane.setLayout(new BorderLayout(0, 0));
			setContentPane(contentPane);

			JScrollPane scrollPane = new JScrollPane();
			contentPane.add(scrollPane, BorderLayout.CENTER);
			model = new DefaultTableModel(new Object[][] {}, new String[] { "",
					"Name" });
			FileView = new FileTable();
			scrollPane.setViewportView(FileView);

			JPanel panel = new JPanel();
			contentPane.add(panel, BorderLayout.SOUTH);
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

			JButton btnBack = new JButton("Back");
			btnBack.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					goUp();
				}
			});
			panel.add(btnBack);

			textPath = new JTextField();
			textPath.setBackground(Color.WHITE);
			textPath.setEditable(false);
			panel.add(textPath);
			textPath.setColumns(10);
			updateList(path);
			JButton load = new JButton("Load Bot");
			load.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					handleLoad();
				}
			});
			panel.add(load);
		}

		private void goUp() {
			String[] paths = path.split("/");
			this.updateList(cut(paths, paths.length - 1, "/"));
		}

		private void updateList(String path) {
			File[] files = new File(path).listFiles();
			this.path = path;
			if (files != null)
				((FileTable) FileView).addFiles(files);
		}

		private void handleLoad() {
			boolean loaded = false;
			Bot bot = null;
			Object o = forFile(botFile);
			if (o instanceof Bot) {
				bot = (Bot) o;
				String msg = String.format(
						"Are you sure you want to load '%s'?", bot.getName());
				loaded = showDialog(msg, "Are you sure?") == 0;
			}
			if (loaded && bot != null) {
				loadBot(bot);
				this.dispose();
			}
		}

		public String cut(String[] args, int end, String separator) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < end; i++) {
				builder.append(args[i] + separator);
			}
			return builder.toString();
		}

		private Object forFile(File file) {
			Object botClass = null;
			try {
				URL url = new URL("file://"
						+ file.getPath().replace(file.getName(), ""));
				ClassLoader cl = new URLClassLoader(new URL[] { url });
				botClass = cl.loadClass(file.getName().replace(".class", ""))
						.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return botClass;
		}

		public class FileTable extends JTable {
			private static final long serialVersionUID = 4125757090768378389L;
			private Icon folder_icon = new ImageIcon("./images/folder.png");
			private Icon file_icon = new ImageIcon("./images/file.png");
			private ArrayList<File> list = new ArrayList<File>();
			private String prev = "", curr = "";

			public FileTable() {
				super(model);
				JTableHeader header = this.getTableHeader();
				header.setResizingAllowed(false);
				header.setReorderingAllowed(false);
				this.setAutoResizeMode(3);
				this.getColumnModel().getColumn(0).setMaxWidth(20);
				this.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent arg0) {
						mouseClick();
					}
				});
			}

			public boolean isCellEditable(int row, int column) {
				return false;
			}

			public void mouseClick() {
				if (this.getSelectedColumn() == 0)
					return;
				prev = curr;
				curr = (String) model.getValueAt(getSelectedRow(), 1);
				boolean isDir = model.getValueAt(getSelectedRow(), 0) == folder_icon;
				if (curr.equals(prev)) {
					if (isDir) {
						updateList(path + "/"
								+ curr.substring(0, curr.length() - 1));
					} else {
						botFile = getFile(getSelectedRow());
						textPath.setText(botFile.getAbsolutePath());
					}
				}
			}

			public void addFiles(File[] files) {
				clearList();
				for (File file : files) {
					if (file.isDirectory() || file.getName().endsWith(".class")) {
						if (!file.isHidden()) {
							addFile(file);
						}
					}
				}
			}

			private void clearList() {
				for (int i = 0; i < list.size(); i++) {
					model.removeRow(0);
				}
				list.clear();
			}

			private File getFile(int index) {
				return list.get(index);
			}

			private void addFile(File o) {
				this.addFile(getIcon(o), o);
			}

			public void addFile(Icon icon, File o) {
				boolean dir = o.isDirectory();
				String name = o.getName();
				if (!list.contains(o)) {
					name = dir ? name + "/" : name;
					list.add(o);
					model.addRow(new Object[] { getIcon(o), name });
				}
			}

			@Override
			public Class<?> getColumnClass(int index) {
				return index == 0 ? Icon.class : String.class;
			}

			public Icon getIcon(File file) {
				return file.isFile() ? file_icon : folder_icon;
			}
		}
	}
}
