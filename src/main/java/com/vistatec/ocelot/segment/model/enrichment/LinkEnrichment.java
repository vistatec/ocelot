package com.vistatec.ocelot.segment.model.enrichment;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Link enrichment class.
 */
public class LinkEnrichment extends Enrichment {

	/** The marker tag. */
	public final static String MARKER_TAG = "ex:json-ld";

	/** The tag name. */
	private final static String TAG_NAME = "xmlns:ex";

	/** The tag value. */
	private final static String TAG_VALUE = "http://example.com";

	/** The reference entity. */
	private String referenceEntity;

	/** The entity name. */
	private LinkInfoData entityName;

	/** The short description. */
	private LinkInfoData shortDescription;

	/** The long description. */
	private LinkInfoData longDescription;

	/** The list of info data. */
	private List<LinkInfoData> infoList;

	/** The image URL. */
	private LinkInfoData imageURL;

	/** The image. */
	private Image image;

	/** The homepage link. */
	private LinkInfoData homePage;

	/** The wikipedia link. */
	private LinkInfoData wikiPage;

	/** The context for the triples model. */
	private Map<String, String> context;

	/**
	 * Constructor.
	 * 
	 * @param nifOffsetString
	 *            the NIF offset string.
	 */
	public LinkEnrichment(String nifOffsetString) {
		super(Enrichment.LINK_TYPE, nifOffsetString);
	}

	/**
	 * Constructor.
	 * 
	 * @param offsetStartIndex
	 *            the offset start index.
	 * @param offsetEndIndex
	 *            the offset end index.
	 */
	public LinkEnrichment(int offsetStartIndex, int offsetEndIndex) {
		super(Enrichment.LINK_TYPE, offsetStartIndex, offsetEndIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.segment.model.enrichment.Enrichment#getTagType()
	 */
	@Override
	public String getTagType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.segment.model.enrichment.Enrichment#getTag()
	 */
	@Override
	public String getTag() {
		return TAG_NAME;
	}

	/**
	 * Gets the entity name.
	 * 
	 * @return the entity name.
	 */
	public LinkInfoData getEntityName() {
		return entityName;
	}

	/**
	 * Sets the entity name.
	 * 
	 * @param entityName
	 *            the entity name.
	 * @param propName
	 *            the entity property name.
	 */
	public void setEntityName(String entityName, String propName) {
		this.entityName = new LinkInfoData(propName, null, String.class);
		this.entityName.setValue(entityName);
	}

	/**
	 * Gets the short description.
	 * 
	 * @return the short description.
	 */
	public LinkInfoData getShortDescription() {
		return shortDescription;
	}

	/**
	 * Sets the the short description.
	 * 
	 * @param shortDescription
	 *            the short description.
	 * @param propName
	 *            the short description property name.
	 */
	public void setShortDescription(String shortDescription, String propName) {
		this.shortDescription = new LinkInfoData(propName, null, String.class);
		this.shortDescription.setValue(shortDescription);
	}

	/**
	 * Gets the long description.
	 * 
	 * @return the long description.
	 */
	public LinkInfoData getLongDescription() {
		return longDescription;
	}

	/**
	 * Sets the long description.
	 * 
	 * @param longDescription
	 *            the long description.
	 * @param propName
	 *            the long description property name.
	 */
	public void setLongDescription(String longDescription, String propName) {
		this.longDescription = new LinkInfoData(propName, null, String.class);
		this.longDescription.setValue(longDescription);
	}

	/**
	 * Gets the list of info data.
	 * 
	 * @return the list of info data.
	 */
	public List<LinkInfoData> getInfoList() {
		return infoList;
	}

	/**
	 * Sets the list of info data.
	 * 
	 * @param infoList
	 *            the list of info data.
	 */
	public void setInfoList(List<LinkInfoData> infoList) {
		this.infoList = infoList;
	}

	/**
	 * Gets the image.
	 * 
	 * @return the image.
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Sets the image.
	 * 
	 * @param image
	 *            the image.
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * Gets the home page and wikipedia links.
	 * 
	 * @return the links.
	 */
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

	/**
	 * Sets the home page link.
	 * 
	 * @param homePage
	 *            the home page link.
	 * @param propName
	 *            the home page property name.
	 */
	public void setHomePage(String homePage, String propName) {
		this.homePage = new LinkInfoData(propName, null, String.class);
		this.homePage.setValue(homePage);
	}

	/**
	 * Gets the home page link.
	 * 
	 * @return the home page link.
	 */
	public LinkInfoData getHomePage() {
		return homePage;
	}

	/**
	 * Sets the wikipedia page link.
	 * 
	 * @param wikiPage
	 *            the wikipedia page link.
	 * @param propName
	 *            the wikipedia page property name.
	 */
	public void setWikiPage(String wikiPage, String propName) {
		this.wikiPage = new LinkInfoData(propName, null, String.class);
		this.wikiPage.setValue(wikiPage);
	}

	/**
	 * Gets the wikipedia page link.
	 * 
	 * @return the wikipedia page link.
	 */
	public LinkInfoData getWikiPage() {
		return wikiPage;
	}

	/**
	 * Sets the referenced entity.
	 * 
	 * @param referenceEntity
	 *            the referenced entity.
	 */
	public void setReferenceEntity(String referenceEntity) {
		this.referenceEntity = referenceEntity;
	}

	/**
	 * Gets the referenced entity.
	 * 
	 * @return the referenced entity.
	 */
	public String getReferenceEntity() {
		return referenceEntity;
	}

	/**
	 * Gets the image URL.
	 * 
	 * @return the image URL.
	 */
	public LinkInfoData getImageURL() {

		return imageURL;
	}

	/**
	 * Sets the image URL.
	 * 
	 * @param imageURL
	 *            the image URL.
	 * @param propName
	 *            the image URL property name.
	 */
	public void setImageURL(String imageURL, String propName) {
		this.imageURL = new LinkInfoData(propName, null, String.class);
		this.imageURL.setValue(imageURL);
	}

	/**
	 * Sets the context for the triples model.
	 * 
	 * @param context
	 *            the context for the triples model.
	 */
	public void setContext(final Map<String, String> context) {
		this.context = context;
	}

	/**
	 * Gets the context for the triples model.
	 * 
	 * @return the context for the triples model.
	 */
	public Map<String, String> getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return entityName.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.segment.model.enrichment.Enrichment#getTagValue()
	 */
	@Override
	public String getTagValue() {
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
		if (imageURL != null) {
			model.add(resource, model.createProperty(imageURL.getPropName()),
					imageURL.getValue());
		}
		if (homePage != null) {
			model.add(resource, model.createProperty(homePage.getPropName()),
					homePage.getValue());
		}
		if (wikiPage != null) {
			model.add(resource, model.createProperty(wikiPage.getPropName()),
					wikiPage.getValue());
		}
		if (infoList != null) {
			for (LinkInfoData info : infoList) {
				model.add(resource, model.createProperty(info.getPropName()),
						info.getValue());
			}
		}
		return model;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof LinkEnrichment) {
			return referenceEntity
					.equals(((LinkEnrichment) obj).referenceEntity)
					&& offsetStartIdx == ((LinkEnrichment) obj).offsetStartIdx
					&& offsetEndIdx == ((LinkEnrichment) obj).offsetEndIdx;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public int hashCode() {
		return 31 * referenceEntity.hashCode() * offsetStartIdx * offsetEndIdx;
	}
}
