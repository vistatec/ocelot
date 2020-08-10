package com.vistatec.ocelot.xliff.okapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.eventbus.EventBus;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.vistatec.ocelot.config.OcelotJsonConfigService;
import com.vistatec.ocelot.config.OcelotJsonConfigTransferService;
import com.vistatec.ocelot.config.TestProvenanceConfig;
import com.vistatec.ocelot.events.SegmentNoteEditEvent;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;
import com.vistatec.ocelot.segment.model.okapi.Note;
import com.vistatec.ocelot.services.OkapiXliffService;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.services.SegmentServiceImpl;
import com.vistatec.ocelot.services.XliffService;
import com.vistatec.ocelot.xliff.XLIFFDocument;
import com.vistatec.ocelot.xliff.freme.EnrichmentAnnotationsConstants;
import com.vistatec.ocelot.xliff.freme.XliffFremeAnnotationWriter;

public class TestOkapiXliff20Writer extends XMLTestCase {

    @Ignore("Fails due to addition of LRE and PDF marks")
	public void ignoreWriteEnrichments() throws Exception {

		File testFile = new File(TestProvenanceConfig.class.getResource("test_empty_provenance.json").toURI());
   	 OcelotJsonConfigService cfgService = new OcelotJsonConfigService(new OcelotJsonConfigTransferService(testFile));
		OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
		XliffService xliffService = new OkapiXliffService(cfgService,
		        eventQueue);
		eventQueue.registerListener(xliffService);

		File xliffFile = new File(getClass().getResource(
		        "xliff2.0.not-enriched-small.xlf").toURI());
		XLIFFDocument xliff = xliffService.parse(xliffFile);
		SegmentService segmentService = new SegmentServiceImpl(eventQueue);
		eventQueue.registerListener(segmentService);

		segmentService.setSegments(xliff);
		OcelotSegment segment = null;
		for (int i = 0; i < segmentService.getNumSegments(); i++) {
			segment = segmentService.getSegment(i);
			((BaseSegmentVariant) segment.getSource())
			        .addEnrichmentList(EnrichmentBuilder
			                .getWritingXliff2_0TestEnrichments(segment
			                        .getSegmentId()));
		}
		File savedFile = saveXliffToTemp(xliffService, xliff);
		System.out.println(savedFile.getAbsolutePath());
		XliffFremeAnnotationWriter annotationWriter = new XliffFremeAnnotationWriter(
		        xliff.getSrcLocale().toString(), xliff.getTgtLocale()
		                .toString());
		annotationWriter.saveAnnotations(savedFile, segmentService);
		savedFile = checkJson(savedFile, segmentService);

		checkAgainstGoldXML(savedFile, "xliff2.0.enriched-small-nojson.xlf");

	}
	
	@Test
	public void testWriteNotesMultipleSegments() throws Exception {
		
		File testFile = new File(TestProvenanceConfig.class.getResource("test_empty_provenance.json").toURI());
		 OcelotJsonConfigService cfgService = new OcelotJsonConfigService(new OcelotJsonConfigTransferService(testFile));
			OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
			OkapiXliffService xliffService = new OkapiXliffService(cfgService,
			        eventQueue);
			eventQueue.registerListener(xliffService);

			File xliffFile = new File(getClass().getResource(
			        "test_file_multiple_seg_2.0.xlf").toURI());
			XLIFFDocument xliff = xliffService.parse(xliffFile);
			SegmentService segmentService = new SegmentServiceImpl(eventQueue);
			segmentService.setSegments(xliff);
			for(OcelotSegment seg: xliff.getSegments()){
				if(seg.getSegmentId().equals("s1")){
					seg.getNotes().add(new Note("ocelot-1", "This is a note for the segment s1"));
					xliffService.updateNotes(new SegmentNoteEditEvent(xliff, seg));
				} else if (seg.getSegmentId().equals("s3")){
					seg.getNotes().add(new Note("ocelot-3", "This is a note for the segment s3"));
					xliffService.updateNotes(new SegmentNoteEditEvent(xliff, seg));
				}
			}
			File savedFile = saveXliffToTemp(xliffService, xliff);
			checkAgainstGoldXML(savedFile,
	                "file_with_notes_multiple_segm_2.0.xlf");
			
	}
	
	@Test
	public void testWriteNoteSingleSegment() throws Exception {
		
		File testFile = new File(TestProvenanceConfig.class.getResource("test_empty_provenance.json").toURI());
		 OcelotJsonConfigService cfgService = new OcelotJsonConfigService(new OcelotJsonConfigTransferService(testFile));
			OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
			OkapiXliffService xliffService = new OkapiXliffService(cfgService,
			        eventQueue);
			eventQueue.registerListener(xliffService);

			File xliffFile = new File(getClass().getResource(
			        "file_single_segm_2.0.xlf").toURI());
			XLIFFDocument xliff = xliffService.parse(xliffFile);
			SegmentService segmentService = new SegmentServiceImpl(eventQueue);
			segmentService.setSegments(xliff);
			for(OcelotSegment seg: xliff.getSegments()){
				if(seg.getSegmentId().equals("s1")){
					seg.getNotes().add(new Note("ocelot-1", "This is a note for the segment s1"));
					xliffService.updateNotes(new SegmentNoteEditEvent(xliff, seg));
				} else if (seg.getSegmentId().equals("s4")){
					seg.getNotes().add(new Note("ocelot-3", "This is a note for the segment s4"));
					xliffService.updateNotes(new SegmentNoteEditEvent(xliff, seg));
				}
			}
			File savedFile = saveXliffToTemp(xliffService, xliff);
			checkAgainstGoldXML(savedFile,
	                "file_single_segm_with_notes2.0.xlf");
			
	}
	
