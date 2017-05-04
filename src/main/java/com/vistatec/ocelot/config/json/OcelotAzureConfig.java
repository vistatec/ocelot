package com.vistatec.ocelot.config.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OcelotAzureConfig {
	
	private String blobEndpoint;
	
	private String queueEndpoint;
	
	private String sas;
	
	public String getBlobEndpoint(){
		return blobEndpoint;
	}
	
	public void setBlobEndpoint( String blobEndpoint){
		
		this.blobEndpoint = blobEndpoint;
	}
	
	public String getQueueEndpoint(){
		return queueEndpoint;
	}
	
	public void setQueueEndpoint(String queueEndpoint){
		this.queueEndpoint = queueEndpoint;
	}
	
	public String getSas(){
		return sas;
	}
	
	public void setSas(String sas){
		this.sas = sas;
	}
	
	/**
	 * Checks if the configuration is completely defined.
	 * 
	 * @return <code>true</code> if all configuration parameters are defined;
	 *         <code>false</code> if at least one parameter is missing.
	 */
	@JsonIgnore
	public boolean isComplete(){
	
		return sas != null && blobEndpoint != null && queueEndpoint != null;
	}
}
