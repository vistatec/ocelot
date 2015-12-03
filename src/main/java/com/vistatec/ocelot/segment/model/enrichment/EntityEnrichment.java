package com.vistatec.ocelot.segment.model.enrichment;

/**
 * Entity enrichment class.
 */
public class EntityEnrichment extends Enrichment {

	/** The ITS tag constant. */
	private final static String ITS_TAG = "its:taIdentRef";

	/** The tag type. */
	private final static String ITS_TAG_TYPE = "its:any";

	/** The annotators ref replace string constant. */
	private final static String ITS_ANNOTATORS_REF_VALUE_STR = "$annotRefStr$";

	/** The annotators ref attribute name. */
	private final static String ITS_ANNOTATORS_REF_ATTR_NAME = "its:annotatorsRef";

	/** The annotators ref value. */
	private final static String ITS_ANNOTATORS_REF_VALUE = "text-analysis|"
			+ ITS_ANNOTATORS_REF_VALUE_STR;

	/** The marker tag. */
	private final static String MARKER_TAG = "mrk";

	/** The default annotator. */
	private final static String DEFAULT_ANNOTATOR = "http://spotlight.dbpedia.org/";

	/** The entity URL. */
	private String entityURL;

	/** The annotator. */
	private String annotator;

	/**
	 * Cosntructor.
	 * 
	 * @param entityURL
	 *            the entity URL.
	 */
	public EntityEnrichment(String entityURL) {

		super(Enrichment.ENTITY_TYPE);
		this.entityURL = entityURL;
		this.annotator = DEFAULT_ANNOTATOR;
	}

	/**
	 * Constructor.
	 * 
	 * @param nifOffsetString
	 *            the NIF offset string.
	 * @param entityURL
	 *            the entity URL.
	 */
	public EntityEnrichment(String nifOffsetString, String entityURL) {
		super(Enrichment.ENTITY_TYPE, nifOffsetString);
		this.entityURL = entityURL;
		this.annotator = DEFAULT_ANNOTATOR;
	}

	/**
	 * Gets the entity URL>
	 * 
	 * @return the entity URL>
	 */
	public String getEntityURL() {
		return entityURL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.segment.model.enrichment.Enrichment#getTagType()
	 */
	@Override
	public String getTagType() {
		return ITS_TAG_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.segment.model.enrichment.Enrichment#getTag()
	 */
	@Override
	public String getTag() {
		return ITS_TAG; // + "=\"" + entityURL + "\"";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return entityURL;
	}

	/**
	 * Gets the annotators ref attribute.
	 * 
	 * @return the annotators ref attribute.
	 */
	public String getAnnotatorsRefAttribute() {
		return ITS_ANNOTATORS_REF_ATTR_NAME;
	}

	/**
	 * Gets the annotators ref value.
	 * 
	 * @return the annotators ref value.
	 */
	public String getAnnotatorsRefValue() {

		String annotatorRef = "";
		if (annotator == null) {
			annotator = DEFAULT_ANNOTATOR;
		}
		annotatorRef = ITS_ANNOTATORS_REF_VALUE.replace(
				ITS_ANNOTATORS_REF_VALUE_STR, annotator);
		return annotatorRef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.segment.model.enrichment.Enrichment#getTagValue()
	 */
	@Override
	public String getTagValue() {
		return entityURL;
	}

	/**
	 * Sets the annotator ref value.
	 * 
	 * @param annotator
	 *            the annotator.
	 */
	public void setAnnotatorRef(String annotator) {

		this.annotator = annotator;
	}

	/**
	 * Gets the annotators ref value.
	 * 
	 * @return the annotators ref value.
	 */
	public String getAnnotatorRef() {

		return annotator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.segment.model.enrichment.Enrichment#getMarkerTag()
	 */
	@Override
	public String getMarkerTag() {
		return MARKER_TAG;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityEnrichment) {
			return entityURL.equals(((EntityEnrichment) obj).getEntityURL())
					&& offsetStartIdx == ((EntityEnrichment) obj).offsetStartIdx
					&& offsetEndIdx == ((EntityEnrichment) obj).offsetEndIdx;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		return 31 * entityURL.hashCode() * offsetStartIdx * offsetEndIdx;
	}
}
