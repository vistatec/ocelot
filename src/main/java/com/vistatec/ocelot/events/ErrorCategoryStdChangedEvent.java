package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class ErrorCategoryStdChangedEvent implements OcelotEvent {

	private int errorCategoryStd;
	
	public ErrorCategoryStdChangedEvent(int errorCategoryStd) {

		this.errorCategoryStd = errorCategoryStd;
	}
	
	public int getErrorCategoryStd(){
		return errorCategoryStd;
	}
	
	
}
