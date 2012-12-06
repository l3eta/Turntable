package org.l3eta.tt.manager.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.l3eta.gui.TextField;

public class ChatTab extends JPanel {
	private static final long serialVersionUID = -3900070887566184661L;
	private Color PM_RED = new Color(0xC62020);
	private Color PM_DEFAULT = new Color(0x282828);

	private JPanel title, chatPanel;
	private TextField message;
	private JTextPane chat;
	private JTextField error;

	public ChatTab() {
		message = new TextField();
		message.setPlaceHolder("enter a message");
		message.setColumns(10);
		message.addKeyListener(keyboard);

		chatPanel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				Alignment.LEADING,
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(chatPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
										.addComponent(message, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addContainerGap()
						.addComponent(chatPanel, GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(message, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		chatPanel.setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		chat = new JTextPane();
		scrollPane.setViewportView(chat);
		chat.setEditable(false);
		chat.setText("");
		error = new JTextField();
		error.setVisible(false);
		error.setEditable(false);
		title = new JPanel();
		title.setVisible(false);
		chatPanel.add(title, BorderLayout.NORTH);
		chatPanel.add(error, BorderLayout.SOUTH);
		chatPanel.add(scrollPane, BorderLayout.CENTER);
		setLayout(groupLayout);
		addColorStyle("red", PM_RED);
		addColorStyle("default", PM_DEFAULT);
	}

	public void addColorStyle(String name, Color color) {
		StyledDocument d = chat.getStyledDocument();
		Style style = d.addStyle(name, null);
		StyleConstants.setForeground(style, color);
	}

	public void setError(String err) {
		if (err.equals("") && error.isVisible()) {
			error.setVisible(false);
		} else {
			error.setText(err);
			error.setVisible(true);
		}
	}

	public void enableMessage(boolean on) {
		message.setEnabled(false);
	}

	private KeyAdapter keyboard = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			String text = message.getText();
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (text.trim().equals("")) {
					return;
				}
				addText(text.trim(), "red");
				message.setText("");
				message.showPlaceHolder();
				// TODO add in send pm callback
			}
		}
	};

	public void addText(String text) {
		addText(text, "default");
	}

	public void addText(String text, String style) {
		try {
			StyledDocument d = chat.getStyledDocument();
			d.insertString(d.getLength(), text + "\r\n", d.getStyle(style));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
