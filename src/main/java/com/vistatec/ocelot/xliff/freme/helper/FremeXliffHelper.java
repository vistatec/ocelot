package com.vistatec.ocelot.xliff.freme.helper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface FremeXliffHelper {

	public String getUnitNodeName();

	public int getSegmentNumber(String unitId);
	
	public String getUnitId(String segmentNumber);
	
	public Element getSourceElement(Element unitElement);
	
	public Element getTargetElement(Element unitElement);
	
	public String getUnitId(Element unitElement);
	
	public void insertLinkNode(Element unitElement, Node linkNode);
	
	public String getTypeAttribute();
}
