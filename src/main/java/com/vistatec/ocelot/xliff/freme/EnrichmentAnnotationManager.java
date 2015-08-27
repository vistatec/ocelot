package com.vistatec.ocelot.xliff.freme;

import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.resource.TextContainer;

import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.CodeAtom;
import com.vistatec.ocelot.segment.model.Enrichment;
import com.vistatec.ocelot.segment.model.EntityEnrichment;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.segment.model.okapi.TextContainerVariant;
import com.vistatec.ocelot.services.SegmentService;

public class EnrichmentAnnotationManager {

	public void insertEnrichmentAnnotations(SegmentService segmService) {

		OcelotSegment currSegment = null;
		for (int i = 0; i < segmService.getNumSegments(); i++) {
			currSegment = segmService.getSegment(i);
			if (currSegment.getSource() instanceof BaseSegmentVariant) {
				BaseSegmentVariant source = (BaseSegmentVariant) currSegment
				        .getSource();
				insertEnrichmentAnnotations(source);
			}
		}

	}

	private void insertEnrichmentAnnotations(BaseSegmentVariant variant) {

		if (variant.getEnirchments() != null
		        && !variant.getEnirchments().isEmpty()) {
			for (Enrichment enrich : variant.getEnirchments()) {
				if (enrich.getType().equals(EntityEnrichment.ENRICHMENT_TYPE)) {
					List<SegmentAtom> newAtoms = new ArrayList<SegmentAtom>();
					// atoms.addAll(variant.getAtoms());
					List<SegmentAtom> intermediateAtoms = new ArrayList<SegmentAtom>();
					TextAtom firstTextAtom = null;
					int totalLength = 0;
					boolean annotationInserted = false;
					for (SegmentAtom atom : variant.getAtoms()) {
						if (atom instanceof TextAtom) {
							if (!annotationInserted && atom.getData().length() + totalLength >= enrich
							        .getOffsetStartIdx()) {
								if (firstTextAtom == null) {
									firstTextAtom = (TextAtom) atom;
								}
							} else {
								newAtoms.add(atom);
							}
							if (atom.getData().length() + totalLength >= enrich
							        .getOffsetEndIdx()) {
								String firstTextPart = firstTextAtom.getData()
								        .substring(
								                0,
								                enrich.getOffsetStartIdx()
								                        - totalLength);
								if (!firstTextPart.isEmpty()) {
									newAtoms.add(new TextAtom(firstTextPart));
								}
								CodeAtom openingCodeAtom = new CodeAtom("0",
								        "<mrk>",
								        "<mrk type=\""
								                + enrich.getTagType()
								                + "\" "
								                + enrich.getTag()
								                + "=\""
								                + ((EntityEnrichment) enrich)
								                        .getEntityURL()
								                + "\" >");
								newAtoms.add(openingCodeAtom);
								if (firstTextAtom.equals(atom)) {
									newAtoms.add(new TextAtom(firstTextAtom
									        .getData().substring(
									                enrich.getOffsetStartIdx()
									                        - totalLength,
									                enrich.getOffsetEndIdx()
									                        - totalLength)));
								} else {
									newAtoms.add(new TextAtom(firstTextAtom
									        .getData().substring(
									                enrich.getOffsetStartIdx()
									                        - totalLength)));
//									int startIdx = variant.getAtoms().indexOf(
//									        firstTextAtom);
//									int endIdx = variant.getAtoms().indexOf(
//									        atom);
//									for (int i = startIdx + 1; i < endIdx; i++) {
//										newAtoms.add(variant.getAtoms().get(i));
//									}
									newAtoms.addAll(intermediateAtoms);
									newAtoms.add(new TextAtom(atom.getData()
									        .substring(
									                0,
									                enrich.getOffsetEndIdx()
									                        - totalLength)));
								}

								newAtoms.add(new CodeAtom("0", "</mrk>", ""));
								String lastTextPart = atom.getData().substring(
								        enrich.getOffsetEndIdx() - totalLength);
								if (!lastTextPart.isEmpty()) {
									newAtoms.add(new TextAtom(lastTextPart));
								}
								annotationInserted = true;
							} else {
								if(!atom.equals(firstTextAtom)){
									intermediateAtoms.add(atom);
								}
							}
							totalLength += atom.getData().length();
						} else {
							if(firstTextAtom == null || annotationInserted){
								newAtoms.add(atom);
							} else {
								intermediateAtoms.add(atom);
							}
						}
					}
					((TextContainerVariant) variant).setAtoms(newAtoms);
				}
			}
		}
	}
}
