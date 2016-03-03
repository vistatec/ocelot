package com.vistatec.ocelot.events;

import java.awt.Component;

import com.vistatec.ocelot.events.api.OcelotEvent;

public class DisplayLeftComponentEvent implements OcelotEvent {

	private Component component;

	public DisplayLeftComponentEvent(Component component) {

		this.component = component;
	}

	public Component getComponent() {
		return component;
	}
}
