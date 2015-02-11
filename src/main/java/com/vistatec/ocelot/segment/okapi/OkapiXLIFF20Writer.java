/*
 * Copyright (C) 2014, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.okapi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.Segment;
import com.vistatec.ocelot.segment.XLIFFWriter;
import java.util.List;
import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.MTag;
import net.sf.okapi.lib.xliff2.core.Part;
import net.sf.okapi.lib.xliff2.core.Tag;
import net.sf.okapi.lib.xliff2.core.TagType;
import net.sf.okapi.lib.xliff2.its.IITSItem;

import net.sf.okapi.lib.xliff2.its.ITSItems;
import net.sf.okapi.lib.xliff2.its.ITSWriter;
import net.sf.okapi.lib.xliff2.its.LocQualityIssue;
import net.sf.okapi.lib.xliff2.its.LocQualityIssues;
import net.sf.okapi.lib.xliff2.its.Provenances;
import net.sf.okapi.lib.xliff2.reader.Event;

/**
 * Write out XLIFF 2.0 files.
 */
public class OkapiXLIFF20Writer implements XLIFFWriter {
    private final Logger LOG = LoggerFactory.getLogger(OkapiXLIFF20Writer.class);
    private final OkapiXLIFF20Parser parser;
    private final ProvenanceConfig provConfig;

    public OkapiXLIFF20Writer(OkapiXLIFF20Parser parser, ProvenanceConfig provConfig) {
        this.parser = parser;
        this.provConfig = provConfig;
    }

    @Override
    public void updateSegment(Segment seg) {
        net.sf.okapi.lib.xliff2.core.Segment unitPart = this.parser.getSegmentUnitPart(seg.getSourceEventNumber());
        if (unitPart == null) {
            LOG.error("Failed to find Okapi Unit Part associated with segment #"+seg.getSegmentNumber());

        } else if (unitPart.isSegment()) {
            //TODO: set ori target
            if (seg.hasOriginalTarget()) {
                FragmentVariant targetFrag = (FragmentVariant) seg.getTarget();
                Fragment updatedOkapiFragment = targetFrag.getUpdatedOkapiFragment(unitPart.getTarget());
                unitPart.setTarget(updatedOkapiFragment);
            }

            updateITSLQIAnnotations(unitPart, seg);

            if (!haveAddedOcelotProvAnnotation(unitPart, seg)) {
                updateITSProvAnnotations(unitPart, seg);
            }

            FragmentVariant source = (FragmentVariant) seg.getSource();
            source.updateSegmentAtoms(unitPart);

            FragmentVariant target = (FragmentVariant) seg.getTarget();
            target.updateSegmentAtoms(unitPart);

        } else {
            LOG.error("Unit part associated with Segment was not an Okapi Segment!");
            LOG.error("Failed to update Unit Part for segment #"+seg.getSegmentNumber());
        }
    }

    /**
     * Records the Ocelot ITS Provenance record to the Okapi segment
     * representation used when saving the file using the Okapi XLIFF 2.0 writer.
     * @param unitPart - Okapi representation of the segment to annotate
     * @param seg - Ocelot segment
     */
    private void updateITSProvAnnotations(Part unitPart, Segment seg) {
        Provenances okapiOcelotProv = getOkapiOcelotProvenance(seg);
        if (okapiOcelotProv != null) {
            ITSWriter.annotate(unitPart.getTarget(), 0, -1, okapiOcelotProv);

            seg.addProvenance(new OkapiProvenance(okapiOcelotProv.getList().get(0)));
            seg.setAddedRWProvenance(true);
        }
    }

    /**
     * Constructs the Okapi representation of the Ocelot ITS Provenance record
     * if the reviewer's profile is set.
     * @param seg - Ocelot segment
     * @return - Okapi ITS Provenance Object or null if profile {@link UserProvenance} is not set
     */
    private Provenances getOkapiOcelotProvenance(Segment seg) {
        UserProvenance userProvenance = provConfig.getUserProvenance();
        if (userProvenance.isEmpty()) {
            return null;
        }

        String ocelotProvId = "OcelotProv" + seg.getSegmentNumber();
        Provenances okapiProvGroup = new Provenances(ocelotProvId);

        net.sf.okapi.lib.xliff2.its.Provenance okapiProv
                = new net.sf.okapi.lib.xliff2.its.Provenance();
        okapiProv.setRevPerson(userProvenance.getRevPerson());
        okapiProv.setRevOrg(userProvenance.getRevOrg());
        okapiProv.setRevTool("http://open.vistatec.com/ocelot");
        okapiProv.setProvRef(userProvenance.getProvRef());
        okapiProvGroup.getList().add(okapiProv);

        return okapiProvGroup;
    }

