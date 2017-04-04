package com.vistatec.ocelot.config.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Configuration object for the LingoTek services.
 */
public class LingoTekConfig {

	/** The LingoTek CMS route. */
	private String lingotekCMSRoute;

	/** The API key */
	private String lgkAPIKey;

	/**
	 * Sets the LingoTek CMS route.
	 * 
	 * @param lingotekCMSRoute
	 *            the LingoTek CMS route.
	 */
	public void setLingotekCMSRoute(String lingotekCMSRoute) {
		this.lingotekCMSRoute = lingotekCMSRoute;
	}

	/**
	 * Gets the LingoTek CMS route.
	 * 
	 * @return the LingoTek CMS route.
	 */
	public String getLingotekCMSRoute() {
		return lingotekCMSRoute;
	}

	/**
	 * Sets the API key.
	 * 
	 * @param lgkAPIKey
	 *            the API key.
	 */
	public void setLgkAPIKey(String lgkAPIKey) {
		this.lgkAPIKey = lgkAPIKey;
	}

	/**
	 * Gets the API key.
	 * 
	 * @return the API key.
	 */
	public String getLgkAPIKey() {
		return lgkAPIKey;
	}

	/**
	 * Checks if the configuration is completely defined.
	 * 
	 * @return <code>true</code> if all configuration parameters are defined;
	 *         <code>false</code> if at least one parameter is missing.
	 */
	@JsonIgnore
	public boolean isComplete() {
		return lgkAPIKey != null && !lgkAPIKey.isEmpty()
				&& lingotekCMSRoute != null && !lingotekCMSRoute.isEmpty();
	}
}
