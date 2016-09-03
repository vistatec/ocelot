/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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

import com.vistatec.ocelot.segment.model.SegmentVariant;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputMethodEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representation of source/target segment text in segment table view.
 * Handles the style of the text with Inline tags and the link between
 * the editor behavior and the underlying data structure.
 */
public class SegmentTextCell extends JTextPane {
    private static final long serialVersionUID = 1L;

    private static Logger LOG = LoggerFactory.getLogger(SegmentTextCell.class);
    public static final String tagStyle = "tag", regularStyle = "regular",
            insertStyle = "insert", deleteStyle = "delete", enrichedStyle = "enriched", highlightStyle="highlight", currHighlightStyle="currHighlight";
    private int row = -1;
    private SegmentVariant vOrig;
    private SegmentVariant v;
    private boolean raw;
    
    private boolean inputMethodChanged;

    // Shared styles table
    private static final StyleContext styles = new StyleContext();
    static {
        Style style = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style regular = styles.addStyle(regularStyle, style);

        Style s = styles.addStyle(tagStyle, regular);
        StyleConstants.setBackground(s, Color.LIGHT_GRAY);

        Style insert = styles.addStyle(insertStyle, s);
        StyleConstants.setForeground(insert, Color.BLUE);
        StyleConstants.setUnderline(insert, true);

        Style delete = styles.addStyle(deleteStyle, insert);
        StyleConstants.setForeground(delete, Color.RED);
        StyleConstants.setStrikeThrough(delete, true);
        StyleConstants.setUnderline(delete, false);
        
        Style highlight = styles.addStyle(highlightStyle, regular);
        StyleConstants.setBackground(highlight, Color.yellow);
        
        Style currHighlight = styles.addStyle(currHighlightStyle, regular);
        StyleConstants.setBackground(currHighlight, Color.green);
    }

    /**
     * Create a dummy cell for the purposes of cell sizing.  This cell
     * doesn't contain the style information and isn't linked to any of
     * the control logic.
     * @return dummy cell
     */
    public static SegmentTextCell createDummyCell() {
        return new SegmentTextCell();
    }

    /**
     * Create an empty cell for the purpose of holding live content. This
     * cell contains style information and is linked to the document.
     * @return real cell
     */
    public static SegmentTextCell createCell() {
        return new SegmentTextCell(styles);
    }

    /**
     * Create an empty cell holding the specified content. This
     * cell contains style information and is linked to the document.
     * @param v
     * @param raw
     * @param isBidi whether the cell contains bidi content
     * @return
     */
    public static SegmentTextCell createCell(int row, SegmentVariant v, boolean raw, boolean isBidi) {
        return new SegmentTextCell(row, v, raw, isBidi);
    }

    private SegmentTextCell(StyleContext styleContext) {
        super(new DefaultStyledDocument(styleContext));
        setEditController();
        addCaretListener(new TagSelectingCaretListener());
        setTransferHandler(new TagAwareTransferHandler());
        setDragEnabled(true);
    }

    private SegmentTextCell() {
        super();
    }

    private SegmentTextCell(int row, SegmentVariant v, boolean raw, boolean isBidi) {
        this(styles);
        setVariant(row, v, raw);
        setBidi(isBidi);
    }

    public void setBidi(boolean isBidi) {
        if (isBidi) {
            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
    }

    /**
     * A caret listener that detects selections that encompass
     * only part of tags and automatically expand the selection
     * to include full tags.  This produces cascading CaretUpdate
     * events, but the cycle should stop after a single additional
     * update.
     */
    class TagSelectingCaretListener implements CaretListener {
        @Override
        public void caretUpdate(CaretEvent e) {
            if (e.getDot() != e.getMark()) {
                int origStart = Math.min(e.getDot(), e.getMark());
                int origEnd = Math.max(e.getDot(), e.getMark());
                int start = v.findSelectionStart(origStart);
                int end = v.findSelectionEnd(origEnd);
                if (start != origStart) {
                    setSelectionStart(start);
                }
                if (end != origEnd) {
                    setSelectionEnd(end);
                }
            }
        }
    }

    public final void setEditController() {
        StyledDocument styledDoc = getStyledDocument();
        if (styledDoc instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument)styledDoc;
            doc.setDocumentFilter(new SegmentFilter());
        }
    }

