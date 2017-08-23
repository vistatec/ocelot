package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;

/**
 * Signals that a segment has been edited by the user.
 */
public class SegmentEditEvent extends SegmentEvent {

	public static final int TARGET_CHANGED = 1;

	public static final int TARGET_RESET = 2;

	public static final int LQI_ADDED = 3;

	public static final int LQI_EDITED = 4;

	public static final int LQI_DELETED = 5;

	private int editType;

	public SegmentEditEvent(XLIFFDocument xliff, OcelotSegment segment, int editType) {
		super(xliff, segment);
		this.editType = editType;
	}

	public int getEditType() {
		return editType;
	}
}
