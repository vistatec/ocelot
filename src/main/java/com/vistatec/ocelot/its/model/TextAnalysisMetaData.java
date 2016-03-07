package com.vistatec.ocelot.its.model;

import java.util.HashMap;
import java.util.Map;

import com.vistatec.ocelot.rules.DataCategoryField;

/**
 * Text-Analysis Meta Data representing an entity enrichment.
 */
public class TextAnalysisMetaData extends EnrichmentMetaData {

	/** The entity: the enriched part of the text. */
	private String entity;

	/** The text-analysis taIdentRef value. */
	private String taIdentRef;

	/** The text-analysis taClassRef value. */
	private String taClassRef;

	/** The text-analysis confidence value. */
	private Double taConfidence;

	/** The text-analysis annotators ref. value. */
	private String taAnnotatorsRef;

	/**
	 * Gets the entity.
	 * 
	 * @return the entity.
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Sets the entity.
	 * 
	 * @param entity
	 *            the entity.
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * Gets the text-analysis taIdentRef value.
	 * 
	 * @return the text-analysis taIdentRef value.
	 */
	public String getTaIdentRef() {
		return taIdentRef;
	}

	/**
	 * Sets the text-analysis taIdentRef value.
	 * 
	 * @param taIdentRef
	 *            the text-analysis taIdentRef value.
	 */
	public void setTaIdentRef(String taIdentRef) {
		this.taIdentRef = taIdentRef;
	}

	/**
	 * Gets the text-analysis taClassRef value.
	 * 
	 * @return the text-analysis taClassRef value.
	 */
	public String getTaClassRef() {
		return taClassRef;
	}

	/**
	 * Sets the text-analysis taClassRef value.
	 * 
	 * @param taClassRef
	 *            the text-analysis taClassRef value.
	 */
	public void setTaClassRef(String taClassRef) {
		this.taClassRef = taClassRef;
	}

	/**
	 * Gets the text-analysis confidence.
	 * 
	 * @return the text-analysis confidence.
	 */
	public Double getTaConfidence() {
		return taConfidence;
	}

	/**
	 * Sets the text-analysis confidence.
	 * 
	 * @param taConfidence
	 *            the text-analysis confidence.
	 */
	public void setTaConfidence(Double taConfidence) {
		this.taConfidence = taConfidence;
	}

	/**
	 * Gets the text-analysis annotators ref.
	 * 
	 * @return the text-analysis annotators ref.
	 */
	public String getTaAnnotatorsRef() {
		return taAnnotatorsRef;
	}

	/**
	 * Sets the text-analysis annotators ref.
	 * 
	 * @param taAnnotatorsRef
	 *            the text-analysis annotators ref.
	 */
	public void setTaAnnotatorsRef(String taAnnotatorsRef) {
		this.taAnnotatorsRef = taAnnotatorsRef;
	}

	/**
	 * Checks if this meta data is empty.
	 * 
	 * @return <code>true</code> if it is empty; <code>false</code> otherwise.
	 */
	public boolean isEmpty() {

		return taIdentRef == null && taAnnotatorsRef == null
				&& taClassRef == null && taConfidence == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.its.model.ITSMetadata#getFieldValues()
	 */
	@Override
	public Map<DataCategoryField, Object> getFieldValues() {
		Map<DataCategoryField, Object> map = new HashMap<DataCategoryField, Object>();
		map.put(DataCategoryField.TA_CLASS_REF, taClassRef);
		map.put(DataCategoryField.TA_CONFIDENCE, taConfidence);
		map.put(DataCategoryField.TA_ENTITY, entity);
		map.put(DataCategoryField.TA_IDENT_REF, taIdentRef);
		map.put(DataCategoryField.ANNOTATORS_REF, taAnnotatorsRef);
		return map;
	}

	/**
	 * Merge this meta data with that passed as parameter.
	 * 
	 * @param metaData
	 *            the meta data.
	 */
	public void merge(TextAnalysisMetaData metaData) {

		if (metaData.getTaAnnotatorsRef() != null) {
			this.taAnnotatorsRef = metaData.getTaAnnotatorsRef();
		}
		if (metaData.getTaClassRef() != null) {
			this.taClassRef = metaData.getTaClassRef();
		}
		if (metaData.getTaConfidence() != null) {
			this.taConfidence = metaData.getTaConfidence();
		}
		if (metaData.getTaIdentRef() != null) {
			this.taIdentRef = metaData.getTaIdentRef();
		}
	}
}
