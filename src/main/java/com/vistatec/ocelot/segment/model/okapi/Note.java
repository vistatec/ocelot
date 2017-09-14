package com.vistatec.ocelot.segment.model.okapi;

public class Note {

	public static final String OCELOT_ID_PREFIX = "ocelot-";
	
	public static final String OCELOT_FROM_PROPERTY = "ocelot";
	
	private String id;
	
	private String content;

	public Note() {
    }
	
	public Note(String id, String content) {
		
		this.id = id;
		this.content = content;
    }
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public boolean isOcelotNote(){
		return id != null && id.startsWith(OCELOT_ID_PREFIX);
	}
	
}
