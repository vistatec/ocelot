package com.vistatec.ocelot.spellcheck;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Dialog providing graphical tools for checking and correcting spelling.
 */
public class SpellcheckDialog extends JDialog implements ActionListener {

	/** The serial version UID. */
	private static final long serialVersionUID = 1L;

	/** The dialog width. */
    private static final int WIDTH = 550;

	/** The dialog height. */
	private static final int HEIGHT = 300;

	/** The buttons width. */
    private static final int BTN_WIDTH = 150;

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

    /** The learn button. */
    private JButton btnLearn;

    /** The ignore button. */
    private JButton btnIgnore;

    /** The ignore all button. */
    private JButton btnIgnoreAll;

	/** The replace button. */
	private JButton btnReplace;

	/** The replace all button. */
	private JButton btnReplaceAll;

    /** The add ITS button. */
    private JButton btnAddIts;

    /** The add ITS all button. */
    private JButton btnAddItsAll;

    /** The unknown word text field. */
    private JTextField txtUnknownWord;

    /** The replacement word text field. */
    private JTextField txtReplaceWord;

    /** The suggested replacements. */
    private JList<String> lstSuggestions;

	/** The controller. */
	private SpellcheckController controller;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner window
	 * @param controller
	 *            the controller
	 */
	public SpellcheckDialog(Window owner, SpellcheckController controller) {

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
        setTitle("Spellcheck");
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
	}

	/**
	 * Gets the component to be displayed at the bottom of the dialog.
	 * 
	 * @return the component to be displayed at the bottom of the dialog.
	 */
	private Component getBottomComponent() {

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        Insets buttonsInsets = new Insets(5, 5, 5, 5);
        {
            btnIgnore = new JButton("Ignore");
            configButton(btnIgnore);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = buttonsInsets;
            buttonsPanel.add(btnIgnore, c);
        }
        {
            btnIgnoreAll = new JButton("Ignore All");
            configButton(btnIgnoreAll);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = buttonsInsets;
            buttonsPanel.add(btnIgnoreAll, c);

        }
        {
            btnReplace = new JButton("Replace");
            configButton(btnReplace);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = buttonsInsets;
            buttonsPanel.add(btnReplace, c);
        }
        {
            btnReplaceAll = new JButton("Replace All");
            configButton(btnReplaceAll);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = buttonsInsets;
            buttonsPanel.add(btnReplaceAll, c);
        }
        {
            btnAddIts = new JButton("Add ITS");
            configButton(btnAddIts);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = buttonsInsets;
            buttonsPanel.add(btnAddIts, c);
        }
        {
            btnAddItsAll = new JButton("Add ITS All");
            configButton(btnAddItsAll);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = buttonsInsets;
            buttonsPanel.add(btnAddItsAll, c);
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonsPanel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
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

		JPanel panel = new JPanel(new GridBagLayout());
        {
            JLabel lblFind = new JLabel("Unknown word:");
            configLabel(lblFind);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.EAST;
            panel.add(lblFind, c);
        }
        {
            txtUnknownWord = new JTextField();
            configTxt(txtUnknownWord);
            txtUnknownWord.setEditable(false);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            c.ipadx = TXT_WIDTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(5, 10, 5, 10);
            panel.add(txtUnknownWord, c);
        }
        {
            btnLearn = new JButton("Learn");
            configButton(btnLearn);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = 0;
            panel.add(btnLearn);
        }
        {
            JLabel lblReplace = new JLabel("Replace with:");
            configLabel(lblReplace);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.anchor = GridBagConstraints.EAST;
            panel.add(lblReplace, c);
        }
        {
            txtReplaceWord = new JTextField();
            configTxt(txtReplaceWord);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 1;
            c.ipadx = TXT_WIDTH;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(5, 10, 5, 10);
            panel.add(txtReplaceWord, c);
        }
        {
            JLabel lblSuggestions = new JLabel("Suggestions:");
            configLabel(lblSuggestions);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2;
            c.anchor = GridBagConstraints.NORTHEAST;
            c.insets = new Insets(5, 0, 0, 0);
            panel.add(lblSuggestions, c);
        }
        {
            lstSuggestions = new JList<>();
            JScrollPane spSuggestions = new JScrollPane(lstSuggestions);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 2;
            c.ipadx = TXT_WIDTH;
            c.ipady = lstSuggestions.getFont().getSize() * 5;
            c.insets = new Insets(5, 10, 5, 10);
            panel.add(spSuggestions, c);
        }
		return panel;
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

        if (e.getSource().equals(btnLearn)) {
            // learn();
		} else if (e.getSource().equals(btnReplace)) {
			replace();
		} else if (e.getSource().equals(btnReplaceAll)) {
			replaceAll();
		}
	}

	/**
	 * Replaces all instances.
	 */
	private void replaceAll() {
	}

	/**
	 * Replaces the last found string with the text contained in the replace
	 * text field.
	 */
	private void replace() {
	}

	/**
	 * Closes the dialog.
	 */
	private void close() {

		controller.closeDialog();
		setVisible(false);
	}

}
