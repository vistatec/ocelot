package com.vistatec.ocelot.config;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.vistatec.ocelot.config.xml.LQIGridConfig;
import com.vistatec.ocelot.config.xml.RootConfig;

public class LQIXmlConfigTransferService  extends XmlConfigTransferService {


	public LQIXmlConfigTransferService(ByteSource parseResource,
	        CharSink saveResource) throws JAXBException {
		
		super(parseResource, saveResource, LQIGridConfig.class);
	}

	@Override
    public RootConfig parse() throws TransferException {
		
		LQIGridConfig gridConfig = null;
		try {
			InputStream readStream = this.parseResource.openStream(); 
            if (parseResource.isEmpty()) {
                return new LQIGridConfig();
            }
            Unmarshaller importer = jaxb.createUnmarshaller();

            gridConfig = (LQIGridConfig) importer.unmarshal(readStream);
//        } catch (JAXBException ex) {
//        	LOG.error("Exception handling JAXB content", ex);
//            throw new TransferException(ex);
        } catch (IOException ex) {
        	LOG.error("Failed to open stream to config file", ex);
            throw new TransferException(ex);
        } catch (JAXBException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		return gridConfig;
    }

}
