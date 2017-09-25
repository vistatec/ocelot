package com.vistatec.ocelot.xliff.freme;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;
import com.vistatec.ocelot.segment.model.okapi.OkapiSegment;
import com.vistatec.ocelot.xliff.okapi.OkapiXLIFF20Parser;

import net.sf.okapi.lib.xliff2.core.ExtElement;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.MTag;
import net.sf.okapi.lib.xliff2.core.Segment;
import net.sf.okapi.lib.xliff2.core.Unit;
import net.sf.okapi.lib.xliff2.its.AnnotatorsRef;
import net.sf.okapi.lib.xliff2.its.DataCategories;
import net.sf.okapi.lib.xliff2.its.TextAnalysis;
import net.sf.okapi.lib.xliff2.reader.Event;

public class FremeAnnotationsWriterXliff20 extends XliffAnnotationWriter {

	private final Logger logger = LoggerFactory.getLogger(FremeAnnotationWriterXliff12.class);

	private OkapiXLIFF20Parser xliffParser;


	public FremeAnnotationsWriterXliff20(OkapiXLIFF20Parser xliffParser) {

		this.xliffParser = xliffParser;
	}

	public void writeAnnotations(OcelotSegment segment) {

		if (isEnriched(segment)) {
			// Event event = xliffParser.getSegmentEvent(((OkapiSegment)
			// segment).eventNum);
			Segment unitPart = this.xliffParser.getSegmentUnitPart(((OkapiSegment) segment).eventNum);
			Event event = xliffParser.getSegmentEvent(segment.getSegmentNumber());
			if (unitPart == null) {
				logger.error("Failed to find Okapi Unit Part associated with segment #" + segment.getSegmentNumber());

			} else if (event == null) {
				logger.error("Failed to find Okapi Unit associated with segment #" + segment.getSegmentNumber());
			} else if (event.isUnit() && unitPart.isSegment()) {
				Unit unit = event.getUnit();
				tripleModel = ModelFactory.createDefaultModel();
				if (((BaseSegmentVariant) segment.getSource()).isEnriched()) {
					writeAnnotations((BaseSegmentVariant) segment.getSource(), unitPart.getSource(), unitPart);
				}
				if (((BaseSegmentVariant) segment.getTarget()).isEnriched()) {
					writeAnnotations((BaseSegmentVariant) segment.getTarget(), unitPart.getTarget(), unitPart);
				}
				writeTripleEnrichments(unit, segment.getSegmentId());
			}
		}
	}


	private void writeTripleEnrichments(Unit unit, String segmentId) {

		if (!tripleModel.isEmpty()) {
			ExtElement extEl = new ExtElement(new QName(EnrichmentAnnotationsConstants.JSON_TAG_DOMAIN,
					EnrichmentAnnotationsConstants.JSON_TAG_LOCAL_NAME,
					EnrichmentAnnotationsConstants.JSON_TAG_PREFIX));
			extEl.getAttributes().setAttribute("segment", segmentId);
			extEl.addContent(getModelJsonString());
			unit.getExtElements().add(extEl);
		}
	}

	private void writeAnnotations(BaseSegmentVariant variant, Fragment fragment, Segment unitPart) {

		termsEnrichnmentsMap = new HashMap<>();
		for (Enrichment enrichment : variant.getEnirchments()) {
			switch (enrichment.getType()) {
			case Enrichment.ENTITY_TYPE:
				writeEntityEnrichment(variant, fragment, unitPart, (EntityEnrichment) enrichment);
				break;
			case Enrichment.TERMINOLOGY_TYPE:
				writeTerminologyEnrichment(variant, fragment, unitPart, (TerminologyEnrichment) enrichment);
				break;
			case Enrichment.LINK_TYPE:
				tripleModel.add(((LinkEnrichment) enrichment).getPropertiesModel());
				break;
			default:
				break;
			}
		}
	}

