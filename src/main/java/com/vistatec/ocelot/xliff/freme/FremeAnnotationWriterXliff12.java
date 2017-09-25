package com.vistatec.ocelot.xliff.freme;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.vistatec.ocelot.config.ConfigurationException;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;
import com.vistatec.ocelot.segment.model.okapi.OkapiSegment;
import com.vistatec.ocelot.xliff.okapi.OkapiXLIFF12Parser;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.common.resource.TextPart;

public class FremeAnnotationWriterXliff12 extends XliffAnnotationWriter {

	private final Logger logger = LoggerFactory.getLogger(FremeAnnotationWriterXliff12.class);

	private OkapiXLIFF12Parser xliffParser;

//	private Map<Integer, Integer> termsenrichnmentsMap;
	
//	private List<Enrichment> tripleEnrichments;
//	private Model tripleModel;

	public FremeAnnotationWriterXliff12(OkapiXLIFF12Parser parser) {

		this.xliffParser = parser;
	}

	public void writeAnnotations(OcelotSegment segment) {

		if (isEnriched(segment)) {

			Event event = xliffParser.getSegmentEvent(((OkapiSegment) segment).eventNum);
			if (event == null) {
				logger.error("Failed to find Okapi Event associated with segment #" + segment.getSegmentNumber());

			} else if (event.isTextUnit()) {
//				tripleEnrichments = new ArrayList<>();
				tripleModel = ModelFactory.createDefaultModel();
				ITextUnit textUnit = event.getTextUnit();
				if (((BaseSegmentVariant) segment.getSource()).isEnriched()) {
					writeAnnotations((BaseSegmentVariant) segment.getSource(), textUnit.getSource(), textUnit);
				}
				if (((BaseSegmentVariant) segment.getTarget()).isEnriched()) {
					writeAnnotations((BaseSegmentVariant) segment.getTarget(),
							textUnit.getTarget(textUnit.getTargetLocales().iterator().next()), textUnit);
				}
//				writeTripleEnrichments(textUnit, tripleEnrichments);
				writeTripleEnrichments(textUnit );
			}
		}
	}


	private void writeTripleEnrichments(ITextUnit textUnit) {

		if(!tripleModel.isEmpty()){
			FremeAnnotations fremeAnnots = textUnit.getAnnotation(FremeAnnotations.class);
			if (fremeAnnots == null) {
				fremeAnnots = new FremeAnnotations();
				textUnit.setAnnotation(fremeAnnots);
			}
			GenericAnnotation tripleAnnotation = new GenericAnnotation(GenericAnnotationTypeExtended.TRIPLE_ENRICHMENT,
					GenericAnnotationTypeExtended.TRIPLE_VALUE, getModelJsonString());
			fremeAnnots.add(tripleAnnotation);
		}
	}

	private void writeAnnotations(BaseSegmentVariant variant, TextContainer textContainer, ITextUnit textUnit) {
		termsEnrichnmentsMap = new HashMap<>();
		for (Enrichment enrich : variant.getEnirchments()) {

			switch (enrich.getType()) {
			case Enrichment.ENTITY_TYPE:
				writeEntityEnrichment((EntityEnrichment) enrich, textContainer, textUnit);
				break;
			case Enrichment.TERMINOLOGY_TYPE:
				writeTerminologyEnrichment((TerminologyEnrichment) enrich, textContainer, textUnit);
//				tripleEnrichments.add(enrich);
				break;
			case Enrichment.LINK_TYPE:
				tripleModel.add(((LinkEnrichment)enrich).getPropertiesModel());
//				tripleEnrichments.add(enrich);
				break;
			default:
				break;
			}
		}
	}

//	private void writeTripleEnrichments(ITextUnit textUnit, List<Enrichment> tripleEnrichments) {
//
//		if (!tripleEnrichments.isEmpty()) {
//			Model tripleEnrichModel = ModelFactory.createDefaultModel();
//			for (Enrichment tripleEnrich : tripleEnrichments) {
//				if (!tripleEnrich.isDisabled()) {
//					if (tripleEnrich.getType().equals(Enrichment.TERMINOLOGY_TYPE)) {
//						tripleEnrichModel.add(((TerminologyEnrichment) tripleEnrich).getTermTriples());
//					} else if (tripleEnrich.getType().equals(Enrichment.LINK_TYPE)) {
//						tripleEnrichModel.add(((LinkEnrichment) tripleEnrich).getPropertiesModel());
//					}
//				}
//			}
//			FremeAnnotations fremeAnnots = textUnit.getAnnotation(FremeAnnotations.class);
//			if (fremeAnnots == null) {
//				fremeAnnots = new FremeAnnotations();
//				textUnit.setAnnotation(fremeAnnots);
//			}
//			StringWriter writer = new StringWriter();
//			tripleEnrichModel.write(writer, EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
//			GenericAnnotation tripleAnnotation = new GenericAnnotation(GenericAnnotationTypeExtended.TRIPLE_ENRICHMENT,
//					GenericAnnotationTypeExtended.TRIPLE_VALUE, writer.toString());
//			fremeAnnots.add(tripleAnnotation);
//		}
//
//	}

