package com.vistatec.ocelot.xliff.freme;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.resource.TextPart;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;

public class FremeAnnotationReaderXliff12 extends FremeAnnotationsReader {

	public FremeAnnotationReaderXliff12(String sourceLang, String targetLang) {

		super(sourceLang, targetLang);
	}

	public Map<TextContainer, List<Enrichment>> enrichmentsMap;

	public Map<TextContainer, List<Enrichment>> readEnrichments(ITextUnit textUnit, TextContainer sourceTc,
			TextContainer tgtTc, TextContainer origTgtTc) {

		enrichmentsMap = new HashMap<>();
		String annotatorRef = getAnnotatorRef(textUnit);
		Model tripleModel = findTripleModels(textUnit);
		enrichmentsMap.put(sourceTc, readEnrichments(sourceTc, textUnit, LocaleId.fromString(sourceLang).getLanguage(),
				annotatorRef, tripleModel));
		if (tgtTc != null) {
			enrichmentsMap.put(tgtTc, readEnrichments(tgtTc, textUnit, LocaleId.fromString(targetLang).getLanguage(),
					annotatorRef, tripleModel));
		}
		if (origTgtTc != null) {
			enrichmentsMap.put(origTgtTc, readEnrichments(origTgtTc, textUnit,
					LocaleId.fromString(targetLang).getLanguage(), annotatorRef, tripleModel));
		}
		return enrichmentsMap;
	}

	private List<Enrichment> readEnrichments(TextContainer textContainer, ITextUnit textUnit, String language,
			String annotatorRef, Model tripleModel) {

		List<Enrichment> enrichments = new ArrayList<>();
		if (textContainer != null) {
			// finds the json text in this trans-unit
			for (TextPart part : textContainer.getParts()) {
				List<Code> codesToRemove = new ArrayList<>();
				TextFragment frag = part.getContent();
				System.out.println("Curr frag: " + frag.getText());
				int previousFragLength = 0;
				if (frag.getCodes() != null) {
					for (Code code : frag.getCodes()) {
						if (code.getTagType().equals(TagType.OPENING)) {
							GenericAnnotations codeAnnotations = code.getGenericAnnotations();
							if (codeAnnotations != null) {
								int startIndex = computeEnrichmentIndex(frag.getCodes().indexOf(code), frag)
										+ previousFragLength;
								int closingCodeIndexInList = frag.getIndexForClosing(code.getId());
								if (closingCodeIndexInList == -1) {
									// TODO LOG WARN
									continue;
								}
								int endIndex = computeEnrichmentIndex(closingCodeIndexInList, frag)
										+ previousFragLength;
								Code closingCode = frag.getCode(closingCodeIndexInList);
								enrichments.addAll(readEntityAndLinkEnrichments(code, closingCode, startIndex, endIndex,
										tripleModel, language));
								enrichments.addAll(
										readTermEnrichments(code, closingCode, startIndex, endIndex, tripleModel));
								if (annotatorRef != null) {
									removeAnnotatorRefs(annotatorRef, code, closingCode);
								}
								if (code.getGenericAnnotations() == null || code.getGenericAnnotations().size() == 0) {
									codesToRemove.add(code);
									codesToRemove.add(closingCode);
								}
							}

						}
					}
				}
				previousFragLength = frag.getText().length();
				for (Code code : codesToRemove) {
					frag.removeCode(code);
				}
			}
		}

		return enrichments;

	}

	private Model findTripleModels(ITextUnit textUnit) {

		Model tripleModel = ModelFactory.createDefaultModel();
		for (GenericSkeletonPart skelPart : ((GenericSkeleton) textUnit.getSkeleton()).getParts()) {
			if (skelPart.toString().contains(EnrichmentAnnotationsConstants.JSON_TAG_NAME)) {
				int jsonStartTagIndex = skelPart.getData().indexOf("<" + EnrichmentAnnotationsConstants.JSON_TAG_NAME);
				// +2 to count '<' and '>' characters.
				int jsonStringStartIndex = skelPart.getData().indexOf(">", jsonStartTagIndex) + 1;
				int jsonEndTagIndex = skelPart.getData().indexOf("</" + EnrichmentAnnotationsConstants.JSON_TAG_NAME,
						jsonStringStartIndex);
				// int jsonStringEndIndex =
				String jsonString = StringEscapeUtils
						.unescapeXml(skelPart.getData().substring(jsonStringStartIndex, jsonEndTagIndex));
				System.out.println(jsonString);
				tripleModel.read(new StringReader(jsonString), null, EnrichmentAnnotationsConstants.JSON_LD_FORMAT);

				skelPart.getData().replace(jsonStartTagIndex,
						jsonEndTagIndex + ("</" + EnrichmentAnnotationsConstants.JSON_TAG_NAME + ">").length(), "");
				break;
			}
		}
		return tripleModel;
	}

