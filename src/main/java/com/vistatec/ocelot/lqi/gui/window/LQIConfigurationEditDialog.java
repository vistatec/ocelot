package com.vistatec.ocelot.lqi.gui.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.vistatec.ocelot.PlatformSupport;
import com.vistatec.ocelot.config.ConfigurationException;
import com.vistatec.ocelot.config.ConfigurationManager;
import com.vistatec.ocelot.config.LqiJsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.lqi.LQIGridController;
import com.vistatec.ocelot.lqi.gui.LQIGridTableHelper;
import com.vistatec.ocelot.lqi.gui.LQIGridTableModel;
import com.vistatec.ocelot.lqi.gui.panel.LQIGridTableContainer;
import com.vistatec.ocelot.lqi.gui.panel.LQIInfoPanel;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

public class LQIConfigurationEditDialog extends JDialog implements
        ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3246046647198453134L;

	private static final int MIN_WIDTH = 600;

	private static final int MIN_HEIGHT = 350;

	private JButton btnSave;

	private JButton btnCancel;

	private JButton btnAddRow;

	private JButton btnDelRow;

	private JButton btnMoveUp;

	private JButton btnMoveDown;

	private JButton btnAddCol;

	private LQIGridConfiguration lqiGridConf;

	private LQIGridTableHelper tableHelper;

	private LQIInfoPanel infoPanel;
	
	public LQIConfigurationEditDialog(LQIConfigurationsDialog owner,
	        PlatformSupport platformSupport, LQIGridConfiguration lqiGridConf) {

		super(owner);
		setModal(true);
		if (lqiGridConf == null) {
			this.lqiGridConf = new LQIGridConfiguration();
		} else {
			this.lqiGridConf = lqiGridConf;
		}

		this.tableHelper = new LQIGridTableHelper(platformSupport);
		init();
	}

	private void init() {

		setTitle("LQI Grid - Edit Configuration");
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setResizable(false);
		add(getTopPanel(), BorderLayout.NORTH);
		add(getCenterPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
		setLocationRelativeTo(getOwner());
	}

	private Component getBottomPanel() {

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		createAndConfigureButtons();
		JPanel rowsPanel = new JPanel();
		rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.X_AXIS));
		rowsPanel.setBorder(BorderFactory.createTitledBorder("Category rows"));
		rowsPanel.add(btnAddRow);
		rowsPanel.add(Box.createHorizontalStrut(10));
		rowsPanel.add(btnDelRow);
		rowsPanel.add(Box.createHorizontalStrut(10));
		rowsPanel.add(btnMoveUp);
		rowsPanel.add(Box.createHorizontalStrut(10));
		rowsPanel.add(btnMoveDown);

		JPanel colsPanel = new JPanel();
		colsPanel.setLayout(new BoxLayout(colsPanel, BoxLayout.X_AXIS));
		colsPanel.setPreferredSize(new Dimension(120, 50));
		colsPanel.setBorder(BorderFactory
		        .createTitledBorder("Severity Columns"));
		colsPanel.add(btnAddCol);
		bottomPanel.add(Box.createRigidArea(new Dimension(1, 50)));
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(rowsPanel);
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(colsPanel);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(btnSave);
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(btnCancel);
		bottomPanel.add(Box.createHorizontalStrut(10));
		return bottomPanel;
	}

	/**
	 * Creates and configures all the buttons for this dialog.
	 */
	private void createAndConfigureButtons() {
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		Toolkit kit = Toolkit.getDefaultToolkit();
		ImageIcon icon = new ImageIcon(kit.createImage(getClass().getResource(
		        "add.png")));
		btnAddRow = new JButton(icon);
		configIconButton(btnAddRow);
		btnAddCol = new JButton(icon);
		configIconButton(btnAddCol);
		icon = new ImageIcon(kit.createImage(getClass().getResource(
		        "remove.png")));
		btnDelRow = new JButton(icon);
		configIconButton(btnDelRow);
		icon = new ImageIcon(kit.createImage(getClass().getResource(
		        "arrow-up.png")));
		btnMoveUp = new JButton(icon);
		configIconButton(btnMoveUp);
		icon = new ImageIcon(kit.createImage(getClass().getResource(
		        "arrow-down.png")));
		btnMoveDown = new JButton(icon);
		configIconButton(btnMoveDown);
	}

	/**
	 * Configures an icon button.
	 * 
	 * @param btn
	 *            the button.
	 */
	private void configIconButton(JButton btn) {
		btn.setPreferredSize(new Dimension(20, 20));
		btn.setOpaque(false);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.addActionListener(this);
	}

	private Component getCenterPanel()  {

		// JPanel centerPanel = new JPanel();
		Action tableAction = new AbstractAction() {

			/**
			 * 
			 */
            private static final long serialVersionUID = -6312119744617259752L;

			@Override
			public void actionPerformed(ActionEvent e) {
				LQIErrorCategory selErrorCat = tableHelper.getLqiTableModel()
				        .getErrorCategoryAtRow(
				                tableHelper.getLqiTable().getSelectedRow());
				if (selErrorCat != null) {
					int selCol = tableHelper.getLqiTable().getSelectedColumn();
					String severityName = tableHelper.getLqiTableModel()
					        .getSeverityNameForColumn(selCol);
					LQIShortCut currShortcut = selErrorCat
					        .getShortcut(severityName);
					ShortCutDialog shortCutDialog = new ShortCutDialog(
					        LQIConfigurationEditDialog.this,
					        selErrorCat.getName() + " - " + severityName,
					        currShortcut);
					shortCutDialog.run();
					if (tableHelper.getLqiTable().getCellEditor() != null) {
						tableHelper.getLqiTable().getCellEditor()
						        .stopCellEditing();
						LQIConfigurationEditDialog.this.requestFocus();
					}
				}
			}
		};
		LQIGridConfiguration clonedConf = null;;
        try {
	        clonedConf = (LQIGridConfiguration) lqiGridConf
	                .clone();
        } catch (CloneNotSupportedException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
		LQIGridTableContainer tableContainer = new LQIGridTableContainer(
		        clonedConf, tableHelper, tableAction,
		        LQIGridTableModel.CONFIG_MODE);
		return tableContainer;
	}

	private Component getTopPanel() {

		infoPanel = new LQIInfoPanel();
		infoPanel.load(lqiGridConf);
//		JPanel topPanel = new JPanel();
//
//		txtName = new JTextField();
//		txtName.setPreferredSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
//		txtSupplier = new JTextField();
//		txtSupplier.setPreferredSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
//		txtThreshold = new JTextField();
//		txtThreshold.setPreferredSize(new Dimension(50, TXT_HEIGHT));
//		txtThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
//		txtThreshold.setDocument(new FloatDocument());
//		loadTextFields();
//		topPanel.setLayout(new GridBagLayout());
//		GridBagConstraints c = new GridBagConstraints();
//
//		c.weightx = 0.5;
//		c.gridx = 0;
//		c.gridy = 0;
//		c.insets = new Insets(10, 10, 10, 0);
//		c.anchor = GridBagConstraints.LINE_END;
//		topPanel.add(new JLabel("Name"), c);
//
//		c.gridx = 1;
//		c.gridy = 0;
//		c.anchor = GridBagConstraints.LINE_START;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.insets = new Insets(10, 5, 10, 10);
//		topPanel.add(txtName, c);
//
//		c.gridx = 2;
//		c.gridy = 0;
//		c.anchor = GridBagConstraints.LINE_END;
//		c.insets = new Insets(10, 10, 10, 0);
//		c.fill = GridBagConstraints.NONE;
//		topPanel.add(new JLabel("Threshold"), c);
//
//		c.gridx = 3;
//		c.gridy = 0;
//		c.insets = new Insets(10, 5, 10, 0);
//		c.anchor = GridBagConstraints.LINE_START;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		topPanel.add(txtThreshold, c);
//
//		c.gridx = 4;
//		c.gridy = 0;
//		c.anchor = GridBagConstraints.LINE_START;
//		c.fill = GridBagConstraints.NONE;
//		c.insets = new Insets(10, 2, 10, 10);
//		topPanel.add(new JLabel("%"), c);
//
//		c.gridx = 0;
//		c.gridy = 1;
//		c.anchor = GridBagConstraints.LINE_END;
//		c.insets = new Insets(0, 10, 10, 0);
//		c.fill = GridBagConstraints.NONE;
//		topPanel.add(new JLabel("Supplier"), c);
//
//		c.gridx = 1;
//		c.anchor = GridBagConstraints.LINE_START;
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.insets = new Insets(0, 5, 10, 10);
//		topPanel.add(txtSupplier, c);

		return infoPanel;
	}

	public static void main(String[] args) throws TransferException,
	        ConfigurationException {
		ConfigurationManager confManager = new ConfigurationManager();
		confManager.readAndCheckConfiguration(new File(System
		        .getProperty("user.home"), ".ocelot"));
		LqiJsonConfigService lqiConfService = confManager.getLqiConfigService();
		LQIGridConfigurations lqiGrid = lqiConfService.readLQIConfig();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		LQIConfigurationsDialog confdialog = new LQIConfigurationsDialog(frame,
		        new LQIGridController(lqiConfService, null, null), lqiGrid);
		LQIConfigurationEditDialog dialog = new LQIConfigurationEditDialog(
		        confdialog, null, lqiGrid.getActiveConfiguration());
		dialog.setVisible(true);
	}

	/**
	 * Saves the new LQI grid configuration.
	 */
	private void saveConfiguration() {

		LQIConfigurationsDialog owner = (LQIConfigurationsDialog) getOwner();
		if (infoPanel.checkMandatoryFields()) {
			if (checkLQITable()) {
				String configurationName = infoPanel.getInsertedName();
				if (owner.isConfigurationValidName(configurationName,
				        lqiGridConf)) {
					lqiGridConf.setName(configurationName);
					lqiGridConf.setSupplier(infoPanel.getInsertedSupplier());
					lqiGridConf.setThreshold(Double.parseDouble(infoPanel.getInsertedThreshold()));
					LQIGridConfiguration confFromTheTable = tableHelper.getLqiTableModel().getLQIGrid();
					lqiGridConf.setErrorCategories(confFromTheTable.getErrorCategories());
					lqiGridConf.setSeverities(confFromTheTable.getSeverities());
					((LQIConfigurationsDialog) getOwner()).save(lqiGridConf);
					close();
				} else {
					JOptionPane
					        .showMessageDialog(
					                this,
					                "A configuration having this name already exists. Please, choose a different name.",
					                "LQI Configuration Save",
					                JOptionPane.WARNING_MESSAGE);
				}
			} else {
				JOptionPane
				        .showMessageDialog(
				                this,
				                "Please, add at least an error category row and a severity column.",
				                "LQI Configuration Save",
				                JOptionPane.WARNING_MESSAGE);
			}

		} else {
			JOptionPane.showMessageDialog(this,
			        "Please, fill in all mandatory fields.",
			        "LQI Configuration Save", JOptionPane.WARNING_MESSAGE);
		}

		// TODO SAVE
		// try {
		// if (tableHelper.getLqiTableModel().isChanged()) {
		// lqiGridConf.updateConfiguration((LQIGridConfiguration)tableHelper
		// .getLqiTableModel().getLQIGrid().clone());
		// controller.saveLQIGridConfiguration(lqiGridConf);
		// // lqiGrid = (LQIGrid) tableHelper.getLqiTableModel().getLQIGrid()
		// // .clone();
		// tableHelper.getLqiTableModel().setChanged(false);
		// }
		// switchToIssuesAnnotsMode();
		// } catch (TransferException e) {
		// logger.error("Error while saving the LQI grid configuration.", e);
		// JOptionPane
		// .showMessageDialog(
		// this,
		// "An error has occurred while saving the LQI grid configuration",
		// "LQI Grid Error", JOptionPane.ERROR_MESSAGE);
		// } catch (CloneNotSupportedException e) {
		// // never happens
		// }
	}

	private boolean checkLQITable() {
		
		LQIGridConfiguration confFromTheTable = tableHelper.getLqiTableModel().getLQIGrid();
		return confFromTheTable.getSeverities() != null
		        && !confFromTheTable.getSeverities().isEmpty()
		        && confFromTheTable.getErrorCategories() != null
		        && !confFromTheTable.getErrorCategories().isEmpty();
	}


	private void discardConfiguration() {

		close();
	}

	private void close() {

		setVisible(false);
		dispose();
	}

	public void open(){
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnSave)) {
			saveConfiguration();
		} else if (e.getSource().equals(btnCancel)) {
			discardConfiguration();
		} else if (e.getSource().equals(btnAddRow)) {
			tableHelper.addErrorCategory();
		} else if (e.getSource().equals(btnDelRow)) {
			tableHelper.deleteSelectedErrorCategory();
		} else if (e.getSource().equals(btnAddCol)) {
			displayNewSeverityDialog();
		} else if (e.getSource().equals(btnMoveUp)) {
			tableHelper.moveSelectedRowUp();
		} else if (e.getSource().equals(btnMoveDown)) {
			tableHelper.moveSelectedRowDown();
		}

	}

	private void displayNewSeverityDialog() {

		SeverityColumnPropsDialog dialog = new SeverityColumnPropsDialog(this,
		        null, null);
		dialog.setVisible(true);
	}

	public boolean checkSeverityName(LQISeverity severity, String newName) {
		boolean good = true;
		if (lqiGridConf.getSeverities() != null) {
			for (LQISeverity sev : lqiGridConf.getSeverities()) {
				if (sev != severity && sev.getName().equals(newName)) {
					good = false;
					break;
				}
			}
		}
		return good;
	}

	public void createSeverityColumn(LQISeverity severity) {
		tableHelper.addSeverityColumn(severity);

	}

	public void severityChanged(LQISeverity oldSeverity, LQISeverity newSeverity) {
		tableHelper.severityColumnChanged(oldSeverity, newSeverity);

	}
	
	/**
	 * Checks if the shortcut defined by the key code and the modifiers is a
	 * reserved one.
	 * 
	 * @param keyCode
	 *            the key code
	 * @param modifiers
	 *            the modifiers
	 * @return <code>true</code> if it is a reserved shortcut;
	 *         <code>false</code> otherwise.
	 */
	public boolean isReservedShortcut(int keyCode, int[] modifiers) {
		return tableHelper.isReservedShortcut(keyCode, modifiers);
	}
	
	/**
	 * Save a new shortcut
	 * 
	 * @param keyCode
	 *            the shortcut key code
	 * @param modifiers
	 *            the modifiers.
	 */
	public void saveShortcut(int keyCode, int[] modifiers) {

		tableHelper.saveShortCut(keyCode, modifiers);

	}

}
