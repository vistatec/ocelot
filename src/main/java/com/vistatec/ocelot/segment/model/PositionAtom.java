package com.vistatec.ocelot.segment.model;

import java.util.List;

import com.vistatec.ocelot.segment.view.SegmentTextCell;

public class PositionAtom implements SegmentAtom {

    private final BaseSegmentVariant bsv;

    public PositionAtom(BaseSegmentVariant bsv) {
        this.bsv = bsv;
    }

    public int getPosition() {
        List<SegmentAtom> atoms = bsv.getAtoms();
        int pos = 0;
        for (SegmentAtom atom : atoms) {
            if (atom == this) {
                return pos;
            } else {
                pos += atom.getLength();
            }
        }
        throw new IllegalStateException("This PositionAtom is no longer associated with its parent SegmentVariant.");
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public String getData() {
        return "";
    }

    @Override
    public String getTextStyle() {
        return SegmentTextCell.regularStyle;
    }

}
