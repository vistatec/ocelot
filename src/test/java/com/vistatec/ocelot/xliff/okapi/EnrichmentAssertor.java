package com.vistatec.ocelot.xliff.okapi;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;

import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkInfoData;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;

public class EnrichmentAssertor {

	public static void assertEnrichments(List<Enrichment> expected,
	        List<Enrichment> actual) {
		
		if(expected != null && actual != null){
			Assert.assertEquals(expected.size(), actual.size());
			Collections.sort(expected, new EnrichmentComparator());
			Collections.sort(actual, new EnrichmentComparator());
			for(int i = 0; i<expected.size(); i++){
				Assert.assertEquals(expected.get(i).getType(), actual.get(i).getType());
				if(expected.get(i).getType().equals(Enrichment.ENTITY_TYPE)){
					assertEntityEnrichment((EntityEnrichment)expected.get(i), (EntityEnrichment)actual.get(i));
				} else if (expected.get(i).getType().equals(Enrichment.LINK_TYPE)){
					assertLinkEnrichment((LinkEnrichment)expected.get(i), (LinkEnrichment)actual.get(i));
				} else {
					assertTermEnrichment((TerminologyEnrichment)expected.get(i), (TerminologyEnrichment)actual.get(i));
				}
			}
		} else {
			Assert.assertNull(expected);
			Assert.assertNull(actual);
		}
		
	}

	public static void assertTermEnrichment(TerminologyEnrichment expected,
	        TerminologyEnrichment actual) {
		
		if (expected != null && actual != null) {
			assertEnrichment(expected, actual);
			Assert.assertEquals(
			        "Source terms don't match - expected: "
			                + expected.getSourceTerm() + ", actual: "
			                + actual.getSourceTerm(), expected.getSourceTerm(),
			        actual.getSourceTerm());
			Assert.assertEquals(
			        "Target terms don't match - expected: "
			                + expected.getTargetTerm() + ", actual: "
			                + actual.getTargetTerm(), expected.getTargetTerm(),
			        actual.getTargetTerm());
			assertNullOrEquals(
			        "Senses don't match - expected: " + expected.getSense()
			                + ", actual: " + actual.getSense(),
			        expected.getSense(), actual.getSense());
			assertNullOrEquals(
			        "Definitions don't match - expected: "
			                + expected.getDefinition() + ", actual: "
			                + actual.getDefinition(), expected.getDefinition(),
			        actual.getDefinition());
		} else {
			Assert.assertNull(expected);
			Assert.assertNull(actual);
		}
	}

	public static void assertEntityEnrichment(EntityEnrichment expected,
	        EntityEnrichment actual) {
		if (expected != null && actual != null) {
			assertEnrichment(expected, actual);
			Assert.assertEquals(
			        "Entity URLs don't match - expected: "
			                + expected.getEntityURL() + ", actual: "
			                + actual.getEntityURL(), expected.getEntityURL(),
			        actual.getEntityURL());
		} else {
			Assert.assertNull(expected);
			Assert.assertNull(actual);
		}
	}

	public static void assertLinkEnrichment(LinkEnrichment expected,
	        LinkEnrichment actual) {
		if (expected != null && actual != null) {
			assertEnrichment(expected, actual);
			Assert.assertEquals(
			        "Referenced entities don't match - expected: "
			                + expected.getReferenceEntity() + ", actual: "
			                + actual.getReferenceEntity(),
			        expected.getReferenceEntity(), actual.getReferenceEntity());
			Assert.assertEquals(expected.getEntityName().getValue(), actual
			        .getEntityName().getValue());
			assertLinkInfoData("Home pages don't match.",
			        expected.getHomePage(), actual.getHomePage());
			assertLinkInfoData("Image URLs don't match.",
			        expected.getImageURL(), actual.getImageURL());
			assertLinkInfoData("Long descriptions don't match.",
			        expected.getLongDescription(), actual.getLongDescription());
			assertLinkInfoData("Short descriptions don't match.",
			        expected.getShortDescription(),
			        actual.getShortDescription());
			assertLinkInfoData("Wiki pages don't match.",
			        expected.getWikiPage(), actual.getWikiPage());
			assertLinkInfoData(expected.getInfoList(), actual.getInfoList());
		} else {
			Assert.assertNull(expected);
			Assert.assertNull(actual);
		}
	}

