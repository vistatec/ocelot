package com.vistatec.ocelot.tm.gui.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.config.xml.TmManagement.TmConfig;

public class TmConfigDialog extends JDialog implements Runnable, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8852580443557452694L;

	private static final String ARROW_UP_ICO = "arrow-up.png";

	private static final String ARROW_DOWN_ICO = "arrow-down.png";

	private static final String DELETE_ICO = "delete.png";

	private static final String CHANGE_DIR_ICO = "change-dir.png";

	private static final String ADD_ICO = "add.png";

	private static final int TOP_BTN_WIDTH = 80;

	private static final int TOP_BTN_HEIGHT = 20;

	private static final int ARROW_BTN_WIDTH = 20;

	private static final int ARROW_BTN_HEIGHT = 20;

	private TmGuiConfigController controller;

	private Logger log = Logger.getLogger(TmConfigDialog.class);

	private Window ownerFrame;

	private JButton btnAdd;

	private JButton btnNew;

	private JButton btnMoveUp;

	private JButton btnMoveDown;

	private JButton btnChangeDir;

	private JButton btnRemove;

	private JButton btnSave;

	private JButton btnCancel;

	private JTable tmTable;

	private TmTableModel tmTableModel;

	// private JScrollPane tablePane;

	public TmConfigDialog(TmGuiConfigController controller, Window ownerFrame) {
		super(ownerFrame);
		setModal(true);
		this.controller = controller;
		this.ownerFrame = ownerFrame;
	}

	private Component createTopBtnPanel() {

		// Create Top Buttons
		Dimension btnDim = new Dimension(TOP_BTN_WIDTH, TOP_BTN_HEIGHT);
		final Dimension arrowBtnDim = new Dimension(ARROW_BTN_WIDTH,
				ARROW_BTN_HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		btnAdd = new JButton("Add");
		ImageIcon icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(ADD_ICO)));
		configButton(btnAdd, btnDim, icon);

		btnNew = new JButton("New");
		icon = new ImageIcon(kit.createImage(Ocelot.class.getResource(ADD_ICO)));
		configButton(btnNew, btnDim, icon);

		btnMoveUp = new JButton();

		icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(ARROW_UP_ICO)));
		configButton(btnMoveUp, arrowBtnDim, icon);

		btnMoveDown = new JButton();
		icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(ARROW_DOWN_ICO)));
		configButton(btnMoveDown, arrowBtnDim, icon);
		btnDim = new Dimension(120, TOP_BTN_HEIGHT);
		btnChangeDir = new JButton("Change Dir");
		icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(CHANGE_DIR_ICO)));
		configButton(btnChangeDir, btnDim, icon);

		btnRemove = new JButton("Remove");
		icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(DELETE_ICO)));
		configButton(btnRemove, btnDim, icon);

		// Add buttons to top panel
		JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,
				20));
		topButtonPanel.setPreferredSize(new Dimension(600, 50));
		topButtonPanel.add(btnAdd);
