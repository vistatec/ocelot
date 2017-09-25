package com.vistatec.ocelot.xliff.freme;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;

public abstract class XliffAnnotationWriter {

	protected Map<Integer, Integer> termsEnrichnmentsMap;

	protected Model tripleModel;
	
	
	public void writeAnnotations(List<OcelotSegment> segments) {
		for (OcelotSegment segment : segments) {
			writeAnnotations(segment);
		}
	}
	
	protected abstract void writeAnnotations(OcelotSegment segment);
	
	protected boolean isEnriched(OcelotSegment segment) {
		return ((BaseSegmentVariant) segment.getSource()).isEnriched()
				|| ((BaseSegmentVariant) segment.getTarget()).isEnriched();
	}
	
	protected boolean hasToWriteTermEnrichment(Enrichment enrich) {

		return !termsEnrichnmentsMap.containsKey(enrich.getOffsetNoTagsStartIdx()) || termsEnrichnmentsMap
				.get(enrich.getOffsetNoTagsStartIdx()).intValue() != enrich.getOffsetNoTagsEndIdx();
	}
	
	protected String getModelJsonString() {
		
		StringWriter writer = new StringWriter();
		tripleModel.write(writer, EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
		return writer.toString();
	}
}
