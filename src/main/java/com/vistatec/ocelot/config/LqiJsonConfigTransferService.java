package com.vistatec.ocelot.config;

import java.io.File;
import java.io.IOException;

import com.vistatec.ocelot.config.json.LQIGridRootConfig;
import com.vistatec.ocelot.config.json.RootConfig;

public class LqiJsonConfigTransferService extends JsonConfigTransferService {

	public LqiJsonConfigTransferService(File confFile) {
	    super(confFile);
    }

	@Override
    public RootConfig read() throws TransferException {
		LOG.debug("Reading the main configuration of LQI grid...");		
		LQIGridRootConfig config = null;
        try {
	        config = mapper.readValue(confFile, LQIGridRootConfig.class);
        } catch (IOException e) {
        	LOG.error("Error while reading the configuration file of LQI grid", e);
        	throw new TransferException(e);
        }
		return config;
    }

	@Override
    public void save(RootConfig config) throws TransferException {
		LOG.debug("Saving the configuration of LQI grid...");
		try {
	        mapper.writeValue(confFile, config);
        } catch (IOException e) {
        	LOG.error("Error while writing the configuration file of LQI grid", e);
        	throw new TransferException(e);
        }
	    
    }

}
