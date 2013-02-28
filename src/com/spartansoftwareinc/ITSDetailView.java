package com.spartansoftwareinc;

import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * ITS detail pane displaying a selected ITS metadata related
 * to a selected segment in the SegmentView.
 */
public class ITSDetailView extends JPanel {
    private LanguageQualityIssueView lqiDetailView;
    
    public ITSDetailView() {
        setLayout(new BorderLayout());
        lqiDetailView = new LanguageQualityIssueView();
        add(lqiDetailView);
    }

    public void clearDisplay() {
        lqiDetailView.clearDisplay();
    }

    public void setMetadata(Segment seg, ITSMetadata data) {
        lqiDetailView.setMetadata(seg, data);
    }
}
