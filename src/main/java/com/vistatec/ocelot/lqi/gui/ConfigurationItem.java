package com.vistatec.ocelot.lqi.gui;

import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

public class ConfigurationItem {

	private LQIGridConfiguration configuration;

	public ConfigurationItem(LQIGridConfiguration configuration) {

		this.configuration = configuration;
	}

	public LQIGridConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public String toString() {
		return configuration.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof ConfigurationItem){
			ConfigurationItem confItem = (ConfigurationItem) obj;
			return configuration.equals(confItem.getConfiguration());
		} else {
			return super.equals(obj);
		}
	}
}