    public final void setDisplayCategories() {
        Style style = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyledDocument styleDoc = this.getStyledDocument();
        Style regular = styleDoc.addStyle(regularStyle, style);

        Style highlight = styleDoc.addStyle(highlightStyle, regular);
        StyleConstants.setBackground(highlight, Color.yellow);
        
        Style currHighlight = styleDoc.addStyle(currHighlightStyle, regular);
        StyleConstants.setBackground(currHighlight, Color.green);
        
        Style s = styleDoc.addStyle(tagStyle, regular);
        StyleConstants.setBackground(s, Color.LIGHT_GRAY);

        Style insert = styleDoc.addStyle(insertStyle, s);
        StyleConstants.setForeground(insert, Color.BLUE);
        StyleConstants.setUnderline(insert, true);

        Style delete = styleDoc.addStyle(deleteStyle, insert);
        StyleConstants.setForeground(delete, Color.RED);
        StyleConstants.setStrikeThrough(delete, true);
        StyleConstants.setUnderline(delete, false);
        
        Style enriched = styleDoc.addStyle(enrichedStyle, regular);
        StyleConstants.setForeground(enriched, Color.BLUE);
        StyleConstants.setUnderline(enriched, true);
        
    }

    public void setTextPane(List<String> styledText) {
        StyledDocument doc = this.getStyledDocument();
        try {
            for (int i = 0; i < styledText.size(); i += 2) {
                doc.insertString(doc.getLength(), styledText.get(i),
                        doc.getStyle(styledText.get(i + 1)));
            }
        } catch (BadLocationException ex) {
            LOG.error("Error rendering text", ex);
        }
    }

    public SegmentVariant getVariant() {
        return this.v;
    }

    public final void setVariant(int row, SegmentVariant v, boolean raw) {
        this.row = row;
        this.v = v;
        this.vOrig = v.createCopy();
        this.raw = raw;
        syncModelToView();
    }

    private void syncModelToView() {
        SegmentVariant tmp = v;
        try {
            // We temporarily set v to null here to get around the
            // SegmentFilter, which will prevent us from clearing the text if
            // there are tags.
            v = null;
            StyledDocument doc = getStyledDocument();
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            LOG.debug("", e);
        } finally {
            v = tmp;
        }
        if (v != null) {
            setTextPane(v.getStyleData(raw));
        }
        else {
            setTextPane(new ArrayList<String>());
        }
    }

    public void setTargetDiff(List<String> targetDiff) {
        setTextPane(targetDiff);
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.text.JTextComponent#processInputMethodEvent(java.awt.event
     * .InputMethodEvent)
     */
    @Override
    protected void processInputMethodEvent(InputMethodEvent e) {
        /*
         * Some keyboards, such as Traditional Chinese keyboard, trigger the
         * INPUT_METHOD_TEXT_CHANGED event while typing text. This event causes
         * the remove method in the DocumentFilter to be invoked, resulting in
         * some characters erroneously deleted. The inputMethodChanged field
         * value is set to true in case this event is triggered. This field is
         * then checked within the remove method, and the characters are
         * actually removed only if this field is false.
         */
        inputMethodChanged = e.getID() == InputMethodEvent.INPUT_METHOD_TEXT_CHANGED;
        super.processInputMethodEvent(e);
    }



	/**
     * Handles edit behavior in segment text cell.
     */
    public class SegmentFilter extends DocumentFilter {

