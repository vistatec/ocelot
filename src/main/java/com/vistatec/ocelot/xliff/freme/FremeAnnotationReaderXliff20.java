package com.vistatec.ocelot.xliff.freme;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.okapi.FragmentVariant;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.lib.xliff2.core.ExtElement;
import net.sf.okapi.lib.xliff2.core.ExtElements;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.MTag;
import net.sf.okapi.lib.xliff2.core.Part;
import net.sf.okapi.lib.xliff2.core.Tag;
import net.sf.okapi.lib.xliff2.core.TagType;
import net.sf.okapi.lib.xliff2.core.Unit;
import net.sf.okapi.lib.xliff2.its.DataCategories;
import net.sf.okapi.lib.xliff2.its.IITSItem;
import net.sf.okapi.lib.xliff2.its.TermTag;
import net.sf.okapi.lib.xliff2.its.TextAnalysis;

public class FremeAnnotationReaderXliff20 extends FremeAnnotationsReader {

	private Map<Fragment, List<Enrichment>> enrichmentsMap;

	public FremeAnnotationReaderXliff20(String sourceLang, String targetLang) {
		super(sourceLang, targetLang);
	}

	public Map<Fragment, List<Enrichment>> readEnrichments(Unit unit, Part unitPart, String segmentId) {

		enrichmentsMap = new HashMap<>();
		Model tripleModel = findTripleModel(unit, segmentId);
		enrichmentsMap.put(unitPart.getSource(),
				readEnrichments(unitPart.getSource(), LocaleId.fromString(sourceLang).getLanguage(), tripleModel, false));
		if (unitPart.getTarget() != null) {
			enrichmentsMap.put(unitPart.getTarget(),
					readEnrichments(unitPart.getTarget(), LocaleId.fromString(targetLang).getLanguage(), tripleModel, true));
		}
		return enrichmentsMap;
	}

	private List<Enrichment> readEnrichments(Fragment frag, String language, Model tripleModel, boolean isTarget) {

		List<Enrichment> enrichments = new ArrayList<>();
		List<Tag> tagsToDelete = new ArrayList<>();
		if (frag.getTags() != null) {
			for (Tag tag : frag.getOwnTags()) {
				if (tag.getTagType().equals(TagType.OPENING) && isFremeAnnotCandidate(tag)) {

					int tagPosition = getTagPosition(frag, tag);
					int startIndex = getPositionInPlainText(tagPosition, frag);
					Tag closingTag = frag.getClosingTag(tag);
					int closingTagPosition = getTagPosition(frag, closingTag);
					int endIndex = getPositionInPlainText(closingTagPosition, frag);
					String enrichmedText = frag.getPlainText().substring(startIndex, endIndex);
					System.out.println("Enriched Text: " + enrichmedText);
					if(tag instanceof TermTag){
						enrichments.addAll(retrieveTermEnrichments(((TermTag) tag).getTermInfoRef(), startIndex,
								endIndex, tripleModel));
						tagsToDelete.add(tag);
						tagsToDelete.add(closingTag);
					} else if (((MTag) tag).hasITSItem()){
						
						enrichments.addAll(readEntityAndLinkEnrichments((MTag) tag, (MTag) closingTag, startIndex,
								endIndex, tripleModel, language));
						if (((MTag) tag).getITSItems().isEmpty()) {
							tagsToDelete.add(tag);
							tagsToDelete.add(closingTag);
						}
					}
				}
			}
		}
		if (!tagsToDelete.isEmpty()) {
			for (Tag tag : tagsToDelete) {
				frag.remove(tag);
			}
		}
//		FragmentVariant fragVariant = new FragmentVariant(frag, isTarget);
//		fragVariant.getDisplayText()
		
		return enrichments;
	}

	private boolean isFremeAnnotCandidate(Tag tag) {
		return tag.getType() != null && (tag.getType().equals("its:any") || tag.getType().equals("term"));
	}

