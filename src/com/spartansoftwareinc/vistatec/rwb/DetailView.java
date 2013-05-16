package com.spartansoftwareinc.vistatec.rwb;

import com.spartansoftwareinc.vistatec.rwb.its.ITSMetadata;
import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssue;
import com.spartansoftwareinc.vistatec.rwb.its.LanguageQualityIssueView;
import com.spartansoftwareinc.vistatec.rwb.its.Provenance;
import com.spartansoftwareinc.vistatec.rwb.its.ProvenanceView;
import com.spartansoftwareinc.vistatec.rwb.segment.Segment;
import com.spartansoftwareinc.vistatec.rwb.segment.SegmentDetailView;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * Detail pane displaying data related to a selected segment in the SegmentView.
 */
public class DetailView extends JPanel {
    private LanguageQualityIssueView lqiDetailView;
    private ProvenanceView provDetailView;
    private SegmentDetailView segDetailView;

    public DetailView() {
        setLayout(new BorderLayout());
        lqiDetailView = new LanguageQualityIssueView();
        add(lqiDetailView);
    }

    public void clearDisplay() {
        if (lqiDetailView != null) {
            lqiDetailView.clearDisplay();
        }
        if (provDetailView != null) {
            provDetailView.clearDisplay();
        }
        if (segDetailView != null) {
            segDetailView.clearDisplay();
        }
    }

    public void setMetadata(Segment seg, ITSMetadata data) {
        if (LanguageQualityIssue.class.equals(
            data.getClass())) {
            removeSegmentDetailView();
            removeProvenanceDetailView();
            addLQIDetailView();
            lqiDetailView.setMetadata(seg, data);
        } else if (Provenance.class.equals(data.getClass())) {
            removeSegmentDetailView();
            removeLQIDetailView();
            addProvenanceDetailView();
            provDetailView.setMetadata(seg, data);
        }
        revalidate();
    }

    public void setSegment(Segment seg) {
        removeProvenanceDetailView();
        removeLQIDetailView();
        addSegmentDetailView();
        segDetailView.setSegment(seg);
        revalidate();
    }

    public void addProvenanceDetailView() {
        if (provDetailView == null) {
            provDetailView = new ProvenanceView();
            add(provDetailView);
        }
    }

    public void removeProvenanceDetailView() {
        if (provDetailView != null) {
            remove(provDetailView);
            provDetailView = null;
        }
    }

    public void addLQIDetailView() {
        if (lqiDetailView == null) {
            lqiDetailView = new LanguageQualityIssueView();
            add(lqiDetailView);
        }
    }

    public void removeLQIDetailView() {
        if (lqiDetailView != null) {
            remove(lqiDetailView);
            lqiDetailView = null;
        }
    }

    public void addSegmentDetailView() {
        if (segDetailView == null) {
            segDetailView = new SegmentDetailView();
            add(segDetailView);
        }
    }

    public void removeSegmentDetailView() {
        if (segDetailView != null) {
            remove(segDetailView);
            segDetailView = null;
        }
    }
}
