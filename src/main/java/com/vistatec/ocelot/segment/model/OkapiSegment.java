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
package com.vistatec.ocelot.segment.model;

import com.vistatec.ocelot.rules.StateQualifier;

/**
 * Ocelot segment that records the Okapi event number that the segment was
 * constructed from for the Okapi writer. Also allows for segment filtering on
 * the XLIFF 1.2 state qualifier and disabling on the phase name.
 */
public class OkapiSegment extends BaseSegment {
    public final int eventNum;

    public final String phaseName;
    private final StateQualifier stateQualifier;

    public OkapiSegment(int segNum, int eventNum, SegmentVariant source,
            SegmentVariant target, SegmentVariant originalTarget,
            StateQualifier stateQualifier, String phaseName) {
        super(segNum, source, target, originalTarget);

        this.stateQualifier = stateQualifier;
        this.phaseName = phaseName;

        this.eventNum = eventNum;
    }

    @Override
    public boolean isEditable() {
        return !"Rebuttal".equalsIgnoreCase(this.phaseName) &&
                !"Translator approval".equalsIgnoreCase(this.phaseName);
    }

    @Override
    public StateQualifier getStateQualifier() {
        return stateQualifier;
    }

    public static class Builder {
        private int segmentNumber, eventNum;
        private SegmentVariant source, target, originalTarget;
        private StateQualifier stateQualifier;
        private String phaseName;

        public Builder segmentNumber(int segNum) {
            this.segmentNumber = segNum;
            return this;
        }

        public Builder eventNumber(int eventNum) {
            this.eventNum = eventNum;
            return this;
        }

        public Builder source(SegmentVariant source) {
            this.source = source;
            return this;
        }

        public Builder target(SegmentVariant target) {
            this.target = target;
            return this;
        }

        public Builder originalTarget(SegmentVariant originalTarget) {
            this.originalTarget = originalTarget;
            return this;
        }

        public Builder stateQualifier(StateQualifier stateQual) {
            this.stateQualifier = stateQual;
            return this;
        }

        public Builder phaseName(String phaseName) {
            this.phaseName = phaseName;
            return this;
        }

        public OkapiSegment build() {
            return new OkapiSegment(segmentNumber, eventNum,
                    source, target, originalTarget, stateQualifier, phaseName);
        }
    }
}
