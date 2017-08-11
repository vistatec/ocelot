package com.vistatec.ocelot.events;

import java.awt.Component;

import javax.swing.ImageIcon;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

public class DQFProjectOpenedEvent implements OcelotEvent {

	private String projectName;

	private Component projectGuiComponent;

	private LQIGridConfiguration projLqiGridConfiguration;

	public DQFProjectOpenedEvent(String projectName, Component projectGuiComponent,
			LQIGridConfiguration projLqiGridConfiguration) {

		this.projectName = projectName;
		this.projectGuiComponent = projectGuiComponent;
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
	
}
