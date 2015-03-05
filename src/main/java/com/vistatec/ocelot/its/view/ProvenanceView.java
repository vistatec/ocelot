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
package com.vistatec.ocelot.its.view;

import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.its.model.ITSMetadata;
import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 * Detail view showing ITS metadata on the selected Provenance in SegmentAttributeView.
 */
public class ProvenanceView extends JScrollPane {
    private static final long serialVersionUID = 1L;

    private JLabel dataCategoryLabel, segmentLabel;
    private JLabel personLabel, orgLabel, toolLabel,
            revPersonLabel, revOrgLabel, revToolLabel;
    private JLabel segment, person, org, tool, revPerson, revOrg, revTool;

    public ProvenanceView() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.insets = new Insets(0,10,5,10); // Pad text
        gridBag.gridwidth = 1;

        dataCategoryLabel = new JLabel ();
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        mainPanel.add(dataCategoryLabel, gridBag);

        segmentLabel = new JLabel ();
        gridBag.gridx = 0;
        gridBag.gridy = 1;
        gridBag.gridwidth = 1;
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        mainPanel.add(segmentLabel, gridBag);

        segment = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 1;
        mainPanel.add(segment, gridBag);

        personLabel = new JLabel ();
        gridBag.gridx = 0;
        gridBag.gridy = 2;
        mainPanel.add(personLabel, gridBag);

        person = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 2;
        mainPanel.add(person, gridBag);

        orgLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 3;
        mainPanel.add(orgLabel, gridBag);

        org = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 3;
        mainPanel.add(org, gridBag);

        toolLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 4;
        mainPanel.add(toolLabel, gridBag);

        tool = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 4;
        mainPanel.add(tool, gridBag);

        revPersonLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 5;
        mainPanel.add(revPersonLabel, gridBag);

        revPerson = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 5;
        mainPanel.add(revPerson, gridBag);

        revOrgLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 6;
        mainPanel.add(revOrgLabel, gridBag);

        revOrg = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 6;
        mainPanel.add(revOrg, gridBag);

        revToolLabel = new JLabel();
        gridBag.gridx = 0;
        gridBag.gridy = 7;
        mainPanel.add(revToolLabel, gridBag);

        revTool = new JLabel();
        gridBag.gridx = 1;
        gridBag.gridy = 7;
        mainPanel.add(revTool, gridBag);

        Dimension prefSize = new Dimension(500, 200);
        setPreferredSize(prefSize);
        //Padding
        setBorder(new EmptyBorder(10,10,10,10));
        setViewportView(mainPanel);
    }

    public void setMetadata(OcelotSegment selectedSegment, ITSMetadata data) {
        Provenance prov = (Provenance) data;
        dataCategoryLabel.setText("Provenance");
        segmentLabel.setText("Segment #");
        segment.setText(selectedSegment.getSegmentNumber()+"");
        personLabel.setText("Person");
        person.setText(prov.getPerson());
        orgLabel.setText("Organization");
        org.setText(prov.getOrg());
        toolLabel.setText("Tool");
        tool.setText(prov.getTool());
        revPersonLabel.setText("RevPerson");
        revPerson.setText(prov.getRevPerson());
        revOrgLabel.setText("RevOrg");
        revOrg.setText(prov.getRevOrg());
        revToolLabel.setText("RevTool");
        revTool.setText(prov.getRevTool());
    }

    public void clearDisplay() {
        dataCategoryLabel.setText("");
        segmentLabel.setText("");
        segment.setText("");
        personLabel.setText("");
        person.setText("");
        orgLabel.setText("");
        org.setText("");
        toolLabel.setText("");
        tool.setText("");
        revPersonLabel.setText("");
        revPerson.setText("");
        revOrgLabel.setText("");
        revOrg.setText("");
        revToolLabel.setText("");
        revTool.setText("");
    }
}