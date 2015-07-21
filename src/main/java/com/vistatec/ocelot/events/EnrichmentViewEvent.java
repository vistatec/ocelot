package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;

public class EnrichmentViewEvent implements OcelotEvent {

	private BaseSegmentVariant variant;
	
	public EnrichmentViewEvent(final BaseSegmentVariant variant) {
		
		this.variant = variant;
	}
	
	public BaseSegmentVariant getVariant(){
		return variant;
	}
}
