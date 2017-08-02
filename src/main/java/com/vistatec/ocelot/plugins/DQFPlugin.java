package com.vistatec.ocelot.plugins;

import java.awt.Window;

import javax.swing.JMenu;

import com.vistatec.ocelot.config.LqiJsonConfigService;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.project.ProjectFile;
import com.vistatec.ocelot.segment.model.OcelotSegment;

public interface DQFPlugin extends Plugin {

	public JMenu getDqfMenu(Window parentWindow);
	
	public void setLQIConfigService(LqiJsonConfigService lqiConfigService);
	
	public void setOcelotEventQueue(OcelotEventQueue eventQueue );
	
	public void fileOpened(ProjectFile file);

	public void editedSegment(OcelotSegment segment);
	
	public void fileSaved(String filename );

	public void projectClosed();

	public void errorDeleted(OcelotSegment segment, LanguageQualityIssue lqi);

	public void errorAdded(OcelotSegment segment, LanguageQualityIssue lqi);

	public void errorEdited(OcelotSegment segment, LanguageQualityIssue lqi);
}
