package com.vistatec.ocelot.plugins;

import java.awt.Component;
import java.awt.Window;
import java.util.List;

import com.vistatec.ocelot.plugins.exception.FremeEnrichmentException;
import com.vistatec.ocelot.plugins.exception.UnknownServiceException;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;

/**
 * The FREME plugin interface.
 */
public interface FremePlugin extends Plugin {

	/** The e-Entity service constant. */
	public int EENTITY_SERVICE = 0;

	/** The e-Link service constant. */
	public int ELINK_SERVICE = 1;

	/** The e-Terminology service constant. */
	public int ETERMINOLOGY = 2;

	/** The e-Translation service constant. */
	public int ETRANSLATION = 3;

	/**
	 * Configures the service chain.
	 * 
	 * @param ocelotMainFrame
	 *            the Ocelot main frame.
	 */
	void configureServiceChain(Window ocelotMainFrame);

	/**
	 * Turns on the service of the specified type.
	 * 
	 * @param serviceType
	 *            the service type.
	 * @throws UnknownServiceException
	 *             exception raised when an unknown service type is requested.
	 */
	void turnOnService(final int serviceType) throws UnknownServiceException;

	/**
	 * Turns off the service of a specified type.
	 * 
	 * @param serviceType
	 *            the service type.
	 * @throws UnknownServiceException
	 *             exception raised when an unknown service type is requested.
	 */
	void turnOffService(final int serviceType) throws UnknownServiceException;

	/**
	 * Enriches the text from the source.
	 * 
	 * @param plainText
	 *            the source text.
	 * @return the list of retrieved enrichments.
	 * @throws FremeEnrichmentException
	 *             the exception raised when an error occurs while enriching a
	 *             text.
	 */
	List<Enrichment> enrichSourceContent(final String plainText)
			throws FremeEnrichmentException;

	/**
	 * Enriches the text from the target.
	 * 
	 * @param plainText
	 *            the target text.
	 * @return the list of retrieved enrichments.
	 * @throws FremeEnrichmentException
	 *             the exception raised when an error occurs while enriching a
	 *             text.
	 */
	List<Enrichment> enrichTargetContent(final String plainText)
			throws FremeEnrichmentException;

	/**
	 * Sets the source and the target languages.
	 * 
	 * @param sourceLanguage
	 *            the source language.
	 * @param targetLanguage
	 *            the target language.
	 */
	public void setSourceAndTargetLanguages(String sourceLanguage,
			String targetLanguage);

	/**
	 * Gets the Entity Categories filter panel.
	 * 
	 * @return the Entity categories filter panel.
	 */
	public Component getCategoryFilterPanel();

}
