package com.vistatec.ocelot.xliff.freme;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.its.model.EnrichmentMetaData;
import com.vistatec.ocelot.its.model.TerminologyMetaData;
import com.vistatec.ocelot.its.model.TextAnalysisMetaData;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;

/**
 * This class should be extended by all classes providing methods for converting
 * XLIFF files tag into enrichments. This class also provides methods for
 * converting enrichments to ITS meta data to be displayed in tables in Ocelot
 * left panel.
 */
public class EnrichmentConverter {

	/** The logger. */
	protected final Logger logger = LoggerFactory.getLogger(EnrichmentConverter.class);

	/** The source language. */
//	private String sourceLang;
//
//	/** The target language. */
//	private String targetLang;

	/**
	 * Constructor.
	 * 
	 * @param sourceLang
	 *            the source language.
	 * @param targetLang
	 *            the target language.
	 * @param logger
	 *            the logger.
	 */
	public EnrichmentConverter() {
//		this.sourceLang = sourceLang;
//		this.targetLang = targetLang;
//		this.logger = logger;
	}

//	/**
//	 * Retrieves the triple enrichments (link and terminology) from a triples
//	 * formatted as a JSON-LD string.
//	 * 
//	 * @param jsonString
//	 *            the triples formatted as JSON-LD string
//	 * @param enrichments
//	 *            the list of enrichments retrieved so far
//	 * @return the complete list of enrichments.
//	 */
//	protected List<Enrichment> retrieveTriplesEnrichments(String jsonString,
//	        List<Enrichment> enrichments, String language) {
//		List<Enrichment> triplesEnrichments = new ArrayList<Enrichment>();
//		Model tripleModel = ModelFactory.createDefaultModel();
//		tripleModel.read(new StringReader(jsonString), null,
//		        EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
//		ResIterator resourcesIt = tripleModel.listSubjects();
//		if (resourcesIt != null) {
//			Resource currRes = null;
//			while (resourcesIt.hasNext()) {
//				currRes = resourcesIt.next();
//				// check if an enrichment (entity of terminology exists for the
//				// current resource
//				Enrichment enrichment = findEnrichmentForURI(enrichments,
//				        currRes.getURI());
//				if (enrichment != null) {
//					// The resource is related to an entity enrichment --> then
//					// the triples represent a Link enrichment
//					if (enrichment.getType().equals(Enrichment.ENTITY_TYPE)) {
//						LinkEnrichment link = new LinkEnrichment(
//						        enrichment.getOffsetStartIdx(),
//						        enrichment.getOffsetEndIdx(), language);
//						ELinkEnrichmentsConstants.fillLinkEnrichment(link,
//						        tripleModel, currRes.getURI());
//						triplesEnrichments.add(link);
//						// The resource is related to a terminology enrichment
//						// --> then the triples represent sources, targets and
//						// senses for this terminology enrichment.
//					} else if (enrichment.getType().equals(
//					        Enrichment.TERMINOLOGY_TYPE)) {
//						triplesEnrichments.addAll(buildTerminologyEnrichments(
//						        tripleModel, currRes.getURI(),
//						        enrichment.getOffsetStartIdx(),
//						        enrichment.getOffsetEndIdx()));
//						enrichments.remove(enrichment);
//					}
//				}
//
//			}
//		}
//		return triplesEnrichments;
//	}

//	/**
//	 * Builds all the terminology enrichments represented by the triples model.
//	 * 
//	 * @param tripleModel
//	 *            the triples model.
//	 * @param termResURI
//	 *            the terminology resource URI
//	 * @param offsetStartIdx
//	 *            the terminology enrichment offset start index
//	 * @param offsetEndIdx
//	 *            the terminology enrichment offset end index
//	 * @return the list of terminology enrichments.
//	 */
//	private List<Enrichment> buildTerminologyEnrichments(Model tripleModel,
//	        String termResURI, int offsetStartIdx, int offsetEndIdx) {
//
//		List<Enrichment> termEnrichments = new ArrayList<Enrichment>();
//
//		Resource mainRes = tripleModel.createResource(termResURI);
//		StmtIterator mainStmtIt = tripleModel.listStatements(mainRes, null,
//		        (RDFNode) null);
//		List<Statement> tripleStmts = null;
//		TerminologyEnrichment termEnrich = null;
//		while (mainStmtIt.hasNext()) {
//			Statement mainStmt = mainStmtIt.next();
//			tripleStmts = new ArrayList<Statement>();
//			tripleStmts.add(mainStmt);
//			String sense = findSense(tripleModel, mainStmt, tripleStmts);
//			String definition = findDefinition(tripleModel, mainStmt, tripleStmts);
//			List<String> sourceList = new ArrayList<String>();
//			List<String> targetList = new ArrayList<String>();
//			findSourceAndTarget(tripleModel, mainStmt,
//			        tripleStmts, sourceList, targetList);
//			if (!sourceList.isEmpty()) {
//				termEnrich = new TerminologyEnrichment();
//				termEnrich.setSourceTermList(sourceList);
//				termEnrich.setTargetTermList(targetList);
//				termEnrich.setSense(sense);
//				termEnrich.setDefinition(definition);
//				termEnrich.setTermTriples(tripleStmts);
//				termEnrich.setOffsetStartIdx(offsetStartIdx);
//				termEnrich.setOffsetEndIdx(offsetEndIdx);
//				termEnrich.setTermInfoRef(termResURI);
//				termEnrichments.add(termEnrich);
//			}
//		}
//		return termEnrichments;
//	}

//	private String findDefinition(Model tripleModel, Statement mainTermStmt,
//            List<Statement> tripleStmts) {
//		
//		String definition = null;
//		StmtIterator definitionStmtIt = tripleModel.listStatements(mainTermStmt
//		        .getObject().asResource(), tripleModel.createProperty(
//		        		"http://tbx2rdf.lider-project.eu/tbx#", "definition"),
//		        (RDFNode) null);
//		Statement definitionStmt = null;
//		if (definitionStmtIt != null && definitionStmtIt.hasNext()) {
//			definitionStmt = definitionStmtIt.next();
//			definition = definitionStmt.getObject().asLiteral().getString();
//			tripleStmts.add(definitionStmt);
//		}
//		return definition;
//    }

//	/**
//	 * Finds the enrichment related to the URI passed as parameter. It could be
//	 * either an entity or a terminology enrichment.
//	 * 
//	 * @param enrichments
//	 *            the list of enrichments.
//	 * @param uri
//	 *            the URI
//	 * @return the enrichment related to the URI
//	 */
//	private Enrichment findEnrichmentForURI(List<Enrichment> enrichments,
//	        String uri) {
//
//		Enrichment enrichment = null;
//		if (enrichments != null) {
//			for (Enrichment currEnrich : enrichments) {
//				if (currEnrich.getType().equals(Enrichment.ENTITY_TYPE)) {
//					if (uri.equals(((EntityEnrichment) currEnrich)
//					        .getEntityURL())) {
//						enrichment = currEnrich;
//						break;
//					}
//				} else if (currEnrich.getType().equals(
//				        Enrichment.TERMINOLOGY_TYPE)) {
//					if (uri.equals(((TerminologyEnrichment) currEnrich)
//					        .getTermInfoRef())) {
//						enrichment = currEnrich;
//						break;
//					}
//				}
//			}
//		}
//
//		return enrichment;
//
//	}
//
//	/**
//	 * Finds the sense for the current terminology triple.
//	 * 
//	 * @param tripleModel
//	 *            the triples model
//	 * @param mainTermStmt
//	 *            the terminology triple
//	 * @param tripleStmts
//	 *            the list of triples statements related to this terminology
//	 *            enrichment
//	 * @return the sense if it exists; <code>null</code> otherwise
//	 */
//	private String findSense(Model tripleModel, Statement mainTermStmt,
//	        List<Statement> tripleStmts) {
//
//		String sense = null;
//		StmtIterator senseStmtIt = tripleModel.listStatements(mainTermStmt
//		        .getObject().asResource(), tripleModel.createProperty(
//		        "http://www.w3.org/2000/01/rdf-schema#", "comment"),
//		        (RDFNode) null);
//		Statement senseStmt = null;
//		if (senseStmtIt != null && senseStmtIt.hasNext()) {
//			senseStmt = senseStmtIt.next();
//			sense = senseStmt.getObject().asLiteral().getString();
//			tripleStmts.add(senseStmt);
//		}
//		return sense;
//
//	}

//	/**
//	 * Finds source and target for the current terminology triple.
//	 * 
//	 * @param tripleModel
//	 *            the triples model.
//	 * @param mainTermStmt
//	 *            the terminology main statement.
//	 * @param tripleStmts
//	 *            the list of triples realted to this terminology enrichment.
//	 * @return an array of strings containing the source at the first index and
//	 *         the target at the second index.
//	 */
//	protected void  findSourceAndTarget(Model tripleModel,
//	        Statement mainTermStmt, List<Statement> tripleStmts, List<String> sourceList, List<String> targetList) {
//
//		String sourceLanguage = sourceLang;
//		if (sourceLang.contains("-")) {
//			sourceLanguage = sourceLang.substring(0, sourceLang.indexOf("-"));
//		}
//		String targetLanguage = targetLang;
//		if (targetLang.contains("-")) {
//			targetLanguage = targetLang.substring(0, targetLang.indexOf("-"));
//		}
//		StmtIterator referenceStmtIt = tripleModel.listStatements(null,
//		        tripleModel.createProperty(
//		                "http://www.w3.org/ns/lemon/ontolex#", "reference"),
//		        mainTermStmt.getObject());
//		if (referenceStmtIt != null) {
//			Statement referenceStmt = null;
//			while (referenceStmtIt.hasNext()) {
//				referenceStmt = referenceStmtIt.next();
//				tripleStmts.add(referenceStmt);
//				String sourceURI = referenceStmt.getSubject().getURI()
//				        .replace("#Sense", "#CanonicalForm");
//				StmtIterator termIt = tripleModel.listStatements(tripleModel
//				        .createResource(sourceURI), tripleModel.createProperty(
//				        "http://www.w3.org/ns/lemon/ontolex#", "writtenRep"),
//				        (RDFNode) null);
//				if (termIt != null && termIt.hasNext()) {
//					Statement sourcTgtStmt = termIt.next();
//					tripleStmts.add(sourcTgtStmt);
//					if (sourceLanguage.equals(sourcTgtStmt.getObject()
//					        .asLiteral().getLanguage())) {
//						sourceList.add(sourcTgtStmt.getObject().asLiteral()
//						        .getString());
//					} else if (targetLanguage.equals(sourcTgtStmt.getObject()
//					        .asLiteral().getLanguage())) {
//						targetList.add(sourcTgtStmt.getObject().asLiteral()
//						        .getString());
//					}
//				}
//			}
//		}
//	}

