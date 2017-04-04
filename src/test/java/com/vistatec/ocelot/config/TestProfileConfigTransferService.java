package com.vistatec.ocelot.config;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.vistatec.ocelot.config.json.OcelotRootConfig;
import com.vistatec.ocelot.config.json.ProfileConfig;
import com.vistatec.ocelot.config.json.RootConfig;

public class TestProfileConfigTransferService extends JsonConfigTransferService {

	private Writer writer;
	
	public TestProfileConfigTransferService(File confFile, Writer writer) {
	    super(confFile);
	    this.writer = writer;
    }

	@Override
    public RootConfig read() throws TransferException {
		
		ProfileConfig config = null;
		try {
	        config = mapper.readValue(confFile, ProfileConfig.class);
        } catch (IOException e) {
        	throw new TransferException(e);
        }
	    return config;
    }

	@Override
    public void save(RootConfig config) throws TransferException {
		LOG.debug("Saving the profile configuration...");
		try {
	        mapper.writeValue(writer, config);
        } catch (IOException e) {
        	throw new TransferException(e);
        }
	    
    }

}
