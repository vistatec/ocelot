package com.vistatec.ocelot.segment.model.enrichment;

import java.awt.Image;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * This class provides constants and static methods for managing the link
 * enrichments.
 */
public abstract class ELinkEnrichmentsConstants {
    static final Logger LOG = LoggerFactory.getLogger(ELinkEnrichmentsConstants.class);

	/** The long description property. */
	public static final String LONG_DESCR_PROP = "http://dbpedia.org/ontology/abstract";

	/** The short description property. */
	public static final String SHORT_DESCR_PROP = "http://www.w3.org/2000/01/rdf-schema#comment";

	/** The image property. */
	public static final String IMAGE_PROP = "http://xmlns.com/foaf/0.1/depiction";

	/** The small image property. */
	public static final String SMALL_IMAGE_PROP = "http://dbpedia.org/ontology/thumbnail";

	/** The wikipedia link property. */
	public static final String WIKI_LINK_PROP = "http://xmlns.com/foaf/0.1/isPrimaryTopicOf";

	/** The homepage link property. */
	public static final String HOMEPAGE_LINK_PROP = "http://xmlns.com/foaf/0.1/homepage";

	/** The birthdate property. */
	public static final String BIRTHDATE_PROP = "http://dbpedia.org/ontology/birthDate";

	/** The deathdate property. */
	public static final String DEATHDATE_PROP = "http://dbpedia.org/ontology/deathDate";

	/** The hometown property. */
	public static final String HOMETOWN_PROP = "http://dbpedia.org/ontology/hometown";

	/** The birth place property. */
	public static final String BIRTH_PLACE_PROP = "http://dbpedia.org/ontology/birthPlace";

	/** The death place property. */
	public static final String DEATH_PLACE_PROP = "http://dbpedia.org/ontology/deathPlace";

	/** The description property. */
	public static final String DESCRIPTION_PROP = "http://purl.org/dc/elements/1.1/description";

	/** The area total property. */
	public static final String AREA_TOTAL_PROP = "http://dbpedia.org/ontology/PopulatedPlace/areaTotal";

	/** The population total property. */
	public static final String POPULATION_TOTAL = "http://dbpedia.org/ontology/populationTotal";

	/** The latitude property. */
	public static final String LATITUDE_PROP = "http://www.w3.org/2003/01/geo/wgs84_pos#lat";

	/** The longitude property. */
	public static final String LONGITUDE_PROP = "http://www.w3.org/2003/01/geo/wgs84_pos#long";

	/** The type property. */
	public static final String TYPE_PROP = "http://dbpedia.org/property/type";

	/** The location property. */
	public static final String LOCATION_PROP = "http://dbpedia.org/property/location";

	/** The entity name property. */
	public static final String ENTITY_NAME_PROP = "http://www.w3.org/2000/01/rdf-schema#label";

	/**
	 * Gets the info properties.
	 * 
	 * @return the info properties.
	 */
	public static List<LinkInfoData> getInfoProperties() {

		List<LinkInfoData> properties = new ArrayList<LinkInfoData>();
		properties.add(new LinkInfoData(DESCRIPTION_PROP, "Description",
		        String.class));
		properties.add(new LinkInfoData(BIRTHDATE_PROP, "Birth Date",
		        Date.class));
		properties.add(new LinkInfoData(DEATHDATE_PROP, "Death Date",
		        Date.class));
		properties
		        .add(new LinkInfoData(HOMETOWN_PROP, "Hometown", String.class));
		properties.add(new LinkInfoData(BIRTH_PLACE_PROP, "Birth Place",
		        String.class));
		properties.add(new LinkInfoData(BIRTH_PLACE_PROP, "Death Place",
		        String.class));
		properties.add(new LinkInfoData(AREA_TOTAL_PROP, "Area Total",
		        String.class, "Km2"));
		properties.add(new LinkInfoData(POPULATION_TOTAL, "Population Total",
		        Integer.class));
		properties
		        .add(new LinkInfoData(LATITUDE_PROP, "Latitude", Float.class));
		properties.add(new LinkInfoData(LONGITUDE_PROP, "Longitude",
		        Float.class));
		properties.add(new LinkInfoData(TYPE_PROP, "Type", String.class));
		properties
		        .add(new LinkInfoData(LOCATION_PROP, "Location", String.class));

		return properties;

	}

