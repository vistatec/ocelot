package com.vistatec.ocelot.segment.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.hp.hpl.jena.rdf.model.Model;
import com.vistatec.ocelot.segment.model.TextAtom.HighlightBoundaries;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.TranslationEnrichment;
import com.vistatec.ocelot.segment.view.SegmentVariantSelection;

public abstract class BaseSegmentVariant implements SegmentVariant {

//	private boolean enriched;
	
	private boolean fremeSuccess;
	
	private boolean sentToFreme;

	private Model tripleModel;
	
	private Set<Enrichment> enrichments;
	
	private TranslationEnrichment transEnrichment;

    private boolean dirty;
    
    private Integer[] enrichIndexMapping;

protected List<HighlightData> highlightDataList;
    
    protected int currentHighlightedIndex = -1;

	protected abstract void setAtoms(List<SegmentAtom> atoms);

	List<SegmentAtom> getAtomsForRange(int start, int length) {
		List<SegmentAtom> atomsForRange = Lists.newArrayList();
		int index = 0;
		int end = start + length;

		for (SegmentAtom atom : getAtoms()) {
			if (index == start && atom instanceof PositionAtom) {
				// Catch PositionAtom at the very beginning of the range.
				atomsForRange.add(atom);
			}
			if (index >= end) {
				return atomsForRange;
			}
			if (index + atom.getLength() > start) {
				if (atom instanceof CodeAtom) {
					atomsForRange.add(atom);
				} else if (atom instanceof PositionAtom) {
					atomsForRange.add(atom);
				} else {
					int min = Math.max(start - index, 0);
					int max = Math.min(end - index, atom.getData().length());
					atomsForRange.add(new TextAtom(atom.getData().substring(
							min, max)));
				}
			}
			index += atom.getLength();
		}
		return atomsForRange;
	}

    @Override
    public SegmentAtom getAtomAt(int offset) {
        int index = 0;
        for (SegmentAtom atom : getAtoms()) {
            if (index <= offset && offset < index + atom.getLength()) {
                return atom;
            }
            index += atom.getLength();
        }
        return null;
    }

	public int getLength() {
		int len = 0;
		for (SegmentAtom atom : getAtoms()) {
			len += atom.getLength();
		}
		return len;
	}

	@Override
	public String getDisplayText() {
		StringBuilder sb = new StringBuilder();
		for (SegmentAtom atom : getAtoms()) {
			sb.append(atom.getData());
		}
		return sb.toString();
	}

	@Override
	public List<String> getStyleData(boolean verbose) {
		ArrayList<String> textToStyle = new ArrayList<String>();

		for (SegmentAtom atom : getAtoms()) {
			if (atom instanceof CodeAtom && verbose) {
                textToStyle.add(((CodeAtom)atom).getVerboseData());
                textToStyle.add(atom.getTextStyle());
            }
            else if(atom instanceof TextAtom) {
            	TextAtom txtAtom = (TextAtom)atom;
            	if(txtAtom.getHighlightBoundaries() != null){
            		int index = 0;
					for (int i = 0; i < txtAtom.getHighlightBoundaries().size(); i++) {
						String style = null;
						if (i == txtAtom.getCurrentHLBoundaryIdx()) {
							style = txtAtom.getCurrHighlightStyle();
						} else {
							style = txtAtom.getHighlightStyle();
						}
						textToStyle.add(atom.getData().substring(
								index,
								txtAtom.getHighlightBoundaries().get(i)
										.getFirstIndex()));
						textToStyle.add(txtAtom.getTextStyle());
						textToStyle.add(atom.getData().substring(
								txtAtom.getHighlightBoundaries().get(i)
										.getFirstIndex(),
								txtAtom.getHighlightBoundaries().get(i)
										.getLastIndex()));
						textToStyle.add(style);
						index = txtAtom.getHighlightBoundaries().get(i)
								.getLastIndex();
					}
            		if(txtAtom.getData().length() > index){
            			textToStyle.add(atom.getData().substring(index));
            			textToStyle.add(txtAtom.getTextStyle());
            		}
            	
			} else {
				textToStyle.add(atom.getData());
            		textToStyle.add(atom.getTextStyle());
				}
			} else if (atom instanceof PositionAtom) {
				// Skip
			} else {
				textToStyle.add(atom.getData());
				textToStyle.add(atom.getTextStyle());
		}
        }
		return textToStyle;
	}

