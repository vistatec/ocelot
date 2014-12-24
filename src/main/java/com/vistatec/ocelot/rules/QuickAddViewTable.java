package com.vistatec.ocelot.rules;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.google.common.eventbus.EventBus;
import com.vistatec.ocelot.events.QuickAddEvent;
import com.vistatec.ocelot.rules.NullITSMetadata.NullDataCategoryFlag;
import com.vistatec.ocelot.ui.OTable;

public class QuickAddViewTable {
    private JTable table;
    private QuickAddTableModel tableModel;
    private RuleConfiguration ruleConfig;
    private String acceleratorGlyph;
    private EventBus eventBus;

    public QuickAddViewTable(RuleConfiguration ruleConfig, EventBus eventBus) {
        this.ruleConfig = ruleConfig;
        this.eventBus = eventBus;
        tableModel = new QuickAddTableModel();
        table = createTable(tableModel);
        acceleratorGlyph = getAcceleratorGlyph();
    }

    public JTable getTable() {
        return table;
    }

    protected RuleConfiguration getConfig() {
        return ruleConfig;
    }

    private String getAcceleratorGlyph() {
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        switch (keyMask) {
            case KeyEvent.META_MASK:
                return "\u2318";
            case KeyEvent.CTRL_MASK:
                return "Ctrl";
            default:
                return "?";
        }
    }

    protected JTable createTable(QuickAddTableModel tableModel) {
        final JTable table = new OTable(tableModel);
        table.setTableHeader(null);
        table.setCellSelectionEnabled(false);
        table.setShowGrid(false);
        table.setDefaultRenderer(DataCategoryFlag.class, new DataCategoryFlagRenderer());
        // Add a little little breathing room, particularly around the edge
        table.setRowHeight(table.getRowHeight() + 4);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMinWidth(50);
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(0).setMaxWidth(50);
        columnModel.getColumn(1).setMinWidth(100);
        columnModel.getColumn(1).setPreferredWidth(325);
        columnModel.getColumn(1).setMaxWidth(325);
        columnModel.getColumn(2).setMinWidth(25);
        columnModel.getColumn(2).setPreferredWidth(25);
        columnModel.getColumn(2).setMaxWidth(25);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    QuickAdd qa = getConfig().getQuickAddLQI(table.getSelectedRow());
                    if (qa != null) {
                        eventBus.post(new QuickAddEvent(qa));
                    }
                }
            }
        });
        table.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return table;
    }

    // Table model:
    // [hotkey]   [description]   [flag]
    class QuickAddTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public int getRowCount() {
            return 10;
        }

        @Override
        public Object getValueAt(int row, int column) {
            // TODO: special case: headers?
            int hotkey = row % 10;
            QuickAdd qa = getConfig().getQuickAddLQI(hotkey);
            switch (column) {
            case 0:
                return "" + acceleratorGlyph + "-" + hotkey;
            case 1:
                return (qa != null) ? qa.getName() : "Unassigned";
            case 2:
                if (qa != null) {
                    DataCategoryFlag flag = ruleConfig.getFlagForMetadata(qa.getLQIData());
                    return (flag != null) ? flag : DataCategoryFlag.getDefault();
                }
                return NullDataCategoryFlag.getInstance();
            }
            throw new IllegalArgumentException("Invalid column " + column);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return DataCategoryFlag.class;
            }
            throw new IllegalArgumentException("Invalid column " + columnIndex);
        }
    }}
