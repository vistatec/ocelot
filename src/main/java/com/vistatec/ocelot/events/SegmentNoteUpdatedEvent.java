package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;

public class SegmentNoteUpdatedEvent extends SegmentEvent{

//	private final Note note;
	
	private final String noteContent;
	
	public SegmentNoteUpdatedEvent(OcelotSegment segment, String noteContent) {
	    super(segment);
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