	private List<Enrichment> readTermEnrichments(Code openingCode, Code closingCode, int startIndex, int endIndex,
			Model tripleModel) {

		List<Enrichment> termEnrichments = new ArrayList<>();
		List<GenericAnnotation> annotsToRemove = new ArrayList<>();
		List<GenericAnnotation> termAnnots = openingCode.getGenericAnnotations()
				.getAnnotations(GenericAnnotationType.TERM);
		if (termAnnots != null) {
			for (GenericAnnotation termAnnot : termAnnots) {
				termEnrichments.addAll(retrieveTermEnrichments(termAnnot.getString(GenericAnnotationType.TERM_INFO),
						startIndex, endIndex, tripleModel));
				annotsToRemove.add(termAnnot);
			}

		}
		for (GenericAnnotation annotToRemove : annotsToRemove) {
			openingCode.getGenericAnnotations().remove(annotToRemove);
			removeAnnotationFromCode(closingCode, annotToRemove);
		}
		return termEnrichments;
	}

	// private List<Enrichment> retrieveTermEnrichments(String termUri, int
	// startIndex, int endIndex, Model tripleModel) {
	//
	// List<Enrichment> termEnrichments = new ArrayList<>();
	// Resource termResource = tripleModel.createResource(termUri);
	// StmtIterator mainStmtIt = tripleModel.listStatements(termResource, null,
	// (RDFNode) null);
	// List<Statement> tripleStmts = null;
	// TerminologyEnrichment termEnrich = null;
	// while (mainStmtIt.hasNext()) {
	// Statement mainStmt = mainStmtIt.next();
	// tripleStmts = new ArrayList<Statement>();
	// tripleStmts.add(mainStmt);
	// String sense = findSense(tripleModel, mainStmt, tripleStmts);
	// String definition = findDefinition(tripleModel, mainStmt, tripleStmts);
	// List<String> sourceList = new ArrayList<String>();
	// List<String> targetList = new ArrayList<String>();
	// findSourceAndTarget(tripleModel, mainStmt, tripleStmts, sourceList,
	// targetList);
	// if (!sourceList.isEmpty()) {
	// termEnrich = new TerminologyEnrichment();
	// termEnrich.setTermInfoRef(termUri);
	// termEnrich.setOffsetStartIdx(startIndex);
	// termEnrich.setOffsetEndIdx(endIndex);
	// termEnrich.setSourceTermList(sourceList);
	// termEnrich.setTargetTermList(targetList);
	// termEnrich.setSense(sense);
	// termEnrich.setDefinition(definition);
	// termEnrich.setTermTriples(tripleStmts);
	// termEnrichments.add(termEnrich);
	// }
	// }
	// return termEnrichments;
	// }

