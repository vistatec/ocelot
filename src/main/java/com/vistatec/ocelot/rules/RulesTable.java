package com.vistatec.ocelot.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

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
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMinWidth(100);
        columnModel.getColumn(0).setPreferredWidth(200);
        columnModel.getColumn(0).setMaxWidth(500);
        columnModel.getColumn(1).setMinWidth(15);
        columnModel.getColumn(1).setPreferredWidth(15);
        columnModel.getColumn(1).setMaxWidth(15);

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

    class TableModel extends AbstractTableModel {
        TableModel() {
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return ruleConfig.getRules().size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Rule rule = ruleConfig.getRules().get(rowIndex);
            switch (columnIndex) {
            case 0:
                return rule.getLabel();
            case 1:
                DataCategoryFlag flag = rule.getFlag();
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