	private void writeTerminologyEnrichment(BaseSegmentVariant variant, Fragment fragment, Segment unitPart,
			TerminologyEnrichment enrichment) {

		// TermTag tag = new TermTag(fragment.getStore().suggestId(false));
		// tag.setTermInfoRef(enrichment.getTermInfoRef());
		// fragment.annotate(fragment.getCodedTextPosition(enrichment.getOffsetStartIdx(),
		// false),
		// fragment.getCodedTextPosition(enrichment.getOffsetEndIdx(), true),
		// tag);
		if (hasToWriteTermEnrichment(enrichment)) {
			MTag tag = fragment.getOrCreateMarker(
					fragment.getCodedTextPosition(enrichment.getOffsetNoTagsStartIdx(), false),
					fragment.getCodedTextPosition(enrichment.getOffsetNoTagsEndIdx(), true), "term", "term");
			tag.setRef(enrichment.getTermInfoRef());
			termsEnrichnmentsMap.put(enrichment.getOffsetNoTagsStartIdx(), enrichment.getOffsetNoTagsEndIdx());
		}
		tripleModel.add(enrichment.getTermTriples());
	}

	private boolean hasToWriteTermEnrichment(TerminologyEnrichment termEnric) {

		return !termsEnrichnmentsMap.containsKey(termEnric.getOffsetNoTagsStartIdx()) || termsEnrichnmentsMap
				.get(termEnric.getOffsetNoTagsStartIdx()).intValue() != termEnric.getOffsetNoTagsEndIdx();
	}

	private void writeEntityEnrichment(BaseSegmentVariant variant, Fragment fragment, Segment unitPart,
			EntityEnrichment enrichment) {

		MTag tag = fragment.getOrCreateMarker(
				fragment.getCodedTextPosition(enrichment.getOffsetNoTagsStartIdx(), false),
				fragment.getCodedTextPosition(enrichment.getOffsetNoTagsEndIdx(), true), "its:any", "its:any");
		TextAnalysis ta = new TextAnalysis();
		AnnotatorsRef annotRef = new AnnotatorsRef();
		annotRef.set(DataCategories.TEXTANALYSIS, enrichment.getAnnotatorRef());
		ta.setAnnotatorRef(annotRef);
		ta.setTaIdentRef(enrichment.getEntityURL());
		tag.getITSItems().add(ta);

	}	

	public static void main(String[] args) throws IOException {

		// OkapiXLIFF20Parser parser = new OkapiXLIFF20Parser();
		// List<OcelotSegment> segments = parser.parse(
		// new File(System.getProperty("user.home") + File.separator +
		// "Projects" + File.separator + "xliff",
		// "test2.0.xlf"));
		// EntityEnrichment entity = new
		// EntityEnrichment("http://dbpedia.org/resource/Sentence");
		// entity.setOffsetStartIdx(12);
		// entity.setOffsetEndIdx(20);
		// ((BaseSegmentVariant)segments.get(0).getSource()).addEnrichment(entity);
		//
		// TerminologyEnrichment termEnrich = new TerminologyEnrichment();
		// termEnrich.setOffsetStartIdx(0);
		// termEnrich.setOffsetEndIdx(8);
		// termEnrich.setTermInfoRef("http://freme-project.eu/#char=0,8");
		// ((BaseSegmentVariant)segments.get(0).getSource()).addEnrichment(termEnrich);
		//
		// Xliff2_0FremeAnnotsWriter annotWriter = new
		// Xliff2_0FremeAnnotsWriter(parser);
		// annotWriter.writeAnnotations(segments.get(0));
		// OkapiXLIFF20Writer writer = new OkapiXLIFF20Writer(parser, new
		// UserProvenance("", "", ""), new EventBusWrapper(null));
		// writer.save(new File(System.getProperty("user.home") + File.separator
		// + "Projects" + File.separator + "xliff",
		// "test2.0-entity.xlf"));
	}

	@Override
	public void writeAnnotations(List<OcelotSegment> segments) {
		for (OcelotSegment segment : segments) {
			writeAnnotations(segment);
		}
	}
}
