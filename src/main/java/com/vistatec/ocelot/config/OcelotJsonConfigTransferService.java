package com.vistatec.ocelot.config;

import java.io.File;
import java.io.IOException;

import com.vistatec.ocelot.config.json.OcelotRootConfig;
import com.vistatec.ocelot.config.json.RootConfig;

public class OcelotJsonConfigTransferService extends JsonConfigTransferService {

	public OcelotJsonConfigTransferService(File confFile) {
	    super(confFile);
    }

	@Override
    public RootConfig read() throws TransferException {
		
		LOG.debug("Reading the main configuration of Ocelot...");		
		OcelotRootConfig config = null;
        try {
	        config = mapper.readValue(confFile, OcelotRootConfig.class);
        } catch (IOException e) {
        	LOG.error("Error while reading the configuration file of Ocelot", e);
        	throw new TransferException(e);
        }
		return config;
    }

	@Override
    public void save(RootConfig config) throws TransferException {

		LOG.debug("Saving the configuration of Ocelot...");
		try {
	        mapper.writeValue(confFile, config);
        } catch (IOException e) {
        	LOG.error("Error while writing the configuration file of Ocelot", e);
        	throw new TransferException(e);
        }
    }

}
