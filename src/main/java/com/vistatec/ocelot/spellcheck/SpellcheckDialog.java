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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.vistatec.ocelot.lqi.model.LQISeverity;

/**
 * Dialog providing graphical tools for checking and correcting spelling.
 */
public class SpellcheckDialog extends JDialog implements ActionListener, ListSelectionListener {

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

    /** The add LQI button. */
    private JButton btnAddLqi;

    /** The add LQI all button. */
    private JButton btnAddLqiAll;

    /** The LQI severity selector */
    private JComboBox<LQISeverity> cbxSeverity;

    /** The unknown word text field. */
    private JTextField txtUnknownWord;

    /** The replacement word text field. */
    private JTextField txtReplaceWord;

    /** The progress bar shown while loading. */
    private JProgressBar progressBar;

    /** The suggested replacements. */
    private JList<String> lstSuggestions;

    /** The label for messages to the user. */
    private JLabel lblMessage;

	/** The controller. */
	private SpellcheckController controller;

    /** The currently displayed check result */
    private CheckResult result;

	/**
     * Constructor.
     * 
     * @param owner
     *            the owner window
     * @param controller
     *            the controller
     * @param list
     *            the severity settings for LQIs
     */
    public SpellcheckDialog(Window owner, SpellcheckController controller, List<LQISeverity> severities) {

		super(owner);
		setModal(false);
		this.controller = controller;
		makeFrame();
        setSeverities(severities);
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
        setAllEnabled(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controller.closeDialog();
			}
		});
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                if (!isChild(e.getOppositeWindow())) {
                    setAllEnabled(false);
                }
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (result != null && !isChild(e.getOppositeWindow())) {
                    setAllEnabled(true);
                    if (controller.segmentsWereModified()) {
                        controller.checkSpelling();
                    }
                }
            }
        });
	}

    private boolean isChild(Component c) {
        if (c == null) {
            return false;
        }
        Container parent = c.getParent();
        return parent == this || isChild(parent);
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
            btnAddLqi = new JButton("Add LQI");
            configButton(btnAddLqi);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = buttonsInsets;
            buttonsPanel.add(btnAddLqi, c);
        }
        {
            btnAddLqiAll = new JButton("Add LQI All");
            configButton(btnAddLqiAll);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = buttonsInsets;
            buttonsPanel.add(btnAddLqiAll, c);
        }
        {
            cbxSeverity = new JComboBox<>();
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 3;
            c.gridy = 0;
            c.gridheight = 2;
            c.insets = buttonsInsets;
            buttonsPanel.add(cbxSeverity, c);
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonsPanel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),
                progressBar.getBorder()));
        panel.add(progressBar, BorderLayout.SOUTH);
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
            lstSuggestions.addListSelectionListener(this);
            JScrollPane spSuggestions = new JScrollPane(lstSuggestions);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 2;
            c.ipadx = TXT_WIDTH;
            c.ipady = lstSuggestions.getFont().getSize() * 5;
            c.insets = new Insets(5, 10, 5, 10);
            panel.add(spSuggestions, c);
        }

        lblMessage = new JLabel();
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        lblMessage.setVisible(false);
        lblMessage.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),
                lblMessage.getBorder()));
        JPanel parentPanel = new JPanel(new BorderLayout());
        parentPanel.add(panel, BorderLayout.CENTER);
        parentPanel.add(lblMessage, BorderLayout.NORTH);

        return parentPanel;
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
            learn();
        } else if (e.getSource().equals(btnIgnore)) {
            controller.ignoreOne();
        } else if (e.getSource().equals(btnIgnoreAll)) {
            controller.ignoreAll();
		} else if (e.getSource().equals(btnReplace)) {
			replace();
		} else if (e.getSource().equals(btnReplaceAll)) {
			replaceAll();
        } else if (e.getSource().equals(btnAddLqi)) {
            addLqi();
        } else if (e.getSource().equals(btnAddLqiAll)) {
            addLqiAll();
		}
	}

    private void addLqi() {
        controller.addLqi((LQISeverity) cbxSeverity.getSelectedItem());
    }

    private void addLqiAll() {
        controller.addLqiAll((LQISeverity) cbxSeverity.getSelectedItem());
    }

    private void learn() {
        controller.learnWord(txtUnknownWord.getText());
    }

	/**
	 * Replaces all instances.
	 */
	private void replaceAll() {
        controller.replaceAll(txtReplaceWord.getText());
	}

	/**
	 * Replaces the last found string with the text contained in the replace
	 * text field.
	 */
	private void replace() {
        controller.replace(txtReplaceWord.getText());
	}

	/**
	 * Closes the dialog.
	 */
	private void close() {

		controller.closeDialog();
		setVisible(false);
	}

    public void setProgress(int cur, int max) {
        progressBar.setIndeterminate(false);
        progressBar.setValue(cur);
        progressBar.setMaximum(max);
    }

    private void setAllEnabled(boolean enabled) {
        setAllEnabledImpl(this, enabled);
        lblMessage.setEnabled(true);
    }

    private static void setAllEnabledImpl(Container container, boolean enabled) {
        for (Component c : container.getComponents()) {
            c.setEnabled(enabled);
            if (c instanceof Container) {
                setAllEnabledImpl((Container) c, enabled);
            }
        }
    }

    public void setResult(CheckResult result) {
        this.result = result;
        setProgressVisible(false);
        txtReplaceWord.setText(null);
        if (result != null) {
            setAllEnabled(true);
            txtUnknownWord.setText(result.getWord());
            List<String> suggestions = result.getSuggestions();
            lstSuggestions.setListData(suggestions.toArray(new String[suggestions.size()]));
        } else {
            setAllEnabled(false);
            txtUnknownWord.setText(null);
            lstSuggestions.setListData(new String[0]);
            setTitle("Spellcheck");
        }
    }

    public void setRemaining(int remaining) {
        setTitle("Spellcheck (" + remaining + " remaining)");
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        if (e.getSource() == lstSuggestions) {
            txtReplaceWord.setText(lstSuggestions.getSelectedValue());
        }
    }

    public void setMessage(String msg) {
        lblMessage.setText(msg);
        lblMessage.setVisible(msg != null);
    }

    public void setProgressVisible(boolean visible) {
        progressBar.setVisible(visible);
    }

    public final void setSeverities(List<LQISeverity> severities) {
        cbxSeverity.setModel(new DefaultComboBoxModel<>(severities.toArray(new LQISeverity[severities.size()])));
    }

}
