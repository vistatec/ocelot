package com.vistatec.ocelot.segment;

/**
 * Listens for changes in the segment selection.
 */
public interface SegmentSelectionListener {

    /**
     * Signals that a segment has been selected in
     * the main segment view.
     */
    public void segmentSelected(Segment segment);

    /**
     * Signals that the target of a segment has been modified.
     */
    public void segmentEdited(Segment segment, SegmentVariant previousTarget);
}
