package com.vistatec.ocelot.plugins;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * FREME menu displayed in the Ocelot menu bar only if the FREME pluign is
 * installed.
 */
public class FremeMenu extends JMenu {

	/** The serial version UID. */
	private static final long serialVersionUID = -5852190101662320008L;

	/** The entity service menu item. */
	private FremeEServiceMenuItem mnuEEntityService;

	/** The link service menu item. */
	private FremeEServiceMenuItem mnuELinkService;
	//
	/** The terminology service menu item. */
	private FremeEServiceMenuItem mnuETerminologyService;

	/** The link service menu item. */
	private FremeEServiceMenuItem mnuETranslationService;

	/** The configure pipeline menu item. */
	private JMenuItem mnuConfigPipeline;

	/**
	 * Constructor.
	 * 
	 * @param itemListener
	 *            the item listener.
	 * @param actionListener
	 *            the action listener.
	 */
	public FremeMenu(final ItemListener itemListener,
	        final ActionListener actionListener) {

		super("Freme e-Services");
		init(itemListener, actionListener);

	}

	/**
	 * Initializes the menu.
	 * 
	 * @param itemListener
	 *            the item listener
	 * @param actionListener
	 *            the action listener
	 */
	private void init(final ItemListener itemListener,
	        ActionListener actionListener) {

		mnuEEntityService = new FremeEServiceMenuItem("e-Entity",
		        FremePlugin.EENTITY_SERVICE);
		mnuEEntityService.setSelected(true);
		mnuEEntityService.addItemListener(itemListener);
		add(mnuEEntityService);

		mnuELinkService = new FremeEServiceMenuItem("e-Link",
		        FremePlugin.ELINK_SERVICE);
		mnuELinkService.setSelected(true);
		mnuELinkService.addItemListener(itemListener);
		add(mnuELinkService);

		mnuETerminologyService = new FremeEServiceMenuItem("e-Terminology",
		        FremePlugin.ETERMINOLOGY);
		mnuETerminologyService.setSelected(true);
		mnuETerminologyService.addItemListener(itemListener);
		add(mnuETerminologyService);

		mnuETranslationService = new FremeEServiceMenuItem("e-Translation",
		        FremePlugin.ETRANSLATION);
		mnuETranslationService.setSelected(true);
		mnuETranslationService.addItemListener(itemListener);
		add(mnuETranslationService);

		mnuConfigPipeline = new JMenuItem("Freme Pipeline Configuration");
		mnuConfigPipeline.addActionListener(actionListener);
		add(mnuConfigPipeline);
	}

}

/**
 * Check box menu item for FREME services.
 */
class FremeEServiceMenuItem extends JCheckBoxMenuItem {

	/** The serial version UID. */
	private static final long serialVersionUID = 7464098022306114511L;

	/** The service type. */
	private int serviceType;

	/**
	 * Constructor.
	 * 
	 * @param text
	 *            the text
	 * @param serviceType
	 *            the service type
	 */
	public FremeEServiceMenuItem(final String text, final int serviceType) {

		super(text);
		this.serviceType = serviceType;
	}

	/**
	 * Gets the service type.
	 * 
	 * @return the service type.
	 */
	public int getServiceType() {
		return serviceType;
	}
}
