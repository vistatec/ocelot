/*
 * Copyright (C) 2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.model.okapi;

import java.util.List;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;

import com.vistatec.ocelot.segment.model.CodeAtom;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;

/**
 * XLIFF 1.2 segment variant, implemented using Okapi
 * TextContainers.
 */
public class TextContainerVariant extends OkapiSegmentVariant {
    private final TextContainer tc;

    public TextContainerVariant(TextContainer tc) {
        this.tc = tc;
    }

    @Override
    public TextContainerVariant createEmptyTarget() {
        return new TextContainerVariant(new TextContainer());
    }

    @Override
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
    protected List<SegmentAtom> getAtoms() {
        return convertTextFragment(tc.getUnSegmentedContentCopy());
    }

    @Override
    protected void setAtoms(List<SegmentAtom> atoms) {
        // Unfortunately, TextContainer's can't view all of the codes
        // they contain.
        List<Code> tcCodes = tc.getUnSegmentedContentCopy().getCodes();
        TextFragment frag = new TextFragment();
        for (SegmentAtom atom : atoms) {
            if (atom instanceof CodeAtom) {
                CodeAtom codeAtom = (CodeAtom) atom;
                Code c = tcCodes.get( Integer.parseInt(codeAtom.getId()) );
                frag.append(c);
            }
            else {
                frag.append(atom.getData());
            }
        }
        tc.setContent(frag);
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
    public int hashCode() {
        return tc.hashCode();
    }

    @Override
    public String toString() {
        return getDisplayText();
    }
}
