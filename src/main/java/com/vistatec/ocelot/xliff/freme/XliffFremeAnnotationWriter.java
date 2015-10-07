package com.vistatec.ocelot.xliff.freme;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.log4j.Logger;
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
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;
import com.vistatec.ocelot.services.SegmentService;
import com.vistatec.ocelot.xliff.freme.helper.DocumentTreeHelper;
import com.vistatec.ocelot.xliff.freme.helper.FremeXliffHelper;
import com.vistatec.ocelot.xliff.freme.helper.FremeXliffHelperFactory;
import com.vistatec.ocelot.xliff.freme.helper.DocumentTreeHelper.NodeWrapper;
import com.vistatec.ocelot.xliff.freme.helper.FremeXliffHelperFactory.UnsupportedVersionException;

public class XliffFremeAnnotationWriter {

	/** Version attribute in XLIFF files. */
	private static final String VERSION_ATTRIBUTE = "version";

	/** The logger for this class. */
	private final Logger logger = Logger
	        .getLogger(XliffFremeAnnotationWriter.class);

	/** The dom document retrieved by parsing the XLIFF file. */
	private Document document;

	/** Helper class for managing XLIFF files. */
	private FremeXliffHelper xliffHelper;

	/** The last id used for FREME mrk tag. */
	private int lastFremeMrkId;

