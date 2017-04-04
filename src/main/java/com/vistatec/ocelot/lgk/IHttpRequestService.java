package com.vistatec.ocelot.lgk;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;

public interface IHttpRequestService {

	/**
	 * This method executes an HTTP GET request.
	 * 
	 * @param url
	 *            the request URL.
	 * @param headerParams
	 *            a map defining the header parameters.
	 * @return an HTTP response.
	 * @throws IOException
	 */
	public HttpResponse executeGetRequest(String url,
			Map<String, String> headerParams) throws IOException;
	
	/**
	 * Check if the response is a successful one by checking if the status code
	 * is HTTP 200.
	 * 
	 * @param response
	 *            the response to check
	 * @return <code>true</code> if the status code is 200; <code>false</code>
	 *         otherwise.
	 */
	public boolean isSuccessful(HttpResponse response);
}
