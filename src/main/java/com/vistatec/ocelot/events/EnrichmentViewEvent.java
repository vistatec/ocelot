package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;

public class EnrichmentViewEvent implements OcelotEvent {

	public static final int STD_VIEW = 0;
	
	public static final int GRAPH_VIEW = 1;
	
	private BaseSegmentVariant variant;
	
	private int segNum;
	
	private int viewType;
	
	private boolean target;
	
	public EnrichmentViewEvent(final BaseSegmentVariant variant, final int segNum, final int viewType, final boolean target) {
		
		this.variant = variant;
		this.viewType = viewType;
		this.target = target;
		this.segNum = segNum;
	}
	
	public BaseSegmentVariant getVariant(){
		return variant;
	}
	
	public int getViewType() {
	    return viewType;
    }
	
	public int getSegNum(){
		return segNum;
	}
	
	public boolean isTarget() {
		return target;
	}
}
