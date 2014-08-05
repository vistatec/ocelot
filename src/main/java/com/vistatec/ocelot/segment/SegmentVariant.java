package com.vistatec.ocelot.segment;

import java.util.List;

/**
 * Abstract representation of a segment variant (eg, the source
 * or target).
 */
public interface SegmentVariant {

    /**
     * Create a new (empty) variant of this type.
     * @return
     */
    SegmentVariant createEmpty();

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
     * another segment variant.  (This method is currently unused and is
     * intended as support for copy/paste.)
     *
     * @param selectionStart start of the selection to be replaced
     * @param selectionEnd end of the selection to be replaced
     * @param rsv content with which to replace the current selection
     */
    public void replaceSelection(int selectionStart, int selectionEnd,
            SegmentVariantSelection rsv);

    @Override
    boolean equals(Object o);

}
