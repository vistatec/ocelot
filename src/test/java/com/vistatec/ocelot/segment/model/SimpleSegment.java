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
 * Test base segment functionality.
 */
public class SimpleSegment extends BaseSegment {
    private final StateQualifier stateQualifier;

    public SimpleSegment(int segNum, SegmentVariant source,
            SegmentVariant target, SegmentVariant originalTarget,
            StateQualifier stateQualifier) {
        super(segNum, source, target, originalTarget, true);
        this.stateQualifier = stateQualifier;
    }

    @Override
    public boolean isEditable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public StateQualifier getStateQualifier() {
        return stateQualifier;
    }

    public static class Builder {
        private int segmentNumber;
        private final SimpleSegmentVariant.Builder source = new SimpleSegmentVariant.Builder();
        private final SimpleSegmentVariant.Builder target = new SimpleSegmentVariant.Builder();
        private SimpleSegmentVariant.Builder originalTarget;
        private StateQualifier stateQualifier;
        
        public Builder segmentNumber(int segNum) {
            this.segmentNumber = segNum;
            return this;
        }

        public SimpleSegmentVariant.Builder source() {
            return this.source;
        }

        public Builder source(String sourcePlainText) {
            this.source.text(sourcePlainText);
            return this;
        }

        public SimpleSegmentVariant.Builder target() {
            return this.target;
        }

        public Builder target(String targetPlainText) {
            this.target.text(targetPlainText);
            return this;
        }

        public SimpleSegmentVariant.Builder originalTarget() {
            this.originalTarget = new SimpleSegmentVariant.Builder();
            return this.originalTarget;
        }

        public Builder originalTarget(String originalTargetPlainText) {
            this.originalTarget = new SimpleSegmentVariant.Builder();
            this.originalTarget.text(originalTargetPlainText);
            return this;
        }
        

        public Builder stateQualifier(StateQualifier stateQualifier) {
            this.stateQualifier = stateQualifier;
            return this;
        }

        public SimpleSegment build() {
            return new SimpleSegment(segmentNumber, source.build(), target.build(),
                    originalTarget == null ? null : originalTarget.build(), stateQualifier);
        }
    }

	@Override
	public String getTuId() {
		return null;
	}

	@Override
    public String getSegmentId() {
	    return null;
    }

}
