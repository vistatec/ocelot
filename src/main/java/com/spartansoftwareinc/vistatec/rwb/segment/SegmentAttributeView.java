package com.spartansoftwareinc.vistatec.rwb.segment;

import com.spartansoftwareinc.vistatec.rwb.DetailView;
import com.spartansoftwareinc.vistatec.rwb.its.ITSMetadata;
import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssueTableView;
import com.spartansoftwareinc.vistatec.rwb.its.OtherITSMetadata;
import com.spartansoftwareinc.vistatec.rwb.its.OtherITSTableView;
import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import com.spartansoftwareinc.vistatec.rwb.its.ProvenanceTableView;
import com.spartansoftwareinc.vistatec.rwb.its.stats.ITSDocStatsTableView;
import java.awt.Component;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Displays ITS metadata attached to the selected segment in the SegmentView.
 */
public class SegmentAttributeView extends JTabbedPane {
    protected ITSDocStatsTableView aggregateTableView;
    protected LanguageQualityIssueTableView lqiTableView;
    protected ProvenanceTableView provTableView;
    protected OtherITSTableView itsTableView;
    protected SegmentAttributeTreeView treeView;
    private DetailView detailView;

    private Segment selectedSegment;

    public SegmentAttributeView(DetailView detailView) {
        this.detailView = detailView;

        aggregateTableView = new ITSDocStatsTableView();
        addTab("Doc Stats", aggregateTableView);

        lqiTableView = new LanguageQualityIssueTableView(this);
        addTab("LQI", lqiTableView);

        provTableView = new ProvenanceTableView(this);
        addTab("Prov", provTableView);

        itsTableView = new OtherITSTableView();
        addTab("Other ITS", itsTableView);

        treeView = new SegmentAttributeTreeView(this);
        addTab("Tree", treeView);

        // Deselect metadata to allow reselection for detail view after switching tabs.
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Component tab = getComponentAt(getSelectedIndex());
                if (tab.equals(lqiTableView)) {
                    lqiTableView.deselectLQI();
                } else if (tab.equals(provTableView)) {
                    provTableView.deselectProv();
                } else if (tab.equals(itsTableView)) {
                    itsTableView.clearTableSelection();
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
        itsTableView.setSegment(seg);
        treeView.clearTree();
        if (seg.containsLQI()) { treeView.loadLQI(seg.getLQI()); }
        treeView.expandTree();
        detailView.setSegment(seg);
    }

    public void clearSegment() {
        this.selectedSegment = null;
        treeView.clearTree();
        lqiTableView.clearSegment();
        provTableView.clearSegment();
        itsTableView.clearSegment();
    }

    public void deletedSegments() {
        aggregateTableView.clearStats();
        clearSegment();
    }

    public void addLQIMetadata(LanguageQualityIssue lqi) {
        aggregateTableView.addLQIMetadata(lqi);
    }

    public void addProvMetadata(Provenance prov) {
        aggregateTableView.addProvMetadata(prov);
    }

    public void setSelectedMetadata(ITSMetadata its) {
        if (getSelectedSegment() != null) {
            detailView.setMetadata(getSelectedSegment(), its);
            for (int i = 0; i < getTabCount(); i++) {
                if (its instanceof LanguageQualityIssue
                        && lqiTableView.equals(getComponentAt(i)) ||
                    its instanceof Provenance
                        && provTableView.equals(getComponentAt(i)) ||
                    its instanceof OtherITSMetadata
                        && itsTableView.equals(getComponentAt(i)) ) {
                    setSelectedIndex(i);
                }
            }
        }
    }
    
    public void deselectMetadata() {
        detailView.clearDisplay();
    }

    public void focusNextTab() {
        int selectedTab = getSelectedIndex();
        selectedTab = selectedTab + 1 == getTabCount() ? 0 : selectedTab + 1;
        setSelectedIndex(selectedTab);
        getComponentAt(selectedTab).requestFocus();
    }
}
