/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.view;

import java.awt.Component;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vistatec.ocelot.config.LqiJsonConfigService;
import com.vistatec.ocelot.events.ItsSelectionEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.its.stats.view.ITSDocStatsTableView;
import com.vistatec.ocelot.its.view.LanguageQualityIssueTableView;
import com.vistatec.ocelot.its.view.OtherITSTableView;
import com.vistatec.ocelot.its.view.ProvenanceTableView;
import com.vistatec.ocelot.its.view.TermsTableView;
import com.vistatec.ocelot.its.view.TextAnalysisTableView;

/**
 * Displays ITS metadata attached to the selected segment in the SegmentView.
 * Container for the various metadata tabs (stats/LQI/Prov/Other).
 */
public class SegmentAttributeView extends JTabbedPane implements OcelotEventQueueListener {
    private static final long serialVersionUID = 1L;

    protected ITSDocStatsTableView aggregateTableView;
    protected LanguageQualityIssueTableView lqiTableView;
    protected ProvenanceTableView provTableView;
    protected OtherITSTableView itsTableView;
    protected TextAnalysisTableView taTableView;
    protected TermsTableView termTableView;

    @Inject
    public SegmentAttributeView(OcelotEventQueue eventQueue, ITSDocStatsTableView docStatsView, LqiJsonConfigService  lqiService) {
        aggregateTableView = docStatsView;
        addTab("Doc Stats", aggregateTableView);
        eventQueue.registerListener(aggregateTableView);

	    lqiTableView = new LanguageQualityIssueTableView(eventQueue, lqiService);
        addTab("LQI", lqiTableView);
        eventQueue.registerListener(lqiTableView);

        provTableView = new ProvenanceTableView(eventQueue);
        addTab("Prov", provTableView);
        eventQueue.registerListener(provTableView);

        taTableView = new TextAnalysisTableView();
        eventQueue.registerListener(taTableView);
        addTab("Text-Analysis", taTableView);
        
        termTableView = new TermsTableView();
        eventQueue.registerListener(termTableView);
        addTab("Terms", termTableView);

        itsTableView = new OtherITSTableView();
        eventQueue.registerListener(itsTableView);
        addTab("Other ITS", itsTableView);
        eventQueue.registerListener(itsTableView);
        

        // Deselect metadata to allow reselection for detail view after switching tabs.
        addChangeListener(new ChangeListener() {
            private Component previousComponent = null;
            @Override
            public void stateChanged(ChangeEvent e) {
                if (previousComponent != null && (previousComponent instanceof SegmentAttributeTablePane)) {
                    ((SegmentAttributeTablePane<?>)previousComponent).clearSelection();
                }
                previousComponent = getSelectedComponent();
            }
        });
    }

    @Subscribe
    public void metadataSelected(ItsSelectionEvent e) {
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