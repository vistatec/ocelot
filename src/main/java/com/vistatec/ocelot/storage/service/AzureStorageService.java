package com.vistatec.ocelot.storage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import com.vistatec.ocelot.storage.service.util.AzureConsts;
import com.vistatec.ocelot.storage.service.util.Util;

/**
 * @author KatiaI
 *
 */
public class AzureStorageService implements StorageService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String accountName, accountKey;
	
	private String azureBlobContainer, azurePostUploadQueue;
	
	public void setAzurePostUploadQueue(String azurePostUploadQueue) {
		this.azurePostUploadQueue = azurePostUploadQueue;
	}

	private CloudStorageAccount storageAccount;
	
	public AzureStorageService(){
		
		Properties azureSProps = Util.getPropertyFile(AzureConsts.AZURE_FOLDER_NAME, AzureConsts.AZURE_S_FILE_NAME);
		Properties azureStorageProps = Util.getPropertyFile(AzureConsts.AZURE_FOLDER_NAME, AzureConsts.AZURE_STORAGE_FILE_NAME);
		
		if(azureSProps != null){
			accountName = azureSProps.getProperty("account.name");
			accountKey = azureSProps.getProperty("account.key");
		} else {
			logger.error("Could not locate storage credentials");
		}
		
		if(azureStorageProps != null) {
			azureBlobContainer = azureStorageProps.getProperty("account.blob.container.name");
			azurePostUploadQueue = azureBlobContainer + "-queue";
		} else {
			logger.error("Could not find customer storage settings");
		}
		
		final String storageConnectionString = String.format(AzureConsts.AZURE_STORAGE_CONNECTION_STRING, accountName, accountKey);
		
		logger.debug("Connection string to Storage System is {}", storageConnectionString);
		
		try {
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
		} catch (InvalidKeyException e) {
			logger.error("Invalid Credentials for Azure Storage Account {}",accountName);
			logger.error("Error:",e.getMessage());
		} catch (URISyntaxException e) {
			logger.error("String is not parsable as a uri reference.");
			logger.error("Error:",e.getMessage());
		}
		
	}
	
	public void setAzureBlobContainer(String azureBlobContainer) {
		this.azureBlobContainer = azureBlobContainer;
	}

	/**
	 * @param filePath the path of the file to be uploaded
	 * @param fileId the fileId of the file to be uploaded
	 * @return true if the file upload to Blob Storage is successful
	 */
	@Override
	public boolean uploadFileToBlobStorage(String filePath, String prefix, String fileId){
		
		CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
		
		try {
			CloudBlobContainer docsContainer = blobClient.getContainerReference(azureBlobContainer);
			docsContainer.createIfNotExists();
			
			String fileName = Paths.get(filePath).getFileName().toString();
			
			// this represents the directory structure in the Azure Blob Storage
			String structure = prefix + File.separator + fileId + File.separator + fileName;
			logger.debug("Structure on Storage should be {}", structure);
			CloudBlockBlob blob = docsContainer.getBlockBlobReference(structure);
		    File xlfFile = new File(filePath);
		    blob.upload(new FileInputStream(xlfFile), xlfFile.length());
		    return true;
		} catch (URISyntaxException e) {
			logger.error("String is not parsable as a uri reference.");
			logger.error("Error:",e.getMessage());
		} catch (StorageException e) {
			logger.error("Azure Storage Service error.");
			logger.error("Error:",e.getMessage());
		} catch (FileNotFoundException e) {
			logger.error("File {} could not be found.",filePath);
			logger.error("Error:",e.getMessage());
		} catch (IOException e) {
			logger.error("File {} could not be found.",filePath);
			logger.error("Error:",e.getMessage());
		}
		
		return false;
		
	}
	
	/**
	 * @param message the message to be sent to Azure Storage queue
	 * @return true if sending of message to Azure Storage queue is successful
	 */
	@Override
	public boolean sendMessageToPostUploadQueue(String message){
		
	    CloudQueueClient queueClient = storageAccount.createCloudQueueClient();
	    
	    try {
	    	CloudQueue queue = queueClient.getQueueReference(azurePostUploadQueue);
	    	queue.createIfNotExists();
	    	CloudQueueMessage queueMessage = new CloudQueueMessage(message);
	    	//queue.setShouldEncodeMessage(false);
	    	queue.addMessage(queueMessage);
	    	return true;
	    } catch (StorageException e) {
	    	logger.error("Azure Storage Service error.");
			logger.error("Error:",e.getMessage());
	    } catch (URISyntaxException e) {
	    	logger.error("String is not parsable as a uri reference.");
			logger.error("Error:",e.getMessage());
	    }
	     
	    return false;
	}
	
	/**
	 * @return true if no errors occur
	 */
	@Override
	public boolean pickMessageFromPostUploadQueue(){
		
	    CloudQueueClient queueClient = storageAccount.createCloudQueueClient();
	    
		try {
			CloudQueue queue = queueClient.getQueueReference(azurePostUploadQueue);
			//queue.setShouldEncodeMessage(false);
			queue.createIfNotExists();
			CloudQueueMessage peekedMessage = queue.peekMessage();
			if(peekedMessage != null){
	    		logger.info(peekedMessage.getMessageContentAsString());
	    	} else {
	    		logger.info("no message found.");
	    	}
	    	return true;
		} catch (URISyntaxException e) {
			logger.error("String is not parsable as a uri reference.");
			logger.error("Error:",e.getMessage());
		} catch (StorageException e) {
			logger.error("Azure Storage Service error.");
			logger.error("Error:",e.getMessage());
		}
    	
		return false;
    	
	}
	
	/**
	 * @return the Azure Storage queue name
	 */
	@Override
	public String getPostUploadQueueName() {
		return azurePostUploadQueue;
	}

	/**
	 * @return the Azure Blob Storage name
	 */
	@Override
	public String getBlobContainerName() {
		return azureBlobContainer;
	}

	
}