	private void writeTerminologyEnrichment(TerminologyEnrichment enrich, TextContainer textContainer,
			ITextUnit textUnit) {

		if (hasToWriteTermEnrichment(enrich)) {
			GenericAnnotation termAnnot = new GenericAnnotation(GenericAnnotationType.TERM);
			termAnnot.setString(GenericAnnotationType.TERM_INFO, enrich.getTermInfoRef());
			termAnnot.setString(EnrichmentAnnotationsConstants.ANNOT_ORIGIN,
					EnrichmentAnnotationsConstants.FREME_ORIGIN);
			TextFragment fragment = getFragment(enrich.getOffsetNoTagsStartIdx(), textContainer);
			int startFragIndex = toFragmentIndex(enrich.getOffsetNoTagsStartIdx(), textContainer, fragment);
			int startAnnotIndex = toCodedIndex(startFragIndex, fragment);
			int endFragIndex = toFragmentIndex(enrich.getOffsetNoTagsEndIdx(), textContainer, fragment);
			int endAnnotIndex = toCodedIndex(endFragIndex, fragment);
			fragment.annotate(startAnnotIndex, endAnnotIndex, GenericAnnotationTypeExtended.ENRICHMENT,
					new FremeAnnotations(termAnnot));
			termsEnrichnmentsMap.put(enrich.getOffsetNoTagsStartIdx(), enrich.getOffsetNoTagsEndIdx());
		}
		tripleModel.add(enrich.getTermTriples());
	}


	private void writeEntityEnrichment(EntityEnrichment enrich, TextContainer textContainer, ITextUnit textUnit) {

		FremeAnnotations annotators = textUnit.getAnnotation(FremeAnnotations.class);
		if (annotators == null) {
			annotators = new FremeAnnotations();
			textUnit.setAnnotation(annotators);
			GenericAnnotation annotRefAnnotation = new GenericAnnotation(GenericAnnotationType.ANNOT);
			annotRefAnnotation.setString(GenericAnnotationType.ANNOT_VALUE, enrich.getAnnotatorsRefValue());
			annotators.add(annotRefAnnotation);

		}
		TextFragment fragment = getFragment(enrich.getOffsetNoTagsStartIdx(), textContainer);
		if (fragment != null) {
			GenericAnnotation entityAnnot = new GenericAnnotation(GenericAnnotationType.TA,
					GenericAnnotationType.TA_IDENT, "REF:" + enrich.getEntityURL());
			entityAnnot.setString(EnrichmentAnnotationsConstants.ANNOT_ORIGIN,
					EnrichmentAnnotationsConstants.FREME_ORIGIN);
			int startFragIndex = toFragmentIndex(enrich.getOffsetNoTagsStartIdx(), textContainer, fragment);
			int startAnnotIndex = toCodedIndex(startFragIndex, fragment);
			int endFragIndex = toFragmentIndex(enrich.getOffsetNoTagsEndIdx(), textContainer, fragment);
			int endAnnotIndex = toCodedIndex(endFragIndex, fragment);
			fragment.annotate(startAnnotIndex, endAnnotIndex, GenericAnnotationTypeExtended.ENRICHMENT,
					new FremeAnnotations(entityAnnot));
		} else {
			logger.warn("Impossible to find text fragment for the opening tag of enrichment " + enrich.toString());
		}

	}

	private TextFragment getFragment(int offset, TextContainer textContainer) {

		TextFragment fragment = null;
		int textIndex = 0;
		for (TextPart part : textContainer.getParts()) {
			TextFragment currFrag = part.getContent();
			if (offset < currFrag.getText().length() + textIndex) {
				fragment = currFrag;
			} else {
				textIndex = currFrag.getText().length();
			}
		}
		return fragment;
	}


	private int toFragmentIndex(int index, TextContainer textContainer, TextFragment textFragment) {

		int fragIndex = -1;
		if (textContainer.getParts().size() == 1) {
			fragIndex = index;
		} else {
			int totOffset = 0;
			for (TextPart part : textContainer.getParts()) {
				if (part.getContent().getText().equals(textFragment)) {
					fragIndex = index - totOffset;
				} else {
					totOffset = totOffset + part.getContent().getText().length();
				}
			}
		}

		return fragIndex;
	}

