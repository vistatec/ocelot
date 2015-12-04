package com.vistatec.ocelot.segment.model.enrichment;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Statement;

/**
 * The terminology enrichment class.
 */
public class TerminologyEnrichment extends Enrichment {

	/** The marker tag. */
	private final static String MARKER_TAG = "mrk";

	/** The tag type. */
	private final static String ITS_TAG_TYPE = "term";

	/** The annotators ref prefix. */
	private final static String ANNOTATOR_REF_PREFIX = "terminology|";

	/** The term info ref tag. */
	private final static String TAG = "ref";

	/** The term info ref value. */
	private String termInfoRef;

	/** The term triples. */
	private List<Statement> termTriples;

	/** The sense. */
	private String sense;

	/** The term definition. */
	private String definition;

	/** The source term. */
	private String sourceTerm;

	/** The target term. */
	private String targetTerm;

	/** The annotator value. */
	private String annotator;

	/**
	 * Constructor.
	 */
	public TerminologyEnrichment() {
		super(Enrichment.TERMINOLOGY_TYPE);
	}

	/**
	 * Constructor.
	 * 
	 * @param nifOffsetString
	 *            the NIF offset string.
	 */
	public TerminologyEnrichment(String nifOffsetString) {
		super(Enrichment.TERMINOLOGY_TYPE, nifOffsetString);
	}

	/**
	 * Constructor.
	 * 
	 * @param nifOffsetString
	 *            the NIF offset string.
	 * @param sourceTerm
	 *            the source term.
	 * @param targetTerm
	 *            the target term.
	 * @param sense
	 *            the sense.
	 * @param definition
	 *            the definition           
	 */
	public TerminologyEnrichment(String nifOffsetString, String sourceTerm,
			String targetTerm, String sense, String definition) {

		super(Enrichment.TERMINOLOGY_TYPE, nifOffsetString);
		this.sourceTerm = sourceTerm;
		this.targetTerm = targetTerm;
		this.sense = sense;
		this.definition = definition;
	}

	/**
	 * Constructor.
	 * 
	 * @param nifOffsetString
	 *            the NIF offset string.
	 * @param sourceTerm
	 *            the source term
	 * @param targetTerm
	 *            the target term
	 * @param sense
	 *            the sense
	 * @param definition
	 *            the definition
	 * @param termTriples
	 *            the term triples
	 * @param termInfoRef
	 *            the term info ref
	 */
	public TerminologyEnrichment(String nifOffsetString, String sourceTerm,
			String targetTerm, String sense, String defintion,
			List<Statement> termTriples, String termInfoRef) {

		super(Enrichment.TERMINOLOGY_TYPE, nifOffsetString);
		this.sourceTerm = sourceTerm;
		this.targetTerm = targetTerm;
		this.sense = sense;
		this.definition = defintion;
		this.termTriples = termTriples;
		this.termInfoRef = termInfoRef;
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
		return TAG;
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
	 * Gets the definition.
	 * 
	 * @return the definition.
	 */
	public String getDefinition() {
		return definition;
	}

	/**
	 * Sets the definition.
	 * 
	 * @param definition
	 *            the definition.
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
	}

	/**
	 * Gets the source term.
	 * 
	 * @return the source term.
	 */
	public String getSourceTerm() {
		return sourceTerm;
	}

	/**
	 * Sets the source term.
	 * 
	 * @param sourceTerm
	 *            the source term.
	 */
	public void setSourceTerm(String sourceTerm) {
		this.sourceTerm = sourceTerm;
	}

	/**
	 * Gets the target term.
	 * 
	 * @return the target term.
	 */
	public String getTargetTerm() {
		return targetTerm;
	}

	/**
	 * Sets the target term.
	 * 
	 * @param targetTerm
	 *            the target term.
	 */
	public void setTargetTerm(String targetTerm) {
		this.targetTerm = targetTerm;
	}

	/**
	 * Gets the annotator.
	 * 
	 * @return the annotator.
	 */
	public String getAnnotator() {
		return annotator;
	}

	/**
	 * Sets the annotator.
	 * 
	 * @param annotator
	 *            the annotator.
	 */
	public void setAnnotator(String annotator) {
		this.annotator = annotator;
	}

	/**
	 * Gets the annotrators ref value.
	 * 
	 * @return the annotrators ref value.
	 */
	public String getAnnotatorsRefValue() {
		String annotatorRef = "";
		if (annotator != null) {
			annotatorRef = ANNOTATOR_REF_PREFIX + annotator;
		}
		return annotatorRef;
	}

	/**
	 * Sets the term triples list.
	 * 
	 * @param termTriples
	 *            the term triples list.
	 */
	public void setTermTriples(List<Statement> termTriples) {
		this.termTriples = termTriples;
	}

	/**
	 * Gets the term triples list.
	 * 
	 * @return the term triples list.
	 */
	public List<Statement> getTermTriples() {
		return termTriples;
	}

	/**
	 * Sets the term info ref.
	 * 
	 * @param termInfoRef
	 *            the term info ref.
	 */
	public void setTermInfoRef(String termInfoRef) {
		this.termInfoRef = termInfoRef;
	}

	/**
	 * Get the term info ref.
	 * 
	 * @return the term info ref.
	 */
	public String getTermInfoRef() {
		return termInfoRef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder descrString = new StringBuilder();
		descrString.append("Source: ");
		descrString.append(sourceTerm);
		if (targetTerm != null) {
			descrString.append(" - Target: ");
			descrString.append(targetTerm);
		}
//		if (sense != null) {
//			descrString.append(" - Sense: ");
//			descrString.append(sense);
//		}
		return descrString.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		boolean retValue = false;
		if (obj instanceof TerminologyEnrichment) {
			TerminologyEnrichment enrich = (TerminologyEnrichment) obj;
			if (sourceTerm != null) {
				retValue = sourceTerm.equals(enrich.getSourceTerm())
						&& (targetTerm == null
								&& enrich.getTargetTerm() == null || (targetTerm
									.equals(enrich.getTargetTerm())))
						&& (sense == null && enrich.getSense() == null || (sense
								.equals(enrich.getSense())));
			} else {
				retValue = super.equals(obj);
			}
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
		StringBuilder totString = new StringBuilder(sourceTerm);
		if (targetTerm != null) {
			totString.append(targetTerm);
		}
		if (sense != null) {
			totString.append(sense);
		}
		return totString.toString().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.segment.model.enrichment.Enrichment#getTagValue()
	 */
	@Override
	public String getTagValue() {
		return termInfoRef;
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

}
