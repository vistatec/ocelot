/*
 * Copyright (C) 2015, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.segment.model.okapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.vistatec.ocelot.segment.model.CodeAtom;
import com.vistatec.ocelot.segment.model.PositionAtom;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.segment.view.SegmentTextCell;
import com.vistatec.ocelot.segment.view.SegmentVariantSelection;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

public class TestTextContainerSegmentVariant {
    private TextContainerVariant tcv, plainTextTCV, plainCodeTCV;

    @Before
    public void beforeTest() {
        tcv = sampleText();
        plainTextTCV = plainText();
        plainCodeTCV = plainCode();
    }

    private TextContainerVariant sampleText() {
        TextContainer tc = new TextContainer();
        TextFragment tf = tc.getFirstContent();
        tf.append("A");
        tf.append(new Code(TagType.OPENING, "b", "<b id=\"1\">"));
        tf.append("B");
        tf.append(new Code(TagType.CLOSING, "b", "</b>"));
        
        return new TextContainerVariant(tc);
    }

    private TextContainerVariant plainText() {
        TextContainer tc = new TextContainer();
        TextFragment tf = tc.getFirstContent();
        tf.append("Plain text");
        return new TextContainerVariant(tc);
    }

    private TextContainerVariant plainCode() {
        TextContainer tc = new TextContainer();
        TextFragment tf = tc.getFirstContent();
        tf.append(new Code(TagType.OPENING, "b", "<b id=\"1\">"));
        tf.append(new Code(TagType.CLOSING, "b", "</b>"));
        return new TextContainerVariant(tc);
    }

    @Test
    public void testGetAtoms() {
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("0", "<b1>", "<b id=\"1\">"),
                                        new TextAtom("B"), new CodeAtom("1", "</b1>", "</b>")),
                tcv.getAtoms());
    }

    @Test
    public void testSetAtoms() {
        List<SegmentAtom> atoms = tcv.getAtoms();
        atoms.add(new TextAtom("X"));
        tcv.setAtoms(atoms);
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("0", "<b1>", "<b id=\"1\">"),
                new TextAtom("B"), new CodeAtom("1", "</b1>", "</b>"), new TextAtom("X")),
                tcv.getAtoms());
    }

    @Test
    public void testReplaceSelection() {
        // A<b1>B</b1>
        // replace <b1>B with B<b1>
        TextContainer tc = new TextContainer();
        TextFragment tf = tc.getFirstContent();
        tf.append("AB");
        tf.append(new Code(TagType.OPENING, "b", "\u202A<b id=\"1\">\u202C"));
        tf.append(new Code(TagType.CLOSING, "b", "\u202A</b>\u202C"));
        TextContainerVariant replacement = new TextContainerVariant(tc);

        // A   LRE   <   b   1   >   PDF   B   LRE   <   /   b   1   >   PDF
        // 0   1     2   3   4   5   6     7   8     9   10  11  12  13  14
        //      - - - 
        // A   B   LRE   <   b   1   >   PDF   LRE   <   /   b   1   >   PDF
        // 0   1   2     3   4   5   6   7     8     9   10  11  12  13  14
        tcv.replaceSelection(1, 8, new SegmentVariantSelection("0", replacement, 1, 8));
        assertEquals("AB<b1></b1>", noMark(tcv.getDisplayText()));
    }
    
    @Test
    public void testGetDisplayText() {
        assertEquals("A<b1>B</b1>", noMark(tcv.getDisplayText()));
    }

    @Test
    public void testGetStyleData() {
        List<String> expected = new ArrayList<>();
        expected.add("A");
        expected.add(SegmentTextCell.regularStyle);
        expected.add("\u202A<b1>\u202C");
        expected.add(SegmentTextCell.tagStyle);
        expected.add("B");
        expected.add(SegmentTextCell.regularStyle);
        expected.add("\u202A</b1>\u202C");
        expected.add(SegmentTextCell.tagStyle);
        assertEquals(expected, tcv.getStyleData(false));
    }

    @Test
    public void testGetStyleDataVerbose() {
        List<String> expected = new ArrayList<String>();
        expected.add("A");
        expected.add(SegmentTextCell.regularStyle);
        expected.add("\u202A<b id=\"1\">\u202C");
        expected.add(SegmentTextCell.tagStyle);
        expected.add("B");
        expected.add(SegmentTextCell.regularStyle);
        expected.add("\u202A</b>\u202C");
        expected.add(SegmentTextCell.tagStyle);
        assertEquals(expected, tcv.getStyleData(true));
    }

    @Test
    public void testCanInsertAt() {
        // A   LRE   <   b   1   >   PDF  B   LRE   <   /   b   1   >   PDF
        // 0   1     2   3   4   5   6    7   8     9   10  11  12  13  14
        // Y   Y     N   N   N   N   N    Y   Y     N   N   N   N   N   N
        assertTrue(tcv.canInsertAt(0));
        assertTrue(tcv.canInsertAt(1));
        assertFalse(tcv.canInsertAt(2));
        assertFalse(tcv.canInsertAt(3));
        assertFalse(tcv.canInsertAt(4));
        assertFalse(tcv.canInsertAt(5));
        assertFalse(tcv.canInsertAt(6));
        assertTrue(tcv.canInsertAt(7));
        assertTrue(tcv.canInsertAt(8));
        assertFalse(tcv.canInsertAt(9));
        assertFalse(tcv.canInsertAt(10));
        assertFalse(tcv.canInsertAt(11));
        assertFalse(tcv.canInsertAt(12));
        assertFalse(tcv.canInsertAt(13));
        assertFalse(tcv.canInsertAt(14));
    }

    @Test
    public void testContainsTag() {
        // A   LRE   <   b   1   >   PDF   B   PDF   <   /   b   1   >   PDF
        // 0   1     2   3   4   5   6     7   8     9   10 11   12  13  14
        assertFalse(tcv.containsTag(0, 1));
        assertTrue(tcv.containsTag(0, 2));
        assertTrue(tcv.containsTag(1, 2));
        assertTrue(tcv.containsTag(2, 2));
        assertTrue(tcv.containsTag(3, 2));
        assertTrue(tcv.containsTag(4, 1));
        assertTrue(tcv.containsTag(5, 1));
        assertTrue(tcv.containsTag(4, 2));
        assertTrue(tcv.containsTag(5, 2));
        assertTrue(tcv.containsTag(6, 2));
    }

    @Test
    public void testGetAtomAt() {
        // A   LRE   <   b   1   >   PDF   B   LRE   <   /   b   1   >   PDF
        // 0   1     2   3   4   5   6     7   8     9   10  11  12  13  14
        assertEquals("A", tcv.getAtomAt(0).getData());
        assertEquals("<b1>", noMark(tcv.getAtomAt(1).getData()));
        assertEquals("<b1>", noMark(tcv.getAtomAt(2).getData()));
        assertEquals("<b1>", noMark(tcv.getAtomAt(3).getData()));
        assertEquals("<b1>", noMark(tcv.getAtomAt(4).getData()));
        assertEquals("B", noMark(tcv.getAtomAt(7).getData()));
        assertEquals("</b1>", noMark(tcv.getAtomAt(9).getData()));
        assertEquals("</b1>", noMark(tcv.getAtomAt(10).getData()));
        assertEquals("</b1>", noMark(tcv.getAtomAt(11).getData()));
        assertEquals("</b1>", noMark(tcv.getAtomAt(12).getData()));
        assertEquals("</b1>", noMark(tcv.getAtomAt(13).getData()));
        assertNull(tcv.getAtomAt(15));
    }

    @Test
    public void testModifyChars() {
        // A  LRE   <   b   1   >   PDF   B   LRE   <   /   b   1   >   PDF
        // 0  1     2   3   4   5   6     7   8     9   10  11  12  13  14
        tcv.modifyChars(0, 0, "X");
        assertEquals("XA<b1>B</b1>", noMark(tcv.getDisplayText()));
        tcv = sampleText();
        tcv.modifyChars(1, 0, "X");
        assertEquals("AX<b1>B</b1>", noMark(tcv.getDisplayText()));
        tcv = sampleText();
        tcv.modifyChars(7, 0, "X");
        assertEquals("A<b1>XB</b1>", noMark(tcv.getDisplayText()));
        tcv.modifyChars(8, 0, "N");
        assertEquals("A<b1>XNB</b1>", noMark(tcv.getDisplayText()));
        tcv = sampleText();
        tcv.modifyChars(8, 0, "X");
        assertEquals("A<b1>BX</b1>", noMark(tcv.getDisplayText()));
        tcv = sampleText();
        tcv.modifyChars(0,  1, "X");
        assertEquals("X<b1>B</b1>", noMark(tcv.getDisplayText()));
        tcv = sampleText();
        tcv.modifyChars(7, 1, "X");
        assertEquals("A<b1>X</b1>", noMark(tcv.getDisplayText()));
        tcv = sampleText();
        tcv.modifyChars(0, 1, null); // delete
        assertEquals("<b1>B</b1>", noMark(tcv.getDisplayText()));
        tcv = sampleText();
        tcv.modifyChars(15, 0, "X");
        assertEquals("A<b1>B</b1>X", noMark(tcv.getDisplayText()));
    }

    @Test
    public void testInsertPlainCode() {
        plainCodeTCV.modifyChars(0, 0, "ABC");
        assertEquals("ABC<b1></b1>", noMark(plainCodeTCV.getDisplayText()));

        plainCodeTCV = plainCode();
        plainCodeTCV.modifyChars(13, 0, "ABC");
        assertEquals("<b1></b1>ABC", noMark(plainCodeTCV.getDisplayText()));

        plainCodeTCV = plainCode();
        plainCodeTCV.modifyChars(7, 0, "ABC");
        assertEquals("<b1>ABC</b1>", noMark(plainCodeTCV.getDisplayText()));

        // These test calls should never be called in real usage, as modifyChars
        // should not be called unless it has already been verified that you are
        // not modifying within a CodeAtom.
        plainCodeTCV = plainCode();
        plainCodeTCV.modifyChars(1, 0, "ABC");
        assertEquals("ABC<b1></b1>", noMark(plainCodeTCV.getDisplayText()));

        plainCodeTCV = plainCode();
        plainCodeTCV.modifyChars(2, 0, "ABC");
        assertEquals("ABC<b1></b1>", noMark(plainCodeTCV.getDisplayText()));

        plainCodeTCV = plainCode();
        plainCodeTCV.modifyChars(6, 0, "ABC");
        assertEquals("<b1>ABC</b1>", noMark(plainCodeTCV.getDisplayText()));

        plainCodeTCV = plainCode();
        plainCodeTCV.modifyChars(7, 0, "ABC");
        assertEquals("<b1>ABC</b1>", noMark(plainCodeTCV.getDisplayText()));

        plainCodeTCV = plainCode();
        plainCodeTCV.modifyChars(6, 0, "ABC");
        assertEquals("<b1>ABC</b1>", noMark(plainCodeTCV.getDisplayText()));
    }

    @Test
    public void testRemoveCharsFromPlainText() {
        plainTextTCV.modifyChars(5, 5, null);
        assertEquals("Plain", noMark(plainTextTCV.getDisplayText()));

        plainTextTCV = plainText();
        plainTextTCV.modifyChars(5, 1, null);
        assertEquals("Plaintext", noMark(plainTextTCV.getDisplayText()));

        plainTextTCV = plainText();
        plainTextTCV.modifyChars(0, 6, null);
        assertEquals("text", noMark(plainTextTCV.getDisplayText()));
    }

    @Test
    public void testDoesThisCrash() {
        List<String> l = Lists.newArrayList("A", "B", "C");
        assertEquals(Lists.newArrayList("C"), l.subList(2, 3));
        assertEquals(Lists.newArrayList(), l.subList(3, 3));
    }

    @Test
    public void testReplaceWithAtoms() {
        // A   LRE   <   b   1   >   PDF   B   LRE   <   /   b   1   >   PDF
        // 0   1     2   3   4   5   6     7   8     9   10  11  12  13  14

        // Insert with zero-length range.
        tcv.replaceSelection(0, 0, Arrays.asList(new TextAtom("C"), new TextAtom("D")));
        assertEquals("CDA<b1>B</b1>", noMark(tcv.toString()));
        tcv.replaceSelection(0, 3, Arrays.asList(new TextAtom("E")));
        assertEquals("E<b1>B</b1>", noMark(tcv.toString()));

        // Range must include entire CodeAtom in order to replace it.
        tcv.replaceSelection(0, 2, Arrays.asList(new TextAtom("F")));
        assertEquals("F<b1>B</b1>", noMark(tcv.toString()));
        tcv.replaceSelection(0, 3, Arrays.asList(new TextAtom("F")));
        assertEquals("F<b1>B</b1>", noMark(tcv.toString()));
        tcv.replaceSelection(0, 4, Arrays.asList(new TextAtom("F")));
        assertEquals("F<b1>B</b1>", noMark(tcv.toString()));
        tcv.replaceSelection(0, 7, Arrays.asList(new TextAtom("F")));
        assertEquals("FB</b1>", noMark(tcv.toString()));

        // Replace with empty list to clear.
        tcv.replaceSelection(0, 3, Collections.<SegmentAtom> emptyList());
        assertEquals("</b1>", noMark(tcv.toString()));
    }

    @Test
    public void testSelection() {
        // A   LRE   <   b   1   >   PDF   B   LRE   <   /   b   1   >   PDF
        // 0   1     2   3   4   5   6     7   8     9   10  11  12  13  14

        // SegmentVariantSelection represents a selection of the textual
        // representation, not the atomic representation.
        SegmentVariantSelection sel = new SegmentVariantSelection("", tcv, 0, 1);
        assertEquals("A", sel.getDisplayText());
        sel = new SegmentVariantSelection("", tcv, 0, 3);
        assertEquals("A<", noMark(sel.getDisplayText()));
        sel = new SegmentVariantSelection("", tcv, 0, 4);
        assertEquals("A<b", noMark(sel.getDisplayText()));
        sel = new SegmentVariantSelection("", tcv, 0, 5);
        assertEquals("A<b1", noMark(sel.getDisplayText()));
        sel = new SegmentVariantSelection("", tcv, 0, 6);
        assertEquals("A<b1>", noMark(sel.getDisplayText()));

        // Selection refers to source SegmentVariant, will become invalid if
        // source is mutated.
        tcv.clearSelection(0, tcv.getLength());
        try {
            assertEquals("A<b1>", sel.getDisplayText());
            fail("Selection should no longer be valid");
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
    }

    @Test
    public void testReplaceWithSelection() {
        // A   LRE   <   b   1   >   PDF   B   LRE   <   /   b   1   >   PDF
        // 0   1     2   3   4   5   6     7   8     9   10  11  12  13  14

        SegmentVariantSelection sel = new SegmentVariantSelection("", tcv.createCopy(), 1, 8);
        assertEquals("<b1>B", noMark(sel.getDisplayText()));

        tcv.replaceSelection(0, 0, sel);
        assertEquals("<b1>BA<b1>B</b1>", noMark(tcv.toString()));
        tcv.replaceSelection(tcv.getLength(), tcv.getLength(), sel);
        assertEquals("<b1>BA<b1>B</b1><b1>B", noMark(tcv.toString()));

        tcv.replaceSelection(0, 15, sel);
        assertEquals("<b1>B</b1><b1>B", noMark(tcv.toString()));
    }

    @Test
    public void testClearSelection() {
        // A < b 1 > B < / b 1 >
        // 0 1 2 3 4 5 6 7 8 9 10

        tcv.clearSelection(0, 0);
        assertEquals("A<b1>B</b1>", noMark(tcv.getDisplayText()));

        tcv.clearSelection(0, 1);
        assertEquals("<b1>B</b1>", noMark(tcv.getDisplayText()));

        // Range must cover entire CodeAtom to remove it.
        tcv.clearSelection(0, 1);
        assertEquals("<b1>B</b1>", noMark(tcv.getDisplayText()));
        tcv.clearSelection(0, 2);
        assertEquals("<b1>B</b1>", noMark(tcv.getDisplayText()));
        tcv.clearSelection(0, 3);
        assertEquals("<b1>B</b1>", noMark(tcv.getDisplayText()));
        tcv.clearSelection(0, 6);
        assertEquals("B</b1>", noMark(tcv.getDisplayText()));
    }

    @Test
    public void testValidation() {
        // A   LRE   <   b   1   >   PDF   B   LRE   <   /   b   1   >   PDF
        // 0   1     2   3   4   5   6     7   8     9   10  11  12  13  14

        TextContainerVariant copy = tcv.createCopy();
        assertFalse(tcv.needsValidation());

        // Replacing empty selection does not dirty the variant.
        tcv.replaceSelection(0, 0, Collections.<SegmentAtom> emptyList());
        assertFalse(tcv.needsValidation());

        // Replacing atoms dirties the variant...
        tcv.replaceSelection(0, 1, Collections.<SegmentAtom> emptyList());
        assertTrue(tcv.needsValidation());
        // ...but tags are still OK here.
        assertTrue(tcv.validateAgainst(copy));
        // ...but the variant is still considered dirty because it's up to the
        // caller to provide the correct original to validate against.
        assertTrue(tcv.needsValidation());

        tcv = copy.createCopy();
        assertFalse(tcv.needsValidation());

        // Removing a tag will make the variant fail validation.
        tcv.replaceSelection(0, 7, Arrays.asList(new TextAtom("Z")));
        assertTrue(tcv.needsValidation());
        assertFalse(tcv.validateAgainst(copy));

        // Restore the missing tag to get it to validate.
        // Note that validation does not handle ordering or nesting issues; it
        // only cares that the codes in the reference variant are present in the
        // same number.
        List<CodeAtom> missing = tcv.getMissingTags(copy);
        assertEquals(1, missing.size());
        assertEquals("<b1>", noMark(missing.get(0).getData()));
        tcv.replaceSelection(tcv.getLength(), tcv.getLength(), missing);
        assertTrue(tcv.needsValidation());
        assertTrue(tcv.validateAgainst(copy));
    }

    private String noMark(String s) {
        return s.replaceAll("[\u202A\u202C]", "");
    }

    @Test
    public void testPositions() {
        // A  LRE  <  b  1  >  PDF  B  LRE  <  /  b  1  >  PDF
        // 0  1    2  3  4  5  6    7  8    9  10 11 12 13 14

        PositionAtom start = tcv.createPosition(0);
        assertTrue(start.getData().isEmpty());
        assertEquals(0, start.getLength());
        assertEquals(0, start.getPosition());
        PositionAtom end = tcv.createPosition(8);
        assertEquals(8, end.getPosition());

        tcv.modifyChars(0, 0, "Z");
        assertEquals("ZA<b1>B</b1>", noMark(tcv.getDisplayText().replaceAll("[\u202A\u202C]", "")));
        assertEquals(0, start.getPosition());
        assertEquals(9, end.getPosition());

        tcv.replaceSelection(start.getPosition(), end.getPosition(), Arrays.asList(new TextAtom("ABCD")));
        assertEquals("ABCD</b1>", noMark(tcv.getDisplayText().replaceAll("[\u202A\u202C]", "")));
        assertEquals(0, start.getPosition());
        assertEquals(4, end.getPosition());

        PositionAtom middle = tcv.createPosition(2);
        tcv.clearSelection(start.getPosition(), end.getPosition());
        assertEquals("</b1>", noMark(tcv.getDisplayText().replaceAll("[\u202A\u202C]", "")));
        try {
            middle.getPosition();
            fail("Positions removed from parent segment should throw an exception");
        } catch (IllegalStateException e) {
            // OK
        }
        assertEquals(0, start.getPosition());
        assertEquals(0, end.getPosition());

        // Attempting to place a position inside a code atom will result in the
        // position sliding to the end of the code.
        PositionAtom inCode = tcv.createPosition(2);
        assertEquals(7, inCode.getPosition());
    }

    @Test
    public void testCopy() {
        // A < b 1 > B < / b 1 >
        // 0 1 2 3 4 5 6 7 8 9 10

        TextContainerVariant copy = tcv.createCopy();

        // We *currently* do not provide our own notion of equality other than
        // identity.
        assertNotEquals(tcv, copy);

        // We *currently* only care that the serialized representation is
        // correct
        assertEquals(noMark(tcv.getDisplayText()), noMark(copy.getDisplayText()));

        // Even the underlying TextContainer does not have its own notion of
        // equality separate from identity. Note that some assumptions in our
        // code might have to change if this situation ever changes.
        assertNotEquals(tcv.getTextContainer(), copy.getTextContainer());

        // The number of atoms should be the same as long as the original did
        // not have PositionAtoms.
        assertEquals(tcv.getAtoms().size(), copy.getAtoms().size());

        // The atoms themselves are the same in terms of the equals() methods on
        // the Ocelot base classes. This ignores the fact that the underlying
        // Okapi Codes do not have a notion of equality. TODO: Is this OK?
        assertEquals(tcv.getAtoms(), copy.getAtoms());

        SegmentVariantSelection sel = new SegmentVariantSelection("", tcv.createCopy(), 1, 5);
        tcv.replaceSelection(tcv.getLength(), tcv.getLength(), sel);
        assertEquals("A<b1>B</b1><b1>", noMark(tcv.getDisplayText()));

        // Copy should not be affected by change to original.
        assertNotEquals(noMark(tcv.getDisplayText()), copy.getDisplayText());

        tcv.createPosition(1);

        TextContainerVariant newCopy = tcv.createCopy();

        // Copy should not retain the PositionAtom in the original.
        assertNotEquals(tcv.getAtoms().size(), newCopy.getAtoms().size());
        for (SegmentAtom atom : newCopy.getAtoms()) {
            if (atom instanceof PositionAtom) {
                fail("Copy should not contain PositionAtoms");
            }
        }
    }
}