    private boolean haveAddedOcelotProvAnnotation(Part unitPart, Segment seg) {
        boolean haveAddedUserProv = seg.addedRWProvenance();

        List<Tag> targetTags = unitPart.getTarget().getOwnTags();
        for (Tag tag : targetTags) {
            if (tag.isMarker()) {
                MTag mtag = (MTag) tag;

                if (mtag.hasITSItem()) {
                    Provenances provMetadata = (Provenances) mtag.getITSItems()
                            .get(net.sf.okapi.lib.xliff2.its.Provenance.class);
                    if (provMetadata != null
                            && provMetadata.getGroupId().matches("OcelotProv[0-9]*")) {
                        haveAddedUserProv = true;
                    }
                }
            }
        }
        return haveAddedUserProv;
    }

    /**
     * Update LQI annotations on the segment. TODO: separate from non-LQI updates
     * @param unitPart - Okapi representation of the segment
     * @param seg - Ocelot segment
     */
    public void updateITSLQIAnnotations(Part unitPart, Segment seg) {
        removeExistingLqiAnnotationsFromSegment(unitPart);

        if (seg.containsLQI()) {
            String ocelotLqiId = "OcelotLQI" + seg.getSegmentNumber();
            LocQualityIssues newOkapiLqiGroup = convertOcelotToOkapiLqi(
                    seg.getLQI(), ocelotLqiId);
            ITSWriter.annotate(unitPart.getTarget(), 0, -1, newOkapiLqiGroup);
        }
    }

    private void removeExistingLqiAnnotationsFromSegment(Part unitPart) {
        List<Tag> sourceTags = unitPart.getSource().getOwnTags();
        List<Tag> targetTags = unitPart.getTarget().getOwnTags();

        removeExistingLqiAnnotations(unitPart, false, sourceTags);
        removeExistingLqiAnnotations(unitPart, true, targetTags);
    }

    private void removeExistingLqiAnnotations(Part unitPart, boolean isTarget, List<Tag> tags) {
        for (Tag tag : tags) {
            if (tag.isMarker()) {
                MTag mtag = (MTag) tag;
                if (mtag.hasITSItem()) {
                    ITSItems items = mtag.getITSItems();
                    IITSItem itsLqiItem = items.get(LocQualityIssue.class);
                    if (itsLqiItem != null) {
                        // Don't delete the LQI issues for opening tags so we
                        // can find the corresponding closing tag and delete it as well
                        if (mtag.getTagType() == TagType.CLOSING ||
                                mtag.getTagType() == TagType.STANDALONE) {
                            items.remove(itsLqiItem);
                        }
                        // TODO: Assumes MTag is only used for ITS metadata
                        if (items.size() <= 1) {
                            Fragment frag = isTarget ?
                                    unitPart.getTarget() : unitPart.getSource();
                            frag.remove(mtag);
                        }
                    }
                }
            }
        }
    }

    private LocQualityIssues convertOcelotToOkapiLqi(List<LanguageQualityIssue> ocelotLqi, String ocelotLqiId) {
        LocQualityIssues newLqiGroup = new LocQualityIssues(ocelotLqiId);
        for (LanguageQualityIssue lqi : ocelotLqi) {
            LocQualityIssue newLqi = new LocQualityIssue();
            newLqi.setType(lqi.getType());
            newLqi.setComment(lqi.getComment());
            newLqi.setSeverity(lqi.getSeverity());

            if (lqi.getProfileReference() != null) {
                newLqi.setProfileRef(lqi.getProfileReference().toString());
            }

            newLqi.setEnabled(lqi.isEnabled());
            newLqiGroup.getList().add(newLqi);
        }
        return newLqiGroup;
    }

    @Override
    public void save(File file) throws IOException, UnsupportedEncodingException {
        net.sf.okapi.lib.xliff2.writer.XLIFFWriter writer = new net.sf.okapi.lib.xliff2.writer.XLIFFWriter();
        StringWriter tmp = new StringWriter();
        writer.create(tmp, parser.getSourceLang());
        writer.setLineBreak("\n"); //FIXME: OS linebreak detection in XLIFF filter doesn't seem to work (Mac) so we need to set it.
        writer.setWithOriginalData(true);
        for (Event event : parser.getEvents()) {
            writer.writeEvent(event);
        }
        writer.close();
        tmp.close();
        Writer outputFile = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()), "UTF-8"));
        outputFile.write(tmp.toString());
        outputFile.flush();
        outputFile.close();
    }

}
