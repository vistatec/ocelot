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
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.eventbus.EventBus;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RSIterator;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.vistatec.ocelot.config.OcelotConfigService;
import com.vistatec.ocelot.config.OcelotXmlConfigTransferService;
import com.vistatec.ocelot.config.TestProvenanceConfig;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIRemoveEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.rules.RulesTestHelpers;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;
import com.vistatec.ocelot.services.OkapiXliffService;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.services.SegmentServiceImpl;
import com.vistatec.ocelot.services.XliffService;
import com.vistatec.ocelot.xliff.XLIFFDocument;
import com.vistatec.ocelot.xliff.freme.EnrichmentAnnotationsConstants;
import com.vistatec.ocelot.xliff.freme.XliffFremeAnnotationWriter;

public class TestOkapiXLIFF12Writer extends XMLTestCase {
    private final OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());

    @Test
    public void testWriteITSNamespace() throws Exception {
        checkAgainstGoldXML(roundtripXliffAndAddLQI("/no-its-namespace.xlf"), "/gold/no-its-namespace.xlf");
    }

    /**
     * The actual unittest for OC-21.  This modifies a segment, saves the file,
     * re-opens it and modifies it again, then verifies that the XML is correct.
     * (In OC-21, the ITS namespace is written out multiple times, rendering the
     * file invalid.)
     */
    @Test
    public void testWriteITSNamespaceMultipleTimes() throws Exception {
        File temp = roundtripXliffAndAddLQI("/no-its-namespace.xlf");

        ByteSource testLoad = Resources.asByteSource(
                TestProvenanceConfig.class.getResource("test_load_provenance.xml"));
        OcelotConfigService cfgService = new OcelotConfigService(new OcelotXmlConfigTransferService(testLoad, null));
        XliffService xliffService = new OkapiXliffService(cfgService, eventQueue);
        eventQueue.registerListener(xliffService);

        XLIFFDocument xliff = xliffService.parse(temp);
        SegmentService segmentService = new SegmentServiceImpl(eventQueue);
        eventQueue.registerListener(segmentService);

        segmentService.setSegments(xliff);
        temp.delete();

        // Remove that LQI we just added
        LanguageQualityIssue lqi = xliff.getSegments().get(0).getLQI().get(0);
        eventQueue.post(new LQIRemoveEvent(lqi, xliff.getSegments().get(0)));

        // Write it back out
        checkAgainstGoldXML(saveXliffToTemp(xliffService, xliff), "/gold/multiple-its-namespace.xlf");
    }

    @Test
    public void testDontWriteRedundantITSNamespaceInXLIFFElement() throws Exception {
        checkAgainstGoldXML(roundtripXliffAndAddLQI("/test.xlf"), "/gold/redundant-its-namespace.xlf");
    }

    @Test
    public void testDontWriteEmptyProvenance() throws Exception {
        checkAgainstGoldXML(roundtripXliffAndAddLQI("/test.xlf", "test_empty_provenance.xml"),
                            "/gold/lqi_no_provenance.xlf");
    }
    
    @Test
    public void testWriteEnrichments() throws Exception {
    	
    	 ByteSource testLoad = Resources.asByteSource(
                 TestProvenanceConfig.class.getResource("test_empty_provenance.xml"));
         OcelotConfigService cfgService = new OcelotConfigService(new OcelotXmlConfigTransferService(testLoad, null));
         XliffService xliffService = new OkapiXliffService(cfgService, eventQueue);
         eventQueue.registerListener(xliffService);

         File xliffFile = new File(getClass()
 		        .getResource("xliff1.2.not-enriched-small.xlf").toURI());
         XLIFFDocument xliff = xliffService.parse(xliffFile);
         SegmentService segmentService = new SegmentServiceImpl(eventQueue);
         eventQueue.registerListener(segmentService);

         segmentService.setSegments(xliff);
		OcelotSegment segment = null;
		for (int i = 0; i < segmentService.getNumSegments(); i++) {
			segment = segmentService.getSegment(i);
			((BaseSegmentVariant) segment.getSource())
			        .addEnrichmentList(EnrichmentBuilder.getWritingXliff1_2TestEnrichments(segment.getSegmentNumber()));
			((OkapiXliffService)xliffService).updateSegment(new SegmentEditEvent(xliff, segment));
		}
		File savedFile = saveXliffToTemp(xliffService, xliff); 
		System.out.println(savedFile.getAbsolutePath());
		XliffFremeAnnotationWriter annotationWriter = new XliffFremeAnnotationWriter(
				xliff.getSrcLocale().toString(), xliff
		                .getTgtLocale().toString());
        annotationWriter.saveAnnotations(savedFile, segmentService);
        checkJson(savedFile, segmentService);
        
		checkAgainstGoldXML(savedFile,
                "xliff1.2.-enriched-small-nojson.xlf");
			
		
    }

    private File checkJson(File savedFile, SegmentService segmentService) throws Exception {
	    
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(savedFile);
		doc.getDocumentElement().normalize();
		NodeList transUnitEls = doc.getElementsByTagName("trans-unit");
		Model actual = null;
		Model expected = null;
		Element transUnitNode = null;
		for (int i = 0; i < transUnitEls.getLength(); i++) {
			transUnitNode = (Element) transUnitEls.item(i);
			OcelotSegment segment = getSegmentForUnit(
			        transUnitNode.getAttribute("id"), segmentService);
			if (segment != null) {
				NodeList jsonNodeEls = transUnitNode.getElementsByTagName(
				        EnrichmentAnnotationsConstants.JSON_TAG_NAME);
				if (jsonNodeEls != null && jsonNodeEls.getLength() > 0) {
					String json = jsonNodeEls.item(0).getTextContent();
					actual = ModelFactory.createDefaultModel();
					actual.read(new StringReader(json), null,
					        EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
					transUnitNode.removeChild(jsonNodeEls.item(0));
				}

				expected = ModelFactory.createDefaultModel();
				for (Enrichment enrich : ((BaseSegmentVariant) segment
				        .getSource()).getEnirchments()) {
					if (enrich.getType().equals(Enrichment.TERMINOLOGY_TYPE)) {
						expected.add(((TerminologyEnrichment) enrich)
						        .getTermTriples()
						        .toArray(
						                new Statement[((TerminologyEnrichment) enrich)
						                        .getTermTriples().size()]));
					} else if (enrich.getType().equals(Enrichment.LINK_TYPE)) {
						expected.add(((LinkEnrichment) enrich)
						        .getPropertiesModel());
					}
				}
			}
			if (actual != null && expected != null) {
				Assert.assertTrue(actual.isIsomorphicWith(expected));
			} else {
				Assert.assertNull(actual);
				Assert.assertNull(expected);
			}

		}		
		
		TransformerFactory transformerFactory = TransformerFactory
		        .newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		try (Writer writer = new FileWriterWithEncoding(savedFile,
		        StandardCharsets.UTF_8)) {
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
//			System.out.println("File saved!");
		}
		return new File(savedFile.getAbsolutePath());
    }
    
    private OcelotSegment getSegmentForUnit(String unitId, SegmentService segService){
    	
    	OcelotSegment segment = null;
    	OcelotSegment currSeg = null;
    	int i = 0;
    	while(i<segService.getNumSegments() && segment == null){
    		currSeg = segService.getSegment(i++);
    		if(currSeg.getTuId().equals(unitId)){
    			segment = currSeg;
    		}
    	}
    	return segment;
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
        XLIFFDocument xliff = xliffService.parse(new File(uri));
        SegmentService segmentService = new SegmentServiceImpl(eventQueue);
        eventQueue.registerListener(segmentService);

        segmentService.setSegments(xliff);
        // Trigger an update
        segmentService.addLQI(new LQIAdditionEvent(RulesTestHelpers.lqi("omission", 90),
                xliff.getSegments().get(0)));

        return saveXliffToTemp(xliffService, xliff);
    }

    private File saveXliffToTemp(XliffService service, XLIFFDocument xliff) throws IOException {
        File temp = File.createTempFile("ocelot", ".xlf");
        service.save(xliff, temp);
        return temp;
    }
}