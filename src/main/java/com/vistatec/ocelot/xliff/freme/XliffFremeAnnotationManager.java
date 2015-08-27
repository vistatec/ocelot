package com.vistatec.ocelot.xliff.freme;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.Enrichment;
import com.vistatec.ocelot.segment.model.EntityEnrichment;
import com.vistatec.ocelot.segment.model.LinkEnrichment;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.TerminologyEnrichment;
import com.vistatec.ocelot.services.SegmentService;

public class XliffFremeAnnotationManager {

	private static final String VERSION_ATTRIBUTE = "version";

	private FremeXliffHelper xliffHelper;

	private Document document;

	private Map<Integer, Integer> sourceTermEnrichment;

	private Map<Integer, Integer> targetTermEnrichment;

	public void saveAnnotations(final File xliffFile, SegmentService segService) {

		try {
			document = parseFile(xliffFile);
			xliffHelper = FremeXliffHelperFactory
			        .createHelper(detectVersion(document));

			NodeList unitElements = document.getElementsByTagName(xliffHelper
			        .getUnitNodeName());
			if (unitElements != null) {
				for (int i = 0; i < unitElements.getLength(); i++) {
					writeAnnotationsForUnit((Element) unitElements.item(i),
					        segService);
				}
			}

			TransformerFactory transformerFactory = TransformerFactory
			        .newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new FileWriterWithEncoding(
			        new File("C:\\Users\\Martab\\file.xlf"), "UTF-8"));
			transformer.transform(source, result);
			System.out.println("File saved!");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeAnnotationsForUnit(Element unitElement,
	        SegmentService segService) {

		String unitId = xliffHelper.getUnitId(unitElement);
		int segmentNumber = xliffHelper.getSegmentNumber(unitId);
		if (segService.getNumSegments() >= segmentNumber) {
			OcelotSegment segment = segService.getSegment(segmentNumber - 1);
			if (segment.getSegmentNumber() == segmentNumber) {
				if (segment.getSource() instanceof BaseSegmentVariant) {
					writeSourceAnnotations(unitElement,
					        (BaseSegmentVariant) segment.getSource());
				}
			} else {
				System.err
				        .println("Error!!!! Segment numbers do not correspond!");
			}
		}
	}

	private void writeSourceAnnotations(Element unitElement,
	        BaseSegmentVariant source) {
		if (source.getEnirchments() != null
		        && !source.getEnirchments().isEmpty()) {
			for (Enrichment enrichment : source.getEnirchments()) {
				if (!enrichment.isDisabled()) {
					if (enrichment.getType().equals(
					        EntityEnrichment.ENRICHMENT_TYPE)) {
						writeSourceEntityEnrichment(unitElement,
						        (EntityEnrichment) enrichment);
					} else if (enrichment.getType().equals(
					        LinkEnrichment.ENRICHMENT_TYPE)) {
						writeLinkEnrichment(unitElement,
						        (LinkEnrichment) enrichment);
					} else if (enrichment.getType().equals(
					        TerminologyEnrichment.ENRICHMENT_TYPE)) {
						if (sourceTermEnrichment == null
						        || (!sourceTermEnrichment
						                .containsKey(enrichment
						                        .getOffsetStartIdx()) || sourceTermEnrichment
						                .get(enrichment.getOffsetStartIdx())
						                .intValue() != enrichment
						                .getOffsetEndIdx())) {

							writeInlineEnrichment(
									xliffHelper.getSourceElement(unitElement),
									enrichment);
							if(sourceTermEnrichment == null){
								sourceTermEnrichment = new HashMap<Integer, Integer>();
							}
							sourceTermEnrichment.put(enrichment.getOffsetStartIdx(), enrichment.getOffsetEndIdx());
						}
					}
				} else {
					if (enrichment.getType().equals(
					        LinkEnrichment.ENRICHMENT_TYPE)) {
						removeLinkNode(unitElement, (LinkEnrichment) enrichment);
					} else if (enrichment.getType().equals(
					        EntityEnrichment.ENRICHMENT_TYPE)) {
						Element sourceElement = xliffHelper
						        .getSourceElement(unitElement);
						removeAnnotationNode(sourceElement, enrichment);
					}
				}
			}
		}
	}

	private void removeLinkNode(Element unitElement, LinkEnrichment enrichment) {

		Node nodeToDelete = findLinkNode(unitElement, enrichment);
		if (nodeToDelete != null) {
			unitElement.removeChild(nodeToDelete);
		}
	}

	private Node findLinkNode(Element unitElement, LinkEnrichment enrichment) {
		NodeList linkNodeList = unitElement.getElementsByTagName(enrichment
		        .getMarkerTag());
		Node linkNode = null;
		if (linkNodeList != null) {
			for (int i = 0; i < linkNodeList.getLength(); i++) {
				linkNode = linkNodeList.item(i);
				Text linkText = (Text) linkNode.getFirstChild();
				StringReader reader = new StringReader(linkText.getData());
				Model model = ModelFactory.createDefaultModel();
				model.read(reader, null, "JSON-LD");
				ResIterator resources = model.listSubjects();
				if (resources != null && resources.hasNext()) {
					Resource entityResource = resources.next();
					if (entityResource.getURI().equals(
					        enrichment.getReferenceEntity())) {
						break;
					}
				}
			}
		}
		return linkNode;
	}

	private void writeLinkEnrichment(Element unitElement,
	        LinkEnrichment enrichment) {

		Element linkNode = (Element) findLinkNode(unitElement, enrichment);
		if (linkNode == null) {
			linkNode = document.createElement(enrichment.getMarkerTag());
			linkNode.setAttribute(enrichment.getTag(), enrichment.getTagValue());
		} else {
			linkNode.removeChild(linkNode.getFirstChild());
		}
		Model propModel = enrichment.getPropertiesModel();
		StringWriter writer = new StringWriter();
		propModel.write(writer, "JSON-LD");
		Text propTextNode = document.createTextNode(writer.toString()
		        .replace("\r", " ").replace("\n", " "));
		System.out.println(writer.toString());
		linkNode.appendChild(propTextNode);
		unitElement.appendChild(linkNode);
	}

	private void writeSourceEntityEnrichment(Element unitElement,
	        EntityEnrichment enrichment) {

		// Annotators ref
		String annotatorsRefValue = enrichment.getAnnotatorsRefValue();
		if (annotatorsRefValue != null
		        && !enrichment.getAnnotatorsRefValue().isEmpty()) {
			unitElement.setAttribute(enrichment.getAnnotatorsRefAttribute(),
			        annotatorsRefValue);
		}
		Element sourceElement = xliffHelper.getSourceElement(unitElement);
		writeInlineEnrichment(sourceElement, enrichment);
	}

	private void writeInlineEnrichment(Element variantElement,
	        Enrichment enrichment) {

		List<NodeWrapper> flatTree = getFlatTree(variantElement);
		List<NodeWrapper> newFlatNodes = insertAnnotationNode(flatTree,
		        enrichment);
		rebuildTree(variantElement, newFlatNodes);
	}

	private void rebuildTree(Element sourceElement,
	        List<NodeWrapper> newFlatNodes) {

		removeChildrenNode(sourceElement);
		for (NodeWrapper currNode : newFlatNodes) {
			removeChildrenNode(currNode.getNode());
			if (currNode.getParent() != null) {
				currNode.getParent().appendChild(currNode.getNode());
			}
		}

	}

	private void removeChildrenNode(Node node) {

		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			node.removeChild(children.item(i));
		}
	}

