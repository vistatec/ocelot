package com.vistatec.ocelot.lgk;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog prompted to the user when the "Download from LGK" menu item is
 * clicked. It lets the user to insert the ID of the document to download.
 */
public class DownloadFromLgkDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -8474704363835758550L;

	/** The initial width of the dialog. */
	private static final int WIDTH = 400;

	/** The initial height of the dialog. */
	private static final int HEIGHT = 150;

	/** The document ID text field. */
	private JTextField txtDocId;

	/** The download button. */
	private JButton btnDownload;

	/** The cancel button. */
	private JButton btnCancel;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner window.
	 */
	public DownloadFromLgkDialog(Window owner) {
		super(owner);
		setModal(true);
		buildDialog();
	}

	private void buildDialog() {

		setTitle("Download from LingoTek");
		setSize(new Dimension(WIDTH, HEIGHT));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 10, 10, 0);
		mainPanel.add(new JLabel("Document ID"), c);

		txtDocId = new JTextField();
		c.gridx = 1;
		c.weightx = 0.5;
		c.insets = new Insets(10, 5, 10, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(txtDocId, c);
		add(mainPanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		btnDownload = new JButton("Download");
		btnDownload.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		bottomPanel.add(btnDownload);
		bottomPanel.add(btnCancel);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void save() {

		if (txtDocId.getText() != null && !txtDocId.getText().isEmpty()) {
			close();
		} else {
			JOptionPane.showMessageDialog(this,
					"Please, insert a document ID.", "Document ID missing",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void cancel() {

		txtDocId.setText("");
		close();
	}

	private void close() {
		setVisible(false);
	}

	/**
	 * Displays the dialog.
	 */
	public void open() {
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnCancel)) {
			cancel();
		} else if (e.getSource().equals(btnDownload)) {
			save();
		}
	}

	/**
	 * Gets the document ID inserted by the user.
	 * 
	 * @return the document ID if the related text field has been filled in; an
	 *         empty string otherwise.
	 */
	public String getDocumentId() {
		return txtDocId.getText();
	}

}
