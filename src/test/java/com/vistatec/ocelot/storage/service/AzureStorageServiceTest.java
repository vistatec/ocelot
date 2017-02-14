package com.vistatec.ocelot.storage.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.storage.model.PostUploadRequest;
import com.vistatec.ocelot.storage.service.util.Util;

public class AzureStorageServiceTest {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	AzureStorageService storageService;
	String jsonMessage;
	String xlfFilePath;
	String fileId;
	String testQueue;

	@Before
	public void setUp() {
		
		storageService = new AzureStorageService();
		fileId = UUID.randomUUID().toString();
		jsonMessage = getSerializedMessageForTest(fileId);
		
		testQueue = storageService.getPostUploadQueueName() + "-test";
		storageService.setAzurePostUploadQueue(testQueue);
        
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			Path path = Paths.get(loader.getResource("azure_test_file.xlf").toURI());
			xlfFilePath = path.toString();
		} catch (URISyntaxException e) {
			logger.error("There was an error while getting xlfFilePath.");
		}
        
	}

	@Test
	public void testUploadFileToBlobStorage() {
		boolean uploadedFileToBlobStorage = storageService.uploadFileToBlobStorage(xlfFilePath, "unit-tests", fileId);
		assertTrue(uploadedFileToBlobStorage);
	}

	@Test
	public void testSendMessageToPostUploadQueue() {
		boolean sentMessageToPostUploadQueue = storageService.sendMessageToPostUploadQueue(jsonMessage);
		assertTrue(sentMessageToPostUploadQueue);
	}

	@Test
	public void testPickMessageFromPostUploadQueue() {
		boolean pickedMessageFromPostUploadQueue = storageService.pickMessageFromPostUploadQueue();
		assertTrue(pickedMessageFromPostUploadQueue);
	}

	@Test
	public void testGetPostUploadQueueName() {
		String postUploadQueueName = storageService.getPostUploadQueueName();
		assertEquals(testQueue, postUploadQueueName);
	}

	@Test
	public void testGetBlobContainerName() {
		storageService.setAzureBlobContainer("blob-container-for-test");
		String blobContainerName = storageService.getBlobContainerName();
		assertEquals("blob-container-for-test",blobContainerName);
	}
	
	private String getSerializedMessageForTest(String fileId){
		
		PostUploadRequest request = new PostUploadRequest();
		request.setFileId(fileId);
		logger.info("Test Message {}.",request);
		String serializedToJson = Util.serializeToJson(request);
		logger.info("Test Message in json format {}.",serializedToJson);
		return serializedToJson;
	}

}
