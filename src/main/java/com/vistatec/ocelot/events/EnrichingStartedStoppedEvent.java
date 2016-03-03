package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class EnrichingStartedStoppedEvent implements OcelotEvent {

	public static final int STARTED = 0;

	public static final int STOPPED = 1;

	private int action;

	public EnrichingStartedStoppedEvent(int action) {
		
		this.action = action;
	}
	
	public int getAction(){
		return action;
	}
}