	public static void assertEnrichment(Enrichment expected, Enrichment actual) {

		Assert.assertEquals(
		        "Start index doesn't match - expected: "
		                + expected.getOffsetStartIdx() + ", actual: "
		                + actual.getOffsetStartIdx(),
		        expected.getOffsetStartIdx(), actual.getOffsetStartIdx());
		Assert.assertEquals(
		        "End index doesn't match - expected: "
		                + expected.getOffsetEndIdx() + ", actual: "
		                + actual.getOffsetEndIdx(), expected.getOffsetEndIdx(),
		        actual.getOffsetEndIdx());
		Assert.assertEquals(
		        "No tags start index doesn't match - expected: "
		                + expected.getOffsetNoTagsStartIdx() + ", actual: "
		                + actual.getOffsetNoTagsStartIdx(),
		        expected.getOffsetNoTagsStartIdx(),
		        actual.getOffsetNoTagsStartIdx());
		Assert.assertEquals(
		        "No tags end index doesn't match - expected: "
		                + expected.getOffsetNoTagsEndIdx() + ", actual: "
		                + actual.getOffsetNoTagsEndIdx(),
		        expected.getOffsetNoTagsEndIdx(),
		        actual.getOffsetNoTagsEndIdx());

	}

	private static void assertLinkInfoData(List<LinkInfoData> expected,
	        List<LinkInfoData> actual) {

		if (expected != null && actual != null) {
			Assert.assertEquals(expected.size(), actual.size());
			Collections.sort(expected, new LinkInfoDataComparator());
			Collections.sort(actual, new LinkInfoDataComparator());
			for (int i = 0; i < expected.size(); i++) {
				assertLinkInfoData(null, expected.get(i), actual.get(i));
			}
		} else {
			Assert.assertTrue(expected == null || expected.isEmpty());
			Assert.assertTrue(actual == null || actual.isEmpty());
		}
	}

	private static void assertLinkInfoData(String message,
	        LinkInfoData expected, LinkInfoData actual) {

		if (expected != null && actual != null) {
			Assert.assertEquals(message, expected.getValue(), actual.getValue());
		} else {
			Assert.assertNull(message, expected);
			Assert.assertNull(message, actual);
		}
	}

	private static void assertNullOrEquals(String message, Object expected,
	        Object actual) {

		if (expected != null && actual != null) {
			Assert.assertEquals(message, expected, actual);
		} else {
			Assert.assertNull(message, expected);
			Assert.assertNull(message, actual);
		}
	}

}

class EnrichmentComparator implements Comparator<Enrichment> {

	@Override
	public int compare(Enrichment o1, Enrichment o2) {

		int retValue = 0;
		if (o1.getType().equals(Enrichment.ENTITY_TYPE)) {
			if (o2.getType().equals(Enrichment.ENTITY_TYPE)) {
				retValue = compareEntityEnrichments((EntityEnrichment) o1,
				        (EntityEnrichment) o2);
			} else {
				retValue = -1;
			}
		} else if (o1.getType().equals(Enrichment.LINK_TYPE)) {
			if (o2.getType().equals(Enrichment.ENTITY_TYPE)) {
				retValue = 1;
			} else if (o2.getType().equals(Enrichment.TERMINOLOGY_TYPE)) {
				retValue = -1;
			} else {
				retValue = compareLinkEnrichments((LinkEnrichment) o1,
				        (LinkEnrichment) o2);
			}
		} else if (o1.getType().equals(Enrichment.TERMINOLOGY_TYPE)) {
			if(!o2.getType().equals(Enrichment.TERMINOLOGY_TYPE)){
				retValue = 1;
			} else {
				retValue = compareTermEnrichment((TerminologyEnrichment) o1,
						(TerminologyEnrichment) o2);
			}
		}

		return retValue;
	}

	private int compareEntityEnrichments(EntityEnrichment e1,
	        EntityEnrichment e2) {
		return e1.getEntityURL().compareTo(e2.getEntityURL());
	}

	private int compareLinkEnrichments(LinkEnrichment e1, LinkEnrichment e2) {
		return e1.getReferenceEntity().compareTo(e2.getReferenceEntity());
	}

	private int compareTermEnrichment(TerminologyEnrichment e1,
	        TerminologyEnrichment e2) {
		int retValue = e1.getSourceTerm().compareTo(e2.getSourceTerm());
		if (retValue == 0) {
			retValue = e1.getTargetTerm().compareTo(e2.getTargetTerm());
			if (retValue == 0) {
				if (e1.getSense() != null && e2.getSense() != null) {
					retValue = e1.getSense().compareTo(e2.getSense());
				} else if (e1.getSense() != null) {
					retValue = -1;
				} else if (e2.getSense() != null) {
					retValue = 1;
				}
				if (retValue == 0) {
					if (e1.getDefinition() != null
					        && e2.getDefinition() != null) {
						retValue = e1.getDefinition().compareTo(
						        e2.getDefinition());
					} else if (e1.getDefinition() != null) {
						retValue = -1;
					} else if (e2.getDefinition() != null) {
						retValue = 1;
					}
				}
			}
		}

		return retValue;
	}

}

class LinkInfoDataComparator implements Comparator<LinkInfoData>{

	@Override
    public int compare(LinkInfoData o1, LinkInfoData o2) {
	    
		return o1.getPropName().compareTo(o2.getPropName());
    }
	
}
