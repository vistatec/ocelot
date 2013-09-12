package com.spartansoftwareinc.vistatec.rwb.its;

import com.spartansoftwareinc.vistatec.rwb.segment.Segment;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * ITS detail pane displaying a selected ITS metadata related
 * to a selected segment in the SegmentView.
 */
public class ITSDetailView extends JPanel {
    private LanguageQualityIssueView lqiDetailView;
    private ProvenanceView provDetailView;
    
    public ITSDetailView() {
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
    }

    public void setMetadata(Segment seg, ITSMetadata data) {
        if (LanguageQualityIssue.class.equals(
            data.getClass())) {
            if (provDetailView != null) {
                remove(provDetailView);
                provDetailView = null;
            }
            if (lqiDetailView == null) {
                lqiDetailView = new LanguageQualityIssueView();
                add(lqiDetailView);
            }
            lqiDetailView.setMetadata(seg, data);
        } else if (Provenance.class.equals(data.getClass())) {
            if (lqiDetailView != null) {
                remove(lqiDetailView);
                lqiDetailView = null;
            }
            if (provDetailView == null) {
                provDetailView = new ProvenanceView();
                add(provDetailView);
            }
            provDetailView.setMetadata(seg, data);
        }
        revalidate();
    }
}
