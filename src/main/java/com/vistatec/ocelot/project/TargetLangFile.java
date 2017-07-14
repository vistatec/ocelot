package com.vistatec.ocelot.project;

import java.io.File;
import java.util.List;

public class TargetLangFile {

	private String targetLanguage;
	
	private List<File> files;
	
	private OcelotProjectFileType type;

	public String getTargetLanguage() {
		return targetLanguage;
	}

	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public OcelotProjectFileType getType() {
		return type;
	}

	public void setType(OcelotProjectFileType type) {
		this.type = type;
	}

}
