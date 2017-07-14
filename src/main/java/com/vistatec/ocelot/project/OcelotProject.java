package com.vistatec.ocelot.project;

import java.util.ArrayList;
import java.util.List;

public class OcelotProject {

	private String name;
	
	private String sourceLanguage;
	
//	private String location;
	
	private List<ProjectFile> files;
	
//	private ProjectLocation location;
	
private String tempLocation;
	
	private String actualLocation;
	
//	private List<TargetLangFile> targetFiles;

	// public OcelotProject() {
	//
	// location = new ProjectLocation();
	// }
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}
	
	public String getTempLocation() {
		return tempLocation;
	}

	public void setTempLocation(String tempLocation) {
		this.tempLocation = tempLocation;
	}

	public String getActualLocation() {
		return actualLocation;
	}

	public void setActualLocation(String actualLocation) {
		this.actualLocation = actualLocation;
	}

//	public String getLocation() {
//		return location;
//	}
//
//	public void setLocation(String location) {
//		this.location = location;
//	}

	public List<ProjectFile> getFiles() {
		return files;
	}

	public void setFiles(List<ProjectFile> files) {
		this.files = files;
	}

//	public List<TargetLangFile> getTargetFiles() {
//		return targetFiles;
//	}
//
//	public void setTargetFiles(List<TargetLangFile> targetFiles) {
//		this.targetFiles = targetFiles;
//	}
	
	public void addFile(ProjectFile file){
		if(files == null){
			files = new ArrayList<>();
		}
		files.add(file);
	}
	
}

//class ProjectLocation {
//	
//	private String tempLocation;
//	
//	private String actualLocation;
//
//	public String getTempLocation() {
//		return tempLocation;
//	}
//
//	public void setTempLocation(String tempLocation) {
//		this.tempLocation = tempLocation;
//	}
//
//	public String getActualLocation() {
//		return actualLocation;
//	}
//
//	public void setActualLocation(String actualLocation) {
//		this.actualLocation = actualLocation;
//	}
//	
//}


