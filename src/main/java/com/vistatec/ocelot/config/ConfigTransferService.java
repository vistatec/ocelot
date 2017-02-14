package com.vistatec.ocelot.config;

import com.vistatec.ocelot.config.json.RootConfig;

public interface ConfigTransferService {
	
	public RootConfig read() throws TransferException;
	
	public void save(RootConfig config) throws TransferException;

}
