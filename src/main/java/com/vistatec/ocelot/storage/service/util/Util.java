package com.vistatec.ocelot.storage.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vistatec.ocelot.storage.model.PostUploadRequest;

/**
 * @author KatiaI
 *
 */
public class Util {
	
	private static Logger logger = LoggerFactory.getLogger(Util.class);
	
	/**
	 * @param fileId the file identifier
	 * @return the PostUploadRequest object associated to the specified fileId
	 * that should be sent in Json format to the Storage queue
	 */
	public static PostUploadRequest getPostUploadRequest(String fileId){
		
		PostUploadRequest postUploadRequest = new PostUploadRequest();
		postUploadRequest.setFileId(fileId);
		return postUploadRequest;
	}
	
	/**
	 * @param object the T object to be serialized in Json
	 * @return the json associated to the object as a json string
	 */
	public static <T> String serializeToJson(T object){
		
		ObjectMapper mapper = new ObjectMapper();
	    String serializedResult = null;
		try {
			serializedResult = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			logger.error("Problem encountered while doing json processing.");
		}
		
		return serializedResult;
	}

}
