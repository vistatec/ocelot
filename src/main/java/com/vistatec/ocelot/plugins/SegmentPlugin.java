package com.vistatec.ocelot.plugins;

import com.vistatec.ocelot.segment.Segment;

/**
 * Segment Plugins are notified when a user enters/exits a target segment edit
 * and when a user opens/saves a file.
 */
public interface SegmentPlugin extends Plugin {

    /**
     * Called when a segment's target becomes editable.
     * @param seg 
     */
    public void onSegmentTargetEnter(Segment seg);

    /**
     * Called when a segment's target has finished editing.
     * @param seg 
     */
    public void onSegmentTargetExit(Segment seg);

    /**
     * Called when a file is opened.
     * @param filename 
     */
    public void onFileOpen(String filename);

    /**
     * Called when a file is saved.
     * @param filename 
     */
    public void onFileSave(String filename);
}
