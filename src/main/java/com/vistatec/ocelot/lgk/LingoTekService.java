package com.vistatec.ocelot.lgk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LingoTekService {

	/** The service URL to download the document. */
	private final static String DOWNLOAD_SERVICE_URL = "{{lingotekCMSRoute}}/api/document/{{document_id}}/content?locale_code={{locale_code}}";

	/**
	 * The placeholder to be replaced with the actual CMS route from the
	 * configuration.
	 */
	private final static String BASE_URL_PLACEHOLDER = "{{lingotekCMSRoute}}";

	/** The placeholder to be replaced with the document ID. */
	private final static String DOC_ID_PLACEHOLDER = "{{document_id}}";

	/** The placeholder to be replaced with the language code. */
	private final static String LOCALE_CODE_PLACEHOLDER = "{{locale_code}}";

	/** The XLIFF 1.2 MIME type. */
	private final static String XLIFF_1_2_MIME_TYPE = "application/x-xliff+xml";

	/** Base of the API key. */
	private final static String API_KEY_BASE = "Bearer ";

	/** The logger for this class. */
	private final Logger log = LoggerFactory.getLogger(LingoTekService.class);

	/** The base URL. */
	private final String baseUrl;

	/** The API key. */
	private final String lgTkApiKey;

	/** The HTTP request service. */
	private IHttpRequestService httpService;

	/**
	 * Constructor.
	 * 
	 * @param baseUrl
	 *            the actual base URL.
	 * @param lgTkApiKey
	 *            the API key.
	 * @param httpService
	 *            the http service
	 */
	public LingoTekService(final String baseUrl, final String lgTkApiKey,
			final IHttpRequestService httpService) {
		this.baseUrl = baseUrl;
		this.lgTkApiKey = lgTkApiKey;
		this.httpService = httpService;
	}

	/**
	 * Constructor.
	 * 
	 * @param baseUrl
	 *            the actual base URL.
	 * @param lgTkApiKey
	 *            the API key.
	 */
	public LingoTekService(final String baseUrl, final String lgTkApiKey) {
		this(baseUrl, lgTkApiKey, new HttpRequestService());
	}

	/**
	 * Sends a request to LingoTek to download the file identified by the given
	 * ID and language code.
	 * 
	 * @param documentID
	 *            the document ID.
	 * @param langCode
	 *            the language code.
	 * @return the file downloaded.
	 * @throws LingoTekServiceException
	 *             exception raised when an error occurs while using the
	 *             LingoTek service.
	 */
	public File downloadFile(final String documentID, final String langCode)
			throws LingoTekServiceException {
		log.info("Request to download a file from LingoTek");
		log.debug("Document id: " + documentID + " - language code: "
				+ langCode);
		File downloadedFile = null;
		try {
			String requestUrl = buildUrl(documentID, langCode);
			log.debug("Request URL: " + requestUrl);
			HttpResponse response = httpService.executeGetRequest(requestUrl,
					createHeaderParams());
			if (httpService.isSuccessful(response)) {
				log.debug("HTTP response status code: "
						+ response.getStatusLine());
				InputStream fileStream = response.getEntity().getContent();
				downloadedFile = File.createTempFile("ocelot", "lgk");
				FileUtils.copyInputStreamToFile(fileStream, downloadedFile);
			} else {
				log.error("HTTP response status code: "
						+ response.getStatusLine() + " - message: "
						+ response.getStatusLine().getReasonPhrase());
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
					throw new LingoTekServiceException(
							"Document not found. Please check the document ID and try again",
							LingoTekServiceException.SEVERITY_WARNING);
				} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
					throw new LingoTekServiceException(
							"Unauthorized to access the LingoTek server: the API key could be wrong. Please check the configuration and try again.",
							LingoTekServiceException.SEVERITY_ERROR);
				} else {
					throw new LingoTekServiceException(
							"Error response received from the LingoTek server. Please, check the configuration parametes and try again.",
							LingoTekServiceException.SEVERITY_ERROR);
				}
			}
		} catch (UnknownHostException e) {
			log.error("Wrong host.", e);
			throw new LingoTekServiceException(
					"The LinkoTek host could be wrong. Please, check the configuration and try again.",
					e, LingoTekServiceException.SEVERITY_ERROR);
		} catch (IOException e) {
			log.error("Error while downloading the document.", e);
			throw new LingoTekServiceException(
					"An error has occurred while using the download service",
					e, LingoTekServiceException.SEVERITY_ERROR);
		}
		return downloadedFile;
	}

	private String buildUrl(String documentID, String langCode) {

		return DOWNLOAD_SERVICE_URL.replace(BASE_URL_PLACEHOLDER, baseUrl)
				.replace(DOC_ID_PLACEHOLDER, documentID)
				.replace(LOCALE_CODE_PLACEHOLDER, langCode);
	}

	private Map<String, String> createHeaderParams() {

		Map<String, String> headerParams = new HashMap<String, String>();
		headerParams.put(HttpHeaders.ACCEPT, XLIFF_1_2_MIME_TYPE);
		headerParams.put(HttpHeaders.AUTHORIZATION, API_KEY_BASE + lgTkApiKey);
		return headerParams;
	}

}
