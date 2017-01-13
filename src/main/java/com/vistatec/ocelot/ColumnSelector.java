package com.vistatec.ocelot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.rules.DataCategoryFlag;
import com.vistatec.ocelot.rules.DataCategoryFlagRenderer;
import com.vistatec.ocelot.segment.view.SegmentTableModel;
import com.vistatec.ocelot.ui.ODialogPanel;
import com.vistatec.ocelot.ui.OTable;
import com.vistatec.ocelot.ui.TableRowToggleMouseAdapter;

public class ColumnSelector extends ODialogPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    private SegmentTableModel model;
    private ColumnTable table;
    protected EnumMap<SegmentViewColumn, Boolean> enabledColumns =
            new EnumMap<SegmentViewColumn, Boolean>(SegmentViewColumn.class);
    private JButton ok;
    
    public ColumnSelector(SegmentTableModel tableModel) {
        super(new BorderLayout(10, 10));
        this.model = tableModel;
        enabledColumns.putAll(model.getColumnEnabledStates());

        setBorder(new EmptyBorder(10,10,10,10));

        JLabel title = new JLabel("Select columns to display:");
        add(title, BorderLayout.PAGE_START);

        this.table = new ColumnTable();
        add(table.getTable(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new DisposeDialogListener());
        buttonPanel.add(cancel);

        ok = new JButton("OK");
        ok.addActionListener(this);
        buttonPanel.add(ok);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

    public JButton getDefaultButton() {
        return ok;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Sync table data back to the model
        for (Map.Entry<SegmentViewColumn, Boolean> e : enabledColumns.entrySet()) {
            model.setColumnEnabled(e.getKey(), e.getValue());
        }
        try {
			model.saveColumnConfiguration();
		} catch (TransferException e) {
			Logger.getLogger(ColumnSelector.class).error(
					"Error while saving the columns configuration", e);
		}
        getDialog().dispose();
        model.fireTableStructureChanged();
    }

    public class ColumnTable {
        private JTable table;
        private TableModel tableModel;

        public ColumnTable() {
            this.table = createTable();
        }

        public JTable getTable() {
            return table;
        }

        class TableModel extends AbstractTableModel {
            private static final long serialVersionUID = 1L;

            @Override
            public int getRowCount() {
                return SegmentViewColumn.values().length;
            }
            
            @Override
            public Object getValueAt(int row, int column) {
                SegmentViewColumn col = getColumnForRow(row);
                switch (column) {
                case 0:
                    return enabledColumns.get(col);
                case 1:
                    return col.getFullName();
                }
                throw new IllegalArgumentException("Invalid column " + column);
            }

            @Override
            public void setValueAt(Object obj, int row, int column) {
                if (obj instanceof Boolean && column == 0) {
                    SegmentViewColumn col= getColumnForRow(row);
                    enabledColumns.put(col, (Boolean)obj);
                    fireTableCellUpdated(row, column);
                }
            }

            private SegmentViewColumn getColumnForRow(int row) {
                return Arrays.asList(SegmentViewColumn.values()).get(row);
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
                }
                throw new IllegalArgumentException("Invalid column " + columnIndex);
            }
        }

        protected JTable createTable() {
            tableModel = new TableModel();
            JTable table = new OTable(tableModel);
            table.setTableHeader(null);
            table.setCellSelectionEnabled(false);
            table.setShowGrid(false);
            table.setDefaultRenderer(DataCategoryFlag.class, new DataCategoryFlagRenderer());
            // Add a little little breathing room, particularly around the edge
            table.setRowHeight(table.getRowHeight() + 4);

            TableColumnModel columnModel = table.getColumnModel();
            // Hack - size the column to fit a checkbox exactly.  Otherwise
            // there's a rendering glitch where the checkbox can jump around
            // in the cell when clicked.
            JCheckBox cb = new JCheckBox();
            int cbWidth = cb.getPreferredSize().width + 4;
            columnModel.getColumn(0).setMinWidth(cbWidth);
            columnModel.getColumn(0).setPreferredWidth(cbWidth);
            columnModel.getColumn(0).setMaxWidth(cbWidth);
            columnModel.getColumn(1).setMinWidth(100);
            columnModel.getColumn(1).setPreferredWidth(150);
            columnModel.getColumn(1).setMaxWidth(150);

            table.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            table.addMouseListener(new TableRowToggleMouseAdapter() {
                @Override
                protected boolean acceptEvent(int row, int column) {
                    return (column > 0);
                }
            });

            return table;
        }
    }
}
