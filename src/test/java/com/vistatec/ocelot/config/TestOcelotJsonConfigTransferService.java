package com.vistatec.ocelot.config;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import com.vistatec.ocelot.config.json.OcelotRootConfig;
import com.vistatec.ocelot.config.json.RootConfig;

public class TestOcelotJsonConfigTransferService extends JsonConfigTransferService {

	private Writer writer;
	
	public TestOcelotJsonConfigTransferService(File confFile, Writer writer) {
	    super(confFile);
	    this.writer = writer;
    }

	@Override
    public RootConfig read() throws TransferException {
		OcelotRootConfig config = null;
        try {
	        config = mapper.readValue(confFile, OcelotRootConfig.class);
        } catch (IOException e) {
        	throw new TransferException(e);
        }
		return config;
    }

	@Override
    public void save(RootConfig config) throws TransferException {
	    
		LOG.debug("Saving the configuration of Ocelot...");
		try {
	        mapper.writeValue(writer, config);
        } catch (IOException e) {
        	throw new TransferException(e);
        }
    }

	
}
