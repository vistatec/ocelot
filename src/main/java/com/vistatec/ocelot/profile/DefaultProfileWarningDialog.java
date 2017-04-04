package com.vistatec.ocelot.profile;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.SystemColor;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class DefaultProfileWarningDialog {

	private Component owner;

	private JCheckBox ckNotShowAgain;

	public DefaultProfileWarningDialog(Component owner) {

		this.owner = owner;
	}

	public void promptWarningMessage() {

		JOptionPane.showMessageDialog(owner, getMainComponent(),
		        "Ocelot Default Configuration", JOptionPane.WARNING_MESSAGE);
	}

	private Component getMainComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(new Dimension(300, 100));
		JTextArea message = new JTextArea();
		Color bg = (Color)UIManager.get("OptionPane.background");
		message.setBackground(bg);
		message.setWrapStyleWord(true);
		message.setLineWrap(true);
		message.setText("The default configuration has been loaded.\n You can customize your own configuration by setting a profile in the \"Profile\" window (File -> Profile menu)");
		panel.add(message);

		JPanel checkBoxContainer = new JPanel();
		checkBoxContainer.setLayout(new BoxLayout(checkBoxContainer,
		        BoxLayout.X_AXIS));
		ckNotShowAgain = new JCheckBox();
		ckNotShowAgain.setText("Do not show again");
		ckNotShowAgain.setFont(message.getFont());
		checkBoxContainer.add(ckNotShowAgain);
		checkBoxContainer.add(Box.createHorizontalGlue());
		panel.add(checkBoxContainer);
		return panel;
	}
	
	public boolean isDoNotShowAgainFlagged(){
		return ckNotShowAgain.isSelected();
	}

}
