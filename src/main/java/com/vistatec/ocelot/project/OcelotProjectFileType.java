package com.vistatec.ocelot.project;

public enum OcelotProjectFileType {

	TRANSLATION,
	REVIEW;
	
	public static OcelotProjectFileType forString(String string){
		
		OcelotProjectFileType retType = null;
		for(OcelotProjectFileType type: values()){
			if(type.toString().equals(string.toLowerCase())){
				retType = type;
				break;
			}
		}
		return retType;
	}
	
	@Override
	public String toString() {
		
		return name().toLowerCase();
	}
}
