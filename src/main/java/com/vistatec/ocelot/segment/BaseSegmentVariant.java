package com.vistatec.ocelot.segment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public abstract class BaseSegmentVariant implements SegmentVariant {

    protected abstract List<SegmentAtom> getAtoms();

    protected abstract void setAtoms(List<SegmentAtom> atoms);

    List<SegmentAtom> getAtomsForRange(int start, int length) {
        List<SegmentAtom> atomsForRange = Lists.newArrayList();
        int index = 0;
        int end = start + length;

        for (SegmentAtom atom : getAtoms()) {
            if (index >= end) {
                return atomsForRange;
            }
            if (index + atom.getLength() > start) {
                if (atom instanceof CodeAtom) {
                    atomsForRange.add(atom);
                }
                else {
                    int min = Math.max(start - index, 0);
                    int max = Math.min(end - index, atom.getData().length());
                    atomsForRange.add(new TextAtom(atom.getData().substring(min, max)));
                }
            }
            index += atom.getLength();
        }
        return atomsForRange;
    }

    public int getLength() {
        int len = 0;
        for (SegmentAtom atom : getAtoms()) {
            len += atom.getLength();
        }
        return len;
    }

    @Override
    public String getDisplayText() {
        StringBuilder sb = new StringBuilder();
        for (SegmentAtom atom : getAtoms()) {
            sb.append(atom.getData());
        }
        return sb.toString();
    }

    @Override
    public List<String> getStyleData(boolean verbose) {
        ArrayList<String> textToStyle = new ArrayList<String>();

        for (SegmentAtom atom : getAtoms()) {
            if (atom instanceof CodeAtom && verbose) {
                textToStyle.add(((CodeAtom)atom).getVerboseData());
            }
            else {
                textToStyle.add(atom.getData());
            }
            textToStyle.add(atom.getTextStyle());
        }
        return textToStyle;
    }

    @Override
    public boolean containsTag(int offset, int length) {
        return checkForCode(offset, length).size() > 0;
    }

    @Override
    public boolean canInsertAt(int offset) {
        return checkForCode(offset, 0).size() == 0;
    }

    // Returns list of codes that occur in the specified range
    private List<CodeAtom> checkForCode(int offset, int length) {
        List<CodeAtom> codes = Lists.newArrayList();

        int offsetEnd = offset + length;
        int index = 0;
        for (SegmentAtom atom : getAtoms()) {
            if (index > offsetEnd) {
                // We've drifted out of the danger zone
                return codes;
            }
            if (atom instanceof CodeAtom) {
                CodeAtom code = (CodeAtom)atom;
                if (offsetEnd > index && offset < index + code.getLength()) {
                    codes.add(code);
                }
            }
            index += atom.getLength();
        }
        return codes;
    }


    @Override
    public void replaceSelection(int selectionStart, int selectionEnd,
            SegmentVariantSelection rsv) {

        BaseSegmentVariant sv = (BaseSegmentVariant)rsv.getVariant(); // XXX
        List<SegmentAtom> replaceAtoms = sv.getAtomsForRange(rsv.getSelectionStart(), 
                rsv.getSelectionEnd() - rsv.getSelectionStart());

        List<SegmentAtom> newAtoms = Lists.newArrayList();
        newAtoms.addAll(getAtomsForRange(0, selectionStart));
        newAtoms.addAll(replaceAtoms);
        newAtoms.addAll(getAtomsForRange(selectionEnd, getLength()));

        // Clean up codes that may be duplicates
        Set<Integer> codeIds = new HashSet<Integer>();
        List<SegmentAtom> cleanedAtoms = Lists.newArrayList();
        // Strip any atoms that exist twice
        for (SegmentAtom atom : newAtoms) {
            if (atom instanceof CodeAtom) {
                int id = ((CodeAtom)atom).getId();
                if (!codeIds.contains(id)) {
                    codeIds.add(id);
                    cleanedAtoms.add(atom);
                }
            }
            else {
                cleanedAtoms.add(atom);
            }
        }
        // Append any atoms that were deleted
        List<CodeAtom> originalCodes = findCodes(getAtoms());
        for (CodeAtom code : originalCodes) {
            if (!codeIds.contains(code.getId())) {
                cleanedAtoms.add(code);
            }
        }
        setAtoms(cleanedAtoms);
    }

    @Override
    public void modifyChars(int offset, int charsToReplace, String newText) {
        int index = 0;
        // The contract of this method is such that it never replaces a code,
        // so the entire modification will always happen within a single text atom.
        List<SegmentAtom> atoms = getAtoms();
        List<SegmentAtom> newAtoms = Lists.newArrayList();
        boolean done = false;
        for (int i = 0; i < atoms.size(); i++) {
            SegmentAtom atom = atoms.get(i);
            if (atom instanceof TextAtom) {
                if (index >= offset && !done) {
                    // Do the text replacement
                    String origText = atom.getData();
                    int atomOffset = Math.max(offset - index, 0);
                    newAtoms.add(new TextAtom(origText.substring(0, atomOffset)));
                    if (newText != null) { // handle delete case
                        newAtoms.add(new TextAtom(newText));
                    }
                    newAtoms.add(new TextAtom(origText.substring(atomOffset + charsToReplace)));
                    done = true;
                }
                else {
                    newAtoms.add(atom);
                }
            }
            else if (atom instanceof CodeAtom) {
                // I need to handle this separately because I might
                // be inserting at the start of a segment that opens with a code.
                // If it was just the text/code boundaries, I could handle them above.
                if (index >= offset && !done) {
                    newAtoms.add(new TextAtom(newText));
                    done = true;
                }
                newAtoms.add(atom);
            }
            index += atom.getLength();
        }
        // Check for append
        if (index == offset) {
            newAtoms.add(new TextAtom(newText));
        }
        setAtoms(newAtoms);
    }

    private List<CodeAtom> findCodes(List<SegmentAtom> atoms) {
        List<CodeAtom> codes = Lists.newArrayList();
        for (SegmentAtom atom : atoms) {
            if (atom instanceof CodeAtom) {
                codes.add((CodeAtom)atom);
            }
        }
        return codes;
    }

}