	/**
	 * Converts the enrichments assigned to a segment to a list of ITS meta
	 * data.
	 * 
	 * @param segment
	 *            the Ocelot segment.
	 */
	public void convertEnrichments2ITSMetadata(OcelotSegment segment) {

		if (segment.getSource() instanceof BaseSegmentVariant) {
			convertEnrichment2ITSMetaData(segment,
			        (BaseSegmentVariant) segment.getSource(),
			        EnrichmentMetaData.SOURCE);
		}
		if (segment.getTarget() != null
		        && segment.getTarget() instanceof BaseSegmentVariant) {
			convertEnrichment2ITSMetaData(segment,
			        (BaseSegmentVariant) segment.getTarget(),
			        EnrichmentMetaData.TARGET);
		}

	}

	/**
	 * Converts the enrichments assigned to a specific variant to a list of ITS
	 * meta data.
	 * 
	 * @param segment
	 *            the Ocelot segment
	 * @param variant
	 *            the variant
	 * @param segmentPart
	 *            a string stating the part of the segment involved.
	 */
	public static void convertEnrichment2ITSMetaData(OcelotSegment segment,
	        BaseSegmentVariant variant, String segmentPart) {

		Set<Enrichment> variantEnrichments = variant.getEnirchments();
		if (variantEnrichments != null) {
			String sourceText = variant.getPlainText();
			TextAnalysisMetaData taAnnot = null;
			TerminologyMetaData termAnnot = null;
			for (Enrichment enrich : variantEnrichments) {
				if (enrich.getType().equals(Enrichment.ENTITY_TYPE)) {
					taAnnot = createTaMetaData((EntityEnrichment) enrich,
					        sourceText, segmentPart);
					TextAnalysisMetaData existingMetaData = findTaMetaData(
					        taAnnot.getEntity(), segment.getTextAnalysis(), segmentPart);
					if (existingMetaData == null) {
						segment.addTextAnalysis(taAnnot);
					} else {
						existingMetaData.merge(taAnnot);
					}
				} else if (enrich.getType().equals(Enrichment.TERMINOLOGY_TYPE)) {
					termAnnot = createTermMetaData(
					        (TerminologyEnrichment) enrich, sourceText,
					        segmentPart);
					if(!segment.getTerms().contains(termAnnot)){
						segment.addTerm(termAnnot);
					}
				}
			}
		}
	}
	