//		topButtonPanel.add(btnNew);
		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(2, 20));
		topButtonPanel.add(separator);
		topButtonPanel.add(btnMoveUp);
		topButtonPanel.add(btnMoveDown);
		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(2, 20));
		topButtonPanel.add(separator);
		topButtonPanel.add(btnChangeDir);
		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(2, 20));
		topButtonPanel.add(separator);
		topButtonPanel.add(btnRemove);
		return topButtonPanel;
	}

	private void configButton(JButton btn, Dimension dim, ImageIcon icon) {

		btn.setPreferredSize(dim);
		btn.setMaximumSize(dim);
		btn.setMinimumSize(dim);
		btn.setIcon(icon);
		btn.addActionListener(this);
	}

	private Component createTablePanel() {

		// Create table
		tmTableModel = new TmTableModel(controller.getTmOrderedList());
		tmTable = new JTable(tmTableModel);
		// TODO delete, only for test purpose
		// tmTableModel = new TmTableModel(createTmList());
		// tmTable = new JTable(tmTableModel);
		tmTable.getTableHeader().setReorderingAllowed(false);
		tmTable.setDefaultRenderer(String.class, new TooltipCellRenderer());
		TableColumn dirPathColumn = tmTable.getColumnModel().getColumn(
				TmTableModel.TM_ROOT_DIR_PATH_COL);
		dirPathColumn.setPreferredWidth(500);
		TableColumn nameCol = tmTable.getColumnModel().getColumn(
				TmTableModel.TM_NAME_COL);
		nameCol.setPreferredWidth(250);
		// TableColumn delColumn =
		// tmTable.getColumnModel().getColumn(TmTableModel.TM_DEL_COL);
		// Toolkit kit = Toolkit.getDefaultToolkit();
		// ImageIcon icon = new
		// ImageIcon(kit.createImage(Ocelot.class.getResource(DELETE_ICO)));
		// delColumn.setCellRenderer(new ImageCellRenderer(icon, "Delete"));
		// delColumn.setMaxWidth(20);
		// delColumn.setWidth(20);
		// TableColumn changeDirColumn =
		// tmTable.getColumnModel().getColumn(TmTableModel.TM_CHANGE_DIR_COL);
		// icon = new
		// ImageIcon(kit.createImage(Ocelot.class.getResource(CHANGE_DIR_ICO)));
		// changeDirColumn.setCellRenderer(new ImageCellRenderer(icon,
		// "Change directory"));
		// changeDirColumn.setMaxWidth(20);
		// changeDirColumn.setWidth(20);
		// Create table scroll container
		JScrollPane tablePane = new JScrollPane(tmTable);
		tablePane.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		tablePane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tablePane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		tablePane.setPreferredSize(new Dimension(600, 200));
		return tablePane;
	}

	private Component createBottomBtnPanel() {

		// Create bottom buttons.
		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);

		// Add buttons to bottom panel
		JPanel bottomPanel = new JPanel(
				new FlowLayout(FlowLayout.RIGHT, 10, 20));
		bottomPanel.setPreferredSize(new Dimension(600, 50));
		bottomPanel.add(btnSave);
		bottomPanel.add(btnCancel);
		return bottomPanel;

	}

	@Override
	public void run() {

		setTitle("TM Configuration");
		setPreferredSize(new Dimension(650, 370));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		add(createTopBtnPanel(), BorderLayout.NORTH);
		add(createTablePanel(), BorderLayout.CENTER);
		add(createBottomBtnPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(ownerFrame);
		setVisible(true);
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image icon = kit.createImage(Ocelot.class.getResource("logo64.png"));
		frame.setIconImage(icon);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		TmConfigDialog dialog = new TmConfigDialog(null, frame);
		SwingUtilities.invokeLater(dialog);
	}

	private void deleteSelectedRow() {

		int selRow = tmTable.getSelectedRow();
		if (selRow != -1) {
			int option = JOptionPane.showConfirmDialog(this,
					"Do you want to delete the selected TM?", "Remove TM",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				TmConfig deletedTm = tmTableModel.deleteRow(selRow);
				controller.handleTmDeleted(deletedTm);
			}
		}
	}

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
				selTm.setTmDataDir(fileChooser.getSelectedFile()
						.getAbsolutePath());
				tmTableModel.fireTableDataChanged();
				controller.handleTmChangedDir(selTm);
			}
		}
	}

	private void close() {

		controller.closeDialog();
		setVisible(false);
		dispose();
	}

	private void save() {

		try {
			controller.deleteTmList();
			controller.changeTmDirectory();
			controller.setTmOrderedList(tmTableModel.getTmList());
			close();
		} catch (IOException | TransferException e) {
			log.trace("Error while saving the TM configuration", e);
			e.printStackTrace();

			JOptionPane.showMessageDialog(this,
					"An error occurred while saving the TM configuration",
					"TM Error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			log.trace("Error while saving the TM configuration", e);
e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"An error occurred while saving the TM configuration",
					"TM Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public List<TmConfig> getNewConfiguration() {

		return tmTableModel.getTmList();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JButton sourceBtn = (JButton) e.getSource();
		if (btnAdd.equals(sourceBtn)) {
			controller.opentCreateTmDialog();
		} else if (btnNew.equals(sourceBtn)) {
			controller.opentCreateTmDialog();
		} else if (btnSave.equals(sourceBtn)) {
			save();

		} else if (btnCancel.equals(sourceBtn)) {
			close();
		} else {
			int selRow = tmTable.getSelectedRow();
			if (selRow != -1) {
				if (btnMoveUp.equals(sourceBtn)) {
					if (tmTableModel.moveUpRow(selRow)) {
						tmTable.getSelectionModel().setSelectionInterval(
								selRow - 1, selRow - 1);
					}
				} else if (btnMoveDown.equals(sourceBtn)) {
					if (tmTableModel.moveDownRow(selRow)) {
						tmTable.getSelectionModel().setSelectionInterval(
								selRow + 1, selRow + 1);
					}
				} else if (btnChangeDir.equals(sourceBtn)) {
					changeSelTmDirectory();
				} else if (btnRemove.equals(sourceBtn)) {
					deleteSelectedRow();
				}
			}
		}

	}

	public void addNewTm(TmConfig newTm) {

		tmTableModel.addRow(newTm);

	}

	// class TableButtonListener extends MouseAdapter {
	//
	// @Override
	// public void mouseClicked(MouseEvent e) {
	// if(e.getButton() == MouseEvent.BUTTON1){
	// int clickedCol = tmTable.getSelectedColumn();
	// if(clickedCol == TmTableModel.TM_DEL_COL){
	// deleteSelectedRow();
	// } else if (clickedCol == TmTableModel.TM_CHANGE_DIR_COL){
	// changeSelTmDirectory();
	// }
	// }
	//
	// }
	//
	// }

}
