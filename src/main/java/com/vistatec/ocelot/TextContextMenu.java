package com.vistatec.ocelot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.vistatec.ocelot.events.ConcordanceSearchEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;

public class TextContextMenu extends JPopupMenu implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -255396661863113048L;

	private JMenuItem mnuConcordance;
	
	private OcelotEventQueue eventQueue;
	
	private String selectedText;
	
	public TextContextMenu(final OcelotEventQueue eventQueue, final String selectedText, final boolean target) {
		
		this.eventQueue = eventQueue;
		this.selectedText = selectedText;
		buildMenu(target);
	}
	
	private void buildMenu(boolean target){		
		
		if(!target){		
			mnuConcordance = new JMenuItem("Concordance Search");		
			mnuConcordance.addActionListener(this);		
			add(mnuConcordance);		
		}		
				
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource().equals(mnuConcordance)){
			eventQueue.post(new ConcordanceSearchEvent(selectedText));
		}
	}
}
