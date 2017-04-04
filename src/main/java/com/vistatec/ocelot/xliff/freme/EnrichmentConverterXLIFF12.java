package com.vistatec.ocelot.xliff.freme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.okapi.common.ISkeleton;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;

import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;
import com.vistatec.ocelot.segment.model.okapi.TextContainerVariant;

/**
 * This class provides methods for converting XLIFF 1.2 tags to enrichments.
 */
public class EnrichmentConverterXLIFF12 extends EnrichmentConverter {

	/**
	 * Constructor.
	 * 
	 * @param sourceLang
	 *            the XLIFF file source language
	 * @param targetLang
	 *            the XLIFF file target language
	 */
	public EnrichmentConverterXLIFF12(String sourceLang, String targetLang) {

		super(sourceLang, targetLang, LoggerFactory
		        .getLogger(EnrichmentConverterXLIFF12.class));
	}

	private boolean hasAnnotations(Code code) {
		return (code.getGenericAnnotations() != null && code.getGenericAnnotations().size() > 0);
	}

	/**
	 * Retrieves enrichments from a specific XLIFF 1.2 text unit. Codes embedded
	 * into the text unit and the skeleton are inspected. The codes representing
	 * enrichments are then deleted.
	 * 
	 * @param textContainer
	 *            the text container
	 * @param textUnit
	 *            the text unit.
	 * @return the list of retrieved enrichments
	 */
	public List<Enrichment> retrieveEnrichments(TextContainer textContainer,
	        ITextUnit textUnit , String language) {

		List<Enrichment> enrichments = new ArrayList<Enrichment>();
		if (textContainer != null) {
			TextContainerVariant variant = new TextContainerVariant(textContainer);
			StringBuilder wholeText = new StringBuilder();
			List<Code> codesToRemove = new ArrayList<Code>();
			List<Enrichment> currEnrichments = new ArrayList<Enrichment>();
			int index = -1;
			for (TextPart part : textContainer.getParts()) {
				TextFragment text = part.getContent();
				String codedText = text.getCodedText();
				Code openingCode = null;
				for (int i = 0; i < codedText.length(); i++) {
					switch (text.charAt(i)) {
					case TextFragment.MARKER_OPENING:
						index = TextFragment.toIndex(codedText.charAt(++i));
						openingCode = part.getContent().getCodes().get(index);
						if (hasAnnotations(openingCode)) {
							currEnrichments.addAll(convertAnnots2Enrichments(
										openingCode, wholeText.length()));

							// all the annotations from this code have been
							// translated to enrichments.
							// the code must be removed
							if (!hasAnnotations(openingCode)) {
								codesToRemove.add(openingCode);
							}
						} else {
							wholeText.append(variant.getCodeText(openingCode, false));
						}
						break;
					case TextFragment.MARKER_CLOSING:
						index = TextFragment.toIndex(codedText.charAt(++i));
						Code code = part.getContent().getCodes().get(index);
						if (hasAnnotations(code)) {
							// check annotations in the closing code.
							manageClosingCodeAnnots(code, openingCode);
							if (code.getGenericAnnotations() == null || code.getGenericAnnotations().size() == 0) {
								codesToRemove.add(code);
							}
						} else {
							wholeText.append(variant.getCodeText(code, false));
						}
						// update end index for all current enrichments.
						for (Enrichment enrich : currEnrichments) {
							enrich.setOffsetEndIdx(wholeText.length());
						}
						enrichments.addAll(currEnrichments);
						currEnrichments.clear();
						break;
					case TextFragment.MARKER_ISOLATED:
						index = TextFragment.toIndex(codedText.charAt(++i));
						Code placeHolderCode = part.getContent().getCodes().get(index);
						wholeText.append(variant.getCodeText(placeHolderCode, false));
						break;
					default:
						wholeText.append(text.charAt(i));
						break;
					}
				}
				for (Code code : codesToRemove) {
					text.removeCode(code);
				}
			}
		}
		// retrieve all triple enrichments.
		enrichments.addAll(retrieveTriplesEnrichments(textUnit.getSkeleton(),
		        enrichments, language));
		return enrichments;
	}

