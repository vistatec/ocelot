package com.vistatec.ocelot.plugins;

import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

public class FremeMenu extends JMenu  {

    /**
     * 
     */
    private static final long serialVersionUID = -5852190101662320008L;

    private FremeEServiceMenuItem mnuEEntityService;

     private FremeEServiceMenuItem mnuELinkService;
    //
    // private FremeEServiceMenuItem mnuETerminologyService;
    //
    // private FremeEServiceMenuItem mnuETranslationService;

    public FremeMenu(final ItemListener listener ) {

        super("Freme e-Services");
        init(listener);

    }

    private void init( final ItemListener listener) {

        mnuEEntityService = new FremeEServiceMenuItem("e-Entity",
                FremePlugin.EENTITY_SERVICE);
        mnuEEntityService.setSelected(true);
        mnuEEntityService.addItemListener(listener);
        add(mnuEEntityService);

        mnuELinkService = new FremeEServiceMenuItem("e-Link", FremePlugin.ELINK_SERVICE);
        mnuELinkService.setSelected(true);
        mnuELinkService.addItemListener(listener);
        add(mnuELinkService);
        // mnuELinkService = new
        // FremeEServiceMenuItem(FremePlugin.ELINK_SERVICE);
        // mnuELinkService.setSelected(true);
        // mnuELinkService.addItemListener(listener);
        // add(mnuELinkService);

        // mnuETerminologyService = new
        // FremeEServiceMenuItem(FremePlugin.ETERMINOLOGY);
        // mnuETerminologyService.setSelected(true);
        // mnuETerminologyService.addItemListener(listener);
        // add(mnuETerminologyService);

        // mnuETranslationService = new
        // FremeEServiceMenuItem(FremePlugin.ETRANSLATION);
        // mnuETranslationService.setSelected(true);
        // mnuETranslationService.addItemListener(listener);
        // add(mnuETranslationService);
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
