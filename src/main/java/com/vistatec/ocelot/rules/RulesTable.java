package com.vistatec.ocelot.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class RulesTable extends JPanel implements Runnable {
    private static final long serialVersionUID = 1L;
    private JFrame frame;
    private RuleConfiguration ruleConfig;

    public RulesTable(RuleConfiguration ruleConfig) {
        //super(new GridBagLayout());
        super(); // XX Layout?

        this.ruleConfig = ruleConfig;
        
        JTable table = new JTable(new TableModel());
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
        List<String> keys = new ArrayList<String>();

        TableModel() {
            // XXX Hack - because ruleConfig itself doesn't sort
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

        // XXX Issue here is that the rulesConfig doesn't actually 
        // correlate the rulesFilter to its dataCategoryFlag except
        // by index into their respective lists.
        // I also probably need to use an ITSMetadata object in order
        // to render the color + border + glyph.
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            String key = keys.get(rowIndex);
            return (columnIndex == 0) ? 
                    key : "display";
        }
        
    }

}
