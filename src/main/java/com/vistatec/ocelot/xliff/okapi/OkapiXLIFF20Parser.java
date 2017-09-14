/*
 * Copyright (C) 2014-2015, VistaTEC or third-party contributors as indicated
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.icu.text.SimpleDateFormat;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.its.model.okapi.OkapiProvenance;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.okapi.FragmentVariant;
import com.vistatec.ocelot.segment.model.okapi.Notes;
import com.vistatec.ocelot.segment.model.okapi.OcelotRevision;
import com.vistatec.ocelot.segment.model.okapi.OkapiSegment;
import com.vistatec.ocelot.xliff.XLIFFParser;
import com.vistatec.ocelot.xliff.freme.EnrichmentConverterXLIFF20;

import net.sf.okapi.lib.xliff2.Const;
import net.sf.okapi.lib.xliff2.changeTracking.ChangeTrack;
import net.sf.okapi.lib.xliff2.changeTracking.Item;
import net.sf.okapi.lib.xliff2.changeTracking.Revision;
import net.sf.okapi.lib.xliff2.changeTracking.Revisions;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.MTag;
import net.sf.okapi.lib.xliff2.core.Note;
import net.sf.okapi.lib.xliff2.core.Part;
import net.sf.okapi.lib.xliff2.core.Segment;
import net.sf.okapi.lib.xliff2.core.StartFileData;
import net.sf.okapi.lib.xliff2.core.StartXliffData;
import net.sf.okapi.lib.xliff2.core.Tag;
import net.sf.okapi.lib.xliff2.core.TagType;
import net.sf.okapi.lib.xliff2.core.Unit;
import net.sf.okapi.lib.xliff2.its.IITSItem;
import net.sf.okapi.lib.xliff2.its.LocQualityIssue;
import net.sf.okapi.lib.xliff2.its.LocQualityIssues;
import net.sf.okapi.lib.xliff2.its.Provenances;
import net.sf.okapi.lib.xliff2.reader.Event;
import net.sf.okapi.lib.xliff2.reader.XLIFFReader;

/**
 * Parse XLIFF 2.0 file for use in the workbench.
 */
