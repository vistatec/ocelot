package com.vistatec.ocelot.report.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Global timing associated to a file.
 */
public class FileTiming implements MergeableTiming {

	/** the file name. */
	private String fileName;

	/** The total timing. */
	private double total;

	/** The owned segments timings. */
	private List<SegmentTiming> segments;

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 * @param total
	 *            the total timing
	 * @param segments
	 *            the segments
	 */
	public FileTiming(final String fileName, final double total,
	        final List<SegmentTiming> segments) {
		this.fileName = fileName;
		this.total = total;
		this.segments = segments;
	}

	/**
	 * Adds a segment timing.
	 * 
	 * @param segmentNumber
	 *            the segment number
	 * @param timing
	 *            the timing.
	 */
	public void addSegmentTiming(String segmentNumber, double timing) {
		if (segments == null) {
			segments = new ArrayList<SegmentTiming>();
		}
		final SegmentTiming segTiming = new SegmentTiming(segmentNumber, timing);
		addSegmentTiming(segTiming);
	}

	/**
	 * Adds a segment timing object. If this segment timing is not included in
	 * the list, then it is added. Otherwise, if the segment is included in the
	 * list, it is merged with the new timing.
	 * 
	 * @param segTiming
	 *            the segment timing object
	 */
	private void addSegmentTiming(SegmentTiming segTiming) {
		if (segments.contains(segTiming)) {
			segments.get(segments.indexOf(segTiming)).merge(segTiming);
		} else {
			segments.add(segTiming);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.report.model.MergeableTiming#getName()
	 */
	@Override
	public String getName() {
		return fileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.report.model.MergeableTiming#getTiming()
	 */
	@Override
	public double getTiming() {
		return total;
	}

	/**
	 * Gets the list of segments timings.
	 * 
	 * @return the list of segments timings.
	 */
	public List<SegmentTiming> getSegmentTimings() {
		return segments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.report.model.MergeableTiming#merge(com.vistatec.ocelot
	 * .report.model.MergeableTiming)
	 */
	@Override
	public void merge(MergeableTiming timing) {

		if (timing instanceof FileTiming) {
			if (timing.getName().equals(this.fileName)) {
				this.total += timing.getTiming();
				for (SegmentTiming segment : ((FileTiming) timing)
				        .getSegmentTimings()) {
					addSegmentTiming(segment);
				}
			}
		}
	}

	/**
	 * One file timing equals to another if they referes to the same file.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof FileTiming) {
			return fileName.equals(((FileTiming) obj).getName());
		} else {
			return super.equals(obj);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return fileName.hashCode();
	}
}
