package com.vistatec.ocelot.plugins;

import java.awt.Window;

import javax.swing.JMenu;

import com.vistatec.ocelot.config.LqiJsonConfigService;

public interface DQFPlugin extends Plugin {

	public JMenu getDqfMenu(Window parentWindow);
	
	public void setLQIConfigService(LqiJsonConfigService lqiConfigService);
}
