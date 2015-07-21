package com.vistatec.ocelot.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.vistatec.ocelot.segment.model.OcelotSegment;

public class EditDistanceReportService {

	private static final String SEPARATOR = ",";
	private static final String LINE_SEPARATOR = "\n";
	private static final String REPORT_FILE_NAME_SUFFIX = "-EditDistance.csv";
	private static final String REPORT_DIR = System.getProperty("user.home") + "/" + "Ocelot Reports";
	private SegmentService segmentService;
	
	
	public EditDistanceReportService(SegmentService segmentService) {
		
		this.segmentService = segmentService;
	}
	
	public void createEditDistanceReport(String fileName){
		FileWriter writer = null;
		try {
			checkAndCreateReportDir();
			File file = new File(REPORT_DIR, getReportFileName(fileName));
			writer = new FileWriter(file);
			writeFileInfo(writer, fileName);
			writeColumnsName(writer);
			writeSegmentsInfo(writer);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private String getReportFileName(String xlifFileName){
		
		return xlifFileName + System.currentTimeMillis() + REPORT_FILE_NAME_SUFFIX;
	}
	
	private void writeSegmentsInfo(FileWriter writer) throws IOException{
		
		OcelotSegment segment = null;
		int total = 0;
		for(int i = 0; i<segmentService.getNumSegments(); i++){
			
			segment = segmentService.getSegment(i);
			writer.append(String.valueOf(segment.getSegmentNumber()));
			writer.append(SEPARATOR);
			writer.append(String.valueOf(segment.getEditDistance()));
			writer.append(LINE_SEPARATOR);
			total += segment.getEditDistance();
		}
		
		writer.append("");
		writer.append(SEPARATOR);
		writer.append("TOTAL");
		writer.append(LINE_SEPARATOR);
		writer.append("");
		writer.append(SEPARATOR);
		writer.append(String.valueOf(total));
		writer.append(LINE_SEPARATOR);
	}
	
	private void checkAndCreateReportDir(){
		
		File reportDir = new File(REPORT_DIR);
		if(!reportDir.exists() ){
			reportDir.mkdir();
		}
	}
	
	private void writeColumnsName(FileWriter writer) throws IOException{
		
		writer.append("Seg#");
		writer.append(SEPARATOR);
		writer.append("Edit Distance");
		writer.append(LINE_SEPARATOR);
	}
	
	private void writeFileInfo(FileWriter writer, String fileName) throws IOException{
		
		writer.append("File");
		writer.append(SEPARATOR);
		writer.append(fileName);
		writer.append(LINE_SEPARATOR);
		
	}
}
