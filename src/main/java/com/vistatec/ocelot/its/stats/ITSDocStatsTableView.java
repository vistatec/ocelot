/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.its.stats;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Table View for displaying segment ITS metadata.
 */
public class ITSDocStatsTableView extends JScrollPane {
    private static final long serialVersionUID = 1L;

    protected JTable docStatsTable;
    private DocumentStatsTableModel docStatsModel;
    private TableRowSorter<DocumentStatsTableModel> sort;

    public ITSDocStatsTableView() {
        docStatsModel = new DocumentStatsTableModel();
        docStatsTable = new JTable(docStatsModel);

        sort = new TableRowSorter<DocumentStatsTableModel>(docStatsModel);
        docStatsTable.setRowSorter(sort);

        setViewportView(docStatsTable);
    }
    
    public void addLQIMetadata(LanguageQualityIssue lqi) {
        docStatsModel.updateLQIStats(lqi);
    }

    public void addProvMetadata(Provenance prov) {
        docStatsModel.updateProvStats(prov);
    }

    public void clearStats() {
        docStatsModel.clearStats();
    }

    private class DocumentStatsTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

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
        public Class<?> getColumnClass(int col) {
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
                if (stat instanceof LanguageQualityIssueStats) {
                    LanguageQualityIssueStats lqiStat = (LanguageQualityIssueStats) stat;
                    if (lqiStat.getType().equals(lqi.getType())) {
                        lqiStat.setRange(lqi.getSeverity());
                        foundExistingStat = true;
                    }
                }
            }
            if (!foundExistingStat) {
                LanguageQualityIssueStats lqiStat = new LanguageQualityIssueStats();
                lqiStat.setType(lqi.getType());
                lqiStat.setRange(lqi.getSeverity());
                addStat(lqiStat);
            }
            fireTableDataChanged();
        }

        private void updateProvStats(Provenance prov) {
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
                    if (stat instanceof ProvenanceStats) {
                        ProvenanceStats provStat = (ProvenanceStats)stat;
                        if (provStat.getProvType().equals(type+":"+value)) {
                            provStat.setCount(provStat.getCount() + 1);
                            foundExistingStat = true;
                        }
                    }
                }
                if (!foundExistingStat) {
                    ProvenanceStats provStat = new ProvenanceStats();
                    provStat.setProvType(type+":"+value);
                    provStat.setType(type);
                    provStat.setValue(value);
                    addStat(provStat);
                }
            }
        }
    }
}
