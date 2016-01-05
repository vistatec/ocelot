package com.vistatec.ocelot.lqi.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.lqi.LQIGridController;
import com.vistatec.ocelot.lqi.constants.LQIConstants;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory.LQIShortCut;
import com.vistatec.ocelot.lqi.model.LQIGrid;

/**
 * Dialog displaying the LQI grid.
 */
public class LQIGridDialog extends JDialog implements ActionListener, Runnable {

	/** The serial version UID. */
	private static final long serialVersionUID = -8889391524131724874L;

	/** Issues annotations mode constant. */
	public static final int ISSUES_ANNOTS_MODE = 0;

	/** Configuration mode constant. */
	public static final int CONFIG_MODE = 1;

	/** Dialog width padding. */
	private static final int DIALOG_WIDTH_PADDING = 50;

	/** Dialog title. */
	private static final String TITLE = "Quality Issues Grid";

	/** Dialog title suffix displayed in Configuration mode. */
	private static final String TITLE_CONF_SUFFIX = " - Configuration";

	/** The logger for this class. */
	private static final Logger logger = Logger.getLogger(LQIGridDialog.class);

	/**
	 * Dialog mode. It can be set to either <code>ISSUES_ANNOTS_MODE</code> or
	 * <code>CONFIG_MODE</code> value.
	 */
	private int mode;

	/** the LQI grid object. */
	private LQIGrid lqiGrid;

	/** Configure button. */
	private JButton btnConfig;

	/** Close button. */
	private JButton btnClose;

	/** Save button. */
	private JButton btnSave;

	/** Cancel button. */
	private JButton btnCancel;

	/** Add new row button. */
	private JButton btnAdd;

	/** Delete row button. */
	private JButton btnDelete;

	/** Arrow up button. */
	private JButton btnUp;

	/** Arrow down button. */
	private JButton btnDown;

	/** The grid scroll panel. */
	private JScrollPane scrollPane;

	/** The controller for this dialog. */
	private LQIGridController controller;

	/** The helper object for table management. */
	private LQIGridTableHelper tableHelper;

	private LQIKeyEventHandler lqiGridKeyEventHandler;
	
	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner window.
	 * @param controller
	 *            the controller.
	 * @param lqiGrid
	 *            the LQI grid object.
	 */
	public LQIGridDialog(JFrame owner, LQIGridController controller,
	        LQIGrid lqiGrid) {
		this(owner, controller, lqiGrid, ISSUES_ANNOTS_MODE);
	}

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner window.
	 * @param controller
	 *            the controller.
	 * @param lqiGrid
	 *            the LQI grid object.
	 * @param mode
	 *            the dialog mode to set
	 */
	public LQIGridDialog(Window owner, LQIGridController controller,
	        LQIGrid lqiGrid, int mode) {

		super(owner);
		setModal(false);
		this.lqiGrid = lqiGrid;
		this.controller = controller;
		this.mode = mode;
	}

	/**
	 * Initializes the dialog.
	 */
	private void init() {

//		LQIKeyEventHandler ocelotKeyEventHandler = new LQIKeyEventHandler(controller, ((JFrame)getOwner()).getRootPane());
		lqiGridKeyEventHandler = new LQIKeyEventHandler(controller, getRootPane());
//		keyEventManager.addKeyEventHandler(ocelotKeyEventHandler);
		LQIKeyEventManager.getInstance().addKeyEventHandler(lqiGridKeyEventHandler);
		lqiGridKeyEventHandler.load(lqiGrid);
		tableHelper = new LQIGridTableHelper();
		String title = TITLE;
		if (mode == CONFIG_MODE) {
			title = title + TITLE_CONF_SUFFIX;
		}
		setTitle(title);
		setResizable(false);
		add(getCenterComponent(), BorderLayout.CENTER);
		add(getBottomComponent(), BorderLayout.SOUTH);
		// pack();
		setTableSize();
		setLocationRelativeTo(getOwner());
	}

