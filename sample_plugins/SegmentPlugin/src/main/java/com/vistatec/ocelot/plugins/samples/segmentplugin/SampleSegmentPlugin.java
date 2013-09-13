package com.vistatec.ocelot.plugins.samples.segmentplugin;

import com.vistatec.ocelot.plugins.SegmentPlugin;
import com.vistatec.ocelot.segment.Segment;
import java.util.Date;

/**
 * Sample Segment Plugin.
 */
public class SampleSegmentPlugin implements SegmentPlugin {
    @Override
    public String getPluginName() {
        return "Sample Segment Plugin";
    }

    @Override
    public String getPluginVersion() {
        return "1.0";
    }

    @Override
    public void onSegmentTargetEnter(Segment seg) {
        System.out.println("TU enter segment #"+seg.getTransUnitId()+": "+new Date());
    }

    @Override
    public void onSegmentTargetExit(Segment seg) {
        System.out.println("TU exit segment #"+seg.getTransUnitId()+": "+new Date());
    }

    @Override
    public void onFileOpen(String filename) {
        System.out.println("Open file '"+filename+"'");
    }

    @Override
    public void onFileSave(String filename) {
        System.out.println("Save file '"+filename+"'");
    }
}
