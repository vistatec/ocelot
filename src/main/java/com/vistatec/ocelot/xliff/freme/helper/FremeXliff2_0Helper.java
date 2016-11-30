package com.vistatec.ocelot.xliff.freme.helper;

import java.util.List;

import org.apache.xerces.dom.DeferredAttrImpl;
import org.apache.xerces.dom.DeferredElementImpl;
import org.apache.xerces.dom.ElementImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.services.SegmentService;

public class FremeXliff2_0Helper extends FremeXliffHelper {

	private static final String UNIT_NODE_NAME = "unit";

	private static final String SEGMENT_NODE_NAME = "segment";
	
	private static final String SEGMENT_ID_ATTR_NAME = "id";

	private static final String TARGET_NODE_NAME = "target";

	private static final String SOURCE_NODE_NAME = "source";

	private static final String UNIT_ID_ATTR_NAME = "id";

	private static final String TYPE_ATTRIBUTE = "type";

	@Override
	public String getUnitNodeName() {
		return UNIT_NODE_NAME;
	}

//	@Override
//	public int getSegmentNumber(String unitId) {
//		return Integer.parseInt(unitId);
//	}
//
//	@Override
//	public String getUnitId(String segmentNumber) {
//		return segmentNumber;
//	}

	private Element getSegmentElement(Element unitElement, String segmentId) {
		NodeList nodes = unitElement.getElementsByTagName(SEGMENT_NODE_NAME);
		Element segment = null;
		int i = 0;
		while (i < nodes.getLength() && segment == null) {
			if (isDesiredSegment((Element)nodes.item(i), segmentId)) {
				segment = (Element) nodes.item(i);
			} else {
				i++;
			}
		}

		return segment;
	}

	private boolean isDesiredSegment(Element node, String segId ){
		
		return node.getAttribute(SEGMENT_ID_ATTR_NAME).equals(segId);
	}
	
	@Override
	public Element getSourceElement(Element unitElement, String segmentId) {
		Element source = null;
		Element segmentElement = getSegmentElement(unitElement, segmentId);
		if (segmentElement != null) {
			NodeList nodes = segmentElement.getChildNodes();
			int i = 0;
			while (i < nodes.getLength() && source == null) {
				if (nodes.item(i).getNodeName().equals(SOURCE_NODE_NAME)) {
					source = (Element) nodes.item(i);
				} else {
					i++;
				}
			}
		}

		return source;
	}

	@Override
	public Element getTargetElement(Element unitElement, String segmentId) {
		Element targetElement = null;
		Element segmentElement = getSegmentElement(unitElement, segmentId);
		if (segmentElement != null) {
			NodeList nodes = segmentElement.getChildNodes();
			int i = 0;
			while (i < nodes.getLength() && targetElement == null) {
				if (nodes.item(i).getNodeName().equals(TARGET_NODE_NAME)) {
					targetElement = (Element) nodes.item(i);
				} else {
					i++;
				}
			}
		}
		return targetElement;
	}

	@Override
	public String getUnitId(Element unitElement) {

		return unitElement.getAttribute(UNIT_ID_ATTR_NAME);
	}

	@Override
	public void insertLinkNode(Element unitElement, Node linkNode) {
		if (unitElement != null && linkNode != null) {
//			unitElement.appendChild(linkNode);
			unitElement.insertBefore(linkNode, unitElement.getFirstChild());
		}

	}

	@Override
	public String getTypeAttribute() {
		return TYPE_ATTRIBUTE;
	}

}
