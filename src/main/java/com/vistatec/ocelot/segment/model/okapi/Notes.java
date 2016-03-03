package com.vistatec.ocelot.segment.model.okapi;

import java.util.ArrayList;
import java.util.Iterator;

public class Notes extends ArrayList<Note> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8694657386087109957L;

	public Note getOcelotNote() {

		Note ocelotNote = null;
		Iterator<Note> noteIt = iterator();
		Note currNote = null;
		while (noteIt.hasNext() && ocelotNote == null) {
			currNote = noteIt.next();
			if (currNote.isOcelotNote()) {
				ocelotNote = currNote;
			}
		}
		return ocelotNote;
	}
	
//	public void addNote(String content, String idSuffix){
//		
//		add(new Note(Note.OCELOT_ID_PREFIX + idSuffix, content));
//	}
	
	public boolean editNote(String content, String idSuffix) {
		
		boolean edited = false;
		Note note = getOcelotNote();
		if(content != null && !content.isEmpty()){
			if(note != null ){
				if( !note.getContent().equals(content)){
					note.setContent(content);
					edited = true;
				}
			} else {
				add(new Note(Note.OCELOT_ID_PREFIX + idSuffix, content));
				edited = true;
			}
		} else if( note != null){
			remove(note);
			edited = true;
		}
		return edited;
	}
}
