package com.vistatec.ocelot.findrep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Dialog providing graphical tools for performing the find and replace
 * functionality.
 */
public class FindReplaceDialog extends JDialog implements ActionListener,
		ItemListener {

	/** The serial version UID. */
	private static final long serialVersionUID = 1L;

	/** The dialog width. */
	private static final int WIDTH = 450;

	/** The dialog height. */
	private static final int HEIGHT = 300;

	/** The buttons width. */
	private static final int BTN_WIDTH = 100;

	/** The buttons height. */
	private static final int BTN_HEIGHT = 25;

	/** The label width. */
	private static final int LBL_WIDTH = 100;

	/** The label height. */
	private static final int LBL_HEIGHT = 25;

	/** The text fields width. */
	private static final int TXT_WIDTH = 300;

	/** The text fields height. */
	private static final int TXT_HEIGHT = 25;

	/** The Settings panel width. */
	private static final int SETTING_PANELS_WIDTH = 135;

	/** The settings panel height. */
	private static final int SETTING_PANELS_HEIGHT = 100;

	/** The text displaying the number of found occurrences. */
	private static final String OCCUR_NUM_LBL_TEXT = "Found $$$ occurrences. ";

	/** The string to be replaced with the actual number of occurrences. */
	private static final String OCCUR_NUM_REPLACE_STRING = "$$$";

	/** The find next button. */
	private JButton btnFindNext;

	/** The replace button. */
	private JButton btnReplace;

	/** The replace all button. */
	private JButton btnReplaceAll;

	/** The find all button. */
	private JButton btnFindAll;

	/** The close button. */
	private JButton btnClose;

	/** The find text field. */
	private JTextField txtFind;

	/** The replace text field. */
	private JTextField txtReplace;

	/** The case sensitive check box. */
	private JCheckBox ckCaseSensitive;

	/** The whole word check box. */
	private JCheckBox ckWholeWord;

	/** The wrap search check box. */
	private JCheckBox ckWrapSearch;

	/** The source radio button. */
	private JRadioButton rbtnSource;

	/** The target radio button. */
	private JRadioButton rbtnTarget;

	/** The up radio button. */
	private JRadioButton rbtnUp;

	/** The down radio button. */
	private JRadioButton rbtnDown;

	/** The string not found label. */
	private JLabel lblStrNotFound;

	/** The end of document reached label. */
	private JLabel lblEndOfDoc;

	/** The beginning of document reached label. */
	private JLabel lblBeginOfDoc;

	/** The label displaying the number of found occurrences. */
	private JLabel lblOccNum;

	/** The replace label. */
	private JLabel lblReplace;

	/** The controller. */
	private FindAndReplaceController controller;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner window
	 * @param controller
	 *            the controller
	 */
	public FindReplaceDialog(Window owner, FindAndReplaceController controller) {

		super(owner);
		setModal(false);
		this.controller = controller;
		makeFrame();
	}

	/**
	 * Makes the frame.
	 */
	private void makeFrame() {

		setResizable(false);
		setSize(new Dimension(WIDTH, HEIGHT));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setTitle("Find/Replace");
		add(getMainComponent(), BorderLayout.CENTER);
		add(getBottomComponent(), BorderLayout.SOUTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controller.closeDialog();
			}
		});
		rbtnSource.setSelected(true);

	}

	/**
	 * Gets the component to be displayed at the bottom of the dialog.
	 * 
	 * @return the component to be displayed at the bottom of the dialog.
	 */
	private Component getBottomComponent() {

		btnFindNext = new JButton("Find Next");
		configButton(btnFindNext);
		btnReplace = new JButton("Replace");
		configButton(btnReplace);
		btnReplaceAll = new JButton("Replace All");
		configButton(btnReplaceAll);
		btnFindAll = new JButton("Find All");
		configButton(btnFindAll);
		btnClose = new JButton("Close");
		configButton(btnClose);
		lblStrNotFound = new JLabel("String not found.");
		lblStrNotFound.setForeground(Color.red);
		Color darkgreen = new Color(0, 153, 0);
		lblEndOfDoc = new JLabel("End of document reached.");
		lblEndOfDoc.setForeground(darkgreen);
		lblBeginOfDoc = new JLabel("Beginning of document reached.");
		lblBeginOfDoc.setForeground(darkgreen);
		lblOccNum = new JLabel();
		lblOccNum.setForeground(darkgreen);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(Box.createHorizontalStrut(16));
		topPanel.add(btnFindNext);
		topPanel.add(Box.createHorizontalStrut(10));
		// topPanel.add(Box.createHorizontalGlue());
		// topPanel.add(btnFindAll);
		// topPanel.add(Box.createHorizontalStrut(10));
		topPanel.add(btnReplace);
		topPanel.add(Box.createHorizontalStrut(10));
		topPanel.add(btnReplaceAll);
		topPanel.add(Box.createHorizontalGlue());

		topPanel.add(Box.createHorizontalStrut(16));

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.add(Box.createHorizontalStrut(5));
		bottomPanel.add(lblStrNotFound);
		bottomPanel.add(lblOccNum);
		bottomPanel.add(lblBeginOfDoc);
		bottomPanel.add(lblEndOfDoc);
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(btnClose);
		bottomPanel.add(Box.createHorizontalStrut(16));
		lblStrNotFound.setVisible(false);
		lblEndOfDoc.setVisible(false);
		lblBeginOfDoc.setVisible(false);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(topPanel);
		panel.add(bottomPanel);

		return panel;
	}

	/**
	 * Configures a button.
	 * 
	 * @param btn
	 *            the button.
	 */
	private void configButton(JButton btn) {

		btn.setSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
		btn.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
		btn.addActionListener(this);
	}

	/**
	 * Gets the component to be displayed in the center of the dialog.
	 * 
	 * @return the component to be displayed in the center of the dialog.
	 */
	private Component getMainComponent() {

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		JLabel lblFind = new JLabel("Find:");
		configLabel(lblFind);
		lblReplace = new JLabel("Replace with:");
		configLabel(lblReplace);
		txtFind = new JTextField();
		configTxt(txtFind);
		txtReplace = new JTextField();
		configTxt(txtReplace);

		panel.add(lblFind);
		panel.add(txtFind);
		panel.add(lblReplace);
		panel.add(txtReplace);
		panel.add(getSettingsPanel());

		return panel;
	}

	/**
	 * Gets the settings panel.
	 * 
	 * @return the settings panel.
	 */
	private Component getSettingsPanel() {

		JPanel settingsPanel = new JPanel();
		settingsPanel.add(getOptionsPanel());
		settingsPanel.add(getDirectionPanel());
		settingsPanel.add(getScopePanel());
		return settingsPanel;
	}

	/**
	 * Gets the options panel.
	 * 
	 * @return the options panel.
	 */
	private Component getOptionsPanel() {

		JPanel optionsPanel = new JPanel();
		optionsPanel.setPreferredSize(new Dimension(SETTING_PANELS_WIDTH,
				SETTING_PANELS_HEIGHT));
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
		ckCaseSensitive = new JCheckBox("Match Case");
		ckCaseSensitive.addItemListener(this);
		ckWholeWord = new JCheckBox("Whole Word");
		ckWholeWord.addItemListener(this);
		ckWrapSearch = new JCheckBox("Wrap Search");
		ckWrapSearch.addItemListener(this);
		optionsPanel.add(ckCaseSensitive);
		optionsPanel.add(ckWholeWord);
		optionsPanel.add(ckWrapSearch);
		return optionsPanel;
	}

	/**
	 * Gets the direction panel.
	 * 
	 * @return the direction panel.
	 */
	private Component getDirectionPanel() {

		JPanel directionPanel = new JPanel();
		directionPanel.setPreferredSize(new Dimension(SETTING_PANELS_WIDTH,
				SETTING_PANELS_HEIGHT));
		directionPanel
				.setLayout(new BoxLayout(directionPanel, BoxLayout.Y_AXIS));
		directionPanel.setBorder(BorderFactory.createTitledBorder("Direction"));
		rbtnDown = new JRadioButton("Down");
		rbtnDown.addItemListener(this);
		rbtnUp = new JRadioButton("Up");
		rbtnUp.addItemListener(this);
		rbtnDown.setSelected(true);
		ButtonGroup group = new ButtonGroup();
		group.add(rbtnDown);
		group.add(rbtnUp);
		directionPanel.add(rbtnDown);
		directionPanel.add(rbtnUp);
		return directionPanel;
	}

	/**
	 * Gets the scope panel.
	 * 
	 * @return the scope panel.
	 */
	private Component getScopePanel() {
		JPanel scopePanel = new JPanel();
		scopePanel.setPreferredSize(new Dimension(SETTING_PANELS_WIDTH,
				SETTING_PANELS_HEIGHT));
		scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
		scopePanel.setBorder(BorderFactory.createTitledBorder("Scope"));
		rbtnSource = new JRadioButton("Source");
		rbtnSource.addItemListener(this);
		rbtnTarget = new JRadioButton("Target");
		rbtnTarget.addItemListener(this);
		ButtonGroup group = new ButtonGroup();
		group.add(rbtnSource);
		group.add(rbtnTarget);
		scopePanel.add(rbtnSource);
		scopePanel.add(rbtnTarget);
		return scopePanel;
	}

	/**
	 * Configures a label.
	 * 
	 * @param lbl
	 *            the label
	 */
	private void configLabel(JLabel lbl) {

		lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl.setSize(new Dimension(LBL_WIDTH, LBL_HEIGHT));
		lbl.setPreferredSize(new Dimension(LBL_WIDTH, LBL_HEIGHT));
	}

	/**
	 * Configures a text field.
	 * 
	 * @param txt
	 *            the text field.
	 */
	private void configTxt(JTextField txt) {

		txt.setPreferredSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
		txt.setSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
	}

	/**
	 * Opens the dialog.
	 */
	public void open() {
		setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnClose)) {
			close();
		} else if (e.getSource().equals(btnFindNext)) {
			findNext();
		} else if (e.getSource().equals(btnReplace)) {
			replace();
		} else if (e.getSource().equals(btnReplaceAll)) {
			replaceAll();
			// } else if (e.getSource().equals(btnFindAll)) {
			// findAll();
		}
	}

	/**
	 * Sets the search result and the proper label is displayed.
	 * 
	 * @param result
	 *            the result.
	 */
	public void setResult(int result) {

		if (result == FindAndReplaceController.RESULT_FOUND) {
			lblBeginOfDoc.setVisible(false);
			lblEndOfDoc.setVisible(false);
			lblStrNotFound.setVisible(false);
		} else if (result == FindAndReplaceController.RESULT_NOT_FOUND) {
			lblBeginOfDoc.setVisible(false);
			lblEndOfDoc.setVisible(false);
			lblStrNotFound.setVisible(true);
			lblOccNum.setVisible(false);
		} else if (result == FindAndReplaceController.RESULT_END_OF_DOC_REACHED) {
			if (rbtnDown.isSelected()) {
				lblBeginOfDoc.setVisible(false);
				lblEndOfDoc.setVisible(true);
			} else {
				lblBeginOfDoc.setVisible(true);
				lblEndOfDoc.setVisible(false);
			}
			lblStrNotFound.setVisible(false);
		}
	}

	/**
	 * Displays the number of found occurrences.
	 * 
	 * @param occurNum
	 *            the number of occurrences.
	 */
	public void displayOccurrenceNum(int occurNum) {

		lblOccNum.setText(OCCUR_NUM_LBL_TEXT.replace(OCCUR_NUM_REPLACE_STRING,
				String.valueOf(occurNum)));
		setResult(FindAndReplaceController.RESULT_FOUND);
		lblOccNum.setVisible(true);
	}

	/**
	 * Hides the number of occurrences.
	 */
	public void hideOccNumber() {

		lblOccNum.setVisible(false);
		lblBeginOfDoc.setVisible(false);
		lblEndOfDoc.setVisible(false);
	}

	/**
	 * Replaces all instances.
	 */
	private void replaceAll() {

		controller.replaceAll(txtReplace.getText());
	}

	/**
	 * Replaces the last found string with the text contained in the replace
	 * text field.
	 */
	private void replace() {

		controller.replace(txtReplace.getText());
	}

	/**
	 * Finds the next occurrence of the text contained in the find text field.
	 */
	private void findNext() {
		if (!txtFind.getText().isEmpty()) {
			controller.findNext(txtFind.getText());
		}
	}

	/**
	 * Closes the dialog.
	 */
	private void close() {

		controller.closeDialog();
		setVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {

		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (e.getSource().equals(rbtnSource)) {
				enableReplaceFunctionality(false);
				controller.setSourceScope();
			} else if (e.getSource().equals(rbtnTarget)) {
				enableReplaceFunctionality(true);
				controller.setTargetScope();
			} else if (e.getSource().equals(rbtnDown)) {
				controller.setSearchDirectionDown();
			} else if (e.getSource().equals(rbtnUp)) {
				controller.setSearchDirectionUp();
			}
		}
		if (e.getSource().equals(ckCaseSensitive)) {
			controller.setCaseSensitive(ckCaseSensitive.isSelected());
		} else if (e.getSource().equals(ckWholeWord)) {
			controller.setWholeWord(ckWholeWord.isSelected());
		} else if (e.getSource().equals(ckWrapSearch)) {
			controller.setWrapSearch(ckWrapSearch.isSelected());
		}
	}

	/**
	 * Enables the replace functionality.
	 * 
	 * @param enable
	 *            boolean stating if the replace functionality is enabled.
	 */
	private void enableReplaceFunctionality(boolean enable) {

		txtReplace.setEnabled(enable);
		btnReplace.setEnabled(enable);
		btnReplaceAll.setEnabled(enable);
		lblReplace.setEnabled(enable);
	}

	/**
	 * Gets the selected scope.
	 * 
	 * @return the selected scope.
	 */
	public int getSelectedScope() {

		if (rbtnSource.isSelected()) {
			return WordFinder.SCOPE_SOURCE;
		} else {
			return WordFinder.SCOPE_TARGET;
		}
	}
}
