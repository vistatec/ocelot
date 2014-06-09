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
        List<String> rendered = getStyleData(false);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rendered.size(); i+= 2) {
            sb.append(rendered.get(i));
        }
        return sb.toString();
    }

    @Override
    public List<String> getStyleData(boolean verbose) {
        ArrayList<String> textToStyle = new ArrayList<String>();
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
                    String tag = getCodeText(tf.getCode(index), verbose);
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

    private String getCodeText(Code code, boolean verbose) {
        if (verbose) {
            return code.hasOuterData() ? code.getOuterData() : code.getData();
        }
        switch (code.getTagType()) {
        case OPENING:
            return "<" + code.getType() + ">"; 
        case CLOSING:
            return "</" + code.getType() + ">";
        case PLACEHOLDER:
            return "<" + code.getType() + "/>";
        }
        throw new IllegalStateException();
    }
    
    @Override
    public boolean containsTag(int offset, int length) {
        return checkForCode(offset, length);
    }
    
    @Override
    public boolean textIsInsertable(String text) {
        return true;
    }

    @Override
    public boolean canInsertAt(int offset) {
        return !checkForCode(offset, 0);
    }

    private boolean checkForCode(int offset, int length) {
        Iterator<TextPart> textParts = tc.iterator();
        int offsetEnd = offset + length;
        int index = 0;
        while (textParts.hasNext()) {
            TextFragment tf = textParts.next().text;
            for (int i = 0; i < tf.length(); i++) {
                char tfChar = tf.charAt(i);
                if (TextFragment.isMarker(tfChar)) {
                    char codeMarker = tf.charAt(++i);
                    int codeIndex = TextFragment.toIndex(codeMarker);
                    String tag = getCodeText(tf.getCode(codeIndex), false);
                    int codeEnd = index + tag.length();
                    if (offsetEnd > index && offset < codeEnd) {
                        return true; 
                    }
                    index += tag.length();
                }
                else if (index > offset + length) {
                    // We've drifted out of the danger zone
                    return false;
                }
                else {
                    index++;
                }
            }
        }
        return false;
    }

    @Override
    public void modifyChars(int offset, int charsToReplace, String newText) {
        Iterator<TextPart> textParts = tc.iterator();
        int index = 0;
        boolean isReplacing = false;
        if (newText == null) newText = "";
        while (textParts.hasNext()) {
            TextPart tp = textParts.next();
            TextFragment tf = tp.text;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tf.length(); i++) {
                char tfChar = tf.charAt(i);
                // Is it time to replace?
                if (index == offset) {
                    // Time to start replacing
                    isReplacing = true;
                    sb.append(newText);
                    index += newText.length(); // not really necessary any more
                }
                if (isReplacing && charsToReplace-- <= 0) {
                    isReplacing = false;
                }
                if (TextFragment.isMarker(tfChar)) {
                    char codeMarker = tf.charAt(++i);
                    int codeIndex = TextFragment.toIndex(codeMarker);
                    String tag = getCodeText(tf.getCode(codeIndex), false);
                    index += tag.length();
                    // We should never be replacing the tag tex, just count it. 
                    sb.append(tfChar).append(codeMarker);
                }
                else {
                    if (!isReplacing) {
                        sb.append(tfChar);
                        index++;
                    }
                }
            }
            // Check for append
            if (index == offset) {
                sb.append(newText);
            }
            tf.setCodedText(sb.toString());
            if (index > offset) {
                return; // don't need to process further
            }
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
