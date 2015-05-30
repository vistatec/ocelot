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
package com.vistatec.ocelot.config.xml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML Root Ocelot configuration element.
 */
@XmlRootElement
public class RootConfig {
    @XmlElement protected List<PluginConfig> plugins;
    @XmlElement protected ProvenanceConfig userProvenance;
    @XmlElement protected TmManagement tmManagement;

    public RootConfig() {
        this.plugins = new ArrayList<>();
        this.userProvenance = new ProvenanceConfig();
        this.tmManagement = new TmManagement();
    }

    public List<PluginConfig> getPlugins() {
        return plugins;
    }

    public ProvenanceConfig getUserProvenance() {
        return userProvenance;
    }

    public TmManagement getTmManagement() {
        return tmManagement;
    }

}
