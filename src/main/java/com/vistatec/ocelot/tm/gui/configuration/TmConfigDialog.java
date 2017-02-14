package com.vistatec.ocelot.tm.gui.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig;
import com.vistatec.ocelot.tm.gui.constants.TmIconsConst;
import com.vistatec.ocelot.ui.TooltipCellRenderer;

/**
 * Modal dialog letting users view and change the current TM Configuration in
 * Ocelot. Users can perform following changes:
 * <ul>
 * <li>add a new TM;</li>
 * <li>remove an existing TM;</li>
 * <li>change the root directory for an existing TM;</li>
 * <li>change the penalty value of an existing TM;</li>
 * <li>enable/disable an existing TM;</li>
 * <li>change the ordering of configured TMs.</li>
 * </ul>
 */
public class TmConfigDialog extends JDialog implements Runnable, ActionListener {

	/** serial version UID. */
	private static final long serialVersionUID = -8852580443557452694L;

	/** Top panel buttons width constant. */
	private static final int TOP_BTN_WIDTH = 80;

	/** Top panel buttons height constant. */
	private static final int TOP_BTN_HEIGHT = 20;

	/** Arrow buttons width constant. */
	private static final int ARROW_BTN_WIDTH = 20;

	/** Arrow buttons height constant. */
	private static final int ARROW_BTN_HEIGHT = 20;

	/** Panels width constant. */
	private static final int PANELS_WIDTH = 600;

	/** Button panels height constant. */
	private static final int BUTTON_PANELS_HEIGHT = 50;

	/** Table panel height constant. */
	private static final int TABLE_PANEL_HEIGHT = 200;

	/** Dialog width constant. */
	private static final int DIALOG_WIDTH = 650;

	/** Dialog height constant. */
	private static final int DIALOG_HEIGHT = 370;

	/** The controller. */
	private TmGuiConfigController controller;

	/** Add button. */
	private JButton btnAdd;

	/** arrow up button. */
	private JButton btnMoveUp;

	/** arrow down button. */
	private JButton btnMoveDown;

	/** Change directory button. */
	private JButton btnChangeDir;

	/** Remove button. */
	private JButton btnRemove;
	
	/** Settings button. */
	private JButton btnSettings;

	/** Save button. */
	private JButton btnSave;

	/** Cancel button. */
	private JButton btnCancel;

	/** The table displaying configured TMs. */
	private JTable tmTable;

	/** The table model. */
	private TmTableModel tmTableModel;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            the controller.
	 * @param ownerFrame
	 *            the owner frame.
	 */
	public TmConfigDialog(TmGuiConfigController controller, Window ownerFrame) {
		super(ownerFrame);
		setModal(true);
		this.controller = controller;
	}

	/**
	 * Creates the button panel displayed on top of the dialog.
	 * 
	 * @return the top button panel.
	 */
	private Component createTopBtnPanel() {

		// Create Top Buttons
		Dimension btnDim = new Dimension(TOP_BTN_WIDTH, TOP_BTN_HEIGHT);
		final Dimension arrowBtnDim = new Dimension(ARROW_BTN_WIDTH,
				ARROW_BTN_HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		// create ADD button.
		btnAdd = new JButton("Add");
		ImageIcon icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(TmIconsConst.ADD_ICO)));
		configButton(btnAdd, btnDim, icon);

		// create Arrow UP button.
		btnMoveUp = new JButton();
		icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(TmIconsConst.ARROW_UP_ICO)));
		configButton(btnMoveUp, arrowBtnDim, icon);

		// create Arrow DOWN button.
		btnMoveDown = new JButton();
		icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(TmIconsConst.ARROW_DOWN_ICO)));
		configButton(btnMoveDown, arrowBtnDim, icon);
		btnDim = new Dimension(120, TOP_BTN_HEIGHT);

		//crate SETTINGS button.
		btnSettings = new JButton("Settings");
		icon = new ImageIcon(kit.createImage(Ocelot.class
                .getResource(TmIconsConst.SETTINGS_ICO)));
		configButton(btnSettings, btnDim, icon);
		
		// create CHANGE DIR button.
		btnChangeDir = new JButton("Change Dir");
		icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(TmIconsConst.CHANGE_DIR_ICO)));
		configButton(btnChangeDir, btnDim, icon);

		btnRemove = new JButton("Remove");
		icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(TmIconsConst.DELETE_ICO)));
		configButton(btnRemove, btnDim, icon);

		// Add buttons to top panel
		JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,
				20));
		topButtonPanel.setPreferredSize(new Dimension(PANELS_WIDTH,
				BUTTON_PANELS_HEIGHT));
		topButtonPanel.add(btnAdd);
		// topButtonPanel.add(btnNew);
		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(2, 20));
		topButtonPanel.add(separator);
		topButtonPanel.add(btnMoveUp);
		topButtonPanel.add(btnMoveDown);
		separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 20));
        topButtonPanel.add(separator);
        topButtonPanel.add(btnSettings);
		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(2, 20));
		topButtonPanel.add(separator);
