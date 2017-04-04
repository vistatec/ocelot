package com.vistatec.ocelot.xliff.freme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.lib.xliff2.core.ExtContent;
import net.sf.okapi.lib.xliff2.core.ExtElement;
import net.sf.okapi.lib.xliff2.core.ExtElements;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.MTag;
import net.sf.okapi.lib.xliff2.core.Tag;
import net.sf.okapi.lib.xliff2.core.Unit;
import net.sf.okapi.lib.xliff2.its.DataCategory;
import net.sf.okapi.lib.xliff2.its.TermTag;
import net.sf.okapi.lib.xliff2.its.TextAnalysis;

import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;
import com.vistatec.ocelot.segment.model.okapi.FragmentVariant;
import com.vistatec.ocelot.segment.model.okapi.TaggedCodeAtom;

/**
 * This class provides methods for converting XLIFF 2.0 tags to enrichments.
 */
public class EnrichmentConverterXLIFF20 extends EnrichmentConverter {

	/**
	 * Constructor.
	 * 
	 * @param sourceLang
	 *            the XLIFF file source language.
	 * @param targetLang
	 *            the XLIFF file target language.
	 */
	public EnrichmentConverterXLIFF20(String sourceLang, String targetLang) {

		super(sourceLang, targetLang, LoggerFactory
		        .getLogger(EnrichmentConverterXLIFF20.class));
	}

	/**
	 * Retrieves the enrichments from a XLIFF 2.0 unit.
	 * 
	 * @param unit
	 *            the unit
	 * @param fragment
	 *            the current involved fragment
	 * @return the list of enrichments for this unit.
	 */
	public List<Enrichment> retrieveEnrichments(Unit unit, Fragment fragment, String language, String segmentId) {

		List<Enrichment> enrichments = new ArrayList<Enrichment>();
		if (fragment != null) {
			FragmentVariant fragVar = new FragmentVariant(fragment, false);
			StringBuilder wholeText = new StringBuilder();
			List<EnrichmentWrapper> currEnrichments = new ArrayList<EnrichmentWrapper>();
			List<Integer> codePositionToRemove = new ArrayList<Integer>();
			List<DataCategory> dataCategoryToDelete = new ArrayList<DataCategory>();
			List<Tag> tagsToRemove = new ArrayList<Tag>();
			String termAnnotator = null;
			if (unit.getAnnotatorsRef() != null) {
				termAnnotator = unit.getAnnotatorsRef().get("terminology");
			}
			String codedText = fragment.getCodedText();
			for (int textIdx = 0; textIdx < codedText.length(); textIdx++) {
				switch (codedText.charAt(textIdx)) {
				case Fragment.MARKER_OPENING:
					MTag tag = fragment.getMTag(codedText, textIdx);
					if (tag != null && (tag.hasITSItem() || tag instanceof TermTag)) {
						manageMarkerOpeningXliff2_0(tag, currEnrichments,
						        dataCategoryToDelete, wholeText.toString(),
						        termAnnotator);
						if (!tag.hasITSItem()) {
							tagsToRemove.add(tag);
							codePositionToRemove.add(textIdx++);
							codePositionToRemove.add(textIdx);
						} else {
							TaggedCodeAtom atom = findCodeAtom(fragVar, tag);
							if(atom != null){
								wholeText.append(atom.getData());
								textIdx++;
							}
						}
					}  else {
						if(manageCode(tag, fragVar, wholeText)){
							textIdx++;
						}
					}
					break;
				case Fragment.MARKER_CLOSING:
					tag = fragment.getMTag(codedText, textIdx);
					EnrichmentWrapper enrichmentWrapper = findEnrichmentByTagId(
					        tag.getId(), currEnrichments);
					if (enrichmentWrapper != null ) {
						enrichmentWrapper.getEnrichment().setOffsetEndIdx(
						        wholeText.length());
						currEnrichments.remove(enrichmentWrapper);
						enrichments.add(enrichmentWrapper.getEnrichment());
						if (tag.getITSItems() != null
						        && !tag.getITSItems().isEmpty()) {
							for (DataCategory annot : dataCategoryToDelete) {
								if (annot.equals(tag.getITSItems().get(
								        annot.getClass()))) {
									tag.getITSItems().remove(annot);
								}
							}
						}
						if (!tag.hasITSItem()) {
							tagsToRemove.add(tag);
							codePositionToRemove.add(textIdx++);
							codePositionToRemove.add(textIdx);
						}
					} else {
						if(manageCode(tag, fragVar, wholeText)){
							textIdx++;
						}
					}
					break;
				case Fragment.CODE_OPENING:
				case Fragment.CODE_CLOSING:
				case Fragment.CODE_STANDALONE:
					manageCode(fragment.getCTag(codedText, textIdx++), fragVar, wholeText);
					break;
				case Fragment.PCONT_STANDALONE:
					break;
				default:
					wholeText.append(codedText.charAt(textIdx));
					break;
				}
			}
			for(Tag tag: tagsToRemove){
				fragment.remove(tag);
			}
			StringBuilder newCodedText = new StringBuilder();
			int lastIndex = 0;
			for (Integer index : codePositionToRemove) {
				newCodedText.append(codedText.substring(lastIndex, index));
				lastIndex = index + 1;
			}
			newCodedText.append(codedText.substring(lastIndex));
			fragment.setCodedText(newCodedText.toString());
			enrichments.addAll(retrieveTriplesEnrichments(
			        unit.getExtElements(), enrichments, language, segmentId));
		}
		return enrichments;
	}

