package com.vistatec.ocelot.segment.okapi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;

import com.vistatec.ocelot.segment.SegmentTextCell;
import com.vistatec.ocelot.segment.SegmentVariant;

/**
 * XLIFF 1.2 segment variant, implemented using Okapi
 * TextContainers.
 */
public class TextContainerVariant implements SegmentVariant {
    private TextContainer tc;

    public TextContainerVariant(TextContainer tc) {
        this.tc = tc;
    }

    public TextContainerVariant createEmpty() {
        return new TextContainerVariant(new TextContainer());
    }

    public TextContainerVariant createCopy() {
        return new TextContainerVariant(tc.clone());
    }

    @Override
    public void setContent(SegmentVariant variant) {
        TextContainerVariant other = (TextContainerVariant)variant;
        tc.setContent(other.getTextContainer().getUnSegmentedContentCopy());
    }

    public TextContainer getTextContainer() {
        return tc;
    }

    @Override
    public String getDisplayText() {
        return tc.getCodedText();
    }

    @Override
    public List<String> getStyleData(boolean verbose) {
        ArrayList<String> textToStyle = new ArrayList<String>();
        // XXX Not clear to me why we can't just use tc.getCodedText() here
        // instead of iterating over the fragments.
        Iterator<TextPart> textParts = tc.iterator();
        while (textParts.hasNext()) {
            TextFragment tf = textParts.next().text;
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < tf.length(); i++) {
                char tfChar = tf.charAt(i);
                if (TextFragment.isMarker(tfChar)) {
                    textToStyle.add(text.toString());
                    textToStyle.add(SegmentTextCell.regularStyle);
                    text = new StringBuilder();

                    char codeMarker = tf.charAt(++i);
                    int index = TextFragment.toIndex(codeMarker);
                    Code code = tf.getCode(index);
                    String tag;
                    if (verbose) {
                        if (code.hasOuterData()) {
                            tag = code.getOuterData();
                        } else {
                            tag = code.getData();
                        }
                    } else {
                        tag = ""+tfChar+codeMarker;
                    }
                    textToStyle.add(tag);
                    textToStyle.add(SegmentTextCell.tagStyle);
                } else {
                    text.append(tfChar);
                }
            }
            if (text.length() > 0) {
                textToStyle.add(text.toString());
                textToStyle.add(SegmentTextCell.regularStyle);
            }
        }
        return textToStyle;
    }

    @Override
    public boolean containsTag(int offset, int length) {
        /*
         * TextFragment marker is composed of 2 unicode chars:
         * 1st char indicates it's a code marker
         * 2nd char indicates the code index position for retrieval
         * -Need 1st char to determine there's a marker in removal text.
         * So this is necessary paranoia - make sure we get enough characters to
         * determine if we've got a tag in here.
         */
        if (offset > 0) {
            offset--;
        }
        return checkForCode(tc.getCodedText(), offset, length);
    }
    
    @Override
    public boolean textIsInsertable(String text) {
        return !checkForCode(text, 0, text.length());
    }

    @Override
    public boolean canInsertAt(int offset) {
        return (offset == 0) ?
                checkForCode(tc.getCodedText(), 0, 1) :
                checkForCode(tc.getCodedText(), offset - 1, 2); // breaks when appending
    }

    private boolean checkForCode(String s, int offset, int length) {
        char[] text = s.toCharArray();
        int max = Math.min(offset + length, s.length());
        for (int i = offset; i < max; i++) {
            if (TextFragment.isMarker(text[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void modifyChars(int offset, int charsToReplace, String newText) {
        Iterator<TextPart> textParts = tc.iterator();
        int textPos = 0;
        while (textParts.hasNext()) {
            TextPart tp = textParts.next();
            TextFragment tf = tp.text;
            if (offset <= textPos + tf.length()) {
                int adjustedOffset = offset - textPos;
                int adjustedLength = charsToReplace;
                if (adjustedOffset + charsToReplace > tf.length()) {
                    adjustedLength = tf.length() - adjustedOffset;
                }
                tf.remove(adjustedOffset, adjustedOffset + adjustedLength);
                charsToReplace -= adjustedLength;
                if (newText != null) {
                    tf.insert(adjustedOffset, new TextFragment(newText));
                    newText = null;
                }
                tp.setContent(tf);
            }
            textPos += tf.length();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof TextContainerVariant)) return false;
        // XXX This is not correct, but it's based on the legacy behavior
        // where equality was checked. Since codes are currently invariant
        // in Ocelot, it will work for now, but break if we ever allow real
        // editing.
        return tc.getCodedText().equals(((TextContainerVariant)o).getTextContainer().getCodedText());
    }

    @Override
    public String toString() {
        return tc.toString();
    }
}
