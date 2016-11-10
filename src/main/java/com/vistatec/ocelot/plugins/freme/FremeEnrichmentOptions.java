package com.vistatec.ocelot.plugins.freme;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FremeEnrichmentOptions {

	public static final int DELETE_OPTION = 0;
	
	public static final int MERGE_OPTION = 1;
	
	public static final int CANCEL_OPTION = 2;
	
	public static int showConfirmDialog(Component parentComponent){
		
		Object[] options = {"Delete", "Merge", "Cancel"};
		return JOptionPane.showOptionDialog(parentComponent, getMessageComponent(), "Enrichment Options", 0, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	}
	
	private static Component getMessageComponent(){
		
//		JOptionPane.sh
		String htmlMessage = "<html>Existing enrichments have been detected.<br/><p>Please choose one of the following options:<br/>"
				+ "<b>Delete - </b>delete existing enrichments and re-run the FREME pipeline;<br/> "
				+ "<b>Merge - </b>re-run the FREME pipeline and merge new enrichments with the existing ones.  </html>";
		JLabel message = new JLabel(htmlMessage);
		message.setFont(new Font(message.getFont().getFontName(), Font.PLAIN, message.getFont().getSize()));
		JPanel panel = new JPanel();
		panel.add(message);
		return panel;
	}
	
	
//	public static void main(String[] args) {
//		
////		JOptionPane.s
//		JFrame frame = new JFrame();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
//		System.out.println(showConfirmDialog(frame));
//	}
}
