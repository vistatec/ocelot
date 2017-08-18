package com.vistatec.ocelot.events;

import java.awt.Component;

import javax.swing.ImageIcon;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

public class DQFProjectOpenedEvent implements OcelotEvent {

	private String projectName;

	private Component projectGuiComponent;

	private LQIGridConfiguration projLqiGridConfiguration;
	
	private boolean isTask;

	public DQFProjectOpenedEvent(String projectName, Component projectGuiComponent, boolean isTask,
			LQIGridConfiguration projLqiGridConfiguration) {

		this.projectName = projectName;
		this.projectGuiComponent = projectGuiComponent;
		this.isTask = isTask;
		this.projLqiGridConfiguration = projLqiGridConfiguration;
	}

	public String getProjectName() {
		return projectName;
	}

	public Component getProjectGuiComponent() {
		return projectGuiComponent;
	}

	public LQIGridConfiguration getProjLqiGridConfiguration() {
		return projLqiGridConfiguration;
	}
	
	public boolean getIsTask(){
		return isTask;
	}
	
}
