/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.xliff.okapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.FileUtil;
import net.sf.okapi.common.IResource;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.annotation.AltTranslation;
import net.sf.okapi.common.annotation.AltTranslationsAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.annotation.ITSLQIAnnotations;
import net.sf.okapi.common.annotation.ITSProvenanceAnnotations;
import net.sf.okapi.common.annotation.XLIFFNote;
import net.sf.okapi.common.annotation.XLIFFNoteAnnotation;
import net.sf.okapi.common.annotation.XLIFFPhase;
import net.sf.okapi.common.annotation.XLIFFPhaseAnnotation;
import net.sf.okapi.common.annotation.XLIFFTool;
import net.sf.okapi.common.annotation.XLIFFToolAnnotation;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.Property;
import net.sf.okapi.common.resource.RawDocument;
import net.sf.okapi.common.resource.StartSubDocument;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.filters.xliff.Parameters;
import net.sf.okapi.filters.xliff.XLIFFFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.OtherITSMetadata;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.its.model.TextAnalysisMetaData;
import com.vistatec.ocelot.its.model.okapi.OkapiProvenance;
import com.vistatec.ocelot.rules.DataCategoryField;
import com.vistatec.ocelot.rules.StateQualifier;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.okapi.Note;
import com.vistatec.ocelot.segment.model.okapi.Notes;
import com.vistatec.ocelot.segment.model.okapi.OkapiSegment;
import com.vistatec.ocelot.segment.model.okapi.TextContainerVariant;
import com.vistatec.ocelot.xliff.XLIFFParser;
import com.vistatec.ocelot.xliff.freme.EnrichmentConverterXLIFF12;

/**
 * Parse XLIFF file for use in the workbench. The Event list is used when
 * writing out files through Okapi; updates to the workbench segments must then
 * be reflected(synchronized) in the proper Event.
 */
public class OkapiXLIFF12Parser implements XLIFFParser {
	private static Logger LOG = LoggerFactory
	        .getLogger(OkapiXLIFF12Parser.class);
	private LinkedList<Event> events;
	private XLIFFFilter filter;
	private int documentSegmentNum;
	private String sourceLang, targetLang;
	private String originalFileName;
	private EnrichmentConverterXLIFF12 enrichmentConverter;

	@Override
	public String getSourceLang() {
		return this.sourceLang;
	}

	public void setSourceLang(String sourceLang) {
		this.sourceLang = sourceLang;
	}

	@Override
	public String getTargetLang() {
		return this.targetLang;
	}

	public void setTargetLang(String targetLang) {
		this.targetLang = targetLang;
	}
	
	public void setOriginalFileName(String originalFileName){
		this.originalFileName = originalFileName;
	}
	
	public String getOriginalFileName(){
		return originalFileName;
	}

	public Event getSegmentEvent(int segEventNumber) {
		return this.events.get(segEventNumber);
	}

	public List<Event> getSegmentEvents() {
		return this.events;
	}

