package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

public class LQIConfigurationSelectionChangedEvent implements OcelotEvent {

	private LQIGridConfiguration newSelectedConfiguration;
	
	private LQIGridConfiguration oldSelectedConfiguration;

	public LQIConfigurationSelectionChangedEvent(LQIGridConfiguration newSelectedConfiguration, LQIGridConfiguration oldSelectedConfiguration) {

		this.newSelectedConfiguration = newSelectedConfiguration;
		this.oldSelectedConfiguration = oldSelectedConfiguration;
	}

	public LQIGridConfiguration getNewSelectedConfiguration() {
		return newSelectedConfiguration;
	}

	public LQIGridConfiguration getOldSelectedConfiguration(){
		return oldSelectedConfiguration;
	}
}
