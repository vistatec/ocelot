package com.vistatec.ocelot.tm.gui;

import java.awt.Component;
import java.awt.Window;

/**
 * This interface provide methods for managing detachable Swing components. A
 * class defining a Swing component that can be detached by the owner window and
 * can be displayed inside its own window, should implement this interface.
 */
public interface DetachableComponent {

	/**
	 * Gets the attached Component.
	 * 
	 * @return the component.
	 */
	public Component getAttachedComponent();

	/**
	 * Gets a window displaying only the component.
	 * 
	 * @return a window displaying the component.
	 */
	public Window getDetachedComponent();
}
