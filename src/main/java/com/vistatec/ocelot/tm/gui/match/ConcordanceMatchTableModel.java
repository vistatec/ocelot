package com.vistatec.ocelot.tm.gui.match;

import java.util.List;

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.tm.TmMatch;

public class ConcordanceMatchTableModel extends TmMatchTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2115553091508295586L;

	private static final int SOURCE_COL = 0;

	private static final int MATCH_SCORE_COL = 1;

	private static final int TARGET_COL = 2;

	private static final int TM_NAME_COL = 3;

	public ConcordanceMatchTableModel(final List<TmMatch> model) {

		super(model, new String[]{"","","",""});
	}

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

	@Override
	public int getSourceColumnIdx() {
		return SOURCE_COL;
	}

	@Override
	public int getTargetColumnIdx() {

		return TARGET_COL;
	}

	@Override
	public int getMatchScoreColumnIdx() {
		return MATCH_SCORE_COL;
	}

	@Override
	public int getTmColumnIdx() {
		return TM_NAME_COL;
	}

}
