package com.vistatec.ocelot.tm.gui.match;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;

import com.vistatec.ocelot.segment.model.SegmentVariant;

public class TmTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8088882927665970088L;

	
	public TmTable(	) {
		
		init();
	}
	

	private void init() {
		// tableModel = new ConcordanceMatchTableModel(null);
		// matchesTable = new JTable(tableModel);

		getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		setDefaultRenderer(SegmentVariant.class,
				new SegmentVariantCellRenderer());
		setDefaultRenderer(Object.class, new AlternateRowsColorRenderer());
		setTableHeader(null);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK),
				"selectPreviousRow");
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK),
				"selectNextRow");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke("DOWN"), "none");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke("UP"), "none");
		getModel().addTableModelListener(this);
		getColumnModel().addColumnModelListener(this);

	}

	private void updateRowHeights() {
		if (getColumnModel().getColumnCount() != getModel().getColumnCount()) {
			// We haven't finished building the column model, so there's no
			// point in calculating
			// the row height yet.
			return;
		}
		if (getRowCount() > 0) {
			FontMetrics fontMetrics = getFontMetrics(getFont());
			for (int row = 0; row < getRowCount(); row++) {
				int rowHeight = fontMetrics.getHeight();
				for (int col = 0; col < getColumnCount(); col++) {
					rowHeight = getPreferredHeightForCell(row, col, rowHeight);
				}
				setRowHeight(row, rowHeight);
			}
		}

	}

	private int getPreferredHeightForCell(int row, int column,
			int previousHeight) {

		Object value = getModel().getValueAt(row, column);
		Component comp = getCellRenderer(row, column)
				.getTableCellRendererComponent(this, value, true, true, row,
						column);
		comp.setFont(getFont());
		FontMetrics metrics = getFontMetrics(getFont());
		comp.setSize(new Dimension(getColumnModel().getColumn(column)
				.getWidth(), metrics.getHeight()));
		return Math.max(previousHeight, comp.getPreferredSize().height);

	}

	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		updateRowHeights();

	}

	@Override
	public void columnMarginChanged(ChangeEvent e) {
		super.columnMarginChanged(e);
		updateRowHeights();
	}
	
}
