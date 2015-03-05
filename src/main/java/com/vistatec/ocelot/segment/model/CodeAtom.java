package com.vistatec.ocelot.segment.model;

import com.vistatec.ocelot.segment.view.SegmentTextCell;

import java.util.Objects;

public class CodeAtom implements SegmentAtom {
    private String id;
    private String data, verboseData;

    public CodeAtom(String id, String data, String verboseData) {
        this.id = id;
        this.data = data;
        this.verboseData = verboseData;
    }

    public String getId() {
        return id;
    }

    @Override
    public int getLength() {
        return data.length();
    }

    @Override
    public String getData() {
        return data;
    }

    public String getVerboseData() {
        return verboseData;
    }

    @Override
    public String getTextStyle() {
        return SegmentTextCell.tagStyle;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof CodeAtom)) return false;
        CodeAtom c = (CodeAtom)o;
        return Objects.equals(id, c.id) &&
               Objects.equals(data, c.data) &&
               Objects.equals(verboseData, c.verboseData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data, verboseData);
    }

    @Override
    public String toString() {
        return "[id=" + id + ", data='" + data + "', verbose='" + verboseData + "']";
    }
}
