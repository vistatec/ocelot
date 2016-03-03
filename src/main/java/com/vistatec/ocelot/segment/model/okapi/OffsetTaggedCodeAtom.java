package com.vistatec.ocelot.segment.model.okapi;

import net.sf.okapi.lib.xliff2.core.Tag;

/**
 * This class tracks the Okapi tag the code atom was created for. In addition,
 * it stores info about the offset in the text fragment where the tag should be
 * inserted.
 */
public class OffsetTaggedCodeAtom extends TaggedCodeAtom {

    /** The offset in the text where the tag has to been inserted. */
    private int offset;

    /**
     * Constructor.
     * 
     * @param tag
     *            the tag
     * @param data
     *            the tag data
     * @param verboseData
     *            the tag attributes
     * @param offset
     *            the tag offset
     */
    public OffsetTaggedCodeAtom(Tag tag, String data, String verboseData,
            int offset) {
        super(tag, data, verboseData);
        this.offset = offset;
    }

    /**
     * Gets the tag offset.
     * 
     * @return the tag offset.
     */
    public int getOffset() {
        return offset;
    }
}
