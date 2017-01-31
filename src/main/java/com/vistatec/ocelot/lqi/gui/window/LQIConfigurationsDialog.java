package com.vistatec.ocelot.lqi.gui.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.lqi.LQIGridController;
import com.vistatec.ocelot.lqi.gui.panel.LQIConfPreviewPanel;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

/**
 * This dialog displays the list of configurations available for the LQI Grid.
 * It provides buttons for deleting, creating, copying and editing a
 * configuration.
 */
public class LQIConfigurationsDialog extends JDialog implements ActionListener,
        Runnable {

	private static final long serialVersionUID = -3937847699154946755L;

	private static final int BTN_WIDTH = 70;

	private static final int BTN_HEIGHT = 25;

	private static final Logger log = Logger
	        .getLogger(LQIConfigurationsDialog.class);

	private LQIGridConfigurations lqiGrid;

	private JButton btnNew;

	private JButton btnCopy;

	private JButton btnEdit;

	private JButton btnDelete;

	private JButton btnClose;

	private JList<ConfigurationListItem> configsList;

	private LQIGridController controller;

	private LQIConfPreviewPanel preview;

	public LQIConfigurationsDialog(Window owner, LQIGridController controller,
	        LQIGridConfigurations lqiGrid) {

		super(owner);
		setModal(true);
		this.controller = controller;
		this.lqiGrid = lqiGrid;
		init();
	}

	private void init() {

		setTitle("LQI Grid Configurations");
		add(getCenterComponent(), BorderLayout.CENTER);
		add(getBottomComponent(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(getOwner());
		setResizable(false);
	}

	private Component getBottomComponent() {

		JPanel bottomPanel = new JPanel();
		btnClose = new JButton("Close");
		btnClose.addActionListener(this);
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		bottomPanel.add(btnClose);
		return bottomPanel;
	}

	private Component getCenterComponent() {

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		createButtons();
		createConfigurationsList();
		JScrollPane listScroll = new JScrollPane(configsList);
		initGridTablePreviewPanel();

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 10, 0, 10);
		c.anchor = GridBagConstraints.LINE_START;
		centerPanel.add(new JLabel("Configurations"), c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 2;
		c.insets = new Insets(0, 10, 10, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		centerPanel.add(listScroll, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.insets = new Insets(0, 0, 10, 10);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTH;
		c.weightx = 0;
		centerPanel.add(btnNew, c);

		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 10, 10);
		centerPanel.add(btnCopy, c);

		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(0, 0, 10, 10);
		centerPanel.add(btnEdit, c);

		c.gridx = 2;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 10, 10);
		centerPanel.add(btnDelete, c);

		c.gridy = 3;
		c.gridx = 0;
		c.gridwidth = 3;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		centerPanel.add(preview, c);

		enableDisableComponents();
		return centerPanel;
	}

	private void createButtons() {
		btnCopy = new JButton("Copy");
		btnCopy.addActionListener(this);
		btnCopy.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(this);
		btnDelete.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
		btnEdit = new JButton("Edit");
		btnEdit.addActionListener(this);
		btnEdit.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
		btnNew = new JButton("New");
		btnNew.addActionListener(this);
		btnNew.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
	}

	private void enableDisableComponents() {
		
		ConfigurationListItem selItem = configsList.getSelectedValue();

		btnCopy.setEnabled(selItem != null);
		btnEdit.setEnabled(selItem != null && !selItem.getConfiguration().isActive());
		btnDelete.setEnabled(selItem != null
		        && !selItem.getConfiguration().isActive());

	}

	private void createConfigurationsList() {

		configsList = new JList<ConfigurationListItem>(getListModel());

		configsList.setCellRenderer(new ConfigurationListRenderer());
		configsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		configsList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					ConfigurationListItem selItem = configsList
					        .getSelectedValue();
					enableDisableComponents();
					if (selItem != null) {
						showConfigurationPreview(selItem.getConfiguration());
					} else {
						showConfigurationPreview(null);
					}
				}
			}
		});
	}

	private DefaultListModel<ConfigurationListItem> getListModel() {

		DefaultListModel<ConfigurationListItem> configsModel = new DefaultListModel<ConfigurationListItem>();
		List<LQIGridConfiguration> configurations = lqiGrid.getConfigurations();
		for (LQIGridConfiguration conf : configurations) {
			configsModel.addElement(new ConfigurationListItem(conf));
		}
		return configsModel;
	}

	private void close() {
		setVisible(false);
		dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnClose)) {
			close();
		} else if (e.getSource().equals(btnCopy)) {
			copyConfiguration();
		} else if (e.getSource().equals(btnDelete)) {
			deleteConfiguration();
		} else if (e.getSource().equals(btnEdit)) {
			editConfiguration();
		} else if (e.getSource().equals(btnNew)) {
			createNewConfiguration();
		}
	}

	private void createNewConfiguration() {
		log.debug("Creating a new configuration from scratch...");
		displayEditConfDialog(new LQIGridConfiguration());
	}

	private void editConfiguration() {
		log.debug("Editing the configuration \""
		        + configsList.getSelectedValue().toString() + "\"...");
		displayEditConfDialog(configsList.getSelectedValue().getConfiguration());
	}

	private void deleteConfiguration() {

		ConfigurationListItem selItem = configsList.getSelectedValue();
		if (selItem != null) {
			log.debug("Deleting the configuration \"" + selItem.toString()
			        + "\"...");
			String message = "";
			boolean delActiveConf = selItem.getConfiguration().equals(
			        lqiGrid.getActiveConfiguration());
			if (delActiveConf) {
				message = "You are trying to delete the active configuration. One of the remaining configurations will be set as the active one. Do you want to continue?";
			} else {
				message = "Do you want to delete the selected configuration?";
			}
			int option = JOptionPane.showConfirmDialog(this, message,
			        "Delete Configuration", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				try {
					lqiGrid.getConfigurations().remove(
					        selItem.getConfiguration());
					if (delActiveConf) {
						lqiGrid.getConfigurations().get(0).setActive(true);
					}
					controller.saveLQIGridConfiguration(lqiGrid, false);
					((DefaultListModel<ConfigurationListItem>) configsList
					        .getModel()).removeElement(selItem);
					preview.clear();
					repaint();

				} catch (TransferException e) {
					log.debug(
					        "Error while deleting an existing configuration.",
					        e);
					JOptionPane
					        .showMessageDialog(
					                this,
					                "An error has occurred while deleting the selected configuration",
					                "Delete Configuration Error",
					                JOptionPane.ERROR_MESSAGE);
				}
			}
		}

	}

	private void copyConfiguration() {
		try {
			LQIGridConfiguration copyConf = (LQIGridConfiguration) configsList
			        .getSelectedValue().getConfiguration().clone();
			copyConf.setName("Copy of " + copyConf.getName());
			log.debug("Creating a new configuration starting from \""
			        + copyConf.getName() + "\"...");
			displayEditConfDialog(copyConf);
		} catch (CloneNotSupportedException e) {
			log.warn("This exception should never be thrown.", e);
		}
	}

	private void displayEditConfDialog(LQIGridConfiguration configurationToEdit) {

		LQIConfigurationEditDialog editDialog = new LQIConfigurationEditDialog(
		        this, controller.getPlatformSupport(), configurationToEdit);
		editDialog.setVisible(true);
	}

	private void initGridTablePreviewPanel() {

		preview = new LQIConfPreviewPanel();
		preview.setBorder(BorderFactory.createCompoundBorder(
		        BorderFactory.createTitledBorder("PREVIEW"),
		        BorderFactory.createLoweredBevelBorder()));
		preview.load(null);
	}

	private void showConfigurationPreview(LQIGridConfiguration lqiConf) {

		preview.load(lqiConf);
	}

	public void save(LQIGridConfiguration lqiGridConf) {

		LQIGridConfiguration selConf = null;
		if (configsList.getSelectedValue() != null) {
			selConf = configsList.getSelectedValue().getConfiguration();
		}

		// configuration edited
		if (selConf != null && selConf.equals(lqiGridConf)) {
			lqiGrid.updateConfiguration(lqiGridConf);
			((DefaultListModel<ConfigurationListItem>) configsList.getModel())
			        .setElementAt(new ConfigurationListItem(lqiGridConf),
			                configsList.getSelectedIndex());
			preview.load(lqiGridConf);

			// new or copy configuration
		} else {
			lqiGrid.addConfiguration(lqiGridConf);
			((DefaultListModel<ConfigurationListItem>) configsList.getModel())
			        .addElement(new ConfigurationListItem(lqiGridConf));
		}
		try {
			controller
			        .saveLQIGridConfiguration(lqiGrid, lqiGridConf.isActive());
		} catch (TransferException e) {
			log.error("Error while saving the LQI grid configuration", e);
			JOptionPane
			        .showMessageDialog(
			                this,
			                "An error has occurred while saving the LQI Grid configuration.",
			                "Save Configuration Error",
			                JOptionPane.ERROR_MESSAGE);
		}

	}

	public boolean isConfigurationValidName(String newName,
	        LQIGridConfiguration editedConf) {

		boolean valid = true;
		for (LQIGridConfiguration conf : lqiGrid.getConfigurations()) {
			if (!conf.equals(editedConf) && newName.equals(conf.getName())) {
				valid = false;
				break;
			}
		}
		return valid;
	}

	@Override
	public void run() {

		setVisible(true);
	}

}

class ConfigurationListItem {

	private LQIGridConfiguration configuration;

	public ConfigurationListItem(LQIGridConfiguration configuration) {

		this.configuration = configuration;
	}

	public LQIGridConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public String toString() {

		return configuration.getName();
	}
}

class ConfigurationListRenderer extends DefaultListCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8333874222032631030L;
	
	private static final String activeConfIconName = "ok.png";
	
	private final ImageIcon activeIcon;
	
	public ConfigurationListRenderer() {
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		activeIcon = new ImageIcon(kit.createImage(getClass().getResource(
		        activeConfIconName)));
    }

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
	        int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
		        index, isSelected, cellHasFocus);
		label.setText("\u2022 " + label.getText());
		if(((ConfigurationListItem)value).getConfiguration().isActive()){
			label.setIcon(activeIcon);
			label.setHorizontalTextPosition(SwingConstants.LEADING);
		}
		return label;
	}

}