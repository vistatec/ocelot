package com.vistatec.ocelot.events;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class SegmentNoteUpdatedEvent extends SegmentEvent{

//	private final Note note;
	
	private final String noteContent;
	private XLIFFDocument xliff;

	public SegmentNoteUpdatedEvent(XLIFFDocument xliff, OcelotSegment segment, String noteContent) {
	    super(segment);
//	    this.note = note;
	    this.noteContent = noteContent;
	    this.xliff = xliff;
    }
//
//	public Note getNote(){
//		return note;
//		
//	}
	
	public String getNoteContent(){
		return noteContent;
	}

    public XLIFFDocument getDocument() {
        return xliff;
    }
}
