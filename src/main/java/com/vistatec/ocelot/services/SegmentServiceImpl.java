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

/**
 * Service for performing segment related operations.
 * FIXME: Should not contain the app segment data model.
 */
public class SegmentServiceImpl implements SegmentService {
    // TODO: remove segments (data) from service implementation
    private List<Segment> segments = new ArrayList<>(100);

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

    private void recalculateDocStats() {
        // TODO:
    }

    @Override
    public void clearAllSegments() {
        this.segments.clear();
        // TODO:
    }
}
