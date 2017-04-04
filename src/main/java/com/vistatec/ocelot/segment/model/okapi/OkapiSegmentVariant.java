package com.vistatec.ocelot.segment.model.okapi;

import java.util.List;

import com.google.common.collect.Lists;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.TextAtom;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextFragment;

/**
 * Contains methods for handling {@link net.sf.okapi.common.resource.TextFragment}s
 * and {@link net.sf.okapi.common.resource.Code}s.
 */
public abstract class OkapiSegmentVariant extends BaseSegmentVariant {

    // XXX problem - in paired tags, they both have the same IDs.
    // IDs aren't unique!
    protected List<SegmentAtom> convertTextFragment(TextFragment tf) {
        List<SegmentAtom> atoms = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
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
                atoms.add(new OkapiCodeAtom(codeIndex + "", getCodeText(code, false), getCodeText(code, true), code));
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

    public String getCodeText(Code code, boolean verbose) {
        if (verbose) {
            return code.hasOuterData() ? code.getOuterData() : code.getData();
        }
        switch (code.getTagType()) {
        case OPENING:
            return "<" + code.getType() + code.getId() + ">";
        case CLOSING:
            return "</" + code.getType() + code.getId() + ">";
        case PLACEHOLDER:
            return "<" + code.getType() + code.getId() + "/>";
        }
        throw new IllegalStateException();
    }

    @Override
    public abstract List<SegmentAtom> getAtoms();

    @Override
    protected abstract void setAtoms(List<SegmentAtom> atoms);

    @Override
    public abstract SegmentVariant createEmptyTarget();

    @Override
    public abstract SegmentVariant createCopy();

    @Override
    public abstract void setContent(SegmentVariant variant);

}
