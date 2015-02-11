/*
 * Copyright (C) 2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.services;

import com.vistatec.ocelot.segment.Segment;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.ItsDocStatsAddedLqiEvent;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIEditEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentTargetUpdateEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.segment.SegmentVariant;

/**
 * Service for performing segment related operations.
 * FIXME: Should not contain the app segment data model.
 */
public class SegmentServiceImpl implements SegmentService {
    // TODO: remove segments (data) from service implementation
    private List<Segment> segments = new ArrayList<>(100);
    private final OcelotEventQueue eventQueue;

    public SegmentServiceImpl(OcelotEventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    public Segment getSegment(int row) {
        return segments.get(row);
    }

    @Override
    public int getNumSegments() {
        return segments.size();
    }

    @Override
    public void setSegments(List<Segment> segments) {
        this.segments = segments;
        recalculateDocStats();
    }

    @Subscribe
    @Override
    public void updateSegmentTarget(SegmentTargetUpdateEvent e) {
        Segment seg = e.getSegment();
        SegmentVariant updatedTarget = e.getUpdatedTarget();
        boolean updatedSeg = seg.updateTarget(updatedTarget);
        if (updatedSeg) {
            eventQueue.post(new SegmentEditEvent(seg));
        }
    }

    @Subscribe
    public void addLQI(LQIAdditionEvent e) {
        Segment seg = e.getSegment();
        LanguageQualityIssue lqi = e.getLQI();
        seg.addLQI(lqi);
        eventQueue.post(new ItsDocStatsAddedLqiEvent(lqi));
        eventQueue.post(new SegmentEditEvent(seg));
        eventQueue.post(new LQIModificationEvent(lqi, seg));
    }

    @Subscribe
    public void editLQI(LQIEditEvent e) {
        Segment seg = e.getSegment();
        LanguageQualityIssue lqi = e.getLQI();
        eventQueue.post(new ItsDocStatsAddedLqiEvent(lqi));
        eventQueue.post(new SegmentEditEvent(seg));
        eventQueue.post(new LQIModificationEvent(lqi, seg));
    }
    private void recalculateDocStats() {
        // TODO:
    }

    @Override
    public void clearAllSegments() {
        this.segments.clear();
        // TODO:
    }
}
