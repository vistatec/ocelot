package com.vistatec.ocelot.config;

import java.io.File;
import java.io.IOException;

import com.vistatec.ocelot.config.json.ProfileConfig;
import com.vistatec.ocelot.config.json.RootConfig;

public class ProfileConfigTransferService extends JsonConfigTransferService {

	public ProfileConfigTransferService(File confFile) {
	    super(confFile);
    }

	@Override
    public RootConfig read() throws TransferException {
		
		LOG.debug("Reading the Profile configuration...");		
		ProfileConfig config = null;
        try {
	        config = mapper.readValue(confFile, ProfileConfig.class);
        } catch (IOException e) {
        	LOG.error("Error while reading the Profile configuration file", e);
        	throw new TransferException(e);
        }
		return config;
    }

	@Override
    public void save(RootConfig config) throws TransferException {
		LOG.debug("Saving the Profile configuration...");
		try {
	        mapper.writeValue(confFile, config);
        } catch (IOException e) {
        	LOG.error("Error while writing the Profile configuration file.", e);
        	throw new TransferException(e);
        }
	    
    }


}
