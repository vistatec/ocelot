package com.spartansoftwareinc.plugins;

import com.spartansoftwareinc.vistatec.rwb.segment.SegmentController;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View for managing ITS plugins.
 */
public class ITSPluginManagerView extends PluginManagerView {
    private static Logger LOG = LoggerFactory.getLogger(ITSPluginManagerView.class);
    private JButton export;

    public ITSPluginManagerView(PluginManager pluginManager, SegmentController segController) {
        super(pluginManager, pluginManager.getITSPlugins(), segController);

        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.anchor = GridBagConstraints.FIRST_LINE_START;
        
        export = new JButton("Export Data");
        export.addActionListener(this);
        export.setEnabled(segController.openFile());
        gridBag.gridx = 1;
        gridBag.gridy = 0;
        add(export, gridBag);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == export) {
            pluginManager.exportData(segmentController.getFileSourceLang(),
                    segmentController.getFileTargetLang(),
                    segmentController.getSegmentTableModel());
        } else if (ae.getSource() == selectPluginDir) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    pluginManager.discover(fc.getSelectedFile());
                    initPlugins(pluginManager.getITSPlugins());
                    revalidate();
                } catch (IOException ex) {
                    LOG.warn("Plugin directory IOException", ex);
                    JOptionPane.showMessageDialog(frame, "Error reading specified plugin directory.");
                }
            }
        }
    }
}
