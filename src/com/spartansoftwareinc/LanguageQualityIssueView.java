package com.spartansoftwareinc;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Detail view showing ITS metadata on the selected LQI in SegmentAttributeView.
 */
public class LanguageQualityIssueView extends JScrollPane {
    private JLabel segment, type, comment, severity, profile, enabled;

    public LanguageQualityIssueView() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.insets = new Insets(0,10,5,10); // Pad text
        gridBag.gridwidth = 1;

        JLabel segmentLabel = new JLabel ("Segment #");
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        mainPanel.add(segmentLabel, gridBag);

        segment = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 0;
        mainPanel.add(segment, gridBag);

        JLabel typeLabel = new JLabel("Type");
        gridBag.gridx = 0;
        gridBag.gridy = 1;
        mainPanel.add(typeLabel, gridBag);

        type = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 1;
        mainPanel.add(type, gridBag);
        
        JLabel commentLabel = new JLabel("Comment");
        gridBag.gridx = 0;
        gridBag.gridy = 2;
        mainPanel.add(commentLabel, gridBag);
        
        comment = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 2;
        mainPanel.add(comment, gridBag);
        
        JLabel severityLabel = new JLabel("Severity");
        gridBag.gridx = 0;
        gridBag.gridy = 3;
        mainPanel.add(severityLabel, gridBag);
        
        severity = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 3;
        mainPanel.add(severity, gridBag);
        
        JLabel profileLabel = new JLabel("Profile Reference");
        gridBag.gridx = 0;
        gridBag.gridy = 4;
        mainPanel.add(profileLabel, gridBag);
        
        profile = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 4;
        mainPanel.add(profile, gridBag);
        
        JLabel enabledLabel = new JLabel("Enabled");
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
        setViewportView(mainPanel);
    }

    public void setLQI(Segment selectedSegment, LanguageQualityIssue lqi) {
        segment.setText(selectedSegment.getSegmentNumber()+"");
        type.setText(lqi.getType());
        comment.setText(lqi.getComment());
        severity.setText(lqi.getSeverity()+"");
        profile.setText(lqi.getProfileReference() != null ?
                lqi.getProfileReference().toString() : "");
        enabled.setText(lqi.isEnabled() ? "yes" : "no");
    }
}