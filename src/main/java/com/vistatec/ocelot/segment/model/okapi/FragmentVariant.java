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
import com.vistatec.ocelot.segment.model.HighlightData;
import com.vistatec.ocelot.segment.model.PositionAtom;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.segment.model.TextAtom.HighlightBoundaries;

/**
 * XLIFF 2.0 segment variant, implemented using the Okapi XLIFF 2.0 library Fragment.
 */
public class FragmentVariant extends BaseSegmentVariant {
    private final static Logger LOG = LoggerFactory.getLogger(FragmentVariant.class);
    private List<SegmentAtom> segmentAtoms;
    private boolean isTarget;
    private int protectedContentId = 0;
    

    public FragmentVariant(Segment okapiSegment, boolean isTarget) {
        this.isTarget = isTarget;
        if (isTarget) {
            Fragment frag = okapiSegment.getTarget();
            if (frag == null) {
                // target elements are optional; make a dummy one if none exists
                frag = new Fragment(okapiSegment.getStore(), true);
                okapiSegment.setTarget(frag);
            }
            segmentAtoms = parseSegmentAtoms(frag);
        }
        else {
            segmentAtoms = parseSegmentAtoms(okapiSegment.getSource());
        }
    }

    public FragmentVariant(List<SegmentAtom> atoms, boolean isTarget) {
        this.segmentAtoms = atoms;
        this.isTarget = isTarget;
    }
    
    public FragmentVariant(Fragment frag, boolean isTarget){
    	
    	this.isTarget = isTarget;
    	this.segmentAtoms = parseSegmentAtoms(frag);
    }

    private List<SegmentAtom> parseSegmentAtoms(Fragment frag) {
        List<SegmentAtom> parsedSegmentAtoms = new ArrayList<SegmentAtom>();
        XLIFFFragmentRenderer fragmentRenderer = new XLIFFFragmentRenderer(frag, null);
        for (IFragmentObject fragPart : fragmentRenderer) {
            Object textObject = fragPart.getObject();
            if (textObject instanceof String) {
				TextAtom txtAtom = new TextAtom(fragPart.render());
				parsedSegmentAtoms.add(txtAtom);

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
		if (highlightDataList != null ){
			setAtomsHighlightedText();
		}
        return parsedSegmentAtoms;
    }

    private CodeAtom convertToCodeAtom(IFragmentObject fragPart, Tag tag) {
        String detailedTag = fragPart.render();
        String basicTag = getBasicTag(detailedTag, tag.getId());
        return new TaggedCodeAtom(tag, basicTag, detailedTag);
    }

    private CodeAtom convertToCodeAtom(IFragmentObject fragPart) {
        String detailedTag = fragPart.render();
        String id = getFragmentObjectId(fragPart);        
        String basicTag = getBasicTag(detailedTag, id);
        return new CodeAtom("PC"+protectedContentId++, basicTag, detailedTag);
    }

    private String getFragmentObjectId(IFragmentObject fragPart) {
        try {
            return fragPart.getCTag().getId();
        } catch (ClassCastException ex) {
        }
        try {
            return fragPart.getMTag().getId();
        } catch (ClassCastException ex) {
        }
        return "";
    }

    private String getBasicTag(String detailedTag, String id) {
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
                + id + detailedTag.substring(tagEndCaratPos, detailedTag.length());
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
            } else if (atom instanceof PositionAtom) {
                // Don't copy position atoms because no one will have a handle
                // on the new atom so it will be useless.
            }
        }
        return copyAtoms;
    }

    boolean isTarget() {
        return isTarget;
    }
    
    @Override
    public FragmentVariant createEmptyTarget() {
        return new FragmentVariant(new ArrayList<SegmentAtom>(), true);
    }

    @Override
    public FragmentVariant createCopy() {
    	FragmentVariant copyFragment = new FragmentVariant(copyAtoms(), isTarget);
        return copyFragment;
    }

