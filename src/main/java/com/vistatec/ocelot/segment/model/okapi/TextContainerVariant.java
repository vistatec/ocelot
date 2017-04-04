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

import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.segment.model.CodeAtom;
import com.vistatec.ocelot.segment.model.HighlightData;
import com.vistatec.ocelot.segment.model.PositionAtom;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.segment.model.TextAtom.HighlightBoundaries;

import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;

/**
 * XLIFF 1.2 segment variant, implemented using Okapi
 * TextContainers.
 */
public class TextContainerVariant extends OkapiSegmentVariant {
    private final TextContainer tc;
    private List<SegmentAtom> atoms;

    public TextContainerVariant(TextContainer tc) {
        this.tc = tc;
        this.atoms = extractAtoms(tc);
    }

    /**
     * Private constructor for cloning only.
     */
    private TextContainerVariant(TextContainer tc, List<SegmentAtom> atoms) {
        this.tc = tc;
        this.atoms = atoms;
    }

    @Override
    public TextContainerVariant createEmptyTarget() {
        return new TextContainerVariant(new TextContainer());
    }

    @Override
    public TextContainerVariant createCopy() {
    	
        return new TextContainerVariant(tc.clone(), copyAtoms());
    }

    private List<SegmentAtom> copyAtoms() {
        List<SegmentAtom> copyAtoms = new ArrayList<SegmentAtom>();
        for (SegmentAtom atom : atoms) {
            if (atom instanceof TextAtom) {
                copyAtoms.add(new TextAtom(atom.getData()));

            } else if (atom instanceof OkapiCodeAtom) {
                OkapiCodeAtom okapiCodeAtom = (OkapiCodeAtom) atom;
                copyAtoms.add(new OkapiCodeAtom(okapiCodeAtom.getId(), okapiCodeAtom.getData(),
                        okapiCodeAtom.getVerboseData(), okapiCodeAtom.getCode().clone()));

            } else if (atom instanceof CodeAtom) {
                CodeAtom codeAtom = (CodeAtom) atom;
                copyAtoms.add(new CodeAtom(codeAtom.getId(), codeAtom.getData(), codeAtom.getVerboseData()));
            } else if (atom instanceof PositionAtom) {
                // Don't copy position atoms because no one will have a handle
                // on the new atom so it will be useless.
            }
        }
        return copyAtoms;
    }

    @Override
    public void setContent(SegmentVariant variant) {
        TextContainerVariant other = (TextContainerVariant)variant;
        tc.setContent(other.getTextContainer().getUnSegmentedContentCopy());
        atoms = extractAtoms(tc);
    }

    public TextContainer getTextContainer() {
        return tc;
    }

    @Override
    public List<SegmentAtom> getAtoms() {
        return this.atoms;
    }

    private List<SegmentAtom> extractAtoms(TextContainer tc) {
    	List<SegmentAtom> atoms = convertTextFragment(tc.getUnSegmentedContentCopy());
		if (highlightDataList != null) {
			HighlightData hlData = null;
			for (int i = 0; i< highlightDataList.size(); i++) {
				hlData = highlightDataList.get(i);
				if (hlData.getAtomIndex() != -1
						&& hlData.getAtomIndex() < atoms.size()
						&& atoms.get(hlData.getAtomIndex()) instanceof TextAtom) {
					TextAtom txtAtom = (TextAtom) atoms.get(hlData
							.getAtomIndex());
//					txtAtom.setHighlightIndices(hlData.getHighlightIndices());
					TextAtom.HighlightBoundaries hlBoundary = new TextAtom.HighlightBoundaries(hlData.getHighlightIndices()[0], hlData.getHighlightIndices()[1]);
					txtAtom.addHighlightBoundary(hlBoundary);
					if(i == currentHighlightedIndex){
						txtAtom.setCurrentHLBoundaryIdx(txtAtom.getHighlightBoundaries().indexOf(hlBoundary));
					}
				}
			}
		}
        return atoms;
    }

    @Override
    public void setAtoms(List<SegmentAtom> atoms) {
        this.atoms = atoms;
        writeAtoms(atoms, tc);
    }

    private void writeAtoms(List<SegmentAtom> atoms, TextContainer tc) {
        // Unfortunately, TextContainer's can't view all of the codes
        // they contain.
        List<Code> tcCodes = tc.getUnSegmentedContentCopy().getCodes();
        TextFragment frag = new TextFragment();
        for (SegmentAtom atom : atoms) {
            if (atom instanceof OkapiCodeAtom) {
                OkapiCodeAtom codeAtom = (OkapiCodeAtom) atom;
                frag.append(codeAtom.getCode());
            } else if (atom instanceof CodeAtom) {
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
	public void addHighlightData(HighlightData highlightData) {
		super.addHighlightData(highlightData);
		if (atoms != null && highlightData.getAtomIndex() < atoms.size()) {
			SegmentAtom atom = atoms.get(highlightData.getAtomIndex());
			if (atom instanceof TextAtom) {
				((TextAtom) atom).addHighlightBoundary(new HighlightBoundaries(
						highlightData.getHighlightIndices()[0], highlightData
								.getHighlightIndices()[1]));
			}
		}
	}
}
