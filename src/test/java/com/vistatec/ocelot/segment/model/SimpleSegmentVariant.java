package com.vistatec.ocelot.segment.model;


import java.util.ArrayList;
import java.util.List;

/**
 * SegmentVariant implementation for use in tests.
 */
public class SimpleSegmentVariant extends BaseSegmentVariant {
    private List<SegmentAtom> atoms = new ArrayList<>();

    private SimpleSegmentVariant() {
    }

    public SimpleSegmentVariant(String text) {
        atoms.add(new TextAtom(text));
    }

    public SimpleSegmentVariant(List<SegmentAtom> atoms) {
        this.atoms = atoms;
    }

    @Override
    public List<SegmentAtom> getAtoms() {
        return atoms;
    }

    @Override
    public SegmentVariant createEmptyTarget() {
        return new SimpleSegmentVariant();
    }

    @Override
    public SegmentVariant createCopy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContent(SegmentVariant variant) {
        atoms.clear();
        atoms.addAll(((SimpleSegmentVariant)variant).getAtoms());
    }

    @Override
    public void modifyChars(int offset, int charsToReplace, String newText) {
        throw new UnsupportedOperationException("Test code doesn't implement modifyChars");
    }

    @Override
    protected void setAtoms(List<SegmentAtom> atoms) {
        this.atoms = atoms;
    }

    public static class Builder {
        List<SegmentAtom> segAtoms = new ArrayList<>();

        public Builder text(String text) {
            segAtoms.add(new TextAtom(text));
            return this;
        }

        public Builder code(String id, String basic, String verbose) {
            segAtoms.add(new CodeAtom(id, basic, verbose));
            return this;
        }

        public SegmentVariant build() {
            return new SimpleSegmentVariant(segAtoms);
        }
    }
}
