package com.vistatec.ocelot.tm.gui.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modal dialog letting users add a new TM to the list of TMs already configured
 * in Ocelot. Users have to insert a name and a root directory for the TM. The
 * root directory must contain at least a .tmx file. Both fields are mandatory.
 */
public class TmAddingDialog extends JDialog implements Runnable,
		ActionListener {

	/** serial version UID. */
	private static final long serialVersionUID = 7715304755942763439L;

	/** Log. */
	private static final Logger log = LoggerFactory.getLogger(TmAddingDialog.class);

	/** the controller. */
	private TmGuiConfigController controller;

	/** The Name label. */
	private JLabel lblName;

	/** The Name text field. */
	private JTextField txtName;

	/** The Location label. */
	private JLabel lblLocation;

	/** The Location text field. */
	private JTextField txtLocation;

	/** The Browse button. */
	private JButton btnBrowse;

	/** The Save button. */
	private JButton btnSave;

	/** The Cancel button. */
	private JButton btnCancel;

	/**
	 * The default border used for rendering text field in the current selected
	 * look and feel.
	 */
	private Border defTxtBorder;

	/**
	 * Creates a new dialog having an owner dialog.
	 * 
	 * @param controller
	 *            the controller.
	 * @param owner
	 *            the owner dialog.
	 */
	public TmAddingDialog(final TmGuiConfigController controller, final JDialog owner) {

		super(owner, true);
		this.controller = controller;
	}

	/**
	 * Creates a new dialog having an owner frame.
	 * 
	 * @param controller
	 *            the controller
	 * @param owner
	 *            the owner.
	 */
	public TmAddingDialog(final TmGuiConfigController controller, final JFrame owner) {

		super(owner, true);
		this.controller = controller;
	}

	/**
	 * Creates the main panel. It is displayed in the dialog center.
	 * 
	 * @return the main panel.
	 */
	private Component getMainPanel() {

		// Create components to display in the main panel
		lblName = new JLabel("Name");
		lblName.setHorizontalTextPosition(JLabel.RIGHT);
		txtName = new JTextField();
		txtName.setPreferredSize(new Dimension(200, 25));
		txtName.setMinimumSize(new Dimension(200, 25));
		defTxtBorder = txtName.getBorder();
		lblLocation = new JLabel("Location");
		lblLocation.setHorizontalTextPosition(JLabel.RIGHT);
		txtLocation = new JTextField();
		txtLocation.setPreferredSize(new Dimension(300, 25));
		txtLocation.setMinimumSize(new Dimension(300, 25));
		btnBrowse = new JButton("Browse...");
		btnBrowse.setPreferredSize(new Dimension(90, 25));
		btnBrowse.addActionListener(this);

		// Create the main panel and add the components.
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		GridBagConstraints gridBag = new GridBagConstraints();
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.insets = new Insets(15, 10, 10, 5);
		mainPanel.add(lblName, gridBag);
		gridBag.gridx = 1;
		gridBag.insets = new Insets(15, 0, 10, 5);
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(txtName, gridBag);
		gridBag.gridx = 0;
		gridBag.gridy = 1;
		gridBag.fill = GridBagConstraints.NONE;
		gridBag.insets = new Insets(0, 10, 10, 5);
		mainPanel.add(lblLocation, gridBag);
		gridBag.gridx = 1;
		gridBag.insets = new Insets(0, 0, 10, 5);
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(txtLocation, gridBag);
		gridBag.gridx = 2;
		gridBag.insets = new Insets(0, 10, 15, 15);
		gridBag.fill = GridBagConstraints.NONE;
		mainPanel.add(btnBrowse, gridBag);

		return mainPanel;
	}

	/**
	 * Creates the bottom panel. It is displayed on bottom of the dialog.
	 * 
	 * @return the bottom panel.
	 */
	private Component getBottomPanel() {

		// Creates components to display in the bottom panel.
		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		// Create the panel and add components
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 10));
		bottomPanel.add(btnSave);
		bottomPanel.add(btnCancel);

		return bottomPanel;
	}

	/**
	 * Builds the dialog and its components. Then makes it visible.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		setTitle("Add New TM");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		add(getMainPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);

	}

	/**
	 * Invoked when the "Browse" button is pressed. It opens a file chooser,
	 * letting the user choose a folder for the TM.
	 */
	private void browseLocation() {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setDialogTitle("Change TM Directory");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int option = fileChooser.showOpenDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (selectedFile != null) {
				txtLocation.setText(selectedFile.getAbsolutePath());
				txtLocation.setToolTipText(txtLocation.getText());
			}
		}
	}

	/**
	 * Checks if all mandatory fields are filled in. Both name and location are
	 * required fields for TM creation. If at least one of the required fields
	 * is empty, it prompts a warning message to the user and the empty required
	 * fields are highlighted with a red border.
	 * 
	 * @return <code>true</code> if all required fields are filled in:
	 *         <code>false</code> otherwise.
	 */
	private boolean checkMandatoryFields() {

		boolean retValue = true;
		if (txtName.getText() == null || txtName.getText().isEmpty()) {
			txtName.setBorder(new LineBorder(Color.red));
			retValue = false;
		} else {
			txtName.setBorder(defTxtBorder);
		}
		if (txtLocation.getText() == null || txtLocation.getText().isEmpty()) {
			txtLocation.setBorder(new LineBorder(Color.red));
			retValue = false;
		} else {
			txtLocation.setBorder(defTxtBorder);
		}
		return retValue;
	}

	/**
	 * Invoked when the "Save" button is pressed. It invokes methods for MT
	 * creation and then closes the dialog.
	 */
	private void save() {

//		if (checkMandatoryFields()) {
//			try {
////				controller
////						.createNewTm(txtName.getText(), txtLocation.getText());
//				close();
//			} catch (IOException e) {
//				log.trace("Error while creating a new TM: " + e.getMessage(), e);
//				JOptionPane.showMessageDialog(this, e.getMessage(),
//						"Create TM", JOptionPane.WARNING_MESSAGE);
//			} catch (Exception e) {
//				log.trace("Error while creating a new TM.", e);
//				e.printStackTrace();
//				JOptionPane.showMessageDialog(this,
//						"An error occurred while creating the TM",
//						"Create TM Error", JOptionPane.ERROR_MESSAGE);
//			}
//		} else {
//			JOptionPane.showMessageDialog(this, "Please, fill in all fields.",
//					"Fill mandatory fields", JOptionPane.WARNING_MESSAGE);
//		}
	}

	/**
	 * Closes the dialog.
	 */
	private void close() {

		controller.closeDialog();
		setVisible(false);
		dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		JButton btnSource = (JButton) e.getSource();
		if (btnBrowse.equals(btnSource)) {
			browseLocation();
		} else if (btnSave.equals(btnSource)) {
			save();
		} else if (btnCancel.equals(btnSource)) {
			close();
		}
	}

}
