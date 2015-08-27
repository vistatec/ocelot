package com.vistatec.ocelot.segment.model;

public class EntityEnrichment extends Enrichment {

	private final static String ITS_TAG = "its:taIdentRef";

	private final static String ITS_TAG_TYPE = "its:any";

	private final static String ITS_ANNOTATORS_REF_VALUE_STR = "$annotRefStr$";

	private final static String ITS_ANNOTATORS_REF_ATTR_NAME = "its:annotatorsRef";

	private final static String ITS_ANNOTATORS_REF_VALUE = "text-analysis|"
	        + ITS_ANNOTATORS_REF_VALUE_STR;

	private final static String MARKER_TAG = "mrk";
	
	private final static String DBPEDIA_ANNOTATOR = "http://spotlight.dbpedia.org/";

	public final static String ENRICHMENT_TYPE = "entity";

	private String entityURL;

	public EntityEnrichment(String nifOffsetString, String entityURL) {
		super(nifOffsetString);
		this.entityURL = entityURL;
		// icon =
	}

	public String getEntityURL() {
		return entityURL;
	}

	@Override
	public String getTagType() {
		return ITS_TAG_TYPE;
	}

	@Override
	public String getTag() {
		return ITS_TAG; //+ "=\"" + entityURL + "\"";
	}

	@Override
	public String toString() {

		return entityURL;
	}

	@Override
	public String getType() {
		return ENRICHMENT_TYPE;
	}

	public String getAnnotatorsRefAttribute(){
		return ITS_ANNOTATORS_REF_ATTR_NAME;
	}
	
	public String getAnnotatorsRefValue() {

		String annotatorRef = "";
		if (entityURL.contains("dbpedia.org")) {
			annotatorRef = ITS_ANNOTATORS_REF_VALUE.replace(
			        ITS_ANNOTATORS_REF_VALUE_STR, DBPEDIA_ANNOTATOR);
		}
		return annotatorRef;
	}

	@Override
    public String getTagValue() {
	    return entityURL;
    }

	@Override
    public String getMarkerTag() {
	    return MARKER_TAG;
    }

}
