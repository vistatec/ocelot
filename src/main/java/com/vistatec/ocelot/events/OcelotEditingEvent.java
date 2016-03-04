package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class OcelotEditingEvent implements OcelotEvent {

    public enum Type {
        START_EDITING,
        STOP_EDITING;
    }
	
	private Type eventType;
	
	public OcelotEditingEvent(Type eventType) {
		this.eventType = eventType;
    }
	
	public Type getEventType(){
		return eventType;
	}
}
