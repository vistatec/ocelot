package com.vistatec.ocelot.segment.okapi;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;

import org.junit.*;

import com.vistatec.ocelot.segment.SegmentTextCell;

import static org.junit.Assert.*;

public class TestTextContainerSegmentVariant {
    private TextContainerVariant tcv;

    @Before
    public void beforeTest() {
        tcv = sampleText();
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

    @Test
    public void testGetDisplayText() {
        assertEquals("A<b>B</b>", tcv.getDisplayText());
    }

    @Test
    public void testGetStyleData() {
        List<String> expected = new ArrayList<String>();
        expected.add("A");
        expected.add(SegmentTextCell.regularStyle);
        expected.add("<b>");
        expected.add(SegmentTextCell.tagStyle);
        expected.add("B");
        expected.add(SegmentTextCell.regularStyle);
        expected.add("</b>");
        expected.add(SegmentTextCell.tagStyle);
        assertEquals(expected, tcv.getStyleData(false));
    }

    @Test
    public void testGetStyleDataVerbose() {
        List<String> expected = new ArrayList<String>();
        expected.add("A");
        expected.add(SegmentTextCell.regularStyle);
        expected.add("<b id=\"1\">");
        expected.add(SegmentTextCell.tagStyle);
        expected.add("B");
        expected.add(SegmentTextCell.regularStyle);
        expected.add("</b>");
        expected.add(SegmentTextCell.tagStyle);
        assertEquals(expected, tcv.getStyleData(true));
    }

    @Test
    public void testCanInsertAt() {
        // A < b > B < / b >
        // 0 1 2 3 4 5 6 7 8
        // Y Y N N Y Y N N N
        assertTrue(tcv.canInsertAt(0));
        assertTrue(tcv.canInsertAt(1));
        assertFalse(tcv.canInsertAt(2));
        assertFalse(tcv.canInsertAt(3));
        assertTrue(tcv.canInsertAt(4));
        assertTrue(tcv.canInsertAt(5));
        assertFalse(tcv.canInsertAt(6));
        assertFalse(tcv.canInsertAt(7));
        assertFalse(tcv.canInsertAt(8));
    }

    @Test
    public void testContainsTag() {
        // A < b > B < / b >
        // 0 1 2 3 4 5 6 7 8
        assertFalse(tcv.containsTag(0, 1));
        assertTrue(tcv.containsTag(0, 2));
        assertTrue(tcv.containsTag(1, 2));
        assertTrue(tcv.containsTag(2, 2));
        assertTrue(tcv.containsTag(3, 2));
        assertFalse(tcv.containsTag(4, 1));
        assertTrue(tcv.containsTag(4, 2));
        assertTrue(tcv.containsTag(5, 2));
        assertTrue(tcv.containsTag(6, 2));
    }

    @Test
    public void testModifyChars() {
        // A < b > B < / b >
        // 0 1 2 3 4 5 6 7 8
        tcv.modifyChars(0, 0, "X");
        assertEquals("XA<b>B</b>", tcv.getDisplayText());
        tcv = sampleText();
        tcv.modifyChars(1, 0, "X");
        assertEquals("AX<b>B</b>", tcv.getDisplayText());
        tcv = sampleText();
        tcv.modifyChars(4, 0, "X");
        assertEquals("A<b>XB</b>", tcv.getDisplayText());
        tcv = sampleText();
        tcv.modifyChars(5, 0, "X");
        assertEquals("A<b>BX</b>", tcv.getDisplayText());
        tcv = sampleText();
        tcv.modifyChars(0,  1, "X");
        assertEquals("X<b>B</b>", tcv.getDisplayText());
        tcv = sampleText();
        tcv.modifyChars(4,  1, "X");
        assertEquals("A<b>X</b>", tcv.getDisplayText());
        tcv = sampleText();
        tcv.modifyChars(0, 1, null); // delete
        assertEquals("<b>B</b>", tcv.getDisplayText());
        tcv = sampleText();
        tcv.modifyChars(9, 0, "X");
        assertEquals("A<b>B</b>X", tcv.getDisplayText());
    }
}
