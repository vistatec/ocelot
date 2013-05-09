package com.spartansoftwareinc;

import java.awt.Component;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Displays ITS metadata attached to the selected segment in the SegmentView.
 */
public class SegmentAttributeView extends JTabbedPane {
    protected SegmentAttributeTableView aggregateTableView;
    protected LanguageQualityIssueTableView lqiTableView;
    protected ITSProvenanceTableView provTableView;
    protected SegmentAttributeTreeView treeView;
    protected NewLanguageQualityIssueView addLQIView;
    private ITSDetailView itsDetailView;

    private Segment selectedSegment;

    public SegmentAttributeView(ITSDetailView detailView) {
        itsDetailView = detailView;

        aggregateTableView = new SegmentAttributeTableView();
        addTab("Doc Stats", aggregateTableView);

        lqiTableView = new LanguageQualityIssueTableView(this);
        addTab("LQI", lqiTableView);

        provTableView = new ITSProvenanceTableView(this);
        addTab("Prov", provTableView);

        treeView = new SegmentAttributeTreeView(this);
        addTab("Tree", treeView);

        addLQIView = new NewLanguageQualityIssueView(this);
        addTab("+", addLQIView);

        // Deselect metadata to allow reselection for detail view after switching tabs.
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Component tab = getComponentAt(getSelectedIndex());
                if (tab.equals(lqiTableView)) {
                    lqiTableView.deselectLQI();
                } else if (tab.equals(provTableView)) {
                    provTableView.deselectProv();
                } else if (tab.equals(treeView)) {
                    treeView.tree.clearSelection();
                }
            }
        });
    }

    public Segment getSelectedSegment() {
        return this.selectedSegment;
    }
    
    public void setSelectedSegment(Segment seg) {
        this.selectedSegment = seg;
        lqiTableView.setSegment(seg);
        provTableView.setSegment(seg);
        treeView.clearTree();
        if (seg.containsLQI()) { treeView.loadLQI(seg.getLQI()); }
        treeView.expandTree();
        addLQIView.updateSegment();
        itsDetailView.clearDisplay();
    }

    public void clearSegment() {
        treeView.clearTree();
        addLQIView.clearSegment();
        lqiTableView.clearSegment();
        provTableView.clearSegment();
    }

    public void deletedSegments() {
        aggregateTableView.clearStats();
        clearSegment();
    }

    public void addLQIMetadata(LanguageQualityIssue lqi) {
        aggregateTableView.addLQIMetadata(lqi);
    }

    public void addProvMetadata(ITSProvenance prov) {
        aggregateTableView.addProvMetadata(prov);
    }

    public void setSelectedMetadata(ITSMetadata its) {
        if (getSelectedSegment() != null) {
            itsDetailView.setMetadata(getSelectedSegment(), its);
            for (int i = 0; i < getTabCount(); i++) {
                if ((its instanceof LanguageQualityIssue
                        && lqiTableView.equals(getComponentAt(i))) ||
                    (its instanceof ITSProvenance
                        && provTableView.equals(getComponentAt(i)))) {
                    setSelectedIndex(i);
                }
            }
        }
    }
    
    public void deselectMetadata() {
        itsDetailView.clearDisplay();
    }
}
