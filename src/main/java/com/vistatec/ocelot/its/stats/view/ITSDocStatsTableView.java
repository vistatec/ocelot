/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.its.stats.view;

import com.vistatec.ocelot.its.model.ErrorCategoryAndSeverityMapper;
import com.vistatec.ocelot.its.stats.model.ITSStats;
import com.vistatec.ocelot.its.stats.model.LanguageQualityIssueStats;
import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.ErrorCategoryStdChangedEvent;
import com.vistatec.ocelot.events.ItsDocStatsChangedEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import com.google.inject.Inject;
import com.vistatec.ocelot.services.ITSDocStatsService;

/**
 * Table View for displaying segment ITS metadata.
 */
public class ITSDocStatsTableView extends JScrollPane implements OcelotEventQueueListener {
	private static final long serialVersionUID = 1L;

	private final DocumentStatsTableModel docStatsModel;
	protected JTable docStatsTable;
	private final TableRowSorter<DocumentStatsTableModel> sort;

	@Inject
	public ITSDocStatsTableView(ITSDocStatsService docStatsService) {
		docStatsModel = new DocumentStatsTableModel(docStatsService);
		docStatsTable = new JTable(docStatsModel);

		sort = new TableRowSorter<>(docStatsModel);
		docStatsTable.setRowSorter(sort);

		setViewportView(docStatsTable);
	}

	@Subscribe
	public void docStatsChanged(ItsDocStatsChangedEvent event) {
		docStatsModel.fireTableDataChanged();
	}

	@Subscribe
	public void errorCatModeChanged(ErrorCategoryStdChangedEvent event) {
		docStatsModel.fireTableDataChanged();
	}

	static class DocumentStatsTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		DocumentStatsTableModel(ITSDocStatsService docStatsService) {
			this.docStatsService = docStatsService;
		}

		public static final int NUMCOLS = 4;
		public String[] colNames = { "Data Category", "Type", "Value", "Count" };
		private final ITSDocStatsService docStatsService;

		@Override
		public int getRowCount() {
			return this.docStatsService.getNumStats();
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
			ITSStats stats = getItsStatistic(row);
			switch (col) {
			case 0:
				tableCell = stats.getDataCategory();
				break;

			case 1:
				if (stats instanceof LanguageQualityIssueStats) {
					tableCell = ErrorCategoryAndSeverityMapper.getInstance()
							.getErrorCategory(stats.getType());
				} else {
					tableCell = stats.getType();
				}
				break;

			case 2:
				tableCell = stats.getValue();
				break;

			case 3:
				tableCell = stats.getCount();
				break;

			default:
				throw new IllegalArgumentException("Incorrect number of columns: " + col);
			}
			return tableCell;
		}

		private ITSStats getItsStatistic(int row) {
			return this.docStatsService.getItsStatistic(row);
		}
	}
}
