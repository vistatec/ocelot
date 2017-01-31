package com.vistatec.ocelot.lqi.gui.panel;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import com.vistatec.ocelot.lqi.gui.LQIGridTableHelper;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

/**
 * This class defines a scroll panel containing the table displayed in the LQI Grid.
 */
public class LQIGridTableContainer extends JScrollPane {

	private static final long serialVersionUID = -492576764036972394L;

	private static final int TABLE_PADDING = 50;

	private LQIGridTableHelper tableHelper;

	private Action action;

	private int mode;

	public LQIGridTableContainer(LQIGridConfiguration lqiGridConf,
	        LQIGridTableHelper tableHelper, Action action, int mode) {

		this.tableHelper = tableHelper;
		this.action = action;
		this.mode = mode;
		init(lqiGridConf);
	}

	public void replaceConfiguration(LQIGridConfiguration lqiGridConf) {

		tableHelper.replaceConfiguration(lqiGridConf);
	}

	private void init(LQIGridConfiguration lqiGridConf) {

		setViewportView(tableHelper.createLQIGridTable(lqiGridConf, mode,
		        action));
		configTable();
		tableHelper.getLqiTable().addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
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
				        colAddedNum++;
				        if (colAddedNum == tableHelper.getLqiTableModel()
				                .getColumnCount()) {
					        colAddedNum = 0;
					        configTable();
					        setTableSize();
				        }
			        }
		        });

	}

	/**
	 * Sets the table size.
	 */
	public synchronized void setTableSize() {
		int height = tableHelper.getLqiTable().getTableHeader().getSize().height
		        + 5
		        + (tableHelper.getLqiTable().getRowHeight() * tableHelper
		                .getLqiTable().getRowCount());

		int width = TABLE_PADDING + tableHelper.getTableWidth();

		setPreferredSize(new Dimension(width, height));
		Window ancestorWindow = SwingUtilities.getWindowAncestor(this);
		if (ancestorWindow != null) {
			ancestorWindow.pack();
		}

	}
	
	public void refresh(){
		
		tableHelper.getLqiTableModel().fireTableStructureChanged();
	}

	private void configTable() {

		tableHelper.initColorForColumns();
		tableHelper.configureTable(action);
	}

	public void setEnabled(boolean enabled) {
		tableHelper.getLqiTable().setEnabled(enabled);
	}

	public boolean isEditingCommentColumn() {

		return tableHelper.getLqiTable().getEditingColumn() == tableHelper
		        .getLqiTableModel().getCommentColumn();
	}

	public String getCommentByCategory(String errorCategory) {

		return tableHelper.getLqiTableModel().getCommentByCategory(
		        errorCategory);
	}

	public void clearCommentCellForCategory(String category) {

		tableHelper.getLqiTableModel().clearCommentForCategory(category);
	}
}