	/**
	 * For each enrichment in the Ocelot segments, a proper annotation is
	 * written into the XLIFF file.
	 * 
	 * @param xliffFile
	 *            the XLIFF file.
	 * @param segService
	 *            the segment service.
	 */
	public void saveAnnotations(final File xliffFile, SegmentService segService) {

		logger.info("Saving enrichment annotations for file "
		        + xliffFile.getName());
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
		StreamResult result = new StreamResult(new FileWriterWithEncoding(
		        new File(filePath), "UTF-8"));
		transformer.transform(source, result);
		System.out.println("File saved!");
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
		int segmentNumber = xliffHelper.getSegmentNumber(unitId);
		logger.debug("Writing annotations for unit " + unitId + " and segment "
		        + segmentNumber);
		if (segService.getNumSegments() >= segmentNumber) {
			OcelotSegment segment = segService.getSegment(segmentNumber - 1);
			if (segment.getSegmentNumber() == segmentNumber) {
				if (segment.getSource() instanceof BaseSegmentVariant) {
					writeAnnotations(unitElement,
					        xliffHelper.getSourceElement(unitElement),
					        (BaseSegmentVariant) segment.getSource());
					writeAnnotations(unitElement,
					        xliffHelper.getTargetElement(unitElement),
					        (BaseSegmentVariant) segment.getTarget());
				}
			} else {
				logger.error("Error while detecting segment with segment number "
				        + segmentNumber
				        + ". Obtained segment number is "
				        + segment.getSegmentNumber());
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
	        BaseSegmentVariant variant) {
		if (variant != null && variant.getEnirchments() != null
		        && !variant.getEnirchments().isEmpty()) {
			Map<Integer, Integer> termEnrichmentsMap = new HashMap<Integer, Integer>();
			List<Enrichment> tripleEnrichments = new ArrayList<Enrichment>();
			for (Enrichment enrichment : variant.getEnirchments()) {
				if (!enrichment.isDisabled()) {
					if (enrichment.getType().equals(
					        Enrichment.ENTITY_TYPE)) {
						writeEntityEnrichment(unitElement, variantElement,
						        (EntityEnrichment) enrichment);
					} else if (enrichment.getType().equals(
					        Enrichment.LINK_TYPE)) {
						tripleEnrichments.add(enrichment);
					} else if (enrichment.getType().equals(
					        Enrichment.TERMINOLOGY_TYPE)) {
						tripleEnrichments.add(enrichment);
						if (!termEnrichmentsMap.containsKey(enrichment
						        .getOffsetStartIdx())
						        || termEnrichmentsMap.get(
						                enrichment.getOffsetStartIdx())
						                .intValue() != enrichment
						                .getOffsetEndIdx()) {

							writeInlineEnrichment(variantElement, enrichment);
							termEnrichmentsMap.put(
							        enrichment.getOffsetStartIdx(),
							        enrichment.getOffsetEndIdx());
						}
					}
				} else {
					if (enrichment.getType().equals(
					        Enrichment.LINK_TYPE)) {
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
			        tripleEnrichments);
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
	        Element variantElement, EntityEnrichment enrichment) {

		// Annotators ref
		String annotatorsRefValue = enrichment.getAnnotatorsRefValue();
		if (annotatorsRefValue != null
		        && !annotatorsRefValue.isEmpty()) {
			unitElement.setAttribute(enrichment.getAnnotatorsRefAttribute(),
			        annotatorsRefValue);
		}
		writeInlineEnrichment(variantElement, enrichment);
	}

	/**
	 * Writes an in line annotation for a specific enrichment.
	 * 
	 * @param variantElement
	 *            the variant element node
	 * @param enrichment
	 *            the enrichment
	 */
	private void writeInlineEnrichment(Element variantElement,
	        Enrichment enrichment) {

		List<NodeWrapper> flatTree = DocumentTreeHelper
		        .getFlatTree(variantElement);
		List<NodeWrapper> newFlatNodes = insertAnnotationNode(flatTree,
		        enrichment);
		DocumentTreeHelper.rebuildTree(variantElement, newFlatNodes);
	}

	/**
	 * Insert an annotation node in the DOM document
	 * 
	 * @param flatNodes
	 *            the flat list of nodes from the DOM document
	 * @param enrichment
	 *            the enrichment
	 * @return the new flat list of DOM nodes
	 */
	private List<NodeWrapper> insertAnnotationNode(List<NodeWrapper> flatNodes,
	        Enrichment enrichment) {

		List<NodeWrapper> newNodesList = new ArrayList<NodeWrapper>();
		List<NodeWrapper> intermediateNodeList = new ArrayList<NodeWrapper>();
		NodeWrapper firstTextNode = null;
		Text currText = null;
		boolean markerInserted = false;
		int totTextLength = 0;
		for (NodeWrapper currNode : flatNodes) {
			if (currNode.getNode().getNodeType() == Node.TEXT_NODE) {
				currText = (Text) currNode.getNode();
				// this text includes the first enrichment index
				if (currText.getData().length() + totTextLength >= enrichment
				        .getOffsetStartIdx()) {
					// its the first text node including the first index
					if (firstTextNode == null) {
						firstTextNode = currNode;
						// the marker has been already inserted
					} else if (markerInserted) {
						newNodesList.add(currNode);
						// it's not the first text node including the first
						// index and the marker hasn't been inserted yet -->
						// it's an intermediate node.
					} else {
						intermediateNodeList.add(currNode);
					}

				} else {
					newNodesList.add(currNode);
				}
				if (!markerInserted
				        && currText.getData().length() + totTextLength >= enrichment
				                .getOffsetEndIdx()) {

					String firstTextPart = ((Text) firstTextNode.getNode())
					        .getData().substring(
					                0,
					                enrichment.getOffsetStartIdx()
					                        - totTextLength);
					if (!firstTextPart.isEmpty()) {
						Text firstTextPartNode = document
						        .createTextNode(firstTextPart);
						newNodesList.add(new NodeWrapper(firstTextPartNode,
						        firstTextNode.getParent()));
					}
					Element markerNode = document.createElement(enrichment
					        .getMarkerTag());
					markerNode
					        .setAttribute(
					                EnrichmentAnnotationsConstants.MARKER_TAG_ID_ATTR,
					                EnrichmentAnnotationsConstants.MARKER_FREME_ID_PREFIX
					                        + (++lastFremeMrkId));
					markerNode.setAttribute(xliffHelper.getTypeAttribute(),
					        enrichment.getTagType());
					if (enrichment.getTag() != null
					        && enrichment.getTagValue() != null) {
						markerNode.setAttribute(enrichment.getTag(),
						        enrichment.getTagValue());
					}
					newNodesList.add(new NodeWrapper(markerNode, firstTextNode
					        .getParent()));
					for (NodeWrapper intermNode : intermediateNodeList) {
						if (intermNode.getParent().equals(
						        firstTextNode.getParent())) {
							intermNode.setParent(markerNode);
						}
					}
					newNodesList.addAll(intermediateNodeList);
					if (currNode.equals(firstTextNode)) {

						Text finalAnnotatedText = document
						        .createTextNode(currText.getData().substring(
						                enrichment.getOffsetStartIdx()
						                        - totTextLength,
						                enrichment.getOffsetEndIdx()
						                        - totTextLength));
						newNodesList.add(new NodeWrapper(finalAnnotatedText,
						        markerNode));
					} else {
						Text finalAnnotatedText = document
						        .createTextNode(currText.getData().substring(
						                0,
						                enrichment.getOffsetEndIdx()
						                        - totTextLength));
						newNodesList.add(new NodeWrapper(finalAnnotatedText,
						        markerNode));
					}

					String finalText = currText.getData().substring(
					        enrichment.getOffsetEndIdx() - totTextLength);
					if (!finalText.isEmpty()) {
						newNodesList.add(new NodeWrapper(document
						        .createTextNode(finalText), currNode
						        .getParent()));
					}
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
		}
		return newNodesList;
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
	        Element variantElement, List<Enrichment> tripleEnrichments) {

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
				} else if (enrich.getType().equals(
				        Enrichment.TERMINOLOGY_TYPE)) {
					allTermEnrichmentsWrap.add(new TermEnrichmentWrapper((TerminologyEnrichment) enrich));
					allTermEnrichments.add((TerminologyEnrichment) enrich);
					if (((TerminologyEnrichment) enrich).getTermTriples() != null) {
						if (!enrich.isDisabled()) {
							tripleModel.add(((TerminologyEnrichment) enrich)
							        .getTermTriples());
						}
					}
				}
			}
			deleteTermEnrichments(variantElement, allTermEnrichmentsWrap, allTermEnrichments);
			NodeList extraNodeList = unitElement
			        .getElementsByTagName(EnrichmentAnnotationsConstants.JSON_TAG_NAME);
			if (extraNodeList != null && extraNodeList.getLength() > 0) {
				Node extraNode = extraNodeList.item(0);
				Text modelText = (Text) extraNode.getFirstChild();
				StringWriter writer = new StringWriter();
				tripleModel.write(writer, EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
				modelText.setData(writer.toString().replace("\r", " ")
				        .replace("\n", " "));
			} else {
				StringWriter writer = new StringWriter();
				tripleModel.write(writer, EnrichmentAnnotationsConstants.JSON_LD_FORMAT);
				Text propTextNode = document.createTextNode(writer.toString()
				        .replace("\r", " ").replace("\n", " "));
				Element tripleNode = document
				        .createElement(EnrichmentAnnotationsConstants.JSON_TAG_NAME);
				tripleNode.setAttribute(
				        EnrichmentAnnotationsConstants.JSON_TAG_DOMAIN_ATTR,
				        EnrichmentAnnotationsConstants.JSON_TAG_DOMAIN);
				tripleNode.appendChild(propTextNode);
				xliffHelper.insertLinkNode(unitElement, tripleNode);
			}
		}
	}
	
	private void deleteTermEnrichments(Element variantElement, Set<TermEnrichmentWrapper> termToDelete, Set<TerminologyEnrichment> termEnrichments){
		
		if(termEnrichments != null){
			for(TerminologyEnrichment termEnrich: termEnrichments){
				if(!termEnrich.isDisabled()){
					termToDelete.remove(new TermEnrichmentWrapper(termEnrich));
				}
			}
		}
		if(!termToDelete.isEmpty()){
			for(TermEnrichmentWrapper currTerm: termToDelete){
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
		this.offsetString = enrichment.getOffsetStartIdx() + "-" + enrichment.getOffsetEndIdx();
    }
	
	public TerminologyEnrichment getEnrichment(){
		return enrichment;
	}
	
	public String getOffsetString(){
		return offsetString;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean retValue = false;
		if(obj instanceof TermEnrichmentWrapper){
			retValue = offsetString.equals(((TermEnrichmentWrapper)obj).getOffsetString());
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
