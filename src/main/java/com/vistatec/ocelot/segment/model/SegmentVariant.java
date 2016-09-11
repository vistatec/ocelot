package com.vistatec.ocelot.segment.model;

import java.util.List;

import com.vistatec.ocelot.segment.view.SegmentVariantSelection;

/**
 * Abstract representation of a segment variant (eg, the source
 * or target).
 */
public interface SegmentVariant {

    /**
     * Create a new (empty) variant of this type.
     * @return
     */
    SegmentVariant createEmptyTarget();

    /**
     * Create a new variant containing the same content as
     * this variant.
     */
    SegmentVariant createCopy();

    /**
     * Set the content of this variant to a copy of the 
     * content in another variant.
     */
    void setContent(SegmentVariant variant);

    /**
     * Get the text representation of this variant for display
     * in the segment cells.
     */
    String getDisplayText();

    /**
     * Get the SegmentAtom representation of this variant for easier
     * serialization to other formats.
     * @return
     */
    List<SegmentAtom> getAtoms();

    /**
     * Get the SegmentAtom at the specified offset.
     * 
     * @param offset
     * @return The atom, or null if the offset is out of bounds
     */
    SegmentAtom getAtomAt(int offset);

    /**
     * Return the style information for the editable cells.  The return
     * value is a list of paired strings:
     *  [0] - text
     *  [1] - style information for that text (one of the static values
     *        defined in SegmentTextCell).
     * This code needs further refactoring.
     *
     * @param verbose if true, provide more verbose information about inline codes
     * @return style data
     */
    List<String> getStyleData(boolean verbose);

    /**
     * Tests whether the specified text range contains a tag or not.
     * @param offset
     * @param length
     * @return
     */
    boolean containsTag(int offset, int length);

    /**
     * Find a valid selection start mark, possibly by backing up the
     * provided index to the beginning of the previous tag.
     * @param selectionStart provisional selection start
     * @return updated selection start index
     */
    int findSelectionStart(int selectionStart);

    /**
     * Find a valid selection end mark, possibly by moving the
     * provided index to the beginning of the next tag.
     * @param selectionStart provisional selection end
     * @return updated selection end index
     */
    int findSelectionEnd(int selectionEnd);

    /**
     * Replace a character range. 
     * @param offset
     * @param charsToReplace Number of chars to remove. May be zero, in which
     *              the new text should simply be inserted.
     * @param newText New chars to replace. May be null, in which case
     *              the original chars should be deleted with no replacement.
     */
    void modifyChars(int offset, int charsToReplace, String newText);

    /**
     * Checks to see if an offset into the variant text is an insertable
     * position.  (For example, insertion in the middle of codes may be
     * disallowed.)
     * @param o
     * @return
     */
    boolean canInsertAt(int offset);

    /**
     * Replace a selection (specified by offsets) with a selection from
     * another segment variant.
     *
     * @param selectionStart start of the selection to be replaced
     * @param selectionEnd end of the selection to be replaced
     * @param rsv content with which to replace the current selection
     */
    public void replaceSelection(int selectionStart, int selectionEnd,
            SegmentVariantSelection rsv);

    /**
     * Replace a selection (specified by offsets) with a list of atoms.
     *
     * @param selectionStart start of the selection to be replaced
     * @param selectionEnd end of the selection to be replaced
     * @param atoms content with which to replace the current selection
     */
    public void replaceSelection(int selectionStart, int selectionEnd, List<? extends SegmentAtom> atoms);

    /**
     * Delete a selection (specified by offsets).
     * @param selectionStart start of the selection to be cleared
     * @param selectionEnd end of the selection to be cleared
     */
    public void clearSelection(int selectionStart, int selectionEnd);

    /**
     * When true, this segment variant has been modified in an unsafe way (by
     * {@link #replaceSelection(int, int, SegmentVariantSelection)}) and may not
     * have valid codes.
     */
    public boolean needsValidation();

    /**
     * Check that this segment variant is consistent with another one
     * (presumably an original made with {@link #createCopy()} before this one
     * was modified).
     * <p>
     * Variants are considered consistent if they have the same number of tags,
     * and all tags in the other variant are also present in this one.
     * 
     * @param sv
     *            A known-valid variant to validate against
     * @return False if this variant's tags are inconsistent with the supplied
     *         variant's
     */
    public boolean validateAgainst(SegmentVariant sv);

    /**
     * Get tags missing from this variant that are present in another one
     * (presumably an original made with {@link #createCopy()} before this one
     * was modified).
     * <p>
     * 
     * @param sv
     *            A known-valid variant to check tags against
     * @return List of tags
     */
    public List<CodeAtom> getMissingTags(SegmentVariant sv);

    @Override
    boolean equals(Object o);

    /**
     * Create an atom at the given offset that will track that location in the
     * text even as the content changes.
     */
    PositionAtom createPosition(int offset);

}
