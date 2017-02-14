package com.vistatec.ocelot.profile;

import java.util.List;

public interface IProfileManager {

	public List<String> getProfiles() ;
	
	public String getActiveProfile();

	public void changeProfile(String selProfile) throws ProfileException;
	
	public void restoreOldProfile(String oldProfile, String newProfile, boolean deleteFolders) throws ProfileException;
}