	/**
	 * Converts all the annotations included in a code to proper enrichments.
	 * 
	 * @param code
	 *            the code
	 * @param startOffsetIndex
	 *            the offset start index for the enrichments to be creaed.
	 * @return the list of created enrichments.
	 */
	private List<Enrichment> convertAnnots2Enrichments(Code code,
	        int startOffsetIndex) {

		List<Enrichment> enrichments = new ArrayList<Enrichment>();
		if (code.getGenericAnnotations() != null) {
			Iterator<GenericAnnotation> annotationsIt = code
			        .getGenericAnnotations().iterator();
			List<GenericAnnotation> annotationsToDelete = new ArrayList<GenericAnnotation>();
			String entityAnnotator = null;
			String termAnnotator = null;
			GenericAnnotation annotation = null;
			while (annotationsIt.hasNext()) {
				annotation = annotationsIt.next();
				switch (annotation.getType()) {
				// Entity Enrichment
				case GenericAnnotationType.TA:
					if (annotation.getString(GenericAnnotationType.TA_IDENT) != null) {
						EntityEnrichment enrichment = convertAnnotation2EntityEnrichment(
						        annotation, entityAnnotator, startOffsetIndex);
						enrichments.add(enrichment);
						annotationsToDelete.add(annotation);
					}
					break;
				// Terminology Enrichment
				case GenericAnnotationType.TERM:
					TerminologyEnrichment termEnrichment = convertAnnotation2TerminologyEnrichment(
					        annotation, termAnnotator, startOffsetIndex, code);
					enrichments.add(termEnrichment);
					annotationsToDelete.add(annotation);
					break;
				// Annotators Ref
				case GenericAnnotationType.ANNOT:
					if (annotation.getString(GenericAnnotationType.ANNOT_VALUE) != null) {
						String annotValue = annotation
						        .getString(GenericAnnotationType.ANNOT_VALUE);
						if (annotValue != null) {
							if (annotValue
							        .contains(EnrichmentAnnotationsConstants.TERM_ANNOTATORS_REF_STRING)) {

								termAnnotator = manageTermAnnotatorsRef(
								        annotValue, enrichments);
								annotationsToDelete.add(annotation);
							}
							if (annotValue
							        .contains(EnrichmentAnnotationsConstants.TA_ANNOTATORS_REF_STRING)) {
								entityAnnotator = manageTAAnnotatorsRef(
								        annotValue, enrichments);
								annotationsToDelete.add(annotation);
							}
						}
					}
					break;
				default:
					break;
				}
			}
			for (GenericAnnotation annot : annotationsToDelete) {
				code.getGenericAnnotations().remove(annot);
			}
		}
		return enrichments;
	}

	/**
	 * Converts the annotation to an Entity Enrichment.
	 * 
	 * @param annotation
	 *            the annotation
	 * @param entityAnnotator
	 *            the entity annotator
	 * @param startOffsetIndex
	 *            the enrichment offset start index
	 * @return the Entity Enrichment.
	 */
	private EntityEnrichment convertAnnotation2EntityEnrichment(
	        GenericAnnotation annotation, String entityAnnotator,
	        int startOffsetIndex) {

		String value = annotation.getString(GenericAnnotationType.TA_IDENT);
		if (value.startsWith(GenericAnnotationType.REF_PREFIX)) {
			value = getRefString(value);
		}
		logger.debug("Found an Entity Enrichment with value \"{}\".", value);
		EntityEnrichment enrichment = new EntityEnrichment(value);
		enrichment.setOffsetStartIdx(startOffsetIndex);
		if (entityAnnotator != null) {
			enrichment.setAnnotatorRef(entityAnnotator);
		}
		return enrichment;
	}

