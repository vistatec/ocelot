package com.vistatec.ocelot.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.vistatec.ocelot.config.json.ProfileConfig;

public class ConfigurationManager {

	public static final String CONF_DIR = "config";

	public static final String TM_DIR = "tm";
	
	public static final String PLUGINS_DIR = "plugins";

	public static final String LAST_SESSION_FILE_NAME = "last-session.json";

	public static final String OCELOT_CONF_FILE_NAME = "ocelot_cfg.json";

	public static final String LQI_CONF_FILE_NAME = "lqi_cfg.json";
	
	public static final String RULES_CONF_FILE_NAME = "rules.properties";

	private final Logger log = Logger.getLogger(ConfigurationManager.class);

	private File configFolder;

	private ProfileConfigService profileConfService;

	private OcelotJsonConfigService ocelotConfService;

	private LqiJsonConfigService lqiConfService;
	
	private DirectoryBasedConfigs rulesConfigs;
	
	private File tmFolder;
	
	private File pluginsFolder;

	public void readAndCheckConfiguration(File ocelotDir)
	        throws ConfigurationException {

		log.debug("Reading configuration...");

		// check configuration folder
		configFolder = new File(ocelotDir, CONF_DIR);
		if (!configFolder.exists()) {
			configFolder.mkdirs();
		}

		// check profile configuration file
		File profileConfFile = createConfigurationFile(configFolder,
		        LAST_SESSION_FILE_NAME);
		createProfileConfigService(profileConfFile);
		File activeConfFolder = checkProfileFolder();
		createOcelotConfigService(activeConfFolder);
		createLqiConfigService(activeConfFolder);
		rulesConfigs = new DirectoryBasedConfigs(activeConfFolder);
		tmFolder = new File(activeConfFolder, TM_DIR);
		if (!tmFolder.exists()) {
			tmFolder.mkdir();
		}
		pluginsFolder = new File(activeConfFolder, PLUGINS_DIR);
		if(!pluginsFolder.exists()){
			pluginsFolder.mkdir();
		}
	}

	private void createProfileConfigService(File profileConfFile)
	        throws ConfigurationException {
		log.debug("Creating the Profile Configuration Service...");
		try {
			profileConfService = new ProfileConfigService(
			        new ProfileConfigTransferService(profileConfFile));
		} catch (TransferException e) {
			throw new ConfigurationException(
			        "Error while creating the Profile Configuration Service.",
			        e);
		}
	}

	private void createOcelotConfigService(File activeConfFolder)
	        throws ConfigurationException {
		log.debug("Creating the Ocelot Configuration Service...");
		try {
			ocelotConfService = new OcelotJsonConfigService(
			        new OcelotJsonConfigTransferService(getConfigFile(
			                activeConfFolder, OCELOT_CONF_FILE_NAME)));
		} catch (TransferException e) {
			throw new ConfigurationException(
			        "Error while creating the Ocelot Configuration Service", e);
		}
	}

	private void createLqiConfigService(File activeConfFolder) throws ConfigurationException {

		log.debug("Creating the LQI Configuration Service...");
		try {
			lqiConfService = new LqiJsonConfigService(
			        new LqiJsonConfigTransferService(getConfigFile(
			                activeConfFolder, LQI_CONF_FILE_NAME)));
		} catch (TransferException e) {
			throw new ConfigurationException(
			        "Error while creating the LQI Config Service", e);
		}
	}

	private File checkProfileFolder() throws ConfigurationException {
		// check if the active profile folder exists. If not, get the default
		// configuration and warn the user
		File activeConfigFolder = null;
		String activeProfName = profileConfService.getProfileName();
		if (!isDefaultProfile(activeProfName)) {
			activeConfigFolder = new File(configFolder, activeProfName);
			if (!activeConfigFolder.exists()) {
				activeConfigFolder = manageMissingConfigurationEvent(activeProfName);
			}
		} else {
			activeConfigFolder = checkDefaultConfFolder();
		}

		return activeConfigFolder;
	}

	private File manageMissingConfigurationEvent(String activeProfName)
	        throws ConfigurationException {

		log.debug("Managing missing configuration \"" + activeProfName
		        + "\"...");
		File activeConfigFolder = null;
		int option = getOptionFromUser(activeProfName);
		if (option == 0) {

			try {
				activeConfigFolder = checkDefaultConfFolder();
				profileConfService
				        .changeActiveProfile(ProfileConfig.DEFAULT_PROF_NAME);
				log.debug("Active configuration changed to the default one.");
			} catch (TransferException e) {
				throw new ConfigurationException(
				        "Error while changing the active profile to \""
				                + ProfileConfig.DEFAULT_PROF_NAME + "\"", e);
			}
		} else {
			throw new ConfigurationException(
			        "Configuration folder is missing: " + activeProfName);
		}
		return activeConfigFolder;
	}

	protected int getOptionFromUser(String activeProfName ){
		
		Object[] options = { "Continue", "Abort" };
		return JOptionPane
		        .showOptionDialog(
		                null,
		                "The configuration \""
		                        + activeProfName
		                        + "\" does not exist. The \"Default\" configuration will be loaded.\nDo you wish to continue?",
		                "Missing Configuration Error", 0,
		                JOptionPane.ERROR_MESSAGE, null, options, options[0]);
	}
	
	private File checkDefaultConfFolder() throws ConfigurationException {

		File defConfFolder = new File(configFolder,
		        ProfileConfig.DEFAULT_PROF_NAME);
		if (!defConfFolder.exists()) {
			createNewProfileFolder(defConfFolder);
		}
		return defConfFolder;
	}
	
	public static void createNewProfileFolder(File profileFolder) throws ConfigurationException{
		
		profileFolder.mkdir();
		createConfigurationFile(profileFolder, OCELOT_CONF_FILE_NAME);
		createConfigurationFile(profileFolder, LQI_CONF_FILE_NAME);
		createConfigurationFile(profileFolder, RULES_CONF_FILE_NAME);
		
	}

	private boolean isDefaultProfile(String profileName) {

		return ProfileConfig.DEFAULT_PROF_NAME.equalsIgnoreCase(profileName);
	}

	private static File createConfigurationFile(File parentDir, String fileName)
	        throws ConfigurationException {

		File confFile = new File(parentDir, fileName);
		if (!confFile.exists()) {
			try {
				InputStream defFileStream = ConfigurationManager.class
				        .getResourceAsStream("/conf/" + fileName);
				FileUtils.copyInputStreamToFile(defFileStream, confFile);
			} catch (IOException e) {
				throw new ConfigurationException(
				        "Error while creating the configuration file "
				                + confFile, e);
			}
		}

		return confFile;
	}

	private File getConfigFile(File activeConfigFolder, String fileName) {
		return new File(activeConfigFolder, fileName);
	}

	public ProfileConfigService getProfileConnfigService() {
		return profileConfService;
	}

	public OcelotJsonConfigService getOcelotConfigService() {
		return ocelotConfService;
	}

	public LqiJsonConfigService getLqiConfigService()  {

		return lqiConfService;
	}

	public File getTmConfigDir() {
		return tmFolder;
	}

	public File getOcelotMainConfigurationFolder() {
		return configFolder;
	}
	
	public DirectoryBasedConfigs getRulesConfigs(){
		return rulesConfigs;
	}
	
	public File getPluginsFolder(){
		return pluginsFolder;
	}
}
