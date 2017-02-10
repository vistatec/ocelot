package com.vistatec.ocelot.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.google.common.io.CharSink;

public class TestProvenanceConfig {

    @Test
    public void testEmptyProvenance() throws TransferException, URISyntaxException {
    	File file = new File(getClass().getResource("test_empty_provenance.json").toURI());
    	OcelotJsonConfigService cfgService = new OcelotJsonConfigService(new OcelotJsonConfigTransferService(file));
        assertTrue(cfgService.getUserProvenance().isEmpty());
    }

    @Test
    public void testLoadProvenance() throws TransferException, URISyntaxException {
        File file = new File(getClass().getResource("test_load_provenance.json").toURI());
        OcelotJsonConfigService cfgService = new OcelotJsonConfigService(new TestOcelotJsonConfigTransferService(file, new StringWriter()));

        UserProvenance prov = cfgService.getUserProvenance();
        assertNotNull(prov);
        assertFalse(prov.isEmpty());
        assertEquals("A", prov.getRevPerson());
        assertEquals("B", prov.getRevOrg());
        assertEquals("C", prov.getProvRef());
        assertEquals("D", prov.getLangCode());
    }

    @Test
    public void testSaveProvenance() throws IOException, TransferException, JAXBException, URISyntaxException {
        File file = new File(getClass().getResource("test_load_provenance.json").toURI());

        StringWriter writer = new StringWriter();
        OcelotJsonConfigService cfgService = new OcelotJsonConfigService(new TestOcelotJsonConfigTransferService(file, writer));

        UserProvenance prov = cfgService.getUserProvenance();
        prov.setProvRef("E");
        prov.setRevPerson("F");
        prov.setRevOrg("G");
        prov.setLangCode("H");
        cfgService.saveUserProvenance(prov);
        
        File savedFile = File.createTempFile("OcelotTest", "provTest");
        FileWriter fWriter = new FileWriter(savedFile);
        fWriter.write(writer.toString());
        fWriter.close();

        OcelotJsonConfigService testCfgService = new OcelotJsonConfigService(new TestOcelotJsonConfigTransferService(savedFile, writer));

        UserProvenance roundtrip = testCfgService.getUserProvenance();
        assertEquals("E", roundtrip.getProvRef());
        assertEquals("F", roundtrip.getRevPerson());
        assertEquals("G", roundtrip.getRevOrg());
        assertEquals("H", roundtrip.getLangCode());
    }

    public class TestCharSink extends CharSink {
        private final StringWriter writer;

        public TestCharSink(StringWriter writer) {
            this.writer = writer;
        }

        @Override
        public Writer openStream() throws IOException {
            return this.writer;
        }

    }
}
