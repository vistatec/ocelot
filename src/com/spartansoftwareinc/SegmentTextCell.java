package com.spartansoftwareinc;

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
    private static Logger LOG = Logger.getLogger(SegmentTextCell.class);
    private TextContainer tc;
    private boolean edited;

    public SegmentTextCell() {
        setEditController();
        setDisplayCategories();
    }

    public SegmentTextCell(TextContainer tc) {
        this();
        setTextContainer(tc);
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
        Style regular = styleDoc.addStyle("regular", style);

        Style s = styleDoc.addStyle("tag", regular);
        StyleConstants.setBackground(s, Color.LIGHT_GRAY);
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

    public ArrayList<String> styleTag(TextContainer tc) {
        ArrayList<String> textToStyle = new ArrayList<String>();
        Iterator<TextPart> textParts = tc.iterator();
        while (textParts.hasNext()) {
            TextFragment tf = textParts.next().text;
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < tf.length(); i++) {
                if (TextFragment.isMarker(tf.charAt(i))) {
                    textToStyle.add(text.toString());
                    textToStyle.add("regular");
                    text = new StringBuilder();
                    String tag = ""+tf.charAt(i)+tf.charAt(++i);
                    textToStyle.add(tag);
                    textToStyle.add("tag");
                } else {
                    text.append(tf.charAt(i));
                }
            }
            if (text.length() > 0) {
                textToStyle.add(text.toString());
                textToStyle.add("regular");
            }
        }
        return textToStyle;
    }

    public boolean changedText() {
        return this.edited;
    }

    public void setChangedText(boolean flag) {
        this.edited = flag;
    }

    public TextContainer getTextContainer() {
        return this.tc;
    }

    public final void setTextContainer(TextContainer tc) {
        this.tc = tc;
        setTextPane(styleTag(tc));
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

        public void replace(FilterBypass fb, int offset, int length, String str,
                AttributeSet a) throws BadLocationException {
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
            int textPos = 0, partIndex = 0;
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
                partIndex++;
                textPos += tf.length();
            }
            setChangedText(true);
        }
    }
}