	@Override
	public List<OcelotSegment> parse(File xliffFile) throws IOException {
		events = new LinkedList<Event>();
		List<OcelotSegment> segments = new LinkedList<OcelotSegment>();
		documentSegmentNum = 1;

		List<String> locales = FileUtil.guessLanguages(xliffFile
		        .getAbsolutePath());
		LocaleId sourceLocale = null, targetLocale = null;
		sourceLocale = (locales.size() >= 1) ? LocaleId.fromString(locales
		        .get(0)) : LocaleId.EMPTY;
		targetLocale = (locales.size() >= 2) ? LocaleId.fromString(locales
		        .get(1)) : LocaleId.EMPTY;

		FileInputStream is = new FileInputStream(xliffFile);
		RawDocument fileDoc = new RawDocument(is, "UTF-8", sourceLocale,
		        targetLocale);
		this.filter = new XLIFFFilter();
		Parameters filterParams = new Parameters();
		filterParams.setAddAltTrans(true);
		filterParams.setEscapeGT(true);
		this.filter.setParameters(filterParams);
		this.filter.open(fileDoc);
		int fileEventNum = 0;

		while (this.filter.hasNext()) {
			Event event = this.filter.next();
			events.add(event);

			if (event.isStartSubDocument()) {
				StartSubDocument fileElement = (StartSubDocument) event
				        .getResource();
				setOriginalFileName(fileElement.getName());
				XLIFFToolAnnotation toolAnn = fileElement
				        .getAnnotation(XLIFFToolAnnotation.class);
				if (toolAnn == null) {
					toolAnn = new XLIFFToolAnnotation();
					fileElement.setAnnotation(toolAnn);
				}
				if (toolAnn.get("Ocelot") == null) {
					toolAnn.add(new XLIFFTool("Ocelot", "Ocelot"), fileElement);
				}
				if (fileElement.getProperty("sourceLanguage") != null) {
					String fileSourceLang = fileElement.getProperty(
					        "sourceLanguage").getValue();
					if (getSourceLang() != null
					        && !getSourceLang().equals(fileSourceLang)) {
						LOG.warn("Mismatch between source languages in file elements");
					}
					setSourceLang(fileSourceLang);
					fileDoc.setSourceLocale(LocaleId.fromString(fileSourceLang));
				}
				if (fileElement.getProperty("targetLanguage") != null) {
					String fileTargetLang = fileElement.getProperty(
					        "targetLanguage").getValue();
					if (getTargetLang() != null
					        && !getTargetLang().equals(fileTargetLang)) {
						LOG.warn("Mismatch between target languages in file elements");
					}
					setTargetLang(fileTargetLang);
					fileDoc.setTargetLocale(LocaleId.fromString(fileTargetLang));
				}
				enrichmentConverter = new EnrichmentConverterXLIFF12(sourceLang, targetLang);

			} else if (event.isTextUnit()) {
				ITextUnit tu = (ITextUnit) event.getResource();
				segments.add(convertTextUnitToSegment(tu, fileEventNum));
			}
			fileEventNum++;
		}
		is.close();
		return segments;
	}
	
	public static void main(String[] args) {
		
		Locale locale = new Locale("en-us");
		System.out.println(locale.getLanguage());
	}

