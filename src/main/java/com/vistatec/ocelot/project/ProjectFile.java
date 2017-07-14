package com.vistatec.ocelot.project;

import java.io.File;

public class ProjectFile {

	private String targetLanguage;
	
	private File file;
	
	private OcelotProjectFileType type;

	public String getTargetLanguage() {
		return targetLanguage;
	}

	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public OcelotProjectFileType getType() {
		return type;
	}

	public void setType(OcelotProjectFileType type) {
		this.type = type;
	}
	
	
}