	private boolean manageCode(Tag code, FragmentVariant variant, StringBuilder text){
		boolean managed = false;
		TaggedCodeAtom atom = findCodeAtom(variant, code);
		if(atom != null){
			text.append(atom.getData());
			managed = true;
		}
		return managed;
	}
	
	private TaggedCodeAtom findCodeAtom(FragmentVariant variant, Tag code){
		
		TaggedCodeAtom atom = null;
		for(SegmentAtom currAtom: variant.getAtoms()){
			if(currAtom instanceof TaggedCodeAtom && ((TaggedCodeAtom)currAtom).getTag().equals(code)){
				atom = (TaggedCodeAtom) currAtom;
			}
		}
		return atom;
	}
	
	
	/**
	 * Manages an opening marker for XLIFF 2.0: depending on the type of the
	 * tag, the proper enrichment is created.
	 * 
	 * @param tag
	 *            the tag
	 * @param currEnrichments
	 *            the list of current enrichments
	 * @param dataCategoryToDelete
	 *            the list of data category to be deleted
	 * @param wholeText
	 *            the current text
	 * @param termAnnotator
	 *            the term annotator.
	 */
	private void manageMarkerOpeningXliff2_0(MTag tag,
	        List<EnrichmentWrapper> currEnrichments,
	        List<DataCategory> dataCategoryToDelete, String wholeText,
	        String termAnnotator) {
		if (tag.getITSItems() != null) {
			TextAnalysis taAnnot = (TextAnalysis) tag.getITSItems().get(
			        TextAnalysis.class);
			if (taAnnot != null) {
				if (taAnnot.getTaIdentRef() != null) {
					EntityEnrichment entityEnr = new EntityEnrichment(
					        taAnnot.getTaIdentRef());
					entityEnr.setAnnotatorRef(taAnnot.getAnnotatorRef());
					entityEnr.setOffsetStartIdx(wholeText.length());
					currEnrichments.add(new EnrichmentWrapper(entityEnr, tag
					        .getId()));
				}
				boolean canDeleteTextAnalysisAnnot = canDeleteTAAnnotation(taAnnot);
				if (canDeleteTextAnalysisAnnot) {
					tag.getITSItems().remove(taAnnot);
					dataCategoryToDelete.add(taAnnot);

				}
			}
		} else if (tag instanceof TermTag) {
			TerminologyEnrichment termEnric = new TerminologyEnrichment();
			termEnric.setOffsetStartIdx(wholeText.length());
			if (termAnnotator != null) {
				termEnric.setAnnotator(termAnnotator);
			}
			termEnric.setTermInfoRef(((TermTag) tag).getTermInfoRef());
			currEnrichments.add(new EnrichmentWrapper(termEnric, tag.getId()));
		}
	}

	/**
	 * Checks if the text-analysis annotation can be deleted.
	 * 
	 * @param taAnnot
	 *            the text-analysis annotation
	 * @return <code>true</code> if it can be deleted; <code>false</code>
	 *         otherwise
	 */
	private boolean canDeleteTAAnnotation(TextAnalysis taAnnot) {

		return (taAnnot.getTaClassRef() == null || taAnnot.getTaClassRef()
		        .isEmpty())
		        && taAnnot.getTaConfidence() == null
		        && (taAnnot.getTaIdent() == null || taAnnot.getTaIdent()
		                .isEmpty())
		        && (taAnnot.getTaSource() == null || taAnnot.getTaSource()
		                .isEmpty());
	}

	/**
	 * Finds the enrichment retrieved from a specific tag.
	 * 
	 * @param tagId
	 *            the tag ID
	 * @param enrichmentList
	 *            the list of enrichments
	 * @return the enrichment if it exists; <code>null</code> otherwise.
	 */
	private EnrichmentWrapper findEnrichmentByTagId(String tagId,
	        List<EnrichmentWrapper> enrichmentList) {

		EnrichmentWrapper enrichWrapper = null;
		if (enrichmentList != null) {
			for (EnrichmentWrapper currEnrichWrapp : enrichmentList) {
				if (currEnrichWrapp.getTagId().equals(tagId)) {
					enrichWrapper = currEnrichWrapp;
					break;
				}
			}
		}
		return enrichWrapper;
	}

