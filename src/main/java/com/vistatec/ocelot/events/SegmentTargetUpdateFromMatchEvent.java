package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;

public class SegmentTargetUpdateFromMatchEvent extends SegmentTargetUpdateEvent {

	public SegmentTargetUpdateFromMatchEvent(OcelotSegment segment,
			SegmentVariant updatedTarget) {
		super(segment, updatedTarget);
	}

}
