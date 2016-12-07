package com.vistatec.ocelot.xliff.freme;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.okapi.lib.xliff2.core.CTag;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.xerces.dom.AttrImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.CodeAtom;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;
import com.vistatec.ocelot.segment.model.okapi.OkapiCodeAtom;
import com.vistatec.ocelot.segment.model.okapi.TaggedCodeAtom;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.xliff.freme.helper.DocumentTreeHelper;
import com.vistatec.ocelot.xliff.freme.helper.DocumentTreeHelper.NodeWrapper;
import com.vistatec.ocelot.xliff.freme.helper.FremeXliffHelper;
import com.vistatec.ocelot.xliff.freme.helper.FremeXliffHelperFactory;
import com.vistatec.ocelot.xliff.freme.helper.FremeXliffHelperFactory.UnsupportedVersionException;

public class XliffFremeAnnotationWriter {

	/** Version attribute in XLIFF files. */
	private static final String VERSION_ATTRIBUTE = "version";

	/** The logger for this class. */
	private final Logger logger = LoggerFactory
	        .getLogger(XliffFremeAnnotationWriter.class);

	/** The dom document retrieved by parsing the XLIFF file. */
	private Document document;

	/** Helper class for managing XLIFF files. */
	private FremeXliffHelper xliffHelper;

	/** The last id used for FREME mrk tag. */
	private int lastFremeMrkId;

	private String sourceLang;

	private String targetLang;

	public XliffFremeAnnotationWriter(final String sourceLang,
	        final String targetLang) {

		this.sourceLang = sourceLang;
		this.targetLang = targetLang;
	}

	/**
	 * For each enrichment in the Ocelot segments, a proper annotation is
	 * written into the XLIFF file.
	 * 
	 * @param xliffFile
	 *            the XLIFF file.
	 * @param segService
	 *            the segment service.
	 * @throws Exception
	 *             If an error occurs while parsing the file
	 */
	public void saveAnnotations(final File xliffFile, SegmentService segService)
	        throws Exception {

		logger.info("Saving enrichment annotations for file {}",
		        xliffFile.getName());
		try {
			document = parseFile(xliffFile);
			// creates the proper helper depending on the file version
			xliffHelper = FremeXliffHelperFactory
			        .createHelper(detectVersion(document));
			// initializes the last FREME marker id.
			findLastFremeMrkId();
			// find the list of unit nodes ("trans-unit" for version 1.2 and
			// "unit" for version 2.0)
			NodeList unitElements = document.getElementsByTagName(xliffHelper
			        .getUnitNodeName());
			if (unitElements != null) {
				for (int i = 0; i < unitElements.getLength(); i++) {
					writeAnnotationsForUnit((Element) unitElements.item(i),
					        segService);
				}
			}

			saveFile(xliffFile.getAbsolutePath());
		} catch (ParserConfigurationException | SAXException e) {
			logger.error("Error while parsing the file", e);
			throw e;
		} catch (IOException e) {
			logger.error("Error while reading or writing the file", e);
		} catch (TransformerException e) {
			logger.error("Error while saving the DOM document into the file", e);
		} catch (UnsupportedVersionException e) {
			logger.error("Error while creating the XLIFF helper", e);
		}

	}

	/**
	 * Saves the DOM document into the file.
	 * 
	 * @param filePath
	 *            the file path
	 * @throws IOException
	 *             the IO exception
	 * @throws TransformerException
	 *             the transformer exception
	 */
	private void saveFile(String filePath) throws IOException,
	        TransformerException {
		TransformerFactory transformerFactory = TransformerFactory
		        .newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		try (Writer writer = new FileWriterWithEncoding(new File(filePath),
		        StandardCharsets.UTF_8)) {
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			logger.debug("File saved!");
		}
	}

