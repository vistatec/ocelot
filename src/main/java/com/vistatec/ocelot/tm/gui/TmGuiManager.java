package com.vistatec.ocelot.tm.gui;

import java.awt.Component;
import java.awt.Window;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.events.ConcordanceSearchEvent;
import com.vistatec.ocelot.events.ConfigTmRequestEvent;
import com.vistatec.ocelot.events.RefreshSegmentView;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.tm.TmManager;
import com.vistatec.ocelot.tm.TmService;
import com.vistatec.ocelot.tm.gui.configuration.TmGuiConfigController;
import com.vistatec.ocelot.tm.gui.match.TmGuiMatchController;

/**
 * This class manages the user interfaces concerned with both TM configuration
 * and TM functionalities.
 */
public class TmGuiManager implements OcelotEventQueueListener {

	/** Object managing all GUI processes pertaining the TM configuration. */
	private TmGuiConfigController configController;

	/**
	 * Object managing all GUI processes pertaining TM and Concordance Search
	 * functionalities.
	 */
	private TmGuiMatchController matchController;
	
	

	/**
	 * Constructor.
	 * 
	 * @param tmManager
	 *            the TM manager
	 * @param tmService
	 *            the TM service
	 * @param eventQueue
	 *            the ocelot event queue.
	 * @param cfgService
	 *            the configuration service            
	 */
	public TmGuiManager(final TmManager tmManager, final TmService tmService,
			final OcelotEventQueue eventQueue, final JsonConfigService cfgService) {

		configController = new TmGuiConfigController(tmManager, cfgService);
		matchController = new TmGuiMatchController(tmService, eventQueue);
	}

	/**
	 * Gets the Concordance Search panel to be displayed in Ocelot main frame.
	 * 
	 * @return the Concordance Search panel
	 */
	public Component getConcordancePanel() {
		return matchController.getConcordancePanel();
	}

	/**
	 * Gets the TM translations panel to be displayed in Ocelot main frame.
	 * 
	 * @return the TM translations panel
	 */
	public Component getTmPanel() {
		return matchController.getTmPanel();
	}

	/**
	 * Saves all segments displayed in Ocelot main grid in a tmx file.
	 * 
	 * @param currentWindow
	 *            current opened window
	 */
	public void saveAsTmx(final Window currentWindow) {

		configController.saveAsTmx(currentWindow);
	}

	/**
	 * Handles the event the user requests to open the TM Configuration dialog.
	 * 
	 * @param e
	 *            the event to handle
	 */
	@Subscribe
	public void handleConfigTmRequest(ConfigTmRequestEvent e) {
		configController.openTmConfigDialog(e.getCurrentWindow());
	}

	/**
	 * Handles the event the user requests to perform a concordance search.
	 * 
	 * @param e
	 *            the event to handle.
	 */
	@Subscribe
	public void handleConcordanceSearchEvent(ConcordanceSearchEvent e) {

		matchController.performConcordanceSearch(e.getText());
	}

	/**
	 * Handles the event the segment selection in Ocelot main grid has changed.
	 * 
	 * @param e
	 *            the event to handle.
	 */
	@Subscribe
	public void handleSegmentSelected(SegmentSelectionEvent e) {
		try{
		matchController.setSelectedSegment(e.getSegment());
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Subscribe
    public void updateSegmentView(RefreshSegmentView event){
		matchController.update(event.getSegmentNumber());
	}
}
