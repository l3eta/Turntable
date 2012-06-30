package org.l3eta.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.awt.AWTUtilities;

//TODO make this match components | remove it.
public abstract class Overlay extends JDialog {
	private static final long serialVersionUID = -3150682958750551572L;
	private int width;
	private Color shadow = new Color(0, 0, 0, 150);
	private Color trans = new Color(0, 0, 0, 0);

	private WindowAdapter opwl = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			e.getWindow().removeWindowListener(this);
			dispose();
			System.exit(0);
		}

		public void windowDeiconified(WindowEvent e) {
			setVisible(true);
		}

		public void windowIconified(WindowEvent e) {
			setVisible(false);
		}
	};

	public WindowFocusListener opfl = new WindowFocusListener() {
		public void windowGainedFocus(WindowEvent e) {
			Overlay.this.toFront();
		}

		public void windowLostFocus(WindowEvent e) {			
		}

	};

	private ComponentListener opcl = new ComponentAdapter() {
		public void componentMoved(ComponentEvent e) {
			setOverlayBounds((JFrame) e.getComponent());
		}

		public void componentResized(ComponentEvent e) {
			setOverlayBounds((JFrame) e.getComponent());
		}
	};

	public Overlay(int width) {
		this.width = width;
		setUndecorated(true);
		AWTUtilities.setWindowOpaque(this, false);
		getRootPane().setBackground(trans);
		setBackground(trans);
	}
	
	protected void init() {
		JPanel content = new JPanel();		
		content.setLayout(new BorderLayout(0, 0));
		
		int lrGap = ((getBounds().width - width) / 4);
		JPanel leftShadow = new JPanel();
		leftShadow.setBackground(shadow);
		((FlowLayout) leftShadow.getLayout()).setHgap(lrGap);
		content.add(leftShadow, BorderLayout.WEST);

		JPanel rightShadow = new JPanel();
		rightShadow.setBackground(shadow);
		((FlowLayout) rightShadow.getLayout()).setHgap(lrGap);
		content.add(rightShadow, BorderLayout.EAST);

		JPanel overlayContent = new JPanel();
		content.add(overlayContent, BorderLayout.CENTER);
		initGui();
		setContentPane(content);		
	}

	private void setOverlayBounds(JFrame parent) {
		Rectangle bounds = parent.getBounds();
		Insets in = parent.getInsets();
		bounds.x = bounds.x + in.left;
		bounds.y = bounds.y + in.top;
		bounds.width = bounds.width - (in.left + in.right);
		bounds.height = bounds.height - (in.top + in.bottom);
		setBounds(bounds);
	}
	
	public abstract void initGui();
	
	public void show(Component c, int width) {		
		if(c instanceof Window) {
			Window w = (Window) c;
			w.addWindowListener(opwl);
			w.addComponentListener(opcl);
			w.addWindowFocusListener(opfl);
			setVisible(true);
		}	
	}
}
