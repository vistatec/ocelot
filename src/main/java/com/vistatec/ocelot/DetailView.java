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
package com.vistatec.ocelot;

import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.NewLanguageQualityIssueView;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.its.ProvenanceView;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.SegmentDetailView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * Detail pane displaying data related to a selected segment in the SegmentView.
 */
public class DetailView extends JPanel {
    private NewLanguageQualityIssueView lqiDetailView;
    private ProvenanceView provDetailView;
    private SegmentDetailView segDetailView;

    public DetailView() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 250));
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
            LanguageQualityIssue lqi = (LanguageQualityIssue) data;
            removeSegmentDetailView();
            removeProvenanceDetailView();
            addLQIDetailView();
            lqiDetailView.setMetadata(seg, lqi);
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
            lqiDetailView = new NewLanguageQualityIssueView();
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
