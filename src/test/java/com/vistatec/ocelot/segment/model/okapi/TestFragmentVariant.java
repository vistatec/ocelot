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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import com.vistatec.ocelot.segment.view.SegmentVariantSelection;

import net.sf.okapi.lib.xliff2.core.Directionality;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.IWithStore;
import net.sf.okapi.lib.xliff2.core.Segment;
import net.sf.okapi.lib.xliff2.core.Store;
import net.sf.okapi.lib.xliff2.core.TagType;

public class TestFragmentVariant {
    private FragmentVariant sampleFV, plainTextFV, plainCodeFV;

    @Before
    public void beforeTest() {
        sampleFV = sampleText(false);
        plainTextFV = plainText();
        plainCodeFV = plainCode();
    }

    @Test
    public void testPlainTextAtoms() {
        assertEquals(Lists.newArrayList(new TextAtom("Plain Text")), plainTextFV.getAtoms());
    }

    @Test
    public void testPlainCodeAtoms() {
        assertEquals(Lists.newArrayList(new CodeAtom("id1", "<pcid1>", "<pc id=\"id1\">"),
                                        new CodeAtom("id1", "</pcid1>", "</pc>")),
            plainCodeFV.getAtoms());
    }

    @Test
    public void testGetAtoms() {
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("id1", "<pcid1>", "<pc id=\"id1\">"),
                                        new TextAtom("B"), new CodeAtom("id1", "</pcid1>", "</pc>")),
            sampleFV.getAtoms());
    }

    @Test
    public void testGetAtomsForTarget() {
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("id1", "<pcid1>", "<pc id=\"id1\">"),
                                        new TextAtom("B"), new CodeAtom("id1", "</pcid1>", "</pc>")),
            sampleText(true).getAtoms());
    }

    @Test
    public void testCreateEmptyTarget() {
        FragmentVariant fv = plainCodeFV.createEmptyTarget();
        assertEquals(Collections.emptyList(), fv.getAtoms());
        assertEquals(true, fv.isTarget());
    }

    @Test
    public void testNullXLIFFTarget() {
        Store store = new Store(new DummyWithStore());
        Segment segment = new Segment(store);
        segment.setSource(new Fragment(store, false).append("Hello world"));
        FragmentVariant v = new FragmentVariant(segment, true);
        assertEquals("", v.getDisplayText());
        assertNotNull(segment.getTarget());
    }

    @Test
    public void testCreateCopy() {
        FragmentVariant copy = sampleFV.createCopy();
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("id1", "<pcid1>", "<pc id=\"id1\">"),
                new TextAtom("B"), new CodeAtom("id1", "</pcid1>", "</pc>")),
                copy.getAtoms());
    }
    
    @Test
    public void testReplaceWithAtoms() {
        // A < p c i d 1 > B <  /  p  c  i  d  1  >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        // Insert with zero-length range.
        sampleFV.replaceSelection(0, 0, Arrays.asList(new TextAtom("C"), new TextAtom("D")));
        assertEquals("CDA<pcid1>B</pcid1>", sampleFV.toString());
        sampleFV.replaceSelection(0, 3, Arrays.asList(new TextAtom("E")));
        assertEquals("E<pcid1>B</pcid1>", sampleFV.toString());

        // Range must include entire CodeAtom in order to replace it.
        sampleFV.replaceSelection(0, 2, Arrays.asList(new TextAtom("F")));
        assertEquals("F<pcid1>B</pcid1>", sampleFV.toString());
        sampleFV.replaceSelection(0, 3, Arrays.asList(new TextAtom("F")));
        assertEquals("F<pcid1>B</pcid1>", sampleFV.toString());
        sampleFV.replaceSelection(0, 4, Arrays.asList(new TextAtom("F")));
        assertEquals("F<pcid1>B</pcid1>", sampleFV.toString());
        sampleFV.replaceSelection(0, 5, Arrays.asList(new TextAtom("F")));
        assertEquals("F<pcid1>B</pcid1>", sampleFV.toString());
        sampleFV.replaceSelection(0, 6, Arrays.asList(new TextAtom("F")));
        assertEquals("F<pcid1>B</pcid1>", sampleFV.toString());
        sampleFV.replaceSelection(0, 7, Arrays.asList(new TextAtom("F")));
        assertEquals("F<pcid1>B</pcid1>", sampleFV.toString());
        sampleFV.replaceSelection(0, 8, Arrays.asList(new TextAtom("F")));
        assertEquals("FB</pcid1>", sampleFV.toString());

        // Replace with empty list to clear.
        sampleFV.replaceSelection(0, 3, Collections.<SegmentAtom> emptyList());
        assertEquals("</pcid1>", sampleFV.toString());
    }

    @Test
    public void testSelection() {
        // A < p c i d 1 > B <  /  p  c  i  d  1  >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        // SegmentVariantSelection represents a selection of the textual
        // representation, not the atomic representation.
        SegmentVariantSelection sel = new SegmentVariantSelection("", sampleFV, 0, 1);
        assertEquals("A", sel.getDisplayText());
        sel = new SegmentVariantSelection("", sampleFV, 0, 2);
        assertEquals("A<", sel.getDisplayText());
        sel = new SegmentVariantSelection("", sampleFV, 0, 3);
        assertEquals("A<p", sel.getDisplayText());
        sel = new SegmentVariantSelection("", sampleFV, 0, 4);
        assertEquals("A<pc", sel.getDisplayText());
        sel = new SegmentVariantSelection("", sampleFV, 0, 5);
        assertEquals("A<pci", sel.getDisplayText());

        // Selection refers to source SegmentVariant, will become invalid if
        // source is mutated.
        sampleFV.clearSelection(0, sampleFV.getLength());
        try {
            assertEquals("A<pci", sel.getDisplayText());
            fail("Selection should no longer be valid");
        } catch (IndexOutOfBoundsException e) {
            // OK
        }
    }

    @Test
    public void testReplaceWithSelection() {
        // A < p c i d 1 > B <  /  p  c  i  d  1  >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        SegmentVariantSelection sel = new SegmentVariantSelection("", sampleFV.createCopy(), 1, 9);
        assertEquals("<pcid1>B", sel.getDisplayText());

        sampleFV.replaceSelection(0, 0, sel);
        assertEquals("<pcid1>BA<pcid1>B</pcid1>", sampleFV.toString());
        sampleFV.replaceSelection(sampleFV.getLength(), sampleFV.getLength(), sel);
        assertEquals("<pcid1>BA<pcid1>B</pcid1><pcid1>B", sampleFV.toString());

        sampleFV.replaceSelection(0, 17, sel);
        assertEquals("<pcid1>B</pcid1><pcid1>B", sampleFV.toString());
    }

    @Test
    public void testClearSelection() {
        // A < p c i d 1 > B <  /  p  c  i  d  1  >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        sampleFV.clearSelection(0, 0);
        assertEquals("A<pcid1>B</pcid1>", sampleFV.getDisplayText());

        sampleFV.clearSelection(0, 1);
        assertEquals("<pcid1>B</pcid1>", sampleFV.getDisplayText());

        // Range must cover entire CodeAtom to remove it.
        sampleFV.clearSelection(0, 1);
        assertEquals("<pcid1>B</pcid1>", sampleFV.getDisplayText());
        sampleFV.clearSelection(0, 2);
        assertEquals("<pcid1>B</pcid1>", sampleFV.getDisplayText());
        sampleFV.clearSelection(0, 3);
        assertEquals("<pcid1>B</pcid1>", sampleFV.getDisplayText());
        sampleFV.clearSelection(0, 4);
        assertEquals("<pcid1>B</pcid1>", sampleFV.getDisplayText());
        sampleFV.clearSelection(0, 5);
        assertEquals("<pcid1>B</pcid1>", sampleFV.getDisplayText());
        sampleFV.clearSelection(0, 6);
        assertEquals("<pcid1>B</pcid1>", sampleFV.getDisplayText());
        sampleFV.clearSelection(0, 7);
        assertEquals("B</pcid1>", sampleFV.getDisplayText());
    }

    @Test
    public void testValidation() {
        // A < p c i d 1 > B <  /  p  c  i  d  1  >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        FragmentVariant copy = sampleFV.createCopy();
        assertFalse(sampleFV.needsValidation());

        // Replacing empty selection does not dirty the variant.
        sampleFV.replaceSelection(0, 0, Collections.<SegmentAtom> emptyList());
        assertFalse(sampleFV.needsValidation());

        // Replacing atoms dirties the variant...
        sampleFV.replaceSelection(0, 1, Collections.<SegmentAtom> emptyList());
        assertTrue(sampleFV.needsValidation());
        // ...but tags are still OK here.
        assertTrue(sampleFV.validateAgainst(copy));
        // ...but the variant is still considered dirty because it's up to the
        // caller to provide the correct original to validate against.
        assertTrue(sampleFV.needsValidation());

        sampleFV = copy.createCopy();
        assertFalse(sampleFV.needsValidation());

        // Removing a tag will make the variant fail validation.
        sampleFV.replaceSelection(0, 8, Arrays.asList(new TextAtom("Z")));
        assertTrue(sampleFV.needsValidation());
        assertFalse(sampleFV.validateAgainst(copy));

        // Restore the missing tag to get it to validate.
        // Note that validation does not handle ordering or nesting issues; it
        // only cares that the codes in the reference variant are present in the
        // same number.
        List<CodeAtom> missing = sampleFV.getMissingTags(copy);
        assertEquals(1, missing.size());
        assertEquals("<pcid1>", missing.get(0).getData());
        sampleFV.replaceSelection(sampleFV.getLength(), sampleFV.getLength(), missing);
        assertTrue(sampleFV.needsValidation());
        assertTrue(sampleFV.validateAgainst(copy));
    }

    @Test
    public void testPositions() {
        // A < p c i d 1 > B <  /  p  c  i  d  1  >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        PositionAtom start = sampleFV.createPosition(0);
        assertTrue(start.getData().isEmpty());
        assertEquals(0, start.getLength());
        assertEquals(0, start.getPosition());
        PositionAtom end = sampleFV.createPosition(9);
        assertEquals(9, end.getPosition());

        sampleFV.modifyChars(0, 0, "Z");
        assertEquals("ZA<pcid1>B</pcid1>", sampleFV.getDisplayText());
        assertEquals(0, start.getPosition());
        assertEquals(10, end.getPosition());

        sampleFV.replaceSelection(start.getPosition(), end.getPosition(), Arrays.asList(new TextAtom("ABCD")));
        assertEquals("ABCD</pcid1>", sampleFV.getDisplayText());
        assertEquals(0, start.getPosition());
        assertEquals(4, end.getPosition());

        PositionAtom middle = sampleFV.createPosition(2);
        sampleFV.clearSelection(start.getPosition(), end.getPosition());
        assertEquals("</pcid1>", sampleFV.getDisplayText());
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
        PositionAtom inCode = sampleFV.createPosition(2);
        assertEquals(8, inCode.getPosition());
    }

    @Test
    public void testCopy() {
        // A < p c i d 1 > B <  /  p  c  i  d  1  >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        FragmentVariant copy = sampleFV.createCopy();

        // We *currently* do not provide our own notion of equality other than
        // identity.
        assertNotEquals(sampleFV, copy);

        // We *currently* only care that the serialized representation is
        // correct
        assertEquals(sampleFV.getDisplayText(), copy.getDisplayText());

        // Okapi Fragments have a concept of equality that is preserved through
        // copying. This differs from TextContainerVariant.
        assertEquals(sampleFV.getUpdatedOkapiFragment(newFragment()), copy.getUpdatedOkapiFragment(newFragment()));

        // The number of atoms should be the same as long as the original did
        // not have PositionAtoms.
        assertEquals(sampleFV.getAtoms().size(), copy.getAtoms().size());

        // The atoms themselves are the same in terms of the equals() methods on
        // the Ocelot base classes. This ignores the fact that the underlying
        // Okapi Codes do not have a notion of equality. TODO: Is this OK?
        assertEquals(sampleFV.getAtoms(), copy.getAtoms());

        SegmentVariantSelection sel = new SegmentVariantSelection("", sampleFV.createCopy(), 1, 5);
        sampleFV.replaceSelection(sampleFV.getLength(), sampleFV.getLength(), sel);
        assertEquals("A<pcid1>B</pcid1><pcid1>", sampleFV.getDisplayText());

        // Copy should not be affected by change to original.
        assertNotEquals(sampleFV.getDisplayText(), copy.getDisplayText());

        sampleFV.createPosition(1);

        FragmentVariant newCopy = sampleFV.createCopy();

        // Copy should not retain the PositionAtom in the original.
        assertNotEquals(sampleFV.getAtoms().size(), newCopy.getAtoms().size());
        for (SegmentAtom atom : newCopy.getAtoms()) {
            if (atom instanceof PositionAtom) {
                fail("Copy should not contain PositionAtoms");
            }
        }
    }

    private FragmentVariant sampleText(boolean isTarget) {
        Store store = new Store(new DummyWithStore());
        Segment segment = new Segment(store);
        Fragment fragment = new Fragment(store, isTarget);
        fragment.append("A");
        fragment.append(TagType.OPENING, "id1", "<b>", false);
        fragment.append("B");
        fragment.append(TagType.CLOSING, "id1", "</b>", false);
        if (isTarget) {
            segment.setTarget(fragment);
        }
        else {
            segment.setSource(fragment);
        }
        return new FragmentVariant(segment, fragment.isTarget());
    }

    private FragmentVariant plainText() {
        Store store = new Store(new DummyWithStore());
        Segment segment = new Segment(store);
        segment.setSource("Plain Text");
        return new FragmentVariant(segment, false);
    }

    private FragmentVariant plainCode() {
        Store store = new Store(new DummyWithStore());
        Segment segment = new Segment(store);
        Fragment fragment = new Fragment(store, false);
        fragment.append(TagType.OPENING, "id1", "<b>", false);
        fragment.append(TagType.CLOSING, "id1", "</b>", false);
        segment.setSource(fragment);
        return new FragmentVariant(segment, false);
    }
    
    private Fragment newFragment() {
        Store store = new Store(new DummyWithStore());
        return new Fragment(store, false);
    }

    class DummyWithStore implements IWithStore {
        @Override
        public Directionality getSourceDir() {
            return Directionality.AUTO;
        }

        @Override
        public Directionality getTargetDir() {
            return Directionality.AUTO;
        }

        @Override
        public boolean isIdUsed(String id) {
            return false;
        }

        @Override
        public void setSourceDir(Directionality arg0) {
        }

        @Override
        public void setTargetDir(Directionality arg0) {
        }
        
    }
}
