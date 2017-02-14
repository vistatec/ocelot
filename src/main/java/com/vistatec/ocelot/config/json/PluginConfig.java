package com.vistatec.ocelot.config.json;

import com.vistatec.ocelot.plugins.Plugin;


public class PluginConfig {

	private String className;

	private boolean enabled;

	public PluginConfig() {
	 
    }
	
	public PluginConfig(Plugin plugin, boolean enabled ) {

		if(plugin != null){
			this.className = plugin.getClass().getName();
		}
		this.enabled = enabled;
    }
	
	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean matches(Plugin plugin ){
		
		return className.equals(plugin.getClass().getName());
	}
	
	public String toString() {

		return "class name: " + className + ", enabled: " + enabled;

	}
	

}
