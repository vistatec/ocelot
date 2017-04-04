package com.vistatec.ocelot.profile;

import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.ConfigurationException;
import com.vistatec.ocelot.config.ConfigurationManager;
import com.vistatec.ocelot.config.ProfileConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.events.ProfileChangedEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;

public class ProfileManager implements IProfileManager {

	private final Logger log = LoggerFactory.getLogger(ProfileManager.class);

	private OcelotEventQueue eventQueue;

	private File confDir;

	private DirectoryFilter dirFilter;

	private ProfileConfigService configService;

	public ProfileManager(File confDir, ProfileConfigService configService,
	        OcelotEventQueue eventQueue) {

		this.confDir = confDir;
		this.configService = configService;
		this.eventQueue = eventQueue;
		dirFilter = new DirectoryFilter();
	}

	/**
	 * Gets the list of available profiles: it lists the names of the folders
	 * existing in the "conf" directory.
	 */
	public List<String> getProfiles() {

		List<String> profiles = new ArrayList<String>();
		File[] dirs = confDir.listFiles(dirFilter);
		if (dirs != null) {
			for (File dir : dirs) {
				profiles.add(dir.getName());
			}
		}

		// the listFiles method does not guarantee that the directory names are
		// returned in alphabetical order.
		Collections.sort(profiles);
		return profiles;
	}

	/**
	 * Gets the name of the active profile.
	 */
	public String getActiveProfile() {
		return configService.getProfileName();
	}

	/**
	 * Changes the active profile.
	 */
	@Override
	public void changeProfile(String selProfile) throws ProfileException {
		// save the profile and restart Ocelot
		// if it is a new profile, create a new configuration folder and copy
		// the default configuration

		log.debug("Changing the current profile to \"" + selProfile + "\"...");
		try {
			if (!getProfiles().contains(selProfile)) {
				createProfileDir(selProfile);
			}
			configService.changeActiveProfile(selProfile);
			eventQueue.post(new ProfileChangedEvent(selProfile));
		} catch (TransferException e) {
			log.error("Error while saving the new profile.", e);
			throw new ProfileException(e);
		} catch (ConfigurationException e) {
			log.error(
			        "Error while copying the default directory to the new configuration folder.",
			        e);
			throw new ProfileException(e);
		} catch (SecurityException e) {
			log.error(
			        "Security exception while creating the new configuration directory",
			        e);
			throw new ProfileException(e);
		}
	}

	/**
	 * Restores the old profile and deletes the folders related to the new
	 * profile if they have been created. This method should be invoked when an
	 * error occurs while changing the active profile.
	 */
	public void restoreOldProfile(String oldProfile, String newProfile,
	        boolean folderCreated) throws ProfileException {

		log.debug("Restoring old profile...");
		try {
			configService.changeActiveProfile(oldProfile);
		} catch (TransferException e) {
			log.error("Error while saving the active profile name", e);
			throw new ProfileException(e);
		}
		if (folderCreated) {
			File confFolder = new File(confDir, newProfile);
			if (confFolder.exists()) {
				deleteFolder(confFolder);
			}
		}
	}

	private void deleteFolder(File folder) {

		File[] files = folder.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();

	}

	/**
	 * Displays the Profile dialog
	 * 
	 * @param currWindow
	 *            the current window.
	 */
	public void displayProfileDialog(Window currWindow) {

		ProfileDialog profDialog = new ProfileDialog(currWindow, this);
		SwingUtilities.invokeLater(profDialog);
	}

	private void createProfileDir(String selProfile) throws ConfigurationException {
		log.debug("Creating a new profile directory \"" + selProfile + "\"...");
//		DirectoryConfigurationUtils.createNewProfileFolder(confDir, selProfile);
		ConfigurationManager.createNewProfileFolder(new File(confDir, selProfile));
	}

	private void promptDefaultProfileMessage(Window currWindow){
		
		DefaultProfileWarningDialog warnDialog = new DefaultProfileWarningDialog(currWindow);
		warnDialog.promptWarningMessage();
		if(warnDialog.isDoNotShowAgainFlagged()){
			log.debug("Do not show again the default configuration message.");
			 try {
	            configService.doNotShowAgain();
            } catch (TransferException e) {
            	log.error("Error while setting the do not show again flag", e);
            }
		}
	}
	
	public void checkProfileAndPromptMessage(Window currWindow){
		
		if(configService.mustPromptMessage()){
			promptDefaultProfileMessage(currWindow);
		}
	}
	
}

class DirectoryFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		return pathname.isDirectory();
	}

}
