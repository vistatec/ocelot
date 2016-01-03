package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class OcelotEditingEvent implements OcelotEvent {

	public static final int START_EDITING = 0;
	public static final int STOP_EDITING = 1;
	
	private int eventType;
	
	public OcelotEditingEvent(int eventType) {
		this.eventType = eventType;
    }
	
	public int getEventType(){
		return eventType;
	}
}
