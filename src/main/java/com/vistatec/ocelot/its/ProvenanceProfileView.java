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
package com.vistatec.ocelot.its;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.ui.ODialogPanel;

/**
 * Provenance configuration view.
 */
public class ProvenanceProfileView extends ODialogPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private Logger LOG = LoggerFactory.getLogger(ProvenanceProfileView.class);

    private JTextField inputRevPerson, inputRevOrg, inputExtRef;
    private JButton save;
    private ProvenanceConfig config;
    private UserProvenance prov;

    public ProvenanceProfileView(ProvenanceConfig config) {
        super(new GridBagLayout());
        this.config = config;
        setBorder(new EmptyBorder(10,10,10,10));

        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.gridwidth = 1;

        prov = config.getUserProvenance();

        JLabel revPersonLabel = new JLabel("Reviewer: ");
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        add(revPersonLabel, gridBag);

        inputRevPerson = new JTextField(15);
        inputRevPerson.setText(prov.getRevPerson());
        gridBag.gridx = 1;
        gridBag.gridy = 0;
        add(inputRevPerson, gridBag);

        JLabel revOrgLabel = new JLabel("Organization: ");
        gridBag.gridx = 0;
        gridBag.gridy = 1;
        add(revOrgLabel, gridBag);

        inputRevOrg = new JTextField(15);
        inputRevOrg.setText(prov.getRevOrg());
        gridBag.gridx = 1;
        gridBag.gridy = 1;
        add(inputRevOrg, gridBag);

        JLabel extRefLabel = new JLabel("External Reference: ");
        gridBag.gridx = 0;
        gridBag.gridy = 2;
        add(extRefLabel, gridBag);

        inputExtRef = new JTextField(15);
        inputExtRef.setText(prov.getProvRef());
        gridBag.gridx = 1;
        gridBag.gridy = 2;
        add(inputExtRef, gridBag);

        save = new JButton("Save");
        save.addActionListener(this);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new DisposeDialogListener());

        JPanel actionPanel = new JPanel();
        actionPanel.add(save);
        actionPanel.add(cancel);
        gridBag.gridx = 1;
        gridBag.gridy = 3;
        add(actionPanel, gridBag);

    }

    @Override
    public JButton getDefaultButton() {
        return save;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == save) {
            prov.setRevPerson(inputRevPerson.getText());
            prov.setRevOrg(inputRevOrg.getText());
            prov.setProvRef(inputExtRef.getText());
            try {
                config.save(prov);
            } catch (IOException ex) {
                LOG.warn("Unable to save changes to user provenance", ex);
            }
            getDialog().dispose();
        }
    }
}
