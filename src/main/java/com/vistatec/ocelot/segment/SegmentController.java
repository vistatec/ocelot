/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment;

import com.vistatec.ocelot.events.ITSDocStatsChangedEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.ProvenanceAddedEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;
import com.vistatec.ocelot.its.stats.ITSDocStats;

/**
 * Data model for a document.  This handles most manipulations of the 
 * segment model and generates most segment-related events.
 */
public class SegmentController {
    private XLIFFWriter segmentWriter;

    private OcelotEventQueue eventQueue;
    private ITSDocStats docStats;

    public SegmentController(OcelotEventQueue eventQueue,
            ITSDocStats docStats) {
        this.eventQueue = eventQueue;
        this.docStats = docStats;
    }

    /**
     * Return the current summary statistics for this document.
     * This view is *LIVE* and will always reflect the current
     * data for this controller.  {@link ITSDocStatsChangedEvent} will
     * be raised when these values change.
     *
     * @return current document statistics
     */
    public ITSDocStats getStats() {
        return docStats;
    }

    protected void notifyResetTarget(Segment seg) {
        eventQueue.post(new SegmentTargetResetEvent(seg));
    }

    private void recalculateDocStats() {
//        docStats.clear();
//        for (Segment seg : segments) {
//            for (LanguageQualityIssue lqi : seg.getLQI()) {
//                docStats.addLQIStats(lqi);
//            }
//            for (Provenance prov : seg.getProv()) {
//                docStats.addProvenanceStats(prov);
//            }
//        }
//        eventQueue.post(new ITSDocStatsChangedEvent());
    }

    void notifyRemovedLQI(LanguageQualityIssue lqi, Segment seg) {
        updateSegment(seg);
        recalculateDocStats();
        eventQueue.post(new LQIModificationEvent(lqi, seg));
    }

    public void clearAllSegments() {
//        segments.clear();
        docStats.clear();
        eventQueue.post(new ITSDocStatsChangedEvent());
    }

    // XXX Inconsistent naming - this is used when provenance is added
    // at runtime (for LQI, this is called notifyModifiedProv)
    void notifyAddedProv(Provenance prov) {
//        dirty = true;
        eventQueue.post(new ProvenanceAddedEvent(prov));
        docStats.addProvenanceStats(prov);
    }

    private void addSegment(Segment seg) {
        seg.setSegmentListener(this);
//        segments.add(seg);
    }

    public void updateSegment(Segment seg) {
        segmentWriter.updateSegment(seg, this);
//        dirty = true;
    }
}