public class OkapiXLIFF20Parser implements XLIFFParser {
	private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX";
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATETIME_PATTERN);
	private List<Event> events;
	private List<net.sf.okapi.lib.xliff2.core.Segment> segmentUnitParts;
	// private List<TargetVersion> targetVersions;
	private Map<String, TargetVersion> targetVersions;
	private Map<Integer, Integer> segmentEventMapping;
	private int documentSegmentNum;
	private String sourceLang, targetLang;
	private String originalFileName;
	private EnrichmentConverterXLIFF20 enrichmentConverter;

	public List<Event> getEvents() {
		return this.events;
	}

	public Event getSegmentEvent(int segEventNumber) {
		return this.events.get(segmentEventMapping.get(segEventNumber));
	}

	public net.sf.okapi.lib.xliff2.core.Segment getSegmentUnitPart(int segmentUnitPartIndex) {
		return this.segmentUnitParts.get(segmentUnitPartIndex);
	}

	public TargetVersion getTargetVersion(String segmentRef) {
		return this.targetVersions.get(segmentRef);
	}
	// public TargetVersion getTargetVersion(int segmentUnitPartIndex){
	// return this.targetVersions.get(segmentUnitPartIndex);
	// }

	@Override
	public List<OcelotSegment> parse(File xliffFile) throws IOException {
		List<OcelotSegment> segments = new LinkedList<>();
		segmentEventMapping = new HashMap<Integer, Integer>();
		events = new LinkedList<Event>();
		segmentUnitParts = new LinkedList<>();
		// targetVersions = new ArrayList<TargetVersion>();
		targetVersions = new HashMap<String, TargetVersion>();
		this.documentSegmentNum = 1;
		int segmentUnitPartIndex = 0;

		XLIFFReader reader = new XLIFFReader();
		reader.open(xliffFile);
		while (reader.hasNext()) {
			Event event = reader.next();
			this.events.add(event);

			if (event.isStartXliff()) {
				StartXliffData xliffElement = event.getStartXliffData();
				this.sourceLang = xliffElement.getSourceLanguage();
				// optional unless document contains target elements underneath
				// <segment> or <ignorable>
				if (xliffElement.getTargetLanguage() != null) {
					this.targetLang = xliffElement.getTargetLanguage();
				}
				enrichmentConverter = new EnrichmentConverterXLIFF20(sourceLang, targetLang);

			} else if (event.isStartFile()) {
				StartFileData fileData = event.getStartFileData();
				this.originalFileName = fileData.getOriginal();
			} else if (event.isUnit()) {
				Unit unit = event.getUnit();
				List<Note> unitOcelotNotes = findOcelotNotes(unit);
				for (Part unitPart : unit) {
					if (unitPart.isSegment()) {
						List<Enrichment> sourceEnrichments = enrichmentConverter.retrieveEnrichments(unit,
								unitPart.getSource(), sourceLang, unitPart.getId());
						List<Enrichment> targetEnrichments = enrichmentConverter.retrieveEnrichments(unit,
								unitPart.getTarget(), targetLang, unitPart.getId());
						net.sf.okapi.lib.xliff2.core.Segment okapiSegment = (net.sf.okapi.lib.xliff2.core.Segment) unitPart;
						OcelotSegment ocelotSegment = convertPartToSegment(okapiSegment, segmentUnitPartIndex++,
								sourceEnrichments, targetEnrichments, unit.getId(), unitPart.getId(true),
								unit.getTranslate());
						if (ocelotSegment.getTarget() != null) {
							setTargetRevisions(unit, okapiSegment, ocelotSegment);
						}
						manageNotes(unitOcelotNotes, ocelotSegment, okapiSegment, unit.getSegmentCount() > 1);
						segments.add(ocelotSegment);
						this.segmentUnitParts.add(okapiSegment);
					}
				}
			}

		}
		reader.close();
		return segments;
	}

	private void manageNotes(List<Note> notes, OcelotSegment ocelotSegment, Segment okapiSegment, boolean checkId) {

		Note noteForThisSegment = findNoteForSegment(notes, okapiSegment, checkId);
		if (noteForThisSegment != null) {
			Notes segmentNotes = new Notes();
			com.vistatec.ocelot.segment.model.okapi.Note note = new com.vistatec.ocelot.segment.model.okapi.Note();
			note.setContent(noteForThisSegment.getText());
			note.setId(noteForThisSegment.getId());
			segmentNotes.add(note);
			ocelotSegment.setNotes(segmentNotes);
			notes.remove(noteForThisSegment);
		}

	}

	public Note findNoteForSegment(List<Note> notes, Segment okapiSegment, boolean checkId) {

		Note noteForThisSegment = null;
		if (notes != null && !notes.isEmpty()) {
			if (checkId) {
				Iterator<Tag> sourceTagsIt = okapiSegment.getSource().getOwnTags().iterator();
				Tag currTag = null;
				String commenTagRef = null;
				while (sourceTagsIt.hasNext() && commenTagRef == null) {
					currTag = sourceTagsIt.next();
					if (currTag.isMarker()) {
						if (((MTag) currTag).getType().equals("comment")) {
							commenTagRef = ((MTag) currTag).getRef();
						}
					}
				}
				if (commenTagRef != null) {
					for (Note currNote : notes) {
						if (commenTagRef.equals("#n=" + currNote.getId())) {
							noteForThisSegment = currNote;
							break;
						}
					}
				}
			} else {
				noteForThisSegment = notes.get(0);
			}
		}
		return noteForThisSegment;
	}

	public List<Note> findOcelotNotes(Unit unit) {

		List<Note> ocelotNotes = new ArrayList<>();
		if (unit.getNoteCount() > 0) {
			for (Note note : unit.getNotes()) {
				if (note.getId() != null
						&& note.getId().contains(com.vistatec.ocelot.segment.model.okapi.Note.OCELOT_ID_PREFIX)) {
					ocelotNotes.add(note);
				}
			}
		}
		return ocelotNotes;

	}

	/**
	 * Sets the revisions of the target for this segment if a
	 * {@link ChangeTrack} object exists for the segment.
	 * 
	 * @param unit
	 *            the xliff unit
	 * @param okapiSegment
	 *            the okapi segment
	 * @param ocelotSegment
	 *            the Ocelot segment.
	 */
	private void setTargetRevisions(Unit unit, net.sf.okapi.lib.xliff2.core.Segment okapiSegment,
			OcelotSegment ocelotSegment) {

		if (unit.hasChangeTrack()) {
			List<OcelotRevision> ocelotRevisions = new ArrayList<OcelotRevision>();
			Revisions targetRevisions = null;
			Iterator<Revisions> revsIt = unit.getChangeTrack().iterator();
			Revisions revs = null;
			while (revsIt.hasNext()) {
				revs = revsIt.next();
				if (isRevisionsForTarget(revs, ocelotSegment.getSegmentId())) {
					targetRevisions = revs;
					break;
				}
			}
			if (targetRevisions != null) {
				for (Revision rev : targetRevisions) {
					Iterator<Item> itemsIt = rev.iterator();
					Item currItem = null;
					while (itemsIt.hasNext()) {
						currItem = itemsIt.next();
						if (currItem.getProperty().equals(Item.PROPERTY_CONTENT_VALUE)) {
							ocelotRevisions.add(new OcelotRevision(rev, currItem));
						}
					}
				}
			} else if (!ocelotSegment.getTarget().getDisplayText().isEmpty()) {
				targetRevisions = createRevisionsForTarget(okapiSegment.getTarget(), ocelotSegment.getSegmentId());
				unit.getChangeTrack().add(targetRevisions);
				ocelotRevisions.add(new OcelotRevision(targetRevisions.get(0), targetRevisions.get(0).get(0)));
			}
			if (!ocelotRevisions.isEmpty()) {
				Collections.sort(ocelotRevisions, new OcelotRevisionComparator());
				if (ocelotRevisions.size() > 1) {
					List<SegmentAtom> atoms = new ArrayList<SegmentAtom>();
					TextAtom origTrgtAtom = new TextAtom(ocelotRevisions.get(ocelotRevisions.size() - 1).getText());
					atoms.add(origTrgtAtom);
					FragmentVariant origTargetVar = new FragmentVariant(atoms, true);
					ocelotSegment.setOriginalTarget(origTargetVar);
				}

				int nextVersion = 1;
				for (OcelotRevision rev : ocelotRevisions) {
					if (rev.getVersion().startsWith(TargetVersion.VERSION_PREFIX)) {
						int revNum = Integer
								.parseInt(rev.getVersion().substring(TargetVersion.VERSION_PREFIX.length()));
						if (revNum >= nextVersion) {
							nextVersion = revNum + 1;
						}
					}
				}
				// check currentVersion and target
				OcelotRevision currRev = null;
				for (OcelotRevision ocelotRev : ocelotRevisions) {
					if (ocelotRev.getVersion().equals(targetRevisions.getCurrentVersion())) {
						currRev = ocelotRev;
						break;
					}
				}
				if (!currRev.getText().equals(okapiSegment.getTarget().getPlainText())) {
					Item currTargetItem = new Item(Item.PROPERTY_CONTENT_VALUE);
					currTargetItem.setText(okapiSegment.getTarget().getPlainText());
					Revision revision = new Revision();
					revision.setVersion(TargetVersion.VERSION_PREFIX + nextVersion++);
					revision.setDatetime(dateFormatter.format(new Date()));
					revision.add(currTargetItem);
					targetRevisions.add(revision);
					targetRevisions.setCurrentVersion(revision.getVersion());
				}
				targetVersions.put(ocelotSegment.getSegmentId(),
						new TargetVersion(TargetVersion.VERSION_PREFIX + nextVersion));
			} else {
				targetVersions.put(ocelotSegment.getSegmentId(), new TargetVersion(TargetVersion.VERSION_PREFIX + "1"));
			}

		} else if (!ocelotSegment.getTarget().getDisplayText().isEmpty()) {
			ChangeTrack changeTrack = new ChangeTrack();
			unit.setChangeTrack(changeTrack);
			changeTrack.add(createRevisionsForTarget(okapiSegment.getTarget(), ocelotSegment.getSegmentId()));
			targetVersions.put(ocelotSegment.getSegmentId(), new TargetVersion(TargetVersion.VERSION_PREFIX + "2"));
		} else {
			targetVersions.put(ocelotSegment.getSegmentId(), new TargetVersion(TargetVersion.VERSION_PREFIX + "1"));
		}
	}

	private boolean isRevisionsForTarget(Revisions revs, String ref) {

		return revs.getAppliesTo().equals(Const.ELEM_TARGET)
				&& ((ref != null && ref.equals(revs.getRef())) || (ref == null && revs.getRef() == null));
	}

	private Revisions createRevisionsForTarget(Fragment target, String ref) {

		Revisions revisions = new Revisions();
		revisions.setAppliesTo(Const.ELEM_TARGET);
		revisions.setCurrentVersion(TargetVersion.VERSION_PREFIX + "1");
		revisions.setRef(ref);
		Revision revision = new Revision();
		revision.setDatetime(dateFormatter.format(new Date()));
		revision.setVersion(TargetVersion.VERSION_PREFIX + "1");
		revisions.add(revision);
		Item item = new Item(Item.PROPERTY_CONTENT_VALUE);
		item.setText(getFragmentPlainText(target));
		revision.add(item);
		return revisions;
	}

	/**
	 * Converts Okapi XLIFF 2.0 Unit Parts to the Ocelot Segment format.
	 * 
	 * @param unitPart
	 *            &lt;segment> or &lt;ignorable> element. See {@link Part} for
	 *            more details.
	 * @param segmentUnitPartIndex
	 *            - Index of the associated original Okapi XLIFF 2.0 Event from
	 *            which the Segment was derived.
	 * @return Segment - Ocelot Segment
	 * @throws MalformedURLException
	 */
	private OcelotSegment convertPartToSegment(net.sf.okapi.lib.xliff2.core.Segment unitPart, int segmentUnitPartIndex,
			List<Enrichment> sourceEnrichments, List<Enrichment> targetEnrichments, String unitId, String segId,
			boolean translatable) throws MalformedURLException {
		segmentEventMapping.put(this.documentSegmentNum, this.events.size() - 1);
		// TODO: load original target from file
		OkapiSegment seg = new OkapiSegment.Builder().segmentNumber(documentSegmentNum++)
				.eventNumber(segmentUnitPartIndex).source(new FragmentVariant(unitPart, false))
				.target(new FragmentVariant(unitPart, true)).tuId(unitId).translatable(translatable).segId(segId)
				.build();
		seg.addAllLQI(parseLqiData(unitPart));
		seg.addAllProvenance(parseProvData(unitPart));
		if (sourceEnrichments != null && !sourceEnrichments.isEmpty() && seg.getSource() != null
				&& seg.getSource() instanceof BaseSegmentVariant) {
			((BaseSegmentVariant) seg.getSource()).setEnrichments(new HashSet<Enrichment>(sourceEnrichments));
			// ((BaseSegmentVariant)seg.getSource()).setEnriched(true);
		}
		if (targetEnrichments != null && !targetEnrichments.isEmpty() && seg.getTarget() != null
				&& seg.getTarget() instanceof BaseSegmentVariant) {
			((BaseSegmentVariant) seg.getTarget()).setEnrichments(new HashSet<Enrichment>(targetEnrichments));
			// ((BaseSegmentVariant)seg.getTarget()).setEnriched(true);
		}
		enrichmentConverter.convertEnrichments2ITSMetadata(seg);
		return seg;
	}

	private List<LanguageQualityIssue> parseLqiData(Part unitPart) throws MalformedURLException {
		List<LanguageQualityIssue> ocelotLqiList = new ArrayList<LanguageQualityIssue>();

		List<Tag> sourceTags = unitPart.getSource().getOwnTags();
		ocelotLqiList.addAll(convertOkapiToOcelotLqiData(sourceTags));

		if (unitPart.getTarget() != null) {
			List<Tag> targetTags = unitPart.getTarget().getOwnTags();
			ocelotLqiList.addAll(convertOkapiToOcelotLqiData(targetTags));
		}

		return ocelotLqiList;
	}

	private List<LanguageQualityIssue> convertOkapiToOcelotLqiData(List<Tag> okapiXliff2Tags)
			throws MalformedURLException {
		List<LanguageQualityIssue> ocelotLqiList = new ArrayList<LanguageQualityIssue>();

		for (Tag tag : okapiXliff2Tags) {
			// ITS XLIFF 2.0 LQI Mapping must be done using the <mrk> element
			if (tag.isMarker()) {
				MTag mtag = (MTag) tag;
				// Same Tag object is generated twice for paired elements; only
				// take the opening LQI
				if (mtag.hasITSItem()
						&& (mtag.getTagType() == TagType.OPENING || mtag.getTagType() == TagType.STANDALONE)) {
					IITSItem itsLqiItem = mtag.getITSItems().get(LocQualityIssue.class);
					if (itsLqiItem != null) {
						if (itsLqiItem.isGroup()) {
							LocQualityIssues lqiGroup = (LocQualityIssues) itsLqiItem;
							for (LocQualityIssue lqi : lqiGroup.getList()) {
								ocelotLqiList.add(convertOkapiToOcelotLqi(lqi));
							}
						} else {
							LocQualityIssue lqi = (LocQualityIssue) itsLqiItem;
							ocelotLqiList.add(convertOkapiToOcelotLqi(lqi));
						}
					}
				}
			}
		}

		return ocelotLqiList;
	}

	/**
	 * Convert from Okapi parsed version of an LQI
	 * 
	 * @param lqi
	 *            - Okapi representation of an ITS Language Quality Issue
	 * @return - Ocelot representation of an ITS Language Quality Issue
	 * @throws MalformedURLException
	 */
	private LanguageQualityIssue convertOkapiToOcelotLqi(LocQualityIssue lqi) throws MalformedURLException {
		LanguageQualityIssue ocelotLQI = new LanguageQualityIssue();
		ocelotLQI.setType(lqi.getType());
		ocelotLQI.setComment(lqi.getComment());
		ocelotLQI.setSeverity(lqi.getSeverity() != null ? lqi.getSeverity() : 0);

		URL profileRef = lqi.getProfileRef() != null ? new URL(lqi.getProfileRef()) : null;
		ocelotLQI.setProfileReference(profileRef);

		ocelotLQI.setEnabled(lqi.isEnabled());
		return ocelotLQI;
	}

	private List<Provenance> parseProvData(Part unitPart) {
		List<Provenance> ocelotProvList = new ArrayList<Provenance>();

		List<Tag> sourceTags = unitPart.getSource().getOwnTags();
		Fragment target = unitPart.getTarget();
		List<Tag> targetTags = target != null ? target.getOwnTags() : new ArrayList<Tag>();
		ocelotProvList.addAll(convertOkapiToOcelotProvData(sourceTags));
		ocelotProvList.addAll(convertOkapiToOcelotProvData(targetTags));

		return ocelotProvList;
	}

	private List<Provenance> convertOkapiToOcelotProvData(List<Tag> okapiXliff2Tags) {
		List<Provenance> ocelotProvList = new ArrayList<Provenance>();

		for (Tag tag : okapiXliff2Tags) {
			// ITS XLIFF 2.0 Provenance Mapping must be done using the <mrk>
			// element
			if (tag.isMarker()) {
				MTag mtag = (MTag) tag;
				if (mtag.hasITSItem()
						&& (mtag.getTagType() == TagType.OPENING || mtag.getTagType() == TagType.STANDALONE)) {
					IITSItem itsProvItem = mtag.getITSItems().get(net.sf.okapi.lib.xliff2.its.Provenance.class);
					if (itsProvItem != null) {
						if (itsProvItem.isGroup()) {
							Provenances provMetadata = (Provenances) itsProvItem;
							for (net.sf.okapi.lib.xliff2.its.Provenance p : provMetadata.getList()) {
								ocelotProvList.add(new OkapiProvenance(p));
							}
						} else {
							ocelotProvList
									.add(new OkapiProvenance((net.sf.okapi.lib.xliff2.its.Provenance) itsProvItem));
						}
					}
				}
			}
		}

		return ocelotProvList;
	}

	@Override
	public String getSourceLang() {
		return this.sourceLang;
	}

	@Override
	public String getTargetLang() {
		return this.targetLang;
	}

	@Override
	public String getOriginalFileName() {
		return originalFileName;
	}

	public void updateTargetVersions() {

		for (TargetVersion tVersion : targetVersions.values()) {
			tVersion.nextVersion();
		}
		// for(TargetVersion tVersion: targetVersions){
		// tVersion.nextVersion();
		// }
	}

	public SimpleDateFormat getRevisionDateFormatter() {
		return dateFormatter;
	}

	public String getFragmentPlainText(Fragment fragment) {

		StringBuilder plainText = new StringBuilder();
		String ctext = fragment.getCodedText();
		for (int i = 0; i < ctext.length(); i++) {
			char ch = ctext.charAt(i);
			switch (ch) {
			case Fragment.CODE_OPENING:
			case Fragment.CODE_CLOSING:
			case Fragment.CODE_STANDALONE:
			case Fragment.MARKER_OPENING:
			case Fragment.MARKER_CLOSING:
			case Fragment.PCONT_STANDALONE:
				i++;
				break;

			case '\r':
				plainText.append("&#13;"); // Literal
				break;
			case '<':
				plainText.append("&lt;");
				break;
			case '&':
				plainText.append("&amp;");
				break;
			case '\n':
			case '\t':
				plainText.append(ch);
				break;
			default:
				if ((ch > 0x001F) && (ch < 0xD800)) {
					// Valid char (most frequent)
					plainText.append(ch);
				} else if (Character.isHighSurrogate(ch)) {
					plainText.append(Character.toChars(ctext.codePointAt(i)));
					i++;
				} else if ((ch < 0x0020) || ((ch > 0xD7FF) && (ch < 0xE000)) || (ch == 0xFFFE) || (ch == 0xFFFF)) {
					// Invalid characters
					plainText.append(String.format("<cp hex=\"%04X\"/>", (int) ch));
				}
				break;
			}

		}

		return plainText.toString();
	}

}

