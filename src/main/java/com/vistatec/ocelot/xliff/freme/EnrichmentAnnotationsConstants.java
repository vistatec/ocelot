package com.vistatec.ocelot.xliff.freme;

public interface EnrichmentAnnotationsConstants {

	/** Prefix used for FREME marker IDs. */
	public String MARKER_FREME_ID_PREFIX = "freme-";
	
	/** The marker tag name. */
	public String MARKER_TAG_NAME = "mrk";
	
	/** The id attribute for the marker tag. */
	public String MARKER_TAG_ID_ATTR = "id";
	
	/** The JSON marker name. */
	public String JSON_TAG_NAME = "ex:json-ld";
	
	/** The JSON marker prefix. */
	public String JSON_TAG_PREFIX = "ex";
	
	/** The JSON marker local name. */
	public String JSON_TAG_LOCAL_NAME = "json-ld";
	
	/** JSON tag domain. */
	public String JSON_TAG_DOMAIN = "http://example.com";
	
	/** JSON tag domain attribute name. */
	public String JSON_TAG_DOMAIN_ATTR = "xmlns:ex";
	
	/** JSON tag segment attribute name. */
	public String JSON_TAG_SEG_ATTR = "segment";
	
	/** JSON-LD format constant. */
	public String JSON_LD_FORMAT = "JSON-LD";
	
	/** Text-Analysis annotators ref start string. */
	public String TA_ANNOTATORS_REF_STRING = "text-analysis|";
	
	/** Terminology annotators ref start string. */
	public String TERM_ANNOTATORS_REF_STRING = "terminology|";

}
