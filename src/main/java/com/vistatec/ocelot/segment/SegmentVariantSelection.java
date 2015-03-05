package com.vistatec.ocelot.segment;

import com.vistatec.ocelot.segment.model.SegmentVariant;

/**
 * Represents a clipboard selection of SegmentVariant content.
 */
public class SegmentVariantSelection {
    private int row;
    private SegmentVariant variant;
    // Indexes into the display representation of variant
    private int selectionStart, selectionEnd;

    public SegmentVariantSelection(int row, SegmentVariant variant, int start, int end) {
        this.row = row;
        this.variant = variant;
        this.selectionStart = start;
        this.selectionEnd = end;
    }

    public int getRow() {
        return row;
    }

    public SegmentVariant getVariant() {
        return variant;
    }

    public int getSelectionStart() {
        return selectionStart;
    }

    public int getSelectionEnd() {
        return selectionEnd;
    }
    
    @Override
    public String toString() {
        return "Row " + row + " [" + selectionStart + ", " + selectionEnd + "] of " + variant;
    }
}