	/**
	 * Converts the annotation to a Terminology Enrichemnt.
	 * 
	 * @param annotation
	 *            the annotation
	 * @param termAnnotator
	 *            the term annotator
	 * @param startOffsetIndex
	 *            the enrichment offset start index
	 * @param code
	 *            the code
	 * @return the terminology enrichment.
	 */
	private TerminologyEnrichment convertAnnotation2TerminologyEnrichment(
	        GenericAnnotation annotation, String termAnnotator,
	        int startOffsetIndex, Code code) {

		TerminologyEnrichment termEnrichment = new TerminologyEnrichment();
		termEnrichment.setOffsetStartIdx(startOffsetIndex);
		if (termAnnotator != null) {
			termEnrichment.setAnnotator(termAnnotator);
		}
		if (code.getOuterData() != null) {
			int index = code.getOuterData().indexOf("ref=\"")
			        + "ref=\"".length();
			if (index != -1) {
				String infoRef = code.getOuterData().substring(index);
				int endIndex = infoRef.indexOf("\"");
				infoRef = infoRef.substring(0, endIndex);
				termEnrichment.setTermInfoRef(infoRef);
			}
		}
		return termEnrichment;
	}

	/**
	 * Gets the referenced value for a specific string of the type "REF:...".
	 * 
	 * @param refValue
	 *            the reference value string
	 * @return the referenced value.
	 */
	private String getRefString(String refValue) {

		String retString = refValue;
		int refIdx = refValue.indexOf(GenericAnnotationType.REF_PREFIX);
		if (refIdx != -1) {
			retString = refValue.substring(refIdx
			        + GenericAnnotationType.REF_PREFIX.length());
		}
		return retString;
	}

	/**
	 * Manages the text-analysis annotators ref. This is the case the annotators
	 * ref value is "text-analysis|...".
	 * 
	 * @param annotValue
	 *            the annotation value
	 * @param enrichments
	 *            the list of enrichments.
	 * @return the text-analysis annotator ref.
	 */
	private String manageTAAnnotatorsRef(String annotValue,
	        List<Enrichment> enrichments) {

		int termIdx = annotValue
		        .indexOf(EnrichmentAnnotationsConstants.TA_ANNOTATORS_REF_STRING);
		int endIndex = annotValue.indexOf(" ", termIdx);
		if (endIndex == -1) {
			endIndex = annotValue.length();
		}
		String entityAnnotator = annotValue
		        .substring(
		                termIdx
		                        + EnrichmentAnnotationsConstants.TA_ANNOTATORS_REF_STRING
		                                .length(), endIndex);
		List<Enrichment> entityEnrichments = findEnrichments(
		        Enrichment.ENTITY_TYPE, enrichments);
		if (entityEnrichments != null) {
			for (Enrichment entityEnrich : entityEnrichments) {
				((EntityEnrichment) entityEnrich)
				        .setAnnotatorRef(entityAnnotator);
			}
		}
		return entityAnnotator;
	}

	/**
	 * Manages the Term annotators ref. This is the case the annotators ref
	 * value is "terminology|...".
	 * 
	 * @param annotValue
	 *            the annotation value
	 * @param enrichments
	 *            the list of enrichments
	 * @return the terminology annotators ref.
	 */
	private String manageTermAnnotatorsRef(String annotValue,
	        List<Enrichment> enrichments) {
		int termIdx = annotValue
		        .indexOf(EnrichmentAnnotationsConstants.TERM_ANNOTATORS_REF_STRING);
		int endIndex = annotValue.indexOf(" ", termIdx);
		if (endIndex == -1) {
			endIndex = annotValue.length();
		}
		String termAnnotator = annotValue
		        .substring(
		                termIdx
		                        + EnrichmentAnnotationsConstants.TERM_ANNOTATORS_REF_STRING
		                                .length(), endIndex);
		List<Enrichment> termEnrichments = findEnrichments(
		        Enrichment.TERMINOLOGY_TYPE, enrichments);
		if (termEnrichments != null) {
			for (Enrichment termEnrich : termEnrichments) {
				((TerminologyEnrichment) termEnrich)
				        .setAnnotator(termAnnotator);
			}
		}
		return termAnnotator;
	}

