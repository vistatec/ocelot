package com.vistatec.ocelot.segment;

import java.util.ArrayList;
import java.util.List;

/**
 * A text-only SegmentVariant implementation (no codes) to
 * simplify construction of Segment instances for tests.
 */
public class SimpleSegmentVariant extends BaseSegmentVariant {
    private List<SegmentAtom> atoms = new ArrayList<SegmentAtom>();;

    private SimpleSegmentVariant() {
    }

    public SimpleSegmentVariant(String text) {
        atoms.add(new TextAtom(text));
    }

    public SimpleSegmentVariant(List<SegmentAtom> atoms) {
        this.atoms = atoms;
    }

    @Override
    protected List<SegmentAtom> getAtoms() {
        return atoms;
    }

    @Override
    public SegmentVariant createEmpty() {
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
}
