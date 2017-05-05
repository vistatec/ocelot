/*
 * Copyright (C) 2016, VistaTEC or third-party contributors as indicated
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

package com.vistatec.ocelot.segment.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.HeadlessException;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;

import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

import org.junit.Test;

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.okapi.FragmentVariant;
import com.vistatec.ocelot.segment.model.okapi.TestFragmentVariant.DummyWithStore;
import com.vistatec.ocelot.segment.model.okapi.TextContainerVariant;
import com.vistatec.ocelot.segment.view.SegmentTextEditorCell.SegmentVariantTransferable;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.Segment;
import net.sf.okapi.lib.xliff2.core.Store;
import net.sf.okapi.lib.xliff2.core.TagType;

public class TestSegmentTextCell {

    @Test
    public void testCreateView() {
        try {
            {
                SegmentTextCell cell = SegmentTextCell.createCell();
                assertTrue(cell.getText().isEmpty());
            }
            {
                SegmentTextCell cell = SegmentTextCell.createDummyCell();
                assertTrue(cell.getText().isEmpty());
            }
            {
                SegmentVariant v = newTextContainerVariant();
                SegmentTextCell cell = SegmentTextCell.createCell(0, v, false, false);
                assertEquals("A<b1>B</b1>", cell.getText());
            }
        } catch (HeadlessException ex) {
            // Can't perform this test while headless.
        }
    }

    @Test
    public void testCopyFromTextContainerVariant() throws Exception {
        // A < b 1 > B < / b 1 >
        // 0 1 2 3 4 5 6 7 8 9 10

        try {
            SegmentVariant v = newTextContainerVariant();
            SegmentTextEditorCell cell = SegmentTextEditorCell.createCell(0, v, false, false);
            assertEquals("A<b1>B</b1>", cell.getText());

            TransferHandler handler = cell.getTransferHandler();

            cell.setSelectionStart(1);
            cell.setSelectionEnd(6);

            Clipboard cb = new Clipboard("test");
            handler.exportToClipboard(cell, cb, TransferHandler.COPY);

            DataFlavor[] flavors = cb.getAvailableDataFlavors();
            assertEquals(Arrays.asList(SegmentTextEditorCell.SELECTION_FLAVOR, DataFlavor.stringFlavor),
                    Arrays.asList(flavors));

            Transferable transferable = cb.getContents(null);
            assertTrue(transferable instanceof SegmentVariantTransferable);

            SegmentVariantTransferable svt = (SegmentVariantTransferable) transferable;

            {
                Object data = svt.getTransferData(SegmentTextEditorCell.SELECTION_FLAVOR);
                assertTrue(data instanceof SegmentVariantSelection);
                SegmentVariantSelection sel = (SegmentVariantSelection) data;
                assertEquals("<b1>B", sel.getDisplayText());
            }
            {
                Object data = svt.getTransferData(DataFlavor.stringFlavor);
                assertTrue(data instanceof String);
                assertEquals("<b1>B", (String) data);
            }

            assertTrue(cell.canStopEditing());
            assertEquals("A<b1>B</b1>", cell.getText());
            assertEquals("A<b1>B</b1>", cell.getVariant().getDisplayText());
        } catch (HeadlessException ex) {
            // Can't perform this test while headless.
        }
    }

    @Test
    public void testCopyFromFragmentVariant() throws Exception {
        // A < p c i d 1 > B <  /  p  c  i  d  1  >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        try {
            SegmentVariant v = newFragmentVariant();
            SegmentTextEditorCell cell = SegmentTextEditorCell.createCell(0, v, false, false);
            assertEquals("A<pcid1>B</pcid1>", cell.getText());

            TransferHandler handler = cell.getTransferHandler();

            cell.setSelectionStart(1);
            cell.setSelectionEnd(9);

            Clipboard cb = new Clipboard("test");
            handler.exportToClipboard(cell, cb, TransferHandler.COPY);

            DataFlavor[] flavors = cb.getAvailableDataFlavors();
            assertEquals(Arrays.asList(SegmentTextEditorCell.SELECTION_FLAVOR, DataFlavor.stringFlavor),
                    Arrays.asList(flavors));

            Transferable transferable = cb.getContents(null);
            assertTrue(transferable instanceof SegmentVariantTransferable);

            SegmentVariantTransferable svt = (SegmentVariantTransferable) transferable;

            {
                Object data = svt.getTransferData(SegmentTextEditorCell.SELECTION_FLAVOR);
                assertTrue(data instanceof SegmentVariantSelection);
                SegmentVariantSelection sel = (SegmentVariantSelection) data;
                assertEquals("<pcid1>B", sel.getDisplayText());
            }
            {
                Object data = svt.getTransferData(DataFlavor.stringFlavor);
                assertTrue(data instanceof String);
                assertEquals("<pcid1>B", (String) data);
            }

            assertTrue(cell.canStopEditing());
            assertEquals("A<pcid1>B</pcid1>", cell.getText());
            assertEquals("A<pcid1>B</pcid1>", cell.getVariant().getDisplayText());
        } catch (HeadlessException ex) {
            // Can't perform this test while headless.
        }
    }
    
    @Test
    public void testCutFromTextContainerVariant() throws Exception {
        // A < b 1 > B < / b 1 >
        // 0 1 2 3 4 5 6 7 8 9 10

        try {
            SegmentVariant v = newTextContainerVariant();
            SegmentTextEditorCell cell = SegmentTextEditorCell.createCell(0, v, false, false);
            assertEquals("A<b1>B</b1>", cell.getText());

            TransferHandler handler = cell.getTransferHandler();

            cell.setSelectionStart(1);
            cell.setSelectionEnd(6);

            Clipboard cb = new Clipboard("test");
            handler.exportToClipboard(cell, cb, TransferHandler.MOVE);

            DataFlavor[] flavors = cb.getAvailableDataFlavors();
            assertEquals(Arrays.asList(SegmentTextEditorCell.SELECTION_FLAVOR, DataFlavor.stringFlavor),
                    Arrays.asList(flavors));

            Transferable transferable = cb.getContents(null);
            assertTrue(transferable instanceof SegmentVariantTransferable);

            SegmentVariantTransferable svt = (SegmentVariantTransferable) transferable;

            {
                Object data = svt.getTransferData(SegmentTextEditorCell.SELECTION_FLAVOR);
                assertTrue(data instanceof SegmentVariantSelection);
                SegmentVariantSelection sel = (SegmentVariantSelection) data;
                assertEquals("<b1>B", sel.getDisplayText());
            }
            {
                Object data = svt.getTransferData(DataFlavor.stringFlavor);
                assertTrue(data instanceof String);
                assertEquals("<b1>B", (String) data);
            }

            assertFalse(cell.canStopEditing());
            assertEquals("A</b1>", cell.getText());
            assertEquals("A</b1>", cell.getVariant().getDisplayText());
        } catch (HeadlessException ex) {
            // Can't perform this test while headless.
        }
    }

    @Test
    public void testCutFromFragmentVariant() throws Exception {
        // A < p c i d 1 > B <  /  p  c  i  d  1  >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        try {
            SegmentVariant v = newFragmentVariant();
            SegmentTextEditorCell cell = SegmentTextEditorCell.createCell(0, v, false, false);
            assertEquals("A<pcid1>B</pcid1>", cell.getText());

            TransferHandler handler = cell.getTransferHandler();

            cell.setSelectionStart(1);
            cell.setSelectionEnd(9);

            Clipboard cb = new Clipboard("test");
            handler.exportToClipboard(cell, cb, TransferHandler.MOVE);

            DataFlavor[] flavors = cb.getAvailableDataFlavors();
            assertEquals(Arrays.asList(SegmentTextEditorCell.SELECTION_FLAVOR, DataFlavor.stringFlavor),
                    Arrays.asList(flavors));

            Transferable transferable = cb.getContents(null);
            assertTrue(transferable instanceof SegmentVariantTransferable);

            SegmentVariantTransferable svt = (SegmentVariantTransferable) transferable;

            {
                Object data = svt.getTransferData(SegmentTextEditorCell.SELECTION_FLAVOR);
                assertTrue(data instanceof SegmentVariantSelection);
                SegmentVariantSelection sel = (SegmentVariantSelection) data;
                assertEquals("<pcid1>B", sel.getDisplayText());
            }
            {
                Object data = svt.getTransferData(DataFlavor.stringFlavor);
                assertTrue(data instanceof String);
                assertEquals("<pcid1>B", (String) data);
            }

            assertFalse(cell.canStopEditing());
            assertEquals("A</pcid1>", cell.getText());
            assertEquals("A</pcid1>", cell.getVariant().getDisplayText());
        } catch (HeadlessException ex) {
            // Can't perform this test while headless.
        }
    }

    @Test
    public void testPasteWithinTextContainerVariant() throws Exception {
        // A < b 1 > B < / b 1 >
        // 0 1 2 3 4 5 6 7 8 9 10

        try {
            SegmentVariant v = newTextContainerVariant();
            SegmentTextEditorCell cell = SegmentTextEditorCell.createCell(0, v, false, false);

            TransferHandler handler = cell.getTransferHandler();

            cell.setSelectionStart(1);
            cell.setSelectionEnd(6);

            Clipboard cb = new Clipboard("test");
            handler.exportToClipboard(cell, cb, TransferHandler.COPY);

            // Paste target is the same row; tags will be preserved.
            Transferable transferable = cb.getContents(null);
            TransferSupport support = new TransferSupport(cell, transferable);

            cell.setCaretPosition(1);
            assertTrue(handler.canImport(support));
            assertTrue(handler.importData(support));
            assertFalse(cell.canStopEditing());
            assertEquals("A<b1>B<b1>B</b1>", cell.getText());
            assertEquals("A<b1>B<b1>B</b1>", cell.getVariant().getDisplayText());

            // Paste into the middle of a tag.
            cell.setCaretPosition(2);
            assertTrue(handler.canImport(support));
            assertFalse(handler.importData(support));
            assertFalse(cell.canStopEditing());
            assertEquals("A<b1>B<b1>B</b1>", cell.getText());
            assertEquals("A<b1>B<b1>B</b1>", cell.getVariant().getDisplayText());
        } catch (HeadlessException ex) {
            // Can't perform this test while headless.
        }
    }

    @Test
    public void testPasteWithinFragmentVariant() throws Exception {
        // A < p c i d 1 > B < / p c i d 1 >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        try {
            SegmentVariant v = newFragmentVariant();
            SegmentTextEditorCell cell = SegmentTextEditorCell.createCell(0, v, false, false);

            TransferHandler handler = cell.getTransferHandler();

            cell.setSelectionStart(1);
            cell.setSelectionEnd(9);

            Clipboard cb = new Clipboard("test");
            handler.exportToClipboard(cell, cb, TransferHandler.COPY);

            // Paste target is the same row; tags will be preserved.
            Transferable transferable = cb.getContents(null);
            TransferSupport support = new TransferSupport(cell, transferable);

            cell.setCaretPosition(1);
            assertTrue(handler.canImport(support));
            assertTrue(handler.importData(support));
            assertFalse(cell.canStopEditing());
            assertEquals("A<pcid1>B<pcid1>B</pcid1>", cell.getText());
            assertEquals("A<pcid1>B<pcid1>B</pcid1>", cell.getVariant().getDisplayText());

            // Paste into the middle of a tag.
            cell.setCaretPosition(2);
            assertTrue(handler.canImport(support));
            assertFalse(handler.importData(support));
            assertFalse(cell.canStopEditing());
            assertEquals("A<pcid1>B<pcid1>B</pcid1>", cell.getText());
            assertEquals("A<pcid1>B<pcid1>B</pcid1>", cell.getVariant().getDisplayText());
        } catch (HeadlessException ex) {
            // Can't perform this test while headless.
        }
    }

    @Test
    public void testPasteToOtherTextContainerVariant() throws Exception {
        // A < b 1 > B < / b 1 >
        // 0 1 2 3 4 5 6 7 8 9 10

        try {
            SegmentVariant v = newTextContainerVariant();
            SegmentTextEditorCell cell = SegmentTextEditorCell.createCell(0, v, false, false);

            TransferHandler handler = cell.getTransferHandler();

            cell.setSelectionStart(1);
            cell.setSelectionEnd(6);

            Clipboard cb = new Clipboard("test");
            handler.exportToClipboard(cell, cb, TransferHandler.COPY);

            // Paste target is a different row; tags will be reduced to plain
            // text.
            SegmentVariant v2 = newTextContainerVariant();
            SegmentTextEditorCell cell2 = SegmentTextEditorCell.createCell(1, v2, false, false);

            Transferable transferable = cb.getContents(null);
            TransferSupport support = new TransferSupport(cell2, transferable);

            cell2.setCaretPosition(1);
            assertTrue(handler.canImport(support));
            assertTrue(handler.importData(support));
            assertTrue(cell2.canStopEditing());
            assertEquals("A<b1>B<b1>B</b1>", cell2.getText());
            assertEquals("A<b1>B<b1>B</b1>", cell2.getVariant().getDisplayText());

            // Paste into the middle of a tag.
            cell2.setCaretPosition(7);
            assertTrue(handler.canImport(support));
            assertFalse(handler.importData(support));
            assertTrue(cell2.canStopEditing());
            assertEquals("A<b1>B<b1>B</b1>", cell2.getText());
            assertEquals("A<b1>B<b1>B</b1>", cell2.getVariant().getDisplayText());
        } catch (HeadlessException ex) {
            // Can't perform this test while headless.
        }
    }

    @Test
    public void testPasteToOtherFragmentVariant() throws Exception {
        // A < p c i d 1 > B < / p c i d 1 >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        try {
            SegmentVariant v = newFragmentVariant();
            SegmentTextEditorCell cell = SegmentTextEditorCell.createCell(0, v, false, false);

            TransferHandler handler = cell.getTransferHandler();

            cell.setSelectionStart(1);
            cell.setSelectionEnd(9);

            Clipboard cb = new Clipboard("test");
            handler.exportToClipboard(cell, cb, TransferHandler.COPY);

            // Paste target is a different row; tags will be reduced to plain
            // text.
            SegmentVariant v2 = newFragmentVariant();
            SegmentTextEditorCell cell2 = SegmentTextEditorCell.createCell(1, v2, false, false);

            Transferable transferable = cb.getContents(null);
            TransferSupport support = new TransferSupport(cell2, transferable);

            cell2.setCaretPosition(1);
            assertTrue(handler.canImport(support));
            assertTrue(handler.importData(support));
            assertTrue(cell2.canStopEditing());
            assertEquals("A<pcid1>B<pcid1>B</pcid1>", cell2.getText());
            assertEquals("A<pcid1>B<pcid1>B</pcid1>", cell2.getVariant().getDisplayText());

            // Paste into the middle of a tag.
            cell2.setCaretPosition(11);
            assertTrue(handler.canImport(support));
            assertFalse(handler.importData(support));
            assertTrue(cell2.canStopEditing());
            assertEquals("A<pcid1>B<pcid1>B</pcid1>", cell2.getText());
            assertEquals("A<pcid1>B<pcid1>B</pcid1>", cell2.getVariant().getDisplayText());
        } catch (HeadlessException ex) {
            // Can't perform this test while headless.
        }
    }

    @Test
    public void testPasteToWrongVariantType() throws Exception {
        // A < p c i d 1 > B < / p c i d 1 >
        // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16

        try {
            SegmentVariant v = newFragmentVariant();
            SegmentTextEditorCell cell = SegmentTextEditorCell.createCell(0, v, false, false);

            TransferHandler handler = cell.getTransferHandler();

            cell.setSelectionStart(1);
            cell.setSelectionEnd(9);

            Clipboard cb = new Clipboard("test");
            handler.exportToClipboard(cell, cb, TransferHandler.COPY);

            // Paste target is the same row but a different variant type, so
            // tags
            // will be reduced to plain text. This can't happen the way Ocelot
            // works
            // currently, but it's better to be safe than sorry.
            SegmentVariant v2 = newTextContainerVariant();
            SegmentTextEditorCell cell2 = SegmentTextEditorCell.createCell(0, v2, false, false);

            Transferable transferable = cb.getContents(null);
            TransferSupport support = new TransferSupport(cell2, transferable);

            cell2.setCaretPosition(1);
            assertTrue(handler.canImport(support));
            assertTrue(handler.importData(support));
            assertTrue(cell2.canStopEditing());
            assertEquals("A<pcid1>B<b1>B</b1>", cell2.getText());
            assertEquals("A<pcid1>B<b1>B</b1>", cell2.getVariant().getDisplayText());

            // Paste into the middle of a tag.
            cell2.setCaretPosition(11);
            assertTrue(handler.canImport(support));
            assertFalse(handler.importData(support));
            assertTrue(cell2.canStopEditing());
            assertEquals("A<pcid1>B<b1>B</b1>", cell2.getText());
            assertEquals("A<pcid1>B<b1>B</b1>", cell2.getVariant().getDisplayText());
        } catch (HeadlessException ex) {
            // Can't perform this test while headless.
        }
    }

    private TextContainerVariant newTextContainerVariant() {
        TextContainer tc = new TextContainer();
        TextFragment tf = tc.getFirstContent();
        tf.append("A");
        tf.append(new Code(TextFragment.TagType.OPENING, "b", "<b id=\"1\">"));
        tf.append("B");
        tf.append(new Code(TextFragment.TagType.CLOSING, "b", "</b>"));

        return new TextContainerVariant(tc);
    }

    private FragmentVariant newFragmentVariant() {
        Store store = new Store(new DummyWithStore());
        Segment segment = new Segment(store);
        Fragment fragment = new Fragment(store, false);
        fragment.append("A");
        fragment.append(TagType.OPENING, "id1", "<b>", false);
        fragment.append("B");
        fragment.append(TagType.CLOSING, "id1", "</b>", false);
        segment.setSource(fragment);
        return new FragmentVariant(segment, fragment.isTarget());
    }
}
