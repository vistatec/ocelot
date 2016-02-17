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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import org.apache.log4j.Logger;

import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.lqi.LQIGridController;
import com.vistatec.ocelot.lqi.LQIKeyEventHandler;
import com.vistatec.ocelot.lqi.LQIKeyEventManager;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGrid;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

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
	private final Logger logger = Logger.getLogger(LQIGridDialog.class);

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
	private JButton btnAddRow;

	/** Delete row button. */
	private JButton btnDeleteRow;

	/** Add new column button. */
	private JButton btnAddCol;

	/** Arrow up button. */
	private JButton btnUp;

	/** Arrow down button. */
	private JButton btnDown;

	/** The columns panel. */
	private JPanel colsPanel;

	/** The rows panel. */
	private JPanel rowsPanel;

	/** The grid scroll panel. */
	private JScrollPane scrollPane;

	/** The controller for this dialog. */
	private LQIGridController controller;

	/** The helper object for table management. */
	private LQIGridTableHelper tableHelper;

	/** The key event handler. */
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

		lqiGridKeyEventHandler = new LQIKeyEventHandler(controller,
		        getRootPane());
		LQIKeyEventManager.getInstance().addKeyEventHandler(
		        lqiGridKeyEventHandler);
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
				System.out.println("Component resized");
				setTableSize();
			}
		});

		tableHelper.getLqiTable().getColumnModel()
		        .addColumnModelListener(new TableColumnModelListener() {

			        /** number of columns added so far. */
			        private int colAddedNum;

			        @Override
			        public void columnSelectionChanged(ListSelectionEvent arg0) {
				        // do nothing
			        }

			        @Override
			        public void columnRemoved(TableColumnModelEvent arg0) {
				        // do nothing
			        }

			        @Override
			        public void columnMoved(TableColumnModelEvent arg0) {
				        // do nothing
			        }

			        @Override
			        public void columnMarginChanged(ChangeEvent e) {

				        // do nothing
			        }

			        @Override
			        public void columnAdded(TableColumnModelEvent e) {
				        System.out.println("Column added");

				        colAddedNum++;
				        if (colAddedNum == tableHelper.getLqiTableModel()
				                .getColumnCount()) {
					        colAddedNum = 0;
					        setTableSize();
				        }
			        }
		        });
		return scrollPane;
	}

	/**
	 * Sets the table size.
	 */
	private synchronized void setTableSize() {
		int height = tableHelper.getLqiTable().getTableHeader().getSize().height
		        + 5
		        + (tableHelper.getLqiTable().getRowHeight() * tableHelper
		                .getLqiTable().getRowCount());

		int width = DIALOG_WIDTH_PADDING + tableHelper.getTableWidth();
		scrollPane.setPreferredSize(new Dimension(width, height));
		tableHelper.initColorForColumns();
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
					int severityIndex = button.getSeverityColumn()
					        - tableHelper.getLqiTableModel()
					                .getSeverityColsStartIndex();
					double severity = lqiGrid.getSeverities()
					        .get(severityIndex).getScore();
					String severityName = lqiGrid.getSeverities()
					        .get(severityIndex).getName();
					String categoryName = tableHelper
					        .getLqiTable()
					        .getValueAt(
					                button.getCategoryRow(),
					                tableHelper.getLqiTableModel()
					                        .getErrorCategoryColumn())
					        .toString();
					controller.createNewLqi(categoryName, severity,
					        severityName);
				} else if (mode == CONFIG_MODE) {

					LQIErrorCategory selErrorCat = tableHelper
					        .getLqiTableModel().getErrorCategoryAtRow(
					                tableHelper.getLqiTable().getSelectedRow());
					if (selErrorCat != null) {
						int selCol = tableHelper.getLqiTable()
						        .getSelectedColumn();
						String severityName = tableHelper.getLqiTableModel()
						        .getSeverityNameForColumn(selCol);
						LQIShortCut currShortcut = selErrorCat
						        .getShortcut(severityName);
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
	 * Clears the commnet cell for a specific category.
	 * 
	 * @param category
	 *            the category name.
	 */
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
		rowsPanel = new JPanel();
		rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.X_AXIS));
		rowsPanel.setBorder(BorderFactory.createTitledBorder("Category rows"));
		rowsPanel.add(btnAddRow);
		rowsPanel.add(Box.createHorizontalStrut(10));
		rowsPanel.add(btnDeleteRow);
		rowsPanel.add(Box.createHorizontalStrut(10));
		rowsPanel.add(btnUp);
		rowsPanel.add(Box.createHorizontalStrut(10));
		rowsPanel.add(btnDown);

		colsPanel = new JPanel();
		colsPanel.setLayout(new BoxLayout(colsPanel, BoxLayout.X_AXIS));
		colsPanel.setPreferredSize(new Dimension(120, 50));
		colsPanel.setBorder(BorderFactory
		        .createTitledBorder("Severity Columns"));
		colsPanel.add(btnAddCol);
		bottomPanel.add(Box.createRigidArea(new Dimension(1, 50)));
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(btnConfig);
		bottomPanel.add(rowsPanel);
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(colsPanel);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(btnSave);
		bottomPanel.add(Box.createHorizontalStrut(10));
		bottomPanel.add(btnCancel);
		bottomPanel.add(btnClose);
		bottomPanel.add(Box.createHorizontalStrut(10));
		if (mode == ISSUES_ANNOTS_MODE) {
			btnCancel.setVisible(false);
			btnSave.setVisible(false);
			colsPanel.setVisible(false);
			rowsPanel.setVisible(false);
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
		btnAddRow = new JButton(icon);
		configIconButton(btnAddRow);
		btnAddCol = new JButton(icon);
		configIconButton(btnAddCol);
		icon = new ImageIcon(kit.createImage(getClass().getResource(
		        "remove.png")));
		btnDeleteRow = new JButton(icon);
		configIconButton(btnDeleteRow);
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
		rowsPanel.setVisible(false);
		colsPanel.setVisible(false);
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
	 * Switches to the configuration mode.
	 */
	private void switchToConfigMode() {

		btnCancel.setVisible(true);
		btnSave.setVisible(true);
		rowsPanel.setVisible(true);
		colsPanel.setVisible(true);
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
		} else if (e.getSource().equals(btnAddRow)) {
			addErrorCategory();
		} else if (e.getSource().equals(btnDeleteRow)) {
			deleteSelectedErrorCategory();
		} else if (e.getSource().equals(btnAddCol)) {
			addSeverity();
		} else if (e.getSource().equals(btnUp)) {
			tableHelper.moveSelectedRowUp();
		} else if (e.getSource().equals(btnDown)) {
			tableHelper.moveSelectedRowDown();
		}

	}

	/**
	 * Adds a severity column.
	 */
	private void addSeverity() {
		tableHelper.displayNewSeverityDialog();

	}

	/**
	 * Checks if an issue can be created.
	 * 
	 * @return <code>true</code> if an issue can be created; <code>false</code>
	 *         otherwise
	 */
	public boolean canCreateIssue() {

		return mode == ISSUES_ANNOTS_MODE && !isEditing();
	}

	/**
	 * Checks if there's a text field being edited in Ocelot.
	 * 
	 * @return <code>true</code> if a text field exists being edited;
	 *         <code>false</code> otherwise.
	 */
	private boolean isEditing() {
		return tableHelper.getLqiTable().getEditingColumn() == tableHelper
		        .getLqiTableModel().getCommentColumn()
		        || controller.isOcelotEditing();
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
	 * @param oldSeverity
	 *            the old severity.
	 * @param newSeverity
	 *            the new severity.
	 */
	public void severityChanged(LQISeverity oldSeverity, LQISeverity newSeverity) {

		tableHelper.severityColumnChanged(oldSeverity, newSeverity);
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

	/**
	 * Gets the comment for a specific category.
	 * 
	 * @param errorCategory
	 *            the error category
	 * @return the comment
	 */
	public String getCommentForCategory(String errorCategory) {

		return tableHelper.getLqiTableModel().getCommentByCategory(
		        errorCategory);
	}

	/**
	 * Creates a new severity column for the given severity.
	 * 
	 * @param severity
	 *            the severity.
	 */
	public void createSeverityColumn(LQISeverity severity) {

		tableHelper.addSeverityColumn(severity);
	}

	/**
	 * Checks if a name chosen for a severity is a valid one. A severity name is
	 * valid if no other severity has the same name.
	 * 
	 * @param severity
	 *            the severity
	 * @param newSeverityName
	 *            the new name.
	 * @return <code>true</code> if the new name is a valid one;
	 *         <code>false</code> otherwise
	 */
	public boolean checkSeverityName(LQISeverity severity,
	        String newSeverityName) {

		boolean validName = true;
		LQIGrid modelGrid = tableHelper.getLqiTableModel().getLQIGrid();
		if (modelGrid.getSeverities() != null) {
			for (LQISeverity sev : modelGrid.getSeverities()) {
				if (!sev.equals(severity)
				        && sev.getName().equals(newSeverityName)) {
					validName = false;
					break;
				}
			}
		}
		return validName;
	}
}
