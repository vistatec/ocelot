package com.vistatec.ocelot.rules;

import org.junit.Test;
import static org.junit.Assert.*;

import com.vistatec.ocelot.its.LanguageQualityIssue;

public class TestQuickAdd {

    @Test
    public void testAddLQIShouldCreateDistinctInstances() {
        QuickAdd qa = new QuickAdd("test", RulesTestHelpers.lqi("omission", 75));

        LanguageQualityIssue lqi1 = qa.createLQI();
        LanguageQualityIssue lqi2 = qa.createLQI();
        assertNull(lqi1.getComment());
        assertNull(lqi2.getComment());
        lqi1.setComment("test");
        assertEquals("test", lqi1.getComment());
        assertNull(lqi2.getComment());
    }
}
