/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.view;

import static com.vistatec.ocelot.SegmentViewColumn.Flag1;
import static com.vistatec.ocelot.SegmentViewColumn.Flag2;
import static com.vistatec.ocelot.SegmentViewColumn.Flag3;
import static com.vistatec.ocelot.SegmentViewColumn.Flag4;
import static com.vistatec.ocelot.SegmentViewColumn.Flag5;
import static com.vistatec.ocelot.SegmentViewColumn.Original;
import static com.vistatec.ocelot.SegmentViewColumn.SegNum;
import static com.vistatec.ocelot.SegmentViewColumn.Source;
import static com.vistatec.ocelot.SegmentViewColumn.Target;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.vistatec.ocelot.SegmentViewColumn;
import com.vistatec.ocelot.config.OcelotJsonConfigService;
import com.vistatec.ocelot.config.OcelotJsonConfigTransferService;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIEditEvent;
import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.SegmentNoteUpdatedEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.SegmentTargetUpdateEvent;
import com.vistatec.ocelot.its.model.ITSMetadata;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class TestSegmentTableModel {
    private SegmentTableModel model;
    private OcelotJsonConfigService confService;

    @Before
    public void setup() throws Exception {
    	File confFile = new File(getClass().getResource("col_config.json").toURI());
    	confService = new OcelotJsonConfigService(new OcelotJsonConfigTransferService(confFile));
        model = new SegmentTableModel(new TestSegmentService(), new RuleConfiguration(), confService);
    }

    @Test
    public void testGetColumn() {
        assertEquals(SegNum, model.getColumn(0));
        assertEquals(Source, model.getColumn(1));
        assertEquals(Target, model.getColumn(2));
        assertEquals(Original, model.getColumn(3));
        assertEquals(Flag1, model.getColumn(4));
        assertEquals(Flag2, model.getColumn(5));
        assertEquals(Flag3, model.getColumn(6));
        assertEquals(Flag4, model.getColumn(7));
        assertEquals(Flag5, model.getColumn(8));
    }

    @Test
    public void testGetSpecialColumnIndexes() {
        assertEquals(0, model.getSegmentNumColumnIndex());
        assertEquals(1, model.getSegmentSourceColumnIndex());
        assertEquals(2, model.getSegmentTargetColumnIndex());
        assertEquals(3, model.getSegmentTargetOriginalColumnIndex());
    }

    @Test
    public void testGetColumnClass() {
        assertEquals(Integer.class, model.getColumnClass(0));
        assertEquals(SegmentVariant.class, model.getColumnClass(1));
        assertEquals(SegmentVariant.class, model.getColumnClass(2));
        assertEquals(SegmentVariant.class, model.getColumnClass(3));
        assertEquals(ITSMetadata.class, model.getColumnClass(4));
        assertEquals(ITSMetadata.class, model.getColumnClass(5));
        assertEquals(ITSMetadata.class, model.getColumnClass(6));
        assertEquals(ITSMetadata.class, model.getColumnClass(7));
        assertEquals(ITSMetadata.class, model.getColumnClass(8));
    }

    @Test
    public void testIsColumnEnabled() {
        for (SegmentViewColumn col : SegmentViewColumn.values()) {
        	assertEquals(confService.isColumnEnabled(col), model.isColumnEnabled(col));
        }
    }

    private class TestSegmentService implements SegmentService {

        @Override
        public OcelotSegment getSegment(int row) {
            return null;
        }

        @Override
        public int getNumSegments() {
            return 0;
        }

        @Override
        public void setSegments(XLIFFDocument xliff) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void updateSegmentTarget(SegmentTargetUpdateEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addLQI(LQIAdditionEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void editLQI(LQIEditEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void clearAllSegments() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeLQI(LQIRemoveEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void resetSegmentTarget(SegmentTargetResetEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void updateSegmentNote(SegmentNoteUpdatedEvent e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
}
