/*
 * Copyright (C) 2014-2015, VistaTEC or third-party contributors as indicated
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
import java.util.Comparator;
import java.util.List;

import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.PCont;
import net.sf.okapi.lib.xliff2.core.Segment;
import net.sf.okapi.lib.xliff2.core.Tag;
import net.sf.okapi.lib.xliff2.core.Tags;
import net.sf.okapi.lib.xliff2.renderer.IFragmentObject;
import net.sf.okapi.lib.xliff2.renderer.XLIFFFragmentRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.CodeAtom;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;

/**
 * XLIFF 2.0 segment variant, implemented using the Okapi XLIFF 2.0 library Fragment.
 */
public class FragmentVariant extends BaseSegmentVariant {
    private final Logger LOG = LoggerFactory.getLogger(FragmentVariant.class);
    private List<SegmentAtom> segmentAtoms;
    private boolean isTarget;
    private int protectedContentId = 0;
//    private boolean enriched;
    //TODO add enrichment tags
//    private List<Enrichment> enrichments;
//    private Map<Integer, CodeAtom> codeAtomsMap;
    

    public FragmentVariant(Segment okapiSegment, boolean isTarget) {
        this.isTarget = isTarget;
        segmentAtoms = parseSegmentAtoms(isTarget ? okapiSegment.getTarget() :
                okapiSegment.getSource());
    }

    public FragmentVariant(List<SegmentAtom> atoms, boolean isTarget) {
        this.segmentAtoms = atoms;
        this.isTarget = isTarget;
    }

    private List<SegmentAtom> parseSegmentAtoms(Fragment frag) {
        List<SegmentAtom> parsedSegmentAtoms = new ArrayList<SegmentAtom>();
        XLIFFFragmentRenderer fragmentRenderer = new XLIFFFragmentRenderer(frag, null);
        for (IFragmentObject fragPart : fragmentRenderer) {
            Object textObject = fragPart.getObject();
            if (textObject instanceof String) {
                parsedSegmentAtoms.add(new TextAtom(fragPart.render()));

            } else if (textObject instanceof Tag) {
                Tag tag = (Tag) textObject;
                parsedSegmentAtoms.add(convertToCodeAtom(fragPart, tag));

            } else if (textObject instanceof PCont) {
                //TODO: Verify usage
                parsedSegmentAtoms.add(convertToCodeAtom(fragPart));

            } else {
                // TODO: More descriptive error
                LOG.error("Unrecognized object type in Fragment");
                System.exit(1);
            }
        }
        return parsedSegmentAtoms;
    }

    private CodeAtom convertToCodeAtom(IFragmentObject fragPart, Tag tag) {
        String detailedTag = fragPart.render();
        String basicTag = getBasicTag(detailedTag);
        return new TaggedCodeAtom(tag, basicTag, detailedTag);
    }

    private CodeAtom convertToCodeAtom(IFragmentObject fragPart) {
        String detailedTag = fragPart.render();
        String basicTag = getBasicTag(detailedTag);
        return new CodeAtom("PC"+protectedContentId++, basicTag, detailedTag);
    }

    private String getBasicTag(String detailedTag) {
        int tagEndCaratPos = detailedTag.indexOf(">");
        if (tagEndCaratPos < 0) {
            // TODO: Handle this case
            LOG.warn("Could not find tag end character '>' in '"+detailedTag+"'");
            System.exit(1);
        }
        if (detailedTag.charAt(tagEndCaratPos-1) == '/') {
            tagEndCaratPos--;
        }
        int beginTagAttrPos = detailedTag.indexOf(" ");
        return "<"+detailedTag.substring(1, beginTagAttrPos >= 0 ? beginTagAttrPos : tagEndCaratPos)
                +detailedTag.substring(tagEndCaratPos, detailedTag.length());
    }

    public Fragment getUpdatedOkapiFragment(Fragment fragment) {
        Fragment updatedFragment = new Fragment(fragment.getStore(), fragment.isTarget(), "");
        Tags tags = fragment.getTags();
        for (SegmentAtom atom : segmentAtoms) {
            if (atom instanceof TaggedCodeAtom) {
                TaggedCodeAtom codeAtom = (TaggedCodeAtom) atom;
                Tag tag = codeAtom.getTag();
                if (tag != null) {
                    updatedFragment.append(Fragment.toChar1(tags.getKey(tag)))
                            .append(Fragment.toChar2(tags.getKey(tag)));
                }
            } else if (atom instanceof CodeAtom) {
                CodeAtom codeAtom = (CodeAtom) atom;
                Tag tag = fetchTag(codeAtom.getId(), fragment);
                if (tag != null) {
                    updatedFragment.append(Fragment.toChar1(tags.getKey(tag)))
                            .append(Fragment.toChar2(tags.getKey(tag)));
                }
            } else {
                updatedFragment.append(atom.getData());
            }
        }
        return updatedFragment;
    }

    private Tag fetchTag(String tagId, Fragment fragment) {
        Tags tags = fragment.getTags();
        for (Tag tag : tags) {
            if (tagId.equals(tag.getId())) {
                return tag;
            }
        }
        return null;
    }

