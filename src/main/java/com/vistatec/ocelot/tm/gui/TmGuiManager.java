package com.vistatec.ocelot.tm.gui;

import java.awt.Component;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.ConcordanceSearchEvent;
import com.vistatec.ocelot.events.ConfigTmRequestEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.tm.TmManager;
import com.vistatec.ocelot.tm.TmService;
import com.vistatec.ocelot.tm.gui.configuration.TmGuiConfigController;
import com.vistatec.ocelot.tm.gui.match.TmGuiMatchController;

public class TmGuiManager implements OcelotEventQueueListener{

	private TmGuiConfigController configController;
	
	private TmGuiMatchController matchController;
	
	public TmGuiManager(final TmManager tmManager, final TmService tmService, final OcelotEventQueue eventQueue) {
		
		configController = new TmGuiConfigController(tmManager);
		matchController = new TmGuiMatchController(tmService, eventQueue);
	}

	public Component getConcordancePanel() {
		return matchController.getConcordancePanel();
	}
	
	public Component getTmPanel(){
		return matchController.getTmPanel();
	}
	
	@Subscribe
	public void handleConfigTmRequest(ConfigTmRequestEvent e){
		configController.openTmConfigDialog(e.getCurrentWindow());
	}
	
	@Subscribe
	public void handleConcordanceSearchEvent(ConcordanceSearchEvent e) {
		
		System.out.println("ConcordanceSearchEvent event received. Text = " + e.getText());
		matchController.performConcordanceSearch(e.getText());
	}
	
	@Subscribe
	public void handleSegmentSelected(SegmentSelectionEvent e){
		matchController.setSelectedSegment(e.getSegment());
	}
}
