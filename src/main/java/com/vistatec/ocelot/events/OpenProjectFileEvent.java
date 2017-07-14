package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.project.ProjectFile;

public class OpenProjectFileEvent implements OcelotEvent {

	private ProjectFile projectFile;

	public OpenProjectFileEvent(ProjectFile projectFile) {
		this.projectFile = projectFile;
	}

	public ProjectFile getProjectFile() {
		return projectFile;
	}
}
