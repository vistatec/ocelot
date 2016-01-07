package com.vistatec.ocelot.lqi;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vistatec.ocelot.PlatformSupport;
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
	
	private LQIGridDialog gridDialog;

	private PlatformSupport platformSupport;

	public LQIGridController(final OcelotConfigService configService,
	        final OcelotEventQueue eventQueue, PlatformSupport platformSupport) {

		this(configService, eventQueue, platformSupport, null);
	}

	public LQIGridController(final OcelotConfigService configService,
	        final OcelotEventQueue eventQueue, PlatformSupport platformSupport,
	        final JFrame ocelotMainFrame) {

		this.configService = configService;
		this.eventQueue = eventQueue;
		this.ocelotMainFrame = ocelotMainFrame;
		this.platformSupport = platformSupport;
	}

	public void setOcelotMainFrame(JFrame ocelotMainFrame) {
		this.ocelotMainFrame = ocelotMainFrame;
	}

	public void displayLQIGrid() {

		gridDialog = new LQIGridDialog(ocelotMainFrame, this,
		        readLQIGridConfiguration(), platformSupport);
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

	/**
	 * Creates a new LQI issue.
	 * 
	 * @param category
	 *            the LQI category
	 * @param severity
	 *            the issue severity.
	 */
	public void createNewLqi(String category, double severity) {

		
		if (gridDialog == null || gridDialog.canCreateIssue()) {
			String comment = null;
			if(gridDialog != null){
				comment = gridDialog.getCommentForCategory(category);
			}
			LanguageQualityIssue lqi = new LanguageQualityIssue();
			lqi.setSeverity(severity);
			lqi.setType(category);
			lqi.setComment(comment);
			System.out.println("-------- Create LQI " + lqi.toString() + " - "
			        + lqi.getSeverity() + " - " + comment );
			if (selectedSegment != null) {
				eventQueue.post(new LQIAdditionEvent(lqi, selectedSegment));
			}
			if(comment != null){
				gridDialog.clearCommentCellForCategory(category);
			}
		} 
	}
	
	public void close() {
		
		gridDialog.dispose();
		gridDialog = null;
    }
}
