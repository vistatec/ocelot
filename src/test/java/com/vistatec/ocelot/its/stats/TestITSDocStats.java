package com.vistatec.ocelot.its.stats;

import java.util.Arrays;
import java.util.Collections;

import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;

import org.junit.*;

import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.Provenance;

import static org.junit.Assert.*;

public class TestITSDocStats {

    @Test
    public void testAddLQI() {
        ITSDocStats docStats = new ITSDocStats();
        docStats.addLQIStats(getLQI("omission", 50));
        assertEquals(Collections.singletonList(getLQIStats("omission", 50)), docStats.getStats());
        docStats.addLQIStats(getLQI("omission", 70));
        assertEquals(Collections.singletonList(getLQIStats("omission", 50, 70)), docStats.getStats());
        docStats.addLQIStats(getLQI("omission", 30));
        assertEquals(Collections.singletonList(getLQIStats("omission", 30, 70)), docStats.getStats());
        docStats.addLQIStats(getLQI("mistranslation", 80));
        assertEquals(Arrays.asList(getLQIStats("omission", 30, 70), getLQIStats("mistranslation", 80)), docStats.getStats());
    }

    @Test
    public void addProvenance() {
        ITSDocStats docStats = new ITSDocStats();
        // XXX Bit of a cheat here, I'm assuming the order that the
        // stats are added to the docStats object
        docStats.addProvenanceStats(new Provenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_PERSON, "testPerson",
                GenericAnnotationType.PROV_ORG, "testOrg",
                GenericAnnotationType.PROV_TOOL, "testTool")));
        // TODO the key needs to be a constant
        assertEquals(Arrays.asList(getProvStats("person", "testPerson", 1),
                                   getProvStats("org", "testOrg", 1),
                                   getProvStats("tool", "testTool", 1)),
                     docStats.getStats());
        docStats.addProvenanceStats(new Provenance(new GenericAnnotation(GenericAnnotationType.PROV,
                GenericAnnotationType.PROV_PERSON, "testPerson",
                GenericAnnotationType.PROV_ORG, "testOrg",
                GenericAnnotationType.PROV_TOOL, "testTool",
                GenericAnnotationType.PROV_REVPERSON, "testRevPerson",
                GenericAnnotationType.PROV_REVORG, "testRevOrg",
                GenericAnnotationType.PROV_REVTOOL, "testRevTool")));
        assertEquals(Arrays.asList(
                            getProvStats("person", "testPerson", 2),
                            getProvStats("org", "testOrg", 2),
                            getProvStats("tool", "testTool", 2),
                            getProvStats("revPerson", "testRevPerson", 1),
                            getProvStats("revOrg", "testRevOrg", 1),
                            getProvStats("revTool", "testRevTool", 1)),
                     docStats.getStats());
    }

    private LanguageQualityIssue getLQI(String type, int severity) {
        LanguageQualityIssue lqi = new LanguageQualityIssue();
        lqi.setType(type);
        lqi.setSeverity(severity);
        return lqi;
    }

    private LanguageQualityIssueStats getLQIStats(String type, double severity) {
        LanguageQualityIssueStats lqiStats = new LanguageQualityIssueStats();
        lqiStats.setType(type);
        lqiStats.setRange(severity);
        return lqiStats;
    }

    private LanguageQualityIssueStats getLQIStats(String type, double min, double max) {
        LanguageQualityIssueStats lqiStats = new LanguageQualityIssueStats();
        lqiStats.setType(type);
        lqiStats.setRange(min);
        lqiStats.setRange(max);
        return lqiStats;
    }

    private ProvenanceStats getProvStats(String type, String value, int count) {
        ProvenanceStats stats = new ProvenanceStats(type, value);
        stats.setCount(count);
        return stats;
    }
}
