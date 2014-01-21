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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML Root Ocelot configuration element.
 */
@XmlRootElement
public class RootConfig {
    @XmlElement
    protected List<PluginConfig> plugins;

    public RootConfig() {
        plugins = new ArrayList<PluginConfig>();
    }

    public void enablePlugin(Plugin plugin, boolean enabled) {
        PluginConfig pcfg = findPluginConfig(plugin);
        pcfg.setEnabled(enabled);
    }

    public PluginConfig findPluginConfig(Plugin plugin) {
        PluginConfig foundPluginConfig = null;
        for (PluginConfig pcfg : plugins) {
            if (pcfg.matches(plugin)) {
                foundPluginConfig = pcfg;
            }
        }
        if (foundPluginConfig == null) {
            foundPluginConfig = new PluginConfig(plugin, false);
            addPluginConfig(foundPluginConfig);
        }
        return foundPluginConfig;
    }

    public void addPluginConfig(PluginConfig pluginConfig) {
        this.plugins.add(pluginConfig);
    }
}
