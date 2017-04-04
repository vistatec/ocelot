package com.vistatec.ocelot.services;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.io.input.CharSequenceInputStream;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.io.Files;
import com.vistatec.ocelot.lgk.IHttpRequestService;
import com.vistatec.ocelot.lgk.LingoTekService;
import com.vistatec.ocelot.lgk.LingoTekServiceException;

public class TestLingoTekService {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final String baseUrl = "https://testhost.com";
	private final String lgTkApiKey = "testapi=testapi---";
	private final String documentID = "documentId";
	private final String langCode = "it=IT";

	@Test
	public void getRequestTest() throws LingoTekServiceException, IOException {

		StringWriter writer = new StringWriter();
		LingoTekService service = new LingoTekService(baseUrl, lgTkApiKey,
				new TestOKHttpRequestService(writer));
		File file = service.downloadFile(documentID, langCode);
		String expectedUrl = baseUrl + "/api/document/" + documentID
				+ "/content?locale_code=" + langCode;
		String headerMapString = HttpHeaders.ACCEPT
				+ "=application/x-xliff+xml"
				+ TestOKHttpRequestService.DATA_SEPARATOR
				+ HttpHeaders.AUTHORIZATION + "=" + "Bearer " + lgTkApiKey;
		Assert.assertEquals(expectedUrl
				+ TestOKHttpRequestService.DATA_SEPARATOR + headerMapString,
				writer.toString());
		String expectedFileString = "File from LingoTek";
		String actualFileString = Files.readFirstLine(file,
				Charset.defaultCharset());
		Assert.assertEquals(expectedFileString, actualFileString);

	}

	@Test
	public void getRequestWrongHostTest() throws LingoTekServiceException {

		LingoTekService service = new LingoTekService(baseUrl, lgTkApiKey);
		thrown.expect(LingoTekServiceException.class);
		thrown.expectMessage("The LinkoTek host could be wrong");
		service.downloadFile(documentID, langCode);
	}
	
	@Test
	public void getRequestNotFoundTest() throws LingoTekServiceException{
		
		LingoTekService service = new LingoTekService(baseUrl, lgTkApiKey, new TestNotFoundHttpService());
		thrown.expect(LingoTekServiceException.class);
		thrown.expectMessage("Document not found");
		service.downloadFile(documentID, langCode);
	}
	
	@Test
	public void getRequestUnauthorizedTest() throws LingoTekServiceException{
		
		LingoTekService service = new LingoTekService(baseUrl, lgTkApiKey, new TestUnauthorizedHttpService());
		thrown.expect(LingoTekServiceException.class);
		thrown.expectMessage("Unauthorized to access the LingoTek server");
		service.downloadFile(documentID, langCode);
	}
	
	@Test
	public void getRequestGenericErrorTest() throws LingoTekServiceException{
		
		LingoTekService service = new LingoTekService(baseUrl, lgTkApiKey, new TestGenericErrordHttpService());
		thrown.expect(LingoTekServiceException.class);
		thrown.expectMessage("Error response received from the LingoTek server");
		service.downloadFile(documentID, langCode);
	}

}

class TestOKHttpRequestService implements IHttpRequestService {

	private StringWriter writer;

	public static final String DATA_SEPARATOR = "|";

	public TestOKHttpRequestService(StringWriter writer) {
		this.writer = writer;
	}

	@Override
	public HttpResponse executeGetRequest(String url,
			Map<String, String> headerParams) throws IOException {

		writer.append(url);
		if (headerParams != null) {
			TreeMap<String, String> orderedParams = new TreeMap<String, String>();
			orderedParams.putAll(headerParams);
			for (Entry<String, String> param : orderedParams.entrySet()) {
				writer.append(DATA_SEPARATOR);
				writer.append(param.getKey());
				writer.append("=");
				writer.append(param.getValue());
			}
		}
		HttpResponse response = new BasicHttpResponse(new BasicStatusLine(
				new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "Ok"));
		BasicHttpEntity entity = new BasicHttpEntity();
		entity.setContent(new CharSequenceInputStream("File from LingoTek",
				Charset.defaultCharset()));
		response.setEntity(entity);
		return response;
	}

	@Override
	public boolean isSuccessful(HttpResponse response) {
		return true;
	}

}

class TestNotFoundHttpService implements IHttpRequestService {

	@Override
	public HttpResponse executeGetRequest(String url,
			Map<String, String> headerParams) throws IOException {

		return new BasicHttpResponse(new BasicStatusLine(
				new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NOT_FOUND,
				"NOT FOUND"));
	}

	@Override
	public boolean isSuccessful(HttpResponse response) {
		return false;
	}

}

class TestUnauthorizedHttpService implements IHttpRequestService {

	@Override
	public HttpResponse executeGetRequest(String url,
			Map<String, String> headerParams) throws IOException {

		return new BasicHttpResponse(new BasicStatusLine(
				new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_UNAUTHORIZED,
				"NOT AUTHORIZED"));
	}

	@Override
	public boolean isSuccessful(HttpResponse response) {
		return false;
	}

}

class TestGenericErrordHttpService implements IHttpRequestService {

	@Override
	public HttpResponse executeGetRequest(String url,
			Map<String, String> headerParams) throws IOException {

		return new BasicHttpResponse(new BasicStatusLine(
				new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_BAD_REQUEST,
				"BAD REQUEST"));
	}

	@Override
	public boolean isSuccessful(HttpResponse response) {
		return false;
	}

}