	/**
	 * Parses the XLIFF file and produces the DOM document.
	 * 
	 * @param xliffFile
	 *            the XLIFF file.
	 * @return the DOM document
	 * @throws ParserConfigurationException
	 *             the Parse configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             the IO exception
	 */
	private Document parseFile(File xliffFile)
	        throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xliffFile);
		doc.getDocumentElement().normalize();
		return doc;
	}

	/**
	 * Detects the XLIFF file version by inspecting the "version" attribute.
	 * 
	 * @param doc
	 *            the DOM document
	 * @return the file version formatted as a string
	 */
	private String detectVersion(Document doc) {

		return doc.getDocumentElement().getAttribute(VERSION_ATTRIBUTE);
	}

	/**
	 * Initializes the <code>lastFremeMrkId</code> field value. It finds the
	 * biggest FREME ID already used in the document.
	 */
	private void findLastFremeMrkId() {

		lastFremeMrkId = 0;
		NodeList markerNodes = document
		        .getElementsByTagName(EnrichmentAnnotationsConstants.MARKER_TAG_NAME);
		for (int i = 0; i < markerNodes.getLength(); i++) {
			Node currNode = markerNodes.item(i);
			Node idItem = currNode.getAttributes().getNamedItem(
			        EnrichmentAnnotationsConstants.MARKER_TAG_ID_ATTR);
			if (idItem != null) {
				String id = idItem.getNodeValue();
				if (id.startsWith(EnrichmentAnnotationsConstants.MARKER_FREME_ID_PREFIX)) {
					id = id.substring(id
					        .indexOf(EnrichmentAnnotationsConstants.MARKER_FREME_ID_PREFIX)
					        + EnrichmentAnnotationsConstants.MARKER_FREME_ID_PREFIX
					                .length());
					int idNum = Integer.parseInt(id);
					if (idNum > lastFremeMrkId) {
						lastFremeMrkId = idNum;
					}
				}
			}
		}
	}

	/**
	 * Writes the annotations for the unit node passed as parameter.
	 * 
	 * @param unitElement
	 *            the unit element node
	 * @param segService
	 *            the Segment Service
	 */
	private void writeAnnotationsForUnit(Element unitElement,
	        SegmentService segService) {

		// Detect the segment associated to this unit
		String unitId = xliffHelper.getUnitId(unitElement);
		List<OcelotSegment> segments = xliffHelper.getSegmentsForUnit(unitId, segService);
		for(OcelotSegment segment: segments){
			logger.debug("Writing annotations for unit {} and segment {}", unitId,
					segment.getSegmentNumber());
				if (segment.getSource() instanceof BaseSegmentVariant) {
					writeAnnotations(unitElement,
					        xliffHelper.getSourceElement(unitElement, segment.getSegmentId()),
					        (BaseSegmentVariant) segment.getSource(),
					        sourceLang, segment.getSegmentId());
					writeAnnotations(unitElement,
					        xliffHelper.getTargetElement(unitElement, segment.getSegmentId()),
					        (BaseSegmentVariant) segment.getTarget(),
					        targetLang, segment.getSegmentId());
				}
		}
	}

	/**
	 * Writes annotations for a specific variant (source or target)
	 * 
	 * @param unitElement
	 *            the unit element node
	 * @param variantElement
	 *            the variant element node
	 * @param variant
	 *            the Ocelot variant
	 */
	private void writeAnnotations(Element unitElement, Element variantElement,
	        BaseSegmentVariant variant, String language, String segmentId) {
		if (variant != null && variant.getEnirchments() != null
		        && !variant.getEnirchments().isEmpty()) {
			List<Enrichment> varEnrichments = new ArrayList<Enrichment>(
			        variant.getEnirchments());
			Collections.sort(varEnrichments, new EnrichmentComparator());

			LinkedList<CodeWrapper> codes = getCodes(variant);
			Map<Integer, Integer> termEnrichmentsMap = new HashMap<Integer, Integer>();
			List<Enrichment> tripleEnrichments = new ArrayList<Enrichment>();
			for (Enrichment enrichment : varEnrichments) {
				if (!enrichment.isDisabled()) {
					if (enrichment.getType().equals(Enrichment.ENTITY_TYPE)) {
						writeEntityEnrichment(unitElement, variantElement,
						        (EntityEnrichment) enrichment, codes);
					} else if (enrichment.getType()
					        .equals(Enrichment.LINK_TYPE)) {
						tripleEnrichments.add(enrichment);
					} else if (enrichment.getType().equals(
					        Enrichment.TERMINOLOGY_TYPE)) {
						tripleEnrichments.add(enrichment);
						if (!termEnrichmentsMap.containsKey(enrichment
						        .getOffsetNoTagsStartIdx())
						        || termEnrichmentsMap.get(
						                enrichment.getOffsetNoTagsStartIdx())
						                .intValue() != enrichment
						                .getOffsetNoTagsEndIdx()) {

							writeInlineEnrichment(variantElement, enrichment,
							        codes);
							termEnrichmentsMap.put(
							        enrichment.getOffsetNoTagsStartIdx(),
							        enrichment.getOffsetNoTagsEndIdx());
						}
					}
				} else {
					if (enrichment.getType().equals(Enrichment.LINK_TYPE)) {
						tripleEnrichments.add(enrichment);
					} else if (enrichment.getType().equals(
					        Enrichment.ENTITY_TYPE)) {
						removeAnnotationNode(variantElement, enrichment);
					} else if (enrichment.getType().equals(
					        Enrichment.TERMINOLOGY_TYPE)) {
						tripleEnrichments.add(enrichment);
					}
				}
			}
			writeTripleEnrichments(unitElement, variantElement,
			        tripleEnrichments, language, segmentId);
		}
	}

	/**
	 * Writes the entity annotation for a specific variant element
	 * 
	 * @param unitElement
	 *            the unit element node
	 * @param variantElement
	 *            the variant element node
	 * @param enrichment
	 *            the entity enrichment.
	 */
	private void writeEntityEnrichment(Element unitElement,
	        Element variantElement, EntityEnrichment enrichment,
	        LinkedList<CodeWrapper> codes) {

		// Annotators ref
		String annotatorsRefValue = enrichment.getAnnotatorsRefValue();
		if (annotatorsRefValue != null && !annotatorsRefValue.isEmpty()) {
			unitElement.setAttribute(enrichment.getAnnotatorsRefAttribute(),
			        annotatorsRefValue);
		}
		writeInlineEnrichment(variantElement, enrichment, codes);
	}

	/**
	 * Writes an in line annotation for a specific enrichment.
	 * 
	 * @param variantElement
	 *            the variant element node
	 * @param enrichment
	 *            the enrichment
	 * @param codes
	 *            the codes contained in the current variant
	 */
	private void writeInlineEnrichment(Element variantElement,
	        Enrichment enrichment, LinkedList<CodeWrapper> codes) {

		List<NodeWrapper> flatTree = DocumentTreeHelper
		        .getFlatTree(variantElement);
		List<NodeWrapper> newFlatNodes = insertAnnotationNode(flatTree,
		        enrichment, codes);
		DocumentTreeHelper.rebuildTree(variantElement, newFlatNodes);
	}

	/**
	 * Insert an annotation node in the DOM document
	 * 
	 * @param flatNodes
	 *            the flat list of nodes from the DOM document
	 * @param enrichment
	 *            the enrichment
	 * @param codes
	 *            the codes contained in current variant
	 * @return the new flat list of DOM nodes
	 */
	private List<NodeWrapper> insertAnnotationNode(List<NodeWrapper> flatNodes,
	        Enrichment enrichment, List<CodeWrapper> codes) {

		// at the end of the method this list will contain the new list of nodes
		// obtained after the insertion of the annotation node.
		List<NodeWrapper> newNodesList = new ArrayList<NodeWrapper>();

		// this list stores the nodes that will be inserted as
		// children of the annotation (marker) node
		List<NodeWrapper> intermediateNodeList = new ArrayList<NodeWrapper>();
		LinkedList<CodeWrapper> codesQueue = new LinkedList<CodeWrapper>(codes);
		NodeWrapper firstTextNode = null;
		Text currText = null;
		boolean markerInserted = false;
		int totTextLength = 0;
		for (NodeWrapper currNode : flatNodes) {
			if (currNode.getNode().getNodeType() == Node.TEXT_NODE) {
				currText = (Text) currNode.getNode();
				if (!isCode(currText.getData(), codesQueue,
				        currNode.getParent())) {
					if (currText.getData().length() + totTextLength > enrichment
					        .getOffsetNoTagsStartIdx()) {
						// this text includes the first enrichment index
						if (firstTextNode == null) {
							// its the first text node including the first index
							firstTextNode = currNode;
						} else if (markerInserted) {
							// the marker has been already inserted
							newNodesList.add(currNode);
							// it's not the first text node including the first
							// index and the marker hasn't been inserted yet -->
							// it's an intermediate node.
						} else {
							intermediateNodeList.add(currNode);
						}

					} else {
						// this text node actually is a tag code existing in the
						// variant. It must be added to the list of nodes, but
						// not be considered as actual text.
						newNodesList.add(currNode);
					}
					if (!markerInserted
					        && currText.getData().length() + totTextLength >= enrichment
					                .getOffsetNoTagsEndIdx()) {

						insertMarker(currNode, firstTextNode, enrichment,
						        newNodesList, intermediateNodeList,
						        totTextLength);
						markerInserted = true;
					}
					totTextLength += currText.getData().length();
				} else {
					if (firstTextNode == null || markerInserted) {
						newNodesList.add(currNode);
					} else {
						intermediateNodeList.add(currNode);
					}
				}
			} else {
				if (firstTextNode == null || markerInserted) {
					newNodesList.add(currNode);
				} else {
					intermediateNodeList.add(currNode);
				}
			}
		}
		return newNodesList;
	}

	private void insertMarker(NodeWrapper currNode, NodeWrapper firstTextNode,
	        Enrichment enrichment, List<NodeWrapper> newNodesList,
	        List<NodeWrapper> intermediateNodeList, int totTextLength) {

		Text currText = (Text) currNode.getNode();

		// Take the part of text preceding the enriched part
		String firstTextPart = ((Text) firstTextNode.getNode()).getData()
		        .substring(0,
		                enrichment.getOffsetNoTagsStartIdx() - totTextLength);
		if (!firstTextPart.isEmpty()) {
			Text firstTextPartNode = document.createTextNode(firstTextPart);
			newNodesList.add(new NodeWrapper(firstTextPartNode, firstTextNode
			        .getParent()));
		}

		// create a node for the marker
		Element markerNode = document.createElement(enrichment.getMarkerTag());
		markerNode.setAttribute(
		        EnrichmentAnnotationsConstants.MARKER_TAG_ID_ATTR,
		        EnrichmentAnnotationsConstants.MARKER_FREME_ID_PREFIX
		                + (++lastFremeMrkId));
		markerNode.setAttribute(xliffHelper.getTypeAttribute(),
		        enrichment.getTagType());
		if (enrichment.getTag() != null && enrichment.getTagValue() != null) {
			markerNode.setAttribute(enrichment.getTag(),
			        enrichment.getTagValue());
		}
		NodeWrapper markerNodeWRapper = new NodeWrapper(markerNode,
		        firstTextNode.getParent());
		newNodesList.add(markerNodeWRapper);

		// insert all the nodes from the intermediateNodeList as children of the
		// marker node
		for (NodeWrapper intermNode : intermediateNodeList) {
			if (intermNode.getParent().equals(firstTextNode.getParent())) {
				intermNode.setParent(markerNode);
			}
		}
		newNodesList.addAll(intermediateNodeList);

		// create a text node for the annotated text
		if (currNode.equals(firstTextNode)) {

			Text finalAnnotatedText = document
			        .createTextNode(currText.getData().substring(
			                enrichment.getOffsetNoTagsStartIdx()
			                        - totTextLength,
			                enrichment.getOffsetNoTagsEndIdx() - totTextLength));
			newNodesList.add(new NodeWrapper(finalAnnotatedText, markerNode));
		} else {
			Text finalAnnotatedText = document
			        .createTextNode(currText.getData().substring(0,
			                enrichment.getOffsetNoTagsEndIdx() - totTextLength));
			newNodesList.add(new NodeWrapper(finalAnnotatedText, markerNode));
		}

		// create a text node for the final part of the text
		String finalText = currText.getData().substring(
		        enrichment.getOffsetNoTagsEndIdx() - totTextLength);
		if (!finalText.isEmpty()) {
			newNodesList.add(new NodeWrapper(
			        document.createTextNode(finalText), currNode.getParent()));
		}
	}

	private boolean isCode(String text, LinkedList<CodeWrapper> codes,
	        Node parentNode) {

		boolean retValue = false;
		CodeWrapper code = codes.peek();
		if (code != null && parentNode != null) {
			if (code.getTagName().equals(parentNode.getNodeName())){
				codes.poll();
				if(text.equals(code.getData())){
					retValue = true;
				}
			}
		}
		return retValue;
	}

	private LinkedList<CodeWrapper> getCodes(BaseSegmentVariant variant) {

		LinkedList<CodeWrapper> codes = new LinkedList<CodeWrapper>();
		CodeWrapper code = null;
		for (SegmentAtom atom : variant.getAtoms()) {
			if (atom instanceof OkapiCodeAtom) {
				code = new CodeWrapper(
				        findCodeActualTagName((OkapiCodeAtom) atom),
				        ((OkapiCodeAtom) atom).getCode().getData());
				codes.add(code);
			} else if (atom instanceof TaggedCodeAtom && ((TaggedCodeAtom)atom).getTag() instanceof CTag){
				String tagData = ((CTag)((TaggedCodeAtom)atom).getTag()).getData();
				code = new CodeWrapper(findCodeActualTagName((TaggedCodeAtom)atom), tagData);
			}
		}
		return codes;
	}

	private static String findCodeActualTagName(CodeAtom code) {

		String verboseData = code.getVerboseData();
		int endNameIdx = verboseData.indexOf(" ");
		if (endNameIdx == -1) {
			endNameIdx = verboseData.indexOf(">");
		}
		return verboseData.substring(1, endNameIdx);
	}

	/**
	 * Removes the annotation associated to a specific enrichment from a variant
	 * element
	 * 
	 * @param variantElement
	 *            the variant element node
	 * @param enrichment
	 *            the enrichment
	 */
	private void removeAnnotationNode(Element variantElement,
	        Enrichment enrichment) {

		if (variantElement != null) {
			NodeList annotationElements = variantElement
			        .getElementsByTagName(enrichment.getMarkerTag());
			if (annotationElements != null) {
				Element currElem = null;
				Element entityNode = null;
				for (int i = 0; i < annotationElements.getLength(); i++) {
					currElem = (Element) annotationElements.item(i);
					NamedNodeMap attributes = currElem.getAttributes();
					boolean entityNodeFound = attributes != null
					        && attributes.getNamedItem(xliffHelper
					                .getTypeAttribute()) != null
					        && attributes
					                .getNamedItem(
					                        xliffHelper.getTypeAttribute())
					                .getNodeValue()
					                .equals(enrichment.getTagType())
					        && (enrichment.getTag() == null || (attributes
					                .getNamedItem(enrichment.getTag()) != null && attributes
					                .getNamedItem(enrichment.getTag())
					                .getNodeValue()
					                .equals(enrichment.getTagValue())));

					if (entityNodeFound) {
						entityNode = currElem;
						break;
					}
				}
				if (entityNode != null) {
					NodeList entityChildNodes = entityNode.getChildNodes();
					Node entityNextSibling = entityNode.getNextSibling();
					Node parent = entityNode.getParentNode();
					parent.removeChild(entityNode);
					Text parentTextNode = null;
					for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
						if (parent.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE) {
							parentTextNode = (Text) parent.getChildNodes()
							        .item(i);
							break;
						}
					}
					Node childNode = null;
					for (int i = 0; i < entityChildNodes.getLength(); i++) {
						childNode = entityChildNodes.item(i);
						if (childNode.getNodeType() == Node.TEXT_NODE) {
							if (parentTextNode != null) {
								parentTextNode.setData(parentTextNode.getData()
								        + ((Text) childNode).getData());
							} else {
								if (entityNextSibling != null) {
									parent.insertBefore(childNode,
									        entityNextSibling);
								} else {
									parent.appendChild(childNode);
								}
							}
						} else {
							if (entityNextSibling != null) {
								parent.insertBefore(childNode,
								        entityNextSibling);
							} else {
								parent.appendChild(childNode);
							}
						}
					}
				}

			}
		}
	}

	/**
	 * Writes the triples in JSON-LD for those enrichmnents having triples
	 * informatiion.
	 * 
	 * @param unitElement
	 *            the unit element node.
	 * @param variantElement
	 *            the variant element node.
	 * @param tripleEnrichments
	 */
	private void writeTripleEnrichments(Element unitElement,
	        Element variantElement, List<Enrichment> tripleEnrichments,
	        String language, String segmentId) {

		Model tripleModel = ModelFactory.createDefaultModel();
		Set<TermEnrichmentWrapper> allTermEnrichmentsWrap = new HashSet<TermEnrichmentWrapper>();
		Set<TerminologyEnrichment> allTermEnrichments = new HashSet<TerminologyEnrichment>();
		if (tripleEnrichments != null) {
			for (Enrichment enrich : tripleEnrichments) {
				if (enrich.getType().equals(Enrichment.LINK_TYPE)) {
					Model linkModel = ((LinkEnrichment) enrich)
					        .getPropertiesModel();
					if (linkModel != null) {
						if (!enrich.isDisabled()) {
							tripleModel.add(linkModel);
						}
					}
				} else if (enrich.getType().equals(Enrichment.TERMINOLOGY_TYPE)) {
					allTermEnrichmentsWrap.add(new TermEnrichmentWrapper(
					        (TerminologyEnrichment) enrich));
					allTermEnrichments.add((TerminologyEnrichment) enrich);
					if (((TerminologyEnrichment) enrich).getTermTriples() != null) {
						if (!enrich.isDisabled()) {
							tripleModel.add(((TerminologyEnrichment) enrich)
							        .getTermTriples());
						}
					}
				}
			}
			deleteTermEnrichments(variantElement, allTermEnrichmentsWrap,
			        allTermEnrichments);
			NodeList extraNodeList = unitElement
			        .getElementsByTagName(EnrichmentAnnotationsConstants.JSON_TAG_NAME);
			Node extraNode = getExtraNodeForSegment(segmentId, extraNodeList);
//			if (extraNodeList != null && extraNodeList.getLength() > 0) {
//				Node extraNode = extraNodeList.item(0);
			if(extraNode != null){
				Text modelText = (Text) extraNode.getFirstChild();
				Model existingModel = ModelFactory.createDefaultModel();
				StringReader reader = new StringReader(modelText.getData());
				existingModel.read(reader, "",
				        EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
				tripleModel.add(existingModel);
				StringWriter writer = new StringWriter();
				tripleModel.write(writer,
				        EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
				modelText.setData(writer.toString().replace("\r", " ")
				        .replace("\n", " "));
			} else {
				StringWriter writer = new StringWriter();
				tripleModel.write(writer,
				        EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
				Text propTextNode = document.createTextNode(writer.toString()
				        .replace("\r", " ").replace("\n", " "));
				Element tripleNode = document
				        .createElement(EnrichmentAnnotationsConstants.JSON_TAG_NAME);
				tripleNode.setAttribute(
				        EnrichmentAnnotationsConstants.JSON_TAG_DOMAIN_ATTR,
				        EnrichmentAnnotationsConstants.JSON_TAG_DOMAIN);
				if(segmentId != null){
					tripleNode.setAttribute(
					        EnrichmentAnnotationsConstants.JSON_TAG_SEG_ATTR,
					        segmentId);
				}
				tripleNode.appendChild(propTextNode);
				xliffHelper.insertLinkNode(unitElement, tripleNode);
			}
		}
	}
	
	private Node getExtraNodeForSegment(String segmentId,  NodeList extraNodeList){
		
		Node extraNode = null;
		if(extraNodeList != null && extraNodeList.getLength() > 0){
			if(segmentId == null){
				extraNode = extraNodeList.item(0);
			} else {
				Node currNode = null;
				for(int i = 0; i<extraNodeList.getLength(); i++){
					currNode = extraNodeList.item(i);
					AttrImpl segAttr = (AttrImpl) currNode.getAttributes().getNamedItem(EnrichmentAnnotationsConstants.JSON_TAG_SEG_ATTR);
					if(segAttr != null && segmentId.equals(segAttr.getValue())){
						extraNode = currNode;
						break;
					}
				}
			}
		}
		return extraNode;
		
	}

	private void deleteTermEnrichments(Element variantElement,
	        Set<TermEnrichmentWrapper> termToDelete,
	        Set<TerminologyEnrichment> termEnrichments) {

		if (termEnrichments != null) {
			for (TerminologyEnrichment termEnrich : termEnrichments) {
				if (!termEnrich.isDisabled()) {
					termToDelete.remove(new TermEnrichmentWrapper(termEnrich));
				}
			}
		}
		if (!termToDelete.isEmpty()) {
			for (TermEnrichmentWrapper currTerm : termToDelete) {
				removeAnnotationNode(variantElement, currTerm.getEnrichment());
			}
		}
	}
}

class TermEnrichmentWrapper {

	private TerminologyEnrichment enrichment;

	private String offsetString;

	public TermEnrichmentWrapper(TerminologyEnrichment enrichment) {

		this.enrichment = enrichment;
		this.offsetString = enrichment.getOffsetStartIdx() + "-"
		        + enrichment.getOffsetEndIdx();
	}

	public TerminologyEnrichment getEnrichment() {
		return enrichment;
	}

	public String getOffsetString() {
		return offsetString;
	}

	@Override
	public boolean equals(Object obj) {
		boolean retValue = false;
		if (obj instanceof TermEnrichmentWrapper) {
			retValue = offsetString.equals(((TermEnrichmentWrapper) obj)
			        .getOffsetString());
		} else {
			retValue = super.equals(obj);
		}
		return retValue;
	}

	@Override
	public int hashCode() {
		return offsetString.hashCode();
	}
}

class EnrichmentComparator implements Comparator<Enrichment> {

	@Override
	public int compare(Enrichment o1, Enrichment o2) {

		int retValue = 0;
		if (o1.getOffsetNoTagsStartIdx() < o2.getOffsetNoTagsStartIdx()) {
			retValue = -1;
		} else if (o1.getOffsetNoTagsStartIdx() > o2.getOffsetNoTagsStartIdx()) {
			retValue = 1;
		} else if (o1.getOffsetNoTagsEndIdx() > o2.getOffsetNoTagsEndIdx()) {
			retValue = -1;
		} else if (o2.getOffsetNoTagsEndIdx() > o1.getOffsetNoTagsEndIdx()) {
			retValue = 1;
		}
		return retValue;
	}

}

class CodeWrapper {

	private String tagName;

	private String data;

	public CodeWrapper() {
	}

	public CodeWrapper(String tagName, String data) {
		super();
		this.tagName = tagName;
		this.data = data;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {

		return "Tag name = " + tagName + ", data = " + data;
	}

}
