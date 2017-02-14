package com.vistatec.ocelot.lqi;

import java.awt.Window;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.PlatformSupport;
import com.vistatec.ocelot.config.LqiJsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIConfigurationSelectionChangedEvent;
import com.vistatec.ocelot.events.LQIConfigurationsChangedEvent;
import com.vistatec.ocelot.events.OcelotEditingEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.lqi.constants.LQIConstants;
import com.vistatec.ocelot.lqi.gui.window.LQIConfigurationsDialog;
import com.vistatec.ocelot.lqi.gui.window.LQIGridDialogView;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.segment.model.OcelotSegment;

/**
 * Controller class for the LQI grid.
 */
public class LQIGridController implements OcelotEventQueueListener {

	/** The logger for this class. */
	private final Logger logger = LoggerFactory
	        .getLogger(LQIGridController.class);

	/** The LQI configuration service. */
	private LqiJsonConfigService configService;

	/** The event queue. */
	private OcelotEventQueue eventQueue;

	/** The selected segment. */
	private OcelotSegment selectedSegment;

	/** States if some Ocelot text field is being edited. */
	private boolean ocelotEditing;

	/** The LQI grid dialog. */
	private LQIGridDialogView gridDialog;
	
	private LQIConfigurationsDialog configsDialog;
	
	private PlatformSupport platformSupport;

	private LQIGridConfigurations lqiGridConfigurations;
	
	private boolean canManageConf;
	
	/**
	 * Controller.
	 * 
	 * @param configService
	 *            the LQI configuration service.
	 * @param eventQueue
	 *            the event queue.
	 */
	public LQIGridController(final LqiJsonConfigService configService,
	        final OcelotEventQueue eventQueue, PlatformSupport platformSupport, boolean canManageConf) {

		this.configService = configService;
		this.eventQueue = eventQueue;
		this.platformSupport = platformSupport;
		this.canManageConf = canManageConf;
	}


	/**
	 * Displays the LQI grid.
	 */
	public void displayLQIGrid(Window owner) {
		logger.debug("Displaying the LQI Grid.");
		if (!isDialogOpened(gridDialog)) {
			gridDialog = new LQIGridDialogView(owner, this,
			        readLQIGridConfiguration(owner), canManageConf);
			SwingUtilities.invokeLater(gridDialog);
		} else {
			// if the LQI grid is already opened, just give it the focus
			gridDialog.requestFocus();
		}
	}

	/**
	 * Saves the LQI grid to the configuration file.
	 * 
	 * @param lqiGrid
	 *            the LQI grid.
	 * @throws TransferException
	 *             the transfer exception.
	 */
	public void saveLQIGridConfiguration(LQIGridConfigurations lqiGrid, boolean activeConfChanged)
	        throws TransferException {

		logger.debug("Saving the LQI grid configuration...");
		configService.saveLQIConfig(lqiGrid);
		LQIGridConfiguration oldActiveConf = null;
		if(activeConfChanged){
			oldActiveConf = lqiGridConfigurations.getActiveConfiguration();
		}
		lqiGridConfigurations = lqiGrid;
		Collections.sort(lqiGridConfigurations.getConfigurations(), new LqiConfigComparator());
		eventQueue.post(new LQIConfigurationsChangedEvent(lqiGridConfigurations, oldActiveConf));
		if(activeConfChanged && gridDialog != null){
				gridDialog.refresh();
		}
	}

	/**
	 * Reads the LQI grid from the configuration file.
	 * 
	 * @return the LQI grid.
	 */
	public LQIGridConfigurations readLQIGridConfiguration(Window activeWindow) {
		
		if (lqiGridConfigurations == null) {
			try {
				lqiGridConfigurations = configService.readLQIConfig();
				if (lqiGridConfigurations.isEmpty()) {
					lqiGridConfigurations = LQIConstants.getDefaultLQIGrid();
					Collections.sort(lqiGridConfigurations.getConfigurations(), new LqiConfigComparator());
				}
			} catch (TransferException e) {
				logger.error("Error while reading the LQI grid configuration",
				        e);
				JOptionPane
				        .showMessageDialog(
				                activeWindow,
				                "An error has occurred while reading the LQI grid configuration",
				                "LQI Grid Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return lqiGridConfigurations;
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

	@Subscribe
	public void handleLQIConfigSelected(LQIConfigurationSelectionChangedEvent event) {
		try{
		// TODO
		/*
		 * - save the new active configuration
		 * 
		 * if the LQI GRID is OPENED: - reload the dialog with the new configuration
		 * 
		 * NOTE: the Plugin Manager should listen to this event too. When the
		 * LQI configuration changes, the Quality Score Evaluator plugin must be
		 * updated.
		 */
		//do something only if the active configuration actually changed. 
		if (lqiGridConfigurations != null && !lqiGridConfigurations.getActiveConfiguration().equals(
		        event.getNewSelectedConfiguration())) {
			try {
				configService.setActiveConfiguration(event.getNewSelectedConfiguration()
				        .getName());
				lqiGridConfigurations.setActiveConfiguration(event.getNewSelectedConfiguration());
			} catch (Exception e) {
				// just log a warning. It is not an actual issue from the user
				// point
				// of view if the active configuration is not saved to the conf
				// file.
				logger.warn("Error while saving the active configuration.", e);
			}
			if (isDialogOpened(gridDialog)) {
				gridDialog.replaceActiveConfiguration(event.getNewSelectedConfiguration());
			}
		}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	
	
	private boolean isDialogOpened(JDialog dialog){
		
		return dialog != null && dialog.isVisible();
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

		if(selectedSegment != null) {
			if (gridDialog == null || gridDialog.canCreateIssue()) {
				logger.debug("Creating a new Language Quality Issue: {} - {} - {}",
						category, severityName, severity);
				String comment = null;
				if (gridDialog != null) {
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
				if (comment != null) {
					gridDialog.clearCommentCellForCategory(category);
				}
			}
		}
	}

	public void manageConfigurations(){
		
		configsDialog = new LQIConfigurationsDialog(gridDialog, this, readLQIGridConfiguration(gridDialog));
		SwingUtilities.invokeLater(configsDialog);
	}
	
	/**
	 * Closes the active dialog.
	 */
	public void closeActiveDialog() {

		if (isDialogOpened(configsDialog)) {
			configsDialog.setVisible(false);
			configsDialog.dispose();
			configsDialog = null;
		} else if (isDialogOpened(gridDialog)) {
			gridDialog.setVisible(false);
			gridDialog.dispose();
			gridDialog = null;
		}
	}
	
	

	public LqiJsonConfigService getConfigService() {
		return configService;
	}

	public PlatformSupport getPlatformSupport() {
		return platformSupport;
	}

}

class LqiConfigComparator implements Comparator<LQIGridConfiguration>{

	@Override
    public int compare(LQIGridConfiguration o1, LQIGridConfiguration o2) {
		
	    return o1.getName().compareTo(o2.getName());
    }
	
}