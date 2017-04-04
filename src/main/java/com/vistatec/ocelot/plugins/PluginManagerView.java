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
package com.vistatec.ocelot.plugins;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.vistatec.ocelot.OcelotApp;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.ui.ODialogPanel;

/**
 * View for managing active plugins.
 */
public class PluginManagerView extends ODialogPanel implements ActionListener, ItemListener {
    private static final long serialVersionUID = 1L;

    private static Logger LOG = LoggerFactory.getLogger(PluginManagerView.class);
    protected JButton selectPluginDir;
    protected PluginManager pluginManager;
    private HashMap<JCheckBox, Plugin> checkboxToPlugin;
    private JButton export;
    private GridBagConstraints gridBag;

    private OcelotApp ocelotApp;
    private SegmentService segmentService;

    @Inject
    public PluginManagerView(PluginManager pluginManager, OcelotApp ocelotApp,
            SegmentService segmentService) {
        super(new GridBagLayout());
        this.pluginManager = pluginManager;
        this.ocelotApp = ocelotApp;
        this.segmentService = segmentService;

        checkboxToPlugin = new HashMap<JCheckBox, Plugin>();
        setBorder(new EmptyBorder(10,10,10,10));

        gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBag.gridwidth = 4;
        gridBag.ipadx = 5;
        gridBag.ipady = 5;
        gridBag.insets = new Insets(5, 10, 5, 10);
        
        selectPluginDir = new JButton("Set Plugin Directory");
        selectPluginDir.addActionListener(this);
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        gridBag.gridwidth = 2;
        add(selectPluginDir, gridBag);

        export = new JButton("Export Data");
        export.addActionListener(this);
        gridBag.gridx = 2;
        gridBag.gridy = 0;
        gridBag.gridwidth = 2;
        add(export, gridBag);
        
        JLabel title = new JLabel("Plugin Name");
        Font font = title.getFont().deriveFont(Font.BOLD, 14);
        title.setFont(font);
        title.setBorder(new EmptyBorder(0,0,0,0)); // first dimension was originally 10
        gridBag.gridx = 0;
        gridBag.gridy = 1;
        gridBag.gridwidth = 2;
        add(title, gridBag);
        JLabel title2 = new JLabel("Type");
        title2.setFont(font);
        title2.setBorder(new EmptyBorder(0,0,0,0));
        gridBag.gridx = 2;
        gridBag.gridwidth = 1;
        add(title2, gridBag);
        JLabel title3 = new JLabel("Version");
        title3.setFont(font);
        title3.setBorder(new EmptyBorder(0,0,0,0));
        gridBag.gridx = 3;
        gridBag.gridwidth = 1;
        add(title3, gridBag);

        initPlugins(pluginManager.getPlugins());
    }

    /**
     * Set the enabled state for the "Export Data" button.  This button is
     * only enabled if there is an open file and at least one {@link ITSPlugin}
     * is defined.
     * @param segController
     */
    private void setExportEnabledState() {
        export.setEnabled(ocelotApp.hasOpenFile() &&
                          !pluginManager.getEnabledITSPlugins().isEmpty());
    }

    public void initPlugins(Set<? extends Plugin> plugins) {
        setExportEnabledState();

        for (JCheckBox pluginBox : checkboxToPlugin.keySet()) {
            remove(pluginBox);
        }

        int gridy = 2;
        if (!checkboxToPlugin.isEmpty()) {
            for (JCheckBox pluginBox : checkboxToPlugin.keySet()) {
                remove(pluginBox);
            }
            checkboxToPlugin = new HashMap<JCheckBox, Plugin>();
        }
        for (Plugin plugin : plugins) {
            addPluginRow(gridy++, plugin);
        }
        
        //TODO
    }
    
    void addPluginRow(int gridy, Plugin plugin) {
        JCheckBox pluginBox = new JCheckBox(plugin.getPluginName());
        Font font = pluginBox.getFont().deriveFont(Font.PLAIN);
        pluginBox.setFont(font);
        pluginBox.setSelected(pluginManager.isEnabled(plugin));
        pluginBox.addItemListener(this);
        pluginBox.setBorder(new EmptyBorder(0, 0, 0, 0));
        gridBag.gridx = 0;
        gridBag.gridy = gridy;
        gridBag.gridwidth = 2;
        add(pluginBox, gridBag);
        checkboxToPlugin.put(pluginBox, plugin);
        JLabel typeLabel = new JLabel(getPluginType(plugin));
        gridBag.gridx = 2;
        gridBag.gridwidth = 1;
        typeLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        typeLabel.setFont(font);
        add(typeLabel, gridBag);
        JLabel pluginLabel = new JLabel(plugin.getPluginVersion());
        gridBag.gridx = 3;
        gridBag.gridwidth = 1;
        pluginLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        pluginLabel.setFont(font);
        add(pluginLabel, gridBag);
    }
    
    private String getPluginType(Plugin plugin) {
        return (plugin instanceof ITSPlugin) ? "ITS" :
                 (plugin instanceof SegmentPlugin) ? "Segment" :
                   "Unknown";
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        if (source.getClass().equals(JCheckBox.class)) {
            JCheckBox checkbox = (JCheckBox) source;
            try {
                pluginManager.setEnabled(checkboxToPlugin.get(checkbox),
                        e.getStateChange() == ItemEvent.SELECTED);
            } catch (TransferException ex) {
                LOG.error("Failed to save enabling plugin", ex);
                JOptionPane.showMessageDialog(getDialog(), "Could not save plugin enabled.");
            }
            setExportEnabledState();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == export) {
            pluginManager.exportData(ocelotApp.getFileSourceLang(),
                    ocelotApp.getFileTargetLang(),
                    segmentService);
        }
        else if (ae.getSource() == selectPluginDir) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    pluginManager.discover(fc.getSelectedFile());
                    pluginManager.pluginsAdded();
                    initPlugins(pluginManager.getPlugins());
                    ocelotApp.handleNewPluginInstalled();
                    revalidate();
                } catch (IOException ex) {
                    LOG.warn("Plugin directory IOException", ex);
                    JOptionPane.showMessageDialog(getDialog(), "Error reading specified plugin directory.");
                }
            }
        }
    }
}
