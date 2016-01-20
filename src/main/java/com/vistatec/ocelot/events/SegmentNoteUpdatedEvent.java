package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class SegmentNoteUpdatedEvent extends SegmentEvent{

//	private final Note note;
	
	private final String noteContent;

	public SegmentNoteUpdatedEvent(XLIFFDocument xliff, OcelotSegment segment, String noteContent) {
	    super(xliff, segment);
//	    this.note = note;
	    this.noteContent = noteContent;
    }
//
//	public Note getNote(){
//		return note;
//		
//	}
	
	public String getNoteContent(){
		return noteContent;
	}
}
