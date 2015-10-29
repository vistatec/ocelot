package com.vistatec.ocelot.its.model;

import java.util.HashMap;
import java.util.Map;

import com.vistatec.ocelot.rules.DataCategoryField;

/**
 * Meta Data representing a terminology enrichment.
 */
public class TerminologyMetaData extends EnrichmentMetaData {

	/** The term: the enriched part of the text. */
	private String term;

	/** The found term in the source language. */
	private String termSource;

	/** The found term in the target langueage. */
	private String termTarget;

	/** The sense of the term. */
	private String sense;

	/** The term confidence. */
	private Double confidence;

	/** The term annotators ref. */
	private String annotatorsRef;

	/**
	 * Gets the term.
	 * 
	 * @return the term.
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * Sets the term.
	 * 
	 * @param term
	 *            the term.
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * Gets the term in the source language.
	 * 
	 * @return the term in the source language.
	 */
	public String getTermSource() {
		return termSource;
	}

	/**
	 * Sets the term in the source language.
	 * 
	 * @param termSource
	 *            the term in the source language.
	 */
	public void setTermSource(String termSource) {
		this.termSource = termSource;
	}

	/**
	 * Gets the term in the target language.
	 * 
	 * @return the term in the target language.
	 */
	public String getTermTarget() {
		return termTarget;
	}

	/**
	 * Sets the term in the target language.
	 * 
	 * @param termTarget
	 *            the term in the target language.
	 */
	public void setTermTarget(String termTarget) {
		this.termTarget = termTarget;
	}

	/**
	 * Gets the sense.
	 * 
	 * @return the sense.
	 */
	public String getSense() {
		return sense;
	}

	/**
	 * Sets the sense.
	 * 
	 * @param sense
	 *            the sense.
	 */
	public void setSense(String sense) {
		this.sense = sense;
	}

	/**
	 * Gets the term confidence.
	 * 
	 * @return the term confidence.
	 */
	public Double getConfidence() {
		return confidence;
	}

	/**
	 * Sets the term confidence.
	 * 
	 * @param confidence
	 *            the term confidence.
	 */
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	/**
	 * Gets the annotators ref.
	 * 
	 * @return the annotators ref.
	 */
	public String getAnnotatorsRef() {
		return annotatorsRef;
	}

	/**
	 * Sets the annotators ref.
	 * 
	 * @param annotatorsRef
	 *            the annotators ref.
	 */
	public void setAnnotatorsRef(String annotatorsRef) {
		this.annotatorsRef = annotatorsRef;
	}

	@Override
	public Map<DataCategoryField, Object> getFieldValues() {

		Map<DataCategoryField, Object> map = new HashMap<DataCategoryField, Object>();
		map.put(DataCategoryField.TERM, term);
		map.put(DataCategoryField.TERM_CONFIDENCE, confidence);
		map.put(DataCategoryField.TERM_SENSE, sense);
		map.put(DataCategoryField.TERM_SOURCE, termSource);
		map.put(DataCategoryField.TERM_TARGET, termTarget);
		map.put(DataCategoryField.ANNOTATORS_REF, annotatorsRef);
		return map;
	}

	public void merge(TerminologyMetaData metaData) {

		if (metaData.getAnnotatorsRef() != null) {
			this.annotatorsRef = metaData.getAnnotatorsRef();
		}
		if (metaData.getConfidence() != null) {
			this.confidence = metaData.getConfidence();
		}
		if (metaData.getSense() != null) {
			this.sense = metaData.getSense();
		}
		if (metaData.getTermTarget() != null) {
			this.termTarget = metaData.getTermTarget();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		boolean retValue = false;
		if (obj instanceof TerminologyMetaData) {
			TerminologyMetaData metaData = (TerminologyMetaData) obj;
			retValue = this.getTerm().equals(metaData.getTerm())
					&& (this.termSource != null && this.termSource
							.equals(metaData.getTermSource())
									|| (this.termSource == null && metaData
											.getTermSource() == null));
		} else {
			retValue = super.equals(obj);
		}
		return retValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (term + termSource).hashCode();
	}
}