	@Test
	public void testUpdateNoteSingleSegment() throws Exception {
		
		File testFile = new File(TestProvenanceConfig.class.getResource("test_empty_provenance.json").toURI());
		 OcelotJsonConfigService cfgService = new OcelotJsonConfigService(new OcelotJsonConfigTransferService(testFile));
			OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
			OkapiXliffService xliffService = new OkapiXliffService(cfgService,
			        eventQueue);
			eventQueue.registerListener(xliffService);

			File xliffFile = new File(getClass().getResource(
			        "file_single_segm_with_notes2.0.xlf").toURI());
			XLIFFDocument xliff = xliffService.parse(xliffFile);
			SegmentService segmentService = new SegmentServiceImpl(eventQueue);
			segmentService.setSegments(xliff);
			for(OcelotSegment seg: xliff.getSegments()){
				if(seg.getSegmentId().equals("s1")){
					Note ocelotNote = seg.getNotes().getOcelotNote(); 
					ocelotNote.setContent(ocelotNote.getContent() + " edited");
					xliffService.updateNotes(new SegmentNoteEditEvent(xliff, seg));
				}
			}
			File savedFile = saveXliffToTemp(xliffService, xliff);
			checkAgainstGoldXML(savedFile,
	                "file_single_segm_with_notes_updated2.0.xlf");
			
	}
	
	@Test
	public void testDeleteNoteSingleSegment() throws Exception {
		
		File testFile = new File(TestProvenanceConfig.class.getResource("test_empty_provenance.json").toURI());
		 OcelotJsonConfigService cfgService = new OcelotJsonConfigService(new OcelotJsonConfigTransferService(testFile));
			OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
			OkapiXliffService xliffService = new OkapiXliffService(cfgService,
			        eventQueue);
			eventQueue.registerListener(xliffService);

			File xliffFile = new File(getClass().getResource(
			        "file_single_segm_with_notes2.0.xlf").toURI());
			XLIFFDocument xliff = xliffService.parse(xliffFile);
			SegmentService segmentService = new SegmentServiceImpl(eventQueue);
			segmentService.setSegments(xliff);
			for(OcelotSegment seg: xliff.getSegments()){
				if(seg.getSegmentId().equals("s1")){
					Note ocelotNote = seg.getNotes().getOcelotNote(); 
					seg.getNotes().remove(ocelotNote);
					xliffService.updateNotes(new SegmentNoteEditEvent(xliff, seg));
				}
			}
			File savedFile = saveXliffToTemp(xliffService, xliff);
			checkAgainstGoldXML(savedFile,
	                "file_single_segm_with_notes_deleted2.0.xlf");
			
	}
	
	@Test
	public void testUpdateNotesMultipleSegments() throws Exception {
		
		File testFile = new File(TestProvenanceConfig.class.getResource("test_empty_provenance.json").toURI());
		 OcelotJsonConfigService cfgService = new OcelotJsonConfigService(new OcelotJsonConfigTransferService(testFile));
			OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
			OkapiXliffService xliffService = new OkapiXliffService(cfgService,
			        eventQueue);
			eventQueue.registerListener(xliffService);

			File xliffFile = new File(getClass().getResource(
			        "file_with_notes_multiple_segm_2.0.xlf").toURI());
			XLIFFDocument xliff = xliffService.parse(xliffFile);
			SegmentService segmentService = new SegmentServiceImpl(eventQueue);
			segmentService.setSegments(xliff);
			for(OcelotSegment seg: xliff.getSegments()){
				if(seg.getSegmentId().equals("s1")){
					Note note = seg.getNotes().getOcelotNote();
					note.setContent(note.getContent() + " edited");
					xliffService.updateNotes(new SegmentNoteEditEvent(xliff, seg));
					break;
				}
			}
			File savedFile = saveXliffToTemp(xliffService, xliff);
			checkAgainstGoldXML(savedFile,
	                "file_with_notes_multiple_segm_updated_2.0.xlf");
			
	}
	
