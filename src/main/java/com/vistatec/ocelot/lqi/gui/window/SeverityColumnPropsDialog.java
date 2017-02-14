package com.vistatec.ocelot.lqi.gui.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.vistatec.ocelot.lqi.gui.FloatDocument;
import com.vistatec.ocelot.lqi.model.LQISeverity;

/**
 * The severity column properties dialog.
 */
public class SeverityColumnPropsDialog extends JDialog implements
        ActionListener {

	/** The serial version UID. */
	private static final long serialVersionUID = -464954861504652213L;

	/** The dialog width. */
	private static final int WIDTH = 300;
	
	/** The dialog height. */
	private static final int HEIGHT = 100;

	/** The text fields width. */
	private static final int TXT_WIDTH = 150;

	/** The text fields height. */
	private static final int TXT_HEIGHT = 25;

	/** The labels width. */
	private static final int LBL_WIDTH = 100;

	/** The labels height. */
	private static final int LBL_HEIGHT = 25;

	/** The title suffix. */
	private static final String TITILE_SUFFIX = " Severity Score";

	/** The LQI grid dialog. */
	private LQIConfigurationEditDialog lqiGridDialog;

	/** The severity. */
	private LQISeverity severity;

	/** The severity score text field. */
	private JTextField txtSeverityScore;

	/** The severity name text field. */
	private JTextField txtSeverityName;

	/** The ok button. */
	private JButton btnOk;

	/** The cancel button. */
	private JButton btnCancel;

	/**
	 * Constructor.
	 * @param ownerDialog the owner dialog
	 * @param location the location
	 * @param severity the severity
	 */
	public SeverityColumnPropsDialog(LQIConfigurationEditDialog ownerDialog, Point location,
	        LQISeverity severity) {

		super(ownerDialog, true);
		this.lqiGridDialog = ownerDialog;
		this.severity = severity;
		makeDialog(location);
	}

	/**
	 * Makes the dialog.
	 * @param location the location where the dialog has to be displayed.
	 */
	private void makeDialog(Point location) {

		setTitle("Severity Column Properties");

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
		panel.setSize(new Dimension(WIDTH, HEIGHT));
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setBorder(BorderFactory.createEmptyBorder(15, 5, 10, 5));

		JLabel lblSeverityName = new JLabel("Severity Name");
		configLabel(lblSeverityName);
		txtSeverityName = new JTextField();
		if (severity != null) {
			txtSeverityName.setText(severity.getName());
		}
		txtSeverityName.setSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
		txtSeverityName.setPreferredSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
		panel.add(lblSeverityName);
		panel.add(txtSeverityName);
		JLabel lblSeverity = new JLabel(TITILE_SUFFIX);
		configLabel(lblSeverity);
		panel.add(lblSeverity);
		txtSeverityScore = new JTextField();
		txtSeverityScore.setDocument(new FloatDocument());
		if (severity != null) {
			txtSeverityScore.setText(String.valueOf(severity.getScore()));
		}
		txtSeverityScore.setSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
		txtSeverityScore.setPreferredSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));

		panel.add(txtSeverityScore);
		add(panel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		btnCancel = new JButton("Cancel");
		btnOk = new JButton("Ok");
		btnCancel.addActionListener(this);
		btnOk.addActionListener(this);
		bottomPanel.add(btnOk);
		bottomPanel.add(btnCancel);
		add(bottomPanel, BorderLayout.SOUTH);
		pack();
		if (location != null) {
			setLocation(location);
		} else {
			setLocationRelativeTo(getOwner());
		}
	}

	/**
	 * Configures a label.
	 * @param lbl the label.
	 */
	private void configLabel(JLabel lbl) {

		lbl.setSize(new Dimension(LBL_WIDTH, LBL_HEIGHT));
		lbl.setPreferredSize(new Dimension(LBL_WIDTH, LBL_HEIGHT));
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);
	}

	/**
	 * Saves the severity column properties.
	 */
	public void save() {

		boolean validFields = true;
		if (txtSeverityName.getText().isEmpty()) {
			txtSeverityName.setBorder(new LineBorder(Color.red));
			validFields = false;
		} else {
			txtSeverityName.setBorder(new JTextField().getBorder());
		}
		if (txtSeverityScore.getText().isEmpty()) {
			txtSeverityScore.setBorder(new LineBorder(Color.red));
			validFields = false;
		} else {
			txtSeverityScore.setBorder(new JTextField().getBorder());
		}
		if (validFields) {
			String newName = txtSeverityName.getText();
			if (lqiGridDialog.checkSeverityName(severity, newName)) {
				
				if (severity == null) {
					severity = new LQISeverity();
					severity.setName(newName);
					severity.setScore(Double.parseDouble(txtSeverityScore
					        .getText()));
					lqiGridDialog.createSeverityColumn(severity);
				} else {
					LQISeverity newSeverity = new LQISeverity(
							newName,
					        Double.parseDouble(txtSeverityScore.getText()));
					lqiGridDialog.severityChanged(severity, newSeverity);
				}
				close();
			} else {
				JOptionPane
		        .showMessageDialog(
		                this,
		                "A severity named \""
		                        + newName
		                        + "\" already exists.\nPlease, choose a different name.",
		                "Severity Duplicate",
		                JOptionPane.WARNING_MESSAGE);
			}

		} else {
			JOptionPane.showMessageDialog(this, "Please, fill all fields.",
			        "Mandatory fields", JOptionPane.WARNING_MESSAGE);
		}

	}

	/**
	 * Closes the dialog.
	 */
	public void close() {

		setVisible(false);
		dispose();
	}

	/**
	 * Gets the score value.
	 * @return the score value.
	 */
	public String getInsertedScoreValue() {
		return txtSeverityScore.getText();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnCancel)) {
			close();
		} else if (e.getSource().equals(btnOk)) {
			save();
		}
	}

}
