package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class ReplaceEvent implements OcelotEvent {

	public static final int REPLACE = 0;
	
	public static final int REPLACE_ALL = 1;
	
    private String oldString;
	private String newString;
	
	private int action;
	
	private int segmentIndex;

    public ReplaceEvent(String oldString, String newString, int action) {
        this.oldString = oldString;
		this.newString = newString;
		this.action = action;
	}
	
    public ReplaceEvent(String oldString, String newString, int segmentIndex, int action) {
        this.oldString = oldString;
		this.newString = newString;
		this.segmentIndex = segmentIndex;
		this.action = action;
	}
	
    public String getOldString() {
        return oldString;
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
