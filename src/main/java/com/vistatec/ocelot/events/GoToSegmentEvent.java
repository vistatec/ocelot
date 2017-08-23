package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class GoToSegmentEvent implements OcelotEvent {

	private int segmentNumber;
	
	public GoToSegmentEvent(int segmentNumber) {
		this.segmentNumber = segmentNumber;
	}
	
	public int getSegmentNuber(){
		return segmentNumber;
	}

}
