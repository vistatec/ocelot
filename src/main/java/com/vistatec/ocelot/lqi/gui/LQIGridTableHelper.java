package com.vistatec.ocelot.lqi.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;

import com.vistatec.ocelot.PlatformSupport;
import com.vistatec.ocelot.lqi.LQIKeyEventManager;
import com.vistatec.ocelot.lqi.constants.LQIConstants;
import com.vistatec.ocelot.lqi.constants.ShortCutConstants;
import com.vistatec.ocelot.lqi.gui.editor.CategoryCellEditor;
import com.vistatec.ocelot.lqi.gui.editor.FloatCellEditor;
import com.vistatec.ocelot.lqi.gui.editor.LQIGridButtonEditor;
import com.vistatec.ocelot.lqi.gui.editor.TextAreaTableCellEditor;
import com.vistatec.ocelot.lqi.gui.renderer.ColorCellRenderer;
import com.vistatec.ocelot.lqi.gui.renderer.ColorHeaderCellRenderer;
import com.vistatec.ocelot.lqi.gui.renderer.LQIGridButtonRenderer;
import com.vistatec.ocelot.lqi.gui.renderer.TextAreaColorCellRenderer;
import com.vistatec.ocelot.lqi.gui.window.LQIConfigurationEditDialog;
import com.vistatec.ocelot.lqi.gui.window.SeverityColumnPropsDialog;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

/**
 * Helper class for the LQI grid table management.
 */
