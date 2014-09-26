/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.its;

import com.vistatec.ocelot.segment.Segment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.border.EmptyBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ITS 2.0 Language Quality Issue Data Category creation form.
 * Follows: http://www.w3.org/International/multilingualweb/lt/drafts/its20/its20.html#lqissue
 */
public class NewLanguageQualityIssueView extends JPanel implements Runnable, ActionListener {
    private static final long serialVersionUID = 3L;
    private static Logger LOG = LoggerFactory.getLogger(NewLanguageQualityIssueView.class);

    private JFrame frame;
    private JLabel segmentLabel, segmentId, typeLabel, commentLabel,
            severityLabel, profileLabel, enabledLabel;
    protected JComboBox typeList;
    private JScrollPane commentScroll;
    private JTextArea commentContent;
    private JSpinner severitySpinner;
    private SpinnerModel severityRating;
    private JTextField profileRefLink;
    private JRadioButton enabledTrue, enabledFalse;
    private boolean enabled = true;
    private JButton save;
    private JButton cancel;

    private Segment selectedSeg;
    private LanguageQualityIssue selectedLQI;

    private String prevType, prevComment;
    private double prevSeverity;
    private URL prevProfile;
    private boolean prevEnabled;

    public static final String[] LQI_TYPE = {"terminology", "mistranslation", "omission",
        "untranslated", "addition", "duplication", "inconsistency",
        "grammar", "legal", "register", "locale-specific-content",
        "locale-violation", "style", "characters", "misspelling",
        "typographical", "formatting", "inconsistent-entities", "numbers",
        "markup", "pattern-problem", "whitespace", "internationalization",
        "length", "non-conformance", "uncategorized", "other"};

