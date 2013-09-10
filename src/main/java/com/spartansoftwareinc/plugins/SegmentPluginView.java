package com.spartansoftwareinc.plugins;

import com.spartansoftwareinc.vistatec.rwb.segment.SegmentController;

/**
 * View for managing segment plugins.
 */
public class SegmentPluginView extends PluginManagerView {
    public SegmentPluginView(PluginManager pluginManager, SegmentController segController) {
        super(pluginManager, pluginManager.getSegmentPlugins(), segController);
    }
}
