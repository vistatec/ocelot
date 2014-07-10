package com.vistatec.ocelot.config;

import java.io.File;

import org.junit.*;

import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RulesParser;

import static org.junit.Assert.*;

public class TestDirectoryBasedConfigs {

    @Test
    public void testReaders() throws Exception {
        File dir = new File(getClass().getResource("/testconfig").toURI());
        DirectoryBasedConfigs configs = new DirectoryBasedConfigs(dir);
        ProvenanceConfig pc = new ProvenanceConfig(configs);
        assertEquals("A", pc.getUserProvenance().getRevPerson());
        assertEquals("B", pc.getUserProvenance().getRevOrg());
        assertEquals("C", pc.getUserProvenance().getProvRef());
        AppConfig ac = new AppConfig(configs);
        assertEquals(2, ac.config.plugins.size());
        RuleConfiguration rc = new RulesParser().loadConfig(configs.getRulesReader());
        assertNotNull(rc);
        assertEquals(1, rc.getRules().size());
    }
}
