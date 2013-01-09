package com.spartansoftwareinc;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * ITS 2.0 Language Quality Issue Data Category creation form.
 * Follows: http://www.w3.org/International/multilingualweb/lt/drafts/its20/its20.html#lqissue
 *
 */
public class LanguageQualityIssue extends JPanel implements Runnable, ActionListener {
	/** Default serial ID */
	private static final long serialVersionUID = 3L;
	private JFrame frame;
	private JComboBox typeList;
	private JTextArea commentContent;
	private SpinnerModel severityRating;
	private JTextField profileRefLink;
	private JRadioButton enabledTrue, enabledFalse;
	private boolean enabled = true;
	private JButton save;
	private JButton cancel;

	public LanguageQualityIssue() {
		setLayout(new GridBagLayout());
		
		// Initialize default grid bag layout
		GridBagConstraints gridBag = new GridBagConstraints();
		gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
		gridBag.insets = new Insets(5,5,5,5);
		gridBag.gridwidth = 1;
		
		// Add UI Components from top to bottom
		addTranslationSegment(gridBag);
		addType(gridBag);
		addComments(gridBag);
		addSeverity(gridBag);
		addProfileReference(gridBag);
		addEnabled(gridBag);
		addSave(gridBag);
	}
	
	private void addTranslationSegment(GridBagConstraints gridBag) {
		JLabel segmentLabel = new JLabel("Translation Segment: ");
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		add(segmentLabel, gridBag);
		
		// TODO: Retrieve selected translation segment text.
		JLabel sourceTargetSegment = new JLabel("heat sink -> dissipateur de chaleur");
		gridBag.gridx = 1;
		gridBag.gridy = 0;
		add(sourceTargetSegment, gridBag);
	}
	
	private void addType(GridBagConstraints gridBag) {
		JLabel type = new JLabel("Type: ");
		gridBag.gridx = 0;
		gridBag.gridy = 1;
		gridBag.weightx = 0.25;
		add(type, gridBag);
		
		String[] types = {"terminology", "mistranslation", "omission", "untranslated", 
				"addition", "duplication", "inconsistency", "grammar", "legal", 
				"register", "locale-specific-content", "locale-violation", "style",
				"characters", "misspelling", "typographical", "formatting",
				"inconsistent-entities", "numbers", "markup", "pattern-problem",
				"whitespace", "internationalization", "length", "uncategorized",
				"other"};
		typeList = new JComboBox(types);
		gridBag.gridx = 1;
		gridBag.gridy = 1;
		gridBag.weightx = 0.5;
		add(typeList, gridBag);
	}
	
	private void addComments(GridBagConstraints gridBag) {
		JLabel comment = new JLabel("Comment: ");
		gridBag.gridx = 0;
		gridBag.gridy = 2;
		gridBag.weightx = 0.25;
		add(comment, gridBag);
		
		commentContent = new JTextArea(5,30);
		commentContent.setEditable(true);
		commentContent.setLineWrap(true);
		JScrollPane commentScroll = new JScrollPane(commentContent);
		gridBag.gridx = 1;
		gridBag.gridy = 2;
		gridBag.weightx = 0.5;
		add(commentScroll, gridBag);
	}
	
	private void addSeverity(GridBagConstraints gridBag) {
		JLabel severity = new JLabel("Severity: ");
		gridBag.gridx = 0;
		gridBag.gridy = 3;
		gridBag.weightx = 0.25;
		add(severity, gridBag);
		
		severityRating = new SpinnerNumberModel(0, 0, 100, 1);
		JSpinner spinner = new JSpinner(severityRating);
		severity.setLabelFor(spinner);
		gridBag.gridx = 1;
		gridBag.gridy = 3;
		gridBag.weightx = 0.5;
		add(spinner, gridBag);
	}
	
	private void addProfileReference(GridBagConstraints gridBag) {
		JLabel profile = new JLabel("Profile Reference: ");
		gridBag.gridx = 0;
		gridBag.gridy = 4;
		gridBag.weightx = 0.25;
		add(profile, gridBag);
		
		// TODO: IRI validation?
		profileRefLink = new JTextField(30);
		gridBag.gridx = 1;
		gridBag.gridy = 4;
		gridBag.weightx = 0.5;
		add(profileRefLink, gridBag);
	}
	
	private void addEnabled(GridBagConstraints gridBag) {
		JLabel enabled = new JLabel("Enabled: ");
		gridBag.gridx = 0;
		gridBag.gridy = 5;
		gridBag.weightx = 0.25;
		add(enabled, gridBag);
		
		enabledTrue = new JRadioButton("Yes");
		enabledTrue.setSelected(true);
		enabledTrue.addActionListener(this);
		
		enabledFalse = new JRadioButton("No");
		enabledFalse.addActionListener(this);
		
		ButtonGroup group = new ButtonGroup();
		group.add(enabledTrue);
		group.add(enabledFalse);
		
		JPanel enabledPanel = new JPanel();
		enabledPanel.add(enabledTrue);
		enabledPanel.add(enabledFalse);
		gridBag.gridx = 1;
		gridBag.gridy = 5;
		gridBag.weightx = 0.5;
		add(enabledPanel, gridBag);
	}
	
	private void addSave(GridBagConstraints gridBag) {
		save = new JButton("Save");
		save.addActionListener(this);
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		
		JPanel actionPanel = new JPanel();
		actionPanel.add(save);
		actionPanel.add(cancel);
		gridBag.gridx = 1;
		gridBag.gridy = 6;
		add(actionPanel, gridBag);
	}
	
	@Override
	public void run() {
		frame = new JFrame("Language Quality Issue");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// TODO: cleanup
			}
		});
		
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancel) {
			frame.dispose();
		} else if (e.getSource() == save) {
			// TODO: Save ITS LQI metadata.
			System.out.println("Type: "+typeList.getSelectedItem());
			System.out.println("Comment: "+commentContent.getText());
			System.out.println("Severity: "+severityRating.getValue().toString());
			System.out.println("Profile Ref: "+profileRefLink.getText());
			System.out.println("Enabled: "+(enabled ? "true" : "false"));
			frame.dispose();
		} else if (e.getSource() == enabledTrue) {
			enabled = true;
		} else if (e.getSource() == enabledFalse) {
			enabled = false;
		}
	}

}
