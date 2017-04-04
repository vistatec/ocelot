package com.vistatec.ocelot.storage.service.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.vistatec.ocelot.storage.model.PostUploadRequest;

public class UtilTest {
	
	private String fileId;
	
	@Before
	public void setUp(){
		fileId = "test-fileId";
	}

	@Test
	public void testGetPostUploadRequest() {
		PostUploadRequest postUploadRequest = Util.getPostUploadRequest(fileId);
		assertEquals(fileId,postUploadRequest.getFileId());
	}

	@Test
	public void testSerializeToJson() {
		PostUploadRequest request = new PostUploadRequest();
		request.setFileId(fileId);
		String serializedToJson = Util.serializeToJson(request);
		assertTrue(serializedToJson.contains(fileId));
	}
}
