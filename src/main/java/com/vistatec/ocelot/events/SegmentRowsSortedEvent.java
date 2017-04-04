package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class SegmentRowsSortedEvent implements OcelotEvent {
	
	private int[] sortedIndexMap;
	
	public SegmentRowsSortedEvent(int[] sortedIndexMap) {
		
		this.sortedIndexMap = sortedIndexMap;
	}
	
	public int[] getSortedIndexMap(){
		return sortedIndexMap;
	}

}
