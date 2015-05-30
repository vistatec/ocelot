package com.vistatec.ocelot.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.vistatec.ocelot.config.xml.RootConfig;

/**
 * Use JAXB to read and save Ocelot's configuration from/to an XML file.
 */
public class XmlConfigTransferService implements ConfigTransferService {
    private static final Logger LOG = LoggerFactory.getLogger(XmlConfigTransferService.class);

    private final JAXBContext jaxb;
    private final ByteSource parseResource;
    private final CharSink saveResource;

    public XmlConfigTransferService(ByteSource parseResource, CharSink saveResource) throws JAXBException {
        this.jaxb = JAXBContext.newInstance(RootConfig.class);
        this.parseResource = parseResource;
        this.saveResource = saveResource;
    }

    @Override
    public RootConfig parse() throws TransferException {
        try (InputStream readStream = this.parseResource.openStream()) {
            if (parseResource.isEmpty()) {
                return new RootConfig();
            }
            Unmarshaller importer = jaxb.createUnmarshaller();

            return (RootConfig) importer.unmarshal(readStream);
        } catch (JAXBException ex) {
            LOG.error("Exception handling JAXB content", ex);
            throw new TransferException(ex);
        } catch (IOException ex) {
            LOG.error("Failed to open stream to config file", ex);
            throw new TransferException(ex);
        }
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
