package com.vistatec.ocelot.segment.model;

import com.vistatec.ocelot.segment.SegmentTextCell;

public class TextAtom implements SegmentAtom {
    private String text;
    public TextAtom(String text) {
        this.text = text;
    }
    @Override
    public int getLength() {
        return text.length();
    }
    @Override
    public String getData() {
        return text;
    }
    @Override
    public String getTextStyle() {
        return SegmentTextCell.regularStyle;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof TextAtom)) return false;
        return text.equals(((TextAtom)o).text);
    }
    @Override
    public int hashCode() {
        return text.hashCode();
    }
    @Override
    public String toString() {
        return '[' + text + ']';
    }
}
