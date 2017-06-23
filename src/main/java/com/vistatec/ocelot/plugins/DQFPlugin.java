package com.vistatec.ocelot.plugins;

import java.awt.Window;

import javax.swing.JMenu;

public interface DQFPlugin extends Plugin {

	public JMenu getDqfMenu(Window parentWindow);
}