        // This is also called when initially populating the table,
        // as swing will try to "remove" the old contents.
        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {

            if (v != null) {
                // Disallow tag deletions
                if (!v.containsTag(offset, length)) {
                    // Remove from cell editor
                    super.remove(fb, offset, length);
    
                    if(!inputMethodChanged){
	                    // Remove from underlying segment structure
	                    deleteChars(offset, length);
                    }
                }
            }
            else {
                // TODO: why does this correct the spacing issue?
                super.remove(fb, offset, length);
            }
            inputMethodChanged = false;
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String str,
                AttributeSet a) throws BadLocationException {
            if (length > 0) {
                if (!v.containsTag(offset, length)) {
                    // Remove from cell editor
                    super.replace(fb, offset, length, str, a);

                    // Remove from underlying segment structure
                    v.modifyChars(offset, length, str);
                }
            } else {
                if (v.canInsertAt(offset)) {
                    // Insert string into cell editor.
                    super.replace(fb, offset, length, str, a);

                    insertChars(str, offset);
                }
            }
            inputMethodChanged = false;

        }

        public void deleteChars(int offset, int charsToRemove) {
            v.modifyChars(offset, charsToRemove, null);
        }

        public void insertChars(String insertText, int offset) {
            v.modifyChars(offset, 0, insertText);
        }
    }

    static class TagAwareTransferHandler extends TransferHandler {

        private static final long serialVersionUID = 1L;

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            SegmentTextCell cell = (SegmentTextCell) c;
            SegmentVariantSelection selection = new SegmentVariantSelection(cell.row, cell.v.createCopy(),
                    cell.getSelectionStart(), cell.getSelectionEnd());
            return new SegmentVariantTransferable(selection);
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == TransferHandler.MOVE) {
                SegmentTextCell cell = (SegmentTextCell) source;
                try {
                    // TODO: This approach doesn't work when drag-and-dropping a
                    // selection backwards (to a lower index) because the region
                    // we then try to replace is no longer correct. Fix this.
                    SegmentVariantSelection sel = (SegmentVariantSelection) data.getTransferData(SELECTION_FLAVOR);
                    SegmentVariantSelection emptySelection = new SegmentVariantSelection(-1, cell.v.createEmptyTarget(),
                            0, 0);
                    cell.v.replaceSelection(sel.getSelectionStart(), sel.getSelectionEnd(), emptySelection);
                    cell.syncModelToView();
                } catch (UnsupportedFlavorException | IOException e) {
                    LOG.debug("", e);
                }
            }
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(SELECTION_FLAVOR)
                    || support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            SegmentTextCell cell = (SegmentTextCell) support.getComponent();
            Transferable trfr = support.getTransferable();
            if (support.isDataFlavorSupported(SELECTION_FLAVOR)) {
                try {
                    SegmentVariantSelection sel = (SegmentVariantSelection) trfr.getTransferData(SELECTION_FLAVOR);
                    if (sel.getRow() == cell.row) {
                        cell.v.replaceSelection(cell.getSelectionStart(), cell.getSelectionEnd(), sel);
                        cell.syncModelToView();
                        return true;
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    LOG.info("", e);
                }
            }
            if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String str = trfr.getTransferData(DataFlavor.stringFlavor).toString();
                    cell.replaceSelection(str);
                    return true;
                } catch (UnsupportedFlavorException | IOException e) {
                    LOG.debug("", e);
                }
            }
            return false;
        }
    }

    static final DataFlavor SELECTION_FLAVOR = new DataFlavor(SegmentVariantSelection.class,
            SegmentVariantSelection.class.getSimpleName());

    static class SegmentVariantTransferable implements Transferable {

        private static final DataFlavor[] FLAVORS = { SELECTION_FLAVOR, DataFlavor.stringFlavor };

        private final SegmentVariantSelection selection;

        public SegmentVariantTransferable(SegmentVariantSelection selection) {
            this.selection = selection;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return FLAVORS;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return Arrays.asList(FLAVORS).contains(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (SELECTION_FLAVOR.equals(flavor)) {
                return selection;
            } else if (DataFlavor.stringFlavor.equals(flavor)) {
                return selection.getDisplayText();
            }
            throw new UnsupportedFlavorException(flavor);
        }

    }

    public boolean canStopEditing() {
        return v == null || !v.needsValidation() || v.validateAgainst(vOrig);
    }
}