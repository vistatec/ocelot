package com.vistatec.ocelot.profile;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ProfileDialog extends JDialog implements ActionListener, Runnable {

	private static final long serialVersionUID = 6832469233487334801L;

	private static final int WIDTH = 300;

	private static final int HEIGHT = 200;

	private static final int EDIT_MODE = 1;

	private static final int VIEW_MODE = 0;

	private JPanel bottomPanel;

	private JPanel mainPanel;

	private JComboBox<String> cmbProfiles;

	private AutocompleteJComboBox cmbEditProfiles;

	private JButton btnClose;

	private JButton btnEdit;

	private JButton btnCancel;

	private JButton btnSave;

	private IProfileManager manager;

	private int mode;

	public ProfileDialog(Window owner, IProfileManager manager) {

		super(owner);
		setModal(true);
		mode = VIEW_MODE;
		this.manager = manager;
	}

	private void buildFrame() {

		setTitle();
		setSize(new Dimension(WIDTH, HEIGHT));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setMinimumSize(new Dimension(WIDTH, HEIGHT));

		add(getMainPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);

	}

	private Component getBottomPanel() {
		bottomPanel = new JPanel();
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnClose = new JButton("Close");
		btnClose.addActionListener(this);
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(this);
		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.add(btnEdit);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(btnSave);
		bottomPanel.add(Box.createHorizontalStrut(5));
		bottomPanel.add(btnCancel);
		bottomPanel.add(btnClose);
		setBottomPanelMode();
		return bottomPanel;
	}

	private Component getMainPanel() {

		mainPanel = new JPanel();
		JLabel lblProfile = new JLabel("Workspace");
		List<String> profiles = manager.getProfiles();
		cmbProfiles = new JComboBox<String>(
		        profiles.toArray(new String[profiles.size()]));
		cmbProfiles.setSelectedItem(manager.getActiveProfile());
		cmbProfiles.setEnabled(false);
		cmbProfiles.setRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 2516887162510893418L;

			@Override
			public void paint(Graphics g) {

				setForeground(SystemColor.controlDkShadow);
				super.paint(g);
			}
		});

		List<String> editProfiles = new ArrayList<String>(profiles);
		cmbEditProfiles = new AutocompleteJComboBox(editProfiles);
		cmbEditProfiles.setSelectedItem(cmbProfiles.getSelectedItem());
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 10, 10, 5);
		mainPanel.add(lblProfile, c);

		c.weightx = 0.1;
		c.gridx = 1;
		c.insets = new Insets(10, 0, 10, 10);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(cmbProfiles, c);
		mainPanel.add(cmbEditProfiles, c);

		setMainPanelMode();
		return mainPanel;
	}

	private void changeMode() {

		if (mode == VIEW_MODE) {
			mode = EDIT_MODE;
		} else {
			mode = VIEW_MODE;
		}
		setTitle();
		setBottomPanelMode();
		setMainPanelMode();

	}

	private void setMainPanelMode() {

		cmbEditProfiles.setVisible(mode == EDIT_MODE);
		cmbProfiles.setVisible(mode == VIEW_MODE);
		if(mode == EDIT_MODE){
			cmbEditProfiles.setSelectedItem(cmbProfiles.getSelectedItem());
		}
	}

	private void setBottomPanelMode() {

		btnClose.setVisible(mode == VIEW_MODE);
		btnEdit.setVisible(mode == VIEW_MODE);
		btnCancel.setVisible(mode == EDIT_MODE);
		btnSave.setVisible(mode == EDIT_MODE);
	}

	private void setTitle() {
		if (mode == VIEW_MODE) {
			setTitle("Workspace");
		} else {
			setTitle("Workspace - Edit");
		}
	}

	private void close() {

		setVisible(false);
	}

	private void save() {

		String selProfile = (String) cmbEditProfiles.getSelectedItem();
		String currentProfile = manager.getActiveProfile();
		if (!selProfile.equals(currentProfile)) {

			int option = JOptionPane
			        .showConfirmDialog(
			                this,
			                "Ocelot will be restarted with the new configuration. Do you want to continue?",
			                "Ocelot profile", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {

				boolean profChanged = true;
				try {
					manager.changeProfile(selProfile);
				} catch (ProfileException e) {
					JOptionPane
					        .showMessageDialog(
					                this,
					                "An error has occurred while changing the profile. The previous profile will be restored.",
					                "Changing Profile Error",
					                JOptionPane.ERROR_MESSAGE);
					try {
						profChanged = false;
						manager.restoreOldProfile(currentProfile, selProfile,
						        isNewProfile(selProfile));
						changeMode();
					} catch (ProfileException e1) {

					}
				}
				if (profChanged) {
					close();

				}
			} else {
				changeMode();
			}
		}
	}

	private boolean isNewProfile(String profileName) {

		boolean newProfile = true;
		for (int i = 0; i < cmbProfiles.getItemCount(); i++) {
			if (cmbProfiles.getItemAt(i).equals(profileName)) {
				newProfile = false;
				break;
			}
		}
		return newProfile;
	}

	private void cancel() {
		cmbProfiles.setSelectedItem(manager.getActiveProfile());
		changeMode();
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnEdit)) {
			changeMode();
		} else if (e.getSource().equals(btnCancel)) {
			cancel();
		} else if (e.getSource().equals(btnSave)) {
			save();
		} else if (e.getSource().equals(btnClose)) {
			close();
		}
	}

	@Override
	public void run() {

		buildFrame();
		setVisible(true);
	}
}

