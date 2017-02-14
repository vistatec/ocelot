package com.vistatec.ocelot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.json.ProfileConfig;

public class ProfileConfigService  {

	
	private final Logger log = LoggerFactory.getLogger(ProfileConfigService.class);
	
	private final ConfigTransferService cfgXservice;
	
	private ProfileConfig config;
	
	public ProfileConfigService(ConfigTransferService cfgXservice) throws TransferException {
    
		this.cfgXservice = cfgXservice;
		config = (ProfileConfig) cfgXservice.read();
				
	}
	
	public String getProfileName(){
		return config.getProfile();
	}
	
	public boolean mustPromptMessage(){
		return config.getProfile().equalsIgnoreCase(ProfileConfig.DEFAULT_PROF_NAME) && config.getPromptMessage(); 
	}
	
	public void changeActiveProfile(String profileName) throws TransferException{
		
		log.debug("Changing active profile");
		if(profileName.equals(ProfileConfig.DEFAULT_PROF_NAME)){
			config.setDefaultProfile();
		} else {
			config.setProfile(profileName);
		}
		cfgXservice.save(config);
	}
	
	public void doNotShowAgain() throws TransferException{
		log.debug("Do not show the message again");
		config.setPromptMessage(false);
		cfgXservice.save(config);
	}
	
	
	
}
