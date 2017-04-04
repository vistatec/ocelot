package com.vistatec.ocelot.lgk;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This class provides methods for sending HTTP request.
 */
public class HttpRequestService implements IHttpRequestService {

	@Override
	public HttpResponse executeGetRequest(String url,
			Map<String, String> headerParams) throws IOException {

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		setHeader(request, headerParams);
		HttpResponse response = client.execute(request);
		return response;
	}

	private void setHeader(HttpGet request, Map<String, String> headerParams) {

		if (headerParams != null) {
			for (Entry<String, String> headerParam : headerParams.entrySet()) {
				request.addHeader(headerParam.getKey(), headerParam.getValue());
			}
		}
	}

	@Override
	public boolean isSuccessful(HttpResponse response) {
		return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
	}

}