	// /**
	// * Finds source and target for the current terminology triple.
	// *
	// * @param tripleModel
	// * the triples model.
	// * @param mainTermStmt
	// * the terminology main statement.
	// * @param tripleStmts
	// * the list of triples realted to this terminology enrichment.
	// * @return an array of strings containing the source at the first index
	// and
	// * the target at the second index.
	// */
	// protected void findSourceAndTarget(Model tripleModel, Statement
	// mainTermStmt, List<Statement> tripleStmts,
	// List<String> sourceList, List<String> targetList) {
	//
	// String sourceLanguage = sourceLang;
	// if (sourceLang.contains("-")) {
	// sourceLanguage = sourceLang.substring(0, sourceLang.indexOf("-"));
	// }
	// String targetLanguage = targetLang;
	// if (targetLang.contains("-")) {
	// targetLanguage = targetLang.substring(0, targetLang.indexOf("-"));
	// }
	// StmtIterator referenceStmtIt = tripleModel.listStatements(null,
	// tripleModel.createProperty("http://www.w3.org/ns/lemon/ontolex#",
	// "reference"),
	// mainTermStmt.getObject());
	// if (referenceStmtIt != null) {
	// Statement referenceStmt = null;
	// while (referenceStmtIt.hasNext()) {
	// referenceStmt = referenceStmtIt.next();
	// tripleStmts.add(referenceStmt);
	// String sourceURI = referenceStmt.getSubject().getURI().replace("#Sense",
	// "#CanonicalForm");
	// StmtIterator termIt =
	// tripleModel.listStatements(tripleModel.createResource(sourceURI),
	// tripleModel.createProperty("http://www.w3.org/ns/lemon/ontolex#",
	// "writtenRep"),
	// (RDFNode) null);
	// if (termIt != null && termIt.hasNext()) {
	// Statement sourcTgtStmt = termIt.next();
	// tripleStmts.add(sourcTgtStmt);
	// if
	// (sourceLanguage.equals(sourcTgtStmt.getObject().asLiteral().getLanguage()))
	// {
	// sourceList.add(sourcTgtStmt.getObject().asLiteral().getString());
	// } else if
	// (targetLanguage.equals(sourcTgtStmt.getObject().asLiteral().getLanguage()))
	// {
	// targetList.add(sourcTgtStmt.getObject().asLiteral().getString());
	// }
	// }
	// }
	// }
	// }

	// private String findDefinition(Model tripleModel, Statement mainTermStmt,
	// List<Statement> tripleStmts) {
	//
	// String definition = null;
	// StmtIterator definitionStmtIt =
	// tripleModel.listStatements(mainTermStmt.getObject().asResource(),
	// tripleModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#",
	// "definition"), (RDFNode) null);
	// Statement definitionStmt = null;
	// if (definitionStmtIt != null && definitionStmtIt.hasNext()) {
	// definitionStmt = definitionStmtIt.next();
	// definition = definitionStmt.getObject().asLiteral().getString();
	// tripleStmts.add(definitionStmt);
	// }
	// return definition;
	// }

	// /**
	// * Finds the sense for the current terminology triple.
	// *
	// * @param tripleModel
	// * the triples model
	// * @param mainTermStmt
	// * the terminology triple
	// * @param tripleStmts
	// * the list of triples statements related to this terminology
	// * enrichment
	// * @return the sense if it exists; <code>null</code> otherwise
	// */
	// private String findSense(Model tripleModel, Statement mainTermStmt,
	// List<Statement> tripleStmts) {
	//
	// String sense = null;
	// StmtIterator senseStmtIt =
	// tripleModel.listStatements(mainTermStmt.getObject().asResource(),
	// tripleModel.createProperty("http://www.w3.org/2000/01/rdf-schema#",
	// "comment"), (RDFNode) null);
	// Statement senseStmt = null;
	// if (senseStmtIt != null && senseStmtIt.hasNext()) {
	// senseStmt = senseStmtIt.next();
	// sense = senseStmt.getObject().asLiteral().getString();
	// tripleStmts.add(senseStmt);
	// }
	// return sense;
	//
	// }

	private List<Enrichment> readEntityAndLinkEnrichments(Code openingCode, Code closingCode, int enrichStartOffset,
			int enrichEndOffset, Model tripleModel, String language) {

		List<Enrichment> enrichments = new ArrayList<>();
		List<GenericAnnotation> annotsToRemove = new ArrayList<>();
		List<GenericAnnotation> taAnnots = openingCode.getGenericAnnotations().getAnnotations(GenericAnnotationType.TA);
		if (taAnnots != null) {
			EntityEnrichment entity = null;
			for (GenericAnnotation taAnnot : taAnnots) {
				entity = new EntityEnrichment(getRefString(taAnnot.getString(GenericAnnotationType.TA_IDENT)));
				entity.setAnnotatorRef(taAnnot.getString(GenericAnnotationType.ANNOTATORREF));
				entity.setOffsetNoTagsStartIdx(enrichStartOffset);
				entity.setOffsetNoTagsEndIdx(enrichEndOffset);
				enrichments.add(entity);
				LinkEnrichment linkEnrich = retrieveLinkEnrichment(entity.getEntityURL(), tripleModel, language,
						enrichStartOffset, enrichEndOffset);
				if (linkEnrich != null) {
					enrichments.add(linkEnrich);
				}
				annotsToRemove.add(taAnnot);
			}
		}
		if (!annotsToRemove.isEmpty()) {

			for (GenericAnnotation annotToRemove : annotsToRemove) {
				openingCode.getGenericAnnotations().remove(annotToRemove);
				removeAnnotationFromCode(closingCode, annotToRemove);
			}
		}

		return enrichments;
	}

