package com.vistatec.ocelot.lqi.gui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;

import com.vistatec.ocelot.lqi.constants.LQIConstants;
import com.vistatec.ocelot.lqi.constants.ShortCutConstants;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGrid;

/**
 * Helper class for the LQI grid table management.
 */
public class LQIGridTableHelper implements MouseListener, TableCellListener {

	/** Minor severity column color. */
	private static final Color MINOR_COL_COLOR = new Color(217, 234, 211);

	/** Serious severity column color. */
	private static final Color SERIOUS_COL_COLOR = new Color(252, 229, 205);

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

	/**
	 * Constructor.
	 * 
	 * @param keyEventManager
	 *            the keyboard event manager.
	 */
	public LQIGridTableHelper() {
		this.keyEventManager = LQIKeyEventManager.getInstance();
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
	public JTable createLQIGridTable(LQIGrid lqiGridObj, int mode,
	        final Action gridButtonAction) {

		initUsedShortcutList(lqiGridObj);
		lqiTableModel = new LQIGridTableModel(lqiGridObj, mode);
		lqiTableModel.addTableCellListener(this);
		lqiTable = new JTable(lqiTableModel);
		lqiTable.setGridColor(Color.LIGHT_GRAY);
		lqiTable.getTableHeader().setReorderingAllowed(false);
		lqiTable.getTableHeader().addMouseListener(this);
		return lqiTable;
	}

	private void initUsedShortcutList(LQIGrid lqiGridObj) {

		shortcutsInUse = new ArrayList<KeyStroke>();
		if (lqiGridObj.getErrorCategories() != null) {
			for (LQIErrorCategory errCat : lqiGridObj.getErrorCategories()) {
				if (errCat.getCriticalShortcut() != null
				        && errCat.getCriticalShortcut().getKeyStroke() != null) {
					shortcutsInUse.add(errCat.getCriticalShortcut()
					        .getKeyStroke());
				}
				if (errCat.getMinorShortcut() != null
				        && errCat.getMinorShortcut().getKeyStroke() != null) {
					shortcutsInUse
					        .add(errCat.getMinorShortcut().getKeyStroke());
				}
				if (errCat.getSeriousShortcut() != null
				        && errCat.getSeriousShortcut().getKeyStroke() != null) {
					shortcutsInUse.add(errCat.getSeriousShortcut()
					        .getKeyStroke());
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
		lqiTable.getTableHeader()
		        .getColumnModel()
		        .getColumn(lqiTableModel.getMinorScoreColumn())
		        .setHeaderRenderer(
		                new ColorHeaderCellRenderer(MINOR_COL_COLOR, lqiTable
		                        .getGridColor()));
		lqiTable.getTableHeader()
		        .getColumnModel()
		        .getColumn(lqiTableModel.getSeriousScoreColumn())
		        .setHeaderRenderer(
		                new ColorHeaderCellRenderer(SERIOUS_COL_COLOR, lqiTable
		                        .getGridColor()));
		lqiTable.getTableHeader()
		        .getColumnModel()
		        .getColumn(lqiTableModel.getCriticalScoreColumn())
		        .setHeaderRenderer(
		                new ColorHeaderCellRenderer(CRITICAL_COL_COLOR,
		                        lqiTable.getGridColor()));
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
		lqiTable.getColumnModel()
		        .getColumn(lqiTableModel.getMinorScoreColumn())
		        .setCellRenderer(new LQIGridButtonRenderer(MINOR_COL_COLOR));
		lqiTable.getColumnModel()
		        .getColumn(lqiTableModel.getMinorScoreColumn())
		        .setCellEditor(buttonEditor);

		lqiTable.getColumnModel()
		        .getColumn(lqiTableModel.getSeriousScoreColumn())
		        .setCellRenderer(new LQIGridButtonRenderer(SERIOUS_COL_COLOR));
		lqiTable.getColumnModel()
		        .getColumn(lqiTableModel.getSeriousScoreColumn())
		        .setCellEditor(buttonEditor);
		lqiTable.getColumnModel()
		        .getColumn(lqiTableModel.getCriticalScoreColumn())
		        .setCellRenderer(new LQIGridButtonRenderer(CRITICAL_COL_COLOR));
		lqiTable.getColumnModel()
		        .getColumn(lqiTableModel.getCriticalScoreColumn())
		        .setCellEditor(buttonEditor);
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
		lqiTable.getTableHeader()
		        .getColumnModel()
		        .getColumn(lqiTableModel.getCriticalScoreColumn())
		        .setHeaderValue(
		                lqiTableModel.getColumnName(lqiTableModel
		                        .getCriticalScoreColumn()));
		lqiTable.getTableHeader()
		        .getColumnModel()
		        .getColumn(lqiTableModel.getMinorScoreColumn())
		        .setHeaderValue(
		                lqiTableModel.getColumnName(lqiTableModel
		                        .getMinorScoreColumn()));
		lqiTable.getTableHeader()
		        .getColumnModel()
		        .getColumn(lqiTableModel.getSeriousScoreColumn())
		        .setHeaderValue(
		                lqiTableModel.getColumnName(lqiTableModel
		                        .getSeriousScoreColumn()));
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
				if (errorCat.getCriticalShortcut() != null
				        && errorCat.getCriticalShortcut().getKeyStroke() != null) {
					shortcutsInUse.remove(errorCat.getCriticalShortcut()
					        .getKeyStroke());
				}
				if (errorCat.getMinorShortcut() != null
				        && errorCat.getMinorShortcut().getKeyStroke() != null) {
					shortcutsInUse.remove(errorCat.getMinorShortcut()
					        .getKeyStroke());
				}
				if (errorCat.getSeriousShortcut() != null
				        && errorCat.getSeriousShortcut().getKeyStroke() != null) {
					shortcutsInUse.remove(errorCat.getSeriousShortcut()
					        .getKeyStroke());
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
		String severity = null;
		if (selectedColumn == lqiTableModel.getMinorScoreColumn()) {
			if (errCat.getMinorShortcut() != null) {
				oldShortcut = errCat.getMinorShortcut().getKeyStroke();
			}
			severity = LQIConstants.MINOR_SEVERITY_NAME;
		} else if (selectedColumn == lqiTableModel.getSeriousScoreColumn()) {
			if (errCat.getSeriousShortcut() != null) {
				oldShortcut = errCat.getSeriousShortcut().getKeyStroke();
			}
			severity = LQIConstants.SERIOUS_SEVERITY_NAME;
		} else if (selectedColumn == lqiTableModel.getCriticalScoreColumn()) {
			if (errCat.getCriticalShortcut() != null) {
				oldShortcut = errCat.getCriticalShortcut().getKeyStroke();
			}
			severity = LQIConstants.CRITICAL_SEVERITY_NAME;
		}
		lqiTable.getCellEditor(lqiTable.getSelectedRow(),
		        lqiTable.getSelectedColumn()).stopCellEditing();
		lqiTableModel.setShortCut(lqiTable.getSelectedRow(),
		        lqiTable.getSelectedColumn(), keyCode, modifiers);
		keyEventManager.shortCutChanged(errCat, oldShortcut, severity);
		shortcutsInUse.remove(oldShortcut);
		shortcutsInUse.add(newShortcut);
	}

	/**
	 * Handles the event a severity score value changes.
	 * 
	 * @param severityScore
	 *            the new severity score value.
	 * @param severityName
	 *            the severity name
	 */
	public void severityScoreChanged(String severityScore, String severityName) {
		if (severityScore != null && !severityScore.isEmpty()) {
			int columnIndex = -1;
			if (severityName.equals(LQIConstants.MINOR_SEVERITY_NAME)) {
				columnIndex = lqiTableModel.getMinorScoreColumn();
			} else if (severityName.equals(LQIConstants.SERIOUS_SEVERITY_NAME)) {
				columnIndex = lqiTableModel.getSeriousScoreColumn();
			} else if (severityName.equals(LQIConstants.CRITICAL_SEVERITY_NAME)) {
				columnIndex = lqiTableModel.getCriticalScoreColumn();
			}
			lqiTableModel.setScoreForColumn(Integer.valueOf(severityScore),
			        columnIndex);

			lqiTable.getTableHeader().getColumnModel().getColumn(columnIndex)
			        .setHeaderValue(lqiTableModel.getColumnName(columnIndex));
			keyEventManager.errorSeverityScoreChanged(
			        Integer.valueOf(severityScore), severityName);
			SwingUtilities.windowForComponent(lqiTable).repaint();
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

	/**
	 * Creates the dialog letting users choose a new severity score.
	 * 
	 * @param score
	 *            the current severity score
	 * @param colIdx
	 *            the severity column index
	 * @param location
	 *            the point where the dialog has to be displayed
	 * @return the severity score dialog.
	 */
	private JDialog createSeverityScoreDialog(final int score,
	        final int colIdx, final Point location) {

		String severity = "";
		if (colIdx == lqiTableModel.getMinorScoreColumn()) {
			severity = LQIConstants.MINOR_SEVERITY_NAME;
		} else if (colIdx == lqiTableModel.getCriticalScoreColumn()) {
			severity = LQIConstants.CRITICAL_SEVERITY_NAME;
		} else if (colIdx == lqiTableModel.getSeriousScoreColumn()) {
			severity = LQIConstants.SERIOUS_SEVERITY_NAME;
		}
		final SeverityScoreDialog severityDialog = new SeverityScoreDialog(
		        (LQIGridDialog) SwingUtilities.windowForComponent(lqiTable),
		        location, severity, score);

		return severityDialog;
	}

	/**
	 * Handles the event the user clicked on the table header.
	 * 
	 * @param e
	 *            the mouse event
	 */
	private void handleClickOnHeaderEvent(MouseEvent e) {
		if (lqiTableModel.getMode() == LQIGridDialog.CONFIG_MODE) {
			JTableHeader tableHeader = (JTableHeader) e.getSource();
			final int colIndex = tableHeader.getColumnModel()
			        .getColumnIndexAtX(e.getX());
			if (colIndex != lqiTableModel.getErrorCategoryColumn()) {
				int score = lqiTableModel.getScoreForColumn(colIndex);
				JDialog dialog = createSeverityScoreDialog(score, colIndex,
				        e.getLocationOnScreen());
				dialog.setVisible(true);
			}
		}
	}

	/**
	 * Replace the current configuration with a new one.
	 * 
	 * @param lqiGridObject
	 *            the new configuration
	 */
	public void replaceConfiguration(LQIGrid lqiGridObject) {

		LQIGrid lqiGridToDiscard = lqiTableModel.getLQIGrid();
		lqiTableModel.setLQIGrid(lqiGridObject);
		updateTableHeader();
		lqiTableModel.setChanged(false);
		keyEventManager.removeActions(lqiGridToDiscard);
		keyEventManager.load(lqiGridObject);
		initUsedShortcutList(lqiGridObject);
	}

	public boolean isReservedShortcut(int keyCode, int[] modifiers) {

		int modifier = 0;
		for (int i = 0; i < modifiers.length; i++) {
			modifier += modifiers[i];
		}
		KeyStroke shortCut = KeyStroke.getKeyStroke(keyCode, modifier);
		return shortcutsInUse.contains(shortCut)
		        || ShortCutConstants.getReservedKeyList().contains(shortCut);
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
}
