package com.vistatec.ocelot.config;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.vistatec.ocelot.config.xml.OcelotRootConfig;
import com.vistatec.ocelot.config.xml.RootConfig;

/**
 * Use JAXB to read and save Ocelot's configuration from/to an XML file.
 */
public class OcelotXmlConfigTransferService extends XmlConfigTransferService {
    
    public OcelotXmlConfigTransferService(ByteSource parseResource, CharSink saveResource) throws JAXBException {
    	super(parseResource, saveResource, OcelotRootConfig.class);
    }

    @Override
    public RootConfig parse() throws TransferException {
        try (InputStream readStream = this.parseResource.openStream()) {
            if (parseResource.isEmpty()) {
                return new OcelotRootConfig();
            }
            Unmarshaller importer = jaxb.createUnmarshaller();

            return (OcelotRootConfig) importer.unmarshal(readStream);
        } catch (JAXBException ex) {
            LOG.error("Exception handling JAXB content", ex);
            throw new TransferException(ex);
        } catch (IOException ex) {
            LOG.error("Failed to open stream to config file", ex);
            throw new TransferException(ex);
        }
    }

    
}