// class RevisionComparator implements Comparator<Revision> {
//
// private SimpleDateFormat dateFormatter;
//
// public RevisionComparator(SimpleDateFormat dateFormatter) {
//
// this.dateFormatter = dateFormatter;
// }
//
// @Override
// public int compare(Revision o1, Revision o2) {
//
// int retValue = 0;
// if(o1.getDatetime() == null || o1.getDatetime().isEmpty()){
// retValue = 1;
// } else if(o2.getDatetime() == null || o2.getDatetime().isEmpty()){
// retValue = -1;
// } else {
// try {
// Long dateTime1 =
// Long.valueOf(dateFormatter.parse(o1.getDatetime()).getTime());
// Long dateTime2 =
// Long.valueOf(dateFormatter.parse(o2.getDatetime()).getTime());
// retValue = (-1) * dateTime1.compareTo(dateTime2);
// } catch (ParseException e) {
// retValue = 0;
// }
//// retValue = (-1) *
// }
// return retValue;
// }
//
// }

class OcelotRevisionComparator implements Comparator<OcelotRevision> {

	@Override
	public int compare(OcelotRevision o1, OcelotRevision o2) {

		return (-1) * Long.valueOf(o1.getDatetimeAsDate().getTime())
				.compareTo(Long.valueOf(o2.getDatetimeAsDate().getTime()));
	}

}

class TargetVersion {

	static final String VERSION_PREFIX = "Rev";
	private String version;
	private boolean updated;

	public TargetVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public void nextVersion() {

		if (updated) {
			int versionNum = Integer.parseInt(version.substring(VERSION_PREFIX.length()));
			version = VERSION_PREFIX + (versionNum + 1);
			updated = false;
		}
	}
}