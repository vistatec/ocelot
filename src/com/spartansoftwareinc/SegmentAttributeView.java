package com.spartansoftwareinc;

import javax.swing.JTabbedPane;

/**
 * Displays ITS metadata attached to the selected segment in the SegmentView.
 */
public class SegmentAttributeView extends JTabbedPane {
    protected SegmentAttributeTreeView treeView;
    protected NewLanguageQualityIssueView addLQIView;
    private LanguageQualityIssueView lqiDetailView;

    private Segment selectedSegment;

    public SegmentAttributeView(LanguageQualityIssueView detailView) {
        this.lqiDetailView = detailView;
        treeView = new SegmentAttributeTreeView(this);
        addTab("Tree", treeView);

        addLQIView = new NewLanguageQualityIssueView(this);
        addTab("+", addLQIView);
    }
    
    public Segment getSelectedSegment() {
        return this.selectedSegment;
    }
    
    public void setSelectedSegment(Segment seg) {
        this.selectedSegment = seg;
        treeView.clearTree();
        if (seg.containsLQI()) { treeView.loadLQI(seg.getLQI()); }
        treeView.expandTree();
        addLQIView.updateSegment();
        lqiDetailView.clearDisplay();
    }
    
    public void setSelectedMetadata(LanguageQualityIssue lqi) {
        lqiDetailView.setLQI(selectedSegment, lqi);
    }
    
    public void deselectMetadata() {
        lqiDetailView.clearDisplay();
    }
}
