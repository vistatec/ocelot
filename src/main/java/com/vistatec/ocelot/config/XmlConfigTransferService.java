package com.vistatec.ocelot.config;

import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.vistatec.ocelot.config.xml.RootConfig;

public abstract class XmlConfigTransferService implements ConfigTransferService {

	protected static final Logger LOG = LoggerFactory
	        .getLogger(OcelotXmlConfigTransferService.class);

	protected final JAXBContext jaxb;
	protected final ByteSource parseResource;
	protected final CharSink saveResource;

	public XmlConfigTransferService(ByteSource parseResource,
	        CharSink saveResource, Class<? extends RootConfig> rootConfigClass)
	        throws JAXBException {

		this.jaxb = JAXBContext.newInstance(rootConfigClass);
		this.parseResource = parseResource;
		this.saveResource = saveResource;
	}

	@Override
    public void save(RootConfig cfg) throws TransferException {
        try (Writer saveStream = this.saveResource.openStream()) {
            Marshaller exporter = jaxb.createMarshaller();
            exporter.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            exporter.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            exporter.marshal(cfg, saveStream);
        } catch (JAXBException ex) {
            LOG.error("Exception saving JAXB content", ex);
            throw new TransferException(ex);
        } catch (IOException ex) {
            LOG.error("Failed to open stream to output file", ex);
            throw new TransferException(ex);
        }
    }
}