	/**
	 * Removes from the segment all the metadata related to enrichments.
	 * @param segment the segment
	 * @param variant the variant
	 */
	public static void removeEnrichmentMetaData(OcelotSegment segment,
			BaseSegmentVariant variant, boolean target) {

		if (variant.getEnirchments() != null) {
			for (Enrichment enrich : variant.getEnirchments()) {
				String sourceText = variant.getPlainText();
				TerminologyMetaData termAnnot = null;
				TextAnalysisMetaData taAnnot = null;
				String segPart = target?TextAnalysisMetaData.TARGET:TextAnalysisMetaData.SOURCE;
				if (enrich.getType().equals(Enrichment.ENTITY_TYPE)) {
					taAnnot = createTaMetaData((EntityEnrichment) enrich,
					        sourceText, null);
					TextAnalysisMetaData existingMetaData = findTaMetaData(
					        taAnnot.getEntity(), segment.getTextAnalysis(), segPart);
					if(existingMetaData != null){
						if(taAnnot.getTaAnnotatorsRef() != null){
							existingMetaData.setTaAnnotatorsRef(null);
						}
						if(taAnnot.getTaClassRef() != null){
							existingMetaData.setTaClassRef(null);
						}
						if(taAnnot.getTaConfidence() != null){
							existingMetaData.setTaConfidence(null);
						}
						if(taAnnot.getTaIdentRef()  != null){
							existingMetaData.setTaIdentRef(null);
						}
						if(existingMetaData.isEmpty()){
							segment.removeTextAnalysis(existingMetaData);
						}
					}
				} else if (enrich.getType().equals(Enrichment.TERMINOLOGY_TYPE)) {
					termAnnot = createTermMetaData(
					        (TerminologyEnrichment) enrich, sourceText,
					        segPart);
					segment.removeTerm(termAnnot);
					
				}
			}
		}
	}

