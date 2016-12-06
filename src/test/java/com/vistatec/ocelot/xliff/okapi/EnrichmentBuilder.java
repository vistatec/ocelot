package com.vistatec.ocelot.xliff.okapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=5,14", Arrays.asList(new String[]{"vacation"}), Arrays.asList(new String[]{"suppression"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(5);
		termEnrich.setOffsetNoTagsEndIdx(14);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=5,14", Arrays.asList(new String[]{"holiday", "leave", "vacation"}), Arrays.asList(new String[]{"congé"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(5);
		termEnrich.setOffsetNoTagsEndIdx(14);
		enrichments.add(termEnrich);
		
		seg2Enrichments.put(1, enrichments);
		
		//segment 2
		enrichments = new ArrayList<Enrichment>();
		termEnrich = new TerminologyEnrichment("char=0,7", Arrays.asList(new String[]{"welcome"}), Arrays.asList(new String[]{"bienvenue"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(0);
		termEnrich.setOffsetNoTagsEndIdx(7);
		enrichments.add(termEnrich);
		
		EntityEnrichment entityEnrich = new EntityEnrichment("char=11,15", "http://dbpedia.org/resource/Bray");
		entityEnrich.setOffsetNoTagsStartIdx(11);
		entityEnrich.setOffsetNoTagsEndIdx(15);
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
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=3,8", Arrays.asList(new String[]{"survey mile"}), Arrays.asList(new String[]{"mille"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(3);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=13,23", Arrays.asList(new String[]{"kilometer"}), Arrays.asList(new String[]{"kilomètre"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(13);
		termEnrich.setOffsetNoTagsEndIdx(23);
		enrichments.add(termEnrich);
		
		entityEnrich = new EntityEnrichment("char=45,52", "http://dbpedia.org/resource/Wicklow");
		entityEnrich.setOffsetNoTagsStartIdx(45);
		entityEnrich.setOffsetNoTagsEndIdx(52);
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
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=65,72", Arrays.asList(new String[]{"popular"}), Arrays.asList(new String[]{"populaire"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(65);
		termEnrich.setOffsetNoTagsEndIdx(72);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77",  Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"message publicitaire"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"tache"}), "wood industry", null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot croaker", "spot"}), Arrays.asList(new String[]{"tambour croca"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		enrichments.add(termEnrich);
		
		List<String> targetList = Arrays.asList(new String[]{"remarquer", "point(tache)"});
		termEnrich = new TerminologyEnrichment("char=73,77",  Arrays.asList(new String[]{"spot"}), targetList, null, null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"point de soudure"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"tache"}), "CH6 - analytical chemistry (sn: principles and methods; rf: industrial analysis, see: TEO)  (Lenoch classification);  TR45 - railway maintenance  (Lenoch classification);  JUD1 - author's rights  (Lenoch classification);  TR56 - pedestrians and cyclists  (Lenoch classification);  TRE - transport security (nt: protection against attacks; protection of people and goods)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"spot"}), Arrays.asList(new String[]{"point"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(73);
		termEnrich.setOffsetNoTagsEndIdx(77);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=82,90", Arrays.asList(new String[]{"Visitors"}), Arrays.asList(new String[]{"Visiteurs"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(82);
		termEnrich.setOffsetNoTagsEndIdx(90);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=82,90", Arrays.asList(new String[]{"visitor"}), Arrays.asList(new String[]{"visiteur"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(82);
		termEnrich.setOffsetNoTagsEndIdx(90);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=91,101", Arrays.asList(new String[]{"wishing to"}), Arrays.asList(new String[]{"désireux"}), "international trade", null);
		termEnrich.setOffsetNoTagsStartIdx(91);
		termEnrich.setOffsetNoTagsEndIdx(101);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=102,110", Arrays.asList(new String[]{"discover"}), Arrays.asList(new String[]{"interroger au préalable"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(102);
		termEnrich.setOffsetNoTagsEndIdx(110);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=102,110", Arrays.asList(new String[]{"discover"}), Arrays.asList(new String[]{"inventer"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(102);
		termEnrich.setOffsetNoTagsEndIdx(110);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "SCIENCE", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "FINANCE", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"regions"}), Arrays.asList(new String[]{"régions"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "environmental policy", "\"A designated area or an administrative division of a city, county or larger geographical territory that is formulated according to some biological, political, economic or demographic criteria. (Source: RHW / ISEP)\"");
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"space"}), Arrays.asList(new String[]{"zone"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"range"}), Arrays.asList(new String[]{"zone"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"oblast"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=123,129", Arrays.asList(new String[]{"region"}), Arrays.asList(new String[]{"région"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(123);
		termEnrich.setOffsetNoTagsEndIdx(129);
		enrichments.add(termEnrich);
		
		seg2Enrichments.put(3, enrichments);
		
		//segment 4
		enrichments = new ArrayList<Enrichment>();
		entityEnrich = new EntityEnrichment("char=12,26", "http://dbpedia.org/resource/Dublin_Airport");
		entityEnrich.setOffsetNoTagsStartIdx(12);
		entityEnrich.setOffsetNoTagsEndIdx(26);
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
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=16,21", Arrays.asList(new String[]{"location"}), Arrays.asList(new String[]{"emplacement"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(16);
		termEnrich.setOffsetNoTagsEndIdx(21);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=16,21", Arrays.asList(new String[]{"place"}), Arrays.asList(new String[]{"emplacement"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(16);
		termEnrich.setOffsetNoTagsEndIdx(21);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=16,21",  Arrays.asList(new String[]{"place"}), Arrays.asList(new String[]{"place"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(16);
		termEnrich.setOffsetNoTagsEndIdx(21);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=16,21", Arrays.asList(new String[]{"place"}), Arrays.asList(new String[]{"place"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(16);
		termEnrich.setOffsetNoTagsEndIdx(21);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), "culture and religion", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"basis ossis metatarsalis"}), Arrays.asList(new String[]{"extrémité tarsienne"}), "Medizin|Fußkrankheiten", "The base or posterior extremity is wedge-shaped, articulating proximally with the tarsal bones, and by its sides with the contiguous metatarsal bones: its dorsal and plantar surfaces are rough for the attachment of ligaments.");
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		targetList = Arrays.asList(new String[]{"socle", "embase", "charge", "base"});
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"feedstock", "base"}), targetList, null, null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"culot d'ergol"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"header"}), Arrays.asList(new String[]{"embase"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"foundation"}), Arrays.asList(new String[]{"assise"}), "wood industry", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), "environmental policy", "\"Any chemical species, ionic or molecular, capable of accepting or receiving a proton (hydrogen ion) from another substance; the other substance acts as an acid in giving of the proton; the other ion is a base. (Source: MGH)\"" );
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), Arrays.asList(new String[]{"base"}), "FINANCE", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		targetList = Arrays.asList(new String[]{"base", "base chimique"});
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"base"}), targetList, null, null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=25,29", Arrays.asList(new String[]{"basis"}), Arrays.asList(new String[]{"banque"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(25);
		termEnrich.setOffsetNoTagsEndIdx(29);
		enrichments.add(termEnrich);
		
		entityEnrich = new EntityEnrichment("char=56,63", "http://dbpedia.org/resource/Wicklow");
		entityEnrich.setOffsetNoTagsStartIdx(56);
		entityEnrich.setOffsetNoTagsEndIdx(63);
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
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=79,83", Arrays.asList(new String[]{"city"}), Arrays.asList(new String[]{"ville"}), "environmental policy", "\"Term used generically today to denote any urban form but applied particularly to large urban settlements. There are, however, no agreed definitions to separate a city from the large metropolis or the smaller town. (Source: GOOD)\"");
		termEnrich.setOffsetNoTagsStartIdx(79);
		termEnrich.setOffsetNoTagsEndIdx(83);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=79,83", Arrays.asList(new String[]{"city", "municipality"}), Arrays.asList(new String[]{"ville", "commune"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(79);
		termEnrich.setOffsetNoTagsEndIdx(83);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=79,83", Arrays.asList(new String[]{"city"}), Arrays.asList(new String[]{"ville"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(79);
		termEnrich.setOffsetNoTagsEndIdx(83);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=89,96", Arrays.asList(new String[]{"popular"}), Arrays.asList(new String[]{"populaire"}), "culture and religion", null);
		termEnrich.setOffsetNoTagsStartIdx(89);
		termEnrich.setOffsetNoTagsEndIdx(96);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=89,96", Arrays.asList(new String[]{"popular"}), Arrays.asList(new String[]{"populaire"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(89);
		termEnrich.setOffsetNoTagsEndIdx(96);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=97,108", Arrays.asList(new String[]{"attraction"}), Arrays.asList(new String[]{"attraction"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(97);
		termEnrich.setOffsetNoTagsEndIdx(108);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=120,124", Arrays.asList(new String[]{"easy"}), Arrays.asList(new String[]{"facile"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(120);
		termEnrich.setOffsetNoTagsEndIdx(124);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=125,132", Arrays.asList(new String[]{"driving"}), Arrays.asList(new String[]{"modulation"}), "EL6 - components - devices  (Lenoch classification);  CH - Chemistry (sn: chemistry as pure science; us: for applied chemistry, see: IC)  (Lenoch classification);  CH8 - organic chemistry  (Lenoch classification);  IC8 - glass - enamel  (Lenoch classification);  TRE - transport security (nt: protection against attacks; protection of people and goods)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
		termEnrich.setOffsetNoTagsStartIdx(125);
		termEnrich.setOffsetNoTagsEndIdx(132);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=125,132", Arrays.asList(new String[]{"driving"}), Arrays.asList(new String[]{"en marche"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(125);
		termEnrich.setOffsetNoTagsEndIdx(132);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=133,141", Arrays.asList(new String[]{"distance"}), Arrays.asList(new String[]{"distance"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(133);
		termEnrich.setOffsetNoTagsEndIdx(141);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=133,141", Arrays.asList(new String[]{"spacing"}), Arrays.asList(new String[]{"écartement"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(133);
		termEnrich.setOffsetNoTagsEndIdx(141);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=133,141", Arrays.asList(new String[]{"length"}), Arrays.asList(new String[]{"distance"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(133);
		termEnrich.setOffsetNoTagsEndIdx(141);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=133,141", Arrays.asList(new String[]{"distance"}), Arrays.asList(new String[]{"espacement"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(133);
		termEnrich.setOffsetNoTagsEndIdx(141);
		enrichments.add(termEnrich);
		
		seg2Enrichments.put(5, enrichments);
		
		//segment 6
		enrichments = new ArrayList<Enrichment>();
		termEnrich = new TerminologyEnrichment("char=2,8", Arrays.asList(new String[]{"single"}), Arrays.asList(new String[]{"célibataire"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(2);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=2,8", Arrays.asList(new String[]{"single"}), Arrays.asList(new String[]{"compartiment individuel"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(2);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=2,8", Arrays.asList(new String[]{"single"}), Arrays.asList(new String[]{"unique", "simple"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(2);
		termEnrich.setOffsetNoTagsEndIdx(8);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne", "rail"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "information technology and data processing", "A vector path defined by two points and a straight or curved segment between them.");
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"courbe"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"lignée"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"gamme"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "environmental policy", "\"Term used in GIS technologies in the vector type of internal data organization: spatial data are divided into point, line and polygon types. In most cases, point entities (nodes) are specified directly as coordinate pairs, with lines (arcs or edges) represented as chains of points. Regions are similarly defined in terms of the lines which form their boundaries. Some vector GIS store information in the form of points, line segments and point pairs; others maintain closed lists of points defining polygon regions. Vector structures are especially suited to storing definitions of spatial objects for which sharp boundaries exist or can be imposed. (Source: YOUNG)\"");
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"droite"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"compagnie maritime", "limite", "ligne", "compagnie de navigation"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "Radioredaktion", "Fil conducteur métallique, ou fiasceau de fils protégé par des enveloppes isolantes.");
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne", "canalisation", "tuyau de carburant", "trait"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"ligne"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"lignée"}), "AG5 - crops (sn: cultivation and products)  (Lenoch classification);  BZ6 - plant diseases  (Lenoch classification);  Allikas:TE - Technology - Engineering  (Lenoch classification);  TR56 - pedestrians and cyclists  (Lenoch classification);  TRE - transport security (nt: protection against attacks; protection of people and goods)  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"line"}), Arrays.asList(new String[]{"palangre"}), "AGB - fishery - aquaculture  (Lenoch classification);  TRA - vehicle disposal - ship disposal - aircraft disposal (rf: vehicle industry, see: INT)  (Lenoch classification);  FI - Financial Affairs - Taxation and Customs (sn: microeconomics 2; nt: further breakdown of ECF)  (Lenoch classification);  JUD1 - author's rights  (Lenoch classification);  JU15 - legal CEs  (Lenoch classification);  JU71 - social security law (nt: social security law in restricted sense)  (Lenoch classification);", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=9,13", Arrays.asList(new String[]{"link"}), Arrays.asList(new String[]{"ligne"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(9);
		termEnrich.setOffsetNoTagsEndIdx(13);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"libellé"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(17);
		termEnrich.setOffsetNoTagsEndIdx(21);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=17,21",  Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(17);
		termEnrich.setOffsetNoTagsEndIdx(21);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}), "LAW", null);
		termEnrich.setOffsetNoTagsStartIdx(17);
		termEnrich.setOffsetNoTagsEndIdx(21);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"Text"}), Arrays.asList(new String[]{"Texte"}), "information technology and data processing", "A field data type that can contain up to 255 characters or the number of characters specified by the Fieldsize property, whichever is less.");
		termEnrich.setOffsetNoTagsStartIdx(17);
		termEnrich.setOffsetNoTagsEndIdx(21);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=17,21", Arrays.asList(new String[]{"text"}), Arrays.asList(new String[]{"texte"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(17);
		termEnrich.setOffsetNoTagsEndIdx(21);
		enrichments.add(termEnrich);

		termEnrich = new TerminologyEnrichment("char=22,32", Arrays.asList(new String[]{"contains"}), Arrays.asList(new String[]{"contient"}), "information technology and data processing", null);
		termEnrich.setOffsetNoTagsStartIdx(22);
		termEnrich.setOffsetNoTagsEndIdx(32);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=22,32", Arrays.asList(new String[]{"contain"}), Arrays.asList(new String[]{"refermer"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(22);
		termEnrich.setOffsetNoTagsEndIdx(32);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pairing"}), Arrays.asList(new String[]{"jumelage"}), "information technology and data processing", "The process of establishing a Bluetooth link or connection between two Bluetooth–enabled devices.");
		termEnrich.setOffsetNoTagsStartIdx(43);
		termEnrich.setOffsetNoTagsEndIdx(49);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pair"}), Arrays.asList(new String[]{"paire"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(43);
		termEnrich.setOffsetNoTagsEndIdx(49);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"Pairing"}), Arrays.asList(new String[]{"Appariement"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(43);
		termEnrich.setOffsetNoTagsEndIdx(49);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pair"}), Arrays.asList(new String[]{"jumeler"}), "information technology and data processing", "To establish a Bluetooth link or connection between two Bluetooth–enabled devices.");
		termEnrich.setOffsetNoTagsStartIdx(43);
		termEnrich.setOffsetNoTagsEndIdx(49);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=59,65", Arrays.asList(new String[]{"pair"}), Arrays.asList(new String[]{"couple"}), "information and information processing", null);
		termEnrich.setOffsetNoTagsStartIdx(43);
		termEnrich.setOffsetNoTagsEndIdx(49);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"balise"}), "information technology and data processing", "A marker used to identify a physical object. An RFID tag is an electronic marker that stores identification data.");
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"bout", "étiquette"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"mot-clé"}), "information technology and data processing", "One or more characters containing information about a file, record type, or other structure.");
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"étiquette"}), "international trade", null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"graff", "inscription murale faite à l'aérosol", "tag"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag", "HTML tag", "document control marker"}), Arrays.asList(new String[]{"marqueur", "balise", "ferret", "étiquette"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"étiqueter"}), "information technology and data processing", "To apply an identification marker to an item, case, or pallet.");
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"ear tag", "tag"}), Arrays.asList(new String[]{"pendentif"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77",  Arrays.asList(new String[]{"market"}), Arrays.asList(new String[]{"repère"}), "natural and applied sciences", null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"bracelet électronique"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"mot-clef"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"étiquette"}), "communications", null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"repère"}), "information technology and data processing", "A geometrical arrangement of shapes that define a value that the Surface Vision System can recognize. These geometrical arrangements are added to physical objects (then called tagged objects) to work with Surface applications (for example, a glass tile that acts as a puzzle piece in a puzzle application)." );
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		termEnrich = new TerminologyEnrichment("char=73,77", Arrays.asList(new String[]{"tag"}), Arrays.asList(new String[]{"attache"}), null, null);
		termEnrich.setOffsetNoTagsStartIdx(50);
		termEnrich.setOffsetNoTagsEndIdx(54);
		enrichments.add(termEnrich);
		
		seg2Enrichments.put(6, enrichments);
		
		return seg2Enrichments;
	}

}
