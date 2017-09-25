package com.vistatec.ocelot.xliff.freme;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.vistatec.ocelot.segment.model.enrichment.ELinkEnrichmentsConstants;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;

public class FremeAnnotationsReader {
	
	protected String sourceLang;
	
	protected String targetLang;
	
	public FremeAnnotationsReader(String sourceLang, String targetLang) {
		
		this.sourceLang = sourceLang;
		this.targetLang = targetLang;
	}

	
	protected LinkEnrichment retrieveLinkEnrichment(String entityUri, Model tripleModel, String language, int startIndex,
			int endIndex) {

		LinkEnrichment linkEnrichment = new LinkEnrichment(language);
		linkEnrichment.setOffsetNoTagsStartIdx(startIndex);
		linkEnrichment.setOffsetNoTagsEndIdx(endIndex);
		ELinkEnrichmentsConstants.fillLinkEnrichment(linkEnrichment, tripleModel, entityUri);
		return linkEnrichment;
	}
	
	protected List<Enrichment> retrieveTermEnrichments(String termUri, int startIndex, int endIndex, Model tripleModel) {

		List<Enrichment> termEnrichments = new ArrayList<>();
		Resource termResource = tripleModel.createResource(termUri);
		StmtIterator mainStmtIt = tripleModel.listStatements(termResource, null, (RDFNode) null);
		List<Statement> tripleStmts = null;
		TerminologyEnrichment termEnrich = null;
		while (mainStmtIt.hasNext()) {
			Statement mainStmt = mainStmtIt.next();
			tripleStmts = new ArrayList<Statement>();
			tripleStmts.add(mainStmt);
			String sense = findSense(tripleModel, mainStmt, tripleStmts);
			String definition = findDefinition(tripleModel, mainStmt, tripleStmts);
			List<String> sourceList = new ArrayList<String>();
			List<String> targetList = new ArrayList<String>();
			findSourceAndTarget(tripleModel, mainStmt, tripleStmts, sourceList, targetList);
			if (!sourceList.isEmpty()) {
				termEnrich = new TerminologyEnrichment();
				termEnrich.setTermInfoRef(termUri);
				termEnrich.setOffsetNoTagsStartIdx(startIndex);
				termEnrich.setOffsetNoTagsEndIdx(endIndex);
				termEnrich.setSourceTermList(sourceList);
				termEnrich.setTargetTermList(targetList);
				termEnrich.setSense(sense);
				termEnrich.setDefinition(definition);
				termEnrich.setTermTriples(tripleStmts);
				termEnrichments.add(termEnrich);
			}
		}
		return termEnrichments;
	}
	
	/**
	 * Finds the sense for the current terminology triple.
	 * 
	 * @param tripleModel
	 *            the triples model
	 * @param mainTermStmt
	 *            the terminology triple
	 * @param tripleStmts
	 *            the list of triples statements related to this terminology
	 *            enrichment
	 * @return the sense if it exists; <code>null</code> otherwise
	 */
	private String findSense(Model tripleModel, Statement mainTermStmt, List<Statement> tripleStmts) {

		String sense = null;
		StmtIterator senseStmtIt = tripleModel.listStatements(mainTermStmt.getObject().asResource(),
				tripleModel.createProperty("http://www.w3.org/2000/01/rdf-schema#", "comment"), (RDFNode) null);
		Statement senseStmt = null;
		if (senseStmtIt != null && senseStmtIt.hasNext()) {
			senseStmt = senseStmtIt.next();
			sense = senseStmt.getObject().asLiteral().getString();
			tripleStmts.add(senseStmt);
		}
		return sense;

	}
	
	private String findDefinition(Model tripleModel, Statement mainTermStmt, List<Statement> tripleStmts) {

		String definition = null;
		StmtIterator definitionStmtIt = tripleModel.listStatements(mainTermStmt.getObject().asResource(),
				tripleModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#", "definition"), (RDFNode) null);
		Statement definitionStmt = null;
		if (definitionStmtIt != null && definitionStmtIt.hasNext()) {
			definitionStmt = definitionStmtIt.next();
			definition = definitionStmt.getObject().asLiteral().getString();
			tripleStmts.add(definitionStmt);
		}
		return definition;
	}
	
	/**
	 * Finds source and target for the current terminology triple.
	 * 
	 * @param tripleModel
	 *            the triples model.
	 * @param mainTermStmt
	 *            the terminology main statement.
	 * @param tripleStmts
	 *            the list of triples realted to this terminology enrichment.
	 * @return an array of strings containing the source at the first index and
	 *         the target at the second index.
	 */
	protected void findSourceAndTarget(Model tripleModel, Statement mainTermStmt, List<Statement> tripleStmts,
			List<String> sourceList, List<String> targetList) {

		String sourceLanguage = sourceLang;
		if (sourceLang.contains("-")) {
			sourceLanguage = sourceLang.substring(0, sourceLang.indexOf("-"));
		}
		String targetLanguage = targetLang;
		if (targetLang.contains("-")) {
			targetLanguage = targetLang.substring(0, targetLang.indexOf("-"));
		}
		StmtIterator referenceStmtIt = tripleModel.listStatements(null,
				tripleModel.createProperty("http://www.w3.org/ns/lemon/ontolex#", "reference"),
				mainTermStmt.getObject());
		if (referenceStmtIt != null) {
			Statement referenceStmt = null;
			while (referenceStmtIt.hasNext()) {
				referenceStmt = referenceStmtIt.next();
				tripleStmts.add(referenceStmt);
				String sourceURI = referenceStmt.getSubject().getURI().replace("#Sense", "#CanonicalForm");
				StmtIterator termIt = tripleModel.listStatements(tripleModel.createResource(sourceURI),
						tripleModel.createProperty("http://www.w3.org/ns/lemon/ontolex#", "writtenRep"),
						(RDFNode) null);
				if (termIt != null && termIt.hasNext()) {
					Statement sourcTgtStmt = termIt.next();
					tripleStmts.add(sourcTgtStmt);
					if (sourceLanguage.equals(sourcTgtStmt.getObject().asLiteral().getLanguage())) {
						sourceList.add(sourcTgtStmt.getObject().asLiteral().getString());
					} else if (targetLanguage.equals(sourcTgtStmt.getObject().asLiteral().getLanguage())) {
						targetList.add(sourcTgtStmt.getObject().asLiteral().getString());
					}
				}
			}
		}
	}

}