	/**
	 * Gets the triples context.
	 * 
	 * @return the triples context.
	 */
	public static Map<String, String> getContext() {

		Map<String, String> context = new HashMap<String, String>();
		context.put("dbo", "http://dbpedia.org/ontology/");
		context.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		context.put("foaf", "http://xmlns.com/foaf/0.1/");
		context.put("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		context.put("dbp", "http://dbpedia.org/property/");
		context.put("dc", "http://purl.org/dc/elements/1.1/");
		context.put("dbpedia", "http://dbpedia.org/resource/");
		return context;

	}

	/**
	 * Fills a link enrichment with properties retrieved by the triples model.
	 * 
	 * @param linkEnrichment
	 *            the link enrichment to be filled.
	 * @param linkModel
	 *            the link triples model.
	 * @param entityURL
	 *            the entity URL.
	 */
	public static void fillLinkEnrichment(LinkEnrichment linkEnrichment,
	        Model linkModel, String entityURL) {

		Resource entityRes = linkModel.createResource(entityURL);
		NodeIterator nameNodeIt = linkModel
		        .listObjectsOfProperty(
		                entityRes,
		                linkModel
		                        .createProperty(ELinkEnrichmentsConstants.ENTITY_NAME_PROP));
		linkEnrichment.setReferenceEntity(entityURL);
		RDFNode currNode = null;
		while(nameNodeIt.hasNext() && linkEnrichment.getEntityName() == null){
			currNode = nameNodeIt.next();
			if(checkLanguage(currNode, linkEnrichment.getLanguage())){
				linkEnrichment.setEntityName(currNode.asLiteral().getString(), ENTITY_NAME_PROP);
			}
		}
		if(linkEnrichment.getEntityName() == null){
			int index = entityURL.lastIndexOf("/");
			linkEnrichment.setEntityName(entityURL.substring(index + 1)
			        .replaceAll("_", " "),
			        ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		}
		
//		if (nameNodeIt.hasNext()) {
//			linkEnrichment.setEntityName(nameNodeIt.next().asLiteral()
//			        .getString(), ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
//		} else {
//			// String entityUri = entityStmt.getObject().asResource().getURI();
//			int index = entityURL.lastIndexOf("/");
//			linkEnrichment.setEntityName(entityURL.substring(index + 1)
//			        .replaceAll("_", " "),
//			        ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
//		}
		NodeIterator shortDescrNodeIt = linkModel
		        .listObjectsOfProperty(
		                entityRes,
		                linkModel
		                        .createProperty(ELinkEnrichmentsConstants.SHORT_DESCR_PROP));
		while(shortDescrNodeIt.hasNext() && linkEnrichment.getShortDescription() == null){
		
			currNode = shortDescrNodeIt.next();
			if(checkLanguage(currNode, linkEnrichment.getLanguage())){
				linkEnrichment.setShortDescription(currNode.asLiteral().getString(), SHORT_DESCR_PROP);
			}
		}
		
//		if (shortDescrNodeIt.hasNext()) {
//			linkEnrichment.setShortDescription(shortDescrNodeIt.next()
//			        .asLiteral().getString(),
//			        ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
//		}
		NodeIterator longDescrNodeIt = linkModel
		        .listObjectsOfProperty(
		                entityRes,
		                linkModel
		                        .createProperty(ELinkEnrichmentsConstants.LONG_DESCR_PROP));
		
		while(longDescrNodeIt.hasNext() && linkEnrichment.getLongDescription() == null){
			currNode = longDescrNodeIt.next();
			if(checkLanguage(currNode, linkEnrichment.getLanguage())){
				linkEnrichment.setLongDescription(currNode.asLiteral().getString(), LONG_DESCR_PROP);
			}
		}
//		if (longDescrNodeIt.hasNext()) {
//			linkEnrichment.setLongDescription(longDescrNodeIt.next()
//			        .asLiteral().getString(),
//			        ELinkEnrichmentsConstants.LONG_DESCR_PROP);
//		}

		NodeIterator imageNodeIt = linkModel
		        .listObjectsOfProperty(
		                entityRes,
		                linkModel
		                        .createProperty(ELinkEnrichmentsConstants.SMALL_IMAGE_PROP));
		if (imageNodeIt.hasNext()) {
			RDFNode imageNode = imageNodeIt.next();
			String imageURL = null;
			if (imageNode.isResource()) {
				imageURL = imageNode.asResource().getURI();
			} else {
				imageURL = imageNode.asLiteral().getString();
			}
			Image image = downloadImage(imageURL);
			if (image != null) {
				linkEnrichment.setImage(image);
				linkEnrichment.setImageURL(imageURL,
				        ELinkEnrichmentsConstants.SMALL_IMAGE_PROP);
			}
		}
		if (linkEnrichment.getImage() == null) {
			imageNodeIt = linkModel.listObjectsOfProperty(entityRes, linkModel
			        .createProperty(ELinkEnrichmentsConstants.IMAGE_PROP));
			if (imageNodeIt.hasNext()) {
				RDFNode imageNode = imageNodeIt.next();
				String imageURL = null;
				if (imageNode.isResource()) {
					imageURL = imageNode.asResource().getURI();
				} else {
					imageURL = imageNode.asLiteral().getString();
				}
				Image image = downloadImage(imageURL);
				if (image != null) {
					linkEnrichment.setImage(image);
					linkEnrichment.setImageURL(imageURL,
					        ELinkEnrichmentsConstants.IMAGE_PROP);
				}
			}
		}
		NodeIterator wikiNodeIt = linkModel
		        .listObjectsOfProperty(
		                entityRes,
		                linkModel
		                        .createProperty(ELinkEnrichmentsConstants.WIKI_LINK_PROP));
		
		while(wikiNodeIt.hasNext() && linkEnrichment.getWikiPage() == null){
			
			RDFNode wikiNode = wikiNodeIt.next();
			if(wikiNode.isResource()){
				linkEnrichment.setWikiPage(wikiNode.asResource().getURI(), WIKI_LINK_PROP);
			} else if (checkLanguage(wikiNode, linkEnrichment.getLanguage())){
				linkEnrichment.setWikiPage(wikiNode.asLiteral().getString(), WIKI_LINK_PROP);
			}
		}
//		if (wikiNodeIt.hasNext()) {
//			RDFNode wikiNode = wikiNodeIt.next();
//			if (wikiNode.isResource()) {
//				linkEnrichment.setWikiPage(wikiNode.asResource().getURI(),
//				        ELinkEnrichmentsConstants.WIKI_LINK_PROP);
//			} else {
//				linkEnrichment.setWikiPage(wikiNode.asLiteral().getString(),
//				        ELinkEnrichmentsConstants.WIKI_LINK_PROP);
//			}
//		}
		NodeIterator homePageNodeIt = linkModel
		        .listObjectsOfProperty(
		                entityRes,
		                linkModel
		                        .createProperty(ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP));
		
		while(homePageNodeIt.hasNext() && linkEnrichment.getHomePage() == null){
			RDFNode homePageNode = homePageNodeIt.next();
			if(homePageNode.isResource()){
				linkEnrichment.setHomePage(homePageNode.asResource().getURI(), HOMEPAGE_LINK_PROP);
			} else if(checkLanguage(homePageNode, linkEnrichment.getLanguage())){
				linkEnrichment.setHomePage(homePageNode.asLiteral().getString(), HOMEPAGE_LINK_PROP);
			}
		}
//		if (homePageNodeIt.hasNext()) {
//			RDFNode homePageNode = homePageNodeIt.next();
//			if (homePageNode.isResource()) {
//				linkEnrichment.setHomePage(homePageNode.asResource().getURI(),
//				        ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
//			} else {
//				linkEnrichment.setHomePage(
//				        homePageNode.asLiteral().getString(),
//				        ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
//			}
//		}
		NodeIterator infoNodeIt = null;
		List<LinkInfoData> enrichmentInfo = new ArrayList<LinkInfoData>();
		for (LinkInfoData infoProp : ELinkEnrichmentsConstants
		        .getInfoProperties()) {
			infoNodeIt = linkModel.listObjectsOfProperty(entityRes,
			        linkModel.createProperty(infoProp.getPropName()));
			if (infoNodeIt.hasNext()) {
				RDFNode node = infoNodeIt.next();
				System.out.println("Property name: " + infoProp.getPropName());
				if (node.isLiteral()) {
					if(checkLanguage(node, linkEnrichment.getLanguage())){
						infoProp.setValue(node.asLiteral().getString());
					}
				} else {
					infoProp.setValue(node.asResource().getURI());
				}
				enrichmentInfo.add(infoProp);
			}
		}
		linkEnrichment.setInfoList(enrichmentInfo);
	}
	
	private static boolean checkLanguage(RDFNode node, String language ){
		
		return node.asLiteral().getLanguage().equals(language) || node.asLiteral().getLanguage().equals("");
	}

	/**
	 * Downloads an image from a specific URL. If the URL is redirecting to
	 * another, it follows the URL chain till the image is found.
	 * 
	 * @param imagUrl
	 *            the image URL
	 * @return the image
	 */
	public static Image downloadImage(String imagUrl) {

		Image image = null;
		try {
			URL url = new URL(imagUrl);
			while (image == null && url != null) {
				image = ImageIO.read(url);
				if (image == null) {
					HttpURLConnection conn = (HttpURLConnection) url
					        .openConnection();
					HttpURLConnection.setFollowRedirects(false);
					String urlStr = conn.getHeaderField("Location");
					if (urlStr != null) {
						url = new URL(urlStr);
					} else {
						url = null;
					}
				}
			}
		} catch (MalformedURLException e) {
			LOG.error("Error in the image URL: " + imagUrl, e);
		} catch (IOException e) {
			LOG.error("Error while downloading the image with URL " + imagUrl, e);
		}
		return image;
	}
}
