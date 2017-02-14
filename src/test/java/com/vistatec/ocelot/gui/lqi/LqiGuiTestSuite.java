package com.vistatec.ocelot.gui.lqi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import com.vistatec.ocelot.config.ConfigurationException;
import com.vistatec.ocelot.config.ConfigurationManager;
import com.vistatec.ocelot.config.ProfileConfigService;
import com.vistatec.ocelot.config.TransferException;

public class LqiGuiTestSuite {

	private static final String TEST_PROFILE = "LqiGuiTest";

	private String oldProfile;

	private ProfileConfigService profileConfService;

	private void setup() throws ConfigurationException, TransferException, IOException {
		// File confDir = new File(System.getProperty("user.home") +
		// File.separator + ".ocelot", ConfigurationManager.CONF_DIR );
		ConfigurationManager confManager = new ConfigurationManager();
		File ocelotDir = new File(System.getProperty("user.home"), ".ocelot");
		confManager.readAndCheckConfiguration(ocelotDir);
		oldProfile = confManager.getProfileConnfigService().getProfileName();
		File testProfileDir = new File(ocelotDir
		        .getAbsolutePath()
		        + File.separator
		        + ConfigurationManager.CONF_DIR, TEST_PROFILE);
		ConfigurationManager.createNewProfileFolder(testProfileDir);
		InputStream lqiConfFileStream = getClass().getResourceAsStream("lqi_cfg.json");
		FileUtils.copyInputStreamToFile(lqiConfFileStream, new File(testProfileDir, ConfigurationManager.LQI_CONF_FILE_NAME));
		confManager.getProfileConnfigService()
		        .changeActiveProfile(TEST_PROFILE);
	}
	
	public void runTests(){
		try {
	        setup();
        } catch (ConfigurationException | TransferException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		String[] params = { "com.vistatec.ocelot.gui.lqi.TestLqiConfigsTool" };
		org.netbeans.jemmy.Test.main(params);
		try {
	        cleanup();
        } catch (TransferException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}

	private void cleanup() throws TransferException {

		profileConfService.changeActiveProfile(oldProfile);
		File testConfFolder = new File(System.getProperty("user.home")
		        + File.separator + ".ocelot" + File.separator
		        + ConfigurationManager.CONF_DIR, TEST_PROFILE);
		File confFiles[] = testConfFolder.listFiles();
		for(File file: confFiles){
			file.delete();
		}
		testConfFolder.delete();
	}
	
	public static void main(String[] args) {
	    LqiGuiTestSuite testSuite = new LqiGuiTestSuite();
	    testSuite.runTests();
    }

}
