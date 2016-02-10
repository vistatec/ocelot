package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class HighlightEvent implements OcelotEvent {

	private int segmentIndex;
	
	private int atomIndex;
	
	private int startIndex;
	
	private int endIndex;
	
	private boolean target;
	
	public HighlightEvent(int segmentIndex, int atomIndex, int startIndex, int endIndex, boolean target) {
		this.segmentIndex = segmentIndex;
		this.atomIndex = atomIndex;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.target = target;
	}

	public int getSegmentIdx(){
		return segmentIndex;
	}
	
	public int getAtomIndex(){
		return atomIndex;
	}
	
	public int getStartIndex(){
		return startIndex;
	}
	
	public int getEndIndex(){
		return endIndex;
	}
	
	public boolean isTarget(){
		return target;
	}
}
