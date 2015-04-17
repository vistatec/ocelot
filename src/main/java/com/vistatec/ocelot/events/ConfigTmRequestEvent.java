package com.vistatec.ocelot.events;

import java.awt.Window;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class ConfigTmRequestEvent implements OcelotEvent {

	private Window currentWindow;
	public ConfigTmRequestEvent(final Window  currentWindow) {
		
		this.currentWindow = currentWindow;
	}
	
	public Window getCurrentWindow(){
		return currentWindow;
	}
}