	@Test
	public void testDeleteNotesMultipleSegments() throws Exception {
		
		File testFile = new File(TestProvenanceConfig.class.getResource("test_empty_provenance.json").toURI());
		 OcelotJsonConfigService cfgService = new OcelotJsonConfigService(new OcelotJsonConfigTransferService(testFile));
			OcelotEventQueue eventQueue = new EventBusWrapper(new EventBus());
			OkapiXliffService xliffService = new OkapiXliffService(cfgService,
			        eventQueue);
			eventQueue.registerListener(xliffService);

			File xliffFile = new File(getClass().getResource(
			        "file_with_notes_multiple_segm_2.0.xlf").toURI());
			XLIFFDocument xliff = xliffService.parse(xliffFile);
			SegmentService segmentService = new SegmentServiceImpl(eventQueue);
			segmentService.setSegments(xliff);
			for(OcelotSegment seg: xliff.getSegments()){
				if(seg.getSegmentId().equals("s1")){
					seg.getNotes().remove(seg.getNotes().getOcelotNote());
					xliffService.updateNotes(new SegmentNoteEditEvent(xliff, seg));
				} else if(seg.getSegmentId().equals("s3")){
					seg.getNotes().remove(seg.getNotes().getOcelotNote());
					xliffService.updateNotes(new SegmentNoteEditEvent(xliff, seg));
				}
			}
			File savedFile = saveXliffToTemp(xliffService, xliff);
			checkAgainstGoldXML(savedFile,
	                "test_file_multiple_seg_2.0.xlf");
			
	}
	
	

	private File checkJson(File savedFile, SegmentService segmentService)
	        throws Exception {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(savedFile);
		doc.getDocumentElement().normalize();
		
		NodeList unitElems = doc.getElementsByTagName("unit");
		for(int i = 0; i<unitElems.getLength(); i++){
			Model expected = null;
			Model actual = null;
			NodeList jsonElems = ((Element)unitElems.item(i)).getElementsByTagName(EnrichmentAnnotationsConstants.JSON_TAG_NAME);
			Element jsonNode = null;
			List<Element> nodesToRemove = new ArrayList<Element>();
			for(int j = 0; j<jsonElems.getLength(); j++){
				jsonNode = (Element) jsonElems.item(j);
				OcelotSegment segment = findSegmentById(jsonNode.getAttribute(EnrichmentAnnotationsConstants.JSON_TAG_SEG_ATTR), segmentService);
				if(segment != null){
					expected = getEnrichModelFromSegment(segment);
					actual = getModelFromJsonElement(jsonNode);
					assertModels(expected, actual);
				}
//				unitElems.item(i).removeChild(jsonNode);
				nodesToRemove.add(jsonNode);
			}
			for(Element node: nodesToRemove){
				unitElems.item(i).removeChild(node);
			}
			NodeList changeTrackingNodes = ((Element)unitElems.item(i)).getElementsByTagName("ctr:changeTrack");
			for(int ct = 0; ct<changeTrackingNodes.getLength(); ct++){
				unitElems.item(i).removeChild(changeTrackingNodes.item(ct).getNextSibling());
				unitElems.item(i).removeChild(changeTrackingNodes.item(ct));
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
			// System.out.println("File saved!");
		}
		return new File(savedFile.getAbsolutePath());
	}

	private void assertModels(Model expected, Model actual) {
	    
		if(expected != null && actual != null){
			Assert.assertTrue(expected.isIsomorphicWith(actual));
		} else {
			assertNull(expected);
			assertNull(actual);
		}
    }

	private Model getModelFromJsonElement(Element jsonNode) {
		
		Model model = ModelFactory.createDefaultModel();
		if(jsonNode != null){
			String jsonString = jsonNode.getTextContent();
			model.read(new StringReader(jsonString), null, EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
		}
	    return model;
    }

	private Model getEnrichModelFromSegment(OcelotSegment segment) {
		
		Model model = ModelFactory.createDefaultModel();
		BaseSegmentVariant source = (BaseSegmentVariant) segment.getSource();
		for(Enrichment enrich: source.getEnirchments()){
			if(enrich.getType().equals(Enrichment.TERMINOLOGY_TYPE)){
				TerminologyEnrichment termEnrich = (TerminologyEnrichment) enrich;
				model.add(termEnrich.getTermTriples().toArray(new Statement[termEnrich.getTermTriples().size()]));
			} else if (enrich.getType().equals(Enrichment.LINK_TYPE)){
				model.add(((LinkEnrichment)enrich).getPropertiesModel());
			}
		}
	    return model;
    }

	private OcelotSegment findSegmentById(String segId, SegmentService segmentService) {
		
		OcelotSegment segment = null;
		OcelotSegment currSeg = null;
		int i = 0;
		while(i<segmentService.getNumSegments() && segment == null){
			currSeg = segmentService.getSegment(i++);
			if(segId.equals(currSeg.getSegmentId())){
				segment = currSeg;
			}
		}
	    return segment;
    }


	private File saveXliffToTemp(XliffService service, XLIFFDocument xliff)
	        throws IOException {
		File temp = File.createTempFile("ocelot", ".xlf");
		service.save(xliff, temp);
		return temp;
	}

	private void checkAgainstGoldXML(File output, String goldResourceName)
	        throws Exception {
		try (Reader r = new InputStreamReader(new FileInputStream(output),
		        StandardCharsets.UTF_8);
		        Reader goldReader = new InputStreamReader(getClass()
		                .getResourceAsStream(goldResourceName),
		                StandardCharsets.UTF_8)) {
			assertXMLEqual(goldReader, r);
		}
		output.delete();
	}
}
