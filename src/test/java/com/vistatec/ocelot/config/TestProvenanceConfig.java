package com.vistatec.ocelot.config;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.*;

import static org.junit.Assert.*;

import java.io.Writer;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBException;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.Resources;

public class TestProvenanceConfig {

    @Test
    public void testEmptyProvenance() throws ConfigTransferService.TransferException, JAXBException {
        OcelotConfigService cfgService = new OcelotConfigService(
                new XmlConfigTransferService(ByteSource.empty(), null));
        assertTrue(cfgService.getUserProvenance().isEmpty());
    }

    @Test
    public void testLoadProvenance() throws JAXBException, ConfigTransferService.TransferException {
        ByteSource testLoad = Resources.asByteSource(
                TestProvenanceConfig.class.getResource("test_load_provenance.xml"));
        OcelotConfigService cfgService = new OcelotConfigService(new XmlConfigTransferService(
                testLoad, null
        ));

        UserProvenance prov = cfgService.getUserProvenance();
        assertNotNull(prov);
        assertFalse(prov.isEmpty());
        assertEquals("A", prov.getRevPerson());
        assertEquals("B", prov.getRevOrg());
        assertEquals("C", prov.getProvRef());
    }

    @Test
    public void testSaveProvenance() throws IOException, ConfigTransferService.TransferException, JAXBException {
        ByteSource testLoad = Resources.asByteSource(
                TestProvenanceConfig.class.getResource("test_load_provenance.xml"));

        StringWriter writer = new StringWriter();
        OcelotConfigService cfgService = new OcelotConfigService(
                new XmlConfigTransferService(testLoad, new TestCharSink(writer)));

        UserProvenance prov = cfgService.getUserProvenance();
        prov.setProvRef("D");
        prov.setRevPerson("E");
        prov.setRevOrg("F");
        cfgService.saveUserProvenance(prov);

        ByteSource savedConfig = ByteSource.wrap(writer.toString().getBytes(Charset.forName("UTF-8")));
        OcelotConfigService testCfgService = new OcelotConfigService(new XmlConfigTransferService(savedConfig, null));

        UserProvenance roundtrip = testCfgService.getUserProvenance();
        assertEquals("D", roundtrip.getProvRef());
        assertEquals("E", roundtrip.getRevPerson());
        assertEquals("F", roundtrip.getRevOrg());
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
