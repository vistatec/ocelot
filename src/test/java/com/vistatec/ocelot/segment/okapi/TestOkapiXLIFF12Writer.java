package com.vistatec.ocelot.segment.okapi;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.*;
import org.w3c.dom.Document;

import static org.junit.Assert.*;

import com.google.common.eventbus.EventBus;
import com.vistatec.ocelot.config.ConfigsForProvTesting;
import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.stats.ITSDocStats;
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
        checkValidXML(roundtripXliffAndAddLQI("/no-its-namespace.xlf"));
    }

    /**
     * The actual unittest for OC-21.  This modifies a segment, saves the file,
     * re-opens it and modifies it again, then verifies that the XML is valid.
     * (In OC-21, the ITS namespace is written out multiple times, rendering the
     * file invalid.)
     */
    @Test
    public void testWriteITSNamespaceMultipleTimes() throws Exception {
        File temp = roundtripXliffAndAddLQI("/no-its-namespace.xlf");
        File detectVersion = roundtripXliffAndAddLQI("/no-its-namespace.xlf");

        SegmentController controller = new SegmentController(
                new OkapiXLIFFFactory(), new EventBusWrapper(new EventBus()), new ITSDocStats(),
                new ProvenanceConfig(new ConfigsForProvTesting("revPerson=q", null)));
        controller.parseXLIFFFile(temp, detectVersion);
        temp.delete();

        // Remove that LQI we just added
        LanguageQualityIssue lqi = controller.getSegment(0).getLQI().get(0);
        controller.getSegment(0).removeLQI(lqi);

        // Write it back out
        checkValidXML(saveXliffToTemp(controller));
    }

    @Test
    public void testDontWriteRedundantITSNamespaceInXLIFFElement() throws Exception {
        checkValidXML(roundtripXliffAndAddLQI("/test.xlf"));
    }

    private File roundtripXliffAndAddLQI(String resourceName) throws Exception {
        // Note that we need non-null provenance to be added, so we supply
        // a dummy revPerson value
        SegmentController controller = new SegmentController(
                new OkapiXLIFFFactory(), new EventBusWrapper(new EventBus()), new ITSDocStats(),
                new ProvenanceConfig(new ConfigsForProvTesting("revPerson=q", null)));
        controller.parseXLIFFFile(new File(getClass().getResource(resourceName).toURI()),
                new File(getClass().getResource(resourceName).toURI()));
        // Trigger an update
        controller.getSegment(0).addLQI(RulesTestHelpers.lqi("omission", 90));

        return saveXliffToTemp(controller);
    }

    private File saveXliffToTemp(SegmentController controller) throws IOException {
        File temp = File.createTempFile("ocelot", ".xlf");
        controller.save(temp);
        return temp;
    }

    private void checkValidXML(File f) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder builder = docFactory.newDocumentBuilder();
        try {
            Document doc = builder.parse(f);
            assertNotNull(doc);
        }
        catch (Exception e) {
            fail("Failed to parse roundtripped XLIFF: " + e.getMessage());
        }
        finally {
            f.delete();
        }
    }
}
