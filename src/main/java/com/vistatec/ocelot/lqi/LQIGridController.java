package com.vistatec.ocelot.lqi;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.PlatformSupport;
import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.config.LqiConfigService;
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

/**
 * Controller class for the LQI grid.
 */
public class LQIGridController implements OcelotEventQueueListener {

	/** The logger for this class. */
	private final Logger logger = LoggerFactory.getLogger(LQIGridController.class);

	/** The LQI configuration service. */
	private LqiConfigService configService;

	/** The event queue. */
	private OcelotEventQueue eventQueue;

	/** The Ocelot main frame. */
	private JFrame ocelotMainFrame;

	/** The selected segment. */
	private OcelotSegment selectedSegment;

	/** States if some Ocelot text field is being edited. */
	private boolean ocelotEditing;
        
        /** The LQI grid dialog. */
	private LQIGridDialog gridDialog;
	
	private PlatformSupport platformSupport;

	/**
	 * Controller.
	 * 
	 * @param configService
	 *            the LQI configuration service.
	 * @param eventQueue
	 *            the event queue.
	 */
	public LQIGridController(final LqiConfigService configService,
	        final OcelotEventQueue eventQueue, PlatformSupport platformSupport) {

		this(configService, eventQueue, platformSupport, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param configService
	 *            the LQI configuration service.
	 * @param eventQueue
	 *            the event queue.
	 * @param ocelotMainFrame
	 *            the Ocelot main frame.
	 */
	public LQIGridController(final LqiConfigService configService,
	        final OcelotEventQueue eventQueue, PlatformSupport platformSupport, final JFrame ocelotMainFrame) {

		this.configService = configService;
		this.eventQueue = eventQueue;
		this.ocelotMainFrame = ocelotMainFrame;
		this.platformSupport = platformSupport;
	}

	/**
	 * Sets the Ocelot main frame.
	 * 
	 * @param ocelotMainFrame
	 *            the Ocelot main frame.
	 */
	public void setOcelotMainFrame(JFrame ocelotMainFrame) {
		this.ocelotMainFrame = ocelotMainFrame;
	}

	/**
	 * Displays the LQI grid.
	 */
	public void displayLQIGrid() {
		logger.debug("Displaying the LQI Grid.");
		gridDialog = new LQIGridDialog(ocelotMainFrame, this,
		        readLQIGridConfiguration(), platformSupport);
		SwingUtilities.invokeLater(gridDialog);
	}

	/**
	 * Saves the LQI grid to the configuration file.
	 * 
	 * @param lqiGrid
	 *            the LQI grid.
	 * @throws TransferException
	 *             the transfer exception.
	 */
	public void saveLQIGridConfiguration(LQIGrid lqiGrid)
	        throws TransferException {

		logger.debug("Saving the LQI grid configuration...");
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
	

	/**
	 * Reads the LQI grid from the configuration file.
	 * 
	 * @return the LQI grid.
	 */
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

	/**
	 * Handles the event a segment has been selected.
	 * 
	 * @param event
	 *            the segment selection event.
	 */
	@Subscribe
	public void handleSegmentSelection(SegmentSelectionEvent event) {
		try {
			selectedSegment = event.getSegment();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Handles the event a user enters/exits the editing mode in Ocelot.
	 * 
	 * @param event
	 *            the Ocelot editing event.
	 */
	@Subscribe
	public void handleOcelotEditingEvent(OcelotEditingEvent event) {

		ocelotEditing = event.getEventType() == OcelotEditingEvent.Type.START_EDITING;
	}

	/**
	 * Checks if some text field is being edited in Ocelot.
	 * 
	 * @return <code>true</code> if a text field exist being in editing mode;
	 *         <code>false</code> otherwise.
	 */
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
	 * @param severityName
	 *            the severity name.
	 */
	public void createNewLqi(String category, double severity,
	        String severityName) {

		logger.debug("Creating a new Language Quality Issue: {} - {} - {}", category, severityName, severity);
		if (gridDialog == null || gridDialog.canCreateIssue()) {
			String comment = null;
			if(gridDialog != null){
				comment = gridDialog.getCommentForCategory(category);
			}
			LanguageQualityIssue lqi = new LanguageQualityIssue();
			lqi.setSeverity(severity);
			lqi.setType(category);
			lqi.setSeverityName(severityName);
			lqi.setComment(comment);
			if (selectedSegment != null) {
				eventQueue.post(new LQIAdditionEvent(lqi, selectedSegment));
			}
			if(comment != null){
				gridDialog.clearCommentCellForCategory(category);
			}
		} 
	}

	/**
	 * Closes the LQI grid.
	 */
	public void close() {
		
		gridDialog.dispose();
		gridDialog = null;
    }
	
	public LqiConfigService getConfigService(){
		return configService;
	}
}