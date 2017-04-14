package com.vistatec.ocelot.lqi.model;

import java.util.Objects;

/**
 * The LQI severity object.
 */
public class LQISeverity {

	/** The severity name. */
	private String name;

	/** The severity score. */
	private double score;

	/**
	 * Default constructor.
	 */
	public LQISeverity() {

	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the severity name.
	 * @param score
	 *            the severity score.
	 */
	public LQISeverity(String name, double score) {

		this.name = name;
		this.score = score;
	}

	/**
	 * Sets the severity name.
	 * 
	 * @param name
	 *            the severity name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the severity name.
	 * 
	 * @return the severity name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the severity score.
	 * 
	 * @param score
	 *            the severity score.
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * Gets the severity score.
	 * 
	 * @return the severity score.
	 */
	public double getScore() {

		return score;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {

		return new LQISeverity(name, score);
	}
	
	@Override
	public String toString() {
	    return name + " - " + score;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof LQISeverity) {
			LQISeverity sevObj = (LQISeverity)obj;
			return (name != null ? name.equals(sevObj.name): sevObj == null) && score == sevObj.score;
			
		} else {
			return super.equals(obj);
		}
	}
	
	@Override
	public int hashCode() {
	    return Objects.hash(name, score);
	}
}
