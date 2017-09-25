package com.vistatec.ocelot.xliff.freme;

import net.sf.okapi.filters.xliff.Parameters;

public class OcelotFilterParameters extends Parameters{

	public static final String MANAGE_FREME_ANNOTATIONS = "freme.annotations";
	
	public void setManageFremeAnnotations(boolean manageFremeAnnotations){
		
		setBoolean(MANAGE_FREME_ANNOTATIONS, manageFremeAnnotations);
	}
	
	public boolean isManageFremeAnnotations() {
		
		return getBoolean(MANAGE_FREME_ANNOTATIONS);
	}
	
}
