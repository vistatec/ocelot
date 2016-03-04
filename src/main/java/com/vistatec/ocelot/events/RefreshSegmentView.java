package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class RefreshSegmentView implements OcelotEvent {
	
	private int segmentNumber;

	public RefreshSegmentView(final int segmentNumber) {

		this.segmentNumber = segmentNumber;
	}

	public int getSegmentNumber() {
		return segmentNumber;
	}

}
