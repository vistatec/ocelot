package com.vistatec.ocelot.segment.okapi;

import com.vistatec.ocelot.segment.model.CodeAtom;

import net.sf.okapi.lib.xliff2.core.Tag;

/**
 * XLIFF 2.0 CodeAtom, tracks which Okapi Tag the code atom was created from.
 */
public class TaggedCodeAtom extends CodeAtom {
    private Tag tag;

    public TaggedCodeAtom(Tag tag, String data, String verboseData) {
        super(tag.getId(), data, verboseData);
        this.tag = tag;
    }

    protected Tag getTag() {
        return this.tag;
    }
}