	private int toCodedIndex(int index, TextFragment frag) {

		Integer codedIndex = null;
		if (!frag.hasCode()) {
			codedIndex = index;
		} else {
			int stringOffset = 0;
			int currCodedStrIndex = 0;
			String codedString = frag.getCodedText();
			while (currCodedStrIndex < codedString.length() && codedIndex == null) {
				if (TextFragment.isMarker(codedString.charAt(currCodedStrIndex))) {
					currCodedStrIndex++;
					currCodedStrIndex++;
				} else {
					if (stringOffset == index) {
						codedIndex = currCodedStrIndex;
					} else {
						stringOffset++;
						currCodedStrIndex++;
					}
				}
			}
			if (codedIndex == null) {
				codedIndex = codedString.length();
			}

		}
		return codedIndex;
	}

	public static void main(String[] args) throws ConfigurationException, XMLStreamException, IOException {
		
		InputStream is = new FileInputStream(new File("C:\\Users\\martab\\Desktop", "json.json"));
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		String json = StringEscapeUtils.unescapeXml(writer.toString());
		System.out.println(json);
		//
		// try {
		// // ConfigurationManager configurationManager = new
		// // ConfigurationManager();
		// // configurationManager.readAndCheckConfiguration(new
		// // File(System.getProperty("user.home")+ File.separator +
		// // ".ocelot"));
		// // OkapiXliffService xliffService = new
		// // OkapiXliffService(configurationManager.getOcelotConfigService(),
		// // new EventBusWrapper(null));
		// // XLIFFDocument document = xliffService.parse(new
		// // File(System.getProperty("user.home") + File.separator +
		// // "Projects" + File.separator + "xliff",
		// // "altTrans.xlf"));
		// // List<OcelotSegment> segments = document.getSegments();
		//
		// OkapiXLIFF12Parser parser = new OkapiXLIFF12Parser();
		// List<OcelotSegment> segments = parser.parse(
		// new File(System.getProperty("user.home") + File.separator +
		// "Projects" + File.separator + "xliff",
		// "altTrans-enrichments.xlf"),
		// true);
		// EntityEnrichment entity = new
		// EntityEnrichment("http://dbpedia.org/resource/sentence");
		// entity.setOffsetStartIdx(21);
		// entity.setOffsetEndIdx(29);
		//
		// // ((BaseSegmentVariant)
		// // segments.get(0).getSource()).addEnrichment(entity);
		// // TerminologyEnrichment termEnrich = new TerminologyEnrichment();
		// // termEnrich.setOffsetStartIdx(21);
		// // termEnrich.setOffsetEndIdx(29);
		// // termEnrich.setTermInfoRef("http://freme-project.eu/#char=21,29");
		// // ((BaseSegmentVariant)
		// // segments.get(0).getSource()).addEnrichment(termEnrich);
		//
		// InputStream modelFile = new FileInputStream(new
		// File("C:\\Users\\martab\\Desktop\\model.ttl"));
		// Model model = ModelFactory.createDefaultModel();
		// model.read(modelFile, null, "TTL");
		// List<Enrichment> enrichments =
		// EnrichmentUtil.retrieveEnrichments(model, "en", "es", false);
		//// enrichments.add(entity);
		// ((BaseSegmentVariant) segments.get(0).getSource()).setEnrichments(new
		// HashSet<>(enrichments));
		// Xliff1_2FremeAnnotationWriter annotWriter = new
		// Xliff1_2FremeAnnotationWriter(parser);
		// annotWriter.writeAnnotations(segments.get(0));
		// OkapiXLIFF12Writer writer = new OkapiXLIFF12Writer(parser, new
		// UserProvenance("", "", ""),
		// new EventBusWrapper(null));
		// writer.save(
		// new File(System.getProperty("user.home") + File.separator +
		// "Projects" + File.separator + "xliff",
		// "altTrans-2.xlf"),
		// true);
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// GenericAnnotation annotRefAnnotation = new
		// GenericAnnotation(GenericAnnotationType.ANNOT);
		// annotRefAnnotation.setString(GenericAnnotationType.ANNOT_VALUE,
		// "text-analysis|http://spotlight.dbpedia.org/");
		// System.out.println(annotRefAnnotation);
		// System.out.println(GenericAnnotationType.ANNOT_VALUE + "=\"" +
		// annotRefAnnotation.getString(GenericAnnotationType.ANNOT_VALUE) +
		// "\"");
		// System.out.println(annotRefAnnotation.getType());
	}

	@Override
	public void writeAnnotations(List<OcelotSegment> segments) {
		for (OcelotSegment segment : segments) {
			writeAnnotations(segment);
		}
	}
}
