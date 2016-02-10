package com.vistatec.ocelot.events;

import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.findrep.FindReplaceResult;

public class ReplaceEvent implements OcelotEvent {

	private String newString;
	
	private List<FindReplaceResult> results;

	public ReplaceEvent(String newString, List<FindReplaceResult> results) {
		this.newString = newString;
		this.results = results;
	}
	
	public ReplaceEvent(String newString, FindReplaceResult result) {
		this.newString = newString;
		this.results = new ArrayList<FindReplaceResult>();
		results.add(result);
	}

	public String getNewString() {
		return newString;
	}
	
	public List<FindReplaceResult> getFindResults(){
		return results;
	}
}