	@Override
	public boolean containsTag(int offset, int length) {
		return checkForCode(offset, length).size() > 0;
	}

	@Override
	public int findSelectionStart(int selectionStart) {
		while (containsTag(selectionStart, 0)) {
			selectionStart--;
		}
		return selectionStart;
	}

	@Override
	public int findSelectionEnd(int selectionEnd) {
		while (containsTag(selectionEnd, 0)) {
			selectionEnd++;
		}
		return selectionEnd;
	}

	@Override
	public boolean canInsertAt(int offset) {
		return checkForCode(offset, 0).size() == 0;
	}

	// Returns list of codes that occur in the specified range
	private List<CodeAtom> checkForCode(int offset, int length) {
		List<CodeAtom> codes = Lists.newArrayList();

		int offsetEnd = offset + length;
		int index = 0;
		for (SegmentAtom atom : getAtoms()) {
			if (index > offsetEnd) {
				// We've drifted out of the danger zone
				return codes;
			}
			if (atom instanceof CodeAtom) {
				CodeAtom code = (CodeAtom) atom;
				if (offsetEnd > index && offset < index + code.getLength()) {
					codes.add(code);
				}
			}
			index += atom.getLength();
		}
		return codes;
	}

	@Override
	public PositionAtom createPosition(int offset) {
		List<SegmentAtom> atoms = Lists.newArrayList();
		atoms.addAll(getAtomsForRange(0, offset));
		PositionAtom position = new PositionAtom(this);
		atoms.add(position);
		atoms.addAll(getAtomsForRange(offset, getLength()));
		setAtoms(atoms);
		clearEnrichIndexMapping();
		return position;
	}

	@Override
	public void replaceSelection(int selectionStart, int selectionEnd,
			SegmentVariantSelection rsv) {

		BaseSegmentVariant sv = (BaseSegmentVariant) rsv.getVariant(); 
		List<SegmentAtom> replaceAtoms = sv.getAtomsForRange(
				rsv.getSelectionStart(),
				rsv.getSelectionEnd() - rsv.getSelectionStart());

        replaceSelection(selectionStart, selectionEnd, replaceAtoms);
        
    }

	@Override
	public void replaceSelection(int selectionStart, int selectionEnd, List<? extends SegmentAtom> atoms) {
		if (selectionStart == selectionEnd && atoms.isEmpty()) {
			// No-op
			return;
		}
		List<SegmentAtom> newAtoms = Lists.newArrayList();
		newAtoms.addAll(getAtomsForRange(0, selectionStart));
		newAtoms.addAll(atoms);
		newAtoms.addAll(getAtomsForRange(selectionEnd, getLength()));
		setAtoms(mergeNeighboringTextAtoms(newAtoms));
        dirty = true;
        clearEnrichIndexMapping();

//		// Clean up codes that may be duplicates
//		Set<String> codeIds = new HashSet<String>();
//		List<SegmentAtom> cleanedAtoms = Lists.newArrayList();
//		// Strip any atoms that exist twice
//		for (SegmentAtom atom : newAtoms) {
//			if (atom instanceof CodeAtom) {
//				String id = ((CodeAtom) atom).getId();
//				if (!codeIds.contains(id)) {
//					codeIds.add(id);
//					cleanedAtoms.add(atom);
//				}
//			} else {
//				cleanedAtoms.add(atom);
//			}
//		}
//		// Append any atoms that were deleted
//		List<CodeAtom> originalCodes = findCodes(getAtoms());
//		for (CodeAtom code : originalCodes) {
//			if (!codeIds.contains(code.getId())) {
//				cleanedAtoms.add(code);
//			}
//		}
//		setAtoms(cleanedAtoms);
	}
	
	private void clearEnrichIndexMapping(){
		enrichIndexMapping = null;
	}

    @Override
    public void clearSelection(int selectionStart, int selectionEnd) {
        replaceSelection(selectionStart, selectionEnd, Collections.<SegmentAtom> emptyList());
    }

