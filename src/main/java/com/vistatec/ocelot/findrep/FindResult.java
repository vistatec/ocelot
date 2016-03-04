package com.vistatec.ocelot.findrep;

/**
 * Result of the searching functionality.
 */
public class FindResult {

	/** The segment index. */
	private int segmentIndex;

	/** The atom index. */
	private int atomIndex;

	/** The found string start index. */
	private int stringStartIndex;

	/** The found string end index. */
	private int stringEndIndex;

	/** States if the scope is the target. */
	private boolean targetScope;

	/**
	 * Constructor.
	 * 
	 * @param segmentIndex
	 *            the segment index
	 * @param atomIndex
	 *            the atom index
	 * @param stringStartIndex
	 *            the string start index
	 * @param stringEndIndex
	 *            the string end index
	 * @param targetScope
	 *            a boolean stating if the scope is the target.
	 */
	public FindResult(int segmentIndex, int atomIndex,
			int stringStartIndex, int stringEndIndex, boolean targetScope) {

		this.segmentIndex = segmentIndex;
		this.atomIndex = atomIndex;
		this.stringStartIndex = stringStartIndex;
		this.stringEndIndex = stringEndIndex;
		this.targetScope = targetScope;
	}

	/**
	 * Gets the segment index.
	 * 
	 * @param segmentIndex
	 *            the segment index.
	 */
	public void setSegmentIndex(int segmentIndex) {
		this.segmentIndex = segmentIndex;
	}

	/**
	 * Gets the segment index.
	 * 
	 * @return the segment index.
	 */
	public int getSegmentIndex() {
		return segmentIndex;
	}

	/**
	 * Sets the atom index.
	 * 
	 * @param atomIndex
	 *            the atom index.
	 */
	public void setAtomIndex(int atomIndex) {
		this.atomIndex = atomIndex;
	}

	/**
	 * Gets the atom index.
	 * 
	 * @return the atom index.
	 */
	public int getAtomIndex() {
		return atomIndex;
	}

	/**
	 * Sets the string start index.
	 * 
	 * @param stringStartIndex
	 *            the string start index.
	 */
	public void setStringStartIndex(int stringStartIndex) {
		this.stringStartIndex = stringStartIndex;
	}

	/**
	 * Gets the string start index.
	 * 
	 * @return the string start index.
	 */
	public int getStringStartIndex() {
		return stringStartIndex;
	}

	/**
	 * Sets the string end index.
	 * 
	 * @param stringEndIndex
	 *            the string end index.
	 */
	public void setStringEndIndex(int stringEndIndex) {
		this.stringEndIndex = stringEndIndex;
	}

	/**
	 * Gets the string end index.
	 * 
	 * @return the string end index.
	 */
	public int getStringEndIndex() {
		return stringEndIndex;
	}

	/**
	 * Sets the scope.
	 * 
	 * @param targetScope
	 *            a boolean stating if the target is the scope.
	 */
	public void setTargetScope(boolean targetScope) {
		this.targetScope = targetScope;
	}

	/**
	 * Checks if the scope is on the target.
	 * 
	 * @return <code>true</code> if the target is the current scope;
	 *         <code>false</code> otherwise.
	 */
	public boolean isTargetScope() {
		return targetScope;
	}

	@Override
	public String toString() {

		return "Segment " + segmentIndex + " - Atom " + atomIndex
				+ " - Indices " + stringStartIndex + ", " + stringEndIndex;
	}
}
