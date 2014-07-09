package com.vistatec.ocelot.config;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.*;

import static org.junit.Assert.*;

public class TestProvenanceConfig {

    @Test
    public void testEmptyProvenance() {
        ProvenanceConfig config = new ProvenanceConfig(new ConfigsForProvTesting(new StringReader(""), null));
        assertTrue(config.isEmpty());
        assertTrue(config.getUserProvenance().isEmpty());
        
        config = new ProvenanceConfig(new ConfigsForProvTesting(null, null));
        assertTrue(config.isEmpty());
        assertTrue(config.getUserProvenance().isEmpty());
    }

    @Test
    public void testLoadProvenance() {
        ProvenanceConfig config = new ProvenanceConfig(new ConfigsForProvTesting(new StringReader(
                "externalReference=C\nrevOrganization=B\nrevPerson=A"), null));
        assertFalse(config.isEmpty());
        UserProvenance prov = config.getUserProvenance();
        assertNotNull(prov);
        assertFalse(prov.isEmpty());
        assertEquals("A", prov.getRevPerson());
        assertEquals("B", prov.getRevOrg());
        assertEquals("C", prov.getProvRef());
    }

    @Test
    public void testSaveProvenance() throws IOException {
        StringWriter sw = new StringWriter();
        ProvenanceConfig config = new ProvenanceConfig(new ConfigsForProvTesting(null, sw));
        UserProvenance prov = config.getUserProvenance();
        prov.setProvRef("D");
        prov.setRevPerson("E");
        prov.setRevOrg("F");
        config.save(prov);

        config = new ProvenanceConfig(new ConfigsForProvTesting(new StringReader(sw.toString()), null));
        assertFalse(config.isEmpty());
        UserProvenance roundtrip = config.getUserProvenance();
        assertEquals("D", roundtrip.getProvRef());
        assertEquals("E", roundtrip.getRevPerson());
        assertEquals("F", roundtrip.getRevOrg());
    }

    
    class ConfigsForProvTesting implements Configs {
        private Reader r;
        private Writer w;
        ConfigsForProvTesting(Reader r, Writer w) {
            this.r = r;
            this.w = w;
        }
        @Override public Reader getProvenanceReader() throws IOException {
            return r;
        }
        @Override public Reader getOcelotReader() throws IOException {
            throw new UnsupportedOperationException();
        }
        @Override public Reader getRulesReader() throws IOException {
            throw new UnsupportedOperationException();
        }
        @Override public Writer getProvenanceWriter() throws IOException {
            return w;
        }
        @Override public Writer getOcelotWriter() throws IOException {
            throw new UnsupportedOperationException();
        }
        @Override public Writer getRulesWriter() throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}
