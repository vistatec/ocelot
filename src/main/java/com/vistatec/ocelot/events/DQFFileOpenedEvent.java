package com.vistatec.ocelot.events;

import java.io.File;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class DQFFileOpenedEvent implements OcelotEvent {

	private File file;
	
	public DQFFileOpenedEvent(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
}
