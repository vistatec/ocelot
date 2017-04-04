package com.vistatec.ocelot.profile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.vistatec.ocelot.config.ConfigurationException;
import com.vistatec.ocelot.config.ConfigurationManager;
import com.vistatec.ocelot.config.json.ProfileConfig;
import com.vistatec.ocelot.events.api.OcelotEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

public class ProfileManagerTest {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();
	
	private ProfileManager profManager;
	
	@Before
	public void setup() throws IOException, ConfigurationException{
		
		File testOcelotDir = testFolder.newFolder(".ocelot");
		ConfigurationManager confManager = new ConfigurationManager();
		confManager.readAndCheckConfiguration(testOcelotDir);
		setupProfileFolders(confManager.getOcelotMainConfigurationFolder());
		profManager = new ProfileManager(confManager.getOcelotMainConfigurationFolder(), confManager.getProfileConnfigService(), new TestEventQueue());
		
	}
	
	
	private void setupProfileFolders(File confFolder) throws IOException{

		File defProfFolder = new File(confFolder, ProfileConfig.DEFAULT_PROF_NAME);
		File profAFolder =  new File(confFolder, "Profile A");
		profAFolder.mkdir();
		FileUtils.copyDirectory(defProfFolder, profAFolder);
		File profBFolder =  new File(confFolder, "Profile B");
		profBFolder.mkdir();
		FileUtils.copyDirectory(defProfFolder, profBFolder);
	}
	
	@Test
	public void getProfilesTest(){
		
		List<String> profiles = profManager.getProfiles();
		Assert.assertNotNull(profiles);
		Assert.assertEquals(3, profiles.size());
		Assert.assertEquals(ProfileConfig.DEFAULT_PROF_NAME, profiles.get(0));
		Assert.assertEquals("Profile A", profiles.get(1));
		Assert.assertEquals("Profile B", profiles.get(2));
		Assert.assertEquals(ProfileConfig.DEFAULT_PROF_NAME, profManager.getActiveProfile());
		
	}
	
	@Test
	public void changeProfileTest() throws ProfileException{
		
		Assert.assertEquals(ProfileConfig.DEFAULT_PROF_NAME, profManager.getActiveProfile());
		profManager.changeProfile("Profile A");
		Assert.assertEquals("Profile A", profManager.getActiveProfile());
		profManager.changeProfile("Profile B");
		Assert.assertEquals("Profile B", profManager.getActiveProfile());
		String newProfileName = "Profile C";
		List<String> profiles = profManager.getProfiles();
		Assert.assertFalse(profiles.contains(newProfileName));
		Assert.assertEquals(3, profiles.size());
		profManager.changeProfile(newProfileName);
		Assert.assertEquals(newProfileName, profManager.getActiveProfile());
		profiles = profManager.getProfiles();
		Assert.assertEquals(4, profiles.size());
		Assert.assertTrue(profiles.contains(newProfileName));
	}
	
	@Test
	public void restoreOldProfile() throws ProfileException{
		
		profManager.changeProfile("Profile A");
		Assert.assertEquals("Profile A", profManager.getActiveProfile());
		profManager.restoreOldProfile(ProfileConfig.DEFAULT_PROF_NAME, "Profile A", false);
		Assert.assertEquals(ProfileConfig.DEFAULT_PROF_NAME, profManager.getActiveProfile());
		List<String> profiles = profManager.getProfiles();
		Assert.assertEquals(3, profiles.size());
		Assert.assertTrue(profiles.contains("Profile A"));
	}
	
	@Test
	public void restoreOldProfileAndDeleteProfDir() throws ProfileException{
		
		profManager.changeProfile("Profile A");
		Assert.assertEquals("Profile A", profManager.getActiveProfile());
		profManager.restoreOldProfile(ProfileConfig.DEFAULT_PROF_NAME, "Profile A", true);
		Assert.assertEquals(ProfileConfig.DEFAULT_PROF_NAME, profManager.getActiveProfile());
		List<String> profiles = profManager.getProfiles();
		Assert.assertEquals(2, profiles.size());
		Assert.assertFalse(profiles.contains("Profile A"));
	}
	
	
}

class TestEventQueue implements OcelotEventQueue {

	@Override
    public void post(OcelotEvent event) {
	    
    }

	@Override
    public void registerListener(OcelotEventQueueListener listener) {
	    
    }

	@Override
    public void unregisterListener(OcelotEventQueueListener listener) {
	    
    }
	
}