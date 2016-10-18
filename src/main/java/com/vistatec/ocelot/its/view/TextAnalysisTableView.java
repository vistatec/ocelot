package com.vistatec.ocelot.its.view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.TextAnalysisAddedEvent;
import com.vistatec.ocelot.its.model.TextAnalysisMetaData;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.view.SegmentAttributeTablePane;

/**
 * Table displaying text-analysis meta data.
 */
public class TextAnalysisTableView extends SegmentAttributeTablePane<TextAnalysisTableView.TaITSTableModel> {

	/** The serial version UID. */
    private static final long serialVersionUID = 1L;

    
    @Subscribe		
    public void handleTextAnalysisAddedEvent(TextAnalysisAddedEvent event){		
    			
    	getTableModel().fireTableDataChanged();		
    }
    
    /*
     * (non-Javadoc)
     * @see com.vistatec.ocelot.segment.view.SegmentAttributeTablePane#createTableModel()
     */
	@Override
    protected TaITSTableModel createTableModel() {
	    return new TaITSTableModel();
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.vistatec.ocelot.segment.view.SegmentAttributeTablePane#segmentSelected(com.vistatec.ocelot.segment.model.OcelotSegment)
	 */
	@Override
	protected void segmentSelected(OcelotSegment seg) {
		List<TextAnalysisMetaData> taMetaData = seg.getTextAnalysis();
		getTableModel().setRows(taMetaData);
	}
	
	/**
	 * Table model for the term meta data table.
	 */
	public class TaITSTableModel extends AbstractTableModel {

		/** The serial version UID. */
        private static final long serialVersionUID = 1L;

        /** The entity column index. */
		private static final int ENTITY_COL = 0;

		/** The confidence column index. */
		private static final int TA_CONFIDENCE_COL = 1;

		/** The class ref column index. */
		private static final int TA_CLASS_REF_COL = 2;

		/** The ident ref column index. */
		private static final int TA_IDENT_REF_COL = 3;

		/** The annotators ref column index. */
		private static final int ANNOT_REF_COL = 4;
		
		/** The segment part column index. */
		private static final int SEGM_PART_COL = 5;

		/** The columns names. */
		private final String[] colNames = { "Entity", "taConfidence",
		        "taClassRef", "taIdentRef", "annotatorsRef", "Segment Part" };

		/** The rows. */
		private List<TextAnalysisMetaData> rows;

		/**
		 * Sets the rows.
		 * @param rows the rows.
		 */
		public void setRows(List<TextAnalysisMetaData> rows) {

			this.rows = rows;
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			int rowCount = 0;
			if (rows != null) {
				rowCount = rows.size();
			}
			return rowCount;
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
			if (column < colNames.length) {
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
			if (rows != null && rowIndex < rows.size()) {
				TextAnalysisMetaData currMetaData = rows.get(rowIndex);
				switch (columnIndex) {
				case ANNOT_REF_COL:
					retValue = currMetaData.getTaAnnotatorsRef();
					break;
				case ENTITY_COL:
					retValue = currMetaData.getEntity();
					break;
				case TA_CLASS_REF_COL:
					retValue = currMetaData.getTaClassRef();
					break;
				case TA_CONFIDENCE_COL:
					retValue = currMetaData.getTaConfidence();
					break;
				case TA_IDENT_REF_COL:
					retValue = currMetaData.getTaIdentRef();
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
