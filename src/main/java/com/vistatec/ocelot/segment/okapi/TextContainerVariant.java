package com.vistatec.ocelot.segment.okapi;

import java.util.List;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import com.google.common.collect.Lists;
import com.vistatec.ocelot.segment.BaseSegmentVariant;
import com.vistatec.ocelot.segment.CodeAtom;
import com.vistatec.ocelot.segment.SegmentAtom;
import com.vistatec.ocelot.segment.SegmentVariant;
import com.vistatec.ocelot.segment.TextAtom;

/**
 * XLIFF 1.2 segment variant, implemented using Okapi
 * TextContainers.
 */
public class TextContainerVariant extends BaseSegmentVariant {
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

    // XXX problem - in paired tags, they both have the same IDs.
    // IDs aren't unique!
    @Override
    protected List<SegmentAtom> getAtoms() {
        List<SegmentAtom> atoms = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        TextFragment tf = tc.getUnSegmentedContentCopy();
        for (int i = 0; i < tf.length(); i++) {
            char tfChar = tf.charAt(i);
            if (TextFragment.isMarker(tfChar)) {
                if (sb.length() > 0) {
                    // Flush as text
                    atoms.add(new TextAtom(sb.toString()));
                    sb.setLength(0);
                }
                char codeMarker = tf.charAt(++i);
                int codeIndex = TextFragment.toIndex(codeMarker);
                Code code = tf.getCode(codeIndex);
                atoms.add(new CodeAtom(codeIndex, getCodeText(code, false),
                                       getCodeText(code, true)));
            }
            else {
                sb.append(tfChar);
            }
        }
        // Flush trailing markup
        if (sb.length() > 0) {
            atoms.add(new TextAtom(sb.toString()));
        }

        return atoms;
    }

    @Override
    protected void setAtoms(List<SegmentAtom> atoms) {
        // Unfortunately, TextContainer's can't view all of the codes
        // they contain.
        List<Code> tcCodes = tc.getUnSegmentedContentCopy().getCodes();
        TextFragment frag = new TextFragment();
        for (SegmentAtom atom : atoms) {
            if (atom instanceof CodeAtom) {
                Code c = tcCodes.get(((CodeAtom)atom).getId());
                frag.append(c);
            }
            else {
                frag.append(atom.getData());
            }
        }
        tc.setContent(frag);
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
        return getDisplayText();
    }
}
