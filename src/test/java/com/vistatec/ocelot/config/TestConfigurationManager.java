package com.vistatec.ocelot.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.vistatec.ocelot.config.json.ProfileConfig;

public class TestConfigurationManager {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private ConfigurationManager confManager;

	private File testOcelotDir;

	@Before
	public void setup() throws IOException {

		testOcelotDir = testFolder.newFolder(".ocelot");
		confManager = new ConfigurationManagerTest();
	}

	@Test
	public void testDefaultConfCreation() throws ConfigurationException {

		confManager.readAndCheckConfiguration(testOcelotDir);
		File confFolder = new File(testOcelotDir, ConfigurationManager.CONF_DIR);
		Assert.assertTrue(confFolder.exists());
		File profileConfFile = new File(confFolder, ConfigurationManager.LAST_SESSION_FILE_NAME);
		Assert.assertTrue(profileConfFile.exists());
		File defProfFolder = new File(confFolder, ProfileConfig.DEFAULT_PROF_NAME);
		Assert.assertTrue(defProfFolder.exists());
		File ocelotConfFile = new File(defProfFolder, ConfigurationManager.OCELOT_CONF_FILE_NAME);
		Assert.assertTrue(ocelotConfFile.exists());
		File lqiConfFile = new File(defProfFolder, ConfigurationManager.LQI_CONF_FILE_NAME);
		Assert.assertTrue(lqiConfFile.exists());
		File tmFolder = new File(defProfFolder, ConfigurationManager.TM_DIR);
		Assert.assertTrue(tmFolder.exists());
		Assert.assertTrue(tmFolder.isDirectory());
	}


	@Test
	public void testActiveProfileFolderMissing() throws IOException, ConfigurationException {

		createNotDefProfileFile();
		confManager.readAndCheckConfiguration(testOcelotDir);
		ProfileConfigService profConfService = confManager.getProfileConnfigService();
		Assert.assertEquals(ProfileConfig.DEFAULT_PROF_NAME, profConfService.getProfileName());
		Assert.assertTrue(profConfService.mustPromptMessage());
	}

	@Test
	public void testActiveProfileFolderMissingException() throws IOException, ConfigurationException {

		createNotDefProfileFile();
		((ConfigurationManagerTest)confManager).setOption(1);
		thrown.expect(ConfigurationException.class);
		thrown.expectMessage("Configuration folder is missing: Test");
		confManager.readAndCheckConfiguration(testOcelotDir);
	}

	
	@Test
	public void testIncosistentLQIConfNoActiveConf() throws IOException, ConfigurationException{

		InputStream lqiNoActiveConfStream = getClass().getResourceAsStream("lqi_config_noactiveConf.json");
		createTestFolder(lqiNoActiveConfStream);
		thrown.expect(ConfigurationException.class);
		thrown.expectMessage("an active configuration is not declared");
	    confManager.readAndCheckConfiguration(testOcelotDir);
	}
	
	@Test
	public void testIncosistentLQIConfActiveConfNotExisting() throws IOException, ConfigurationException{

		InputStream lqiNoActiveConfStream = getClass().getResourceAsStream("lqi_config_not_existing.json");
		createTestFolder(lqiNoActiveConfStream);
		thrown.expect(ConfigurationException.class);
		thrown.expectMessage("the active configuration Configuration C is not defined");
		confManager.readAndCheckConfiguration(testOcelotDir);
	}
	
	@Test
	public void testIncosistentLQIConfSeverityNotExisting() throws IOException, ConfigurationException{

		InputStream lqiNoActiveConfStream = getClass().getResourceAsStream("lqi_config_wrong_severity.json");
		createTestFolder(lqiNoActiveConfStream);
		thrown.expect(ConfigurationException.class);
		thrown.expectMessage("the category mistranslation has a shortcut associated to a not existent severity Test Severity");
		confManager.readAndCheckConfiguration(testOcelotDir);
	}


	private void createTestFolder(InputStream lqiConfStream) throws IOException{
		
		createNotDefProfileFile();
		File testFolder = new File(testOcelotDir.getAbsolutePath() + File.separator + ConfigurationManager.CONF_DIR, "Test");
		testFolder.mkdir();
		InputStream defOcelotConfFile = getClass().getResourceAsStream("ocelot_cfg.json");
		File testOcelotFile = new File(testFolder, ConfigurationManager.OCELOT_CONF_FILE_NAME);
		FileUtils.copyInputStreamToFile(defOcelotConfFile, testOcelotFile);
		File testLqiConfFile = new File(testFolder, ConfigurationManager.LQI_CONF_FILE_NAME);
		FileUtils.copyInputStreamToFile(lqiConfStream, testLqiConfFile);
	}
	
	private void createNotDefProfileFile() throws IOException{
		
		File confFolder = new File(testOcelotDir, ConfigurationManager.CONF_DIR);
		confFolder.mkdir();
		File profileConfFile = new File(confFolder, ConfigurationManager.LAST_SESSION_FILE_NAME);
		profileConfFile.createNewFile();
		InputStream testProfFileStream = getClass().getResourceAsStream("testProfileFile.json");
		FileUtils.copyInputStreamToFile(testProfFileStream, profileConfFile);
		
	}
	

}

class ConfigurationManagerTest extends ConfigurationManager {

	private int option;
	
	public ConfigurationManagerTest() {
		option = 0;
    }
	
	@Override
	protected int getOptionFromUser(String activeProfName) {
	    return option;
	}
	
	public void setOption(int option){
		this.option = option;
	}
}

