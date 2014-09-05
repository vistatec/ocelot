/*
 * Copyright (C) 2014, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.okapi;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.segment.BaseSegmentVariant;
import com.vistatec.ocelot.segment.CodeAtom;
import com.vistatec.ocelot.segment.SegmentAtom;
import com.vistatec.ocelot.segment.SegmentVariant;
import com.vistatec.ocelot.segment.TextAtom;

import net.sf.okapi.lib.xliff2.core.CTag;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.MTag;
import net.sf.okapi.lib.xliff2.core.PCont;
import net.sf.okapi.lib.xliff2.core.Tag;
import net.sf.okapi.lib.xliff2.its.TermTag;

/**
 * XLIFF 2.0 segment variant, implemented using the Okapi XLIFF 2.0 library Fragment.
 */
public class FragmentVariant extends BaseSegmentVariant {
    private final Logger LOG = LoggerFactory.getLogger(FragmentVariant.class);
    private List<SegmentAtom> segmentAtoms;

    public FragmentVariant(Fragment fragment) {
        segmentAtoms = parseSegmentAtoms(fragment);
    }

    public FragmentVariant(List<SegmentAtom> atoms) {
        this.segmentAtoms = atoms;
    }

    private List<SegmentAtom> parseSegmentAtoms(Fragment frag) {
        List<SegmentAtom> parsedSegmentAtoms = new ArrayList<SegmentAtom>();
        for (Object textObject : frag) {
            if (textObject instanceof String) {
                parsedSegmentAtoms.add(new TextAtom((String) textObject));

            } else if (textObject instanceof Tag) {
                if (textObject instanceof MTag) {
                    MTag mtag = (MTag) textObject;
                    //TODO:

                } else if (textObject instanceof CTag) {
                    CTag ctag = (CTag) textObject;
                    // TODO: verbose data? Check identifier.
                    parsedSegmentAtoms.add(new CodeAtom(ctag.getId(),
                            ctag.getData(), ctag.getData()));

                } else if (textObject instanceof TermTag) {
                    //TODO:
                }

            } else if (textObject instanceof PCont) {
                //TODO: Verify usage, there's no ID?
//                PCont pcont = (PCont) textObject;
//                segmentAtoms.add(new CodeAtom(, pcont.getCodedText(), null));

            } else {
                // TODO: More descriptive error
                LOG.error("Unrecognized object type in Fragment");
                System.exit(1);
            }
        }
        return parsedSegmentAtoms;
    }

    @Override
    protected List<SegmentAtom> getAtoms() {
        return this.segmentAtoms;
    }

    @Override
    protected void setAtoms(List<SegmentAtom> atoms) {
        this.segmentAtoms = atoms;
    }

    private List<SegmentAtom> copyAtoms() {
        List<SegmentAtom> copyAtoms = new ArrayList<SegmentAtom>();
        for (SegmentAtom atom : segmentAtoms) {
            if (atom instanceof TextAtom) {
                copyAtoms.add(new TextAtom(atom.getData()));

            } else if (atom instanceof CodeAtom) {
                CodeAtom codeAtom = (CodeAtom) atom;
                copyAtoms.add(new CodeAtom(codeAtom.getId(),
                        codeAtom.getData(), codeAtom.getVerboseData()));
            }
        }
        return copyAtoms;
    }

    @Override
    public SegmentVariant createEmpty() {
        return new FragmentVariant(new ArrayList<SegmentAtom>());
    }

    @Override
    public SegmentVariant createCopy() {
        return new FragmentVariant(copyAtoms());
    }

    @Override
    public void setContent(SegmentVariant variant) {
        FragmentVariant copy = (FragmentVariant) variant;
        this.segmentAtoms = copy.copyAtoms();
    }
}
