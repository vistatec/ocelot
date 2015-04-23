package com.vistatec.ocelot.tm.gui.match;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class MatchScoreRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -8959470309220918429L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		JLabel comp = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		JTextPane textPane = new JTextPane();
		textPane.setBackground(comp.getBackground());
		textPane.setForeground(Color.white);
		textPane.setFont(comp.getFont());
		if(comp != null && value != null){
			int score = (int) value;
			Color highlightColor = comp.getBackground();
			if(score == 100){
				highlightColor = new Color(16, 164, 87);
			} else if (score >= 95 ){
				highlightColor = new Color(95, 164, 16);
			} else if (score >= 85){
				highlightColor = new Color(232, 206, 11);
			}  else if (score >= 75){
				highlightColor = new Color(232, 143, 11);
			} else if (score >= 65){
				highlightColor = new Color(232, 99, 11);
			} else {
				highlightColor = new Color(232, 51, 11);
			}
			
			DefaultHighlightPainter painter = new DefaultHighlightPainter(
					highlightColor);
			StyledDocument style = textPane.getStyledDocument();
			SimpleAttributeSet rightAlign = new SimpleAttributeSet();
			StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
			try {
				style.insertString(style.getLength(), " " + value + "% ", rightAlign);
				textPane.getHighlighter().addHighlight(0, textPane.getText().length(), painter);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return textPane;
	}

}
