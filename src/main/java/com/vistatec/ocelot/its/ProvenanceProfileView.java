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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Provenance configuration view.
 */
public class ProvenanceProfileView extends JPanel implements Runnable, ActionListener {
    private static final long serialVersionUID = 1L;

    private JFrame frame;
    private Image icon;
    private JTextField inputRevPerson, inputRevOrg, inputExtRef;
    private JButton save;
    private String revPerson, revOrg, extRef;
    private Properties p;
    private File provFile;

    public ProvenanceProfileView(Image icon) throws IOException {
        super(new GridBagLayout());
        this.icon = icon;
        setBorder(new EmptyBorder(10,10,10,10));
        parseConfig();

        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.gridwidth = 1;

        JLabel revPersonLabel = new JLabel("Reviewer: ");
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        add(revPersonLabel, gridBag);

        inputRevPerson = new JTextField(15);
        inputRevPerson.setText(revPerson);
        gridBag.gridx = 1;
        gridBag.gridy = 0;
        add(inputRevPerson, gridBag);

        JLabel revOrgLabel = new JLabel("Organization: ");
        gridBag.gridx = 0;
        gridBag.gridy = 1;
        add(revOrgLabel, gridBag);

        inputRevOrg = new JTextField(15);
        inputRevOrg.setText(revOrg);
        gridBag.gridx = 1;
        gridBag.gridy = 1;
        add(inputRevOrg, gridBag);

        JLabel extRefLabel = new JLabel("External Reference: ");
        gridBag.gridx = 0;
        gridBag.gridy = 2;
        add(extRefLabel, gridBag);

        inputExtRef = new JTextField(15);
        inputExtRef.setText(extRef);
        gridBag.gridx = 1;
        gridBag.gridy = 2;
        add(inputExtRef, gridBag);

        save = new JButton("Save");
        save.addActionListener(this);
        JPanel actionPanel = new JPanel();
        actionPanel.add(save);
        gridBag.gridx = 1;
        gridBag.gridy = 3;
        add(actionPanel, gridBag);
    }

    public final void readConfigFile() throws IOException {
        p = new Properties();
        File rwDir = new File(System.getProperty("user.home"), ".ocelot");
        rwDir.mkdirs();
        provFile = new File(rwDir, "provenance.properties");
        if (provFile.exists()) {
            p.load(new FileInputStream(provFile));
        }
    }

    public final void parseConfig() throws IOException {
        readConfigFile();
        revPerson = p.getProperty("revPerson");
        revOrg = p.getProperty("revOrganization");
        extRef = p.getProperty("externalReference");
    }

    public void saveConfig() throws FileNotFoundException, IOException {
        p.setProperty("revPerson", revPerson);
        p.setProperty("revOrganization", revOrg);
        p.setProperty("externalReference", extRef);
        File rwDir = new File(System.getProperty("user.home"),".ocelot");
        provFile = new File(rwDir, "provenance.properties");
        p.store(new FileOutputStream(provFile), null);
    }

    @Override
    public void run() {
        frame = new JFrame("Credentials");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImage(icon);

        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == save) {
            revPerson = inputRevPerson.getText();
            revOrg = inputRevOrg.getText();
            extRef = inputExtRef.getText();
            try {
                saveConfig();
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
            frame.dispose();
        }
    }
}
