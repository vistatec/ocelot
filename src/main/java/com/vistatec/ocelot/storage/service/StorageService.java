package com.vistatec.ocelot.storage.service;

/**
 * @author KatiaI
 *
 */
public interface StorageService {
	
	public boolean uploadFileToBlobStorage(String filePath, String prefix, String fileId);
	public boolean sendMessageToPostUploadQueue(String message);
	public boolean pickMessageFromPostUploadQueue();
	public String getPostUploadQueueName();
	public String getBlobContainerName();

}
