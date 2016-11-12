package com.vistatec.ocelot.tm.gui.match;

import java.util.Collections;
import java.util.List;

import com.vistatec.ocelot.segment.editdistance.EditDistance;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.tm.TmMatch;

/**
 * Table data model associated to the table displayed in the TM Translations
 * panel.
 */
public class TranslationsMatchTableModel extends TmMatchTableModel {

	/** Serial version UID. */
	private static final long serialVersionUID = -8386866780750598834L;

	/** The segment number column index. */
	public static final int SEG_NUM_COL = 0;
	/** The source column index. */
	public static final int SOURCE_COL = 1;
	/** The match score column index. */
	public static final int MATCH_SCORE_COL = 2;
	/** The target column index. */
	public static final int TARGET_COL = 3;
	/** The TM name column index. */
	public static final int TM_NAME_COL = 4;

	/** The segment for which matches were found. */
	private OcelotSegment segment;

	/**
	 * Constructor.
	 * 
	 * @param model
	 *            the TM list being the actual model.
	 */
	public TranslationsMatchTableModel(final List<TmMatch> model) {

		super(model, new String[] { "", "", "", "", "" });
	}

	/**
	 * Gets the class of objects displayed in the parameter column.
	 * 
	 * @return the class of the objects at the queried column.
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
			clazz = List.class;
			break;
		case TARGET_COL:
			clazz = SegmentVariant.class;
			break;
		case MATCH_SCORE_COL:
		case SEG_NUM_COL:
			clazz = Integer.class;
			break;
		default:
			break;
		}
		return clazz;
	}

	/**
	 * The model is not editable. It always returns <code>false</code>.
	 * 
	 * @return <code>false</code>
	 * @see com.vistatec.ocelot.tm.gui.match.TmMatchTableModel#isCellEditable(int,
	 *      int)
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return column == TARGET_COL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {

		Object retValue = null;
		if (model != null && row < model.size()) {
			TmMatch currMatch = model.get(row);
			switch (column) {
			case SOURCE_COL:
				retValue = getDiff(currMatch.getSource());
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
			case SEG_NUM_COL:
				retValue = model.indexOf(currMatch) + 1;
				break;
			default:
				break;
			}
		}
		return retValue;
	}

	List<String> getDiff(SegmentVariant matchSource) {
		if (segment == null) {
			return Collections.singletonList(matchSource.getDisplayText());
		} else {
			return EditDistance.styleTextDifferences(matchSource, segment.getSource());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.tm.gui.match.TmMatchTableModel#getSourceColumnIdx()
	 */
	@Override
	public int getSourceColumnIdx() {
		return SOURCE_COL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.tm.gui.match.TmMatchTableModel#getTargetColumnIdx()
	 */
	@Override
	public int getTargetColumnIdx() {
		return TARGET_COL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.tm.gui.match.TmMatchTableModel#getMatchScoreColumnIdx
	 * ()
	 */
	@Override
	public int getMatchScoreColumnIdx() {
		return MATCH_SCORE_COL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.tm.gui.match.TmMatchTableModel#getTmColumnIdx()
	 */
	@Override
	public int getTmColumnIdx() {
		return TM_NAME_COL;
	}

	/**
	 * Gets the segment number column index.
	 * 
	 * @return the segment number column index.
	 */
	public int getSegmentNumColumnIdx() {
		return SEG_NUM_COL;
	}
	
	/**
	 * Sets the TM list and reference segment.
	 * 
	 * @param segment
	 *			the segment for which matches were found. Can be null.
	 * @param model
	 *			the TM list.
	 */
	public void setModel(OcelotSegment segment, List<TmMatch> model) {
		this.segment = segment;
		setModel(model);
	}

}
