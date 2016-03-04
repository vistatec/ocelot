package com.vistatec.ocelot.events;

import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.findrep.FindResult;

public class HighlightEvent implements OcelotEvent {

	private List<FindResult> highlightDataList;
	
	private int currResultIndex;
	
	public HighlightEvent(List<FindResult> highlightDataList, int currResultIndex) {
		
		this.highlightDataList	= highlightDataList;
		this.currResultIndex = currResultIndex;
	}
	
	public HighlightEvent(FindResult highlightData) {
		
		highlightDataList = new ArrayList<FindResult>();
		highlightDataList.add(highlightData);
		
	}
	
	public List<FindResult> getHighlightDataList(){
		return highlightDataList;
	}
	
	public int getCurrResultIndex(){
		
		return currResultIndex; 
	}
	
}
