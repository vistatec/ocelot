package com.spartansoftwareinc.plugins;

import com.spartansoftwareinc.vistatec.rwb.segment.SegmentController;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import javax.swing.JButton;

/**
 * View for managing ITS plugins.
 */
public class ITSPluginManagerView extends PluginManagerView {
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
        super.actionPerformed(ae);
        if (ae.getSource() == export) {
            pluginManager.exportData(segmentController.getFileSourceLang(),
                    segmentController.getFileTargetLang(),
                    segmentController.getSegmentTableModel());
        }
    }
}