    @Override
    public void setContent(SegmentVariant variant) {
        FragmentVariant copy = (FragmentVariant) variant;
        this.segmentAtoms = copy.copyAtoms();
    }

//	@Override
//	public void addHighlightData(HighlightData highlightData) {
//		super.addHighlightData(highlightData);
//		if (segmentAtoms != null
//				&& highlightData.getAtomIndex() < segmentAtoms.size()) {
//			if (segmentAtoms.get(highlightData.getAtomIndex()) instanceof TextAtom) {
//				TextAtom txtatom = (TextAtom) segmentAtoms.get(highlightData
//						.getAtomIndex());
//				txtatom.addHighlightBoundary(new HighlightBoundaries(
//						highlightData.getHighlightIndices()[0], highlightData
//								.getHighlightIndices()[1]));
//			}
//		}
//	}

//	@Override
//	public void setHighlightDataList(List<HighlightData> highlightDataList) {
//		super.setHighlightDataList(highlightDataList);
//		clearAtomsHighlightedText();
//		setAtomsHighlightedText();
//	}
//
//	public void setAtomsHighlightedText() {
//		if (segmentAtoms != null && highlightDataList != null) {
//			HighlightData hd = null;
//			TextAtom txtAtom = null;
//			for(int i = 0; i<highlightDataList.size(); i++){
//				hd = highlightDataList.get(i);
//				if (hd.getAtomIndex() < segmentAtoms.size()
//						&& segmentAtoms.get(hd.getAtomIndex()) instanceof TextAtom) {
//					txtAtom = (TextAtom) segmentAtoms.get(hd.getAtomIndex());
//					HighlightBoundaries hb  = new HighlightBoundaries(hd
//							.getHighlightIndices()[0], hd
//							.getHighlightIndices()[1]);
//					txtAtom.addHighlightBoundary(hb);
//					if(currentHighlightedIndex == i){
//						txtAtom.setCurrentHLBoundaryIdx(txtAtom.getHighlightBoundaries().indexOf(hb));
//					}
//				}
//			}
//		}
//	}
    
//	@Override
//	public void removeHighlightData(int atomIndex, int startIndex, int endIndex) {
//		super.removeHighlightData(atomIndex, startIndex, endIndex);
//		if(segmentAtoms != null && atomIndex < segmentAtoms.size()){
//			if(segmentAtoms.get(atomIndex) instanceof TextAtom){
//				TextAtom txtAtom = (TextAtom) segmentAtoms.get(atomIndex);
//				txtAtom.removeHighlighBoundary(startIndex, endIndex);
//			}
//		}
//	}

//	private void clearAtomsHighlightedText() {
//		if (segmentAtoms != null) {
//			for (SegmentAtom a : segmentAtoms) {
//				if (a instanceof TextAtom) {
//					((TextAtom) a).clearHighlights();
//				}
//			}
//		}
//	}

//	@Override
//	public void clearHighlightedText() {
//		super.clearHighlightedText();
//		clearAtomsHighlightedText();
//	}
	
//	@Override
//	public void setCurrentHighlightedIndex(int currentHighlightedIndex) {
//		super.setCurrentHighlightedIndex(currentHighlightedIndex);
//		if(segmentAtoms != null){
//			if(currentHighlightedIndex > -1 ){
//				HighlightData hd = highlightDataList.get(currentHighlightedIndex);
//				if(hd.getAtomIndex() < segmentAtoms.size() && segmentAtoms.get(hd.getAtomIndex()) instanceof TextAtom){
//					TextAtom txtAtom = (TextAtom)segmentAtoms.get(hd.getAtomIndex());
//					if(txtAtom.getHighlightBoundaries() != null){
//						HighlightBoundaries hb = null;
//						for(int i = 0; i<txtAtom.getHighlightBoundaries().size(); i++){
//							hb = txtAtom.getHighlightBoundaries().get(i);
//							if(hb.getFirstIndex() == hd.getHighlightIndices()[0] && hb.getLastIndex() == hd.getHighlightIndices()[1]){
//								txtAtom.setCurrentHLBoundaryIdx(i);
//								break;
//							}
//						}
//					}
//				}
//			} else {
//				for(SegmentAtom atom: segmentAtoms){
//					if(atom instanceof TextAtom) {
//						((TextAtom)atom).setCurrentHLBoundaryIdx(-1);
//					}
//				}
//			}
//		}
//	}
	
//	@Override
//	public void replaced(String newString) {
//		if (highlightDataList != null) {
//			HighlightData hd = highlightDataList.get(currentHighlightedIndex);
//			if (segmentAtoms != null && hd.getAtomIndex() < segmentAtoms.size()
//					&& segmentAtoms.get(hd.getAtomIndex()) instanceof TextAtom) {
//				TextAtom currAtom = (TextAtom) segmentAtoms.get(hd
//						.getAtomIndex());
//				if (currAtom.getHighlightBoundaries() != null) {
//					HighlightBoundaries currHb = currAtom
//							.getHighlightBoundaries().get(
//									currAtom.getCurrentHLBoundaryIdx());
//					if (currAtom.getCurrentHLBoundaryIdx() < currAtom
//							.getHighlightBoundaries().size() - 1) {
//						int currHbIndex = currAtom.getCurrentHLBoundaryIdx() + 1;
//						int delta = newString.length()
//								- (currHb.getLastIndex() - currHb
//										.getFirstIndex());
//						HighlightBoundaries nextHb = null;
//						while (currHbIndex < currAtom.getHighlightBoundaries()
//								.size()) {
//							nextHb = currAtom.getHighlightBoundaries().get(
//									currHbIndex++);
//							nextHb.setFirstIndex(nextHb.getFirstIndex() + delta);
//							nextHb.setLastIndex(nextHb.getLastIndex() + delta);
//						}
//					}
//					currAtom.getHighlightBoundaries().remove(currHb);
//					currAtom.setCurrentHLBoundaryIdx(-1);
//				}
//			}
//		}
//		super.replaced(newString);
//	}
}