	private List<Enrichment> readEntityAndLinkEnrichments(MTag openingTag, MTag closingTag, int startIndex,
			int endIndex, Model tripleModel, String language) {

		List<Enrichment> enrichments = new ArrayList<>();
		Iterator<IITSItem> itsItemIt = openingTag.getITSItems().iterator();
		List<IITSItem> itsItemToDelete = new ArrayList<>();
		IITSItem currItem = null;
		while (itsItemIt.hasNext()) {
			currItem = itsItemIt.next();
			if (currItem.getDataCategoryName().equals(DataCategories.TEXTANALYSIS)) {
				TextAnalysis taItem = (TextAnalysis) currItem;
				EntityEnrichment entityEnrich = new EntityEnrichment(taItem.getTaIdentRef());
				entityEnrich.setOffsetNoTagsStartIdx(startIndex);
				entityEnrich.setOffsetNoTagsEndIdx(endIndex);
				entityEnrich.setAnnotatorRef(taItem.getAnnotatorRef());
				enrichments.add(entityEnrich);
				LinkEnrichment linkEnrich = retrieveLinkEnrichment(entityEnrich.getEntityURL(), tripleModel, language, startIndex, endIndex);
				if (linkEnrich != null) {
					enrichments.add(linkEnrich);
				}
				itsItemToDelete.add(currItem);
			}
		}
		if (!itsItemToDelete.isEmpty()) {
			for (IITSItem item : itsItemToDelete) {
				openingTag.getITSItems().remove(item);
				IITSItem itemInClosingTag = findItsItem(item, closingTag);
				if (itemInClosingTag != null) {
					closingTag.getITSItems().remove(itemInClosingTag);
				}
			}
		}
		return enrichments;
	}

	private IITSItem findItsItem(IITSItem item, MTag tag) {

		IITSItem foundItem = null;
		if (tag.hasITSItem()) {
			IITSItem currItem = null;
			Iterator<IITSItem> itsItemIt = tag.getITSItems().iterator();
			while (itsItemIt.hasNext() && foundItem == null) {
				currItem = itsItemIt.next();
				if (currItem.getDataCategoryName().equals(item.getDataCategoryName())) {
					if (currItem.getDataCategoryName().equals(DataCategories.TEXTANALYSIS)
							&& areEqualTaItems((TextAnalysis) currItem, (TextAnalysis) item)) {
						foundItem = currItem;
					}
				}
			}
		}

		return foundItem;
	}

	private boolean areEqualTaItems(TextAnalysis ta1, TextAnalysis ta2) {

		return ta1.getTaIdentRef().equals(ta2.getTaIdentRef());
	}

	private int getTagPosition(Fragment frag, Tag tag) {

		int tagKey = frag.getTags().getKey(tag);
		char tagChar1 = Fragment.toChar1(tagKey);
		char tagChar2 = Fragment.toChar2(tagKey); 
		return frag.getCodedText().indexOf(new String(new char[]{tagChar1, tagChar2}));
	}

	private int getPositionInPlainText(int positionInCodedText, Fragment frag) {

		if (!frag.hasTag()) {
			return positionInCodedText;
		}
		int len = 0;
		String text = frag.getCodedText();
		for (int i = 0; i < text.length(); i++) {
			if (i > positionInCodedText) {
				return len;
			}
			if (Fragment.isChar1(text.charAt(i))) {
				i++;
			} else {
				len++;
			}
		}
		return len;
	}


	private Model findTripleModel(Unit unit, String segmentId) {

		Model tripleModel = ModelFactory.createDefaultModel();
		if (!unit.getExtElements().isEmpty()) {
			Iterator<ExtElement> extElIt = unit.getExtElements().iterator();
			// ExtElement segExtEl = null;
			ExtElement currExtEl = null;
			ExtElements newExtElements = new ExtElements();
			while (extElIt.hasNext()) {
				currExtEl = extElIt.next();
				if (currExtEl.getQName().getLocalPart().equals(EnrichmentAnnotationsConstants.JSON_TAG_LOCAL_NAME)) {
					if (!currExtEl.getAttributes().isEmpty()) {
						String segId = currExtEl.getAttributes().getAttributeValue(
								"",
								EnrichmentAnnotationsConstants.JSON_TAG_SEG_ATTR);
						if (segmentId.equals(segId)) {
							tripleModel = readTripleModel(currExtEl);
						} else {
							newExtElements.add(currExtEl);
						}
					} else {
						newExtElements.add(currExtEl);
					}
				} else {
					newExtElements.add(currExtEl);
				}
			}
			unit.setExtElements(newExtElements);
		}
		return tripleModel;
	}

	private Model readTripleModel(ExtElement currExtEl) {

		Model tripleModel = ModelFactory.createDefaultModel();
		String jsonText = currExtEl.getFirstContent().getText();
		System.out.println("**************");
		System.out.println(jsonText);
		System.out.println("**************");
		tripleModel.read(new StringReader(jsonText), null, EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
		return tripleModel;
	}
}
