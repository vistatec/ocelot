package com.vistatec.ocelot.segment.okapi;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.*;
import org.w3c.dom.Document;

import static org.junit.Assert.*;

import com.google.common.eventbus.EventBus;
import com.vistatec.ocelot.config.ConfigsForProvTesting;
import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RulesTestHelpers;
import com.vistatec.ocelot.segment.SegmentController;

public class TestOkapiXLIFF12Writer {
    @Test
    public void testWriteITSNamespace() throws Exception {
        // Methodology:
        // - Open no-its-namespace.xlf
        // - Add an LQI
        // - Write it out
        // Load the file and verify that it's valid XML

        OkapiXLIFF12Factory factory = new OkapiXLIFF12Factory();
        // Note that we need non-null provenance to be added, so we supply
        // a dummy revPerson value
        SegmentController controller = new SegmentController(factory, new EventBus(), new RuleConfiguration(),
                new ProvenanceConfig(new ConfigsForProvTesting("revPerson=q", null)));
        controller.parseXLIFFFile(new File(getClass().getResource("/no-its-namespace.xlf").toURI()));
        // Trigger an update
        controller.getSegment(0).addLQI(RulesTestHelpers.lqi("omission", 90));

        // Write it back out
        File temp = File.createTempFile("ocelot", ".xlf");
        System.out.println("Writing to " + temp);
        controller.save(temp);

        // See if we created valid XML
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder builder = docFactory.newDocumentBuilder();
        try {
            Document doc = builder.parse(temp);
            assertNotNull(doc);
        }
        catch (Exception e) {
            fail("Failed to parse roundtripped XLIFF: " + e.getMessage());
        }
    }
}
