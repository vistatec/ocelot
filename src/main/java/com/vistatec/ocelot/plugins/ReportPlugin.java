package com.vistatec.ocelot.plugins;

import java.awt.Window;
import java.util.List;

import com.vistatec.ocelot.segment.model.OcelotSegment;

public interface ReportPlugin extends Plugin {

	
	public void generateReport(Window window) throws ReportException;
	
	public void onOpenFile(String fileName, List<OcelotSegment> segments);
	
	public class ReportException extends Exception {

        private static final long serialVersionUID = 8206326021490700485L;

        
	}
}
