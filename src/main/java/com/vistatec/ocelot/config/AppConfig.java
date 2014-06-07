/*
 * Copyright (C) 2014, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.config;

import com.vistatec.ocelot.plugins.Plugin;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ocelot application configuration preferences persistence class.
 * Handles the marshalling and unmarshalling of configuration data to/from the
 * XML config file.
 */
public class AppConfig {
    private Logger LOG = LoggerFactory.getLogger(AppConfig.class);
    protected Configs configs;
    protected JAXBContext jaxb;
    protected RootConfig config = new RootConfig();

    /**
     * Create an empty configuration.
     */
    public AppConfig() { }

    /**
     * Create a configuration based on the specified files.
     * @param configs
     */
    public AppConfig(Configs configs) {
        this.configure(configs);
    }

    public void configure(Configs configs) {
        this.configs = configs;
        try {
            jaxb = JAXBContext.newInstance(RootConfig.class);
            Reader r = configs.getOcelotReader();
            if (r == null) {
                config = new RootConfig();
                marshal();
            } else {
                unmarshal(r);
            }
        } catch (JAXBException ex) {
            LOG.error("Exception handling JAXB content", ex);
        } catch (IOException ex) {
            LOG.error("Failed to create config file", ex);
        }
    }

    public boolean wasPluginEnabled(Plugin plugin) {
        PluginConfig pcfg = config.findPluginConfig(plugin);
        return pcfg.getEnabled();
    }

    public void savePluginEnabled(Plugin plugin, boolean enabled) {
        config.enablePlugin(plugin, enabled);
        try {
            marshal();
        } catch (Exception ex) {
            LOG.error("Failed to save plugin enabled configuration", ex);
        }
    }

    private void unmarshal(Reader r) throws JAXBException {
        Unmarshaller unmarshal = jaxb.createUnmarshaller();
        config = (RootConfig) unmarshal.unmarshal(r);
    }

    private void marshal() throws JAXBException, IOException {
        Marshaller marshaller = jaxb.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        Writer w = configs.getOcelotWriter();
        marshaller.marshal(config, w);
        w.close();
    }
}
