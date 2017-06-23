package com.vistatec.ocelot.project.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class OcelotProjectDetailsPanel extends JPanel {

	private static final long serialVersionUID = -3130629188454414510L;

	public static final int CREATE = 1;
	
	public static final int VIEW = 2;
	
	private JTextField txtLocation;
	
	private JTextField txtName;
	
	private JTextArea txtDescription;
	
	private JCheckBox ckTranslation;
	
	private JCheckBox ckReview;
	
	private JButton btnBrowse;
	
	private int mode;
	
	public OcelotProjectDetailsPanel(int mode) {
		this.mode = mode;
		initPanel();
	}

	private void initPanel() {
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel lblName = new JLabel("Name");
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(20, 10, 10, 5);
		c.anchor = GridBagConstraints.EAST;
		add(lblName, c);
		
		txtName = new JTextField();
		c.gridx = 1;
		c.weightx = 0.1;
		c.insets = new Insets(20, 0, 10, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(txtName, c);
		
		JLabel lblDescr = new JLabel("Description");
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.insets = new Insets(0, 10, 10, 5);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(lblDescr, c);
		
		txtDescription = new JTextArea();
		txtDescription.setLineWrap(true);
		txtDescription.setWrapStyleWord(true);
		JScrollPane scroll = new JScrollPane(txtDescription);
		c.gridx = 1;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.insets = new Insets(0, 0, 10, 5);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		add(scroll, c);
		
		JLabel lblLocation = new JLabel("Location");
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 10, 10, 5);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(lblLocation, c);
		
		txtLocation = new JTextField();
		c.gridx = 1;
		c.weightx = 0.1;
		c.insets = new Insets(0, 0, 10, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(txtLocation, c);
		if(mode == CREATE){
			btnBrowse = new JButton("...");
			btnBrowse.setSize(new Dimension(30, 25));
			btnBrowse.setPreferredSize(new Dimension(30, 25));
			btnBrowse.setMinimumSize(new Dimension(30, 25));
			btnBrowse.setMaximumSize(new Dimension(30, 25));
			c.gridx = 2;
			c.weightx = 0;
			c.insets = new Insets(10, 0, 10, 20);
			c.fill = GridBagConstraints.NONE;
//			c.anchor = GridBagConstraints.PAGE_END;
			add(btnBrowse, c);
			
		}
		
		
		
	}
	
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);
		frame.setPreferredSize(new Dimension(400, 300));
		frame.add(new OcelotProjectDetailsPanel(OcelotProjectDetailsPanel.CREATE), BorderLayout.CENTER);
		frame.setVisible(true);
	}
	
}
