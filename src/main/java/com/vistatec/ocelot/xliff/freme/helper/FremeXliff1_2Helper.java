package com.vistatec.ocelot.xliff.freme.helper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FremeXliff1_2Helper extends FremeXliffHelper {

	private static final String UNIT_NODE_NAME = "trans-unit";

	private static final String TARGET_NODE_NAME = "target";
	
	private static final String SOURCE_NODE_NAME = "source";

	private static final String UNIT_ID_ATTR_NAME = "id";
	
	private static final String TYPE_ATTRIBUTE = "mtype";

	@Override
	public String getUnitNodeName() {
		return UNIT_NODE_NAME;
	}

//	@Override
//	public int getSegmentNumber(String unitId) {
//		int unitIdNum = Integer.parseInt(unitId);
//		return unitIdNum + 1;
//	}
//
//	@Override
//	public String getUnitId(String segmentNumber) {
//		int segmentNumberNum = Integer.parseInt(segmentNumber);
//		return String.valueOf(segmentNumberNum - 1);
//	}

	@Override
	public Element getSourceElement(Element unitElement, String segmentId) {
		NodeList nodes = unitElement.getChildNodes();
		Element source = null;
		int i = 0;
		while(i<nodes.getLength() && source == null){
			if(nodes.item(i).getNodeName().equals(SOURCE_NODE_NAME)){
				source = (Element)nodes.item(i);
			} else {
				i++;
			}
		}
		
		return source;
	}

	@Override
	public Element getTargetElement(Element unitElement, String segmentId) {
		Element targetElement = null;
		NodeList nodes = unitElement.getChildNodes();
		int i = 0;
		while(i<nodes.getLength() && targetElement == null){
			if(nodes.item(i).getNodeName().equals(TARGET_NODE_NAME)){
				targetElement = (Element)nodes.item(i);
			} else {
				i++;
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
		
		if(unitElement != null && linkNode != null){
			unitElement.appendChild(linkNode);
		}
    }

	@Override
    public String getTypeAttribute() {
	    return TYPE_ATTRIBUTE;
    }

}
