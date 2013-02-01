package com.spartansoftwareinc;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Detail view showing ITS metadata on the selected LQI in SegmentAttributeView.
 */
public class LanguageQualityIssueView extends JPanel {
    private JLabel segmentLabel, typeLabel, commentLabel, severityLabel,
            profileLabel, enabledLabel;
    private JLabel segment, type, comment, severity, profile, enabled;

    public LanguageQualityIssueView() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.insets = new Insets(0,10,5,10); // Pad text
        gridBag.gridwidth = 1;

        segmentLabel = new JLabel ();
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        mainPanel.add(segmentLabel, gridBag);

        segment = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 0;
        mainPanel.add(segment, gridBag);

        typeLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 1;
        mainPanel.add(typeLabel, gridBag);

        type = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 1;
        mainPanel.add(type, gridBag);
        
        commentLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 2;
        mainPanel.add(commentLabel, gridBag);
        
        comment = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 2;
        mainPanel.add(comment, gridBag);
        
        severityLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 3;
        mainPanel.add(severityLabel, gridBag);
        
        severity = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 3;
        mainPanel.add(severity, gridBag);
        
        profileLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 4;
        mainPanel.add(profileLabel, gridBag);
        
        profile = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 4;
        mainPanel.add(profile, gridBag);
        
        enabledLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 5;
        mainPanel.add(enabledLabel, gridBag);
        
        enabled = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 5;
        mainPanel.add(enabled, gridBag);

        add(mainPanel);
        Dimension prefSize = new Dimension(500, 200);
        setPreferredSize(prefSize);
    }

    public void setMetadata(Segment selectedSegment, ITSMetadata data) {
        LanguageQualityIssue lqi = (LanguageQualityIssue) data;
        segmentLabel.setText("Segment #");
        segment.setText(selectedSegment.getSegmentNumber()+"");
        typeLabel.setText("Type");
        type.setText(lqi.getType());
        commentLabel.setText("Comment");
        comment.setText(lqi.getComment());
        severityLabel.setText("Severity");
        severity.setText(lqi.getSeverity()+"");
        profileLabel.setText("Profile Reference");
        profile.setText(lqi.getProfileReference() != null ?
                lqi.getProfileReference().toString() : "");
        enabledLabel.setText("Enabled");
        enabled.setText(lqi.isEnabled() ? "yes" : "no");
    }

    public void clearDisplay() {
        segmentLabel.setText("");
        segment.setText("");
        typeLabel.setText("");
        type.setText("");
        commentLabel.setText("");
        comment.setText("");
        severityLabel.setText("");
        severity.setText("");
        profileLabel.setText("");
        profile.setText("");
        enabledLabel.setText("");
        enabled.setText("");
    }
}