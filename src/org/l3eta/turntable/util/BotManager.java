package org.l3eta.turntable.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import org.l3eta.turntable.tt.Bot;

public class BotManager extends JFrame {
	private Image icon;
	private JPanel contentPane;
	private boolean hasLoader = false;
	private HashMap<BotWindow, Bot> bots = new HashMap<BotWindow, Bot>();
	
	
	/**
	 * Launch the application.
	 */
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

	
	public String getNode() {
		String home = System.getProperty("user.home");
		
		
			
		
		return home;
	}
	/**
	 * Create the frame.
	 */
	public BotManager() {
		try {
			icon = ImageIO.read(new File("./images/", "icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setIconImage(icon);
		setTitle("BotManager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewConsole = new JMenuItem("New Bot");
		mntmNewConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!hasLoader) {
					new FileDialog().setVisible(true);
					hasLoader = true;
				}				
			}
		});
		mnNewMenu.add(mntmNewConsole);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnNewMenu.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JDesktopPane desktopPane = new JDesktopPane();
		contentPane.add(desktopPane, BorderLayout.CENTER);

		JInternalFrame internalFrame = new BotWindow("New Bot");
		internalFrame.setBounds(53, 11, 316, 189);
		desktopPane.add(internalFrame);
	}
	
	public void loadBot(Bot bot) {
		
	}

	public class BotWindow extends JInternalFrame {
		private static final long serialVersionUID = 682783358630156701L;
		private JTextField textField;
		private JTextPane textPane;

		public BotWindow(String title) {
			super(title);
			setClosable(true);
			setResizable(true);
			setIconifiable(true);
			setMaximizable(true);
			textField = new JTextField();
			textField.setForeground(Color.WHITE);
			textField.setBackground(Color.BLACK);
			getContentPane().add(textField, BorderLayout.SOUTH);
			textField.setColumns(10);

			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			textPane = new JTextPane();
			textPane.setForeground(Color.WHITE);
			textPane.setBackground(Color.BLACK);
			scrollPane.setViewportView(textPane);
			setVisible(true);
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
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
			try {
				Class<?> c = Class.forName(path);
				Object bot = c.newInstance();
				if (bot instanceof Bot) {
					Bot b = (Bot) bot;
					String msg = String
							.format("Are you sure you want to load the bot for room '%s'",
									b.getRoom().getName());
					int i = JOptionPane.showConfirmDialog(null, msg,
							"Are you sure?", 0);
					loaded = i == 0;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (loaded) {
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
						System.out
								.println(((Bot) forFile(getFile(getSelectedRow())))
										.getRoom().getName());
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

			private Object forFile(File file) {
				Object botClass = null;
				try {
					URL url = new URL("file://"
							+ file.getPath().replace(file.getName(), ""));
					URL[] urls = new URL[] { url };
					ClassLoader cl = new URLClassLoader(urls);
					System.out.println(url);
					botClass = cl.loadClass(
							file.getName().replace(".class", "")).newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return botClass;
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
