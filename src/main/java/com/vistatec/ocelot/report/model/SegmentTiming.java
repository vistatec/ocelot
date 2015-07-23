package com.vistatec.ocelot.report.model;

/**
 * This class represents timing for a single Ocelot segment.
 */
public class SegmentTiming implements MergeableTiming {

	/** The segment number */
	private String segNumber;

	/** the timing. */
	private double timing;

	/**
	 * Constructor
	 * 
	 * @param segNumber
	 *            the segment Number
	 * @param timing
	 *            the timing
	 */
	public SegmentTiming(final String segNumber, final double timing) {
		this.segNumber = segNumber;
		this.timing = timing;
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
		if (this.segNumber.equals(timing.getName())) {
			this.timing += this.timing + timing.getTiming();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.report.model.MergeableTiming#getSegNumber()
	 */
	public String getName() {
		return segNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.report.model.MergeableTiming#getTiming()
	 */
	public double getTiming() {
		return timing;
	}

	/**
	 * Two segments are logically the same one if they have the same segment
	 * number. The assumption is that they are from the same file.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		boolean retValue = false;
		if (obj instanceof SegmentTiming) {
			retValue = this.segNumber.equals(((SegmentTiming) obj).getName());
		} else {
			retValue = super.equals(obj);
		}
		return retValue;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Integer.parseInt(segNumber);
	}

}