//		topButtonPanel.add(btnChangeDir);
//		separator = new JSeparator(JSeparator.VERTICAL);
//		separator.setPreferredSize(new Dimension(2, 20));
//		topButtonPanel.add(separator);
		topButtonPanel.add(btnRemove);
		return topButtonPanel;
	}

	/**
	 * Configures a button, setting sizes and icon.
	 * 
	 * @param btn
	 *            the button to configure
	 * @param dim
	 *            the dimension
	 * @param icon
	 *            the icon
	 */
	private void configButton(JButton btn, Dimension dim, ImageIcon icon) {

		btn.setPreferredSize(dim);
		btn.setMaximumSize(dim);
		btn.setMinimumSize(dim);
		btn.setIcon(icon);
		btn.addActionListener(this);
	}

	/**
	 * Creates the main panel displaying the TM table.
	 * 
	 * @return the table panel.
	 */
	private Component createTablePanel() {

		// Create table
		tmTableModel = new TmTableModel(controller.getTmOrderedList());
		tmTable = new JTable(tmTableModel);
		tmTable.getTableHeader().setReorderingAllowed(false);
		tmTable.setDefaultRenderer(String.class, new TooltipCellRenderer());
		TableColumn dirPathColumn = tmTable.getColumnModel().getColumn(
				TmTableModel.TM_ROOT_DIR_PATH_COL);
		dirPathColumn.setPreferredWidth(500);
		TableColumn nameCol = tmTable.getColumnModel().getColumn(
				TmTableModel.TM_NAME_COL);
		nameCol.setPreferredWidth(250);
		// Create table scroll container
		JScrollPane tablePane = new JScrollPane(tmTable);
		tablePane.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		tablePane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tablePane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		tablePane.setPreferredSize(new Dimension(PANELS_WIDTH,
				TABLE_PANEL_HEIGHT));
		return tablePane;
	}

	/**
	 * Creates the buttons panel displayed on bottom of the dialog.
	 * 
	 * @return the bottom buttons panel.
	 */
	private Component createBottomBtnPanel() {

		// Create bottom buttons.
		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		// Add buttons to bottom panel
		JPanel bottomPanel = new JPanel(
				new FlowLayout(FlowLayout.RIGHT, 10, 20));
		bottomPanel.setPreferredSize(new Dimension(PANELS_WIDTH,
				BUTTON_PANELS_HEIGHT));
		bottomPanel.add(btnSave);
		bottomPanel.add(btnCancel);
		return bottomPanel;

	}

	/**
	 * run method. Configures the dialog, creates its components and makes it
	 * visible.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		setTitle("TM Configuration");
		setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		add(createTopBtnPanel(), BorderLayout.NORTH);
		add(createTablePanel(), BorderLayout.CENTER);
		add(createBottomBtnPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	/**
	 * It prompts a confirmation message to the user. If user confirms, it
	 * deletes the selected TM and removes the associated row from the table.
	 * 
	 */
	private void deleteSelectedTm() {

		int selRow = tmTable.getSelectedRow();
		if (selRow != -1) {
			int option = JOptionPane.showConfirmDialog(this,
					"Do you want to delete the selected TM?", "Delete TM",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				if (controller.deleteTm(tmTableModel.getTmAtRow(selRow)
						.getTmName())) {
					tmTableModel.deleteRow(selRow);
				}
			}
		}
	}

	/**
	 * Changes the root directory of the selected TM.
	 */
	private void changeSelTmDirectory() {

		TmConfig selTm = tmTableModel.getTmAtRow(tmTable.getSelectedRow());
		if (selTm != null) {
			JFileChooser fileChooser = new JFileChooser(new File(
					selTm.getTmDataDir()));
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setDialogTitle("Change TM Directory");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int option = fileChooser.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File selectedDir = fileChooser.getSelectedFile();
				if (controller.changeTmDirectory(selTm, selectedDir)) {
					selTm.setTmDataDir(selectedDir.getAbsolutePath());
					tmTableModel.fireTableRowsUpdated(tmTable.getSelectedRow(),
							tmTable.getSelectedRow());
					tmTableModel.setEdited(true);
				}
			}
		}
	}

	/**
	 * Closes this dialog.
	 */
	private void close() {

		controller.closeDialog();
		setVisible(false);
		dispose();
	}

	/**
	 * Upon user confirmation, it discards all changes applied to the TM
	 * configuration by restoring the previous one. Then it closes the dialog.
	 * If no changes have been applied, it simply closes the dialog.
	 * 
	 */
	private void cancel() {

		if (tmTableModel.isEdited()) {
			int option = JOptionPane
					.showConfirmDialog(
							this,
							"Changes to TM configuration will be discarded. Do you wish to continue?",
							"Cancel TM Configuration",
							JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				controller.cancel();
				close();
			}
		} else {
			close();
		}

	}

	/**
	 * Saves the current TM configuration, then it closes the dialog.
	 */
	private void save() {

		if (controller.save(tmTableModel.getTmList())) {
			close();
		}
	}
	
	private void addTm(){
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
//		fileChooser.setFileSelectionMode(mode);
		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return ".tmx, .TMX (Translation Memory eXchange)";
			}
			
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".tmx");
			}
		});
		fileChooser.setDialogTitle("Add tmx files");
		int option = fileChooser.showOpenDialog(this);
		if(option == JFileChooser.APPROVE_OPTION){
			File[] selectedFiles = fileChooser.getSelectedFiles();
			if(selectedFiles.length == 0){
				File selFile = fileChooser.getSelectedFile();
				if(selFile != null){
					selectedFiles = new File[1];
					selectedFiles[0] = selFile;
				}
			}
			try {
	            controller.createNewTm(selectedFiles);
            } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            } catch (TransferException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
		}
	}

	/**
	 * Manages the events triggered by buttons defined in the dialog.
	 * 
	 * @param e
	 *            the triggered event
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		JButton sourceBtn = (JButton) e.getSource();
		if (btnAdd.equals(sourceBtn)) {
			// add button pressed
//			controller.opentAddTmDialog();
			addTm();
		} else if (btnSave.equals(sourceBtn)) {
			// save button pressed
			save();

		} else if (btnCancel.equals(sourceBtn)) {
			// cancel button pressed
			cancel();
		} else if (btnSettings.equals(sourceBtn)){
            controller.openSettingsDialog();
        } else {
			// remaining button actions need a selected row. If no row is
			// selected, do nothing.
			int selRow = tmTable.getSelectedRow();
			// if a row is selected
			if (selRow != -1) {
				if (btnMoveUp.equals(sourceBtn)) {
					// arrow up button pressed
					if (tmTableModel.moveUpRow(selRow)) {
						tmTable.getSelectionModel().setSelectionInterval(
								selRow - 1, selRow - 1);
					}
				} else if (btnMoveDown.equals(sourceBtn)) {
					// arrow down button pressed
					if (tmTableModel.moveDownRow(selRow)) {
						tmTable.getSelectionModel().setSelectionInterval(
								selRow + 1, selRow + 1);
					}
				} else if (btnChangeDir.equals(sourceBtn)) {
					// change dir button pressed
					changeSelTmDirectory();
				} else if (btnRemove.equals(sourceBtn)) {
					// remove button pressed
					deleteSelectedTm();
				}
			}
		}

	}

	/**
	 * Adds a new row to the TM table.
	 * 
	 * @param newTm
	 *            the TM to be added.
	 */
	public void addNewTm(TmConfig newTm) {

		tmTableModel.addRow(newTm);

	}

}
