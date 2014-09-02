package com.vistatec.ocelot.segment;

import net.sf.okapi.common.HashCodeUtil;

public class CodeAtom implements SegmentAtom {
    private int id;
    private String data, verboseData;
    public CodeAtom(int id, String data, String verboseData) {
        this.id = id;
        this.data = data;
        this.verboseData = verboseData;
    }
    public int getId() {
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
        return id == c.id && data.equals(c.data) && verboseData.equals(c.verboseData);
    }
    @Override
    public int hashCode() {
        int h = HashCodeUtil.hash(HashCodeUtil.SEED, id);
        h = HashCodeUtil.hash(h, data);
        return HashCodeUtil.hash(h, verboseData);
    }
    @Override
    public String toString() {
        return "[id=" + id + ", data='" + data + "', verbose='" + verboseData + "']";
    }
}