	private void removeAnnotationNode(Element variantElement,
	        Enrichment enrichment) {

		NodeList annotationElements = variantElement
		        .getElementsByTagName(enrichment.getMarkerTag());
		if (annotationElements != null) {
			Element currElem = null;
			Element entityNode = null;
			for (int i = 0; i < annotationElements.getLength(); i++) {
				currElem = (Element) annotationElements.item(i);
				NamedNodeMap attributes = currElem.getAttributes();
				// boolean entityNodeFound = attributes != null
				// && enrichment.getTagType().equals(
				// attributes.getNamedItem(
				// xliffHelper.getTypeAttribute())
				// .getNodeValue())
				// && enrichment.getTagValue().equals(
				// attributes.getNamedItem(enrichment.getTag())
				// .getNodeValue());
				boolean entityNodeFound = attributes != null
				        && attributes.getNamedItem(xliffHelper
				                .getTypeAttribute()) != null
				        && attributes
				                .getNamedItem(xliffHelper.getTypeAttribute())
				                .getNodeValue().equals(enrichment.getTagType())
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
						parentTextNode = (Text) parent.getChildNodes().item(i);
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
							parent.insertBefore(childNode, entityNextSibling);
						} else {
							parent.appendChild(childNode);
						}
					}
				}
			}

		}
	}

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
					markerNode.setAttribute(xliffHelper.getTypeAttribute(),
					        enrichment.getTagType());
					if (enrichment.getTag() != null) {
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

	// private void writeSourceEntityEnrichment(Element unitElement,
	// EntityEnrichment enrichment) {
	//
	// String annotatorsRefValue = enrichment.getAnnotatorsRefValue();
	// if (annotatorsRefValue != null
	// && !enrichment.getAnnotatorsRefValue().isEmpty()) {
	// unitElement.setAttribute(enrichment.getAnnotatorsRefAttribute(),
	// annotatorsRefValue);
	// }
	// Element sourceElement = xliffHelper.getSourceElement(unitElement);
	// addAnnotation(sourceElement, enrichment, 0, null);
	// unitElement.setAttribute(enrichment.getAnnotatorsRefAttribute(),
	// enrichment.getAnnotatorsRefValue());
	// // sourceElement.
	//
	// }
	//
	// private String parseSource(Element sourceElement){
	//
	// StringBuilder sourceContent = new StringBuilder();
	// retrieveNodeContent(sourceElement, sourceContent);
	// return sourceContent.toString();
	// }
	//
	// private void retrieveNodeContent(Node node, StringBuilder content){
	//
	// NodeList childList = node.getChildNodes();
	// if(childList != null){
	// Node child = null;
	// for(int i = 0; i<childList.getLength(); i++){
	// child = childList.item(i);
	// if(child.getNodeType() == Node.TEXT_NODE) {
	// content.append(((Text)child).getData());
	// } else {
	// content.append("<");
	// content.append(child.getNodeName());
	// NamedNodeMap attributes = child.getAttributes();
	// if(attributes != null && attributes.getLength() > 0){
	// for(int attrIdx = 0; attrIdx<attributes.getLength(); attrIdx++){
	// content.append(" ");
	// content.append(attributes.item(attrIdx).getNodeName());
	// content.append("=\"");
	// content.append(attributes.item(attrIdx).getNodeValue());
	// content.append("\"");
	// }
	// }
	// content.append(">");
	// retrieveNodeContent(child, content);
	// content.append("</");
	// content.append(child.getNodeName());
	// content.append(">");
	// }
	// }
	// }
	// }
	//
	// private void addAnnotation(Node element, Enrichment enrichment,
	// int currIdx, Node firstTextNode) {
	//
	// NodeList childList = element.getChildNodes();
	// if (childList != null) {
	// Node currNode = null;
	// for (int i = 0; i < childList.getLength(); i++) {
	// currNode = childList.item(i);
	// if (currNode.getNodeType() == Node.TEXT_NODE) {
	// Text currText = (Text) currNode;
	// if (firstTextNode == null) {
	// if (currText.getData().length() > currIdx
	// + enrichment.getOffsetStartIdx()) {
	// firstTextNode = currText;
	// if (currText.getData().length() > enrichment
	// .getOffsetEndIdx()) {
	// String wholeText = currText.getData();
	// firstTextNode = document
	// .createTextNode(wholeText.substring(0,
	// enrichment.getOffsetStartIdx()));
	// Element entityElem = document
	// .createElement("mrk");
	// entityElem.setAttribute("type",
	// (enrichment.getTagType()));
	// entityElem.setAttribute(enrichment.getTag(),
	// ((EntityEnrichment) enrichment)
	// .getEntityURL());
	// Text annotatedTextNode = document
	// .createTextNode(wholeText.substring(
	// enrichment.getOffsetStartIdx(),
	// enrichment.getOffsetEndIdx()));
	// entityElem.appendChild(annotatedTextNode);
	//
	// element.insertBefore(entityElem, currText);
	// element.insertBefore(firstTextNode, entityElem);
	// currText.setData(wholeText.substring(enrichment
	// .getOffsetEndIdx()));
	//
	// } else {
	// currIdx = currText.getData().length() - 1;
	// addAnnotation(currNode, enrichment, currIdx, firstTextNode);
	// }
	// } else {
	// if(currText.getData().length() > enrichment.getOffsetEndIdx() + currIdx){
	//
	// } else {
	// currIdx = currText.getData().length() - 1;
	// addAnnotation(currNode, enrichment, currIdx, firstTextNode);
	// }
	// }
	// }
	//
	// }
	// }
	// }
	// }

	private List<NodeWrapper> getFlatTree(Node node) {

		List<NodeWrapper> nodes = new ArrayList<NodeWrapper>();
		// nodes.add(node);
		if (node.getChildNodes() != null) {
			Node child = null;
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				child = node.getChildNodes().item(i);
				nodes.add(new NodeWrapper(child, node));
				nodes.addAll(getFlatTree(child));
			}
		}
		return nodes;
	}

	private Document parseFile(File xliffFile)
	        throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xliffFile);
		doc.getDocumentElement().normalize();
		return doc;
	}

	private String detectVersion(Document doc) {

		return doc.getDocumentElement().getAttribute(VERSION_ATTRIBUTE);
	}

