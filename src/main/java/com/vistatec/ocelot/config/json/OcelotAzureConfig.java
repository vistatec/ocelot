package com.vistatec.ocelot.config.json;

public class OcelotAzureConfig {
		
	private String accountName;
	private String accountKey;
	private String accountBlobContainerName;
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountKey() {
		return accountKey;
	}
	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}
	public String getAccountBlobContainerName() {
		return accountBlobContainerName;
	}
	public void setAccountBlobContainerName(String accountBlobContainerName) {
		this.accountBlobContainerName = accountBlobContainerName;
	}
	
}
