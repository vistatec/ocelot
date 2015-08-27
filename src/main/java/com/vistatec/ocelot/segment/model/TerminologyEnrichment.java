package com.vistatec.ocelot.segment.model;

public class TerminologyEnrichment extends Enrichment {

//	private final static String ITS_TAG = "its:term";

	private final static String MARKER_TAG = "mrk";
	
	private final static String ITS_TAG_TYPE = "term";

	public final static String ENRICHMENT_TYPE = "term";

	private String sense;

	private String sourceTerm;

	private String targetTerm;

	public TerminologyEnrichment(String nifOffsetString) {
		super(nifOffsetString);
	}

	public TerminologyEnrichment(String nifOffsetString, String sourceTerm,
	        String targetTerm, String sense) {

		super(nifOffsetString);
		this.sourceTerm = sourceTerm;
		this.targetTerm = targetTerm;
		this.sense = sense;
	}

	@Override
	public String getTagType() {
		return ITS_TAG_TYPE;
	}

	@Override
	public String getTag() {
		return null;
	}

	@Override
	public String getType() {
		return ENRICHMENT_TYPE;
	}

	public String getSense() {
		return sense;
	}

	public void setSense(String sense) {
		this.sense = sense;
	}

	public String getSourceTerm() {
		return sourceTerm;
	}

	public void setSourceTerm(String sourceTerm) {
		this.sourceTerm = sourceTerm;
	}

	public String getTargetTerm() {
		return targetTerm;
	}

	public void setTargetTerm(String targetTerm) {
		this.targetTerm = targetTerm;
	}

	@Override
	public String toString() {

		StringBuilder descrString = new StringBuilder();
		descrString.append("Source: ");
		descrString.append(sourceTerm);
		if (targetTerm != null) {
			descrString.append(" - Target: ");
			descrString.append(targetTerm);
		}
		if (sense != null) {
			descrString.append(" - Sense: ");
			descrString.append(sense);
		}
		// return "Source: " + sourceTerm + targetTerm != null ? (" - Target: "
		// + targetTerm) : "" + "\nSense: " + sense;
		return descrString.toString();
	}

	@Override
	public boolean equals(Object obj) {

		boolean retValue = false;
		if (obj instanceof TerminologyEnrichment) {
			TerminologyEnrichment enrich = (TerminologyEnrichment) obj;
			retValue = sourceTerm.equals(enrich.getSourceTerm())
			        && (targetTerm == null && enrich.getTargetTerm() == null || (targetTerm
			                .equals(enrich.getTargetTerm())))
			        && (sense == null && enrich.getSense() == null || (sense
			                .equals(enrich.getSense())));
		} else {
			retValue = super.equals(obj);
		}
		return retValue;
	}
	
	@Override
	public int hashCode() {
		StringBuilder totString = new StringBuilder(sourceTerm);
		if(targetTerm != null){
			totString.append(targetTerm);
		}
		if(sense != null){
			totString.append(sense);
		}
	    return totString.toString().hashCode();
	}

	@Override
    public String getTagValue() {
	    return null;
    }

	@Override
    public String getMarkerTag() {
	    return MARKER_TAG;
    }
}
