package com.vistatec.ocelot.report.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.vistatec.ocelot.report.model.FileTiming;
import com.vistatec.ocelot.report.model.SegmentTiming;

/**
 * Writer class generating time-capture reports.
 */
public class SegmentTimingWriter {

	/** Time-capture report file name suffix. */
	private static final String FILE_NAME_SUFFIX = "_TimeCaptureReport.csv";

	/** Field separator. */
	private static final String SEPARATOR = ",";

	/** Line separator. */
	private static final String LINE_SEPARATOR = "\n";

	private final Logger logger = Logger.getLogger(SegmentTimingWriter.class);

	/**
	 * Writes a report file from the file timing passed as parameter. Then it is
	 * saved in a specific folder.
	 * 
	 * @param folderPath
	 *            the folder where the report has to be saved.
	 * @param fileTiming
	 *            the file timing
	 */
	public void write(String folderPath, FileTiming fileTiming)
	        throws IOException {

		logger.debug("Writing time-capture report for file "
		        + fileTiming.getName());
		FileWriter writer = null;
		try {
			File file = new File(folderPath,
			        getReportFileName(fileTiming.getName()));
			writer = new FileWriter(file);
			writeFileInfo(writer, fileTiming);
			writeColumnsName(writer);
			writeSegmentsInfo(writer, fileTiming.getSegmentTimings());
			writer.flush();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				logger.warn("Error while closing the file writer", e);
			}
		}

	}

	/**
	 * Writes a line in the file for each segment included in the list.
	 * 
	 * @param writer
	 *            the file writer
	 * @param segments
	 *            the list of segments
	 * @throws IOException
	 *             the IO exception
	 */
	private void writeSegmentsInfo(FileWriter writer,
	        List<SegmentTiming> segments) throws IOException {

		Collections.sort(segments, new SegmentComparator());
		for (SegmentTiming segment : segments) {
			writer.append(segment.getName());
			writer.append(SEPARATOR);
			writer.append(getTimeString(segment.getTiming()));
			writer.append("\n");
		}
	}

	/**
	 * Writes columns names.
	 * 
	 * @param writer
	 *            the file writer
	 * @throws IOException
	 *             the IO exception
	 */
	private void writeColumnsName(FileWriter writer) throws IOException {

		writer.append("\n");
		writer.append("Seg#");
		writer.append(SEPARATOR);
		writer.append("Time");
		writer.append("\n");
	}

	/**
	 * Gets the timing formatted as a string
	 * 
	 * @param timing
	 *            the timing
	 * @return the timing formatted as a string
	 */
	private String getTimeString(double timing) {

		String timingStr = null;
		double seconds = timing / 1000;
		double minutes = seconds / 60;
		double hours = 0;
		if (minutes >= 1) {
			hours = minutes / 60;
			if (hours >= 1) {
				timingStr = String.valueOf((int) Math.floor(hours)) + "h "
				        + String.valueOf((int) Math.floor(minutes)) + "m "
				        + String.valueOf(seconds % 60) + "s";
			} else {
				timingStr = String.valueOf((int) Math.floor(minutes)) + "m "
				        + String.valueOf(seconds % 60) + "s";
			}
		} else {
			timingStr = String.valueOf(seconds) + "s";
		}
		return timingStr;
	}

	/**
	 * Writes a line containing info about the file
	 * 
	 * @param writer
	 *            the file writer
	 * @param fileTiming
	 *            the file timing
	 * @throws IOException
	 *             the IO exception
	 */
	private void writeFileInfo(FileWriter writer, FileTiming fileTiming)
	        throws IOException {

		writer.append("File");
		writer.append(SEPARATOR);
		writer.append(fileTiming.getName());
		writer.append(LINE_SEPARATOR);
		writer.append("Total Time");
		writer.append(SEPARATOR);
		writer.append(getTimeString(fileTiming.getTiming()));
		writer.append(LINE_SEPARATOR);
	}

	/**
	 * Gets the report file name
	 * 
	 * @param name
	 *            the report file name.
	 * @return the report file name
	 */
	private String getReportFileName(String name) {

		return name + FILE_NAME_SUFFIX;
	}

}

/**
 * Comparator for Segment Timings.
 */
class SegmentComparator implements Comparator<SegmentTiming> {

	/**
	 * Segment s1 is smaller than Segment s2 if s1 segment number is lesser than
	 * s2 segment number.
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(SegmentTiming o1, SegmentTiming o2) {

		return new Integer(o1.getName()).compareTo(new Integer(o2.getName()));
	}

}