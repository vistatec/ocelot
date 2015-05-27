package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class ConcordanceSearchEvent implements OcelotEvent {

	private String text;
	
	public ConcordanceSearchEvent(final String text) {
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
}
