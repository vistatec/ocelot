package com.vistatec.ocelot.segment.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.vistatec.ocelot.segment.view.SegmentVariantSelection;

public abstract class BaseSegmentVariant implements SegmentVariant {

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
    public int findSelectionStart(int selectionStart) {
        while (containsTag(selectionStart, 0)) {
            selectionStart--;
        }
        return selectionStart;
    }

    @Override
    public int findSelectionEnd(int selectionEnd) {
        while (containsTag(selectionEnd, 0)) {
            selectionEnd++;
        }
        return selectionEnd;
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
        Set<String> codeIds = new HashSet<String>();
        List<SegmentAtom> cleanedAtoms = Lists.newArrayList();
        // Strip any atoms that exist twice
        for (SegmentAtom atom : newAtoms) {
            if (atom instanceof CodeAtom) {
                String id = ((CodeAtom)atom).getId();
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

    /**
     * Modify the text contents of the segment; Assumes checks for attempting to change
     * parts of the segment's unmodifiable text (such as protected codes) was already made.
     * @param insertCharacterOffset - Character position indexed from the beginning of the segment to start inserting text
     * @param charsToReplace - Number of characters to delete in the original segment text starting from the {@code insertionOffset}
     * @param newText - String to insert at the {@code insertionOffset}; Swing sets to {@code null} if no text to insert
     */
    @Override
    public void modifyChars(int insertCharacterOffset, int charsToReplace, String newText) {
        int caretPosition = 0;
        List<SegmentAtom> atoms = getAtoms();
        List<SegmentAtom> newAtoms = Lists.newArrayList();
        boolean done = false;
        boolean insertingText = newText != null;

        for (SegmentAtom atom : atoms) {
            Range<Integer> atomCharacterRange = Range.closedOpen(caretPosition,
                    caretPosition+atom.getLength());
            if (atomCharacterRange.contains(insertCharacterOffset) && !done) {
                if (atom instanceof CodeAtom) {
                    // Assume inserting at the start of a CodeAtom;
                    // append handled by next atom (after for-loop if last atom)
                    if (insertingText) {
                        // Ignore incrementing caret for newText; delete/replace based off of original text.
                        newAtoms.add(new TextAtom(newText));
                    }
                    newAtoms.add(atom);
                    done = true;

                } else if (atom instanceof TextAtom) {
                    String origAtomText = atom.getData();

                    int atomCharInsertionIndex = Math.max(insertCharacterOffset - caretPosition, 0);
                    newAtoms.add(new TextAtom(origAtomText.substring(0, atomCharInsertionIndex)));

                    if (insertingText) {
                        // Ignore incrementing caret for newText; delete/replace based off of original text.
                        newAtoms.add(new TextAtom(newText));
                    }

                    // Ignore decreasing caret for removing text; delete/replace based off of original text.
                    newAtoms.add(new TextAtom(origAtomText.substring(atomCharInsertionIndex + charsToReplace)));
                    if (atomCharInsertionIndex + charsToReplace > atom.getLength()) {
                        insertCharacterOffset = atomCharacterRange.upperEndpoint();
                        charsToReplace -= (atom.getLength()-atomCharInsertionIndex);
                    } else {
                        done = true;
                    }
                }

            } else {
                newAtoms.add(atom);
            }

            caretPosition += atom.getLength();
        }
        // Check for appending text to the end of the segment (no delete or replace)
        if (caretPosition == insertCharacterOffset) {
            newAtoms.add(new TextAtom(newText));
        }

        setAtoms(mergeNeighboringTextAtoms(newAtoms));
    }

    /**
     * Prevent unnecessary SegmentAtom text fragmentation.
     */
    private List<SegmentAtom> mergeNeighboringTextAtoms(List<SegmentAtom> segmentAtoms) {
        LinkedList<SegmentAtom> defraggedAtoms = new LinkedList<SegmentAtom>();
        for (SegmentAtom atom : segmentAtoms) {
            if (atom instanceof TextAtom && !defraggedAtoms.isEmpty() &&
                    defraggedAtoms.getLast() instanceof TextAtom) {
                TextAtom mergedTextAtom = new TextAtom(
                        defraggedAtoms.getLast().getData()+atom.getData());

                defraggedAtoms.removeLast();
                defraggedAtoms.add(mergedTextAtom);

            } else {
                defraggedAtoms.add(atom);
            }
        }
        return defraggedAtoms;
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