	/**
	 * Finds the Text-Analysis meta data related to a specific entity.
	 * 
	 * @param entity
	 *            the entity string
	 * @param metaDataList
	 *            the list of meta data
	 * @return the Text-Analysis meta data related to the entity
	 */
	private static TextAnalysisMetaData findTaMetaData(String entity,
	        List<TextAnalysisMetaData> metaDataList, String segPart) {

		TextAnalysisMetaData taMetaData = null;
		if (metaDataList != null) {
			for (TextAnalysisMetaData metaData : metaDataList) {
				if (metaData.getEntity().equals(entity) && metaData.getSegPart().equals(segPart)) {
					taMetaData = metaData;
					break;
				}
			}
		}

		return taMetaData;

	}

	/**
	 * Creates a text-analysis meta data starting from an entity enrichment.
	 * 
	 * @param enrichment
	 *            the enrichment
	 * @param wholeText
	 *            the whole text assigned to the involved segment part
	 * @param segmentPart
	 *            a string stating which segment part is involved (source,
	 *            target or segment)
	 * @return the text-analysis meta data
	 */
	private static TextAnalysisMetaData createTaMetaData(EntityEnrichment enrichment,
	        String wholeText, String segmentPart) {
		TextAnalysisMetaData taAnnot = new TextAnalysisMetaData();
		taAnnot.setTaIdentRef(enrichment.getEntityURL());
		taAnnot.setEntity(wholeText.substring(enrichment.getOffsetNoTagsStartIdx(),
		        enrichment.getOffsetNoTagsEndIdx()));
		taAnnot.setTaAnnotatorsRef(enrichment.getAnnotatorRef());
		taAnnot.setSegPart(segmentPart);
		return taAnnot;
	}

	/**
	 * Creates a terminlogy meta data starting from a terminology enrichment.
	 * 
	 * @param enrichment
	 *            the terminology enrichment
	 * @param wholeText
	 *            the whole text assigned to the involved segment part
	 * @param segmentPart
	 *            a string stating which segment part is involved (source,
	 *            target or segment)
	 * @return the terminology meta data
	 */
	private static TerminologyMetaData createTermMetaData(
	        TerminologyEnrichment enrichment, String wholeText,
	        String segmentPart) {

		TerminologyMetaData termAnnot = new TerminologyMetaData();
		termAnnot.setAnnotatorsRef(enrichment.getAnnotator());
		termAnnot.setSense(enrichment.getSense());
		termAnnot.setTermSource(enrichment.getSourceTerm());
		termAnnot.setTermTarget(enrichment.getTargetTerm());
		termAnnot.setTerm(wholeText.substring(enrichment.getOffsetNoTagsStartIdx(),
		        enrichment.getOffsetNoTagsEndIdx()));
		termAnnot.setSegPart(segmentPart);
		return termAnnot;
	}
	
	
	
//	protected int toCodedIndex(int index, TextFragment frag){
//		
//		Integer codedIndex = null;
//		if(!frag.hasCode()){
//			codedIndex = index;
//		} else {
//			int stringOffset = 0;
//			int currCodedStrIndex = 0;
//			String codedString = frag.getCodedText();
//			while(currCodedStrIndex<codedString.length() && codedIndex == null){
//				if(TextFragment.isMarker(codedString.charAt(currCodedStrIndex))){
//					currCodedStrIndex++;
//					currCodedStrIndex++;
//				} else {
//					if(stringOffset == index){
//						codedIndex = currCodedStrIndex;
//					} else {
//						stringOffset++;
//						currCodedStrIndex++;
//					}
//				}
//			}
//		}
//		return codedIndex;
//	}

}