package com.vistatec.ocelot.tm.gui.match;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellEditor;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.view.SegmentTextCell;
import com.vistatec.ocelot.segment.view.SegmentView.TextPopupMenuListener;
import com.vistatec.ocelot.tm.okapi.PensieveTmMatch.PensieveSegmentVariant;


public class ReadOnlyCellEditor extends AbstractCellEditor implements TableCellEditor {


	/**
	 * 
	 */
	private static final long serialVersionUID = -591391978033697647L;
	
    private final SegmentTextCell editorComp = SegmentTextCell.createCell();

	@Override
	public Object getCellEditorValue() {
		
		return editorComp.getVariant();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {
		
//		Component comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
		SegmentTextCell renderedComp = (SegmentTextCell)table.getCellRenderer(row, column).getTableCellRendererComponent(table, value, isSelected, true, row, column);
        editorComp.setVariant(row, renderedComp.getVariant().createCopy(), false);
		editorComp.setBackground(table.getSelectionBackground());
		editorComp.setSelectionColor(Color.BLUE);
		editorComp.setSelectedTextColor(Color.WHITE);
		editorComp.getCaret().setVisible(true);
		editorComp.setEditable(false);
//		editorComp.addMouseListener(new MouseAdapter() {
//			
//			@Override
//			public void mouseExited(MouseEvent e) {
//				fireEditingStopped();
//			}
//		});
//		Component component = super.getta
//		OcelotSegment seg = segmentTableModel.getSegment(sort.convertRowIndexToModel(row));
//		editorComponent = new SegmentTextCell(seg.getSource().createCopy(), false);
//		editorComponent.setBackground(table.getSelectionBackground());
//		editorComponent.setSelectionColor(Color.BLUE);
//		editorComponent.setSelectedTextColor(Color.WHITE);
//        editorComponent.setEditable(false);
//        editorComponent.getCaret().setVisible(true);
//        editorComponent.addMouseListener(new TextPopupMenuListener());
//        editorComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "finish");
//        editorComponent.getActionMap().put("finish", new AbstractAction() {
//       	 private static final long serialVersionUID = 1L;
//       	 
//       	 @Override
//       	 public void actionPerformed(ActionEvent e) {
//       		 fireEditingStopped();
//       	 }
//        });
       
        return editorComp;
	}
	
	 @Override
        public boolean isCellEditable(EventObject anEvent) {
//            if (anEvent instanceof MouseEvent) {
//                // Override normal behavior and only allow double-click to edit the
//                // cell
//                return ((MouseEvent)anEvent).getClickCount() >= 2;
//            }
//            if (anEvent instanceof ActionEvent) {
//                return true;
//            }
            return true;
        }
	
}
