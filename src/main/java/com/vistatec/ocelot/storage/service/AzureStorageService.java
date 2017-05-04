package com.vistatec.ocelot.storage.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueMessage;

/**
 * @author KatiaI
 *
 */
public class AzureStorageService implements StorageService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String sas, blobEndpoint, queueEndpoint;

	public AzureStorageService(String sas, String blobEndpoint, String queueEndpoint) {

		this.sas = sas;
		this.blobEndpoint = blobEndpoint;
		this.queueEndpoint = queueEndpoint;
	}


	/**
	 * @param filePath
	 *            the path of the file to be uploaded
	 * @param fileId
	 *            the fileId of the file to be uploaded
	 * @return true if the file upload to Blob Storage is successful
	 */
	@Override
	public boolean uploadFileToBlobStorage(String filePath, String prefix, String fileId, String fileName) {

		boolean uploaded = false;
		boolean canUpload = sas != null && blobEndpoint != null;

		if (canUpload) {
			try {
				String urlEncodedFileName = URLEncoder.encode(fileName, "UTF-8");
				String blobURIString = blobEndpoint + "/" + prefix + "/" + fileId + "/" + urlEncodedFileName + "?" + sas;
				CloudBlockBlob blob = new CloudBlockBlob(new URI(blobURIString));
				File xlfFile = new File(filePath);
				blob.upload(new FileInputStream(xlfFile), xlfFile.length());
				logger.info("Uploading to blob: \n" + blobURIString);
				uploaded = true;
			} catch (StorageException e) {
				logger.error("Azure Storage Service error.", e);
			} catch (URISyntaxException e) {
				logger.error("String is not parsable as a uri reference.", e);
			} catch (IOException e) {
				logger.error("File " + filePath + " could not be found.", e);
			}
		} else {
			logger.error("No configuration parameters to handle Storage task");
		}

		return uploaded;

	}

	/**
	 * @param message
	 *            the message to be sent to Azure Storage queue
	 * @return true if sending of message to Azure Storage queue is successful
	 */
	@Override
	public boolean sendMessageToPostUploadQueue(String message) {

		boolean messageSent = false;
		boolean canSendMessage = sas != null && queueEndpoint != null;

		if (canSendMessage) {
			try {
				logger.info("Sending message to queue:\n");
				CloudQueue queue = getQueue();
				CloudQueueMessage queueMessage = new CloudQueueMessage(message);
				queue.addMessage(queueMessage);
				messageSent = true;
			} catch (StorageException e) {
				logger.error("Azure Storage Service error.", e);
			} catch (URISyntaxException e) {
				logger.error("String is not parsable as a uri reference.", e);
			}
		} else {
			logger.error("No configuration parameters to handle Storage task");
		}

		return messageSent;

	}

	/**
	 * @return true if no errors occur
	 */
	@Override
	public boolean pickMessageFromPostUploadQueue() {

		boolean messagePicked = false;
		boolean canPickMessage = sas != null && queueEndpoint != null;

		if (canPickMessage) {
			CloudQueue queue;
			try {
				logger.info("Picking message from queue:\n");
				queue = getQueue();
				CloudQueueMessage peekedMessage = queue.peekMessage();
				if (peekedMessage != null) {
					logger.info(peekedMessage.getMessageContentAsString());
				} else {
					logger.info("no message found.");
				}
				messagePicked = true;
			} catch (StorageException e) {
				logger.error("Azure Storage Service error.", e);
			} catch (URISyntaxException e) {
				logger.error("String is not parsable as a uri reference.", e);
			}
		} else {
			logger.error("No configuration parameters to handle Storage task");
		}

		return messagePicked;
	}

	private CloudQueue getQueue() throws StorageException, URISyntaxException {
		String queueUriString = queueEndpoint + "?" + sas;
		CloudQueue queue = new CloudQueue(new URI(queueUriString));
		 queue.createIfNotExists();
		logger.info(queueUriString);
		return queue;
	}

}
