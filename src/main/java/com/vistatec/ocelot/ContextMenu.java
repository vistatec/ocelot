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
import com.vistatec.ocelot.segment.Segment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * ITS Metadata context menu.
 */
public class ContextMenu extends JPopupMenu implements ActionListener {
    /**
     * Default serial ID
     */
    private static final long serialVersionUID = 2L;
    private JMenuItem addLQI, removeLQI, resetTarget;
    private Segment selectedSeg;
    private LanguageQualityIssue selectedLQI;

    public ContextMenu(Segment selectedSeg) {
        this.selectedSeg = selectedSeg;

        addLQI = new JMenuItem("Add Issue");
        addLQI.addActionListener(this);
        addLQI.setEnabled(selectedSeg.isEditablePhase());
        add(addLQI);

        resetTarget = new JMenuItem("Reset Target");
        resetTarget.addActionListener(this);
        resetTarget.setEnabled(selectedSeg.hasOriginalTarget());
        add(resetTarget);
    }

    public ContextMenu(Segment selectedSeg, LanguageQualityIssue selectedLQI) {
        this(selectedSeg);
        this.selectedLQI = selectedLQI;

        removeLQI = new JMenuItem("Remove Issue");
        removeLQI.addActionListener(this);
        add(removeLQI);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addLQI) {
            NewLanguageQualityIssueView addLQIView = new NewLanguageQualityIssueView();
            addLQIView.setSegment(selectedSeg);
            SwingUtilities.invokeLater(addLQIView);
        } else if (e.getSource() == removeLQI) {
            selectedSeg.removeLQI(selectedLQI);
        } else if (e.getSource() == resetTarget) {
            selectedSeg.resetTarget();
        }
    }
}
