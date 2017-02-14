package com.vistatec.ocelot.storage.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author KatiaI
 *
 */
public class PostUploadRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("fileId")
	private String fileId;
	
	public String getFileId() {
		return fileId;
	}
	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	@Override
	public String toString() {
		return "PostUploadRequest [fileId=" + fileId + "]";
	}
	
}