	public OkapiSegment convertTextUnitToSegment(ITextUnit tu, int fileEventNum) {
		TextContainer srcTu = tu.getSource();
		TextContainer tgtTu = new TextContainer();

		Set<LocaleId> targetLocales = tu.getTargetLocales();
		if (targetLocales.size() > 1) {
			LocaleId chosenTargetLocale = targetLocales.iterator().next();
			LOG.warn("More than 1 target locale: " + targetLocales);
			LOG.warn("Using target locale '" + chosenTargetLocale + "'");
			tgtTu = tu.getTarget(chosenTargetLocale);
		} else if (targetLocales.size() == 1) {
			for (LocaleId tgt : targetLocales) {
				tgtTu = tu.getTarget(tgt);
			}
		} else {
			tu.setTarget(LocaleId.fromString(getTargetLang()), tgtTu);
		}

		TextContainer oriTgtTu = retrieveOriginalTarget(tgtTu);
		List<Enrichment> sourceEnrichments = enrichmentConverter
		        .retrieveEnrichments(srcTu, tu, LocaleId.fromString(getSourceLang()).getLanguage());
		List<Enrichment> targetEnrichments = enrichmentConverter
		        .retrieveEnrichments(tgtTu, tu, LocaleId.fromString(getTargetLang()).getLanguage());
		List<Enrichment> originalTargetEnrichments = enrichmentConverter
		        .retrieveEnrichments(oriTgtTu, tu, LocaleId.fromString(getTargetLang()).getLanguage());

		OkapiSegment.Builder segBuilder = new OkapiSegment.Builder()
		        .segmentNumber(documentSegmentNum++)
		        .eventNumber(fileEventNum)
		        .source(new TextContainerVariant(srcTu))
		        .target(new TextContainerVariant(tgtTu))
		        .originalTarget(
		                oriTgtTu != null ? new TextContainerVariant(oriTgtTu)
		                        : null)
		        .tuId(tu.getId())
		        .translatable(tu.isTranslatable());

		Property stateQualifier = tgtTu.getProperty("state-qualifier");
		if (stateQualifier != null) {
			StateQualifier sq = StateQualifier.get(stateQualifier.getValue());
			if (sq != null) {
				segBuilder.stateQualifier(sq);
			} else {
				LOG.info("Ignoring state-qualifier value '"
				        + stateQualifier.getValue() + "'");
			}
		}
		XLIFFPhaseAnnotation phaseAnn = tu
		        .getAnnotation(XLIFFPhaseAnnotation.class);
		if (phaseAnn != null) {
			XLIFFPhase refPhase = phaseAnn.getReferencedPhase();
			segBuilder.phaseName(refPhase.getPhaseName());
		}
		OkapiSegment segment = segBuilder.build();
		if (segment.getSource() instanceof BaseSegmentVariant
		        && !sourceEnrichments.isEmpty()) {
			((BaseSegmentVariant) segment.getSource())
			        .addEnrichmentList(sourceEnrichments);
//			((BaseSegmentVariant) segment.getSource()).setEnriched(true);
		}
		if (segment.getTarget() instanceof BaseSegmentVariant
		        && !targetEnrichments.isEmpty()) {
			((BaseSegmentVariant) segment.getTarget())
			        .addEnrichmentList(targetEnrichments);
//			((BaseSegmentVariant) segment.getTarget()).setEnriched(true);
		}
		if (segment.getOriginalTarget() instanceof BaseSegmentVariant
		        && !originalTargetEnrichments.isEmpty()) {
			((BaseSegmentVariant) segment.getOriginalTarget())
			        .addEnrichmentList(originalTargetEnrichments);
		}
		List<Enrichment> totEnrichments = new ArrayList<Enrichment>(
		        sourceEnrichments);
		totEnrichments.addAll(targetEnrichments);
		readNotes(segment, tu);
		return attachITSDataToSegment(segment, tu, srcTu, tgtTu, totEnrichments);
	}

	private void readNotes(OkapiSegment seg, ITextUnit tu) {
		
		XLIFFNoteAnnotation noteAnnot = tu.getAnnotation(XLIFFNoteAnnotation.class);
		XLIFFNote ocelotOkapiNote = null;
		if(noteAnnot != null){
			ocelotOkapiNote = readOcelotNote(noteAnnot);
		}
		if(ocelotOkapiNote != null ){
			Notes notes = new Notes();
	        notes.add(new Note(Note.OCELOT_ID_PREFIX + "1", ocelotOkapiNote.getNoteText()));
	        seg.setNotes(notes);
		}
	}
	
	public XLIFFNote readOcelotNote(XLIFFNoteAnnotation noteAnnot){
		
		XLIFFNote ocelotNote = null;
		Iterator<XLIFFNote> notesIt = noteAnnot.iterator();
		XLIFFNote currNote = null;
		while(notesIt.hasNext() && ocelotNote == null){
			currNote = notesIt.next();
			if(Note.OCELOT_FROM_PROPERTY.equals(currNote.getFrom())){
				ocelotNote = currNote;
			}
		}
		return ocelotNote;
	}