    public void updateSegmentAtoms(Segment okapiSegment) {
        this.segmentAtoms = parseSegmentAtoms(isTarget ?
                okapiSegment.getTarget() : okapiSegment.getSource());
    }

    @Override
    public List<SegmentAtom> getAtoms() {
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

            } else if (atom instanceof TaggedCodeAtom) {
                TaggedCodeAtom taggedCodeAtom = (TaggedCodeAtom) atom;
                copyAtoms.add(new TaggedCodeAtom(taggedCodeAtom.getTag(),
                        taggedCodeAtom.getData(), taggedCodeAtom.getVerboseData()));

            } else if (atom instanceof CodeAtom) {
                CodeAtom codeAtom = (CodeAtom) atom;
                copyAtoms.add(new CodeAtom(codeAtom.getId(),
                        codeAtom.getData(), codeAtom.getVerboseData()));
            }
        }
        return copyAtoms;
    }

    boolean isTarget() {
        return isTarget;
    }
    
//    public void setEnrichments(final List<Enrichment> enrichments ){
//        this.enrichments = enrichments;
//    	if(enrichments != null){
//                String fragDisplayText = getTextForEnrichment();
//                clearTextAtoms();
//                int startIndex = 0;
//                EnrichedAtom lastEnrichedAtom = null;
//                TextAtom textAtom = null;
////                if(codeAtomsMap.containsKey(startIndex)){
////                	segmentAtoms.add(codeAtomsMap.remove(startIndex));
////                }
//                for (Enrichment e : enrichments) {
//                	
//                    if (lastEnrichedAtom != null
//                            && lastEnrichedAtom.containsOffset(e
//                                    .getOffsetStartIdx())) {
//                    	lastEnrichedAtom.addEnrichment(e);
//                        if (startIndex < e.getOffsetEndIdx()) {
//                            startIndex = e.getOffsetEndIdx();
//                        }
//                    } else {
//                    	textAtom = new TextAtom(fragDisplayText.substring(startIndex,
//                                e.getOffsetStartIdx()));
//                        segmentAtoms.add(textAtom);
//                        lastEnrichedAtom = new EnrichedAtom(fragDisplayText.substring(e.getOffsetStartIdx(),
//                                e.getOffsetEndIdx()));
//                        lastEnrichedAtom.addEnrichment(e);
//                        segmentAtoms.add(lastEnrichedAtom);
//                        startIndex = e.getOffsetEndIdx();
//                    }
//                }
//                if (startIndex < fragDisplayText.length()) {
//                	segmentAtoms.add(new TextAtom(fragDisplayText.substring(startIndex)));
//                }
//            }
//    }
    
//    private String getTextForEnrichment(){
//    	
//    	StringBuilder text = new StringBuilder();
//    	codeAtomsMap = new HashMap<Integer, CodeAtom>();
//    	for(SegmentAtom atom: segmentAtoms){
//    		if(atom instanceof CodeAtom){
//    			codeAtomsMap.put(text.length(), (CodeAtom)atom);
//    		} else {
//    			text.append(atom.getData());
//    		}
//    	}
//    	return text.toString();
//    }
//    
//    private void clearTextAtoms(){
//    	List<TextAtom> textAtoms = new ArrayList<TextAtom>();
//    	for(SegmentAtom atom: getAtoms()){
//    		if(atom instanceof TextAtom){
//    			textAtoms.add((TextAtom)atom);
//    		}
//    	}
//    	segmentAtoms.removeAll(textAtoms);
//    }
    
    
    
//    public boolean isEnriched(){
//        return enriched;
//    }
//    
//    public void setEnriched(final boolean enriched){
//        this.enriched = enriched;
//    }
    
    @Override
    public FragmentVariant createEmptyTarget() {
        return new FragmentVariant(new ArrayList<SegmentAtom>(), true);
    }

    @Override
    public FragmentVariant createCopy() {
        return new FragmentVariant(copyAtoms(), isTarget);
    }

    @Override
    public void setContent(SegmentVariant variant) {
        FragmentVariant copy = (FragmentVariant) variant;
        this.segmentAtoms = copy.copyAtoms();
    }

//    public List<Enrichment> getEnirchments() {
//        return enrichments;
//    }
    
    @Override
    public List<String> getStyleData(boolean verbose) {
        ArrayList<String> textToStyle = new ArrayList<String>();

        for (SegmentAtom atom : getAtoms()) {
            if (atom instanceof CodeAtom && verbose) {
                textToStyle.add(((CodeAtom)atom).getVerboseData());
            }
            else {
                textToStyle.add(atom.getData());
            }
            textToStyle.add(atom.getTextStyle());
        }
        return textToStyle;
    }
}

class EnrichmentComparator implements Comparator<Enrichment> {

    @Override
    public int compare(Enrichment o1, Enrichment o2) {

        int comparison = 0;
        if (o1.getOffsetStartIdx() < o2.getOffsetStartIdx()) {
            comparison = -1;
        } else if (o1.getOffsetStartIdx() > o2.getOffsetStartIdx()) {
            comparison = 1;
        }
        return comparison;
    }

}

