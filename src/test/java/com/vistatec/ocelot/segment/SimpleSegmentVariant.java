package com.vistatec.ocelot.segment;

import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.ObjectUtils;

/**
 * A text-only SegmentVariant implementation (no codes) to
 * simplify construction of Segment instances for tests.
 */
public class SimpleSegmentVariant implements SegmentVariant {
    private String text;
    public SimpleSegmentVariant(String text) {
        this.text = text;
    }

    @Override
    public SegmentVariant createEmpty() {
        return new SimpleSegmentVariant("");
    }

    @Override
    public SegmentVariant createCopy() {
        return new SimpleSegmentVariant(text);
    }

    @Override
    public void setContent(SegmentVariant variant) {
        this.text = ((SimpleSegmentVariant)variant).text;
    }

    @Override
    public String getDisplayText() {
        return text;
    }

    @Override
    public List<String> getStyleData(boolean verbose) {
        List<String> l = new ArrayList<String>(2);
        l.add(text);
        l.add(SegmentTextCell.regularStyle);
        return l;
    }

    @Override
    public boolean containsTag(int offset, int length) {
        return false;
    }

    @Override
    public void modifyChars(int offset, int charsToReplace, String newText) {
        text = text.substring(0, offset) + newText +
               text.substring(offset + charsToReplace);
    }

    @Override
    public boolean textIsInsertable(String text) {
        return true;
    }

    @Override
    public boolean canInsertAt(int offset) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof SimpleSegmentVariant)) return false;
        return ObjectUtils.safeEquals(text, ((SimpleSegmentVariant)o).text);
    }
}
