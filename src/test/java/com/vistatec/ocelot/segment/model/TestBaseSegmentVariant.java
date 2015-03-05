package com.vistatec.ocelot.segment.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.google.common.collect.Lists;

public class TestBaseSegmentVariant {
    SimpleSegmentVariant simpleSv, complexSv;

    @Before
    public void setup() {
        List<SegmentAtom> atoms = Lists.newArrayList(
                new TextAtom("A"),
                new CodeAtom("1", "<b>", "<b>"),
                new TextAtom("B"),
                new CodeAtom("2", "</b>", "</b>")
        );

        simpleSv = new SimpleSegmentVariant(atoms);

        complexSv = new SimpleSegmentVariant(Lists.newArrayList(
                new TextAtom("ABC"),
                new CodeAtom("1", "<b>", "<b>"),
                new TextAtom("DEF"),
                new CodeAtom("1", "</b>", "</b>")
        ));
    }

    @Test
    public void testGetAtomsForRange() {
        // A < b > B < / B >
        // 0 1 2 3 4 5 6 7 8
        assertEquals((List<SegmentAtom>)new ArrayList<SegmentAtom>(),
                     simpleSv.getAtomsForRange(0, 0));
        assertEquals(Lists.newArrayList(new TextAtom("A")),
                simpleSv.getAtomsForRange(0, 1));
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("1", "<b>", "<b>")),
                simpleSv.getAtomsForRange(0,  2));
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("1", "<b>", "<b>")),
                simpleSv.getAtomsForRange(0,  3));
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("1", "<b>", "<b>")),
                simpleSv.getAtomsForRange(0,  4));
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("1", "<b>", "<b>"), new TextAtom("B")),
                simpleSv.getAtomsForRange(0,  5));
        assertEquals(Lists.newArrayList(new CodeAtom("1", "<b>", "<b>"), new TextAtom("B")),
                simpleSv.getAtomsForRange(1, 4));
        assertEquals(Lists.newArrayList(new CodeAtom("1", "<b>", "<b>"), new TextAtom("B")),
                simpleSv.getAtomsForRange(2, 3));
        assertEquals(Lists.newArrayList(new CodeAtom("1", "<b>", "<b>"), new TextAtom("B")),
                simpleSv.getAtomsForRange(3, 2));
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom("1", "<b>", "<b>"), 
                                        new TextAtom("B"), new CodeAtom("2", "</b>", "</b>")),
                simpleSv.getAtomsForRange(0, 8));

        // A B C < b > D E F < / b >
        // 0 1 2 3 4 5 6 7 8 9 0 1 2
        assertEquals(Lists.newArrayList(new TextAtom("C"), new CodeAtom("1", "<b>", "<b>")),
                complexSv.getAtomsForRange(2, 4));
        assertEquals(Lists.newArrayList(new TextAtom("C"), new CodeAtom("1", "<b>", "<b>"), new TextAtom("D")),
                complexSv.getAtomsForRange(2, 5));
        assertEquals(Lists.newArrayList(new CodeAtom("1", "<b>", "<b>"), new TextAtom("D")),
                complexSv.getAtomsForRange(3, 4));
        assertEquals(Lists.newArrayList(new CodeAtom("1", "<b>", "<b>"), new TextAtom("DE")),
                complexSv.getAtomsForRange(3, 5));
    }

    @Test
    public void testFindSelectionStart() {
        // A < b > B < / B >
        // 0 1 2 3 4 5 6 7 8
        assertEquals(0, simpleSv.findSelectionStart(0)); // unchanged
        assertEquals(1, simpleSv.findSelectionStart(1)); // unchanged
        assertEquals(1, simpleSv.findSelectionStart(2));
        assertEquals(1, simpleSv.findSelectionStart(3));
        assertEquals(4, simpleSv.findSelectionStart(4)); // unchanged
        assertEquals(5, simpleSv.findSelectionStart(5)); // unchanged
        assertEquals(5, simpleSv.findSelectionStart(6));
        assertEquals(5, simpleSv.findSelectionStart(7));
        assertEquals(5, simpleSv.findSelectionStart(8));
    }

    @Test
    public void testFindSelectionEnd() {
        // A < b > B < / B >
        // 0 1 2 3 4 5 6 7 8
        assertEquals(0, simpleSv.findSelectionEnd(0)); // unchanged
        assertEquals(1, simpleSv.findSelectionEnd(1));
        assertEquals(4, simpleSv.findSelectionEnd(2));
        assertEquals(4, simpleSv.findSelectionEnd(3));
        assertEquals(4, simpleSv.findSelectionEnd(4));
        assertEquals(5, simpleSv.findSelectionEnd(5));
        assertEquals(9, simpleSv.findSelectionEnd(6));
        assertEquals(9, simpleSv.findSelectionEnd(7));
        assertEquals(9, simpleSv.findSelectionEnd(8));
    }
}
