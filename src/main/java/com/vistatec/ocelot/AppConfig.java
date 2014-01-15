/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot;

import com.vistatec.ocelot.plugins.Plugin;
import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ocelot application configuration preferences persistence class.
 */
public class AppConfig extends XMLConfiguration {
    private Logger LOG = LoggerFactory.getLogger(AppConfig.class);
    private File cfgFile;

    public AppConfig() {
        super();
    }

    public AppConfig(File cfgFile) throws ConfigurationException {
        super(cfgFile);
        this.cfgFile = cfgFile;
    }

    public boolean wasPluginEnabled(Plugin plugin) {
        return super.getString("plugins."+plugin.getPluginName()+".enabled") != null;
    }

    public void savePluginEnabled(Plugin plugin, boolean enabled) {
        if (enabled) {
            setProperty("plugins." + plugin.getPluginName() + ".enabled", "true");
        } else {
            clearProperty("plugins." + plugin.getPluginName() + ".enabled");
        }
    }

    @Override
    public void setProperty(String key, Object value) {
        super.setProperty(key, value);
        saveOcelotConfig();
    }

    @Override
    public void clearProperty(String key) {
        super.clearProperty(key);
        saveOcelotConfig();
    }

    private void saveOcelotConfig() {
        try {
            this.save(cfgFile);
        } catch (ConfigurationException ex) {
            LOG.error("Failed to save Ocelot configuration preferences", ex);
        }
    }
}
