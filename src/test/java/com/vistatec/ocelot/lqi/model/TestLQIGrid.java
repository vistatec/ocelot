package com.vistatec.ocelot.lqi.model;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.vistatec.ocelot.config.xml.LQIGridConfig.LQICategory;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

public class TestLQIGrid {
    private LQIGrid grid = null;

    @Before
    public void setup() {
        grid = new LQIGrid();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsEmpty() {
        assertTrue(grid.isEmpty());
        grid.setSeverities(Collections.singletonList(new LQISeverity("Murky", 7.0)));
        assertFalse(grid.isEmpty());
        grid.setSeverities(Collections.EMPTY_LIST);
        assertTrue(grid.isEmpty());
        grid.setErrorCategories(Collections.singletonList(new LQIErrorCategory("Confusion")));
        assertFalse(grid.isEmpty());
    }

    @Test
    public void testSeverities() {
        grid.setSeverities(Lists.newArrayList(
                new LQISeverity("Murky", 7.0),
                new LQISeverity("Dodgy", 5.0),
                new LQISeverity("Funky", 2.5)
        ));
        assertEquals(2.5, grid.getSeverityScore("Funky"), 0.01);
        assertEquals(7.0, grid.getSeverityScore("Murky"), 0.01);
        assertEquals(5.0, grid.getSeverityScore("Dodgy"), 0.01);
        // We return a default score of 0 for unknown severities
        assertEquals(0.0, grid.getSeverityScore("Frizzle"), 0.01);
        assertNull(grid.getSeverity("Frizzle"));
        grid.setSeverityScore("Murky", 17.0);
        assertEquals(17.0, grid.getSeverityScore("Murky"), 0.01);
        assertEquals(5.0, grid.getSeverityScore("Dodgy"), 0.01);
        assertEquals(2.5, grid.getSeverityScore("Funky"), 0.01);
        LQISeverity sev = grid.getSeverity("Murky");
        assertNotNull(sev);
        assertEquals("Murky", sev.getName());
        assertEquals(17.0, sev.getScore(), 0.01);
        assertEquals(3, grid.getSeverities().size());
        // You can't set a score for a severity that doesn't already exist.
        grid.setSeverityScore("Frizzle", 1.0);
        assertEquals(0.0, grid.getSeverityScore("Frizzle"), 0.01);
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        LQISeverity sev = new LQISeverity("Murky", 7.0);
        List<LQIErrorCategory> cats = Collections.singletonList(new LQIErrorCategory("Confusion"));
        grid.setSeverities(Collections.singletonList(sev));
        grid.setErrorCategories((cats));

        LQIGrid clone = (LQIGrid)grid.clone();
        assertEquals(sev, clone.getSeverity("Murky"));
        assertEquals(cats, clone.getErrorCategories());
    }
}