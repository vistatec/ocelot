package com.vistatec.ocelot.lqi;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.config.OcelotConfigService;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.OcelotEditingEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.lqi.constants.LQIConstants;
import com.vistatec.ocelot.lqi.gui.LQIGridDialog;
import com.vistatec.ocelot.lqi.model.LQIGrid;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public class LQIGridController implements OcelotEventQueueListener {

	private final Logger logger = Logger.getLogger(LQIGridController.class);

	private OcelotConfigService configService;

	private OcelotEventQueue eventQueue;

	private JFrame ocelotMainFrame;

	private OcelotSegment selectedSegment;

	private boolean ocelotEditing;

	public LQIGridController(final OcelotConfigService configService,
	        final OcelotEventQueue eventQueue) {

		this(configService, eventQueue, null);
	}

	public LQIGridController(final OcelotConfigService configService,
	        final OcelotEventQueue eventQueue, final JFrame ocelotMainFrame) {

		this.configService = configService;
		this.eventQueue = eventQueue;
		this.ocelotMainFrame = ocelotMainFrame;
	}

	public void setOcelotMainFrame(JFrame ocelotMainFrame) {
		this.ocelotMainFrame = ocelotMainFrame;
	}

	public void displayLQIGrid() {

		LQIGridDialog gridDialog = new LQIGridDialog(ocelotMainFrame, this,
		        readLQIGridConfiguration());
		SwingUtilities.invokeLater(gridDialog);
	}

	public void saveLQIGridConfiguration(LQIGrid lqiGrid)
	        throws TransferException {

		try {
			configService.saveLQIConfig(lqiGrid);
		} catch (TransferException e) {
			logger.error("Error while saving the LQI grid configuration.", e);
			JOptionPane
			        .showMessageDialog(
			                ocelotMainFrame,
			                "An error has occurred while saving the LQI grid configuration",
			                "LQI Grid Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public LQIGrid readLQIGridConfiguration() {

		LQIGrid lqiGrid = null;
		try {
			lqiGrid = configService.readLQIConfig();
			if (lqiGrid.isEmpty()) {
				lqiGrid = LQIConstants.getDefaultLQIGrid();
			}
		} catch (TransferException e) {
			logger.error("Error while reading the LQI grid configuration", e);
			JOptionPane
			        .showMessageDialog(
			                ocelotMainFrame,
			                "An error has occurred while reading the LQI grid configuration",
			                "LQI Grid Error", JOptionPane.ERROR_MESSAGE);
		}
		return lqiGrid;
	}

	@Subscribe
	public void handleSegmentSelection(SegmentSelectionEvent event) {
		try {
			selectedSegment = event.getSegment();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Subscribe
	public void handleOcelotEditingEvent(OcelotEditingEvent event) {

		ocelotEditing = event.getEventType() == OcelotEditingEvent.START_EDITING;
	}

	public boolean isOcelotEditing() {
		return ocelotEditing;
	}

	public void createNewLQI(LanguageQualityIssue lqi) {

		if (selectedSegment != null) {
			eventQueue.post(new LQIAdditionEvent(lqi, selectedSegment));
		}
	}
}