    public NewLanguageQualityIssueView() {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10,10,10,10));

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
        segmentLabel = new JLabel("Segment #: ");
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        add(segmentLabel, gridBag);

        segmentId = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 0;
        add(segmentId, gridBag);
    }

    private void addType(GridBagConstraints gridBag) {
        typeLabel = new JLabel("Type: ");
        gridBag.gridx = 0;
        gridBag.gridy = 1;
        add(typeLabel, gridBag);

        typeList = new JComboBox(LQI_TYPE);
        gridBag.gridx = 1;
        gridBag.gridy = 1;
        add(typeList, gridBag);
    }

    private void addComments(GridBagConstraints gridBag) {
        commentLabel = new JLabel("Comment: ");
        gridBag.gridx = 0;
        gridBag.gridy = 2;
        add(commentLabel, gridBag);

        commentContent = new JTextArea(5, 15);
        commentContent.setEditable(true);
        commentContent.setLineWrap(true);
        commentScroll = new JScrollPane(commentContent);
        gridBag.gridx = 1;
        gridBag.gridy = 2;
        add(commentScroll, gridBag);
    }

    private void addSeverity(GridBagConstraints gridBag) {
        severityLabel = new JLabel("Severity: ");
        gridBag.gridx = 0;
        gridBag.gridy = 3;
        add(severityLabel, gridBag);

        severityRating = new SpinnerNumberModel(0, 0, 100, 0.000000001);
        severitySpinner = new JSpinner(severityRating);
        severitySpinner.setEditor(new JSpinner.NumberEditor(severitySpinner, "0.000000000"));
        severityLabel.setLabelFor(severitySpinner);
        gridBag.gridx = 1;
        gridBag.gridy = 3;
        add(severitySpinner, gridBag);
    }

    private void addProfileReference(GridBagConstraints gridBag) {
        profileLabel = new JLabel("Profile Reference: ");
        gridBag.gridx = 0;
        gridBag.gridy = 4;
        add(profileLabel, gridBag);

        // TODO: IRI validation?
        profileRefLink = new JTextField(15);
        gridBag.gridx = 1;
        gridBag.gridy = 4;
        add(profileRefLink, gridBag);
    }

    private void addEnabled(GridBagConstraints gridBag) {
        enabledLabel = new JLabel("Enabled: ");
        gridBag.gridx = 0;
        gridBag.gridy = 5;
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
                    if (ke.getSource().equals(save)) {
                        save.doClick();
                    } else {
                        cancel.doClick();
                    }
                }
            }
        };

        save = new JButton("Save");
        save.addActionListener(this);
        save.addKeyListener(enter);
        save.setEnabled(selectedSeg != null);

        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        cancel.addKeyListener(enter);

        JPanel actionPanel = new JPanel();
        actionPanel.add(save);
        actionPanel.add(cancel);
        gridBag.gridx = 1;
        gridBag.gridy = 6;
        add(actionPanel, gridBag);
    }

    public void setSegment(Segment seg) {
        this.selectedSeg = seg;
        updateSegment();
    }

    public void updateSegment() {
        segmentId.setText(
                selectedSeg.getSegmentNumber() + "");
        setEditableByPhase(selectedSeg);
        save.setEnabled(true);
    }

    public void clearSegment() {
        segmentId.setText("");
        save.setEnabled(false);
    }

    public void setMetadata(Segment selectedSegment, LanguageQualityIssue lqi) {
        setSegment(selectedSegment);
        this.selectedLQI = lqi;

        prevType = lqi.getType();
        prevComment = lqi.getComment();
        prevSeverity = lqi.getSeverity();
        prevProfile = lqi.getProfileReference();
        prevEnabled = lqi.isEnabled();

        segmentLabel.setText("Segment #");
        segmentId.setText(selectedSegment.getSegmentNumber()+"");

        typeLabel.setText("Type");
        setType(prevType);

        commentLabel.setText("Comment");
        commentContent.setText(prevComment);
        commentContent.setVisible(true);

        severityLabel.setText("Severity");
        severityRating.setValue(prevSeverity);

        profileLabel.setText("Profile Reference");
        profileRefLink.setText(prevProfile != null ?
                prevProfile.toString() : "");

        enabledLabel.setText("Enabled");
        enabledTrue.setSelected(prevEnabled);
        enabledFalse.setSelected(!prevEnabled);
    }

    public void setEditableByPhase(Segment selectedSegment) {
        typeList.setEnabled(selectedSegment.isEditablePhase());
        severitySpinner.setEnabled(selectedSegment.isEditablePhase());
        profileRefLink.setEnabled(selectedSegment.isEditablePhase());
    }

    public boolean setType(String metadataType) {
        for (int i = 0; i < LQI_TYPE.length; i++) {
            String value = LQI_TYPE[i];
            if (metadataType.equals(value)) {
                typeList.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public void requestFocus() {
        typeList.requestFocus();
    }

    public void resetForm() {
        if (!setType(prevType)) {
            typeList.setSelectedIndex(0);
        }
        commentContent.setText(prevComment != null ? prevComment : "");
        severityRating.setValue(prevSeverity);
        profileRefLink.setText(prevProfile != null ? prevProfile.toString() : "");
        if (prevEnabled) {
            enabledTrue.doClick();
        } else {
            enabledFalse.doClick();
        }
    }

    public void clearDisplay() {
        segmentLabel.setText("");
        segmentId.setText("");
        typeLabel.setText("");
        typeList.setVisible(false);
        commentLabel.setText("");
        commentContent.setText("");
        commentScroll.setVisible(false);
        severityLabel.setText("");
        severitySpinner.setVisible(false);
        profileLabel.setText("");
        profileRefLink.setText("");
        profileRefLink.setVisible(false);
        enabledLabel.setText("");
        enabledTrue.setVisible(false);
        enabledFalse.setVisible(false);
        save.setVisible(false);
        cancel.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancel) {
            if (addingLQI()) {
                frame.dispose();
            } else {
                resetForm();
            }

        } else if (e.getSource() == save) {
            LanguageQualityIssue lqi = addingLQI() ?
                    new LanguageQualityIssue() : this.selectedLQI;
            lqi.setType(typeList.getSelectedItem().toString());
            lqi.setComment(commentContent.getText());
            lqi.setSeverity(new Double(severityRating.getValue().toString()));
            if (!profileRefLink.getText().isEmpty()) {
                try {
                    lqi.setProfileReference(new URL(profileRefLink.getText()));
                } catch (MalformedURLException ex) {
                    LOG.warn("Profile reference '"+profileRefLink.getText()
                            +"' is not a valid URL", ex);
                }
            }
            lqi.setEnabled(enabled);

            if (selectedSeg.containsLQI()) {
                lqi.setIssuesRef(selectedSeg.getLQI().get(0).getIssuesRef());
            } else {
                // TODO: generate unique LQI issues ref
                lqi.setIssuesRef(Calendar.getInstance().getTime().toString());
            }

            if (addingLQI()) {
                selectedSeg.addLQI(lqi);
                frame.dispose();
            } else {
                selectedSeg.editedLQI(lqi);
            }

        } else if (e.getSource() == enabledTrue) {
            enabled = true;
        } else if (e.getSource() == enabledFalse) {
            enabled = false;
        }
    }

    public boolean addingLQI() {
        return frame != null && this.selectedLQI == null;
    }

    @Override
    public void run() {
        frame = new JFrame("Add Language Quality Issue");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.getContentPane().add(this);

        frame.pack();
        frame.setVisible(true);
    }
}
