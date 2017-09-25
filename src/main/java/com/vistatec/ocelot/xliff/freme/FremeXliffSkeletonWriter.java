package com.vistatec.ocelot.xliff.freme;

import java.nio.charset.CharsetEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Namespaces;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;
import net.sf.okapi.common.annotation.GenericAnnotations;
import net.sf.okapi.common.encoder.EncoderContext;
import net.sf.okapi.common.filterwriter.ITSContent;
import net.sf.okapi.common.resource.Code;
import net.sf.okapi.common.resource.DocumentPart;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextFragment.TagType;
import net.sf.okapi.common.skeleton.GenericSkeleton;
import net.sf.okapi.common.skeleton.GenericSkeletonPart;
import net.sf.okapi.filters.xliff.XLIFFFilter;
import net.sf.okapi.filters.xliff.XLIFFSkeletonWriter;

public class FremeXliffSkeletonWriter extends XLIFFSkeletonWriter {

	private static final Pattern XLIFF_ELEMENT_PATTERN = Pattern.compile("(.*<xliff)([^>]*)(>.*)");
	private static final Pattern ITSXLF_NAMESPACE_PATTERN = Pattern
			.compile("xmlns(:[^=]+)?=\"" + Namespaces.ITSXLF_NS_URI + "\"");

	private OcelotFilterParameters params;
	private CharsetEncoder chsEnc;
	private ITSContent itsCont;

	public FremeXliffSkeletonWriter(OcelotFilterParameters params) {

		this.params = params;
	}

	@Override
	public String processTextUnit(ITextUnit resource) {

		FremeAnnotations fremeAnnots = resource.getAnnotation(FremeAnnotations.class);
		if (fremeAnnots != null) {
			manageFremeAnnotatorsRef(fremeAnnots, resource);
			manageFremeTripleEnrichments(fremeAnnots, resource);
		}
		return super.processTextUnit(resource);
	}

	private void manageFremeTripleEnrichments(FremeAnnotations fremeAnnots, ITextUnit resource) {

		List<GenericAnnotation> tripleAnnots = fremeAnnots
				.getAnnotations(GenericAnnotationTypeExtended.TRIPLE_ENRICHMENT);
		if (tripleAnnots != null && !tripleAnnots.isEmpty()) {
			GenericSkeleton skeleton = (GenericSkeleton) resource.getSkeleton();
			GenericSkeletonPart lastPart = skeleton.getLastPart();
			int transUnitClosingTagIdx = lastPart.getData().indexOf("</trans-unit>");

			lastPart.getData().insert(transUnitClosingTagIdx,
					getTriplesTag(tripleAnnots.get(0).getString(GenericAnnotationTypeExtended.TRIPLE_VALUE)));
		}
	}

	private String getTriplesTag(String tripleString) {

		StringBuilder triplesTag = new StringBuilder();
		triplesTag.append("<");
		triplesTag.append(EnrichmentAnnotationsConstants.JSON_TAG_NAME);
		triplesTag.append(" ");
		triplesTag.append(EnrichmentAnnotationsConstants.JSON_TAG_DOMAIN_ATTR);
		triplesTag.append("=\"");
		triplesTag.append(EnrichmentAnnotationsConstants.JSON_TAG_DOMAIN);
		triplesTag.append("\">");
//		triplesTag.append(XLIFFFilter.CDATA_START);
		triplesTag.append(StringEscapeUtils.escapeXml11(tripleString));
//		triplesTag.append(XLIFFFilter.CDATA_END);
		triplesTag.append("</");
		triplesTag.append(EnrichmentAnnotationsConstants.JSON_TAG_NAME);
		triplesTag.append(">");
		return triplesTag.toString();
		// return "<ex:json-ld "
	}

	private void manageFremeAnnotatorsRef(FremeAnnotations fremeAnnots, ITextUnit resource) {
		List<GenericAnnotation> annots = fremeAnnots.getAnnotations(GenericAnnotationType.ANNOT);
		if (annots != null && !annots.isEmpty()) {
			String annotatorString = getAnnotatorString(annots);
			if (!resource.getSkeleton().toString().contains(annotatorString)) {
				((GenericSkeleton) resource.getSkeleton()).getFirstPart().append(annotatorString);
			}
		}
	}

	private String getAnnotatorString(List<GenericAnnotation> annots) {

		StringBuilder str = new StringBuilder();
		str.append(" ");
		str.append(Namespaces.ITS_NS_PREFIX);
		str.append(":annotatorsRef=\"");
		for (GenericAnnotation annot : annots) {
			str.append(annot.getString(GenericAnnotationType.ANNOT_VALUE));
			str.append(" ");
		}
		str.insert(str.length() - 1, "\"");

		return str.toString();
	}

	@Override
	protected String expandCodeContent(Code code, LocaleId locToUse, EncoderContext context) {
		// Handle mrk for modifiable attributes
		if (code.hasOnlyAnnotation()) {
			if (code.getTagType() == TagType.OPENING) {
				boolean mtypeNeeded = true;
				StringBuilder tmp = new StringBuilder(code.getOuterData());
				// Existing annotation have outer data
				if (tmp.length() > 0) {
					// So we remove the closing bracket
					tmp.delete(tmp.length() - 1, tmp.length());
					mtypeNeeded = false;
				} else { // New annotation have no outer data
							// So we create it here
					tmp.append("<mrk");
				}
				// Output the live attributes
				outputITSAttributes((GenericAnnotations) code.getAnnotation(GenericAnnotationTypeExtended.ENRICHMENT),
						params.getEscapeGT(), mtypeNeeded, tmp, locToUse, false);
				tmp.append(">");
				return tmp.toString();
			} else {
				return "</mrk>";
			}
		} else { // Normal inline code
			return super.expandCodeContent(code, locToUse, context);
		}
	}

	private void outputITSAttributes(GenericAnnotations anns, boolean escapeGT, boolean mtypeNeeded,
			StringBuilder output, LocaleId trgLocId, boolean writeMTConfAnnotatorsRef) {
		if (itsCont == null) {
			itsCont = new ITSContent(chsEnc, false, true);
		}
		itsCont.outputAnnotations(anns, output, true, true, mtypeNeeded, trgLocId);
		if (writeMTConfAnnotatorsRef) {
			if (anns == null)
				return;
			GenericAnnotation ga = anns.getFirstAnnotation(GenericAnnotationType.MTCONFIDENCE);
			if (ga == null)
				return;
			String ref = ga.getString(GenericAnnotationType.ANNOTATORREF);
			if (ref == null)
				return;
			output.append(" its:annotatorsRef=\"mt-confidence|" + ref + "\"");
		}
	}

	@Override
	public String processDocumentPart(DocumentPart dp) {

		String origSkel = dp.getSkeleton().toString();
		Matcher m = XLIFF_ELEMENT_PATTERN.matcher(origSkel);
		if (m.find()) {
			String xliffAttributes = m.group(2);
			Matcher attrM = ITSXLF_NAMESPACE_PATTERN.matcher(xliffAttributes);
			// If we found the namespace, we don't need to change anything
			if (!attrM.find()) {

				StringBuilder sb = new StringBuilder();
				sb.append(m.group(1));
				sb.append(m.group(2));
				sb.append(" xmlns:").append(Namespaces.ITSXLF_NS_PREFIX).append("=\"").append(Namespaces.ITSXLF_NS_URI)
						.append("\" ");
				sb.append(m.group(3));
				GenericSkeleton newSkel = new GenericSkeleton(sb.toString());
				dp.setSkeleton(newSkel);
			}
		}
		return super.processDocumentPart(dp);
	}

}
