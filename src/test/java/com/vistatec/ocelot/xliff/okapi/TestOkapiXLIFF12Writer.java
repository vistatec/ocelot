/*
 * Copyright (C) 2015, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.xliff.okapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.vistatec.ocelot.config.OcelotConfigService;
import com.vistatec.ocelot.config.TestProvenanceConfig;
import com.vistatec.ocelot.config.OcelotXmlConfigTransferService;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.rules.RulesTestHelpers;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.services.OkapiXliffService;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.services.SegmentServiceImpl;
import com.vistatec.ocelot.services.XliffService;

public class TestOkapiXLIFF12Writer extends XMLTestCase {
    private final OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());

//    @Test
//    public void testWriteITSNamespace() throws Exception {
//        checkAgainstGoldXML(roundtripXliffAndAddLQI("/no-its-namespace.xlf"), "/gold/no-its-namespace.xlf");
//    }

//    /**
//     * The actual unittest for OC-21.  This modifies a segment, saves the file,
//     * re-opens it and modifies it again, then verifies that the XML is correct.
//     * (In OC-21, the ITS namespace is written out multiple times, rendering the
//     * file invalid.)
//     */
//    @Test
//    public void testWriteITSNamespaceMultipleTimes() throws Exception {
//        File temp = roundtripXliffAndAddLQI("/no-its-namespace.xlf");
//        File detectVersion = roundtripXliffAndAddLQI("/no-its-namespace.xlf");
//
//        ByteSource testLoad = Resources.asByteSource(
//                TestProvenanceConfig.class.getResource("test_load_provenance.xml"));
//        OcelotConfigService cfgService = new OcelotConfigService(new XmlConfigTransferService(testLoad, null));
//        XliffService xliffService = new OkapiXliffService(cfgService, eventQueue);
//        eventQueue.registerListener(xliffService);
//
//        List<OcelotSegment> segments = xliffService.parse(temp, detectVersion);
//        SegmentService segmentService = new SegmentServiceImpl(eventQueue);
//        eventQueue.registerListener(segmentService);
//
//        segmentService.setSegments(segments);
//        temp.delete();
//
//        // Remove that LQI we just added
//        LanguageQualityIssue lqi = segments.get(0).getLQI().get(0);
//        eventQueue.post(new LQIRemoveEvent(lqi, segments.get(0)));
//
//        // Write it back out
//        checkAgainstGoldXML(saveXliffToTemp(xliffService), "/gold/multiple-its-namespace.xlf");
//    }

    @Test
    public void testDontWriteRedundantITSNamespaceInXLIFFElement() throws Exception {
        checkAgainstGoldXML(roundtripXliffAndAddLQI("/test.xlf"), "/gold/redundant-its-namespace.xlf");
    }

    @Test
    public void testDontWriteEmptyProvenance() throws Exception {
        checkAgainstGoldXML(roundtripXliffAndAddLQI("/test.xlf", "test_empty_provenance.xml"),
                            "/gold/lqi_no_provenance.xlf");
    }

    private void checkAgainstGoldXML(File output, String goldResourceName) throws Exception {
        try (Reader r = new InputStreamReader(new FileInputStream(output), StandardCharsets.UTF_8);
                Reader goldReader = new InputStreamReader(getClass().getResourceAsStream(goldResourceName),
                                        StandardCharsets.UTF_8)) {
           assertXMLEqual(goldReader, r);
       }
       output.delete();
    }
    
    private File roundtripXliffAndAddLQI(String resourceName) throws Exception {
        return roundtripXliffAndAddLQI(resourceName, "test_load_provenance.xml");
    }

    private File roundtripXliffAndAddLQI(String resourceName, String provenanceConfig) throws Exception {
        // Note that we need non-null provenance to be added, so we supply
        // a dummy revPerson value
        ByteSource testLoad = Resources.asByteSource(
                TestProvenanceConfig.class.getResource(provenanceConfig));
        OcelotConfigService cfgService = new OcelotConfigService(new OcelotXmlConfigTransferService(testLoad, null));
        XliffService xliffService = new OkapiXliffService(cfgService, eventQueue);
        eventQueue.registerListener(xliffService);

        URI uri = getClass().getResource(resourceName).toURI();
        List<OcelotSegment> segments = xliffService.parse(new File(uri), new File(uri));
        SegmentService segmentService = new SegmentServiceImpl(eventQueue);
        eventQueue.registerListener(segmentService);

        segmentService.setSegments(segments);
        // Trigger an update
        segmentService.addLQI(new LQIAdditionEvent(RulesTestHelpers.lqi("omission", 90),
                segments.get(0)));

        return saveXliffToTemp(xliffService);
    }

    private File saveXliffToTemp(XliffService service) throws IOException {
        File temp = File.createTempFile("ocelot", ".xlf");
        service.save(temp);
        return temp;
    }
}
