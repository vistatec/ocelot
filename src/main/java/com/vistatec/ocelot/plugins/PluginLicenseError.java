package com.vistatec.ocelot.plugins;

public class PluginLicenseError {

	private Plugin plugin;
	
	private String message;
	
	public PluginLicenseError(Plugin plugin, String message) {
		
		this.plugin = plugin;
		this.message = message;
	}
	
	public Plugin getPlugin(){
		return plugin;
	}
	
	public String getMessage(){
		return message;
	}
}
