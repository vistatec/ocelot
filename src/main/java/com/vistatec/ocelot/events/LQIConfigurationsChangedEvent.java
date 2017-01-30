package com.vistatec.ocelot.events;

import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

public class LQIConfigurationsChangedEvent implements OcelotEvent {

	private LQIGridConfigurations lqiGridSavedConfigurations;

	private LQIGridConfiguration oldActiveConf;

	public LQIConfigurationsChangedEvent(LQIGridConfigurations lqiGridSavedConfigurations,
	        LQIGridConfiguration oldActiveConf) {

		this.lqiGridSavedConfigurations = lqiGridSavedConfigurations;
		this.oldActiveConf = oldActiveConf;
	}

	public LQIGridConfigurations getLqiGridSavedConfigurations() {
		return lqiGridSavedConfigurations;
	}

	public boolean isActiveConfChanged() {
		return oldActiveConf != null;
	}
	
	public LQIGridConfiguration getOldActiveConfiguration(){
		return oldActiveConf;
	}

}