//	private void retrieveWholeText(Node node, StringBuilder strBuilder) {
//		NodeList list = node.getChildNodes();
//		Node child = null;
//		for (int i = 0; i < list.getLength(); i++) {
//			child = list.item(i);
//			if (child.getNodeType() == Node.TEXT_NODE) {
//				Text textNode = (Text) child;
//				strBuilder.append(textNode.getData());
//			}
//			retrieveWholeText(child, strBuilder);
//		}
//	}

	public void testMethod(File xliffFile) {
		// try {
		// DocumentBuilderFactory dbFactory = DocumentBuilderFactory
		// .newInstance();
		// DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		// Document doc = dBuilder.parse(xliffFile);
		// doc.getDocumentElement().normalize();
		// FremeXliffHelper xliffHelper = new FremeXliff1_2Helper();
		// NodeList list = doc.getElementsByTagName(xliffHelper
		// .getUnitNodeName());
		// for (int i = 0; i < list.getLength(); i++) {
		// Node currNode = list.item(i);
		// if (currNode.getNodeType() == Node.ELEMENT_NODE) {
		// Element unitElement = (Element) currNode;
		// Element sourceElement = xliffHelper
		// .getSourceElement(unitElement);
		// String sourceContent = parseSource(sourceElement);
		// System.out.println(sourceContent);
		// // StringBuilder wholeText = new StringBuilder();
		// // retrieveWholeText(sourceElement, wholeText);
		// // System.out.println(wholeText.toString());
		// // System.out.println(sourceElement.getNodeValue());
		// }
		// }
		// } catch (ParserConfigurationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (SAXException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public static void main(String[] args) {

		String str = "{    \"@id\" : \"dbpedia:Dublin\",    \"areaTotal\" : \"114.99\",    \"abstract\" : \"Dublin (/ˈdʌblɨn/, Irish: Baile Átha Cliath [blʲa:ˈklʲiəh]) is the capital and largest city of Ireland. Dublin is in the province of Leinster on Ireland's east coast, at the mouth of the River Liffey.Founded as a Viking settlement, the Kingdom of Dublin became Ireland's principal city following the Norman invasion. The city expanded rapidly from the 17th century and was briefly the second largest city in the British Empire before the Act of Union in 1800. Following the partition of Ireland in 1922, Dublin became the capital of the Irish Free State, later renamed Ireland.Dublin is administered by a City Council. The city is listed by the Globalization and World Cities Research Network (GaWC) as a global city, with a ranking of \\\"Alpha-\\\", placing it among the top thirty cities in the world. It is a historical and contemporary centre for education, the arts, administration, economy and industry.\",    \"populationTotal\" : \"527612\",    \"location\" : \"Merrion Square, Dublin, ; extremes from all Dublin stations.\",    \"comment\" : \"Dublin (/ˈdʌblɨn/, Irish: Baile Átha Cliath [blʲa:ˈklʲiəh]) is the capital and largest city of Ireland. Dublin is in the province of Leinster on Ireland's east coast, at the mouth of the River Liffey.Founded as a Viking settlement, the Kingdom of Dublin became Ireland's principal city following the Norman invasion. The city expanded rapidly from the 17th century and was briefly the second largest city in the British Empire before the Act of Union in 1800.\",    \"label\" : \"Dublin\",    \"lat\" : \"53.3477783203125\",    \"long\" : \"-6.2597222328186035156\",    \"homepage\" : \"http://www.dublincity.ie/\",    \"isPrimaryTopicOf\" : \"http://en.wikipedia.org/wiki/Dublin\",    \"@context\" : {      \"comment\" : \"http://www.w3.org/2000/01/rdf-schema#comment\",      \"homepage\" : \"http://xmlns.com/foaf/0.1/homepage\",      \"abstract\" : \"http://dbpedia.org/ontology/abstract\",      \"long\" : \"http://www.w3.org/2003/01/geo/wgs84_pos#long\",      \"areaTotal\" : \"http://dbpedia.org/ontology/PopulatedPlace/areaTotal\",      \"label\" : \"http://www.w3.org/2000/01/rdf-schema#label\",      \"populationTotal\" : \"http://dbpedia.org/ontology/populationTotal\",      \"lat\" : \"http://www.w3.org/2003/01/geo/wgs84_pos#lat\",      \"isPrimaryTopicOf\" : \"http://xmlns.com/foaf/0.1/isPrimaryTopicOf\",      \"location\" : \"http://dbpedia.org/property/location\",      \"dbo\" : \"http://dbpedia.org/ontology/\",      \"geo\" : \"http://www.w3.org/2003/01/geo/wgs84_pos#\",      \"dbp\" : \"http://dbpedia.org/property/\",      \"rdfs\" : \"http://www.w3.org/2000/01/rdf-schema#\",      \"dbpedia\" : \"http://dbpedia.org/resource/\",      \"foaf\" : \"http://xmlns.com/foaf/0.1/\",      \"dc\" : \"http://purl.org/dc/elements/1.1/\"    }  } ";
		StringReader reader = new StringReader(str);
		Model model = ModelFactory.createDefaultModel();
		model.read(reader, null, "JSON-LD");
		StringWriter writer = new StringWriter();
		model.write(writer, "JSON-LD");
		System.out.println(writer.toString());
		// try {
		// File file = new File(
		// "C:\\Users\\Martab\\Projects\\XLIFF Files\\FremeAnnotations",
		// "linkAnnotation.json");
		// Model model = ModelFactory.createDefaultModel();
		// model.read(new FileInputStream(file), null, "JSON-LD");
		// StmtIterator statements = model.listStatements();
		// Statement currStatement = null;
		// while (statements.hasNext()) {
		// currStatement = statements.next();
		// System.out.print("<"+currStatement.getSubject().toString()+">");
		// System.out.print(" <" + currStatement.getPredicate().toString() +
		// ">");
		// RDFNode object = currStatement.getObject();
		// if (object instanceof Resource) {
		// System.out
		// .print(" <" + currStatement.getObject().toString()+">");
		// } else {
		// System.out.print(" \""
		// + currStatement.getObject().toString() + "\"");
		// }
		// System.out.println(".");
		// }
		// // model.write(new OutputStreamWriter(System.out), "JSON-LD");
		// StringWriter writer = new StringWriter();
		// model.write(writer, "JSON-LD");
		// System.out.println(writer.toString());
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// XliffFremeAnnotationManager manager = new
		// XliffFremeAnnotationManager();
		// File xliffFile = new File(
		// "C:\\Users\\Martab\\Projects\\XLIFF Files\\FremeAnnotations",
		// "EntityAnnotated.xlf");
		// manager.testMethod(xliffFile);
		// try {
		// DocumentBuilderFactory dbFactory = DocumentBuilderFactory
		// .newInstance();
		// DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		// File xliffFile = new File(
		// "C:\\Users\\Martab\\Projects\\XLIFF Files\\FremeAnnotations",
		// "EntityAnnotated.xlf");
		// Document doc = dBuilder.parse(xliffFile);
		// doc.getDocumentElement().normalize();
		// FremeXliffHelper xliffHelper = new FremeXliff1_2Helper();
		// NodeList list =
		// doc.getElementsByTagName(xliffHelper.getUnitNodeName());
		// for(int i = 0; i<list.getLength(); i++){
		// Node currNode = list.item(i);
		// if(currNode.getNodeType() == Node.ELEMENT_NODE){
		// Element unitElement = (Element)currNode;
		// Element sourceElement = xliffHelper.getSourceElement(unitElement);
		// StringBuilder wholeText = new StringBuilder();
		// NodeList childNodes = sourceElement.getChildNodes();
		// Node child= null;
		// for(int k = 0; k<childNodes.getLength(); k++){
		// child = childNodes.item(k);
		// if(child.getNodeType() == Node.TEXT_NODE){
		// Text textNode = (Text)child;
		// wholeText.append(textNode.getData());
		// }
		// }
		// System.out.println(sourceElement.getNodeValue());
		// }
		// }
		// } catch (ParserConfigurationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (SAXException e) {
		// // TODO Auto-generated catch bloc)k
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}

class NodeWrapper {

	private Node node;

	private Node parent;

	public NodeWrapper(Node node, Node parent) {
		this.node = node;
		this.parent = parent;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

}
