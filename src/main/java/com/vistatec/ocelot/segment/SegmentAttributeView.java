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
package com.vistatec.ocelot.segment;

import com.vistatec.ocelot.DetailView;
import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.LanguageQualityIssueTableView;
import com.vistatec.ocelot.its.OtherITSMetadata;
import com.vistatec.ocelot.its.OtherITSTableView;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.its.ProvenanceTableView;
import com.vistatec.ocelot.its.stats.ITSDocStatsTableView;
import java.awt.Component;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Displays ITS metadata attached to the selected segment in the SegmentView.
 */
public class SegmentAttributeView extends JTabbedPane {
    private static final long serialVersionUID = 1L;

    protected ITSDocStatsTableView aggregateTableView;
    protected LanguageQualityIssueTableView lqiTableView;
    protected ProvenanceTableView provTableView;
    protected OtherITSTableView itsTableView;
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
        detailView.setSegment(seg);
    }

    public void clearSegment() {
        this.selectedSegment = null;
        lqiTableView.clearSegment();
        provTableView.clearSegment();
        itsTableView.clearSegment();
    }

    public LanguageQualityIssueTableView getLQITableView() {
        return lqiTableView;
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
