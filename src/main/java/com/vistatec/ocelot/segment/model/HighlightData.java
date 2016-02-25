package com.vistatec.ocelot.segment.model;


/**
 * This class contains data of a highlighted string contained in a text atom.
 */
public class HighlightData {

	/** The atom index. */
	private int atomIndex;

	/** The start and end indices of the highlighted string. */
	private int[] highlightIndices;


	/**
	 * Default constructor.
	 */
	public HighlightData() {
	}

	/**
	 * Constructor.
	 * 
	 * @param atomIndex
	 *            the atom index.
	 * @param highlightIndices
	 *            the start and end indices of the highlighted string.
	 */
	public HighlightData(int atomIndex, int[] highlightIndices) {

		this.atomIndex = atomIndex;
		this.highlightIndices = highlightIndices;
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
	 * Sets the atom index.
	 * 
	 * @param atomIndex
	 *            the atom index.
	 */
	public void setAtomIndex(int atomIndex) {
		this.atomIndex = atomIndex;
	}

	/**
	 * Gets the start and end indices of the highlighted string.
	 * 
	 * @return the start and end indices of the highlighted string.
	 */
	public int[] getHighlightIndices() {
		return highlightIndices;
	}

	/**
	 * Sets the start and end indices of the highlighted string.
	 * 
	 * @param highlightIndices
	 *            the start and end indices of the highlighted string.
	 */
	public void setHighlightIndices(int[] highlightIndices) {
		this.highlightIndices = highlightIndices;
	}

}
