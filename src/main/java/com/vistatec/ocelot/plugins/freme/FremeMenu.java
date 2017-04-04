package com.vistatec.ocelot.plugins.freme;

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

	public static final int CONFIG_MENU = 0;
	
	public static final int FILTER_MENU = 1;
	
	public static final int ENRICH_MENU = 2;
	
	public static final int SAVE_TRANS_MENU = 3;
	
	/** The entity service menu item. */
	private FremeEServiceMenuItem mnuEEntityService;

	/** The link service menu item. */
	private FremeEServiceMenuItem mnuELinkService;
	//
	/** The terminology service menu item. */
	private FremeEServiceMenuItem mnuETerminologyService;

	/** The link service menu item. */
	private FremeEServiceMenuItem mnuETranslationService;
	
//	/** The Entity Categories Filter menu item. */
//	private FremeMenuItem mnuViewCatFilter;

	/** The configure pipeline menu item. */
	private FremeMenuItem mnuConfigPipeline;
	
	/** The Enrich menu item. */
	private FremeMenuItem mnuEnrich;

	private FremeMenuItem mnuSaveTrans;
	
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

//		mnuViewCatFilter = new FremeMenuItem("Entity Categories Filter", FILTER_MENU);
//		mnuViewCatFilter.addActionListener(actionListener);
//		add(mnuViewCatFilter);
		
		mnuEnrich = new FremeMenuItem("Enrich Segments", ENRICH_MENU);
		mnuEnrich.addActionListener(actionListener);
		add(mnuEnrich);
		
		mnuConfigPipeline = new FremeMenuItem("Freme Pipeline Configuration", CONFIG_MENU);
		mnuConfigPipeline.addActionListener(actionListener);
		add(mnuConfigPipeline);
		
		mnuSaveTrans = new FremeMenuItem("Replace Targets", SAVE_TRANS_MENU);
		mnuSaveTrans.addActionListener(actionListener);
		add(mnuSaveTrans);
	}
	
	public void setEnrichMenuEnabled(boolean enabled){
		
		mnuEnrich.setEnabled(enabled);
	}

}

class FremeMenuItem extends JMenuItem {

	private static final long serialVersionUID = 2853253514750283163L;
	
	private int mnuType;
	
	public FremeMenuItem(String text, int mnuType) {
		super(text);
		this.mnuType = mnuType;
	}
	
	public int getMenuType(){
		return mnuType;
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
