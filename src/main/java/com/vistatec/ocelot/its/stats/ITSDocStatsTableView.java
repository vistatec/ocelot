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
        private ITSDocStats stats = new ITSDocStats();

        @Override
        public int getRowCount() {
            return stats.getStats().size();
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
                    tableCell = stats.getStats().get(row).getDataCategory();
                    break;

                case 1:
                    tableCell = stats.getStats().get(row).getType();
                    break;

                case 2:
                    tableCell = stats.getStats().get(row).getValue();
                    break;

                case 3:
                    tableCell = stats.getStats().get(row).getCount();
                    break;

                default:
                    throw new IllegalArgumentException("Incorrect number of columns: "+col);
            }
            return tableCell;
        }

        private void clearStats() {
            this.stats.clear();
        }

        private void updateLQIStats(LanguageQualityIssue lqi) {
            stats.addLQIStats(lqi);
            fireTableDataChanged();
        }

        private void updateProvStats(Provenance prov) {
            stats.addProvenanceStats(prov);
            fireTableDataChanged();
        }
    }
}
