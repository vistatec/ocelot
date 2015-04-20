package com.vistatec.ocelot.tm.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.tm.gui.constants.TmIconsConst;

/**
 * Detachable panel displayed on top of Ocelot frame.
 */
public abstract class AbstractDetachableTmPanel implements DetachableComponent,
		ActionListener {

	/** The TM panel. */
	protected Component panel;

	/** The window where the panel is displayed when in detached state. */
	protected Window window;

	/** The original panel container. In case of TM panel, it is a Split Pane. */
	protected JSplitPane panelContainer;

	/** The pin button. This is the button firing the attach/detach events. */
	private JButton btnPin;

	/**
	 * Manages the pin button pressed event.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnPin)) {
			attachDetachEvent();
		}

	}

	/**
	 * Manages the attach/detach event. If the window is currently
	 * <code>null</code>, then it is a "detach" request; otherwise it is an
	 * "attach" event.
	 */
	private void attachDetachEvent() {

		if (window == null) {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					detach();

				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					attach();
				}
			});
		}

	}

	/**
	 * Gets the pin component.
	 * 
	 * @return the pin component, firing the attach/detach events.
	 */
	protected Component getPinComponent() {

		if (btnPin == null) {
			Toolkit kit = Toolkit.getDefaultToolkit();
			ImageIcon icon = new ImageIcon(kit.createImage(Ocelot.class
					.getResource(TmIconsConst.PIN_ICO)));
			btnPin = new JButton(icon);
			btnPin.addActionListener(this);
			btnPin.setToolTipText("Detach");
			final Dimension dim = new Dimension(16, 16);
			btnPin.setPreferredSize(dim);
			btnPin.setMaximumSize(dim);
			btnPin.setMinimumSize(dim);
			btnPin.setOpaque(false);
			btnPin.setBorder(null);
			btnPin.setBackground(SystemColor.control);
		}
		return btnPin;
	}

	/**
	 * Detaches the component: creates a window and displays the component
	 * inside the new window. The original container does no longer contain the
	 * component.
	 */
	private void detach() {

		window = getDetachedComponent();
		window.setVisible(true);
		panelContainer.repaint();
	}

	/**
	 * Attaches the component: the component window is closed. The component is
	 * added to the original component.
	 */
	private void attach() {

		panelContainer.add(panel, JSplitPane.LEFT);
		window.setVisible(false);
		window.dispose();
		window = null;
		panelContainer.setDividerLocation(0.3);
		panelContainer.repaint();
	}

	/**
	 * Builds the panel being the attachable/detachable component.
	 */
	protected abstract void makePanel();

	/**
	 * Builds the window displaying the detached component.
	 */
	protected abstract void makeWindow();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.tm.gui.DetachableComponent#getAttachedComponent()
	 */
	@Override
	public Component getAttachedComponent() {

		if (panel == null) {
			makePanel();
		}
		return panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.tm.gui.DetachableComponent#getDetachedComponent()
	 */
	@Override
	public Window getDetachedComponent() {
		if (window == null) {
			makeWindow();
			window.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {

					attach();
				}

			});
			Window mainFrame = SwingUtilities.getWindowAncestor(panel);
			window.setIconImages(mainFrame.getIconImages());
			panelContainer = (JSplitPane) panel.getParent();
			window.add(panel);
			window.setLocationRelativeTo(mainFrame);

			window.pack();
		}
		return window;
	}

}
