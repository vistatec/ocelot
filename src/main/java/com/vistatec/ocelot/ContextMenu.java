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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.vistatec.ocelot.events.EnrichmentViewEvent;
import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.view.LanguageQualityIssuePropsPanel;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.xliff.XLIFFDocument;

/**
 * ITS Metadata context menu.
 */
public class ContextMenu extends JPopupMenu implements ActionListener {
	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 2L;
	private JMenuItem addLQI, removeLQI, resetTarget, viewEnrichments;
	private OcelotSegment selectedSeg;
	private SegmentVariant variant;
    private LanguageQualityIssue selectedLQI;
    private LQIGridConfigurations lqiGrid;
    private XLIFFDocument xliff;
    private OcelotEventQueue eventQueue;

    public ContextMenu(XLIFFDocument xliff, OcelotSegment selectedSeg, OcelotEventQueue eventQueue, LQIGridConfigurations lqiGrid) {
        this.xliff = xliff;
        this.selectedSeg = selectedSeg;
        this.eventQueue = eventQueue;
        this.lqiGrid = lqiGrid;

        addLQI = new JMenuItem("Add Issue");
        addLQI.addActionListener(this);
        addLQI.setEnabled(selectedSeg.isEditable());
        add(addLQI);

        resetTarget = new JMenuItem("Reset Target");
        resetTarget.addActionListener(this);
        resetTarget.setEnabled(selectedSeg.hasOriginalTarget());
        add(resetTarget);
    }

    public ContextMenu(XLIFFDocument xliff, OcelotSegment selectedSeg, LanguageQualityIssue selectedLQI,
                       OcelotEventQueue eventQueue, LQIGridConfigurations lqiGrid) {
 		this(xliff, selectedSeg, eventQueue, lqiGrid);
		this.selectedLQI = selectedLQI;
		if(selectedLQI != null){
			removeLQI = new JMenuItem("Remove Issue");
			removeLQI.addActionListener(this);
			add(removeLQI);
		}
	}

	public ContextMenu(XLIFFDocument xliff, OcelotSegment selectedSeg, SegmentVariant variant,
			OcelotEventQueue eventQueue, LQIGridConfigurations lqiGrid) {

		this(xliff, selectedSeg, eventQueue, lqiGrid);
		this.variant = variant;
		createEnrichmentMenuItem();
	}

	public ContextMenu(XLIFFDocument xliff, OcelotSegment selectedSeg, SegmentVariant variant,
			LanguageQualityIssue selectedLQI, OcelotEventQueue eventQueue, LQIGridConfigurations lqiGrid) {
		this(xliff, selectedSeg, selectedLQI, eventQueue, lqiGrid);
		this.variant = variant;
		createEnrichmentMenuItem();
	}

	private void createEnrichmentMenuItem() {

		if (variant != null && variant instanceof BaseSegmentVariant
		        && ((BaseSegmentVariant) variant).isEnriched()) {
			viewEnrichments = new JMenuItem("View Enrichments");
			viewEnrichments.addActionListener(this);
			add(viewEnrichments);

		}
	}


	@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addLQI) {
			LanguageQualityIssuePropsPanel addLQIView = new LanguageQualityIssuePropsPanel(
			        eventQueue, lqiGrid);
            addLQIView.setSegment(selectedSeg);
            SwingUtilities.invokeLater(addLQIView);
		} else if (e.getSource().equals(removeLQI)) {
            eventQueue.post(new LQIRemoveEvent(selectedLQI, selectedSeg));
		} else if (e.getSource().equals(resetTarget)) {
            eventQueue.post(new SegmentTargetResetEvent(xliff, selectedSeg));
		} else if(e.getSource().equals(viewEnrichments) ){
			eventQueue.post(new EnrichmentViewEvent(
			        (BaseSegmentVariant) variant, selectedSeg
			                .getSegmentNumber(), EnrichmentViewEvent.STD_VIEW,
			        variant.equals(selectedSeg.getTarget())));
        }
    }
}
