package com.vistatec.ocelot.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import com.vistatec.ocelot.config.json.ProfileConfig;

public class DirectoryConfigurationUtils {

	private static final String CONF_DIR = "conf";

	private static final String TM_DIR = "tm";

	private static final String PROFILE_CONF_FILE_NAME = "profile.json";

	private static final String OCELOT_CONF_FILE_NAME = "ocelot_cfg.json";

	private static final String LQI_CONF_FILE_NAME = "lqi_cfg.json";


	public static ProfileConfigService setupProfileConfService(File confFolder)
	        throws IOException, TransferException, ConfigurationException {

		File profFile = new File(confFolder, PROFILE_CONF_FILE_NAME);
		if (!profFile.exists()) {
			createDefaultFile(profFile.getParentFile(), PROFILE_CONF_FILE_NAME);
		}
		ProfileConfigService profConfService = new ProfileConfigService(
		        new ProfileConfigTransferService(profFile));
		checkProfilesFolders(profConfService, confFolder);
		return profConfService;
	}

	private static void checkProfilesFolders(
	        ProfileConfigService profConfService, File confFolder)
	        throws IOException, ConfigurationException, TransferException {
		// check if the active profile folder exists.
				// If not, gets the default folder and warn the user.
		String activeProfName = profConfService.getProfileName();
		if (!isDefaultProfile(activeProfName)) {
			File profFolder = new File(confFolder, activeProfName);
			if (!profFolder.exists()) {
				Object[] options = { "Continue", "Abort" };
				int option = JOptionPane
				        .showOptionDialog(
				                null,
				                "The configuration \""
				                        + activeProfName
				                        + "\" does not exist. The \"Default\" configuration will be loaded.\nDo you wish to continue?",
				                "Missing Configuration Error", 0,
				                JOptionPane.ERROR_MESSAGE, null, options,
				                options[0]);
				if (option == 0) {
					checkDefaultConfFolder(confFolder);
					profConfService.changeActiveProfile(ProfileConfig.DEFAULT_PROF_NAME);
				} else {
					throw new ConfigurationException(
					        "Configuration folder is missing: "
					                + activeProfName);
				}
			}
		}
	}

	public static File getConfigurationFolder(File ocelotDir) {
		File confFolder = new File(ocelotDir, CONF_DIR);
		if (!confFolder.exists()) {
			confFolder.mkdirs();
		}
		return confFolder;
	}

	public static File getActiveProfileFolder(File configFolder, String profileName) throws IOException {

		File profileFolder = new File(configFolder, profileName);
		if(isDefaultProfile(profileName)){
			checkDefaultConfFolder(profileFolder);
		}
		return profileFolder;
	}

	private static void checkDefaultConfFolder(File defConfFolder)
	        throws IOException {

		if (!defConfFolder.exists()) {
			defConfFolder = createConfigurationFolder(defConfFolder.getParentFile(), defConfFolder.getName());
		}
	}

	private static File createConfigurationFolder(File parentDir,
	        String folderName) throws IOException {

		File confFolder = null;
		confFolder = new File(parentDir, folderName);
		if (!confFolder.exists()) {
			confFolder.mkdir();
		}
		createDefaultFile(confFolder, OCELOT_CONF_FILE_NAME);
		createDefaultFile(confFolder, LQI_CONF_FILE_NAME);
		return confFolder;
	}

	private static void createDefaultFile(File confFolder, String fileName)
	        throws IOException {

		File defFile = new File(confFolder, fileName);
		if (!defFile.exists()) {
			defFile.createNewFile();
			InputStream defOcelotFileStream = DirectoryConfigurationUtils.class
			        .getResourceAsStream("/conf/" + fileName);
			FileUtils.copyInputStreamToFile(defOcelotFileStream, defFile);
		}
	}

	public static OcelotJsonConfigService setupOcelotConfService(File profileDir)
	        throws IOException, TransferException {

		File ocelotConfFile = new File(profileDir, OCELOT_CONF_FILE_NAME);
		if (!ocelotConfFile.exists()) {
			createDefaultFile(profileDir, OCELOT_CONF_FILE_NAME);
		}
		return new OcelotJsonConfigService(new OcelotJsonConfigTransferService(
		        ocelotConfFile));
	}

	public static LqiJsonConfigService setupLqiConfService(File profileDir)
	        throws IOException, TransferException {

		File lqiConfFile = new File(profileDir, LQI_CONF_FILE_NAME);
		if (!lqiConfFile.exists()) {
			createDefaultFile(profileDir, LQI_CONF_FILE_NAME);
		}
		return new LqiJsonConfigService(new LqiJsonConfigTransferService(
		        lqiConfFile));
	}

	public static File getTmFolder(File profileDir) {

		File tm = new File(profileDir, TM_DIR);
		tm.mkdirs();
		return tm;
	}

	private static boolean isDefaultProfile(String profileName){
		
		return ProfileConfig.DEFAULT_PROF_NAME.equalsIgnoreCase(profileName);
	}
	
	public static void createNewProfileFolder(File confFolder, String newProfileName) throws IOException{
		
		createConfigurationFolder(confFolder, newProfileName);
		
	}

}