    @Override
    public boolean needsValidation() {
        return dirty;
    }

    @Override
    public boolean validateAgainst(SegmentVariant sv) {
        List<CodeAtom> theseCodes = findCodes(getAtoms());
        List<CodeAtom> thoseCodes = findCodes(sv.getAtoms());
        if (theseCodes.size() != thoseCodes.size()) {
            return false;
        }
        Set<String> codeIds = new HashSet<String>();
        for (CodeAtom code : theseCodes) {
            codeIds.add(code.getId());
        }
        for (CodeAtom code : thoseCodes) {
            if (!codeIds.contains(code.getId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<CodeAtom> getMissingTags(SegmentVariant sv) {
        List<CodeAtom> theseCodes = findCodes(getAtoms());
        List<CodeAtom> thoseCodes = findCodes(sv.getAtoms());
        
        List<CodeAtom> missing = Lists.newArrayList();
        for (CodeAtom code : thoseCodes) {
            if (!theseCodes.contains(code)) {
                missing.add(code);
            }
        }
        return missing;
    }

	/**
	 * Modify the text contents of the segment; Assumes checks for attempting to
	 * change parts of the segment's unmodifiable text (such as protected codes)
	 * was already made.
	 * 
	 * @param insertCharacterOffset
	 *            - Character position indexed from the beginning of the segment
	 *            to start inserting text
	 * @param charsToReplace
	 *            - Number of characters to delete in the original segment text
	 *            starting from the {@code insertionOffset}
	 * @param newText
	 *            - String to insert at the {@code insertionOffset}; Swing sets
	 *            to {@code null} if no text to insert
	 */
	@Override
	public void modifyChars(int insertCharacterOffset, int charsToReplace,
			String newText) {
		int caretPosition = 0;
		List<SegmentAtom> atoms = getAtoms();
		List<SegmentAtom> newAtoms = Lists.newArrayList();
		boolean done = false;
		boolean insertingText = newText != null;

		for (SegmentAtom atom : atoms) {
			Range<Integer> atomCharacterRange = Range.closedOpen(caretPosition,
					caretPosition + atom.getLength());
			if (atomCharacterRange.contains(insertCharacterOffset) && !done) {
				if (atom instanceof CodeAtom) {
					// Assume inserting at the start of a CodeAtom;
					// append handled by next atom (after for-loop if last atom)
					if (insertingText) {
						// Ignore incrementing caret for newText; delete/replace
						// based off of original text.
						newAtoms.add(new TextAtom(newText));
					}
					newAtoms.add(atom);
					done = true;

				} else if (atom instanceof TextAtom) {
					String origAtomText = atom.getData();

					int atomCharInsertionIndex = Math.max(insertCharacterOffset
							- caretPosition, 0);
					newAtoms.add(new TextAtom(origAtomText.substring(0,
							atomCharInsertionIndex)));

					if (insertingText) {
						// Ignore incrementing caret for newText; delete/replace
						// based off of original text.
						newAtoms.add(new TextAtom(newText));
					}

					// Ignore decreasing caret for removing text; delete/replace
					// based off of original text.
					newAtoms.add(new TextAtom(origAtomText
							.substring(atomCharInsertionIndex + charsToReplace)));
					if (atomCharInsertionIndex + charsToReplace > atom
							.getLength()) {
						insertCharacterOffset = atomCharacterRange
								.upperEndpoint();
						charsToReplace -= (atom.getLength() - atomCharInsertionIndex);
					} else {
						done = true;
					}
				}

			} else {
				newAtoms.add(atom);
			}

			caretPosition += atom.getLength();
		}
		// Check for appending text to the end of the segment (no delete or
		// replace)
		if (caretPosition == insertCharacterOffset) {
			newAtoms.add(new TextAtom(newText));
		}

		setAtoms(mergeNeighboringTextAtoms(newAtoms));
		clearEnrichIndexMapping();
	}

	/**
	 * Prevent unnecessary SegmentAtom text fragmentation.
	 */
	private List<SegmentAtom> mergeNeighboringTextAtoms(
			List<SegmentAtom> segmentAtoms) {
		LinkedList<SegmentAtom> defraggedAtoms = new LinkedList<SegmentAtom>();
		for (SegmentAtom atom : segmentAtoms) {
			if (atom instanceof TextAtom && !defraggedAtoms.isEmpty()
					&& defraggedAtoms.getLast() instanceof TextAtom) {
				TextAtom mergedTextAtom = new TextAtom(defraggedAtoms.getLast()
						.getData() + atom.getData());

				defraggedAtoms.removeLast();
				defraggedAtoms.add(mergedTextAtom);

			} else {
				defraggedAtoms.add(atom);
			}
		}
		return defraggedAtoms;
	}

	private List<CodeAtom> findCodes(List<SegmentAtom> atoms) {
		List<CodeAtom> codes = Lists.newArrayList();
		for (SegmentAtom atom : atoms) {
			if (atom instanceof CodeAtom) {
				codes.add((CodeAtom) atom);
			}
		}
		return codes;
	}
	
	public boolean isEnriched() {
		return enrichments != null && !enrichments.isEmpty();
	}

	public void clearHighlightedText() {
		highlightDataList = null;
		currentHighlightedIndex = -1;
		clearHighlightedTextInAtoms();
	}
	public boolean isFremeSuccess(){
		return fremeSuccess;
	}
	
	public void setFremeSuccess(boolean success){
		this.fremeSuccess = success;
	}
	
	private void clearHighlightedTextInAtoms() {
		
		if(getAtoms() != null ){
			for(SegmentAtom atom: getAtoms()){
				if(atom instanceof TextAtom){
					((TextAtom)atom).clearHighlights();
				}
			}
		}
	}

	public void setHighlightDataList(List<HighlightData> highlightDataList) {
		this.highlightDataList = highlightDataList;
		clearHighlightedTextInAtoms();
		setAtomsHighlightedText();
	}
	
	public void setAtomsHighlightedText() {
		
		final List<SegmentAtom> segmentAtoms = getAtoms();
		if (segmentAtoms != null && highlightDataList != null) {
			HighlightData hd = null;
			TextAtom txtAtom = null;
			for(int i = 0; i<highlightDataList.size(); i++){
				hd = highlightDataList.get(i);
				if (hd.getAtomIndex() < segmentAtoms.size()
						&& segmentAtoms.get(hd.getAtomIndex()) instanceof TextAtom) {
					txtAtom = (TextAtom) segmentAtoms.get(hd.getAtomIndex());
					HighlightBoundaries hb  = new HighlightBoundaries(hd
							.getHighlightIndices()[0], hd
							.getHighlightIndices()[1]);
					txtAtom.addHighlightBoundary(hb);
					if(currentHighlightedIndex == i){
						txtAtom.setCurrentHLBoundaryIdx(txtAtom.getHighlightBoundaries().indexOf(hb));
					}
				}
			}
		}
	}
    
    public List<HighlightData> getHighlightDataList(){
    	return highlightDataList;
    }
    
    public void addHighlightData(HighlightData highlightData){
    	//TODO TRY TO REMOVE
    	if(highlightDataList == null){
    		highlightDataList = new ArrayList<HighlightData>(); 
    	}
    	highlightDataList.add(highlightData);
    	// END 
    	
    	TextAtom hAtom = findHighlightedAtom(highlightData.getAtomIndex());
    	if(hAtom != null){
    		hAtom.addHighlightBoundary(new HighlightBoundaries(
					highlightData.getHighlightIndices()[0],
					highlightData.getHighlightIndices()[1]));
    	}
    }
    
    public void removeHighlightData(int atomIndex, int startIndex, int endIndex){
    	
    	HighlightData hdToDelete = null;
    	if(highlightDataList != null){
    		for(HighlightData hd: highlightDataList){
    			if(hd.getAtomIndex() == atomIndex && hd.getHighlightIndices()[0] == startIndex && hd.getHighlightIndices()[1] == endIndex){
    				hdToDelete = hd;
    				break;
    			}
    		}
    		highlightDataList.remove(hdToDelete);
    	}
    	TextAtom hAtom = findHighlightedAtom(atomIndex);
    	if(hAtom != null){
    		hAtom.removeHighlighBoundary(startIndex, endIndex);
    	}
    	
    }
    
    public TextAtom findHighlightedAtom(int atomIndex){
    	
    	TextAtom hAtom = null;
    	if(getAtoms() != null && atomIndex < getAtoms().size()){
    		SegmentAtom atom = getAtoms().get(atomIndex);
    		if(atom instanceof TextAtom){
    			hAtom = (TextAtom) atom;
    		}
    	}
    	return hAtom;
    }

	public void setCurrentHighlightedIndex(int currentHighlightedIndex) {
		this.currentHighlightedIndex = currentHighlightedIndex;

		if (getAtoms() != null) {
			if (currentHighlightedIndex > -1) {
				HighlightData hd = highlightDataList
						.get(currentHighlightedIndex);
				if (hd.getAtomIndex() < getAtoms().size()
						&& getAtoms().get(hd.getAtomIndex()) instanceof TextAtom) {
					TextAtom hAtom = (TextAtom) getAtoms().get(
							hd.getAtomIndex());
					if (hAtom.getHighlightBoundaries() != null) {
						HighlightBoundaries hb = null;
						for (int i = 0; i < hAtom.getHighlightBoundaries()
								.size(); i++) {
							hb = hAtom.getHighlightBoundaries().get(i);
							if (hd.getHighlightIndices()[0] == hb
									.getFirstIndex()
									&& hd.getHighlightIndices()[1] == hb
											.getLastIndex()) {
								hAtom.setCurrentHLBoundaryIdx(i);
								break;
							}
						}
					}

				}
			} else {
				for(SegmentAtom atom: getAtoms()){
					if(atom instanceof TextAtom) {
						((TextAtom)atom).setCurrentHLBoundaryIdx(-1);
					}
				}
			}
		}
	}

    public int getCurrentHighlightedIndex(){
    	return currentHighlightedIndex;
    }


//	public void setEnriched(final boolean enriched) {
//		this.enriched = enriched;
//	}

	public void setSentToFreme(boolean sentToFreme){
		this.sentToFreme = sentToFreme;
	}
	
	public boolean isSentToFreme(){
		return sentToFreme;
	}
	
    public void replaced(String newString){
    	
    	if(highlightDataList != null){
    		replaceTextInAtoms(newString);
    		updateHlDataInCurrentAtomAfterReplace(newString);
    	}
    }
    
    private void replaceTextInAtoms(String newString){
    	
    	HighlightData hd = highlightDataList.get(currentHighlightedIndex);
    	final List<SegmentAtom> segmentAtoms = getAtoms();
		if (segmentAtoms != null && hd.getAtomIndex() < segmentAtoms.size()
				&& segmentAtoms.get(hd.getAtomIndex()) instanceof TextAtom) {
			TextAtom currAtom = (TextAtom) segmentAtoms.get(hd
					.getAtomIndex());
			if (currAtom.getHighlightBoundaries() != null) {
				HighlightBoundaries currHb = currAtom
						.getHighlightBoundaries().get(
								currAtom.getCurrentHLBoundaryIdx());
				if (currAtom.getCurrentHLBoundaryIdx() < currAtom
						.getHighlightBoundaries().size() - 1) {
					int currHbIndex = currAtom.getCurrentHLBoundaryIdx() + 1;
					int delta = newString.length()
							- (currHb.getLastIndex() - currHb
									.getFirstIndex());
					HighlightBoundaries nextHb = null;
					while (currHbIndex < currAtom.getHighlightBoundaries()
							.size()) {
						nextHb = currAtom.getHighlightBoundaries().get(
								currHbIndex++);
						nextHb.setFirstIndex(nextHb.getFirstIndex() + delta);
						nextHb.setLastIndex(nextHb.getLastIndex() + delta);
					}
				}
				currAtom.getHighlightBoundaries().remove(currHb);
				currAtom.setCurrentHLBoundaryIdx(-1);
			}
		}
    }
    
    private void updateHlDataInCurrentAtomAfterReplace(String newString){
    	
    	HighlightData replacedHd = highlightDataList
				.get(currentHighlightedIndex);
		if (currentHighlightedIndex < highlightDataList.size() - 1) {
			HighlightData nextHd = null;
			int hdIndex = currentHighlightedIndex + 1;
			int delta = newString.length()
					- (replacedHd.getHighlightIndices()[1] - replacedHd
							.getHighlightIndices()[0]);
			while (hdIndex < highlightDataList.size()) {
				nextHd = highlightDataList.get(hdIndex++);
				if (replacedHd.getAtomIndex() == nextHd.getAtomIndex()) {
					
					int[] newHLIndices = {
							nextHd.getHighlightIndices()[0] + delta,
							nextHd.getHighlightIndices()[1] + delta };
					nextHd.setHighlightIndices(newHLIndices);
				}
			}
		}
		highlightDataList.remove(currentHighlightedIndex);
		currentHighlightedIndex = -1;
    }
    
    
	public Set<Enrichment> getEnirchments() {
		return enrichments;
	}

	public void setEnrichments(Set<Enrichment> enrichments) {
		this.enrichments = null;
		if(enrichments != null){
			for(Enrichment enrich: enrichments){
				addEnrichment(enrich);
			}
		}
	}

	public void addEnrichment(final Enrichment enrichment){
        if(enrichment != null){
            if(enrichments == null){
                enrichments = new HashSet<Enrichment>();
            }
            if(enrichment.getType().equals(Enrichment.TRANSLATION_TYPE)){
            	this.transEnrichment = (TranslationEnrichment) enrichment;
            } else {
            	enrichments.add(enrichment);
            	adjustOffsets(enrichment);
            }
        }
    }
	
	private void adjustOffsets(final Enrichment enrichment ){
		if(enrichIndexMapping == null){
			buildIndexMapping();
		}
		enrichment.setOffsetNoTagsStartIdx(enrichIndexMapping[enrichment.getOffsetStartIdx()]);
		enrichment.setOffsetNoTagsEndIdx(enrichIndexMapping[enrichment.getOffsetEndIdx()]);
	}
	
	
	public String getPlainText(){
		
		StringBuilder plainTextSb = new StringBuilder();
		for(SegmentAtom atom: getAtoms()){
			if(atom instanceof TextAtom){
				plainTextSb.append(atom.getData());
			}
		}
		
		return plainTextSb.toString();
	}
	
	private void buildIndexMapping(){
		String enrichedText = getDisplayText();
		String plainText = getPlainText();
		enrichIndexMapping = new Integer[enrichedText.length() + 1];
		if(enrichedText.equals(plainText)){
			for(int i = 0; i<enrichIndexMapping.length; i++){
				enrichIndexMapping[i] = i;
			}
		} else {
			int tagIdx = 0;
			int noTagIdx = 0;
			boolean inTag = false;
			while (tagIdx < enrichedText.length() && noTagIdx < plainText.length()) {
				if( !inTag && (enrichedText.charAt(tagIdx) == plainText.charAt(noTagIdx)) ){
					enrichIndexMapping[tagIdx] = noTagIdx++;
				} else if (enrichedText.charAt(tagIdx) == '<'){
					inTag = true;
					enrichIndexMapping[tagIdx] = noTagIdx;
				} else if (enrichedText.charAt(tagIdx) == '>'){
					inTag = false;
				}
				tagIdx++;
			}
			enrichIndexMapping[tagIdx] = noTagIdx;
			
		}
		
	}
	
    
    public void addEnrichmentList(final List<Enrichment> enrichmentList){
    	if(enrichmentList != null){
    		for(Enrichment enrich: enrichmentList){
    			addEnrichment(enrich);
    		}
    	}
    }
    
    
    public void clearEnrichments(){
    	
    	sentToFreme = false;
    	fremeSuccess = false;
//    	enriched = false;
    	enrichments = null;
    	transEnrichment = null;
    }
    
    public TranslationEnrichment getTranslationEnrichment(){
    	
    	return transEnrichment;
    }
    
    public void setTripleModel(Model model){
    	this.tripleModel = model;
    }
    
    public Model getTripleModel(){
    	return tripleModel;
    }

    @Override
    public String toString() {
        return getDisplayText();
    }
}