package com.vistatec.ocelot.segment;

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;

import static org.junit.Assert.*;

import org.junit.*;

import com.vistatec.ocelot.SegmentViewColumn;
import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.rules.RuleConfiguration;

import static com.vistatec.ocelot.SegmentViewColumn.*;

import java.util.List;

import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIEditEvent;
import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.SegmentTargetUpdateEvent;
import com.vistatec.ocelot.services.SegmentService;

public class TestSegmentTableModel {
    private SegmentTableModel model;

    @Before
    public void setup() throws Exception {
        model = new SegmentTableModel(new TestSegmentService(), new RuleConfiguration());
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
            assertEquals(col.isVisibleByDefaut(), model.isColumnEnabled(col));
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
        public void setSegments(List<OcelotSegment> segments) {
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

    }
}