	/**
	 * Finds all the enrichments of a specific type among a list of enrichments.
	 * 
	 * @param type
	 *            the type
	 * @param enrichments
	 *            the list of enrichments
	 * @return the list of enrichments of the specified type.
	 */
	private List<Enrichment> findEnrichments(String type,
	        List<Enrichment> enrichments) {

		List<Enrichment> retEnrichments = new ArrayList<Enrichment>();
		if (enrichments != null) {
			for (Enrichment currEnrich : enrichments) {
				if (currEnrich.getType().equals(type)) {
					retEnrichments.add(currEnrich);
				}
			}
		}
		return retEnrichments;
	}

	/**
	 * Checks if the annotations in the closing code are still listed among the
	 * annotations of the related opening code. An annotation is deleted if it
	 * is not included in the opening code.
	 * 
	 * @param closingCode
	 *            the closing code.
	 * @param openingCode
	 *            the opening code.
	 */
	private void manageClosingCodeAnnots(Code closingCode, Code openingCode) {

		if (closingCode.getGenericAnnotations() != null) {
			List<GenericAnnotation> annotToDelete = new ArrayList<GenericAnnotation>();
			Iterator<GenericAnnotation> annotIt = closingCode
			        .getGenericAnnotations().iterator();
			GenericAnnotation annot = null;
			while (annotIt.hasNext()) {
				annot = annotIt.next();
				if (!existAnnotInCode(openingCode, annot)) {
					annotToDelete.add(annot);
				}
			}
			for (GenericAnnotation annotation : annotToDelete) {
				closingCode.getGenericAnnotations().remove(annotation);
			}
		}
	}

	/**
	 * Checks if an annotation exists in a specific code.
	 * 
	 * @param code
	 *            the code
	 * @param annot
	 *            the annotation
	 * @return <code>true</code> if the annotation exists; <code>false</code>
	 *         otherwise.
	 */
	private boolean existAnnotInCode(Code code, GenericAnnotation annot) {

		boolean exist = false;
		if (code.getGenericAnnotations() != null) {
			Iterator<GenericAnnotation> annotIt = code.getGenericAnnotations()
			        .iterator();
			GenericAnnotation currAnnot = null;
			while (annotIt.hasNext() && !exist) {
				currAnnot = annotIt.next();
				if (currAnnot.getType().equals(annot.getType())) {
					exist = true;
				}
			}
		}
		return exist;
	}

	/**
	 * Retrieves triple enrichments from the skeleton. The skeleton in XLIFF 1.2
	 * file includes a <code>&lt;ex:json-ld&gt;</code> tag containing the
	 * JSON-LD triples. Triple enrichments are those enrichments having info
	 * stored in the JSON triples (Link and Terminology enrichments).
	 * 
	 * @param skeleton
	 *            the skeleton
	 * @param enrichments
	 *            all the enrichments found so far.
	 * @return the complete list of enrichments.
	 */
	private List<Enrichment> retrieveTriplesEnrichments(ISkeleton skeleton,
	        List<Enrichment> enrichments, String language) {
		List<Enrichment> triplesEnrichments = new ArrayList<Enrichment>();
		if (skeleton != null) {
			String skelString = skeleton.toString();
			int jsonStartIdx = skelString
			        .indexOf(EnrichmentAnnotationsConstants.JSON_TAG_NAME);
			if (jsonStartIdx != -1) {
				jsonStartIdx = skelString.indexOf(">", jsonStartIdx) + 1;
				String stringJson = skelString.substring(jsonStartIdx);
				int endJsonIdx = stringJson.indexOf("</"
				        + LinkEnrichment.MARKER_TAG);
				stringJson = stringJson.substring(0, endJsonIdx);
				triplesEnrichments.addAll(retrieveTriplesEnrichments(
				        stringJson, enrichments, language));
			}
		}

		return triplesEnrichments;

	}

}
