package com.vistatec.ocelot.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.vistatec.ocelot.rules.NullITSMetadata.NullDataCategoryFlag;

public class RulesTable extends JPanel implements Runnable {
    private static final long serialVersionUID = 1L;
    private JFrame frame;
    private RuleConfiguration ruleConfig;

    public RulesTable(RuleConfiguration ruleConfig) {
        //super(new GridBagLayout());
        super(); // XX Layout?

        this.ruleConfig = ruleConfig;
        
        JTable table = new JTable(new TableModel());
        table.setDefaultRenderer(DataCategoryFlag.class, new DataCategoryFlagRenderer());
        add(table);
    }
    
    @Override
    public void run() {
        frame = new JFrame("Rules");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
    }

    // TODO register renderers
    class TableModel extends AbstractTableModel {
        List<String> keys = new ArrayList<String>();

        TableModel() {
            // XXX Hack - because ruleConfig itself doesn't sort
            // XXX no longer correct order
            keys.addAll(ruleConfig.getRules().keySet());
            Collections.sort(keys);
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return keys.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            String key = keys.get(rowIndex);
            switch (columnIndex) {
            case 0:
                return key;
            case 1:
                DataCategoryFlag flag = ruleConfig.getRules().get(key).getFlag();
                return (flag != null) ? flag : NullDataCategoryFlag.getInstance();
            }
            throw new IllegalArgumentException("Invalid column " + columnIndex);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return DataCategoryFlag.class;
            }
            throw new IllegalArgumentException("Invalid column " + columnIndex);
        }
        
    }

}
