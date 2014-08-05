package com.vistatec.ocelot.segment;

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
                new CodeAtom(1, "<b>", "<b>"),
                new TextAtom("B"),
                new CodeAtom(2, "</b>", "</b>")
        );

        simpleSv = new SimpleSegmentVariant(atoms);

        complexSv = new SimpleSegmentVariant(Lists.newArrayList(
                new TextAtom("ABC"),
                new CodeAtom(1, "<b>", "<b>"),
                new TextAtom("DEF"),
                new CodeAtom(1, "</b>", "</b>")
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
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom(1, "<b>", "<b>")),
                simpleSv.getAtomsForRange(0,  2));
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom(1, "<b>", "<b>")),
                simpleSv.getAtomsForRange(0,  3));
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom(1, "<b>", "<b>")),
                simpleSv.getAtomsForRange(0,  4));
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom(1, "<b>", "<b>"), new TextAtom("B")),
                simpleSv.getAtomsForRange(0,  5));
        assertEquals(Lists.newArrayList(new CodeAtom(1, "<b>", "<b>"), new TextAtom("B")),
                simpleSv.getAtomsForRange(1, 4));
        assertEquals(Lists.newArrayList(new CodeAtom(1, "<b>", "<b>"), new TextAtom("B")),
                simpleSv.getAtomsForRange(2, 3));
        assertEquals(Lists.newArrayList(new CodeAtom(1, "<b>", "<b>"), new TextAtom("B")),
                simpleSv.getAtomsForRange(3, 2));
        assertEquals(Lists.newArrayList(new TextAtom("A"), new CodeAtom(1, "<b>", "<b>"), 
                                        new TextAtom("B"), new CodeAtom(2, "</b>", "</b>")),
                simpleSv.getAtomsForRange(0, 8));

        // A B C < b > D E F < / b >
        // 0 1 2 3 4 5 6 7 8 9 0 1 2
        assertEquals(Lists.newArrayList(new TextAtom("C"), new CodeAtom(1, "<b>", "<b>")),
                complexSv.getAtomsForRange(2, 4));
        assertEquals(Lists.newArrayList(new TextAtom("C"), new CodeAtom(1, "<b>", "<b>"), new TextAtom("D")),
                complexSv.getAtomsForRange(2, 5));
        assertEquals(Lists.newArrayList(new CodeAtom(1, "<b>", "<b>"), new TextAtom("D")),
                complexSv.getAtomsForRange(3, 4));
        assertEquals(Lists.newArrayList(new CodeAtom(1, "<b>", "<b>"), new TextAtom("DE")),
                complexSv.getAtomsForRange(3, 5));
    }
}
