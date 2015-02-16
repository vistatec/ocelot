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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vistatec.ocelot.events.ITSSelectionEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.SegmentDeselectionEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.LanguageQualityIssueTableView;
import com.vistatec.ocelot.its.OtherITSTableView;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.its.ProvenanceTableView;
import com.vistatec.ocelot.its.stats.ITSDocStats;
import com.vistatec.ocelot.its.stats.ITSDocStatsTableView;

import java.awt.Component;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Displays ITS metadata attached to the selected segment in the SegmentView.
 * Container for the various metadata tabs (stats/LQI/Prov/Other).
 */
public class SegmentAttributeView extends JTabbedPane {
    private static final long serialVersionUID = 1L;

    protected ITSDocStatsTableView aggregateTableView;
    protected LanguageQualityIssueTableView lqiTableView;
    protected ProvenanceTableView provTableView;
    protected OtherITSTableView itsTableView;
    private Segment selectedSegment;

    @Inject
    public SegmentAttributeView(EventBus eventBus, ITSDocStats docStats) {
        aggregateTableView = new ITSDocStatsTableView(eventBus, docStats);
        addTab("Doc Stats", aggregateTableView);

        lqiTableView = new LanguageQualityIssueTableView(eventBus);
        addTab("LQI", lqiTableView);

        provTableView = new ProvenanceTableView(eventBus);
        addTab("Prov", provTableView);

        itsTableView = new OtherITSTableView(eventBus);
        addTab("Other ITS", itsTableView);

        // Deselect metadata to allow reselection for detail view after switching tabs.
        addChangeListener(new ChangeListener() {
            private Component previousComponent = null;
            @Override
            public void stateChanged(ChangeEvent e) {
                if (previousComponent != null && (previousComponent instanceof SegmentAttributeTablePane)) {
                    ((SegmentAttributeTablePane)previousComponent).clearSelection();
                }
                previousComponent = getSelectedComponent();
            }
        });
        eventBus.register(this);
    }

    public Segment getSelectedSegment() {
        return this.selectedSegment;
    }

    @Subscribe
    public void setSelectedSegment(SegmentSelectionEvent e) {
        this.selectedSegment = e.getSegment();
    }

    @Subscribe
    public void clearSegment(SegmentDeselectionEvent e) {
        this.selectedSegment = null;
    }

    @Subscribe
    public void metadataSelected(ITSSelectionEvent e) {
        if (e.getITSMetadata() instanceof LanguageQualityIssue) {
            setTab(lqiTableView);
        }
        else if (e.getITSMetadata() instanceof Provenance) {
            setTab(provTableView);
        }
    }

    @Subscribe
    public void lqiModification(LQIModificationEvent e) {
        setTab(lqiTableView);
    }

    private void setTab(Component tab) {
        for (int i = 0; i < getTabCount(); i++) {
            if (tab.equals(getComponentAt(i))) {
                setSelectedIndex(i);
            }
        }
    }

    public void focusNextTab() {
        int selectedTab = getSelectedIndex();
        selectedTab = selectedTab + 1 == getTabCount() ? 0 : selectedTab + 1;
        setSelectedIndex(selectedTab);
        getComponentAt(selectedTab).requestFocus();
    }
}
