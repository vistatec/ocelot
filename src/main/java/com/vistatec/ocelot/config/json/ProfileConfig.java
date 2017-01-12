package com.vistatec.ocelot.config.json;

public class ProfileConfig implements RootConfig {

	public static final String DEFAULT_PROFILE = "default";
	
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
		profile = DEFAULT_PROFILE;
		promptMessage = true;
				
	}
	
}