	/**
	 * Gets the component to be displayed in the center of the dialog.
	 * 
	 * @return the component to be displayed in the center of the dialog.
	 */
	private Component getCenterComponent() {

		LQIGrid clonedGrid = null;
		try {
			clonedGrid = (LQIGrid) lqiGrid.clone();
		} catch (CloneNotSupportedException e) {
			// never happens
		}
		scrollPane = new JScrollPane(tableHelper.createLQIGridTable(clonedGrid,
		        mode, getGridButtonAction()));
		tableHelper.getLqiTable().addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				setTableSize();
			}
		});
		return scrollPane;
	}

	private void setTableSize() {
		int height = tableHelper.getLqiTable().getTableHeader().getSize().height
		        + 5
		        + (tableHelper.getLqiTable().getRowHeight() * tableHelper
		                .getLqiTable().getRowCount());

		int width = DIALOG_WIDTH_PADDING + tableHelper.getTableWidth();
		scrollPane.setPreferredSize(new Dimension(width, height));
		tableHelper.configureTable(getGridButtonAction());
		pack();

	}

	/**
	 * Gets the action for the LQI grid buttons.
	 * 
	 * @return the action for the LQI grid buttons.
	 */
	private Action getGridButtonAction() {

		Action annotationAncion = new AbstractAction() {

			private static final long serialVersionUID = -57792331199984334L;

			@Override
			public void actionPerformed(ActionEvent e) {

				LQIGridButton button = (LQIGridButton) e.getSource();
				if (mode == ISSUES_ANNOTS_MODE) {
					double severity = 0;
					if (button.getSeverityColumn() == tableHelper
					        .getLqiTableModel().getMinorScoreColumn()) {
						severity = lqiGrid.getMinorScore();
					} else if (button.getSeverityColumn() == tableHelper
					        .getLqiTableModel().getSeriousScoreColumn()) {
						severity = lqiGrid.getSeriousScore();
					} else if (button.getSeverityColumn() == tableHelper
					        .getLqiTableModel().getCriticalScoreColumn()) {
						severity = lqiGrid.getCriticalScore();
					}
					String categoryName = tableHelper
					        .getLqiTable()
					        .getValueAt(
					                button.getCategoryRow(),
					                tableHelper.getLqiTableModel()
					                        .getErrorCategoryColumn())
					        .toString();
//					Object commentObj = tableHelper.getLqiTable().getValueAt(
//					        button.getCategoryRow(),
//					        tableHelper.getLqiTableModel().getCommentColumn());
//					String comment = null;
//					if (commentObj != null) {
//						comment = commentObj.toString();
//					}
//					createNewLqi(categoryName, severity, comment);
					controller.createNewLqi(categoryName, severity);
				} else if (mode == CONFIG_MODE) {

					LQIErrorCategory selErrorCat = tableHelper
					        .getLqiTableModel().getErrorCategoryAtRow(
					                tableHelper.getLqiTable().getSelectedRow());
					if (selErrorCat != null) {
						int selCol = tableHelper.getLqiTable()
						        .getSelectedColumn();
						String severityName = tableHelper.getLqiTableModel()
						        .getSeverityNameForColumn(selCol);
						LQIShortCut currShortcut = null;
						if (selCol == tableHelper.getLqiTableModel()
						        .getCriticalScoreColumn()) {
							currShortcut = selErrorCat.getCriticalShortcut();
						} else if (selCol == tableHelper.getLqiTableModel()
						        .getMinorScoreColumn()) {
							currShortcut = selErrorCat.getMinorShortcut();
						} else if (selCol == tableHelper.getLqiTableModel()
						        .getSeriousScoreColumn()) {
							currShortcut = selErrorCat.getSeriousShortcut();
						}
						ShortCutDialog shortCutDialog = new ShortCutDialog(
						        LQIGridDialog.this, selErrorCat.getName()
						                + " - " + severityName, currShortcut);
						shortCutDialog.run();
					}
				}
				if (tableHelper.getLqiTable().getCellEditor() != null) {
					tableHelper.getLqiTable().getCellEditor().stopCellEditing();
					LQIGridDialog.this.requestFocus();
				}
			}
		};

		return annotationAncion;
	}
	
	public boolean isReservedShortcut(int keyCode, int[] modifiers){
		return tableHelper.isReservedShortcut(keyCode, modifiers);
	}

	public void clearCommentCellForCategory(String category) {
		
		tableHelper.getLqiTableModel().clearCommentForCategory(category);
    }

	/**
	 * Gets the component to be displayed at the bottom of the dialog.
	 * 
	 * @return the component to be displayed at the bottom of the dialog.
	 */
	private Component getBottomComponent() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		createAndConfigureButtons();
		bottomPanel.add(Box.createRigidArea(new Dimension(1, 50)));
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(btnConfig);
		bottomPanel.add(btnAdd);
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(btnDelete);
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(btnUp);
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(btnDown);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(btnSave);
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(btnCancel);
		bottomPanel.add(btnClose);
		bottomPanel.add(Box.createHorizontalStrut(10));
		if (mode == ISSUES_ANNOTS_MODE) {
			btnCancel.setVisible(false);
			btnSave.setVisible(false);
			btnAdd.setVisible(false);
			btnDelete.setVisible(false);
			btnUp.setVisible(false);
			btnDown.setVisible(false);
		} else {
			btnConfig.setVisible(false);
			btnClose.setVisible(false);
		}

		return bottomPanel;
	}

	/**
	 * Creates and configures all the buttons for this dialog.
	 */
	private void createAndConfigureButtons() {
		btnConfig = new JButton("Configure");
		btnConfig.addActionListener(this);
		btnClose = new JButton("Close");
		btnClose.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnSave = new JButton("Save");
		btnSave.addActionListener(this);
		Toolkit kit = Toolkit.getDefaultToolkit();
		ImageIcon icon = new ImageIcon(kit.createImage(getClass().getResource(
		        "add.png")));
		btnAdd = new JButton(icon);
		configIconButton(btnAdd);
		icon = new ImageIcon(kit.createImage(getClass().getResource(
		        "remove.png")));
		btnDelete = new JButton(icon);
		configIconButton(btnDelete);
		icon = new ImageIcon(kit.createImage(getClass().getResource(
		        "arrow-up.png")));
		btnUp = new JButton(icon);
		configIconButton(btnUp);
		icon = new ImageIcon(kit.createImage(getClass().getResource(
		        "arrow-down.png")));
		btnDown = new JButton(icon);
		configIconButton(btnDown);
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

	/**
	 * Switches to the Issues Annotation mode.
	 */
	private void switchToIssuesAnnotsMode() {

		btnCancel.setVisible(false);
		btnSave.setVisible(false);
		btnAdd.setVisible(false);
		btnDelete.setVisible(false);
		btnUp.setVisible(false);
		btnDown.setVisible(false);
		btnClose.setVisible(true);
		btnConfig.setVisible(true);
		setTitle(TITLE);
		repaint();
		mode = ISSUES_ANNOTS_MODE;
		tableHelper.getLqiTableModel().setMode(mode);
		tableHelper.configureTable(getGridButtonAction());
		setTableSize();
	}

	/**
	 * Switches to teh configuration mode.
	 */
	private void switchToConfigMode() {

		btnCancel.setVisible(true);
		btnSave.setVisible(true);
		btnAdd.setVisible(true);
		btnDelete.setVisible(true);
		btnUp.setVisible(true);
		btnDown.setVisible(true);
		btnClose.setVisible(false);
		btnConfig.setVisible(false);
		setTitle(TITLE + TITLE_CONF_SUFFIX);
		repaint();
		mode = CONFIG_MODE;
		tableHelper.getLqiTableModel().setMode(mode);
		tableHelper.configureTable(getGridButtonAction());
		setTableSize();
		

	}

	/**
	 * Saves the new LQI grid configuration.
	 */
	private void saveConfiguration() {

		try {
			if (tableHelper.getLqiTableModel().isChanged()) {
				controller.saveLQIGridConfiguration(tableHelper
				        .getLqiTableModel().getLQIGrid());
				lqiGrid = (LQIGrid) tableHelper.getLqiTableModel().getLQIGrid()
				        .clone();
				tableHelper.getLqiTableModel().setChanged(false);
			}
			switchToIssuesAnnotsMode();
		} catch (TransferException e) {
			logger.error("Error while saving the LQI grid configuration.", e);
			JOptionPane
			        .showMessageDialog(
			                this,
			                "An error has occurred while saving the LQI grid configuration",
			                "LQI Grid Error", JOptionPane.ERROR_MESSAGE);
		} catch (CloneNotSupportedException e) {
			// never happens
		}
	}

	/**
	 * Discards the changes applied to the LQI grid configuration.
	 */
	private void discardConfiguration() {

		boolean canSwitchMode = true;
		if (tableHelper.getLqiTableModel().isChanged()) {
			int option = JOptionPane
			        .showConfirmDialog(
			                this,
			                "All changes to the grid will be discarded. Do you wish to continue?",
			                "LQI Grid Discard Changes",
			                JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				try {
					tableHelper.replaceConfiguration((LQIGrid) lqiGrid.clone());

				} catch (CloneNotSupportedException e) {
					// never happens
				}
			} else {
				canSwitchMode = false;
			}
		}
		if (canSwitchMode) {
			switchToIssuesAnnotsMode();
		}
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		LQIGridDialog dialog = new LQIGridDialog(frame, null,
		        LQIConstants.getDefaultLQIGrid(), ISSUES_ANNOTS_MODE);
		SwingUtilities.invokeLater(dialog);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnConfig)) {
			switchToConfigMode();
		} else if (e.getSource().equals(btnSave)) {
			saveConfiguration();
		} else if (e.getSource().equals(btnCancel)) {
			discardConfiguration();
		} else if (e.getSource().equals(btnClose)) {
			close();
		} else if (e.getSource().equals(btnAdd)) {
			addErrorCategory();
		} else if (e.getSource().equals(btnDelete)) {
			deleteSelectedErrorCategory();
		} else if (e.getSource().equals(btnUp)) {
			tableHelper.moveSelectedRowUp();
		} else if (e.getSource().equals(btnDown)) {
			tableHelper.moveSelectedRowDown();
		}

	}
	
	public boolean canCreateIssue(){
		
		return mode == ISSUES_ANNOTS_MODE && !isEditing();
	}
	
	private boolean isEditing(){
		return tableHelper.getLqiTable().getEditingColumn() == tableHelper.getLqiTableModel().getCommentColumn() || controller.isOcelotEditing();
//		return tableHelper.getLqiTable().getColumnModel().getColumn(tableHelper.getLqiTableModel().getCommentColumn()).getCellEditor().
	}

	/**
	 * Adds a new error category (i.e. a new row in the LQI grid).
	 */
	private void addErrorCategory() {

		if (mode == CONFIG_MODE) {
			tableHelper.addErrorCategory();
		}
	}

	/**
	 * Deletes the selected error category.
	 */
	private void deleteSelectedErrorCategory() {

		if (mode == CONFIG_MODE) {
			tableHelper.deleteSelectedErrorCategory();
		}
	}

	/**
	 * Handles the event a severity score has been changed.
	 * 
	 * @param severityScore
	 *            the severity score.
	 * @param severityName
	 *            the severity name.
	 */
	public void severityScoreChanged(String severityScore, String severityName) {
		tableHelper.severityScoreChanged(severityScore, severityName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		init();
		setVisible(true);
	}

	/**
	 * Closes the dialog and disposes it.
	 */
	private void close() {

		lqiGridKeyEventHandler.removeActions(lqiGrid);
		setVisible(false);
		controller.close();
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


	public String getCommentForCategory(String errorCategory) {

		return tableHelper.getLqiTableModel().getCommentByCategory(
		        errorCategory);
	}
}
