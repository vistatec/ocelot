package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class ProfileChangedEvent implements OcelotEvent {

	private String profile;
	
	public ProfileChangedEvent(String profile) {
		
		this.profile = profile;
    }
	
	public String getProfile(){
		return profile;
	}
}
