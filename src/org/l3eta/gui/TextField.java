package org.l3eta.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;

import javax.swing.JTextField;

public class TextField extends JTextField {
	private static final long serialVersionUID = -47138228535898460L;
	private boolean showPlaceholder;
	private Color cPlaceHolder = Color.LIGHT_GRAY;
	private String placeHolder = "";

	// private final int x = KeyEvent.VK_X, control = KeyEvent.VK_CONTROL;

	public TextField() {
		addKeyListener(keyboard);
		showPlaceholder = true;
	}

	private KeyAdapter keyboard = new KeyAdapter() {
		// private boolean cut, fCut;

		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			final String text = getText(); 
			switch (key) {
				case VK_BACK_SPACE:
				case VK_DELETE:
					if (text.length() == 1)
						showPlaceholder = true;
					break;
				/*
				 * case x: cut = e.isControlDown(); fCut = false; break;
				 */
				
				default:
					if (text.length() != 1)
						showPlaceholder = false;
					break;
			}
		}

		/*
		 * public void keyReleased(KeyEvent e) { int k = e.getKeyCode(); if (k
		 * == x || k == control && cut) { if (!fCut) { if (getText().length() ==
		 * 0) showPlaceholder = true; System.out.println("Derp: " +
		 * getText().length()); fCut = true; } else { cut = false; } } }
		 */
	};

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}	
	
	public void showPlaceHolder() {
		showPlaceholder = true;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (showPlaceholder) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(cPlaceHolder);
			g2d.drawString(placeHolder, 2, 15);
		}
	}

}
