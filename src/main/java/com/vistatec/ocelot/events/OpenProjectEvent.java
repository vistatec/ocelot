package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.project.OcelotProject;

public class OpenProjectEvent implements OcelotEvent {

	private OcelotProject project;

	public OpenProjectEvent(OcelotProject project) {
		this.project = project;
	}

	public OcelotProject getProject() {
		return project;
	}
}
