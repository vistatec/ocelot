package com.vistatec.ocelot.its.view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.vistatec.ocelot.its.model.TerminologyMetaData;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.view.SegmentAttributeTablePane;

/**
 * Table displaying term meta data.
 */
public class TermsTableView extends SegmentAttributeTablePane<TermsTableView.TermITSTableModel> {

	/** The serial version UID. */
    private static final long serialVersionUID = -3076817521019788334L;

    /*
     * (non-Javadoc)
     * @see com.vistatec.ocelot.segment.view.SegmentAttributeTablePane#createTableModel()
     */
	@Override
    protected TermITSTableModel createTableModel() {
	    return new TermITSTableModel();
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.segment.view.SegmentAttributeTablePane#segmentSelected(com.vistatec.ocelot.segment.model.OcelotSegment)
	 */
	@Override
	protected void segmentSelected(OcelotSegment seg) {
		List<TerminologyMetaData> termsMetaData = seg.getTerms();
		getTableModel().setRows(termsMetaData);
	}
	
	/**
	 * Table model for the terms meta data table.
	 */
	public class TermITSTableModel extends AbstractTableModel {

		/** the serial version UID.  */
        private static final long serialVersionUID = -8973682252927892105L;

        /** The term column index. */
		private static final int TERM_COL = 0;
		
		/** The source column index. */
		private static final int SOURCE_COL = 1;
		
		/** The target column index. */
		private static final int TARGET_COL = 2;
		
		/** The domain column index. */
		private static final int DOMAIN_COL = 3;
		
		/** The annotators ref column index. */
		private static final int ANNOT_REF_COL = 4;
		
		/** The segment part column index. */
		private static final int SEGM_PART_COL = 5;
		
		/** The columns names.*/
		private final String[] colNames = { "Term", "Source", "Target",
		        "Domain", "annotatorsRef", "Segment Part" };

		/** The table rows. */
		private List<TerminologyMetaData> rows;
		
		/**
		 * Sets the rows.
		 * @param rows the rows.
		 */
		public void setRows(List<TerminologyMetaData> rows){
			this.rows = rows;
		}
		
		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			int count = 0;
			if(rows != null){
				count = rows.size();
			}
			return count;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return colNames.length;
		}
		
		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(int column) {
			String colName = "";
			if(column<colNames.length){
				colName = colNames[column];
			}
		    return colName;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			
			Object retValue = null;
			if(rows != null && rowIndex < rows.size()){
				TerminologyMetaData currMetaData = rows.get(rowIndex);
				switch (columnIndex) {
				case ANNOT_REF_COL:
					retValue = currMetaData.getAnnotatorsRef();
					break;
				case DOMAIN_COL:
					retValue = currMetaData.getSense();
					break;
				case SOURCE_COL:
					retValue = currMetaData.getTermSource();
					break;
				case TARGET_COL:
					retValue = currMetaData.getTermTarget();
					break;
				case TERM_COL:
					retValue = currMetaData.getTerm();
					break;
				case SEGM_PART_COL:
					retValue = currMetaData.getSegPart();
					break;
				default:
					break;
				}
			}
			return retValue;
		}

	}
	
}
