package com.vistatec.ocelot.config.json;


public class ProfileConfig implements RootConfig {

	public final static String DEFAULT_PROF_NAME = "Default";
	
	private String profile;
	
	private boolean promptMessage;
	
	public void setProfile(String profile){
		this.profile = profile;
	}
	
	public String getProfile(){
		return profile;
	}
	
	public void setPromptMessage(boolean promptMessage ){
		this.promptMessage = promptMessage;
	}
	
	public boolean getPromptMessage(){
		return promptMessage;
	}
	
	public void setDefaultProfile(){
		profile = DEFAULT_PROF_NAME;
		promptMessage = true;
				
	}
	
}
