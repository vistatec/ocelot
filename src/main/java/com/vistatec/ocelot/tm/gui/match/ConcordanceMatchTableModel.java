package com.vistatec.ocelot.tm.gui.match;

import java.util.List;

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.tm.TmMatch;

/**
 * Data model assigned to the table displayed in the Concordance Search panel.
 */
public class ConcordanceMatchTableModel extends TmMatchTableModel {

	/** Serial version UID. */
	private static final long serialVersionUID = 2115553091508295586L;

	/** The source column index. */
	private static final int SOURCE_COL = 0;

	/** The match score column index. */
	private static final int MATCH_SCORE_COL = 1;

	/** The target column index. */
	private static final int TARGET_COL = 2;

	/** The TM name column index. */
	private static final int TM_NAME_COL = 3;

	/**
	 * Constructor.
	 * 
	 * @param model
	 *            the list of TMs being the actual model.
	 */
	public ConcordanceMatchTableModel(final List<TmMatch> model) {

		super(model, new String[] { "", "", "", "" });
	}

	/**
	 * Gets the class of the objects displayed in the queried column.
	 * 
	 * @return the class of the objects displayed in the queried column
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {

		Class<?> clazz = Object.class;
		switch (columnIndex) {

		case TM_NAME_COL:
			clazz = String.class;
			break;
		case SOURCE_COL:
		case TARGET_COL:
			clazz = SegmentVariant.class;
			break;
		case MATCH_SCORE_COL:
			clazz = Integer.class;
			break;
		default:
			break;
		}
		return clazz;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {

		Object retValue = null;
		if (model != null && row < model.size()) {
			TmMatch currMatch = model.get(row);
			switch (column) {
			case SOURCE_COL:
				retValue = currMatch.getSource();
				break;
			case MATCH_SCORE_COL:
				retValue = (int) (currMatch.getMatchScore());
				break;
			case TARGET_COL:
				retValue = currMatch.getTarget();
				break;
			case TM_NAME_COL:
				retValue = currMatch.getTmOrigin();
				break;
			default:
				break;
			}
		}
		return retValue;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.tm.gui.match.TmMatchTableModel#getSourceColumnIdx()
	 */
	@Override
	public int getSourceColumnIdx() {
		return SOURCE_COL;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.tm.gui.match.TmMatchTableModel#getTargetColumnIdx()
	 */
	@Override
	public int getTargetColumnIdx() {

		return TARGET_COL;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.tm.gui.match.TmMatchTableModel#getMatchScoreColumnIdx()
	 */
	@Override
	public int getMatchScoreColumnIdx() {
		return MATCH_SCORE_COL;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.tm.gui.match.TmMatchTableModel#getTmColumnIdx()
	 */
	@Override
	public int getTmColumnIdx() {
		return TM_NAME_COL;
	}

	/**
	 * The table model is not editable. It always returns <code>false</code>
	 * regardless of parameter values.
	 * 
	 * @return <code>false</code>.
	 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return column == TARGET_COL;
	}

}
