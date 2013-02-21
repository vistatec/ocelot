package com.spartansoftwareinc;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

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
 */
public class NewLanguageQualityIssueView extends JPanel implements ActionListener {
    private static final long serialVersionUID = 3L;
    private JFrame frame;
    private SegmentAttributeView segAttrView;
    private JLabel sourceTargetSegment;
    protected JComboBox typeList;
    private JTextArea commentContent;
    private SpinnerModel severityRating;
    private JTextField profileRefLink;
    private JRadioButton enabledTrue, enabledFalse;
    private boolean enabled = true;
    private JButton save;
    private JButton clear;

    public NewLanguageQualityIssueView(SegmentAttributeView segView) {
        setLayout(new GridBagLayout());
        this.segAttrView = segView;

        // Initialize default grid bag layout: left align, 1 grid each
        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
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
        JLabel segmentLabel = new JLabel("Segment #: ");
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        add(segmentLabel, gridBag);

        sourceTargetSegment = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 0;
        add(sourceTargetSegment, gridBag);
    }

    private void addType(GridBagConstraints gridBag) {
        JLabel type = new JLabel("Type: ");
        gridBag.gridx = 0;
        gridBag.gridy = 1;
//		gridBag.weightx = 0.25;
        add(type, gridBag);

        String[] types = {"terminology", "mistranslation", "omission",
            "untranslated", "addition", "duplication", "inconsistency",
            "grammar", "legal", "register", "locale-specific-content",
            "locale-violation", "style", "characters", "misspelling",
            "typographical", "formatting", "inconsistent-entities", "numbers",
            "markup", "pattern-problem", "whitespace", "internationalization",
            "length", "uncategorized", "other"};
        typeList = new JComboBox(types);
        gridBag.gridx = 1;
        gridBag.gridy = 1;
//		gridBag.weightx = 0.5;
        add(typeList, gridBag);
    }

    private void addComments(GridBagConstraints gridBag) {
        JLabel comment = new JLabel("Comment: ");
        gridBag.gridx = 0;
        gridBag.gridy = 2;
//		gridBag.weightx = 0.25;
        add(comment, gridBag);

        commentContent = new JTextArea(5, 15);
        commentContent.setEditable(true);
        commentContent.setLineWrap(true);
        JScrollPane commentScroll = new JScrollPane(commentContent);
        gridBag.gridx = 1;
        gridBag.gridy = 2;
//		gridBag.weightx = 0.5;
        add(commentScroll, gridBag);
    }

    private void addSeverity(GridBagConstraints gridBag) {
        JLabel severity = new JLabel("Severity: ");
        gridBag.gridx = 0;
        gridBag.gridy = 3;
//		gridBag.weightx = 0.25;
        add(severity, gridBag);

        severityRating = new SpinnerNumberModel(0, 0, 100, 1);
        JSpinner spinner = new JSpinner(severityRating);
        severity.setLabelFor(spinner);
        gridBag.gridx = 1;
        gridBag.gridy = 3;
//		gridBag.weightx = 0.5;
        add(spinner, gridBag);
    }

    private void addProfileReference(GridBagConstraints gridBag) {
        JLabel profile = new JLabel("Profile Reference: ");
        gridBag.gridx = 0;
        gridBag.gridy = 4;
//		gridBag.weightx = 0.25;
        add(profile, gridBag);

        // TODO: IRI validation?
        profileRefLink = new JTextField(15);
        gridBag.gridx = 1;
        gridBag.gridy = 4;
//		gridBag.weightx = 0.5;
        add(profileRefLink, gridBag);
    }

    private void addEnabled(GridBagConstraints gridBag) {
        JLabel enabledLabel = new JLabel("Enabled: ");
        gridBag.gridx = 0;
        gridBag.gridy = 5;
//		gridBag.weightx = 0.25;
        add(enabledLabel, gridBag);

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
//		gridBag.weightx = 0.5;
        add(enabledPanel, gridBag);
    }

    private void addSave(GridBagConstraints gridBag) {
        KeyListener enter = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    clearForm();
                }
            }
        };

        save = new JButton("Save");
        save.addActionListener(this);
        save.addKeyListener(enter);
        save.setEnabled(segAttrView.getSelectedSegment() != null);

        clear = new JButton("Clear");
        clear.addActionListener(this);
        clear.addKeyListener(enter);

        JPanel actionPanel = new JPanel();
        actionPanel.add(save);
        actionPanel.add(clear);
        gridBag.gridx = 1;
        gridBag.gridy = 6;
        add(actionPanel, gridBag);
    }

//    @Override
//    public void run() {
//        frame = new JFrame("Language Quality Issue");
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                // TODO: cleanup
//            }
//        });
//
//        frame.getContentPane().add(this);
//        frame.pack();
//        frame.setVisible(true);
//    }

    public void updateSegment() {
        sourceTargetSegment.setText(
                segAttrView.getSelectedSegment().getSegmentNumber() + "");
        save.setEnabled(true);
    }

    public void clearForm() {
        typeList.setSelectedIndex(0);
        commentContent.setText("");
        severityRating.setValue(0);
        profileRefLink.setText("");
        enabledTrue.doClick();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clear) {
            clearForm();

        } else if (e.getSource() == save) {
            LanguageQualityIssue lqi = new LanguageQualityIssue();
            lqi.setType(typeList.getSelectedItem().toString());
            lqi.setComment(commentContent.getText());
            lqi.setSeverity(new Integer(severityRating.getValue().toString()));
            if (!profileRefLink.getText().isEmpty()) {
                try {
                    lqi.setProfileReference(new URL(profileRefLink.getText()));
                } catch (MalformedURLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
            lqi.setEnabled(enabled);

            Segment selectedSeg = segAttrView.getSelectedSegment();
            if (selectedSeg.containsLQI()) {
                lqi.setIssuesRef(selectedSeg.getLQI().getFirst().getIssuesRef());
            } else {
                // TODO: generate unique LQI issues ref
                lqi.setIssuesRef(Calendar.getInstance().getTime().toString());
            }
            selectedSeg.addLQI(lqi);
            segAttrView.segmentView.updateEvent(selectedSeg);
            segAttrView.setSelectedSegment(selectedSeg);
            // Switch to Main segment metadata tab.
            segAttrView.setSelectedIndex(0);
            clearForm();

        } else if (e.getSource() == enabledTrue) {
            enabled = true;
        } else if (e.getSource() == enabledFalse) {
            enabled = false;
        }
    }

}
