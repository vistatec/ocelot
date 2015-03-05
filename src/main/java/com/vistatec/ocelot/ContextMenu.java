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

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.NewLanguageQualityIssueView;
import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;

/**
 * ITS Metadata context menu.
 */
public class ContextMenu extends JPopupMenu implements ActionListener {
    /**
     * Default serial ID
     */
    private static final long serialVersionUID = 2L;
    private JMenuItem addLQI, removeLQI, resetTarget;
    private OcelotSegment selectedSeg;
    private LanguageQualityIssue selectedLQI;

    private OcelotEventQueue eventQueue;

    public ContextMenu(OcelotSegment selectedSeg, OcelotEventQueue eventQueue) {
        this.selectedSeg = selectedSeg;
        this.eventQueue = eventQueue;

        addLQI = new JMenuItem("Add Issue");
        addLQI.addActionListener(this);
        addLQI.setEnabled(selectedSeg.isEditable());
        add(addLQI);

        resetTarget = new JMenuItem("Reset Target");
        resetTarget.addActionListener(this);
        resetTarget.setEnabled(selectedSeg.hasOriginalTarget());
        add(resetTarget);
    }

    public ContextMenu(OcelotSegment selectedSeg, LanguageQualityIssue selectedLQI, OcelotEventQueue eventQueue) {
        this(selectedSeg, eventQueue);
        this.selectedLQI = selectedLQI;

        removeLQI = new JMenuItem("Remove Issue");
        removeLQI.addActionListener(this);
        add(removeLQI);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addLQI) {
            NewLanguageQualityIssueView addLQIView = new NewLanguageQualityIssueView(eventQueue);
            addLQIView.setSegment(selectedSeg);
            SwingUtilities.invokeLater(addLQIView);
        } else if (e.getSource() == removeLQI) {
            eventQueue.post(new LQIRemoveEvent(selectedLQI, selectedSeg));
        } else if (e.getSource() == resetTarget) {
            eventQueue.post(new SegmentTargetResetEvent(selectedSeg));
        }
    }
}
