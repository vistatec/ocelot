package com.vistatec.ocelot.xliff.freme;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FremeXliff2_0Helper implements FremeXliffHelper {

	@Override
	public String getUnitNodeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSegmentNumber(String unitId) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public String getUnitId(String segmentNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getSourceElement(Element unitElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getTargetElement(Element unitElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnitId(Element unitElement) {

		return null;
	}

	@Override
	public void insertLinkNode(Element unitElement, Node linkNode) {
		if (unitElement != null && linkNode != null) {
			unitElement.insertBefore(linkNode, unitElement.getFirstChild());
		}

	}

	@Override
    public String getTypeAttribute() {
	    // TODO Auto-generated method stub
	    return null;
    }

}
