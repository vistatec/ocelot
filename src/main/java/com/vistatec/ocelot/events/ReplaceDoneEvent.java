package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class ReplaceDoneEvent implements OcelotEvent {
	
	private int replacedOccurrencesNum;
	
	public ReplaceDoneEvent( int replacedOccurrencesNum) {
		
		this.replacedOccurrencesNum = replacedOccurrencesNum;
	}
	
	public int getReplacedOccurrencesNum(){
		return replacedOccurrencesNum;
	}

}
