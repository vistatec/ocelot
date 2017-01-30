package com.vistatec.ocelot.lqi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class TestLQIGrid {


    @SuppressWarnings("unchecked")
    @Test
    public void testRootIsEmpty() {
    	LQIGridConfigurations grid = new LQIGridConfigurations();
        assertTrue(grid.isEmpty());
        LQIGridConfiguration gridConf = new LQIGridConfiguration();
        grid.addConfiguration(gridConf);
        assertFalse(grid.isEmpty());
        grid.setConfigurations(Collections.EMPTY_LIST);
        assertTrue(grid.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConfIsEmpty(){
    	LQIGridConfiguration gridConf = new LQIGridConfiguration();
    	assertTrue(gridConf.isEmpty());
        gridConf.setSeverities(Collections.singletonList(new LQISeverity("Murky", 7.0)));
        assertFalse(gridConf.isEmpty());
        gridConf.setSeverities(Collections.EMPTY_LIST);
        assertTrue(gridConf.isEmpty());
        gridConf.setErrorCategories(Collections.singletonList(new LQIErrorCategory("Confusion")));
        assertFalse(gridConf.isEmpty());
    }
    
    @Test
    public void testSeverities() {
    	
    	LQIGridConfiguration gridConf = new LQIGridConfiguration();
    	gridConf.setSeverities(Lists.newArrayList(
                new LQISeverity("Murky", 7.0),
                new LQISeverity("Dodgy", 5.0),
                new LQISeverity("Funky", 2.5)
        ));
        assertEquals(2.5, gridConf.getSeverityScore("Funky"), 0.01);
        assertEquals(7.0, gridConf.getSeverityScore("Murky"), 0.01);
        assertEquals(5.0, gridConf.getSeverityScore("Dodgy"), 0.01);
        // We return a default score of 0 for unknown severities
        assertEquals(0.0, gridConf.getSeverityScore("Frizzle"), 0.01);
        assertNull(gridConf.getSeverity("Frizzle"));
        gridConf.setSeverityScore("Murky", 17.0);
        assertEquals(17.0, gridConf.getSeverityScore("Murky"), 0.01);
        assertEquals(5.0, gridConf.getSeverityScore("Dodgy"), 0.01);
        assertEquals(2.5, gridConf.getSeverityScore("Funky"), 0.01);
        LQISeverity sev = gridConf.getSeverity("Murky");
        assertNotNull(sev);
        assertEquals("Murky", sev.getName());
        assertEquals(17.0, sev.getScore(), 0.01);
        assertEquals(3, gridConf.getSeverities().size());
        // You can't set a score for a severity that doesn't already exist.
        gridConf.setSeverityScore("Frizzle", 1.0);
        assertEquals(0.0, gridConf.getSeverityScore("Frizzle"), 0.01);
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
    	LQIGridConfiguration gridConf = new LQIGridConfiguration();
        LQISeverity sev = new LQISeverity("Murky", 7.0);
        List<LQIErrorCategory> cats = Collections.singletonList(new LQIErrorCategory("Confusion"));
        gridConf.setSeverities(Collections.singletonList(sev));
        gridConf.setErrorCategories((cats));

        LQIGridConfiguration clone = (LQIGridConfiguration)gridConf.clone();
        assertEquals(sev, clone.getSeverity("Murky"));
        assertEquals(cats, clone.getErrorCategories());
    }
}