	// private LinkEnrichment retrieveLinkEnrichment(String entityUri, Model
	// tripleModel, String language, int startIndex,
	// int endIndex) {
	//
	// LinkEnrichment linkEnrichment = new LinkEnrichment(startIndex, endIndex,
	// language);
	// ELinkEnrichmentsConstants.fillLinkEnrichment(linkEnrichment, tripleModel,
	// entityUri);
	// return linkEnrichment;
	// }

	private void removeAnnotatorRefs(String annotatorRef, Code openingCode, Code closingCode) {

		List<GenericAnnotation> annotsToRemove = new ArrayList<>();
		List<GenericAnnotation> annotatorAnnots = openingCode.getGenericAnnotations()
				.getAnnotations(GenericAnnotationType.ANNOT);
		if (annotatorAnnots != null) {
			for (GenericAnnotation annotator : annotatorAnnots) {
				String annotatorValue = annotator.getString(GenericAnnotationType.ANNOT_VALUE);
				if (annotatorValue.contains(annotatorRef)) {
					String newAnnotatorValue = annotatorValue.replaceAll(annotatorRef.replace("|", "\\|"), "");
					if (newAnnotatorValue.trim().isEmpty()) {
						annotsToRemove.add(annotator);
					} else {
						annotator.setString(GenericAnnotationType.ANNOT_VALUE, newAnnotatorValue);
						GenericAnnotation closingAnnotation = findAnnotationInCode(closingCode, annotator);
						closingAnnotation.setString(GenericAnnotationType.ANNOT_VALUE, newAnnotatorValue);
					}
					// String[] annotatorsSplit = annotatorValue.split(" ");
					// if (annotatorsSplit.length == 1) {
					// annotsToRemove.add(annotator);
					// } else {
					// StringBuilder newAnnotatorValue = new StringBuilder();
					// for (String currAnnotValue : annotatorsSplit) {
					// if
					// (!currAnnotValue.contains(EnrichmentAnnotationsConstants.TA_ANNOTATORS_REF_STRING))
					// {
					// newAnnotatorValue.append(currAnnotValue);
					// newAnnotatorValue.append(" ");
					// }
					// }
					// newAnnotatorValue.delete(newAnnotatorValue.length() - 1,
					// newAnnotatorValue.length());
					// annotator.setString(GenericAnnotationType.ANNOT_VALUE,
					// newAnnotatorValue.toString());
					// GenericAnnotation closingAnnotation =
					// findAnnotationInCode(closingCode, annotator);
					// closingAnnotation.setString(GenericAnnotationType.ANNOT_VALUE,
					// newAnnotatorValue.toString());
					// }
					break;
				}
			}
		}
		if (!annotsToRemove.isEmpty()) {
			for (GenericAnnotation annot : annotsToRemove) {
				openingCode.getGenericAnnotations().remove(annot);
				removeAnnotationFromCode(closingCode, annot);
			}
		}
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
			retString = refValue.substring(refIdx + GenericAnnotationType.REF_PREFIX.length());
		}
		return retString;
	}

	private int computeEnrichmentIndex(int codeIndexInList, TextFragment frag) {

		int codePositionInCodedText = frag.getCodePosition(codeIndexInList);
		return TextFragment.fromFragmentToString(frag, codePositionInCodedText);
	}

	private void removeAnnotationFromCode(Code code, GenericAnnotation annotToDelete) {

		GenericAnnotation taAnnot = findAnnotationInCode(code, annotToDelete);
		if (taAnnot != null) {
			code.getGenericAnnotations().remove(taAnnot);
		}
	}

	private GenericAnnotation findAnnotationInCode(Code code, GenericAnnotation annotToFind) {

		GenericAnnotation foundAnnot = null;
		switch (annotToFind.getType()) {
		case GenericAnnotationType.TA:
			for (GenericAnnotation taAnnot : code.getGenericAnnotations().getAnnotations(GenericAnnotationType.TA)) {
				if (taAnnot.getString(GenericAnnotationType.TA_IDENT)
						.equals(annotToFind.getString(GenericAnnotationType.TA_IDENT))) {
					foundAnnot = taAnnot;
					break;
				}
			}
			break;
		case GenericAnnotationType.ANNOT:
			for (GenericAnnotation annotatorAnnot : code.getGenericAnnotations()
					.getAnnotations(GenericAnnotationType.ANNOT)) {
				if (annotatorAnnot.getString(GenericAnnotationType.ANNOT_VALUE)
						.equals(annotToFind.getString(GenericAnnotationType.ANNOT_VALUE))) {
					foundAnnot = annotatorAnnot;
					break;
				}
			}
			break;
		default:
			break;
		}

		return foundAnnot;
	}

	private String getAnnotatorRef(ITextUnit textUnit) {
		String annotator = null;
		GenericAnnotations annotations = textUnit.getAnnotation(GenericAnnotations.class);
		if (annotations != null) {
			List<GenericAnnotation> annotatorAnnots = annotations.getAnnotations(GenericAnnotationType.ANNOT);
			if (annotatorAnnots != null) {
				Iterator<GenericAnnotation> annotatorsIt = annotatorAnnots.iterator();
				GenericAnnotation annot = null;

				while (annotatorsIt.hasNext() && annotator == null) {
					annot = annotatorsIt.next();
					String annotValue = annot.getString(GenericAnnotationType.ANNOT_VALUE);
					if (annotValue != null
							&& annotValue.contains(EnrichmentAnnotationsConstants.TA_ANNOTATORS_REF_STRING)) {
						annotator = getAnnotatorValue(EnrichmentAnnotationsConstants.TA_ANNOTATORS_REF_STRING,
								annot.getString(GenericAnnotationType.ANNOT_VALUE));
						if (annotator != null) {
							annotValue = annotValue.replaceAll(annotator.replace("|", "\\|"), "");
							annot.setString(GenericAnnotationType.ANNOT_VALUE, annotValue.trim());
						}
					}
				}
				if (annotator != null) {
					if (annot.getString(GenericAnnotationType.ANNOT_VALUE).isEmpty()) {
						annotations.remove(annot);
						for (GenericSkeletonPart skelPart : ((GenericSkeleton) textUnit.getSkeleton()).getParts()) {
							if (skelPart.getData().toString().contains(annotator)) {
								int annotStartIndex = skelPart.getData().indexOf("its:annotatorsRef");
								int annotEndIndex = skelPart.getData().indexOf(annotator, annotStartIndex)
										+ annotator.length() + 1;
								skelPart.getData().delete(annotStartIndex, annotEndIndex);
								break;
							}
						}
					} else {
						for (GenericSkeletonPart skelPart : ((GenericSkeleton) textUnit.getSkeleton()).getParts()) {
							if (skelPart.getData().toString().contains(annotator)) {
								int annotStartIndex = skelPart.getData().indexOf(annotator);
								int annotEndIndex = annotStartIndex + annotator.length();
								skelPart.getData().delete(annotStartIndex, annotEndIndex);
							}
						}
					}
				}
			}
		}
		return annotator;
	}

	private String getAnnotatorValue(String annotatorType, String annotatorValueString) {

		String retValue = null;
		String[] annotators = annotatorValueString.split(" ");
		int i = 0;
		while (i < annotators.length && retValue == null) {
			if (annotators[i].contains(annotatorType)) {
				retValue = annotators[i];
			}
			i++;
		}

		return retValue;

	}

	// public static void main(String[] args) throws IOException {
	//
	// OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();
	// List<OcelotSegment> segments = parser.parse(
	// new File(System.getProperty("user.home") + File.separator + "Projects" +
	// File.separator + "xliff",
	// "altTrans-enrichments.xlf"),
	// true);
	// }
}
