package com.vistatec.ocelot.plugins;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class FremeMenu extends JMenu {

	/**
     * 
     */
	private static final long serialVersionUID = -5852190101662320008L;

	private FremeEServiceMenuItem mnuEEntityService;

	private FremeEServiceMenuItem mnuELinkService;
	//
	private FremeEServiceMenuItem mnuETerminologyService;

	private FremeEServiceMenuItem mnuETranslationService;
	
	private JMenuItem mnuConfigPipeline;

	public FremeMenu(final ItemListener itemListener, final ActionListener actionListener) {

		super("Freme e-Services");
		init(itemListener, actionListener);

	}

	private void init(final ItemListener listener, ActionListener actionListener) {

		mnuEEntityService = new FremeEServiceMenuItem("e-Entity",
		        FremePlugin.EENTITY_SERVICE);
		mnuEEntityService.setSelected(true);
		mnuEEntityService.addItemListener(listener);
		add(mnuEEntityService);

		mnuELinkService = new FremeEServiceMenuItem("e-Link",
		        FremePlugin.ELINK_SERVICE);
		mnuELinkService.setSelected(true);
		mnuELinkService.addItemListener(listener);
		add(mnuELinkService);

		mnuETerminologyService = new FremeEServiceMenuItem("e-Terminology",
		        FremePlugin.ETERMINOLOGY);
		mnuETerminologyService.setSelected(true);
		mnuETerminologyService.addItemListener(listener);
		add(mnuETerminologyService);

		 mnuETranslationService = new
		 FremeEServiceMenuItem("e-Translation", FremePlugin.ETRANSLATION);
		 mnuETranslationService.setSelected(true);
		 mnuETranslationService.addItemListener(listener);
		 add(mnuETranslationService);
		 
		 mnuConfigPipeline = new JMenuItem("Freme Pipeline Configuration");
		 mnuConfigPipeline.addActionListener(actionListener);
		 add(mnuConfigPipeline);
	}

}

class FremeEServiceMenuItem extends JCheckBoxMenuItem {

	/**
   * 
   */
	private static final long serialVersionUID = 7464098022306114511L;

	private int serviceType;

	public FremeEServiceMenuItem(final String text, final int serviceType) {

		super(text);
		this.serviceType = serviceType;
	}

	public int getServiceType() {
		return serviceType;
	}
}
