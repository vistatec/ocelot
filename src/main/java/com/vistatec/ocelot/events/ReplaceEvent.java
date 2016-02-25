package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class ReplaceEvent implements OcelotEvent {

	public static final int REPLACE = 0;
	
	public static final int REPLACE_ALL = 1;
	
	private String newString;
	
	private int action;
	
	private int segmentIndex;

	public ReplaceEvent(String newString, int action) {
		this.newString = newString;
		this.action = action;
	}
	
	public ReplaceEvent(String newString, int segmentIndex, int action) {
		this.newString = newString;
		this.segmentIndex = segmentIndex;
		this.action = action;
	}
	

	public String getNewString() {
		return newString;
	}
	
	
	public int getSegmentIndex(){
		return segmentIndex;
	}
	
	public int getAction(){
		return action;
	}
}
