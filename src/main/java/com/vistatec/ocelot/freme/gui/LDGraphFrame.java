package com.vistatec.ocelot.freme.gui;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JDialog;

public class LDGraphFrame extends JDialog {

	private static final long serialVersionUID = 8754322433455263070L;

	public LDGraphFrame(Window owner, Component graphComponent, int segNum,
	        boolean isTarget) {

		super(owner);
		setTitle("Linked Data Graph - Segment #" + segNum + " - "
		        + (isTarget ? "target" : "source"));
		add(graphComponent);
		pack();
		setLocationRelativeTo(owner);

	}

	public void open() {

		setVisible(true);
	}

}
