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
import java.awt.event.InputMethodEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

/**
 * Representation of source/target segment text in segment table view.
 * Handles the style of the text with Inline tags and the link between
 * the editor behavior and the underlying data structure.
 */
public class SegmentTextCell extends JTextPane {
    private static final long serialVersionUID = 1L;

    private static Logger LOG = Logger.getLogger(SegmentTextCell.class);
    public static final String tagStyle = "tag", regularStyle = "regular",
            insertStyle = "insert", deleteStyle = "delete", highlightStyle="highlight";
    private SegmentVariant v;
    
    private boolean inputMethodChanged;

    public SegmentTextCell() {
        setEditController();
        setDisplayCategories();
        addCaretListener(new TagSelectingCaretListener());
    }

    public SegmentTextCell(SegmentVariant v, boolean raw, boolean isBidi) {
        this();
        setVariant(v, raw);
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
        
        Style s = styleDoc.addStyle(tagStyle, regular);
        StyleConstants.setBackground(s, Color.LIGHT_GRAY);

        Style insert = styleDoc.addStyle(insertStyle, s);
        StyleConstants.setForeground(insert, Color.BLUE);
        StyleConstants.setUnderline(insert, true);

        Style delete = styleDoc.addStyle(deleteStyle, insert);
        StyleConstants.setForeground(delete, Color.RED);
        StyleConstants.setStrikeThrough(delete, true);
        StyleConstants.setUnderline(delete, false);
    }

    public void setTextPane(List<String> styledText) {
        StyledDocument doc = this.getStyledDocument();
        try {
            for (int i = 0; i < styledText.size(); i += 2) {
                doc.insertString(doc.getLength(), styledText.get(i),
                        doc.getStyle(styledText.get(i + 1)));
            }
        } catch (BadLocationException ex) {
            LOG.error(ex);
        }
    }

    public SegmentVariant getVariant() {
        return this.v;
    }

    public final void setVariant(SegmentVariant v, boolean raw) {
        this.v = v;
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
}
