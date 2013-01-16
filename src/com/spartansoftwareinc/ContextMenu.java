package com.spartansoftwareinc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * Translation segment context menu. Lists potential actions when clicking
 * on a translation segment.
 */
public class ContextMenu extends JPopupMenu implements ActionListener {
	/** Default serial ID */
	private static final long serialVersionUID = 2L;
	JMenuItem languageQualityIssue;
	
	public ContextMenu() {
		languageQualityIssue = new JMenuItem("Language Quality Issue");
		languageQualityIssue.addActionListener(this);
		add(languageQualityIssue);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == languageQualityIssue) {
//			LanguageQualityIssueView lqi = new LanguageQualityIssueView();
//			SwingUtilities.invokeLater(lqi);
		}
	}
}
