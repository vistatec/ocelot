package com.vistatec.ocelot.lqi.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.vistatec.ocelot.ui.IntegerDocument;

public class SeverityScoreDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -464954861504652213L;

	private static final int WIDTH = 150;

	private static final int HEIGHT = 50;

	private static final int TXT_WIDTH = 100;

	private static final int TXT_HEIGHT = 25;

	private static final int BTN_SIZE = 20;

	private static final String OK_ICON = "ok.png";

	private static final String TITILE_SUFFIX = " Severity Score";

	private LQIGridDialog lqiGridDialog;

	private String severityName;

	private JTextField txtSeverity;

	private JButton btnOk;

	public SeverityScoreDialog(LQIGridDialog ownerDialog, Point location,
	        String severityName, int score) {

		super(ownerDialog, true);
		this.lqiGridDialog = ownerDialog;
		this.severityName = severityName;
		makeDialog(location, score);
	}

	private void makeDialog(Point location, int score) {

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
		panel.setSize(new Dimension(WIDTH, HEIGHT));

		JLabel lblSeverity = new JLabel(severityName + TITILE_SUFFIX);
		panel.add(lblSeverity);
		txtSeverity = new JTextField();
		txtSeverity.setDocument(new IntegerDocument());
		txtSeverity.setText(String.valueOf(score));
		txtSeverity.setPreferredSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
		txtSeverity.addActionListener(this);

		panel.add(txtSeverity);
		Toolkit kit = Toolkit.getDefaultToolkit();
		ImageIcon icon = new ImageIcon(kit.createImage(getClass().getResource(
		        OK_ICON)));
		btnOk = new JButton(icon);
		btnOk.setPreferredSize(new Dimension(BTN_SIZE, BTN_SIZE));
		btnOk.setOpaque(false);
		btnOk.setBorderPainted(false);
		btnOk.setContentAreaFilled(false);
		btnOk.addActionListener(this);

		panel.add(btnOk);
		add(panel);
		setUndecorated(true);
//		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		pack();
		setLocation(location);
	}

	public void close() {

		setVisible(false);
		dispose();
	}

	public String getInsertedScoreValue() {
		return txtSeverity.getText();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		lqiGridDialog.severityScoreChanged(txtSeverity.getText(), severityName);
		close();
	}
}
