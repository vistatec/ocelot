package com.vistatec.ocelot.segment.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class LinkEnrichment extends Enrichment {

	public final static String ENRICHMENT_TYPE = "link";

	private final static String MARKER_TAG = "ex:json-ld";

	private final static String TAG_NAME = "xmlns:ex";

	private final static String TAG_VALUE = "http://example.com";

	// private final static String ENTITY_NAME_PROP = "";
	//
	// private final static String SHORT_DESCR_PROP = "";
	//
	// private final static String LONG_DESCR_PROP = "";
	//
	// private final static String IMAGE_PROP = "";
	//
	// private final static String HOME_PAGE_PROP = "";
	//
	// private final static String WIKI_PAGE_PROP = "";

	// private String url;
	//
	// private String value;

	private String referenceEntity;

	private LinkInfoData entityName;

	private LinkInfoData shortDescription;

	private LinkInfoData longDescription;

	private List<LinkInfoData> infoList;

	private LinkInfoData imageURL;

	private Image image;

	private LinkInfoData homePage;

	private LinkInfoData wikiPage;

	private Map<String, String> context;

	// private List<String> links;

	public LinkEnrichment(String nifOffsetString) {
		super(nifOffsetString);
		// this.url = url;
		// this.value = value;
	}

	@Override
	public String getTagType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTag() {
		return TAG_NAME;
	}

	@Override
	public String getType() {

		return ENRICHMENT_TYPE;
	}

	public LinkInfoData getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName, String propName) {
		this.entityName = new LinkInfoData(propName, null, String.class);
		this.entityName.setValue(entityName);
	}

	public LinkInfoData getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription, String propName) {
		this.shortDescription = new LinkInfoData(propName, null, String.class);
		this.shortDescription.setValue(shortDescription);
	}

	public LinkInfoData getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription, String propName) {
		this.longDescription = new LinkInfoData(propName, null, String.class);
		this.longDescription.setValue(longDescription);
	}

	public List<LinkInfoData> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<LinkInfoData> infoList) {
		this.infoList = infoList;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public List<String> getLinks() {

		List<String> links = new ArrayList<String>();
		if (wikiPage != null) {
			links.add(wikiPage.getValue());
		}
		if (homePage != null) {
			links.add(homePage.getValue());
		}

		return links;
	}

	// public void setLinks(List<String> links) {
	// this.links = links;
	// }

	public void setHomePage(String homePage, String propName) {
		this.homePage = new LinkInfoData(propName, null, String.class);
		this.homePage.setValue(homePage);
	}

	public LinkInfoData getHomePage() {
		return homePage;
	}

	public void setWikiPage(String wikiPage, String propName) {
		this.wikiPage = new LinkInfoData(propName, null, String.class);
		this.wikiPage.setValue(wikiPage);
	}

	public LinkInfoData getWikiPage() {
		return wikiPage;
	}

	public void setReferenceEntity(String referenceEntity) {
		this.referenceEntity = referenceEntity;
	}

	public String getReferenceEntity() {
		return referenceEntity;
	}

	public LinkInfoData getImageURL() {

		return imageURL;
	}

	public void setImageURL(String imageURL, String propName) {
		this.imageURL = new LinkInfoData(propName, null, String.class);
	}

	public void setContext(final Map<String, String> context) {
		this.context = context;
	}

	public Map<String, String> getContext() {
		return context;
	}

	// public String getUrl(){
	// return url;
	// }
	//
	// public String getValue(){
	// return value;
	// }

	@Override
	public String toString() {

		return entityName.getValue();
	}

	@Override
	public String getTagValue() {
		// TODO Auto-generated method stub
		return TAG_VALUE;
	}

	@Override
	public String getMarkerTag() {
		// TODO Auto-generated method stub
		return MARKER_TAG;
	}

	public Model getPropertiesModel() {

		Model model = ModelFactory.createDefaultModel();
		if (context != null && !context.isEmpty()) {
			model.setNsPrefixes(context);
		}
		Resource resource = model.createResource(referenceEntity);
		if (entityName != null) {
			model.add(resource, model.createProperty(entityName.getPropName()),
			        entityName.getValue());
		}
		if (shortDescription != null) {
			model.add(resource,
			        model.createProperty(shortDescription.getPropName()),
			        shortDescription.getValue());
		}
		if (longDescription != null) {
			model.add(resource,
			        model.createProperty(longDescription.getPropName()),
			        longDescription.getValue());
		}
		if(imageURL != null){
			model.add(resource,
			        model.createProperty(imageURL.getPropName()),
			        imageURL.getValue());
		}
		if(homePage != null){
			model.add(resource,
			        model.createProperty(homePage.getPropName()),
			        homePage.getValue());
		}
		if(wikiPage != null){
			model.add(resource,
			        model.createProperty(wikiPage.getPropName()),
			        wikiPage.getValue());
		}
		if(infoList != null){
			for(LinkInfoData info: infoList){
				model.add(resource,
				        model.createProperty(info.getPropName()),
				        info.getValue());
			}
		}
		return model;
	}
}