public class LQIGridTableHelper implements MouseListener, TableCellListener,
        ActionListener {

	
	
	/** Minor severity column color. */
	private static final Color MINOR_COL_COLOR = new Color(217, 234, 211);

	// /** Serious severity column color. */
	// private static final Color SERIOUS_COL_COLOR = new Color(252, 229, 205);

	/** Critical severity column color. */
	private static final Color CRITICAL_COL_COLOR = new Color(244, 204, 204);

	/** LQI grid table row height constant. */
	private static final int ROW_HEIGHT = 30;

	/** LQI grid table column width constant. */
	private static final int COLUMN_WITDH = 150;

	/** LQI grid table Comment column width constant. */
	private static final int COMMENT_COLUMN_WITDH = 250;
	
	/** LQI grid table Weight column width constant. */
	private static final int WEIGHT_COLUMN_WIDTH = 60;

	/** The LQI grid table. */
	private JTable lqiTable;

	/** The LQI grid table model. */
	private LQIGridTableModel lqiTableModel;

	/** Object handling keyboard shortcuts. */
	private LQIKeyEventManager keyEventManager;

	/** List of all shortcut used in the LQI grid. */
	private List<KeyStroke> shortcutsInUse;

	/** table severity columns colors. */
	private Color[] columnColors;

	/** The severity columns pop up menu. */
	private JPopupMenu severityMenu;

	/** The severity properties menu item. */
	private SeverityMenuItem mnuSeverityProps;

	/** The delete severity menu item. */
	private SeverityMenuItem mnuDelSeverity;

	private PlatformSupport platformSupport;

	/**
	 * Constructor.
	 */
	public LQIGridTableHelper(PlatformSupport platformSupport) {
	    this.platformSupport = platformSupport;
		this.keyEventManager = LQIKeyEventManager.getInstance();
		severityMenu = new JPopupMenu();
		mnuSeverityProps = new SeverityMenuItem("Edit", SeverityMenuItem.EDIT);
		mnuSeverityProps.addActionListener(this);
		severityMenu.add(mnuSeverityProps);
		mnuDelSeverity = new SeverityMenuItem("Delete", SeverityMenuItem.DELETE);
		mnuDelSeverity.addActionListener(this);
		severityMenu.add(mnuDelSeverity);

	}

	/**
	 * Create the LQI grid table.
	 * 
	 * @param lqiGridObj
	 *            the LQI grid object.
	 * @param mode
	 *            the mode.
	 * @param gridButtonAction
	 *            the action for LQI grid buttons.
	 * @return the LQI grid table.
	 */
	public JTable createLQIGridTable(LQIGridConfiguration lqiGridObj, int mode,
	        final Action gridButtonAction) {

		initUsedShortcutList(lqiGridObj);
		lqiTableModel = new LQIGridTableModel(lqiGridObj, mode);
		initColorForColumns();
		lqiTableModel.addTableCellListener(this);
		lqiTable = new JTable(lqiTableModel);
		lqiTable.setGridColor(Color.LIGHT_GRAY);
		lqiTable.getTableHeader().setReorderingAllowed(false);
		lqiTable.getTableHeader().addMouseListener(this);
		return lqiTable;
	}

	/**
	 * Initializes the list containing used shortcuts.
	 * 
	 * @param lqiGridObj
	 *            the LQI grid.
	 */
	private void initUsedShortcutList(LQIGridConfiguration lqiGridObj) {

		shortcutsInUse = new ArrayList<KeyStroke>();
		if (lqiGridObj != null && lqiGridObj.getErrorCategories() != null) {
			for (LQIErrorCategory errCat : lqiGridObj.getErrorCategories()) {
				if (errCat.getShortcuts() != null) {
					for (LQIShortCut shortcut : errCat.getShortcuts()) {
						shortcutsInUse.add(shortcut.getKeyStroke());
				}
				}
			}
		}
	}

	/**
	 * Configures the table
	 * 
	 * @param gridButtonAction
	 *            the action for LQI grid buttons.
	 */
	public void configureTable(Action gridButtonAction) {
		lqiTable.setRowHeight(ROW_HEIGHT);
		lqiTable.getTableHeader()
		        .getColumnModel()
		        .getColumn(lqiTableModel.getErrorCategoryColumn())
		        .setHeaderRenderer(
		                new ColorHeaderCellRenderer(lqiTable.getBackground(),
		                        lqiTable.getGridColor()));
		int firstSeverityColIdx = lqiTableModel.getSeverityColsStartIndex();
		for (int i = 0; i < columnColors.length; i++) {

		lqiTable.getTableHeader()
		        .getColumnModel()
			        .getColumn(i + firstSeverityColIdx)
		        .setHeaderRenderer(
			                new ColorHeaderCellRenderer(columnColors[i],
		                        lqiTable.getGridColor()));
		}
		lqiTable.getTableHeader()
		        .getColumnModel()
		        .getColumn(lqiTableModel.getCommentColumn())
		        .setHeaderRenderer(
		                new ColorHeaderCellRenderer(lqiTable.getBackground(),
		                        lqiTable.getGridColor()));
		int catWeightColIdx = lqiTableModel.getErrorCatWeightColumn();
		if(catWeightColIdx > -1){
			lqiTable.getTableHeader()
	        .getColumnModel()
	        .getColumn(catWeightColIdx)
	        .setHeaderRenderer(
	                new ColorHeaderCellRenderer(lqiTable.getBackground(),
	                        lqiTable.getGridColor()));
			
		}

		ColorCellRenderer centerRenderer = new ColorCellRenderer(
		        lqiTable.getBackground());
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		lqiTable.getColumnModel()
		        .getColumn(lqiTableModel.getErrorCategoryColumn())
		        .setCellRenderer(centerRenderer);
		lqiTable.getColumnModel()
		        .getColumn(lqiTableModel.getErrorCategoryColumn())
		        .setCellEditor(new CategoryCellEditor());
		lqiTable.getColumnModel()
		        .getColumn(lqiTableModel.getCommentColumn())
		        .setCellRenderer(
		                new TextAreaColorCellRenderer(lqiTable.getBackground()));
		lqiTable.getColumnModel().getColumn(lqiTableModel.getCommentColumn())
		        .setCellEditor(new TextAreaTableCellEditor());
		if(catWeightColIdx > -1){
			lqiTable.getColumnModel().getColumn(catWeightColIdx).setCellRenderer(centerRenderer);
			lqiTable.getColumnModel().getColumn(catWeightColIdx).setCellEditor(new FloatCellEditor());
		}
		LQIGridButtonEditor buttonEditor = new LQIGridButtonEditor(
		        gridButtonAction);
		for (int i = 0; i < columnColors.length; i++) {
		lqiTable.getColumnModel()
			        .getColumn(firstSeverityColIdx + i)
			        .setCellRenderer(new LQIGridButtonRenderer(columnColors[i]));
			lqiTable.getColumnModel().getColumn(firstSeverityColIdx + i)
		        .setCellEditor(buttonEditor);
		}

		for (int i = 0; i < lqiTable.getColumnCount(); i++) {
			if (i != lqiTableModel.getErrorCatWeightColumn() && i != lqiTableModel.getCommentColumn()) {
				lqiTable.getColumnModel().getColumn(i).setWidth(COLUMN_WITDH);
				lqiTable.getColumnModel().getColumn(i)
				        .setMinWidth(COLUMN_WITDH);
			}
		}
		lqiTable.getColumnModel().getColumn(lqiTableModel.getCommentColumn())
		        .setWidth(COMMENT_COLUMN_WITDH);
		lqiTable.getColumnModel().getColumn(lqiTableModel.getCommentColumn())
		        .setMinWidth(COMMENT_COLUMN_WITDH);
		if (lqiTableModel.getErrorCatWeightColumn() != -1) {
			lqiTable.getColumnModel()
			        .getColumn(lqiTableModel.getErrorCatWeightColumn())
			        .setWidth(WEIGHT_COLUMN_WIDTH);
			lqiTable.getColumnModel()
			        .getColumn(lqiTableModel.getErrorCatWeightColumn())
			        .setMinWidth(WEIGHT_COLUMN_WIDTH);
		}
		

	}

	/**
	 * // * Gets the table width.
	 * 
	 * @return newSeverity
	 */
	public int getTableWidth() {
		
		int weightColWidth = 0;
		int normalColCount = lqiTable.getColumnCount() - 1;
		if(lqiTableModel.getErrorCatWeightColumn() != -1){
			weightColWidth = WEIGHT_COLUMN_WIDTH;
			normalColCount = lqiTable.getColumnCount() - 2;
		}
		return COLUMN_WITDH * (normalColCount)
		        + COMMENT_COLUMN_WITDH + weightColWidth; 
	}

	/**
	 * Updates the table header.
	 */
	private void updateTableHeader() {

		int sevrityColsCount = lqiTableModel.getSeverityColumnsCount();
		int severityColsFirstIdx = lqiTableModel.getSeverityColsStartIndex();
		if (severityColsFirstIdx > -1) {
			for (int index = severityColsFirstIdx; index < sevrityColsCount; index++) {
				lqiTable.getTableHeader().getColumnModel().getColumn(index)
				        .setHeaderValue(lqiTableModel.getColumnName(index));
			}
		}
	}

	/**
	 * Adds a new error category by adding a new row to the LQI grid table.
	 */
	public void addErrorCategory() {

		if (lqiTable.getRowCount() == LQIConstants.LQI_CATEGORIES_LIST.length) {
			JOptionPane.showMessageDialog(lqiTable,
			        "Impossible to add a category: no LQI categories left.",
			        "LQI Grid Add Category", JOptionPane.WARNING_MESSAGE);
		} else {
			LQIErrorCategory errorCat = lqiTableModel.addRow();
			lqiTable.getSelectionModel().setSelectionInterval(
			        lqiTableModel.getRowCount() - 1,
			        lqiTableModel.getRowCount() - 1);
			if (lqiTable.editCellAt(lqiTableModel.getRowCount() - 1, 0)) {
				lqiTable.getEditorComponent().requestFocusInWindow();
			}
			keyEventManager.errorCategoryAdded(lqiTableModel.getLQIGrid(),
			        errorCat);
		}
	}

	/**
	 * Deletes the selected category. A message is prompt to the user asking to
	 * confirm the deletion. In case the user confirms, then the selected row is
	 * deleted.
	 */
	public void deleteSelectedErrorCategory() {
		int selRow = lqiTable.getSelectedRow();
		if (selRow != -1) {
			int option = JOptionPane.showConfirmDialog(lqiTable,
			        "Do you want to delete the selected category?",
			        "LQI Grid Category Deletion", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				LQIErrorCategory errorCat = lqiTableModel.deleteRow(selRow);
				keyEventManager.errorCategoryDeleted(
				        lqiTableModel.getLQIGrid(), errorCat);
				if (errorCat.getShortcuts() != null) {
					for (LQIShortCut shortcut : errorCat.getShortcuts()) {
						shortcutsInUse.remove(shortcut.getKeyStroke());
				}
				}
			}
		}
	}

	/**
	 * Gets the LQI grid table.
	 * 
	 * @return the LQI grid table.
	 */
	public JTable getLqiTable() {
		return lqiTable;
	}

	/**
	 * Gets the LQI grid table model.
	 * 
	 * @return the LQI grid table model.
	 */
	public LQIGridTableModel getLqiTableModel() {
		return lqiTableModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.lqi.gui.TableCellListener#cellValueChanged(com.vistatec
	 * .ocelot.lqi.gui.TableCellEvent)
	 */
	@Override
	public void cellValueChanged(TableCellEvent e) {
		LQIErrorCategory errorCat = lqiTableModel.getErrorCategoryAtRow(e
		        .getRowIndex());
		keyEventManager.categoryNameChanged(errorCat, (String) e.getOldValue());
	}

	/**
	 * Saves a new shortcut for the selected cell.
	 * 
	 * @param keyCode
	 *            the shortcut key code.
	 * @param modifiers
	 *            the shortcut modifiers.
	 */
	public void saveShortCut(int keyCode, int[] modifiers) {
		LQIErrorCategory errCat = lqiTableModel.getErrorCategoryAtRow(lqiTable
		        .getSelectedRow());
		int selectedColumn = lqiTable.getSelectedColumn();
		int modifier = 0;
		for (int i = 0; i < modifiers.length; i++) {
			modifier += modifiers[i];
		}
		KeyStroke newShortcut = KeyStroke.getKeyStroke(keyCode, modifier);
		KeyStroke oldShortcut = null;
		String severityName = null;
		LQISeverity severity = lqiTableModel
		        .getSeverityByColumn(selectedColumn);
		if (severity != null) {
			severityName = severity.getName();
			LQIShortCut catShortcut = errCat.getShortcut(severityName);
			if (catShortcut != null) {
				oldShortcut = catShortcut.getKeyStroke();
		}
		lqiTable.getCellEditor(lqiTable.getSelectedRow(),
		        lqiTable.getSelectedColumn()).stopCellEditing();
		lqiTableModel.setShortCut(lqiTable.getSelectedRow(),
		        lqiTable.getSelectedColumn(), keyCode, modifiers);
			keyEventManager.shortCutChanged(errCat, oldShortcut, severityName);
		shortcutsInUse.remove(oldShortcut);
		shortcutsInUse.add(newShortcut);
	}
	}

	/**
	 * Handles the event a severity column changes.
	 * 
	 * @param oldSeverity
	 *            the old severity.
	 * @param newSeverity
	 *            the new severity.
	 */
	public void severityColumnChanged(LQISeverity oldSeverity,
	        LQISeverity newSeverity) {

		int columnIndex = lqiTableModel.getSeverityColIndex(oldSeverity
		        .getName());

		try {
			LQISeverity oldSevCloned = (LQISeverity) oldSeverity.clone();

			if (columnIndex != -1) {
				if (oldSevCloned.getScore() != newSeverity.getScore()) {
					lqiTableModel.setScoreForColumn(newSeverity.getScore(),
					        columnIndex);
			keyEventManager.errorSeverityScoreChanged(
					        newSeverity.getScore(), oldSevCloned.getName());
				}
				if (!oldSevCloned.getName().equals(newSeverity.getName())) {
					lqiTableModel.setNameForSeverityColumn(
					        newSeverity.getName(), columnIndex);
					keyEventManager.errorSeverityNameChanged(lqiTableModel
					        .getLQIGrid().getErrorCategories(), newSeverity
					        .getName(), oldSevCloned.getName());
				}
				if (oldSevCloned.getScore() == newSeverity.getScore()
				        && !oldSevCloned.getName()
				                .equals(newSeverity.getName())) {
					lqiTable.getTableHeader()
					        .getColumnModel()
					        .getColumn(columnIndex)
					        .setHeaderValue(
					                lqiTableModel.getColumnName(columnIndex));
			SwingUtilities.windowForComponent(lqiTable).repaint();
				} else {
					lqiTableModel.sortSeverityColumns();
				}
			}
		} catch (CloneNotSupportedException e) {
			// never happens
		}

	}

	/**
	 * Moves the selected row down.
	 */
	public void moveSelectedRowDown() {

		int selRow = lqiTable.getSelectedRow();
		if (selRow != -1) {
			if (lqiTable.getCellEditor() != null) {
				lqiTable.getCellEditor().stopCellEditing();
			}
			if (lqiTableModel.moveRowDown(selRow)) {
				lqiTable.getSelectionModel().setSelectionInterval(selRow + 1,
				        selRow + 1);
			}

		}
	}

	/**
	 * Moves the selected row up.
	 */
	public void moveSelectedRowUp() {
		int selRow = lqiTable.getSelectedRow();
		if (selRow != -1) {
			if (lqiTable.getCellEditor() != null) {
				lqiTable.getCellEditor().stopCellEditing();
			}
			if (lqiTableModel.moveRowUp(selRow)) {
				lqiTable.getSelectionModel().setSelectionInterval(selRow - 1,
				        selRow - 1);
			}
		}
	}

//	/**
//	 * Displays the dialog for creating a new severity.
//	 */
//	public void displayNewSeverityDialog() {
//
//		SeverityColumnPropsDialog dialog = new SeverityColumnPropsDialog(
//		        (LQIGridDialog) SwingUtilities.windowForComponent(lqiTable),
//		        null, null);
//		dialog.setVisible(true);
//	}

	/**
	 * Handles the event the user clicked on the table header.
	 * 
	 * @param e
	 *            the mouse event
	 */
	private void handleClickOnHeaderEvent(MouseEvent e) {
		if (lqiTableModel.getMode() == LQIGridTableModel.CONFIG_MODE
		        && e.getButton() == MouseEvent.BUTTON3) {
			JTableHeader tableHeader = (JTableHeader) e.getSource();
			final int colIndex = tableHeader.getColumnModel()
			        .getColumnIndexAtX(e.getX());
			LQISeverity severity = lqiTableModel.getSeverityByColumn(colIndex);
			if (severity != null) {
				mnuDelSeverity.setSeverity(severity);
				mnuSeverityProps.setSeverity(severity);
				severityMenu.show(tableHeader, e.getX(), e.getY());
			}
		}
	}

	/**
	 * Replace the current configuration with a new one.
	 * 
	 * @param lqiGridObject
	 *            the new configuration
	 */
	public void replaceConfiguration(LQIGridConfiguration lqiGridObject) {

		LQIGridConfiguration lqiGridToDiscard = lqiTableModel.getLQIGrid();
		lqiTableModel.setLQIGrid(lqiGridObject);
		updateTableHeader();
		lqiTableModel.setChanged(false);
		keyEventManager.removeActions(lqiGridToDiscard);
		keyEventManager.load(lqiGridObject);
		initUsedShortcutList(lqiGridObject);
	}

	/**
	 * Checks if the shortcut defined by given key code and modifiers is a
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

		int modifier = 0;
		for (int i = 0; i < modifiers.length; i++) {
			modifier += modifiers[i];
		}
		KeyStroke shortCut = KeyStroke.getKeyStroke(keyCode, modifier);
		return shortcutsInUse.contains(shortCut)
		        || ShortCutConstants.getReservedKeyList(platformSupport).contains(shortCut);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof JTableHeader) {
			handleClickOnHeaderEvent(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		if (e.getSource() instanceof JTableHeader) {
			handleClickOnHeaderEvent(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// does nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// does nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// does nothing

	}

	/**
	 * Initializes colors for severity columns.
	 */
	public void initColorForColumns() {

		int colNum = lqiTableModel.getSeverityColumnsCount();
		columnColors = new Color[colNum];
		float p = 1;
		double delta = 1.0 / (float) (colNum - 1);
		for (int i = 1; i <= colNum; i++) {
			float r = getValue(MINOR_COL_COLOR.getRed(),
			        CRITICAL_COL_COLOR.getRed(), p);
			float g = getValue(MINOR_COL_COLOR.getGreen(),
			        CRITICAL_COL_COLOR.getGreen(), p);
			float b = getValue(MINOR_COL_COLOR.getBlue(),
			        CRITICAL_COL_COLOR.getBlue(), p);
			columnColors[i - 1] = new Color((int) r, (int) g, (int) b);
			p -= delta;
		}
	}

	private float getValue(float firstValue, float secValue, float p) {

		float value = 0;
		value = firstValue * p + secValue * (1 - p);
		if (value < 0) {
			value = 0;
		} else if (value > 255) {
			value = 255;
		}
		return value;
	}

	/**
	 * Adds a column for a severity passed as parameter.
	 * 
	 * @param newSeverity
	 *            the new severity
	 */
	public void addSeverityColumn(LQISeverity newSeverity) {

		lqiTableModel.addSeverityColumn(newSeverity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(mnuDelSeverity)) {
			deleteSeverity(mnuDelSeverity.getSeverity());
		} else if (e.getSource().equals(mnuSeverityProps)) {
			showSeverityPropsDialog(mnuSeverityProps.getSeverity());
		}
	}

	/**
	 * Deletes a severity. Then the related column is removed.
	 * 
	 * @param severity
	 *            the severity to me deleted.
	 */
	private void deleteSeverity(LQISeverity severity) {

		int option = JOptionPane.showConfirmDialog(lqiTable,
		        "Do you want to delete the \"" + severity.getName()
		                + "\" column?", "Delete Severity Column",
		        JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			System.out.println("Delete");
			lqiTableModel.removeSeverityColumn(severity);
			keyEventManager.errorSeverityDeleted(lqiTableModel.getLQIGrid()
			        .getErrorCategories(), severity);
		}

	}

	/**
	 * Displays the severity property dialog.
	 * 
	 * @param lqiSeverity
	 *            the severity
	 */
	private void showSeverityPropsDialog(LQISeverity lqiSeverity) {

		System.out.println("Edit");
		final SeverityColumnPropsDialog severityDialog = new SeverityColumnPropsDialog(
		        (LQIConfigurationEditDialog) SwingUtilities.windowForComponent(lqiTable),
		        null, lqiSeverity);
		severityDialog.setVisible(true);
	}

}

/**
 * The Severity menu item class.
 */
class SeverityMenuItem extends JMenuItem {

	/** The serial version UID. */
	private static final long serialVersionUID = 8456313250004675606L;

	/** The edit action. */
	public static final int EDIT = 0;

	/** The delete action. */
	public static final int DELETE = 1;

	/** The menu item action. */
	private int mnuAction;

	/** The severity. */
	private LQISeverity severity;

	/**
	 * Constructor
	 * 
	 * @param text
	 *            the menu item text
	 * @param mnuAction
	 *            the action
	 */
	public SeverityMenuItem(String text, int mnuAction) {
		super(text);
		this.mnuAction = mnuAction;
	}

	/**
	 * Gets the menu item action.
	 * 
	 * @return the menu item action.
	 */
	public int getMnuAction() {
		return mnuAction;
	}

	/**
	 * Sets the severity.
	 * 
	 * @param severity
	 *            the severity.
	 */
	public void setSeverity(LQISeverity severity) {

		this.severity = severity;
	}

	/**
	 * Gets the severity.
	 * 
	 * @return the severity.
	 */
	public LQISeverity getSeverity() {
		return severity;
	}
}