	private OkapiSegment attachITSDataToSegment(OkapiSegment seg, ITextUnit tu,
	        TextContainer srcTu, TextContainer tgtTu,
	        List<Enrichment> enrichments) {

		ITSLQIAnnotations lqiAnns = retrieveITSLQIAnnotations(tu, srcTu, tgtTu);
		List<LanguageQualityIssue> lqiList = new ArrayList<>();
		for (GenericAnnotation ga : lqiAnns
		        .getAnnotations(GenericAnnotationType.LQI)) {
			lqiList.add(new LanguageQualityIssue(ga));
		}
		seg.addAllLQI(lqiList);

		ITSProvenanceAnnotations provAnns = retrieveITSProvAnnotations(tu,
		        srcTu, tgtTu);
		List<GenericAnnotation> provAnnList = provAnns
		        .getAnnotations(GenericAnnotationType.PROV);
		if (provAnnList != null) {
			List<Provenance> provList = new ArrayList<>();
			for (GenericAnnotation ga : provAnnList) {
				provList.add(new OkapiProvenance(ga));
			}
			seg.addAllProvenance(provList);
		}

		List<OtherITSMetadata> otherList = new ArrayList<OtherITSMetadata>();
		// otherList.addAll(EnrichmentConverter.convertEnrichments2ITSMetadata(enrichments,
		// seg));
		enrichmentConverter.convertEnrichments2ITSMetadata(seg);
//		seg.addAllTextAnalysis(retrieveITSTAAnnotations(tu, srcTu, tgtTu));
		if (tgtTu != null) {
			for (GenericAnnotation mtAnn : retrieveITSMTConfidenceAnnotations(tgtTu)) {
				otherList
				        .add(new OtherITSMetadata(
				                DataCategoryField.MT_CONFIDENCE,
				                mtAnn.getDouble(GenericAnnotationType.MTCONFIDENCE_VALUE)));
			}
		}
		seg.addAllOtherITSMetadata(otherList);

		return seg;
	}

	private List<TextAnalysisMetaData> retrieveITSTAAnnotations(ITextUnit tu,
	        TextContainer srcTu, TextContainer tgtTu) {

		List<TextAnalysisMetaData> taAnnotations = new ArrayList<TextAnalysisMetaData>();
		// Iterable<IAnnotation> annotations = tu.getAnnotations();
		// OK!!!!
		taAnnotations.addAll(createTaAnnotations(tu.getAnnotation(GenericAnnotations.class), TextAnalysisMetaData.SEGMENT));
		taAnnotations.addAll(createTaAnnotations(srcTu.getAnnotation(GenericAnnotations.class), TextAnalysisMetaData.SOURCE));
		if (tgtTu != null) {
			taAnnotations.addAll(createTaAnnotations(tgtTu.getAnnotation(GenericAnnotations.class), TextAnalysisMetaData.TARGET));
		}
		// tu.getAnnotation(GenericAnnotation)
		return taAnnotations;
	}

	private List<TextAnalysisMetaData> createTaAnnotations(
	        GenericAnnotations annotations, String entityType) {

		List<TextAnalysisMetaData> taAnnotations = new ArrayList<TextAnalysisMetaData>();
		if (annotations != null) {
			Iterator<GenericAnnotation> annotsIt = annotations.iterator();
			GenericAnnotation annot = null;
			TextAnalysisMetaData taAnnot = null;
			while (annotsIt.hasNext()) {
				annot = annotsIt.next();
				if (annot.getType().equals(GenericAnnotationType.TA)) {
					taAnnot = new TextAnalysisMetaData();
					taAnnot.setEntity(entityType);
					// taAnnot.setTaAnnotatorsRef(taAnnotatorsRef);
					taAnnot.setTaClassRef(annot
					        .getString(GenericAnnotationType.TA_CLASS));
					taAnnot.setTaConfidence(annot
					        .getDouble(GenericAnnotationType.TA_CONFIDENCE));
					taAnnot.setTaIdentRef(annot
					        .getString(GenericAnnotationType.TA_IDENT));
					taAnnotations.add(taAnnot);
				} else if (annot.getType().equals(GenericAnnotationType.ANNOT)) {
					String annotValue = annot.getString(GenericAnnotationType.ANNOT_VALUE);
					if(annotValue != null && annotValue.startsWith("text-analysis|")){
						int index = annotValue.indexOf("text-analysis|") + "text-analysis|".length();
						taAnnot = new TextAnalysisMetaData();
						taAnnot.setTaAnnotatorsRef(annotValue.substring(index));
						taAnnotations.add(taAnnot);
					}
				}

			}
		}
		return taAnnotations;
	}

