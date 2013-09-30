package com.vistatec.ocelot.plugins;

import com.vistatec.ocelot.segment.SegmentController;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View for managing segment plugins.
 */
public class SegmentPluginView extends PluginManagerView {
    private Logger LOG = LoggerFactory.getLogger(SegmentPluginView.class);
    private static Image icon;

    public SegmentPluginView(PluginManager pluginManager, SegmentController segController, Image icon) {
        super(pluginManager, pluginManager.getSegmentPlugins(), segController, icon);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == selectPluginDir) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    pluginManager.discover(fc.getSelectedFile());
                    initPlugins(pluginManager.getSegmentPlugins());
                    revalidate();
                } catch (IOException ex) {
                    LOG.warn("Plugin directory IOException", ex);
                    JOptionPane.showMessageDialog(frame, "Error reading specified plugin directory.");
                }
            }
        }
    }
}
