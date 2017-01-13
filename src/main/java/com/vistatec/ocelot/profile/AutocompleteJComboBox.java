package com.vistatec.ocelot.profile;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;


/**
 * A combo box for String objects, providing the auto completion feature.
 */
public class AutocompleteJComboBox extends JComboBox<String> {

	private static final long serialVersionUID = -6590880332131797609L;
	
	private List<String> model;
	
	private StringSearcher searcher;
	
	private DocumentListener docListener;
	
	public AutocompleteJComboBox(List<String> model) {

		this.model = model;
		this.searcher = new StringSearcher(this);
		setEditable(true);
		addAllItems();
		Component c = getEditor().getEditorComponent();
		if (c instanceof JTextComponent) {

			final JTextComponent tc = (JTextComponent) c;
			docListener = new AutoCompletionDocumentListener(this, tc);
			tc.getDocument().addDocumentListener(docListener);
			tc.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent e) {

				}

				@Override
				public void focusGained(FocusEvent e) {
					if (tc.getText().length() > 0) {
						setPopupVisible(true);
					}
				}
			});
		} else {
			throw new IllegalStateException(
			        "Editing component is not a JTextComponent!");
		}
	}

	private void addAllItems() {

		removeAllItems();
		List<String> founds = searcher.search("");
		if (founds != null) {
			Collections.sort(founds);
			for (String s : founds) {
				addItem(s);
			}
		}

	}
	
	public List<String> getItems(){
		return model;
	}
	
	
	@Override
	public void setSelectedItem(Object anObject) {
		JTextComponent tc = (JTextComponent)getEditor().getEditorComponent();
		tc.getDocument().removeDocumentListener(docListener);
		addAllItems();
	    super.setSelectedItem(anObject);
	    tc.getDocument().addDocumentListener(docListener);
	}
	
}


class AutoCompletionDocumentListener implements DocumentListener {
	
	private AutocompleteJComboBox comboBox;
	
	private JTextComponent tc;
	
	private StringSearcher searcher;
	
	public AutoCompletionDocumentListener(AutocompleteJComboBox comboBox, JTextComponent tc) {
		
		this.comboBox = comboBox;
		this.tc = tc;
		this.searcher = new StringSearcher(comboBox);
    }
	
	@Override
	public void removeUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		update();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

	}

	private boolean containsString(List<String> list, String str){
		
		boolean found = false;
		Iterator<String> listIt = list.iterator();
		while(listIt.hasNext() && !found){
			found = listIt.next().equalsIgnoreCase(str);
		}
		return found;
	}
	
	private void update() {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				List<String> founds = searcher.search(tc.getText());
				Collections.sort(founds);

				comboBox.setEditable(false);
				comboBox.removeAllItems();
				if (!containsString(founds, tc.getText())) {
					comboBox.addItem(tc.getText());
				}
				for (String s : founds) {
					comboBox.addItem(s);
				}
				comboBox.setEditable(true);
				comboBox.setPopupVisible(true);
				tc.requestFocus();
			}
		});
	}
	
	
}

class StringSearcher {
	
	
	private AutocompleteJComboBox comboBox;
	
	public StringSearcher(AutocompleteJComboBox comboBox) {
		
		this.comboBox = comboBox;
    }

	public List<String> search(String text){
		
		List<String> founds = new ArrayList<String>();
		if(comboBox.getItems() != null){
			for(String str: comboBox.getItems()){
				if(str.toLowerCase().startsWith(text.toLowerCase())){
					founds.add(str);
				}
			}
		}
		return founds;
	}
}