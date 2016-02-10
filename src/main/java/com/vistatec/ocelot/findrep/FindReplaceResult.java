package com.vistatec.ocelot.findrep;

/**
 * Result of the searching functionality.
 */
public class FindReplaceResult {

	/** The segment index. */
	private int segmentIndex;

	/** The atom index. */
	private int atomIndex;

	/** The found string start index. */
	private int stringStartIndex;

	/** The found string end index. */
	private int stringEndIndex;

	/** The scope. */
	private int scope;

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
	 * @param scope
	 *            the scope
	 */
	public FindReplaceResult(int segmentIndex, int atomIndex,
			int stringStartIndex, int stringEndIndex, int scope) {

		this.segmentIndex = segmentIndex;
		this.atomIndex = atomIndex;
		this.stringStartIndex = stringStartIndex;
		this.stringEndIndex = stringEndIndex;
		this.scope = scope;
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
	 * @param scope
	 *            the scope.
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}

	/**
	 * Gets the scope.
	 * 
	 * @return the scope.
	 */
	public int getScope() {
		return scope;
	}
}
