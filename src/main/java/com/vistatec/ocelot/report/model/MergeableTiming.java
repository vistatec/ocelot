package com.vistatec.ocelot.report.model;

/**
 * This interface provides a merge method for merging timing associated to the
 * same resource.
 */
public interface MergeableTiming {

	/**
	 * Gets the resource name.
	 * 
	 * @return the resource name.
	 */
	String getName();

	/**
	 * Gets the timing.
	 * 
	 * @return the timing.
	 */
	double getTiming();

	/**
	 * Merges this timing object with the one passed as parameter.
	 * 
	 * @param timingObject
	 *            the timing object
	 */
	void merge(MergeableTiming timingObject);
}
