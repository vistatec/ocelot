package com.spartansoftwareinc;

import javax.swing.JScrollPane;

/**
 * ITS detail pane displaying a selected ITS metadata related
 * to a selected segment in the SegmentView.
 */
public class ITSDetailView extends JScrollPane {
    private LanguageQualityIssueView lqiDetailView;
    
    public ITSDetailView() {
        lqiDetailView = new LanguageQualityIssueView();
        setViewportView(lqiDetailView);
    }

    public void clearDisplay() {
        lqiDetailView.clearDisplay();
    }

    public void setMetadata(Segment seg, ITSMetadata data) {
        lqiDetailView.setMetadata(seg, data);
    }
}
