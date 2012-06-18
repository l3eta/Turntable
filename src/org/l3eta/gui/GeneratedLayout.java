package org.l3eta.gui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

public class GeneratedLayout extends GroupLayout {
	private Group h;
	private SequentialGroup v;

	public GeneratedLayout(Container c) {
		super(c);
		h = createParallelGroup(Alignment.LEADING);
		v = createSequentialGroup().addContainerGap();
	}

	public void addSingle(Component c, int hPref, int vPref) {
		h.addComponent(c, GroupLayout.PREFERRED_SIZE, hPref, GroupLayout.PREFERRED_SIZE);
		v.addComponent(c, GroupLayout.PREFERRED_SIZE, vPref, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED);
	}

	public void addPair(Component c, Component c1, int spacing) {
		//TODO
	}

	public void set() {
		setHorizontalGroup(createParallelGroup(Alignment.LEADING).addGroup(createSequentialGroup().addContainerGap().addGroup(h)));
		setVerticalGroup(createParallelGroup(Alignment.LEADING).addGroup(v));
	}
}