package com.vistatec.ocelot.xliff.freme.helper;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.services.SegmentService;

public abstract class FremeXliffHelper {

	public abstract String getUnitNodeName();

	public int getSegmentNumber(String unitId, SegmentService segService) {
		
		int segNumber = -1;
		for(int i = 0; i<segService.getNumSegments(); i++){
			if(segService.getSegment(i).getTuId().equals(unitId)){
				segNumber = segService.getSegment(i).getSegmentNumber();
				break;
			}
		}
		return segNumber;
	}
	
	public String getUnitId(OcelotSegment segment){return null;}
	
	public abstract Element getSourceElement(Element unitElement, String segmentId);
	
	public abstract Element getTargetElement(Element unitElement, String segmentId);
	
	public abstract String getUnitId(Element unitElement);
	
	public abstract void insertLinkNode(Element unitElement, Node linkNode);
	
	public abstract String getTypeAttribute();

	public List<OcelotSegment> getSegmentsForUnit(String unitId,
            SegmentService segService) {
		
		List<OcelotSegment> segments = new ArrayList<OcelotSegment>();
		for(int i = 0; i<segService.getNumSegments(); i++){
			if(segService.getSegment(i).getTuId().equals(unitId)){
				segments.add(segService.getSegment(i));
			}
		}
		return segments;
	}
}
