package com.vistatec.ocelot.xliff.okapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.StatementImpl;
import com.vistatec.ocelot.segment.model.enrichment.ELinkEnrichmentsConstants;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkInfoData;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;

public class EnrichmentBuilder {

	public static Map<String, List<Enrichment>> getExpectedEnrichmentsXliff20() {

		Map<String, List<Enrichment>> seg2Enrichments = new HashMap<String, List<Enrichment>>();

		// segment s1
		List<Enrichment> enrichments = new ArrayList<Enrichment>();
		TerminologyEnrichment termEnrich = new TerminologyEnrichment(
		        "char=0,8", Arrays.asList(new String[]{"sentence"}), Arrays.asList(new String[]{"phrase"}),
		        "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(0);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=0,8", Arrays.asList(new String[]{"sentence"}), Arrays.asList(new String[]{"condamnation", "peine", "sentence"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(0);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=28,36", Arrays.asList(new String[]{"phrase", "sentence"}), Arrays.asList(new String[]{"phrase"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(15);
		termEnrich.setOffsetNoTagsEndIdx(23);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=28,36", Arrays.asList(new String[]{"decree"}), Arrays.asList(new String[]{"verdict"}),
		        "civil law", null);
		termEnrich.setOffsetNoTagsStartIdx(15);
		termEnrich.setOffsetNoTagsEndIdx(23);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=28,36", Arrays.asList(new String[]{"judgement", "sentence"}), Arrays.asList(new String[]{"arrêt"}),
		         null, null);
		termEnrich.setOffsetNoTagsStartIdx(15);
		termEnrich.setOffsetNoTagsEndIdx(23);
		enrichments.add(termEnrich);

		seg2Enrichments.put("s1", enrichments);

		// segment s3
		enrichments = new ArrayList<Enrichment>();
		termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(10);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=3,10",Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(10);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "communications", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(10);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"section"}), Arrays.asList(new String[]{"maille"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(10);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=3,10",  Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "information technology and data processing", "The representation of an object in a XAML file.");
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(10);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"cell"}), Arrays.asList(new String[]{"pile"}),
		        "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(10);
		enrichments.add(termEnrich);

		seg2Enrichments.put("s3", enrichments);

		// segment s4
		enrichments = new ArrayList<Enrichment>();
		// same term enrichments as s3
		enrichments.addAll(seg2Enrichments.get("s3"));
		EntityEnrichment entityEnrich = new EntityEnrichment("char=18,27",
		        "http://dbpedia.org/resource/House");
		entityEnrich.setOffsetNoTagsStartIdx(11);
		entityEnrich.setOffsetNoTagsEndIdx(20);
		enrichments.add(entityEnrich);

		LinkEnrichment linkEnrich = new LinkEnrichment("char=18,27", "en");
		linkEnrich.setOffsetNoTagsStartIdx(11);
		linkEnrich.setOffsetNoTagsEndIdx(20);
		linkEnrich.setEntityName("House",
		        ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich
		        .setImageURL(
		                "http://commons.wikimedia.org/wiki/Special:FilePath/Vasskertentrance.jpg?width=300",
		                ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich
		        .setLongDescription(
		                "A house is a building that functions as a home, ranging from simple dwellings such as rudimentary huts of nomadic tribes and the improvised shacks in shantytowns to complex, fixed structures of wood, brick, marble or other materials containing plumbing, ventilation and electrical systems. Most conventional modern houses in Western cultures will contain a bedroom, bathroom, kitchen or cooking area, and a living room. In traditional agriculture-oriented societies, domestic animals such as chickens or larger livestock (like cattle) may share part of the house with humans. The social unit that lives in a house is known as a household. Most commonly, a household is a family unit of some kind, although households may also be other social groups or individuals. The design and structure of the house is also subject to change as a consequence of globalization, urbanization and other social, economic, demographic, and technological reasons. Various other cultural factors also influence the building style and patterns of domestic space.",
		                ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich.setReferenceEntity("http://dbpedia.org/resource/House");
		linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/House",
		        ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		linkEnrich
		        .setShortDescription(
		                "A house is a building that functions as a home, ranging from simple dwellings such as rudimentary huts of nomadic tribes and the improvised shacks in shantytowns to complex, fixed structures of wood, brick, marble or other materials containing plumbing, ventilation and electrical systems. Most conventional modern houses in Western cultures will contain a bedroom, bathroom, kitchen or cooking area, and a living room. In traditional agriculture-oriented societies, domestic animals such as chickens or larger livestock (like cattle) may share part of the house with humans. The social unit that lives in a house is known as a household. Most commonly, a household is a family unit of some kind, although households may also be other social groups or individuals. The design and structure of the hou",
		                ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		enrichments.add(linkEnrich);

		seg2Enrichments.put("s4", enrichments);

		// segment s5
		enrichments = new ArrayList<Enrichment>();
		termEnrich = new TerminologyEnrichment("char=0,4", Arrays.asList(new String[]{"Text"}), Arrays.asList(new String[]{"Texte"}),
		        "information technology and data processing", "A field data type that can contain up to 255 characters or the number of characters specified by the Fieldsize property, whichever is less.");
		termEnrich.setOffsetNoTagsStartIdx(0);
		termEnrich.setOffsetNoTagsEndIdx(4);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=0,4", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"libellé"}),
		        null, null);
		termEnrich.setOffsetNoTagsStartIdx(0);
		termEnrich.setOffsetNoTagsEndIdx(4);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=0,4", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}),
		        "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(0);
		termEnrich.setOffsetNoTagsEndIdx(4);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=0,4", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}),
		        "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(0);
		termEnrich.setOffsetNoTagsEndIdx(4);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=0,4", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}),
		        "LAW", null);
		termEnrich.setOffsetNoTagsStartIdx(0);
		termEnrich.setOffsetNoTagsEndIdx(4);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=16,20", Arrays.asList(new String[]{"bold"}), Arrays.asList(new String[]{"gras"}),
		        "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(8);
		termEnrich.setOffsetNoTagsEndIdx(12);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=46,53", Arrays.asList(new String[]{"italic"}), Arrays.asList(new String[]{"en italique"}), "information technology and data processing",
		        null);
		termEnrich.setOffsetNoTagsStartIdx(23);
		termEnrich.setOffsetNoTagsEndIdx(30);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=46,53", Arrays.asList(new String[]{"italic"}), Arrays.asList(new String[]{"italique"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(23);
		termEnrich.setOffsetNoTagsEndIdx(30);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=46,53", Arrays.asList(new String[]{"italic"}), Arrays.asList(new String[]{"italique"}), "information technology and data processing", "Pertaining to characters that are evenly slanted to the right or in the direction of text flow.");
		termEnrich.setOffsetNoTagsStartIdx(23);
		termEnrich.setOffsetNoTagsEndIdx(30);
		enrichments.add(termEnrich);

		seg2Enrichments.put("s5", enrichments);

		// segment s6
		enrichments = new ArrayList<Enrichment>();
		termEnrich = new TerminologyEnrichment("char=4,11", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(4);
		termEnrich.setOffsetNoTagsEndIdx(11);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=4,11", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(4);
		termEnrich.setOffsetNoTagsEndIdx(11);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=4,11", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "communications", null);
		termEnrich.setOffsetNoTagsStartIdx(4);
		termEnrich.setOffsetNoTagsEndIdx(11);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=4,11", Arrays.asList(new String[]{"section"}), Arrays.asList(new String[]{"maille"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(4);
		termEnrich.setOffsetNoTagsEndIdx(11);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=4,11", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "information technology and data processing", "A unit of information within a markup language that is defined by a tag, or a pair of tags surrounding some content, and includes any attributes defined within the initial tag.");
		termEnrich.setOffsetNoTagsStartIdx(4);
		termEnrich.setOffsetNoTagsEndIdx(11);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=4,11", Arrays.asList(new String[]{"cell"}), Arrays.asList(new String[]{"pile"}),
		        "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(4);
		termEnrich.setOffsetNoTagsEndIdx(11);
		enrichments.add(termEnrich);

		entityEnrich = new EntityEnrichment("char=21,30",
		        "http://dbpedia.org/resource/House");
		entityEnrich.setOffsetNoTagsStartIdx(12);
		entityEnrich.setOffsetNoTagsEndIdx(21);
		enrichments.add(entityEnrich);

		linkEnrich = new LinkEnrichment("char=21,30", "en");
		linkEnrich.setOffsetNoTagsStartIdx(12);
		linkEnrich.setOffsetNoTagsEndIdx(21);
		linkEnrich.setEntityName("House",
		        ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich
		        .setImageURL(
		                "http://commons.wikimedia.org/wiki/Special:FilePath/Vasskertentrance.jpg?width=300",
		                ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich
		        .setLongDescription(
		                "A house is a building that functions as a home, ranging from simple dwellings such as rudimentary huts of nomadic tribes and the improvised shacks in shantytowns to complex, fixed structures of wood, brick, marble or other materials containing plumbing, ventilation and electrical systems. Most conventional modern houses in Western cultures will contain a bedroom, bathroom, kitchen or cooking area, and a living room. In traditional agriculture-oriented societies, domestic animals such as chickens or larger livestock (like cattle) may share part of the house with humans. The social unit that lives in a house is known as a household. Most commonly, a household is a family unit of some kind, although households may also be other social groups or individuals. The design and structure of the house is also subject to change as a consequence of globalization, urbanization and other social, economic, demographic, and technological reasons. Various other cultural factors also influence the building style and patterns of domestic space.",
		                ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich.setReferenceEntity("http://dbpedia.org/resource/House");
		linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/House",
		        ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		linkEnrich
		        .setShortDescription(
		                "A house is a building that functions as a home, ranging from simple dwellings such as rudimentary huts of nomadic tribes and the improvised shacks in shantytowns to complex, fixed structures of wood, brick, marble or other materials containing plumbing, ventilation and electrical systems. Most conventional modern houses in Western cultures will contain a bedroom, bathroom, kitchen or cooking area, and a living room. In traditional agriculture-oriented societies, domestic animals such as chickens or larger livestock (like cattle) may share part of the house with humans. The social unit that lives in a house is known as a household. Most commonly, a household is a family unit of some kind, although households may also be other social groups or individuals. The design and structure of the hou",
		                ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		enrichments.add(linkEnrich);

		seg2Enrichments.put("s6", enrichments);

		// segment s7
		enrichments = new ArrayList<Enrichment>();
		entityEnrich = new EntityEnrichment("char=0,2",
		        "http://dbpedia.org/resource/James_Edward_Smith");
		entityEnrich.setOffsetNoTagsStartIdx(0);
		entityEnrich.setOffsetNoTagsEndIdx(2);
		enrichments.add(entityEnrich);

		linkEnrich = new LinkEnrichment("char=0,2", "en");
		linkEnrich.setOffsetNoTagsStartIdx(0);
		linkEnrich.setOffsetNoTagsEndIdx(2);
		linkEnrich.setEntityName("James Edward Smith",
		        ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich
		        .setImageURL(
		                "http://commons.wikimedia.org/wiki/Special:FilePath/James_Edward_Smith.jpg?width=300",
		                ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich
		        .setLongDescription(
		                "Sir James Edward Smith (2 December 1759 – 17 March 1828) was an English botanist and founder of the Linnean Society.",
		                ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich
		        .setReferenceEntity("http://dbpedia.org/resource/James_Edward_Smith");
		linkEnrich
		        .setShortDescription(
		                "Sir James Edward Smith (2 December 1759 – 17 March 1828) was an English botanist and founder of the Linnean Society.",
		                ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		linkEnrich.setWikiPage(
		        "http://en.wikipedia.org/wiki/James_Edward_Smith",
		        ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		List<LinkInfoData> infoList = new ArrayList<LinkInfoData>();
		LinkInfoData info = new LinkInfoData(
		        ELinkEnrichmentsConstants.BIRTHDATE_PROP, "Birth Date",
		        Date.class);
		info.setValue("1759-12-02");
		infoList.add(info);
		info = new LinkInfoData(ELinkEnrichmentsConstants.DEATHDATE_PROP,
		        "Death Date", Date.class);
		info.setValue("1828-03-17");
		infoList.add(info);
		info = new LinkInfoData(ELinkEnrichmentsConstants.BIRTH_PLACE_PROP,
		        "Birth Place", String.class);
		info.setValue("http://dbpedia.org/resource/Norwich");
		infoList.add(info);
		linkEnrich.setInfoList(infoList);
		enrichments.add(linkEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"divide", "division", "split"}), Arrays.asList(new String[]{"clivage"}),
		        null, null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"traversing crack"}), Arrays.asList(new String[]{"fente transversante"}), "wood industry", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"half-round log"}), Arrays.asList(new String[]{"moitié de bille"}), "wood industry", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fractionnement"}), "information technology and data processing",
		        null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fractionner"}), "information technology and data processing",
		        "To divide an audio or video clip into two clips.");
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment(
		        "char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fractionner"})
		    ,
		        "en:EC - Economics (sn: macroeconomics; nt: generic field; rf: Commerce and Movement of Goods, see: CO; Financial Affairs - Taxation - Customs, see: FI)  (Lenoch classification);  AGG - foodstuff production  (Lenoch classification);  FI - Financial Affairs - Taxation and Customs (sn: microeconomics 2; nt: further breakdown of ECF)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);  CEP - Treaties - Agreements - Conventions - Cooperation - Commonity Policy - Commercial Policy  (Lenoch classification);",
		        null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"break", "crack", "split"}), Arrays.asList(new String[]{"brisure", "crevasse", "fente"}),
		        null, null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fendre", "fente"}),
		        null, null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fendu"}),
		        "wood industry", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "communications", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"section"}), Arrays.asList(new String[]{"maille"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "information technology and data processing", "The representation of an object in a XAML file.");
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"cell"}), Arrays.asList(new String[]{"pile"}),
		        "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		seg2Enrichments.put("s7", enrichments);

		// segment s8
		enrichments = new ArrayList<Enrichment>();
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"divide", "division", "split"}), Arrays.asList(new String[]{"clivage"}),
		        null, null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"traversing crack"}), Arrays.asList(new String[]{"fente transversante"}), "wood industry", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"half-round log"}), Arrays.asList(new String[]{"moitié de bille"}), "wood industry", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fractionnement"}), "information technology and data processing",
		        null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fractionner"}), "information technology and data processing",
		        "To divide an audio or video clip into two clips.");
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment(
		        "char=3,8",
		        Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fractionner"}),
		        "en:EC - Economics (sn: macroeconomics; nt: generic field; rf: Commerce and Movement of Goods, see: CO; Financial Affairs - Taxation - Customs, see: FI)  (Lenoch classification);  AGG - foodstuff production  (Lenoch classification);  FI - Financial Affairs - Taxation and Customs (sn: microeconomics 2; nt: further breakdown of ECF)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);  CEP - Treaties - Agreements - Conventions - Cooperation - Commonity Policy - Commercial Policy  (Lenoch classification);",
		        null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"break", "crack", "split"}), Arrays.asList(new String[]{"brisure", "crevasse", "fente"}),
		        null, null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fendre", "fente"}),
		        null, null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fendu"}),
		        "wood industry", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "communications", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"section"}), Arrays.asList(new String[]{"maille"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "information technology and data processing", "The representation of an object in a XAML file.");
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"cell"}), Arrays.asList(new String[]{"pile"}),
		        "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(16);
		enrichments.add(termEnrich);

		seg2Enrichments.put("s8", enrichments);

		return seg2Enrichments;
	}

	public static Map<Integer, List<Enrichment>> getExpectedEnrichmentsXliff12() {

		Map<Integer, List<Enrichment>> seg2Enrichments = new HashMap<Integer, List<Enrichment>>();
		
		//segment 1
		List<Enrichment> enrichments = new ArrayList<Enrichment>();
		TerminologyEnrichment termEnrich = new TerminologyEnrichment("char=5,14", Arrays.asList(new String[]{"vacation"}), Arrays.asList(new String[]{"vacance judiciaire"}), "international trade", null);
		termEnrich.setOffsetNoTagsStartIdx(5);
		termEnrich.setOffsetNoTagsEndIdx(14);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=5,14");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=5,14", Arrays.asList(new String[]{"vacation"}), Arrays.asList(new String[]{"suppression"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(5);
		termEnrich.setOffsetNoTagsEndIdx(14);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=5,14");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=5,14", Arrays.asList(new String[]{"holiday", "leave", "vacation"}), Arrays.asList(new String[]{"congé"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(5);
		termEnrich.setOffsetNoTagsEndIdx(14);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=5,14");
		enrichments.add(termEnrich);
		
		seg2Enrichments.put(1, enrichments);
		
		//segment 2
		enrichments = new ArrayList<Enrichment>();
		termEnrich = new TerminologyEnrichment("char=0,7", Arrays.asList(new String[]{"welcome"}), Arrays.asList(new String[]{"bienvenue"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(0);
		termEnrich.setOffsetNoTagsEndIdx(7);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=0,7");
		enrichments.add(termEnrich);
		
		EntityEnrichment entityEnrich = new EntityEnrichment("char=11,15", "http://dbpedia.org/resource/Bray");
		entityEnrich.setOffsetNoTagsStartIdx(11);
		entityEnrich.setOffsetNoTagsEndIdx(15);
		entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
		enrichments.add(entityEnrich);
		
		LinkEnrichment linkEnrich = new LinkEnrichment("char=11,15", "en");
		linkEnrich.setOffsetNoTagsStartIdx(11);
		linkEnrich.setOffsetNoTagsEndIdx(15);
		linkEnrich.setEntityName("Bray", ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich.setHomePage("http://www.bray.ie", ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
		linkEnrich.setImageURL("http://en.wikipedia.org/wiki/Special:FilePath/Brayview.JPG?width=300", ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich.setLongDescription("Bray (Irish: Bré, meaning \"hill\", formerly Brí Chualann) is a town in north County Wicklow, Ireland. It is a busy urban centre and seaside resort, with a population of 31,872 making it the ninth largest urban area in Ireland at the 2011 census. It is situated about 20 km (12 mi) south of Dublin on the east coast. The town straddles the Dublin-Wicklow border, with a portion of the northern suburbs situated in County Dublin. Bray's scenic location and proximity to Dublin make it a popular destination for tourists and day-trippers from the capital. Bray is home to Ireland's only film studios, Ardmore Studios, hosting Irish and international productions for film, television and advertising. Some light industry is located in the town, with business and retail parks concentrated largely on its southern periphery. Bray town centre has a range of shops serving the consumer needs of the surrounding area. Commuter links between Bray and Dublin are provided by rail, Dublin Bus and the M11 and M50 motorways.", ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich.setReferenceEntity("http://dbpedia.org/resource/Bray");
		linkEnrich.setShortDescription("Bray (Irish: Bré, meaning \"hill\", formerly Brí Chualann) is a town in north County Wicklow, Ireland. It is a busy urban centre and seaside resort, with a population of 31,872 making it the ninth largest urban area in Ireland at the 2011 census. It is situated about 20 km (12 mi) south of Dublin on the east coast. The town straddles the Dublin-Wicklow border, with a portion of the northern suburbs situated in County Dublin.", ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/Bray", ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		List<LinkInfoData> infoList = new ArrayList<LinkInfoData>();
		LinkInfoData infoData = new LinkInfoData(ELinkEnrichmentsConstants.LONGITUDE_PROP, "Longitude",
		        Float.class);
		infoData.setValue("-6.11136");
		infoList.add(infoData);
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LATITUDE_PROP, "Latitude", Float.class);
		infoData.setValue("53.201");
		infoList.add(infoData);
		linkEnrich.setInfoList(infoList);
		enrichments.add(linkEnrich);
		
		seg2Enrichments.put(2, enrichments);
		
		//segment 3
		enrichments = new ArrayList<Enrichment>();
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"mile"}), Arrays.asList(new String[]{"mille"}), "international trade", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"survey mile"}), Arrays.asList(new String[]{"mille"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=13,23", Arrays.asList(new String[]{"kilometer"}), Arrays.asList(new String[]{"kilomètre"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(13);
		termEnrich.setOffsetNoTagsEndIdx(23);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=13,23");
		enrichments.add(termEnrich);
		
		entityEnrich = new EntityEnrichment("char=45,52", "http://dbpedia.org/resource/Wicklow");
		entityEnrich.setOffsetNoTagsStartIdx(45);
		entityEnrich.setOffsetNoTagsEndIdx(52);
		entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
		enrichments.add(entityEnrich);
		
		linkEnrich = new LinkEnrichment("char=45,52", "en");
		linkEnrich.setOffsetNoTagsStartIdx(45);
		linkEnrich.setOffsetNoTagsEndIdx(52);
		
		linkEnrich.setEntityName("Wicklow", ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich.setHomePage("http://www.wicklow.ie", ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
		linkEnrich.setImageURL("http://commons.wikimedia.org/wiki/Special:FilePath/Wicklow_Town_-_geograph.org.uk_-_692370_(cropped).jpg?width=300", ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich.setLongDescription("Wicklow (Irish: Cill Mhantáin, meaning \"church of the toothless one\") is the county town of County Wicklow and the capital of the Mid-East Region in Ireland. Located south of Dublin on the east coast of the island, it has a population of 10,356 according to the 2011 census. The town is to the east of the N11 route between Dublin and Wexford. Wicklow is also linked to the rail network, with Dublin commuter services now extending to the town. Additional services connect with Arklow, Wexford and Rosslare Europort, a main ferry port. There is also a commercial port, mainly importing timber and textiles. The River Vartry is the main river which flows through the town.", ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich.setReferenceEntity("http://dbpedia.org/resource/Wicklow");
		linkEnrich.setShortDescription("Wicklow (Irish: Cill Mhantáin, meaning \"church of the toothless one\") is the county town of County Wicklow and the capital of the Mid-East Region in Ireland. Located south of Dublin on the east coast of the island, it has a population of 10,356 according to the 2011 census. The town is to the east of the N11 route between Dublin and Wexford. Wicklow is also linked to the rail network, with Dublin commuter services now extending to the town. Additional services connect with Arklow, Wexford and Rosslare Europort, a main ferry port. There is also a commercial port, mainly importing timber and textiles. The River Vartry is the main river which flows through the town.", ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/Wicklow", ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		infoList = new ArrayList<LinkInfoData>();
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LONGITUDE_PROP, "Longitude",
		        Float.class);
		infoData.setValue("-6.033");
		infoList.add(infoData);
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LATITUDE_PROP, "Latitude", Float.class);
		infoData.setValue("52.9779");
		infoList.add(infoData);
		linkEnrich.setInfoList(infoList);
		enrichments.add(linkEnrich);
		
		entityEnrich = new EntityEnrichment("char=57,61", "http://dbpedia.org/resource/Bray");
		entityEnrich.setOffsetNoTagsStartIdx(57);
		entityEnrich.setOffsetNoTagsEndIdx(61);
		entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
		enrichments.add(entityEnrich);
		
		linkEnrich = new LinkEnrichment("char=57,61", "en");
		linkEnrich.setOffsetNoTagsStartIdx(57);
		linkEnrich.setOffsetNoTagsEndIdx(61);
		linkEnrich.setEntityName("Bray", ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich.setHomePage("http://www.bray.ie", ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
		linkEnrich.setImageURL("http://en.wikipedia.org/wiki/Special:FilePath/Brayview.JPG?width=300", ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich.setLongDescription("Bray (Irish: Bré, meaning \"hill\", formerly Brí Chualann) is a town in north County Wicklow, Ireland. It is a busy urban centre and seaside resort, with a population of 31,872 making it the ninth largest urban area in Ireland at the 2011 census. It is situated about 20 km (12 mi) south of Dublin on the east coast. The town straddles the Dublin-Wicklow border, with a portion of the northern suburbs situated in County Dublin. Bray's scenic location and proximity to Dublin make it a popular destination for tourists and day-trippers from the capital. Bray is home to Ireland's only film studios, Ardmore Studios, hosting Irish and international productions for film, television and advertising. Some light industry is located in the town, with business and retail parks concentrated largely on its southern periphery. Bray town centre has a range of shops serving the consumer needs of the surrounding area. Commuter links between Bray and Dublin are provided by rail, Dublin Bus and the M11 and M50 motorways.", ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich.setReferenceEntity("http://dbpedia.org/resource/Bray");
		linkEnrich.setShortDescription("Bray (Irish: Bré, meaning \"hill\", formerly Brí Chualann) is a town in north County Wicklow, Ireland. It is a busy urban centre and seaside resort, with a population of 31,872 making it the ninth largest urban area in Ireland at the 2011 census. It is situated about 20 km (12 mi) south of Dublin on the east coast. The town straddles the Dublin-Wicklow border, with a portion of the northern suburbs situated in County Dublin.", ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/Bray", ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		infoList = new ArrayList<LinkInfoData>();
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LONGITUDE_PROP, "Longitude",
		        Float.class);
		infoData.setValue("-6.11136");
		infoList.add(infoData);
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LATITUDE_PROP, "Latitude", Float.class);
		infoData.setValue("53.201");
		infoList.add(infoData);
		linkEnrich.setInfoList(infoList);
		enrichments.add(linkEnrich);
		
		termEnrich = new TerminologyEnrichment("char=65,72", Arrays.asList(new String[]{"popular"}), Arrays.asList(new String[]{"populaire"}), "culture and religion", null);
		termEnrich.setOffsetNoTagsStartIdx(65);
		termEnrich.setOffsetNoTagsEndIdx(72);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=65,72");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=65,72", Arrays.asList(new String[]{"popular"}), Arrays.asList(new String[]{"populaire"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(65);
		termEnrich.setOffsetNoTagsEndIdx(72);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=65,72");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77",  Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"message publicitaire"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"tache"}), "wood industry", null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot croaker", "spot"}), Arrays.asList(new String[]{"tambour croca"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		List<String> targetList = Arrays.asList(new String[]{"remarquer", "point(tache)"});
		termEnrich = new TerminologyEnrichment("char=73,77",  Arrays.asList(new String[]{"spot"}), targetList, null, null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"point de soudure"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"tache"}), "CH6 - analytical chemistry (sn: principles and methods; rf: industrial analysis, see: TEO)  (Lenoch classification);  TR45 - railway maintenance  (Lenoch classification);  JUD1 - author's rights  (Lenoch classification);  TR56 - pedestrians and cyclists  (Lenoch classification);  TRE - transport security (nt: protection against attacks; protection of people and goods)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"point"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=82,90", Arrays.asList(new String[]{"Visitors"}), Arrays.asList(new String[]{"Visiteurs"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(82);
		termEnrich.setOffsetNoTagsEndIdx(90);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=82,90");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=82,90", Arrays.asList(new String[]{"visitor"}), Arrays.asList(new String[]{"visiteur"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(82);
		termEnrich.setOffsetNoTagsEndIdx(90);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=82,90");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=91,101", Arrays.asList(new String[]{"wishing to"}), Arrays.asList(new String[]{"désireux"}), "international trade", null);
		termEnrich.setOffsetNoTagsStartIdx(91);
		termEnrich.setOffsetNoTagsEndIdx(101);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=91,101");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=102,110", Arrays.asList(new String[]{"discover"}), Arrays.asList(new String[]{"interroger au préalable"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(102);
		termEnrich.setOffsetNoTagsEndIdx(110);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=102,110");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=102,110", Arrays.asList(new String[]{"discover"}), Arrays.asList(new String[]{"inventer"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(102);
		termEnrich.setOffsetNoTagsEndIdx(110);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=102,110");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "SCIENCE", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "FINANCE", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"regions"}), Arrays.asList(new String[]{"régions"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "environmental policy", "\"A designated area or an administrative division of a city, county or larger geographical territory that is formulated according to some biological, political, economic or demographic criteria. (Source: RHW / ISEP)\"");
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"space"}), Arrays.asList(new String[]{"zone"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"range"}), Arrays.asList(new String[]{"zone"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"oblast"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
		enrichments.add(termEnrich);
		
		seg2Enrichments.put(3, enrichments);
		
		//segment 4
		enrichments = new ArrayList<Enrichment>();
		entityEnrich = new EntityEnrichment("char=12,26", "http://dbpedia.org/resource/Dublin_Airport");
		entityEnrich.setOffsetNoTagsStartIdx(12);
		entityEnrich.setOffsetNoTagsEndIdx(26);
		entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
		enrichments.add(entityEnrich);
		
		linkEnrich = new LinkEnrichment("char=12,26", "en");
		linkEnrich.setOffsetNoTagsStartIdx(12);
		linkEnrich.setOffsetNoTagsEndIdx(26);
		linkEnrich.setEntityName("Dublin Airport", ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich.setHomePage("http://www.dublinairport.com", ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
		linkEnrich.setImageURL("http://en.wikipedia.org/wiki/Special:FilePath/DUBlogo.png?width=300", ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich.setLongDescription("Dublin Airport, (Irish: Aerfort Bhaile Átha Cliath) (IATA: DUB, ICAO: EIDW), is an international airport serving Dublin, the capital city of Ireland. It is operated by daa. The airport is located 5.4 nmi (10.0 km; 6.2 mi) north of Dublin in Collinstown, County Dublin. In 2015, over 25 million passengers passed through the airport making it the airport's busiest year on record. It is also the busiest of the state's airports by total passenger traffic. It also has the greatest traffic levels on the island of Ireland followed by Belfast International Airport, County Antrim. The airport has an extensive short and medium haul network, served by an array of carriers as well as some intercontinental routes focused in the Middle East and North America. It serves as the headquarters of Ireland's flag carrier – Aer Lingus, regional airline Stobart Air, Europe's largest low-cost carrier – Ryanair, ASL Airlines Ireland, together with a fifth airline, CityJet, which does not maintain major operations here. United States border preclearance services are available at the airport for US-bound passengers. Shannon Airport is the only other airport in Europe to offer this facility.", ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich.setReferenceEntity("http://dbpedia.org/resource/Dublin_Airport");
		linkEnrich.setShortDescription("Dublin Airport, (Irish: Aerfort Bhaile Átha Cliath) (IATA: DUB, ICAO: EIDW), is an international airport serving Dublin, the capital city of Ireland. It is operated by daa. The airport is located 5.4 nmi (10.0 km; 6.2 mi) north of Dublin in Collinstown, County Dublin. In 2015, over 25 million passengers passed through the airport making it the airport's busiest year on record. It is also the busiest of the state's airports by total passenger traffic. It also has the greatest traffic levels on the island of Ireland followed by Belfast International Airport, County Antrim.", ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/Dublin_Airport", ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		infoList = new ArrayList<LinkInfoData>();
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LONGITUDE_PROP, "Longitude",
		        Float.class);
		infoData.setValue("-6.27");
		infoList.add(infoData);
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LATITUDE_PROP, "Latitude", Float.class);
		infoData.setValue("53.4214");
		infoList.add(infoData);
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LOCATION_PROP, "Location", String.class);
		infoData.setValue("Collinstown, Fingal, Ireland");
		infoList.add(infoData);
		linkEnrich.setInfoList(infoList);
		enrichments.add(linkEnrich);
		
		entityEnrich = new EntityEnrichment("char=30,37", "http://dbpedia.org/resource/Wicklow");
		entityEnrich.setOffsetNoTagsStartIdx(30);
		entityEnrich.setOffsetNoTagsEndIdx(37);
		entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
		enrichments.add(entityEnrich);
		
		linkEnrich = new LinkEnrichment("char=30,37", "en");
		linkEnrich.setOffsetNoTagsStartIdx(30);
		linkEnrich.setOffsetNoTagsEndIdx(37);
		
		linkEnrich.setEntityName("Wicklow", ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich.setHomePage("http://www.wicklow.ie", ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
		linkEnrich.setImageURL("http://commons.wikimedia.org/wiki/Special:FilePath/Wicklow_Town_-_geograph.org.uk_-_692370_(cropped).jpg?width=300", ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich.setLongDescription("Wicklow (Irish: Cill Mhantáin, meaning \"church of the toothless one\") is the county town of County Wicklow and the capital of the Mid-East Region in Ireland. Located south of Dublin on the east coast of the island, it has a population of 10,356 according to the 2011 census. The town is to the east of the N11 route between Dublin and Wexford. Wicklow is also linked to the rail network, with Dublin commuter services now extending to the town. Additional services connect with Arklow, Wexford and Rosslare Europort, a main ferry port. There is also a commercial port, mainly importing timber and textiles. The River Vartry is the main river which flows through the town.", ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich.setReferenceEntity("http://dbpedia.org/resource/Wicklow");
		linkEnrich.setShortDescription("Wicklow (Irish: Cill Mhantáin, meaning \"church of the toothless one\") is the county town of County Wicklow and the capital of the Mid-East Region in Ireland. Located south of Dublin on the east coast of the island, it has a population of 10,356 according to the 2011 census. The town is to the east of the N11 route between Dublin and Wexford. Wicklow is also linked to the rail network, with Dublin commuter services now extending to the town. Additional services connect with Arklow, Wexford and Rosslare Europort, a main ferry port. There is also a commercial port, mainly importing timber and textiles. The River Vartry is the main river which flows through the town.", ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/Wicklow", ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		infoList = new ArrayList<LinkInfoData>();
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LONGITUDE_PROP, "Longitude",
		        Float.class);
		infoData.setValue("-6.033");
		infoList.add(infoData);
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LATITUDE_PROP, "Latitude", Float.class);
		infoData.setValue("52.9779");
		infoList.add(infoData);
		linkEnrich.setInfoList(infoList);
		enrichments.add(linkEnrich);
		
		entityEnrich = new EntityEnrichment("char=39,43", "http://dbpedia.org/resource/Bray");
		entityEnrich.setOffsetNoTagsStartIdx(39);
		entityEnrich.setOffsetNoTagsEndIdx(43);
		entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
		enrichments.add(entityEnrich);
		
		linkEnrich = new LinkEnrichment("char=39,43", "en");
		linkEnrich.setOffsetNoTagsStartIdx(39);
		linkEnrich.setOffsetNoTagsEndIdx(43);
		linkEnrich.setEntityName("Bray", ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich.setHomePage("http://www.bray.ie", ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
		linkEnrich.setImageURL("http://en.wikipedia.org/wiki/Special:FilePath/Brayview.JPG?width=300", ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich.setLongDescription("Bray (Irish: Bré, meaning \"hill\", formerly Brí Chualann) is a town in north County Wicklow, Ireland. It is a busy urban centre and seaside resort, with a population of 31,872 making it the ninth largest urban area in Ireland at the 2011 census. It is situated about 20 km (12 mi) south of Dublin on the east coast. The town straddles the Dublin-Wicklow border, with a portion of the northern suburbs situated in County Dublin. Bray's scenic location and proximity to Dublin make it a popular destination for tourists and day-trippers from the capital. Bray is home to Ireland's only film studios, Ardmore Studios, hosting Irish and international productions for film, television and advertising. Some light industry is located in the town, with business and retail parks concentrated largely on its southern periphery. Bray town centre has a range of shops serving the consumer needs of the surrounding area. Commuter links between Bray and Dublin are provided by rail, Dublin Bus and the M11 and M50 motorways.", ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich.setReferenceEntity("http://dbpedia.org/resource/Bray");
		linkEnrich.setShortDescription("Bray (Irish: Bré, meaning \"hill\", formerly Brí Chualann) is a town in north County Wicklow, Ireland. It is a busy urban centre and seaside resort, with a population of 31,872 making it the ninth largest urban area in Ireland at the 2011 census. It is situated about 20 km (12 mi) south of Dublin on the east coast. The town straddles the Dublin-Wicklow border, with a portion of the northern suburbs situated in County Dublin.", ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/Bray", ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		infoList = new ArrayList<LinkInfoData>();
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LONGITUDE_PROP, "Longitude",
		        Float.class);
		infoData.setValue("-6.11136");
		infoList.add(infoData);
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LATITUDE_PROP, "Latitude", Float.class);
		infoData.setValue("53.201");
		infoList.add(infoData);
		linkEnrich.setInfoList(infoList);
		enrichments.add(linkEnrich);
		
		seg2Enrichments.put(4, enrichments);
		
		//segment 5
		enrichments = new ArrayList<Enrichment>();
		entityEnrich = new EntityEnrichment("char=0,4", "http://dbpedia.org/resource/Bray");
		entityEnrich.setOffsetNoTagsStartIdx(0);
		entityEnrich.setOffsetNoTagsEndIdx(4);
		entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
		enrichments.add(entityEnrich);
		
		linkEnrich = new LinkEnrichment("char=0,4", "en");
		linkEnrich.setOffsetNoTagsStartIdx(0);
		linkEnrich.setOffsetNoTagsEndIdx(4);
		linkEnrich.setEntityName("Bray", ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich.setHomePage("http://www.bray.ie", ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
		linkEnrich.setImageURL("http://en.wikipedia.org/wiki/Special:FilePath/Brayview.JPG?width=300", ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich.setLongDescription("Bray (Irish: Bré, meaning \"hill\", formerly Brí Chualann) is a town in north County Wicklow, Ireland. It is a busy urban centre and seaside resort, with a population of 31,872 making it the ninth largest urban area in Ireland at the 2011 census. It is situated about 20 km (12 mi) south of Dublin on the east coast. The town straddles the Dublin-Wicklow border, with a portion of the northern suburbs situated in County Dublin. Bray's scenic location and proximity to Dublin make it a popular destination for tourists and day-trippers from the capital. Bray is home to Ireland's only film studios, Ardmore Studios, hosting Irish and international productions for film, television and advertising. Some light industry is located in the town, with business and retail parks concentrated largely on its southern periphery. Bray town centre has a range of shops serving the consumer needs of the surrounding area. Commuter links between Bray and Dublin are provided by rail, Dublin Bus and the M11 and M50 motorways.", ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich.setReferenceEntity("http://dbpedia.org/resource/Bray");
		linkEnrich.setShortDescription("Bray (Irish: Bré, meaning \"hill\", formerly Brí Chualann) is a town in north County Wicklow, Ireland. It is a busy urban centre and seaside resort, with a population of 31,872 making it the ninth largest urban area in Ireland at the 2011 census. It is situated about 20 km (12 mi) south of Dublin on the east coast. The town straddles the Dublin-Wicklow border, with a portion of the northern suburbs situated in County Dublin.", ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/Bray", ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		infoList = new ArrayList<LinkInfoData>();
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LONGITUDE_PROP, "Longitude",
		        Float.class);
		infoData.setValue("-6.11136");
		infoList.add(infoData);
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LATITUDE_PROP, "Latitude", Float.class);
		infoData.setValue("53.201");
		infoList.add(infoData);
		linkEnrich.setInfoList(infoList);
		enrichments.add(linkEnrich);
		
		termEnrich = new TerminologyEnrichment("char=16,21", Arrays.asList(new String[]{"place"}), Arrays.asList(new String[]{"emplacement"}), "information technology and data processing", "A window that is part of the navigation layer in the Dynamics NAV application.");
		termEnrich.setOffsetNoTagsStartIdx(16);
		termEnrich.setOffsetNoTagsEndIdx(21);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=16,21");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=16,21", Arrays.asList(new String[]{"location"}), Arrays.asList(new String[]{"emplacement"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(16);
		termEnrich.setOffsetNoTagsEndIdx(21);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=16,21");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=16,21", Arrays.asList(new String[]{"place"}), Arrays.asList(new String[]{"emplacement"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(16);
		termEnrich.setOffsetNoTagsEndIdx(21);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=16,21");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=16,21",  Arrays.asList(new String[]{"place"}), Arrays.asList(new String[]{"place"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(16);
		termEnrich.setOffsetNoTagsEndIdx(21);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=16,21");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=16,21", Arrays.asList(new String[]{"place"}), Arrays.asList(new String[]{"place"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(16);
		termEnrich.setOffsetNoTagsEndIdx(21);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=16,21");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), "culture and religion", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"basis ossis metatarsalis"}), Arrays.asList(new String[]{"extrémité tarsienne"}), "Medizin|Fußkrankheiten", "The base or posterior extremity is wedge-shaped, articulating proximally with the tarsal bones, and by its sides with the contiguous metatarsal bones: its dorsal and plantar surfaces are rough for the attachment of ligaments.");
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		targetList = Arrays.asList(new String[]{"socle", "embase", "charge", "base"});
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"feedstock", "base"}), targetList, null, null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"culot d'ergol"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"header"}), Arrays.asList(new String[]{"embase"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"foundation"}), Arrays.asList(new String[]{"assise"}), "wood industry", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), "environmental policy", "\"Any chemical species, ionic or molecular, capable of accepting or receiving a proton (hydrogen ion) from another substance; the other substance acts as an acid in giving of the proton; the other ion is a base. (Source: MGH)\"" );
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), "FINANCE", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		targetList = Arrays.asList(new String[]{"base", "base chimique"});
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), targetList, null, null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"basis"}), Arrays.asList(new String[]{"banque"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=25,29");
		enrichments.add(termEnrich);
		
		entityEnrich = new EntityEnrichment("char=56,63", "http://dbpedia.org/resource/Wicklow");
		entityEnrich.setOffsetNoTagsStartIdx(56);
		entityEnrich.setOffsetNoTagsEndIdx(63);
		entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
		enrichments.add(entityEnrich);
		
		linkEnrich = new LinkEnrichment("char=56,63", "en");
		linkEnrich.setOffsetNoTagsStartIdx(56);
		linkEnrich.setOffsetNoTagsEndIdx(63);
		
		linkEnrich.setEntityName("Wicklow", ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
		linkEnrich.setHomePage("http://www.wicklow.ie", ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
		linkEnrich.setImageURL("http://commons.wikimedia.org/wiki/Special:FilePath/Wicklow_Town_-_geograph.org.uk_-_692370_(cropped).jpg?width=300", ELinkEnrichmentsConstants.IMAGE_PROP);
		linkEnrich.setLongDescription("Wicklow (Irish: Cill Mhantáin, meaning \"church of the toothless one\") is the county town of County Wicklow and the capital of the Mid-East Region in Ireland. Located south of Dublin on the east coast of the island, it has a population of 10,356 according to the 2011 census. The town is to the east of the N11 route between Dublin and Wexford. Wicklow is also linked to the rail network, with Dublin commuter services now extending to the town. Additional services connect with Arklow, Wexford and Rosslare Europort, a main ferry port. There is also a commercial port, mainly importing timber and textiles. The River Vartry is the main river which flows through the town.", ELinkEnrichmentsConstants.LONG_DESCR_PROP);
		linkEnrich.setReferenceEntity("http://dbpedia.org/resource/Wicklow");
		linkEnrich.setShortDescription("Wicklow (Irish: Cill Mhantáin, meaning \"church of the toothless one\") is the county town of County Wicklow and the capital of the Mid-East Region in Ireland. Located south of Dublin on the east coast of the island, it has a population of 10,356 according to the 2011 census. The town is to the east of the N11 route between Dublin and Wexford. Wicklow is also linked to the rail network, with Dublin commuter services now extending to the town. Additional services connect with Arklow, Wexford and Rosslare Europort, a main ferry port. There is also a commercial port, mainly importing timber and textiles. The River Vartry is the main river which flows through the town.", ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
		linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/Wicklow", ELinkEnrichmentsConstants.WIKI_LINK_PROP);
		infoList = new ArrayList<LinkInfoData>();
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LONGITUDE_PROP, "Longitude",
		        Float.class);
		infoData.setValue("-6.033");
		infoList.add(infoData);
		infoData = new LinkInfoData(ELinkEnrichmentsConstants.LATITUDE_PROP, "Latitude", Float.class);
		infoData.setValue("52.9779");
		infoList.add(infoData);
		linkEnrich.setInfoList(infoList);
		enrichments.add(linkEnrich);
		
		termEnrich = new TerminologyEnrichment("char=79,83", Arrays.asList(new String[]{"city"}), Arrays.asList(new String[]{"ville"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(79);
		termEnrich.setOffsetNoTagsEndIdx(83);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=79,83");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=79,83", Arrays.asList(new String[]{"city"}), Arrays.asList(new String[]{"ville"}), "environmental policy", "\"Term used generically today to denote any urban form but applied particularly to large urban settlements. There are, however, no agreed definitions to separate a city from the large metropolis or the smaller town. (Source: GOOD)\"");
		termEnrich.setOffsetNoTagsStartIdx(79);
		termEnrich.setOffsetNoTagsEndIdx(83);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=79,83");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=79,83", Arrays.asList(new String[]{"city", "municipality"}), Arrays.asList(new String[]{"ville", "commune"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(79);
		termEnrich.setOffsetNoTagsEndIdx(83);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=79,83");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=79,83", Arrays.asList(new String[]{"city"}), Arrays.asList(new String[]{"ville"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(79);
		termEnrich.setOffsetNoTagsEndIdx(83);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=79,83");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=89,96", Arrays.asList(new String[]{"popular"}), Arrays.asList(new String[]{"populaire"}), "culture and religion", null);
		termEnrich.setOffsetNoTagsStartIdx(89);
		termEnrich.setOffsetNoTagsEndIdx(96);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=89,96");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=89,96", Arrays.asList(new String[]{"popular"}), Arrays.asList(new String[]{"populaire"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(89);
		termEnrich.setOffsetNoTagsEndIdx(96);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=89,96");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=97,108", Arrays.asList(new String[]{"attraction"}), Arrays.asList(new String[]{"attraction"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(97);
		termEnrich.setOffsetNoTagsEndIdx(108);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=97,108");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=120,124", Arrays.asList(new String[]{"easy"}), Arrays.asList(new String[]{"facile"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(120);
		termEnrich.setOffsetNoTagsEndIdx(124);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=120,124");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=125,132", Arrays.asList(new String[]{"driving"}), Arrays.asList(new String[]{"modulation"}), "EL6 - components - devices  (Lenoch classification);  CH - Chemistry (sn: chemistry as pure science; us: for applied chemistry, see: IC)  (Lenoch classification);  CH8 - organic chemistry  (Lenoch classification);  IC8 - glass - enamel  (Lenoch classification);  TRE - transport security (nt: protection against attacks; protection of people and goods)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
		termEnrich.setOffsetNoTagsStartIdx(125);
		termEnrich.setOffsetNoTagsEndIdx(132);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=125,132");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=125,132", Arrays.asList(new String[]{"driving"}), Arrays.asList(new String[]{"en marche"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(125);
		termEnrich.setOffsetNoTagsEndIdx(132);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=125,132");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=133,141", Arrays.asList(new String[]{"distance"}), Arrays.asList(new String[]{"distance"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(133);
		termEnrich.setOffsetNoTagsEndIdx(141);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=133,141");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=133,141", Arrays.asList(new String[]{"spacing"}), Arrays.asList(new String[]{"écartement"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(133);
		termEnrich.setOffsetNoTagsEndIdx(141);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=133,141");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=133,141", Arrays.asList(new String[]{"length"}), Arrays.asList(new String[]{"distance"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(133);
		termEnrich.setOffsetNoTagsEndIdx(141);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=133,141");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=133,141", Arrays.asList(new String[]{"distance"}), Arrays.asList(new String[]{"espacement"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(133);
		termEnrich.setOffsetNoTagsEndIdx(141);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=133,141");
		enrichments.add(termEnrich);
		
		seg2Enrichments.put(5, enrichments);
		
		//segment 6
		enrichments = new ArrayList<Enrichment>();
		termEnrich = new TerminologyEnrichment("char=2,8", Arrays.asList(new String[]{"single"}), Arrays.asList(new String[]{"célibataire"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(2);
		termEnrich.setOffsetNoTagsEndIdx(8);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=2,8");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=2,8", Arrays.asList(new String[]{"single"}), Arrays.asList(new String[]{"compartiment individuel"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(2);
		termEnrich.setOffsetNoTagsEndIdx(8);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=2,8");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=2,8", Arrays.asList(new String[]{"single"}), Arrays.asList(new String[]{"unique", "simple"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(2);
		termEnrich.setOffsetNoTagsEndIdx(8);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=2,8");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne", "rail"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "information technology and data processing", "A vector path defined by two points and a straight or curved segment between them.");
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"courbe"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"lignée"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"gamme"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "environmental policy", "\"Term used in GIS technologies in the vector type of internal data organization: spatial data are divided into point, line and polygon types. In most cases, point entities (nodes) are specified directly as coordinate pairs, with lines (arcs or edges) represented as chains of points. Regions are similarly defined in terms of the lines which form their boundaries. Some vector GIS store information in the form of points, line segments and point pairs; others maintain closed lists of points defining polygon regions. Vector structures are especially suited to storing definitions of spatial objects for which sharp boundaries exist or can be imposed. (Source: YOUNG)\"");
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"droite"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"compagnie maritime", "limite", "ligne", "compagnie de navigation"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "Radioredaktion", "Fil conducteur métallique, ou fiasceau de fils protégé par des enveloppes isolantes.");
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne", "canalisation", "tuyau de carburant", "trait"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"lignée"}), "AG5 - crops (sn: cultivation and products)  (Lenoch classification);  BZ6 - plant diseases  (Lenoch classification);  Allikas:TE - Technology - Engineering  (Lenoch classification);  TR56 - pedestrians and cyclists  (Lenoch classification);  TRE - transport security (nt: protection against attacks; protection of people and goods)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"palangre"}), "AGB - fishery - aquaculture  (Lenoch classification);  TRA - vehicle disposal - ship disposal - aircraft disposal (rf: vehicle industry, see: INT)  (Lenoch classification);  FI - Financial Affairs - Taxation and Customs (sn: microeconomics 2; nt: further breakdown of ECF)  (Lenoch classification);  JUD1 - author's rights  (Lenoch classification);  JU15 - legal CEs  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"link"}), Arrays.asList(new String[]{"ligne"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"libellé"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(17);
		termEnrich.setOffsetNoTagsEndIdx(21);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=17,21");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=17,21",  Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(17);
		termEnrich.setOffsetNoTagsEndIdx(21);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=17,21");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}), "LAW", null);
		termEnrich.setOffsetNoTagsStartIdx(17);
		termEnrich.setOffsetNoTagsEndIdx(21);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=17,21");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"Text"}), Arrays.asList(new String[]{"Texte"}), "information technology and data processing", "A field data type that can contain up to 255 characters or the number of characters specified by the Fieldsize property, whichever is less.");
		termEnrich.setOffsetNoTagsStartIdx(17);
		termEnrich.setOffsetNoTagsEndIdx(21);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=17,21");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(17);
		termEnrich.setOffsetNoTagsEndIdx(21);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=17,21");
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=22,32", Arrays.asList(new String[]{"contains"}), Arrays.asList(new String[]{"contient"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(22);
		termEnrich.setOffsetNoTagsEndIdx(32);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=22,32");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=22,32", Arrays.asList(new String[]{"contain"}), Arrays.asList(new String[]{"refermer"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(22);
		termEnrich.setOffsetNoTagsEndIdx(32);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=22,32");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pairing"}), Arrays.asList(new String[]{"jumelage"}), "information technology and data processing", "The process of establishing a Bluetooth link or connection between two Bluetooth–enabled devices.");
		termEnrich.setOffsetNoTagsStartIdx(43);
		termEnrich.setOffsetNoTagsEndIdx(49);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=59,65");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pair"}), Arrays.asList(new String[]{"paire"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(43);
		termEnrich.setOffsetNoTagsEndIdx(49);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=59,65");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"Pairing"}), Arrays.asList(new String[]{"Appariement"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(43);
		termEnrich.setOffsetNoTagsEndIdx(49);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=59,65");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pair"}), Arrays.asList(new String[]{"jumeler"}), "information technology and data processing", "To establish a Bluetooth link or connection between two Bluetooth–enabled devices.");
		termEnrich.setOffsetNoTagsStartIdx(43);
		termEnrich.setOffsetNoTagsEndIdx(49);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=59,65");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pair"}), Arrays.asList(new String[]{"couple"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(43);
		termEnrich.setOffsetNoTagsEndIdx(49);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=59,65");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"balise"}), "information technology and data processing", "A marker used to identify a physical object. An RFID tag is an electronic marker that stores identification data.");
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"bout", "étiquette"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"mot-clé"}), "information technology and data processing", "One or more characters containing information about a file, record type, or other structure.");
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"étiquette"}), "international trade", null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"graff", "inscription murale faite à l'aérosol", "tag"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag", "HTML tag", "document control marker"}), Arrays.asList(new String[]{"marqueur", "balise", "ferret", "étiquette"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"étiqueter"}), "information technology and data processing", "To apply an identification marker to an item, case, or pallet.");
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"ear tag", "tag"}), Arrays.asList(new String[]{"pendentif"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77",  Arrays.asList(new String[]{"market"}), Arrays.asList(new String[]{"repère"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"bracelet électronique"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"mot-clef"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"étiquette"}), "communications", null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"repère"}), "information technology and data processing", "A geometrical arrangement of shapes that define a value that the Surface Vision System can recognize. These geometrical arrangements are added to physical objects (then called tagged objects) to work with Surface applications (for example, a glass tile that acts as a puzzle piece in a puzzle application)." );
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"attache"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
		enrichments.add(termEnrich);
		
		seg2Enrichments.put(6, enrichments);
		
		return seg2Enrichments;
	}
	
	public static List<Enrichment> getWritingXliff1_2TestEnrichments(int segNum){
		
		List<Enrichment> enrichments = new ArrayList<Enrichment>();
		
		EntityEnrichment entityEnrich = null;
		TerminologyEnrichment termEnrich = null;
		LinkEnrichment linkEnrich = null;
		List<LinkInfoData> infoList = null;
		ArrayList<Statement> termTriples;
		Model model = ModelFactory.createDefaultModel();
		switch (segNum) {
		case 1:
			
			termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"mile"}), Arrays.asList(new String[]{"mille"}), "international trade", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/847196")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/847196"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("international trade", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/mile-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/847196")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/mile-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("mile", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/mille-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/847196")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/mille-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("mille", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"survey mile"}), Arrays.asList(new String[]{"mille"}), "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/586133")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/586133"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/survey+mile-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/586133")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/survey+mile-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("survey mile", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/mille-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/586133")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/mille-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("mille", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=13,23", Arrays.asList(new String[]{"kilometer"}), Arrays.asList(new String[]{"kilomètre"}), "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(13);
			termEnrich.setOffsetNoTagsEndIdx(23);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=13,23");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=13,23"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/584753")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/584753"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/kilometer-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/584753")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/kilometer-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("kilometer", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/kilom%C3%A8tre-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/584753")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/kilom%C3%A8tre-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("kilomètre", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			entityEnrich = new EntityEnrichment("char=45,52", "http://dbpedia.org/resource/Wicklow");
			entityEnrich.setOffsetNoTagsStartIdx(45);
			entityEnrich.setOffsetNoTagsEndIdx(52);
			entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
			enrichments.add(entityEnrich);
			
			linkEnrich = new LinkEnrichment("char=45,52", "en");
			linkEnrich.setOffsetNoTagsStartIdx(45);
			linkEnrich.setOffsetNoTagsEndIdx(52);
			
			linkEnrich.setEntityName("Wicklow", ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
			linkEnrich.setHomePage("http://www.wicklow.ie", ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
			linkEnrich.setImageURL("http://commons.wikimedia.org/wiki/Special:FilePath/Wicklow_Town_-_geograph.org.uk_-_692370_(cropped).jpg?width=300", ELinkEnrichmentsConstants.IMAGE_PROP);
			linkEnrich.setLongDescription("Wicklow (Irish: Cill Mhantáin, meaning \"church of the toothless one\") is the county town of County Wicklow and the capital of the Mid-East Region in Ireland. Located south of Dublin on the east coast of the island, it has a population of 10,356 according to the 2011 census. The town is to the east of the N11 route between Dublin and Wexford. Wicklow is also linked to the rail network, with Dublin commuter services now extending to the town. Additional services connect with Arklow, Wexford and Rosslare Europort, a main ferry port. There is also a commercial port, mainly importing timber and textiles. The River Vartry is the main river which flows through the town.", ELinkEnrichmentsConstants.LONG_DESCR_PROP);
			linkEnrich.setReferenceEntity("http://dbpedia.org/resource/Wicklow");
			linkEnrich.setShortDescription("Wicklow (Irish: Cill Mhantáin, meaning \"church of the toothless one\") is the county town of County Wicklow and the capital of the Mid-East Region in Ireland. Located south of Dublin on the east coast of the island, it has a population of 10,356 according to the 2011 census. The town is to the east of the N11 route between Dublin and Wexford. Wicklow is also linked to the rail network, with Dublin commuter services now extending to the town. Additional services connect with Arklow, Wexford and Rosslare Europort, a main ferry port. There is also a commercial port, mainly importing timber and textiles. The River Vartry is the main river which flows through the town.", ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
			linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/Wicklow", ELinkEnrichmentsConstants.WIKI_LINK_PROP);
			infoList = new ArrayList<LinkInfoData>();
			LinkInfoData infoData = new LinkInfoData(ELinkEnrichmentsConstants.LONGITUDE_PROP, "Longitude",
			        Float.class);
			infoData.setValue("-6.033");
			infoList.add(infoData);
			infoData = new LinkInfoData(ELinkEnrichmentsConstants.LATITUDE_PROP, "Latitude", Float.class);
			infoData.setValue("52.9779");
			infoList.add(infoData);
			linkEnrich.setInfoList(infoList);
			enrichments.add(linkEnrich);
			
			entityEnrich = new EntityEnrichment("char=57,61", "http://dbpedia.org/resource/Bray");
			entityEnrich.setOffsetNoTagsStartIdx(57);
			entityEnrich.setOffsetNoTagsEndIdx(61);
			entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
			enrichments.add(entityEnrich);
			
			linkEnrich = new LinkEnrichment("char=57,61", "en");
			linkEnrich.setOffsetNoTagsStartIdx(57);
			linkEnrich.setOffsetNoTagsEndIdx(61);
			linkEnrich.setEntityName("Bray", ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
			linkEnrich.setHomePage("http://www.bray.ie", ELinkEnrichmentsConstants.HOMEPAGE_LINK_PROP);
			linkEnrich.setImageURL("http://en.wikipedia.org/wiki/Special:FilePath/Brayview.JPG?width=300", ELinkEnrichmentsConstants.IMAGE_PROP);
			linkEnrich.setLongDescription("Bray (Irish: Bré, meaning \"hill\", formerly Brí Chualann) is a town in north County Wicklow, Ireland. It is a busy urban centre and seaside resort, with a population of 31,872 making it the ninth largest urban area in Ireland at the 2011 census. It is situated about 20 km (12 mi) south of Dublin on the east coast. The town straddles the Dublin-Wicklow border, with a portion of the northern suburbs situated in County Dublin. Bray's scenic location and proximity to Dublin make it a popular destination for tourists and day-trippers from the capital. Bray is home to Ireland's only film studios, Ardmore Studios, hosting Irish and international productions for film, television and advertising. Some light industry is located in the town, with business and retail parks concentrated largely on its southern periphery. Bray town centre has a range of shops serving the consumer needs of the surrounding area. Commuter links between Bray and Dublin are provided by rail, Dublin Bus and the M11 and M50 motorways.", ELinkEnrichmentsConstants.LONG_DESCR_PROP);
			linkEnrich.setReferenceEntity("http://dbpedia.org/resource/Bray");
			linkEnrich.setShortDescription("Bray (Irish: Bré, meaning \"hill\", formerly Brí Chualann) is a town in north County Wicklow, Ireland. It is a busy urban centre and seaside resort, with a population of 31,872 making it the ninth largest urban area in Ireland at the 2011 census. It is situated about 20 km (12 mi) south of Dublin on the east coast. The town straddles the Dublin-Wicklow border, with a portion of the northern suburbs situated in County Dublin.", ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
			linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/Bray", ELinkEnrichmentsConstants.WIKI_LINK_PROP);
			infoList = new ArrayList<LinkInfoData>();
			infoData = new LinkInfoData(ELinkEnrichmentsConstants.LONGITUDE_PROP, "Longitude",
			        Float.class);
			infoData.setValue("-6.11136");
			infoList.add(infoData);
			infoData = new LinkInfoData(ELinkEnrichmentsConstants.LATITUDE_PROP, "Latitude", Float.class);
			infoData.setValue("53.201");
			infoList.add(infoData);
			linkEnrich.setInfoList(infoList);
			enrichments.add(linkEnrich);
			
			termEnrich = new TerminologyEnrichment("char=65,72", Arrays.asList(new String[]{"popular"}), Arrays.asList(new String[]{"populaire"}), "culture and religion", null);
			termEnrich.setOffsetNoTagsStartIdx(65);
			termEnrich.setOffsetNoTagsEndIdx(72);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=65,72");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=65,72"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/757476")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/757476"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("culture and religion", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/popular-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/757476")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/popular-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("popular", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/populaire-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/757476")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/populaire-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("populaire", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=65,72", Arrays.asList(new String[]{"popular"}), Arrays.asList(new String[]{"populaire"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(65);
			termEnrich.setOffsetNoTagsEndIdx(72);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=65,72");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=65,72"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2843523")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/popular-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2843523")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/popular-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("popular", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/populaire-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2843523")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/populaire-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("populaire", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77",  Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"message publicitaire"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(73);
			termEnrich.setOffsetNoTagsEndIdx(77);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2984405")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/message+publicitaire-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2984405")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/message+publicitaire-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("message publicitaire", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2984405")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("spot", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"tache"}), "wood industry", null);
			termEnrich.setOffsetNoTagsStartIdx(73);
			termEnrich.setOffsetNoTagsEndIdx(77);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/278611")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/278611"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("wood industry", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tache-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/278611")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tache-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tache", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/278611")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("spot", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot croaker", "spot"}), Arrays.asList(new String[]{"tambour croca"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(73);
			termEnrich.setOffsetNoTagsEndIdx(77);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2794802")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tambour+croca-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2794802")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tambour+croca-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tambour croca", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot+croaker-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2794802")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot+croaker-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("spot croaker", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2794802")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("spot", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			List<String> targetList = Arrays.asList(new String[]{"remarquer", "point(tache)"});
			termEnrich = new TerminologyEnrichment("char=73,77",  Arrays.asList(new String[]{"spot"}), targetList, null, null);
			termEnrich.setOffsetNoTagsStartIdx(73);
			termEnrich.setOffsetNoTagsEndIdx(77);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2749530")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/point%28tache%29-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2749530")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/point%28tache%29-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("point(tache)", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/remarquer-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2749530")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/remarquer-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("remarquer", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2749530")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("spot", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"point de soudure"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(73);
			termEnrich.setOffsetNoTagsEndIdx(77);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2867069")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/point+de+soudure-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2867069")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/point+de+soudure-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("point de soudure", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2867069")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("spot", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"tache"}), "CH6 - analytical chemistry (sn: principles and methods; rf: industrial analysis, see: TEO)  (Lenoch classification);  TR45 - railway maintenance  (Lenoch classification);  JUD1 - author's rights  (Lenoch classification);  TR56 - pedestrians and cyclists  (Lenoch classification);  TRE - transport security (nt: protection against attacks; protection of people and goods)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
			termEnrich.setOffsetNoTagsStartIdx(73);
			termEnrich.setOffsetNoTagsEndIdx(77);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1002136")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1002136"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("CH6 - analytical chemistry (sn: principles and methods; rf: industrial analysis, see: TEO)  (Lenoch classification);  TR45 - railway maintenance  (Lenoch classification);  JUD1 - author's rights  (Lenoch classification);  TR56 - pedestrians and cyclists  (Lenoch classification);  TRE - transport security (nt: protection against attacks; protection of people and goods)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tache-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1002136")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tache-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tache", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1002136")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("spot", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"point"}), "information and information processing", null);
			termEnrich.setOffsetNoTagsStartIdx(73);
			termEnrich.setOffsetNoTagsEndIdx(77);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/406663")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/406663"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information and information processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/point-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/406663")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/point-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("point", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/406663")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/spot-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("spot", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=82,90", Arrays.asList(new String[]{"Visitors"}), Arrays.asList(new String[]{"Visiteurs"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(82);
			termEnrich.setOffsetNoTagsEndIdx(90);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=82,90");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=82,90"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2779354")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Visiteurs-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2779354")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Visiteurs-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("Visiteurs", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Visitors-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2779354")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Visitors-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("Visitors", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=82,90", Arrays.asList(new String[]{"visitor"}), Arrays.asList(new String[]{"visiteur"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(82);
			termEnrich.setOffsetNoTagsEndIdx(90);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=82,90");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=82,90"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2766442")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/visiteur-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2766442")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/visiteur-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("visiteur", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/visitor-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2766442")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/visitor-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("visitor", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=91,101", Arrays.asList(new String[]{"wishing to"}), Arrays.asList(new String[]{"désireux"}), "international trade", null);
			termEnrich.setOffsetNoTagsStartIdx(91);
			termEnrich.setOffsetNoTagsEndIdx(101);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=91,101");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=91,101"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/856999")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/856999"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("international trade", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/d%C3%A9sireux-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/856999")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/d%C3%A9sireux-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("désireux", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/wishing+to-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/856999")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/wishing+to-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("wishing to", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=102,110", Arrays.asList(new String[]{"discover"}), Arrays.asList(new String[]{"interroger au préalable"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(102);
			termEnrich.setOffsetNoTagsEndIdx(110);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=102,110");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=102,110"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2762101")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/interroger+au+pr%C3%A9alable-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2762101")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/interroger+au+pr%C3%A9alable-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("interroger au préalable", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/discover-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2762101")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/discover-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("discover", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=102,110", Arrays.asList(new String[]{"discover"}), Arrays.asList(new String[]{"inventer"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(102);
			termEnrich.setOffsetNoTagsEndIdx(110);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=102,110");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=102,110"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2749124")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/inventer-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2749124")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/inventer-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("inventer", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/discover-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2749124")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/discover-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("discover", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "SCIENCE", null);
			termEnrich.setOffsetNoTagsStartIdx(123);
			termEnrich.setOffsetNoTagsEndIdx(129);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=123,129"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/362257")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/362257"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("SCIENCE", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/362257")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("région", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/362257")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("region", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "FINANCE", null);
			termEnrich.setOffsetNoTagsStartIdx(123);
			termEnrich.setOffsetNoTagsEndIdx(129);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=123,129"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/639178")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/639178"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("FINANCE", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/639178")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("région", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/639178")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("region", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(123);
			termEnrich.setOffsetNoTagsEndIdx(129);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=123,129"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2905190")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2905190")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("région", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2905190")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("region", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"regions"}), Arrays.asList(new String[]{"régions"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(123);
			termEnrich.setOffsetNoTagsEndIdx(129);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=123,129"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2746268")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gions-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2746268")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gions-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("régions", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/regions-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2746268")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/regions-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("regions", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "environmental policy", "\"A designated area or an administrative division of a city, county or larger geographical territory that is formulated according to some biological, political, economic or demographic criteria. (Source: RHW / ISEP)\"");
			termEnrich.setOffsetNoTagsStartIdx(123);
			termEnrich.setOffsetNoTagsEndIdx(129);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=123,129"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/655082")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/655082"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("environmental policy", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/655082"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("\"A designated area or an administrative division of a city, county or larger geographical territory that is formulated according to some biological, political, economic or demographic criteria. (Source: RHW / ISEP)\"", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/655082")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("région", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/655082")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("region", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "information and information processing", null);
			termEnrich.setOffsetNoTagsStartIdx(123);
			termEnrich.setOffsetNoTagsEndIdx(129);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=123,129"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/616979")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/616979"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information and information processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/616979")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("région", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/616979")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("region", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"space"}), Arrays.asList(new String[]{"zone"}), "information technology and data processing", null);
			termEnrich.setOffsetNoTagsStartIdx(123);
			termEnrich.setOffsetNoTagsEndIdx(129);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=123,129"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/405404")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/405404"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/space-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/405404")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/space-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("space", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/zone-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/405404")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/zone-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("zone", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"range"}), Arrays.asList(new String[]{"zone"}), "information and information processing", null);
			termEnrich.setOffsetNoTagsStartIdx(123);
			termEnrich.setOffsetNoTagsEndIdx(129);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=123,129"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/408230")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/408230"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information and information processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/zone-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/408230")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/zone-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("zone", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/range-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/408230")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/range-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("range", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"oblast"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(123);
			termEnrich.setOffsetNoTagsEndIdx(129);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=123,129"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2951473")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/oblast-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2951473")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/oblast-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("oblast", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2951473")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("region", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "information technology and data processing", null);
			termEnrich.setOffsetNoTagsStartIdx(123);
			termEnrich.setOffsetNoTagsEndIdx(129);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=123,129");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=123,129"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/622643")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/622643"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/622643")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/r%C3%A9gion-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("région", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/622643")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/region-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("region", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			
			break;
		case 2:
			enrichments = new ArrayList<Enrichment>();
			termEnrich = new TerminologyEnrichment("char=2,8", Arrays.asList(new String[]{"single"}), Arrays.asList(new String[]{"célibataire"}), "information technology and data processing", null);
			termEnrich.setOffsetNoTagsStartIdx(2);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=2,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=2,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/624141")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/624141"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/c%C3%A9libataire-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/624141")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/c%C3%A9libataire-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("célibataire", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/single-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/624141")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/single-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("single", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=2,8", Arrays.asList(new String[]{"single"}), Arrays.asList(new String[]{"compartiment individuel"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(2);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=2,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=2,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2992413")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/compartiment+individuel-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2992413")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/compartiment+individuel-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("compartiment individuel", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/single-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2992413")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/single-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("single", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=2,8", Arrays.asList(new String[]{"single"}), Arrays.asList(new String[]{"unique", "simple"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(2);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=2,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=2,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2750116")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/unique-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2750116")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/unique-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("unique", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/single-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2750116")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/single-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("single", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/simple-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2750116")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/simple-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("simple", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne", "rail"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2997242")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2997242")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ligne", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/rail-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2997242")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/rail-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("rail", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2997242")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "information technology and data processing", "In word processing, a string of characters displayed or printed in a single horizontal row.");
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1224377")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1224377"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1224377"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("In word processing, a string of characters displayed or printed in a single horizontal row.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1224377")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ligne", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1224377")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"courbe"}), "information technology and data processing", null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/620725")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/620725"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/courbe-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/620725")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/courbe-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("courbe", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/620725")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"lignée"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/3133777")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/lign%C3%A9e-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3133777")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/lign%C3%A9e-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("lignée", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3133777")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"gamme"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/3031785")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/gamme-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3031785")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/gamme-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("gamme", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3031785")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "environmental policy", "\"Term used in GIS technologies in the vector type of internal data organization: spatial data are divided into point, line and polygon types. In most cases, point entities (nodes) are specified directly as coordinate pairs, with lines (arcs or edges) represented as chains of points. Regions are similarly defined in terms of the lines which form their boundaries. Some vector GIS store information in the form of points, line segments and point pairs; others maintain closed lists of points defining polygon regions. Vector structures are especially suited to storing definitions of spatial objects for which sharp boundaries exist or can be imposed. (Source: YOUNG)\"");
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/657132")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/657132"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("environmental policy", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/657132"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("\"Term used in GIS technologies in the vector type of internal data organization: spatial data are divided into point, line and polygon types. In most cases, point entities (nodes) are specified directly as coordinate pairs, with lines (arcs or edges) represented as chains of points. Regions are similarly defined in terms of the lines which form their boundaries. Some vector GIS store information in the form of points, line segments and point pairs; others maintain closed lists of points defining polygon regions. Vector structures are especially suited to storing definitions of spatial objects for which sharp boundaries exist or can be imposed. (Source: YOUNG)\"", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/657132")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ligne", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/657132")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"droite"}), "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/592647")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/592647"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/592647")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/droite-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/592647")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/droite-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("droite", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2741440")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2741440")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ligne", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2741440")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"compagnie maritime", "limite", "ligne", "compagnie de navigation"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2758314")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/compagnie+maritime-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2758314")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/compagnie+maritime-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("compagnie maritime", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/limite-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2758314")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/limite-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("limite", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2758314")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ligne", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/compagnie+de+navigation-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2758314")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/compagnie+de+navigation-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("compagnie de navigation", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2758314")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "Radioredaktion", "Fil conducteur métallique, ou fiasceau de fils protégé par des enveloppes isolantes.");
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1138856")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1138856"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("Radioredaktion", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1138856"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("Fil conducteur métallique, ou fiasceau de fils protégé par des enveloppes isolantes.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1138856")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ligne", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1138856")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne", "canalisation", "tuyau de carburant", "trait"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2747308")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2747308")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ligne", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/canalisation-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2747308")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/canalisation-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("canalisation", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tuyau+de+carburant-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2747308")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tuyau+de+carburant-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tuyau de carburant", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2747308")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/trait-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2747308")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/trait-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("trait", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/389705")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/389705"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/389705")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ligne", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/389705")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"lignée"}), "AG5 - crops (sn: cultivation and products)  (Lenoch classification);  BZ6 - plant diseases  (Lenoch classification);  Allikas:TE - Technology - Engineering  (Lenoch classification);  TR56 - pedestrians and cyclists  (Lenoch classification);  TRE - transport security (nt: protection against attacks; protection of people and goods)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1000324")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1000324"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("AG5 - crops (sn: cultivation and products)  (Lenoch classification);  BZ6 - plant diseases  (Lenoch classification);  Allikas:TE - Technology - Engineering  (Lenoch classification);  TR56 - pedestrians and cyclists  (Lenoch classification);  TRE - transport security (nt: protection against attacks; protection of people and goods)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/lign%C3%A9e-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1000324")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/lign%C3%A9e-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("lignée", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1000324")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"palangre"}), "AGB - fishery - aquaculture  (Lenoch classification);  TRA - vehicle disposal - ship disposal - aircraft disposal (rf: vehicle industry, see: INT)  (Lenoch classification);  FI - Financial Affairs - Taxation and Customs (sn: microeconomics 2; nt: further breakdown of ECF)  (Lenoch classification);  JUD1 - author's rights  (Lenoch classification);  JU15 - legal CEs  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/973841")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/973841"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("AGB - fishery - aquaculture  (Lenoch classification);  TRA - vehicle disposal - ship disposal - aircraft disposal (rf: vehicle industry, see: INT)  (Lenoch classification);  FI - Financial Affairs - Taxation and Customs (sn: microeconomics 2; nt: further breakdown of ECF)  (Lenoch classification);  JUD1 - author's rights  (Lenoch classification);  JU15 - legal CEs  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/palangre-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/973841")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/palangre-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("palangre", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/973841")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/line-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("line", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"link"}), Arrays.asList(new String[]{"ligne"}), "information and information processing", null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(13);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,13");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,13"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/398263")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/398263"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information and information processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/398263")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ligne-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ligne", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/link-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/398263")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/link-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("link", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"libellé"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(17);
			termEnrich.setOffsetNoTagsEndIdx(21);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=17,21");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=17,21"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/3147064")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/libell%C3%A9-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3147064")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/libell%C3%A9-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("libellé", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/text-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3147064")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/text-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("text", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=17,21",  Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}), "information technology and data processing", null);
			termEnrich.setOffsetNoTagsStartIdx(17);
			termEnrich.setOffsetNoTagsEndIdx(21);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=17,21");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=17,21"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/624610")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/624610"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/texte-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/624610")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/texte-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("texte", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/text-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/624610")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/text-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("text", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}), "LAW", null);
			termEnrich.setOffsetNoTagsStartIdx(17);
			termEnrich.setOffsetNoTagsEndIdx(21);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=17,21");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=17,21"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/367387")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/367387"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("LAW", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/texte-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/367387")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/texte-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("texte", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/text-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/367387")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/text-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("text", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"Text"}), Arrays.asList(new String[]{"Texte"}), "information technology and data processing", "A field data type that can contain up to 255 characters or the number of characters specified by the Fieldsize property, whichever is less.");
			termEnrich.setOffsetNoTagsStartIdx(17);
			termEnrich.setOffsetNoTagsEndIdx(21);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=17,21");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=17,21"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1232555")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232555"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232555"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("A field data type that can contain up to 255 characters or the number of characters specified by the Fieldsize property, whichever is less.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Text-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232555")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Text-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("Text", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Texte-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232555")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Texte-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("Texte", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}), "information and information processing", null);
			termEnrich.setOffsetNoTagsStartIdx(17);
			termEnrich.setOffsetNoTagsEndIdx(21);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=17,21");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=17,21"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/406755")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/406755"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information and information processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/texte-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/406755")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/texte-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("texte", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/text-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/406755")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/text-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("text", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=22,32", Arrays.asList(new String[]{"contains"}), Arrays.asList(new String[]{"contient"}), "information technology and data processing", null);
			termEnrich.setOffsetNoTagsStartIdx(22);
			termEnrich.setOffsetNoTagsEndIdx(32);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=22,32");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=22,32"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/622303")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/622303"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/contains-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/622303")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/contains-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("contains", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/contient-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/622303")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/contient-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("contient", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=22,32", Arrays.asList(new String[]{"contain"}), Arrays.asList(new String[]{"refermer"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(22);
			termEnrich.setOffsetNoTagsEndIdx(32);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=22,32");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=22,32"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2749711")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/contain-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2749711")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/contain-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("contain", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/refermer-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2749711")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/refermer-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("refermer", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pairing"}), Arrays.asList(new String[]{"jumelage"}), "information technology and data processing", "The process of establishing a Bluetooth link or connection between two Bluetooth–enabled devices.");
			termEnrich.setOffsetNoTagsStartIdx(43);
			termEnrich.setOffsetNoTagsEndIdx(49);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=59,65");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=59,65"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1227171")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1227171"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1227171"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("The process of establishing a Bluetooth link or connection between two Bluetooth–enabled devices.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pairing-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1227171")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pairing-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("pairing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/jumelage-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1227171")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/jumelage-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("jumelage", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pair"}), Arrays.asList(new String[]{"paire"}), "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(43);
			termEnrich.setOffsetNoTagsEndIdx(49);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=59,65");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=59,65"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/587621")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/587621"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/paire-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/587621")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/paire-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("paire", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pair-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/587621")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pair-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("pair", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"Pairing"}), Arrays.asList(new String[]{"Appariement"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(43);
			termEnrich.setOffsetNoTagsEndIdx(49);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=59,65");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=59,65"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2730805")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Pairing-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2730805")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Pairing-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("Pairing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Appariement-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2730805")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/Appariement-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("Appariement", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pair"}), Arrays.asList(new String[]{"jumeler"}), "information technology and data processing", "To establish a Bluetooth link or connection between two Bluetooth–enabled devices.");
			termEnrich.setOffsetNoTagsStartIdx(43);
			termEnrich.setOffsetNoTagsEndIdx(49);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=59,65");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=59,65"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1227169")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1227169"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1227169"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("To establish a Bluetooth link or connection between two Bluetooth–enabled devices.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/jumeler-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1227169")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/jumeler-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("jumeler", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pair-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1227169")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pair-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("pair", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pair"}), Arrays.asList(new String[]{"couple"}), "information and information processing", null);
			termEnrich.setOffsetNoTagsStartIdx(43);
			termEnrich.setOffsetNoTagsEndIdx(49);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=59,65");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=59,65"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/400734")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/400734"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information and information processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pair-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/400734")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pair-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("pair", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/couple-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/400734")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/couple-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("couple", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"balise"}), "information technology and data processing", "A marker used to identify a physical object. An RFID tag is an electronic marker that stores identification data.");
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1232241")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232241"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232241"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("A marker that can be applied to content or items (like photos or text) to identify certain types of information. This allows the user to find, view and sort tagged items with ease.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/balise-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232241")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/balise-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("balise", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232241")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"bout", "étiquette"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2748159")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/bout-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748159")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/bout-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("bout", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748159")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9tiquette-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748159")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9tiquette-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("étiquette", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"mot-clé"}), "information technology and data processing", "One or more characters containing information about a file, record type, or other structure.");
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1232243")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232243"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232243"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("One or more characters containing information about a file, record type, or other structure.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/mot-cl%C3%A9-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232243")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/mot-cl%C3%A9-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("mot-clé", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232243")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"étiquette"}), "international trade", null);
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/855542")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/855542"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("international trade", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/855542")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9tiquette-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/855542")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9tiquette-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("étiquette", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"graff", "inscription murale faite à l'aérosol", "tag"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2890440")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/graff-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2890440")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/graff-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("graff", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/inscription+murale+faite+%C3%A0+l'a%C3%A9rosol-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2890440")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/inscription+murale+faite+%C3%A0+l'a%C3%A9rosol-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("inscription murale faite à l'aérosol", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2890440")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2890440")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag", "HTML tag", "document control marker"}), Arrays.asList(new String[]{"marqueur", "balise", "ferret", "étiquette"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2955474")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/marqueur-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2955474")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/marqueur-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("marqueur", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/balise-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2955474")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/balise-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("balise", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ferret-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2955474")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ferret-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ferret", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2955474")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/HTML+tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2955474")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/HTML+tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("HTML tag", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/document+control+marker-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2955474")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/document+control+marker-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("document control marker", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9tiquette-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2955474")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9tiquette-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("étiquette", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"étiqueter"}), "information technology and data processing", "To apply an identification marker to an item, case, or pallet.");
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1232244")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232244"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232244"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("To apply an identification marker to an item, case, or pallet.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9tiqueter-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232244")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9tiqueter-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("étiqueter", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232244")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"ear tag", "tag"}), Arrays.asList(new String[]{"pendentif"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2891402")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ear+tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2891402")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/ear+tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("ear tag", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2891402")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pendentif-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2891402")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pendentif-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("pendentif", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77",  Arrays.asList(new String[]{"market"}), Arrays.asList(new String[]{"repère"}), "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1232240")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232240"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232240"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("A geometrical arrangement of shapes that define a value that the Surface Vision System can recognize. These geometrical arrangements are added to physical objects (then called tagged objects) to work with Surface applications (for example, a glass tile that acts as a puzzle piece in a puzzle application).", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/rep%C3%A8re-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232240")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/rep%C3%A8re-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("repère", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232240")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"bracelet électronique"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2996296")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/bracelet+%C3%A9lectronique-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2996296")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/bracelet+%C3%A9lectronique-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("bracelet électronique", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2996296")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"mot-clef"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/3302470")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/mot-clef-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3302470")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/mot-clef-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("mot-clef", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3302470")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"étiquette"}), "communications", null);
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/537890")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/537890"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("communications", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/537890")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9tiquette-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/537890")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9tiquette-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("étiquette", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"repère"}), "information technology and data processing", "A geometrical arrangement of shapes that define a value that the Surface Vision System can recognize. These geometrical arrangements are added to physical objects (then called tagged objects) to work with Surface applications (for example, a glass tile that acts as a puzzle piece in a puzzle application)." );
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1232240")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232240"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1232240"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("A geometrical arrangement of shapes that define a value that the Surface Vision System can recognize. These geometrical arrangements are added to physical objects (then called tagged objects) to work with Surface applications (for example, a glass tile that acts as a puzzle piece in a puzzle application).", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/rep%C3%A8re-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232240")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/rep%C3%A8re-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("repère", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1232240")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"attache"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(50);
			termEnrich.setOffsetNoTagsEndIdx(54);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=73,77");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=73,77"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/3133778")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/attache-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3133778")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/attache-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("attache", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3133778")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/tag-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("tag", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			break;

		default:
			break;
		}
		
		return enrichments;
	}

	public static List<Enrichment> getWritingXliff2_0TestEnrichments(String segId){
		
		List<Enrichment> enrichments = new ArrayList<Enrichment>();
		TerminologyEnrichment termEnrich = null;
		List<Statement> termTriples = null;
		Model model = ModelFactory.createDefaultModel();
		
		switch (segId) {
		case "s1":
			termEnrich = new TerminologyEnrichment(
			        "char=0,8", Arrays.asList(new String[]{"sentence"}), Arrays.asList(new String[]{"phrase"}),
			        "information technology and data processing", null);
			termEnrich.setOffsetNoTagsStartIdx(0);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=0,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=0,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/623645")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/623645"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/phrase-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/623645")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/phrase-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("phrase", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/sentence-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/623645")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/sentence-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("sentence", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=0,8", Arrays.asList(new String[]{"sentence"}), Arrays.asList(new String[]{"condamnation", "peine", "sentence"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(0);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=0,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=0,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2766333")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/peine-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2766333")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/peine-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("peine", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/condamnation-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2766333")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/condamnation-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("condamnation", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/sentence-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2766333")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/sentence-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("sentence", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/sentence-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2766333")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/sentence-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("sentence", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=28,36", Arrays.asList(new String[]{"phrase", "sentence"}), Arrays.asList(new String[]{"phrase"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(15);
			termEnrich.setOffsetNoTagsEndIdx(23);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=28,36");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=0,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/3102851")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/phrase-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3102851")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/phrase-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("phrase", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/phrase-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3102851")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/phrase-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("phrase", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/sentence-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3102851")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/sentence-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("sentence", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=28,36", Arrays.asList(new String[]{"decree"}), Arrays.asList(new String[]{"verdict"}),
			        "civil law", null);
			termEnrich.setOffsetNoTagsStartIdx(15);
			termEnrich.setOffsetNoTagsEndIdx(23);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=28,36");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=28,36"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/627030")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/627030"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("civil law", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/decree-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/627030")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/decree-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("decree", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/verdict-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/627030")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/verdict-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("verdict", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=28,36", Arrays.asList(new String[]{"judgement", "sentence"}), Arrays.asList(new String[]{"arrêt"}),
			         null, null);
			termEnrich.setOffsetNoTagsStartIdx(15);
			termEnrich.setOffsetNoTagsEndIdx(23);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=28,36");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=0,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2741305")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/arr%C3%AAt-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2741305")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/arr%C3%AAt-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("arrêt", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/judgement-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2741305")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/judgement-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("judgement", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/sentence-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2741305")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/sentence-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("sentence", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			break;
		case "s3":
			enrichments = new ArrayList<Enrichment>();
			
			EntityEnrichment entityEnrich = new EntityEnrichment("char=0,2", "http://dbpedia.org/resource/PH");
			entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
			entityEnrich.setOffsetNoTagsStartIdx(0);
			entityEnrich.setOffsetNoTagsEndIdx(2);
			enrichments.add(entityEnrich);
			
			LinkEnrichment linkEnrich = new LinkEnrichment("char=0,2", "en");
			linkEnrich.setOffsetNoTagsStartIdx(0);
			linkEnrich.setOffsetNoTagsEndIdx(2);
			linkEnrich.setEntityName("PH",
			        ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
			linkEnrich
			        .setImageURL(
			                "http://commons.wikimedia.org/wiki/Special:FilePath/Lemon.jpg?width=300",
			                ELinkEnrichmentsConstants.IMAGE_PROP);
			linkEnrich
			        .setLongDescription(
			                "In chemistry, pH (/piːˈeɪtʃ/) is a numeric scale used to specify the acidity or basicity(alkalinity) of an aqueous solution. It is roughly the negative of the logarithm to base 10 of the concentration, measured in units of moles per liter, of hydrogen ions. More precisely it is the negative of the logarithm to base 10 of the activity of the hydrogen ion. Solutions with a pH less than 7 are acidic and solutions with a pH greater than 7 are basic. Pure water is neutral, being neither an acid nor a base. Contrary to popular belief, the pH value can be less than 0 or greater than 14 for very strong acids and bases respectively. pH measurements are important in agronomy, medicine, biology, chemistry, agriculture, forestry, food science, environmental science, oceanography, civil engineering, chemical engineering, nutrition, water treatment & water purification, as well as many other applications. The pH scale is traceable to a set of standard solutions whose pH is established by international agreement.Primary pH standard values are determined using a concentration cell with transference, by measuring the potential difference between a hydrogen electrode and a standard electrode such as the silver chloride electrode. The pH of aqueous solutions can be measured with a glass electrode and a pH meter, or indicator.",
			                ELinkEnrichmentsConstants.LONG_DESCR_PROP);
			linkEnrich.setReferenceEntity("http://dbpedia.org/resource/PH");
			linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/PH",
			        ELinkEnrichmentsConstants.WIKI_LINK_PROP);
			linkEnrich
			        .setShortDescription(
			                "In chemistry, pH (/piːˈeɪtʃ/) is a numeric scale used to specify the acidity or basicity(alkalinity) of an aqueous solution. It is roughly the negative of the logarithm to base 10 of the concentration, measured in units of moles per liter, of hydrogen ions. More precisely it is the negative of the logarithm to base 10 of the activity of the hydrogen ion. Solutions with a pH less than 7 are acidic and solutions with a pH greater than 7 are basic. Pure water is neutral, being neither an acid nor a base. Contrary to popular belief, the pH value can be less than 0 or greater than 14 for very strong acids and bases respectively.",
			                ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
			enrichments.add(linkEnrich);
			
			termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,10"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2748786")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748786")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748786")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,10",Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,10"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/583221")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/583221"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/583221")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/583221")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "communications", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,10"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/534143")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/534143"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("communications", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/534143")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/534143")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"section"}), Arrays.asList(new String[]{"maille"}), "information and information processing", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,10"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/392422")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/392422"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information and information processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/maille-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/392422")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/maille-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("maille", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/section-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/392422")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/section-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("section", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,10",  Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "information technology and data processing", "The representation of an object in a XAML file.");
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,16"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1220854")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1220854"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1220854"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("The representation of an object in a XAML file.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1220854")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1220854")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"cell"}), Arrays.asList(new String[]{"pile"}),
			        "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,10"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/390042")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/390042"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/cell-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/390042")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/cell-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("cell", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pile-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/390042")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pile-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("pile", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			break;
		case "s4":
			enrichments = new ArrayList<Enrichment>();
			termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,10"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2748786")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748786")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748786")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,10",Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,10"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/583221")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/583221"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/583221")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/583221")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "communications", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,10"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/534143")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/534143"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("communications", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/534143")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/534143")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"section"}), Arrays.asList(new String[]{"maille"}), "information and information processing", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,10"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/392422")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/392422"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information and information processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/maille-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/392422")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/maille-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("maille", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/section-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/392422")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/section-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("section", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,10",  Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "information technology and data processing", "The representation of an object in a XAML file.");
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,16"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1220854")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1220854"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1220854"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("The representation of an object in a XAML file.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1220854")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1220854")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,10", Arrays.asList(new String[]{"cell"}), Arrays.asList(new String[]{"pile"}),
			        "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(10);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,10");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,10"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/390042")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/390042"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/cell-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/390042")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/cell-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("cell", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pile-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/390042")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pile-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("pile", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			entityEnrich = new EntityEnrichment("char=18,27",
			        "http://dbpedia.org/resource/House");
			entityEnrich.setOffsetNoTagsStartIdx(11);
			entityEnrich.setOffsetNoTagsEndIdx(20);
			entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
			enrichments.add(entityEnrich);

			linkEnrich = new LinkEnrichment("char=18,27", "en");
			linkEnrich.setOffsetNoTagsStartIdx(11);
			linkEnrich.setOffsetNoTagsEndIdx(20);
			linkEnrich.setEntityName("House",
			        ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
			linkEnrich
			        .setImageURL(
			                "http://commons.wikimedia.org/wiki/Special:FilePath/Vasskertentrance.jpg?width=300",
			                ELinkEnrichmentsConstants.IMAGE_PROP);
			linkEnrich
			        .setLongDescription(
			                "A house is a building that functions as a home, ranging from simple dwellings such as rudimentary huts of nomadic tribes and the improvised shacks in shantytowns to complex, fixed structures of wood, brick, marble or other materials containing plumbing, ventilation and electrical systems. Most conventional modern houses in Western cultures will contain a bedroom, bathroom, kitchen or cooking area, and a living room. In traditional agriculture-oriented societies, domestic animals such as chickens or larger livestock (like cattle) may share part of the house with humans. The social unit that lives in a house is known as a household. Most commonly, a household is a family unit of some kind, although households may also be other social groups or individuals. The design and structure of the house is also subject to change as a consequence of globalization, urbanization and other social, economic, demographic, and technological reasons. Various other cultural factors also influence the building style and patterns of domestic space.",
			                ELinkEnrichmentsConstants.LONG_DESCR_PROP);
			linkEnrich.setReferenceEntity("http://dbpedia.org/resource/House");
			linkEnrich.setWikiPage("http://en.wikipedia.org/wiki/House",
			        ELinkEnrichmentsConstants.WIKI_LINK_PROP);
			linkEnrich
			        .setShortDescription(
			                "A house is a building that functions as a home, ranging from simple dwellings such as rudimentary huts of nomadic tribes and the improvised shacks in shantytowns to complex, fixed structures of wood, brick, marble or other materials containing plumbing, ventilation and electrical systems. Most conventional modern houses in Western cultures will contain a bedroom, bathroom, kitchen or cooking area, and a living room. In traditional agriculture-oriented societies, domestic animals such as chickens or larger livestock (like cattle) may share part of the house with humans. The social unit that lives in a house is known as a household. Most commonly, a household is a family unit of some kind, although households may also be other social groups or individuals. The design and structure of the hou",
			                ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
			enrichments.add(linkEnrich);
			break;
		case "s7":
			enrichments = new ArrayList<Enrichment>();
//			entityEnrich = new EntityEnrichment("char=0,2",
//			        "http://dbpedia.org/resource/James_Edward_Smith");
//			entityEnrich.setOffsetNoTagsStartIdx(0);
//			entityEnrich.setOffsetNoTagsEndIdx(2);
//			entityEnrich.setAnnotatorRef("http://spotlight.dbpedia.org/");
//			enrichments.add(entityEnrich);
//
//			linkEnrich = new LinkEnrichment("char=0,2", "en");
//			linkEnrich.setOffsetNoTagsStartIdx(0);
//			linkEnrich.setOffsetNoTagsEndIdx(2);
//			linkEnrich.setEntityName("James Edward Smith",
//			        ELinkEnrichmentsConstants.ENTITY_NAME_PROP);
//			linkEnrich
//			        .setImageURL(
//			                "http://commons.wikimedia.org/wiki/Special:FilePath/James_Edward_Smith.jpg?width=300",
//			                ELinkEnrichmentsConstants.IMAGE_PROP);
//			linkEnrich
//			        .setLongDescription(
//			                "Sir James Edward Smith (2 December 1759 – 17 March 1828) was an English botanist and founder of the Linnean Society.",
//			                ELinkEnrichmentsConstants.LONG_DESCR_PROP);
//			linkEnrich
//			        .setReferenceEntity("http://dbpedia.org/resource/James_Edward_Smith");
//			linkEnrich
//			        .setShortDescription(
//			                "Sir James Edward Smith (2 December 1759 – 17 March 1828) was an English botanist and founder of the Linnean Society.",
//			                ELinkEnrichmentsConstants.SHORT_DESCR_PROP);
//			linkEnrich.setWikiPage(
//			        "http://en.wikipedia.org/wiki/James_Edward_Smith",
//			        ELinkEnrichmentsConstants.WIKI_LINK_PROP);
//			List<LinkInfoData> infoList = new ArrayList<LinkInfoData>();
//			LinkInfoData info = new LinkInfoData(
//			        ELinkEnrichmentsConstants.BIRTHDATE_PROP, "Birth Date",
//			        Date.class);
//			info.setValue("1759-12-02");
//			infoList.add(info);
//			info = new LinkInfoData(ELinkEnrichmentsConstants.DEATHDATE_PROP,
//			        "Death Date", Date.class);
//			info.setValue("1828-03-17");
//			infoList.add(info);
//			info = new LinkInfoData(ELinkEnrichmentsConstants.BIRTH_PLACE_PROP,
//			        "Birth Place", String.class);
//			info.setValue("http://dbpedia.org/resource/Norwich");
//			infoList.add(info);
//			linkEnrich.setInfoList(infoList);
//			enrichments.add(linkEnrich);
			
			termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"divide", "division", "split"}), Arrays.asList(new String[]{"clivage"}),
			        null, null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/3039345")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/divide-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3039345")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/divide-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("divide", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/clivage-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3039345")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/clivage-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("clivage", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3039345")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("split", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/division-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/3039345")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/division-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("division", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"traversing crack"}), Arrays.asList(new String[]{"fente transversante"}), "wood industry", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/274273")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/274273"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("wood industry", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/traversing+crack-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/274273")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/traversing+crack-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("traversing crack", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fente+transversante-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/274273")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fente+transversante-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("fente transversante", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"half-round log"}), Arrays.asList(new String[]{"moitié de bille"}), "wood industry", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/278608")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/278608"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("wood industry", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/moiti%C3%A9+de+bille-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/278608")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/moiti%C3%A9+de+bille-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("moitié de bille", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/half-round+log-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/278608")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/half-round+log-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("half-round log", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fractionnement"}), "information technology and data processing",
			        null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/624250")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/624250"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fractionnement-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/624250")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fractionnement-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("fractionnement", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/624250")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("split", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fractionner"}), "information technology and data processing",
			        "To divide an audio or video clip into two clips.");
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1231404")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1231404"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1231404"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("To divide an audio or video clip into two clips.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fractionner-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1231404")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fractionner-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("fractionner", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1231404")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("split", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment(
			        "char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fractionner"})
			    ,
			        "en:EC - Economics (sn: macroeconomics; nt: generic field; rf: Commerce and Movement of Goods, see: CO; Financial Affairs - Taxation - Customs, see: FI)  (Lenoch classification);  AGG - foodstuff production  (Lenoch classification);  FI - Financial Affairs - Taxation and Customs (sn: microeconomics 2; nt: further breakdown of ECF)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);  CEP - Treaties - Agreements - Conventions - Cooperation - Commonity Policy - Commercial Policy  (Lenoch classification);",
			        null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/979551")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/979551"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("en:EC - Economics (sn: macroeconomics; nt: generic field; rf: Commerce and Movement of Goods, see: CO; Financial Affairs - Taxation - Customs, see: FI)  (Lenoch classification);  AGG - foodstuff production  (Lenoch classification);  FI - Financial Affairs - Taxation and Customs (sn: microeconomics 2; nt: further breakdown of ECF)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);  CEP - Treaties - Agreements - Conventions - Cooperation - Commonity Policy - Commercial Policy  (Lenoch classification);", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fractionner-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/979551")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fractionner-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("fractionner", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/979551")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("split", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			
			termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"break", "crack", "split"}), Arrays.asList(new String[]{"brisure", "crevasse", "fente"}),
			        null, null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2918666")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/brisure-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2918666")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/brisure-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("brisure", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/break-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2918666")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/break-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("break", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/crack-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2918666")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/crack-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("crack", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fente-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2918666")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fente-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("fente", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2918666")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("split", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/crevasse-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2918666")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/crevasse-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("crevasse", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fendre", "fente"}),
			        null, null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2748864")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fente-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748864")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fente-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("fente", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fendre-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748864")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fendre-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("fendre", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748864")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("split", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"split"}), Arrays.asList(new String[]{"fendu"}),
			        "wood industry", null);
			termEnrich.setOffsetNoTagsStartIdx(3);
			termEnrich.setOffsetNoTagsEndIdx(8);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=3,8");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=3,8"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/279665")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/279665"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("wood industry", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fendu-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/279665")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/fendu-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("fendu", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/279665")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/split-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("split", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), null, null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(16);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,16");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,16"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/2748786")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748786")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/2748786")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(16);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,16");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,16"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/583221")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/583221"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/583221")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/583221")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "communications", null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(16);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,16");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,16"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/534143")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/534143"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("communications", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/534143")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/534143")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"section"}), Arrays.asList(new String[]{"maille"}), "information and information processing", null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(16);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,16");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,16"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/392422")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/392422"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information and information processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/maille-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/392422")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/maille-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("maille", "fr")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/section-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/392422")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/section-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("section", "en")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"element"}), Arrays.asList(new String[]{"élément"}), "information technology and data processing", "The representation of an object in a XAML file.");
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(16);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,16");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,16"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/1220854")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1220854"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("information technology and data processing", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/1220854"), model.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition"), model.createLiteral("The representation of an object in a XAML file.", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1220854")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/element-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("element", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/1220854")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/%C3%A9l%C3%A9ment-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("élément", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);

			termEnrich = new TerminologyEnrichment("char=9,16", Arrays.asList(new String[]{"cell"}), Arrays.asList(new String[]{"pile"}),
			        "natural and applied sciences", null);
			termEnrich.setOffsetNoTagsStartIdx(9);
			termEnrich.setOffsetNoTagsEndIdx(16);
			termEnrich.setTermInfoRef("http://freme-project.eu/#char=9,16");
			termTriples = new ArrayList<Statement>();
			termTriples.add(new StatementImpl(model.createResource("http://freme-project.eu/#char=9,16"), model.createProperty("http://www.w3.org/2005/11/its/rdf#termInfoRef"), model.createResource("https://term.tilde.com/terms/390042")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/390042"), model.createProperty("http://www.w3.org/2000/01/rdf-schema#comment"), model.createLiteral("natural and applied sciences", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/cell-en#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/390042")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/cell-en#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("cell", "en")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pile-fr#Sense"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#reference"), model.createResource("https://term.tilde.com/terms/390042")));
			termTriples.add(new StatementImpl(model.createResource("https://term.tilde.com/terms/pile-fr#CanonicalForm"), model.createProperty("http://www.w3.org/ns/lemon/ontolex#writtenRep"), model.createLiteral("pile", "fr")));
			termEnrich.setTermTriples(termTriples);
			enrichments.add(termEnrich);
			break;
		default:
			break;
		}
		


		// segment s3
		


		// segment s4
//		enrichments = new ArrayList<Enrichment>();
		// same term enrichments as s3
//		enrichments.addAll(seg2Enrichments.get("s3"));
		

		//s7
		


		
		return enrichments;
	}
}
