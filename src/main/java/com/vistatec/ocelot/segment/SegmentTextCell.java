/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;
import org.apache.log4j.Logger;

/**
 * Representation of source/target segment text in segment table view.
 * Handles the style of the text with Inline tags and the link between
 * the editor behavior and the underlying data structure.
 */
public class SegmentTextCell extends JTextPane {
    private static final long serialVersionUID = 1L;

    private static Logger LOG = Logger.getLogger(SegmentTextCell.class);
    public static String tagStyle = "tag", regularStyle = "regular",
            insertStyle = "insert", deleteStyle = "delete";
    private TextContainer tc;

    public SegmentTextCell() {
        setEditController();
        setDisplayCategories();
    }

    public SegmentTextCell(TextContainer tc, boolean raw) {
        this();
        setTextContainer(tc, raw);
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

    public void setTextPane(ArrayList<String> styledText) {
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

    public ArrayList<String> styleTag(TextContainer tc, boolean raw) {
        ArrayList<String> textToStyle = new ArrayList<String>();
        Iterator<TextPart> textParts = tc.iterator();
        while (textParts.hasNext()) {
            TextFragment tf = textParts.next().text;
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < tf.length(); i++) {
                char tfChar = tf.charAt(i);
                if (TextFragment.isMarker(tfChar)) {
                    textToStyle.add(text.toString());
                    textToStyle.add(regularStyle);
                    text = new StringBuilder();

                    char codeMarker = tf.charAt(++i);
                    int index = TextFragment.toIndex(codeMarker);
                    Code code = tf.getCode(index);
                    String tag;
                    if (raw) {
                        if (code.hasOuterData()) {
                            tag = code.getOuterData();
                        } else {
                            tag = code.getData();
                        }
                    } else {
                        tag = ""+tfChar+codeMarker;
                    }
                    textToStyle.add(tag);
                    textToStyle.add(tagStyle);
                } else {
                    text.append(tfChar);
                }
            }
            if (text.length() > 0) {
                textToStyle.add(text.toString());
                textToStyle.add(regularStyle);
            }
        }
        return textToStyle;
    }

    public TextContainer getTextContainer() {
        return this.tc;
    }

    public final void setTextContainer(TextContainer tc, boolean raw) {
        this.tc = tc;
        setTextPane(styleTag(tc, raw));
    }

    public void setTargetDiff(ArrayList<String> targetDiff) {
        setTextPane(targetDiff);
    }

    /**
     * Handles edit behavior in segment text cell.
     */
    public class SegmentFilter extends DocumentFilter {

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            String text = fb.getDocument().getText(offset, length);
            if (offset > 0) {
                /**
                 * TextFragment marker is composed of 2 unicode chars:
                 * 1st char indicates it's a code marker
                 * 2nd char indicates the code index position for retrieval
                 * -Need 1st char to determine there's a marker in removal text.
                 */
                text = fb.getDocument().getText(offset-1, 1) + text;
            }
            boolean tag = containsTag(text);

            if (!tag && tc != null) {
                // Remove from cell editor
                super.remove(fb, offset, length);

                // Remove from underlying segment structure
                deleteChars(offset, length);
            }

            // TODO: why does this correct the spacing issue?
            if (tc == null) {
                super.remove(fb, offset, length);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String str,
                AttributeSet a) throws BadLocationException {
            if (containsTag(str)) {
                return;
            }
            if (length > 0) {
                String text = fb.getDocument().getText(offset, length);
                if (offset > 0) {
                    /**
                     * TextFragment marker is composed of 2 unicode chars: 1st
                     * char indicates it's a code marker 2nd char indicates the
                     * code index position for retrieval -Need 1st char to
                     * determine there's a marker in removal text.
                     */
                    text = fb.getDocument().getText(offset - 1, 1) + text;
                }
                if (!containsTag(text)) {
                    // Remove from cell editor
                    super.replace(fb, offset, length, str, a);

                    // Remove from underlying segment structure
                    modifyChars(offset, length, str);
                }
            } else {
                boolean insideMarker = false;
                if (offset > 0) {
                    String prevChar = fb.getDocument().getText(offset-1, 1);
                    insideMarker = TextFragment.isMarker(prevChar.toCharArray()[0]);
                }

                if (!insideMarker) {
                    // Insert string into cell editor.
                    super.replace(fb, offset, length, str, a);

                    insertChars(str, offset);
                }
            }

        }

        public boolean containsTag(String text) {
            boolean tag = false;
            for (int i = 0; i < text.length(); i++) {
                if (TextFragment.isMarker(text.charAt(i))) {
                    tag = true;
                }
            }
            return tag;
        }

        public void deleteChars(int offset, int charsToRemove) {
            modifyChars(offset, charsToRemove, null);
        }

        public void insertChars(String insertText, int offset) {
            modifyChars(offset, 0, insertText);
        }

        public void modifyChars(int offset, int charsToRemove, String replacementChars) {
            Iterator<TextPart> textParts = tc.iterator();
            int textPos = 0;
            while (textParts.hasNext()) {
                TextPart tp = textParts.next();
                TextFragment tf = tp.text;
                if (offset <= textPos + tf.length()) {
                    int adjustedOffset = offset - textPos;
                    int adjustedLength = charsToRemove;
                    if (adjustedOffset + charsToRemove > tf.length()) {
                        adjustedLength = tf.length() - adjustedOffset;
                    }
                    tf.remove(adjustedOffset, adjustedOffset + adjustedLength);
                    charsToRemove -= adjustedLength;
                    if (replacementChars != null) {
                        tf.insert(adjustedOffset, new TextFragment(replacementChars));
                        replacementChars = null;
                    }
                    tp.setContent(tf);
                }
                textPos += tf.length();
            }
        }
    }
}