	public ITSLQIAnnotations retrieveITSLQIAnnotations(ITextUnit tu,
	        TextContainer srcTu, TextContainer tgtTu) {
		ITSLQIAnnotations lqiAnns = tu.getAnnotation(ITSLQIAnnotations.class);
		lqiAnns = lqiAnns == null ? new ITSLQIAnnotations() : lqiAnns;

		ITSLQIAnnotations srcLQIAnns = srcTu
		        .getAnnotation(ITSLQIAnnotations.class);
		if (srcLQIAnns != null) {
			lqiAnns.addAll(srcLQIAnns);
		}
		if (tgtTu != null) {
			ITSLQIAnnotations tgtLQIAnns = tgtTu
			        .getAnnotation(ITSLQIAnnotations.class);
			if (tgtLQIAnns != null) {
				lqiAnns.addAll(tgtLQIAnns);
			}
		}
		return lqiAnns;
	}

	public ITSProvenanceAnnotations retrieveITSProvAnnotations(ITextUnit tu,
	        TextContainer srcTu, TextContainer tgtTu) {
		ITSProvenanceAnnotations provAnns = tu
		        .getAnnotation(ITSProvenanceAnnotations.class);
		provAnns = provAnns == null ? new ITSProvenanceAnnotations() : provAnns;

		ITSProvenanceAnnotations srcProvAnns = srcTu
		        .getAnnotation(ITSProvenanceAnnotations.class);
		if (srcProvAnns != null) {
			provAnns.addAll(srcProvAnns);
		}

		if (tgtTu != null) {
			ITSProvenanceAnnotations tgtProvAnns = tgtTu
			        .getAnnotation(ITSProvenanceAnnotations.class);
			if (tgtProvAnns != null) {
				provAnns.addAll(tgtProvAnns);
			}
		}
		return provAnns;
	}

	public List<GenericAnnotation> retrieveITSMTConfidenceAnnotations(
	        TextContainer tgtTu) {
		GenericAnnotations tgtAnns = tgtTu
		        .getAnnotation(GenericAnnotations.class);
		List<GenericAnnotation> mtAnns = new LinkedList<GenericAnnotation>();
		if (tgtAnns != null) {
			mtAnns = tgtAnns.getAnnotations(GenericAnnotationType.MTCONFIDENCE);
		}
		return mtAnns;
	}

	public TextContainer retrieveOriginalTarget(TextContainer target) {
		AltTranslationsAnnotation altTrans = target
		        .getAnnotation(AltTranslationsAnnotation.class);
		if (altTrans != null) {
			Iterator<AltTranslation> iterAltTrans = altTrans.iterator();
			while (iterAltTrans.hasNext()) {
				AltTranslation altTran = iterAltTrans.next();
				// Check if alt-trans is Ocelot generated.
				XLIFFTool altTool = altTran.getTool();
				if ( /*altTran.getALttransType().equals("previous-version") ||*/ (altTool != null && altTool.getName().equals("Ocelot"))) {
					// We should be able to replace this with |return
					// altTrans.getTarget;|
					// once an issue with the XLIFF reader is fixed (Okapi 412).
					ITextUnit tu = altTran.getEntry();
					for (LocaleId trg : tu.getTargetLocales()) {
						return altTran.getTarget(); // If there is a target
						                            // return it
					}
					// No target: create one empty
					return tu.createTarget(
					        LocaleId.fromString(getTargetLang()), true,
					        IResource.CREATE_EMPTY);
				}
			}
		}
		return null;
	}

	protected XLIFFFilter getFilter() {
		return this.filter;
	}
	
}
