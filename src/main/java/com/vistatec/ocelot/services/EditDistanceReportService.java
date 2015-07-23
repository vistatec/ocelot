package com.vistatec.ocelot.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.vistatec.ocelot.segment.model.OcelotSegment;

/**
 * This class generates an edit distance report each time a xliff file is saved
 */
public class EditDistanceReportService {

	/** Field separator. */
	private static final String SEPARATOR = ",";

	/** Line separator. */
	private static final String LINE_SEPARATOR = "\n";

	/** Report file name suffix. */
	private static final String REPORT_FILE_NAME_SUFFIX = "-EditDistance.csv";

	/** Report default directory. */
	private static final String REPORT_DIR = System.getProperty("user.home")
	        + "/" + "Ocelot Reports";

	private final Logger logger = Logger
	        .getLogger(EditDistanceReportService.class);

	/** The segment service. */
	private SegmentService segmentService;

	/**
	 * Constructor.
	 * 
	 * @param segmentService
	 *            the segment service.
	 */
	public EditDistanceReportService(SegmentService segmentService) {

		this.segmentService = segmentService;
	}

	/**
	 * Creates the edit distance report for the xliff file passed as parameter.
	 * 
	 * @param fileName
	 *            the xliff file name.
	 */
	public void createEditDistanceReport(String fileName) {
		logger.info("Creating EditDistance report for file " + fileName);
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
			logger.error(
			        "Error while generating the Edit Distance report for the file "
			                + fileName, e);
			JOptionPane.showMessageDialog(null,
			        "Impossible to generate the Edit Distance file.",
			        "Edit Distance Report error", JOptionPane.ERROR_MESSAGE);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.warn("Error while closing the file writer.", e);
				}
			}
		}

	}

	/**
	 * Gets the report file name.
	 * 
	 * @param xlifFileName
	 *            the xliff file name
	 * @return the report file name.
	 */
	private String getReportFileName(String xlifFileName) {

		return xlifFileName + System.currentTimeMillis()
		        + REPORT_FILE_NAME_SUFFIX;
	}

	/**
	 * Writes info about the segments
	 * 
	 * @param writer
	 *            the file writer.
	 * @throws IOException
	 *             the IO exception
	 */
	private void writeSegmentsInfo(FileWriter writer) throws IOException {

		OcelotSegment segment = null;
		int total = 0;
		for (int i = 0; i < segmentService.getNumSegments(); i++) {

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

	/**
	 * Checks if the default report folder exists. If it is not the case, it
	 * creates it.
	 */
	private void checkAndCreateReportDir() {

		File reportDir = new File(REPORT_DIR);
		if (!reportDir.exists()) {
			reportDir.mkdir();
		}
	}

	/**
	 * Writes columns names.
	 * 
	 * @param writer
	 *            teh file writer.
	 * @throws IOException
	 *             the IO exception.
	 */
	private void writeColumnsName(FileWriter writer) throws IOException {

		writer.append("Seg#");
		writer.append(SEPARATOR);
		writer.append("Edit Distance");
		writer.append(LINE_SEPARATOR);
	}

	/**
	 * Writes info about the file.
	 * 
	 * @param writer
	 *            the file writer.
	 * @param fileName
	 *            the xliff file name
	 * @throws IOException
	 *             the IO exception.
	 */
	private void writeFileInfo(FileWriter writer, String fileName)
	        throws IOException {

		writer.append("File");
		writer.append(SEPARATOR);
		writer.append(fileName);
		writer.append(LINE_SEPARATOR);

	}
}