	/**
	 * Retrieves the triple enrichments for a XLIFF file 2.0. The triple
	 * enrichments are those enrichments expressed in triples (link and
	 * terminology).
	 * 
	 * @param elements
	 *            the extra elements in the current XLIFF 2.0 unit
	 * @param enrichments
	 *            the list of enrichments found so far.
	 * @return the complete list of enrichments.
	 */
	private List<Enrichment> retrieveTriplesEnrichments(
	        final ExtElements elements, final List<Enrichment> enrichments, String language, String segmentId) {

		List<Enrichment> triplesEnrichments = new ArrayList<Enrichment>();
		if (elements != null) {
//			Iterator<ExtElement> elemsIt = elements.iterator();
			ExtElement elem = getExtElementForSegment(elements, segmentId);
			if(elem != null && !elem.getChildren().isEmpty()){
				if (elem.getChildren().get(0) instanceof ExtContent) {
					String jsonString = ((ExtContent) elem.getChildren()
					        .get(0)).getText();
					triplesEnrichments.addAll(retrieveTriplesEnrichments(
					        jsonString, enrichments, language));
//=======
//	        final ExtElements elements, final List<Enrichment> enrichments, String language) {
//
//		List<Enrichment> triplesEnrichments = new ArrayList<Enrichment>();
//		if (elements != null) {
//			Iterator<ExtElement> elemsIt = elements.iterator();
//			ExtElement elem = null;
//			while (elemsIt.hasNext()) {
//				elem = elemsIt.next();
//				if (elem.getQName().getPrefix().equals("ex")
//				        && elem.getQName().getLocalPart().equals("json-ld")
//				        && !elem.getChildren().isEmpty()) {
//					if (elem.getChildren().get(0) instanceof ExtContent) {
//						String jsonString = ((ExtContent) elem.getChildren()
//						        .get(0)).getText();
//						triplesEnrichments.addAll(retrieveTriplesEnrichments(
//						        jsonString, enrichments, language));
//					}
//
//>>>>>>> ldManual
				}
			}
		}

		return triplesEnrichments;
	}
	
	private ExtElement getExtElementForSegment(ExtElements elements, String segmentId){
		
		ExtElement element = null;
		Iterator<ExtElement> elemsIt = elements.iterator();
		ExtElement currElem = null;
		while(elemsIt.hasNext() && element == null){
			currElem = elemsIt.next();
			if(isExtElemForSegment(currElem, segmentId)){
				element = currElem;
			}
		}
		return element;
	}
	
	
	private boolean isExtElemForSegment(ExtElement elem, String segmentId){
		
		boolean isJsonLDNode = elem.getQName().getPrefix()
		        .equals(EnrichmentAnnotationsConstants.JSON_TAG_PREFIX)
		        && elem.getQName()
		                .getLocalPart()
		                .equals(EnrichmentAnnotationsConstants.JSON_TAG_LOCAL_NAME);

		boolean isRelatedToSegment = segmentId == null
		        || segmentId.equals(elem.getAttributes().getAttributeValue("",
		                EnrichmentAnnotationsConstants.JSON_TAG_SEG_ATTR));
		return isJsonLDNode && isRelatedToSegment;
	}
	
}

/**
 * Wrapper class for enrichments. It contains information about the enrichment
 * and the ID of the tag from which the enrichemnt has been retrieved.
 */
class EnrichmentWrapper {

	/** The enrichment. */
	private Enrichment enrichment;

	/** The tag ID. */
	private String tagId;

	/**
	 * Constructor.
	 * 
	 * @param enrichment
	 *            the enrichment
	 * @param tagId
	 *            the tag ID
	 */
	public EnrichmentWrapper(Enrichment enrichment, String tagId) {
		super();
		this.enrichment = enrichment;
		this.tagId = tagId;
	}

	/**
	 * Gets the enrichment.
	 * 
	 * @return the enrichment.
	 */
	public Enrichment getEnrichment() {
		return enrichment;
	}

	/**
	 * Sets the enrichment
	 * 
	 * @param enrichment
	 *            the enrichment
	 */
	public void setEnrichment(Enrichment enrichment) {
		this.enrichment = enrichment;
	}

	/**
	 * Gets the tag ID.
	 * 
	 * @return the tag ID.
	 */
	public String getTagId() {
		return tagId;
	}

	/**
	 * Sets the tag ID
	 * 
	 * @param tagId
	 *            the tag ID.
	 */
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

}
