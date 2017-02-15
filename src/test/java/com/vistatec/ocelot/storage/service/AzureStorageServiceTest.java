package com.vistatec.ocelot.storage.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.ConfigurationException;
import com.vistatec.ocelot.config.ConfigurationManager;
import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.config.json.OcelotAzureConfig;
import com.vistatec.ocelot.storage.model.PostUploadRequest;
import com.vistatec.ocelot.storage.service.util.Util;

public class AzureStorageServiceTest {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private AzureStorageService storageService;
	
	String jsonMessage;
	String xlfFilePath;
	String fileId;
	String testQueue;
	
	boolean execute;
	
	@Before
	public void setUp() {
		
		ConfigurationManager manager = new ConfigurationManager();
		String refDir = System.getProperty("user.home") + File.separator + ".Ocelot" ;
		try {
			manager.readAndCheckConfiguration(new File(refDir));
			JsonConfigService configService = manager.getOcelotConfigService();
			OcelotAzureConfig ocelotAzureConfiguration = configService.getOcelotAzureConfiguration();
			if(ocelotAzureConfiguration != null){
				String accountKey = ocelotAzureConfiguration.getAccountKey();
				String accountName = ocelotAzureConfiguration.getAccountName();
				String accountBlobContainerName = ocelotAzureConfiguration.getAccountBlobContainerName();
				
				execute = accountName != null && accountKey != null && accountBlobContainerName != null;
				
				if(execute){
					storageService = new AzureStorageService(accountName,accountKey,accountBlobContainerName);
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
			} 
		} catch (ConfigurationException exc) {
			logger.error("Configuration error");
		}
			
		
	}

	@Test
	public void testUploadFileToBlobStorage() {
		if(execute){
			boolean uploadedFileToBlobStorage = storageService.uploadFileToBlobStorage(xlfFilePath, "unit-tests", fileId);
			assertTrue(uploadedFileToBlobStorage);
		}
		
	}

	@Test
	public void testSendMessageToPostUploadQueue() {
		if(execute){
			boolean sentMessageToPostUploadQueue = storageService.sendMessageToPostUploadQueue(jsonMessage);
			assertTrue(sentMessageToPostUploadQueue);
		}
	}

	@Test
	public void testPickMessageFromPostUploadQueue() {
		if(execute){
			boolean pickedMessageFromPostUploadQueue = storageService.pickMessageFromPostUploadQueue();
			assertTrue(pickedMessageFromPostUploadQueue);
		}
		
	}

	@Test
	public void testGetPostUploadQueueName() {
		if(execute){
			String postUploadQueueName = storageService.getPostUploadQueueName();
			assertEquals(testQueue, postUploadQueueName);
		}
		
	}

	@Test
	public void testGetBlobContainerName() {
		if(execute){
			storageService.setAzureBlobContainer("blob-container-for-test");
			String blobContainerName = storageService.getBlobContainerName();
			assertEquals("blob-container-for-test",blobContainerName);
		}
		
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
