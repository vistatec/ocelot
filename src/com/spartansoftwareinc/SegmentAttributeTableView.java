package com.spartansoftwareinc;

import java.util.LinkedList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Table View for displaying segment ITS metadata.
 */
public class SegmentAttributeTableView extends JScrollPane {
    protected JTable docStatsTable;
    private DocumentStatsTableModel docStatsModel;
    private TableRowSorter sort;

    public SegmentAttributeTableView() {
        docStatsModel = new DocumentStatsTableModel();
        docStatsTable = new JTable(docStatsModel);

        sort = new TableRowSorter(docStatsModel);
        docStatsTable.setRowSorter(sort);

        setViewportView(docStatsTable);
    }
    
    public void addLQIMetadata(LanguageQualityIssue lqi) {
        docStatsModel.updateLQIStats(lqi);
    }

    public void addProvMetadata(ITSProvenance prov) {
        docStatsModel.updateProvStats(prov);
    }

    public void clearStats() {
        docStatsModel.clearStats();
    }

    private class DocumentStatsTableModel extends AbstractTableModel {
        public static final int NUMCOLS = 4;
        public String[] colNames = {"Data Category", "Type", "Value", "Count"};
        private List<ITSStats> rows = new LinkedList<ITSStats>();

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return NUMCOLS;
        }

        @Override
        public String getColumnName(int col) {
            return col < NUMCOLS ? colNames[col] : "";
        }

        @Override
        public Class getColumnClass(int col) {
            if (col == 3) {
                return Integer.class;
            } else {
                return String.class;
            }
        }

        @Override
        public Object getValueAt(int row, int col) {
            Object tableCell;
            switch (col) {
                case 0:
                    tableCell = rows.get(row).getDataCategory();
                    break;

                case 1:
                    tableCell = rows.get(row).getType();
                    break;

                case 2:
                    tableCell = rows.get(row).getValue();
                    break;

                case 3:
                    tableCell = rows.get(row).getCount();
                    break;

                default:
                    throw new IllegalArgumentException("Incorrect number of columns: "+col);
            }
            return tableCell;
        }

        private List<ITSStats> getStats() {
            return this.rows;
        }

        private void addStat(ITSStats stat) {
            this.rows.add(stat);
        }

        private void clearStats() {
            this.rows.clear();
        }

        private void updateLQIStats(LanguageQualityIssue lqi) {
            boolean foundExistingStat = false;
            for (ITSStats stat : getStats()) {
                if (stat instanceof LQIStatistics) {
                    LQIStatistics lqiStat = (LQIStatistics) stat;
                    if (lqiStat.getType().equals(lqi.getType())) {
                        lqiStat.setRange(lqi.getSeverity());
                        foundExistingStat = true;
                    }
                }
            }
            if (!foundExistingStat) {
                LQIStatistics lqiStat = new LQIStatistics();
                lqiStat.setType(lqi.getType());
                lqiStat.setRange(lqi.getSeverity());
                addStat(lqiStat);
            }
            fireTableDataChanged();
        }

        private void updateProvStats(ITSProvenance prov) {
            calcProvenanceStats("person", prov.getPerson());
            calcProvenanceStats("org", prov.getOrg());
            calcProvenanceStats("tool", prov.getTool());
            calcProvenanceStats("revPerson", prov.getRevPerson());
            calcProvenanceStats("revOrg", prov.getRevOrg());
            calcProvenanceStats("revTool", prov.getRevTool());
            fireTableDataChanged();
        }

        private void calcProvenanceStats(String type, String value) {
            if (value != null) {
                boolean foundExistingStat = false;
                for (ITSStats stat : getStats()) {
                    if (stat instanceof ProvStatistics) {
                        ProvStatistics provStat = (ProvStatistics)stat;
                        if (provStat.getProvType().equals(type+":"+value)) {
                            provStat.setCount(provStat.getCount() + 1);
                            foundExistingStat = true;
                        }
                    }
                }
                if (!foundExistingStat) {
                    ProvStatistics provStat = new ProvStatistics();
                    provStat.setProvType(type+":"+value);
                    provStat.setType(type);
                    provStat.setValue(value);
                    addStat(provStat);
                }
            }
        }
    }
}
