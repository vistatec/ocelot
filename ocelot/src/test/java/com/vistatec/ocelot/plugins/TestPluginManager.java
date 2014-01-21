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
package com.vistatec.ocelot.plugins;

import com.vistatec.ocelot.config.AppConfig;
import java.io.File;
import java.net.URL;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestPluginManager {

    @Test
    public void testPluginManager() throws Exception {
        URL url = getClass().getResource("/");
        // If this assertion fails, it's probably something related
        // to the build environment
        assertNotNull(url);

        File pluginDir = new File(url.toURI());
        File testConfig = new File(pluginDir, "ocelot_test_cfg.xml");
        PluginManager pluginManager = new PluginManager(new AppConfig(testConfig));
        pluginManager.discover(pluginDir);

        Set<ITSPlugin> itsPlugins = pluginManager.getITSPlugins();
        assertEquals(1, itsPlugins.size());
        Plugin itsPlugin = itsPlugins.iterator().next();
        assertEquals("Sample ITS Plugin", itsPlugin.getPluginName());
        assertEquals("1.0", itsPlugin.getPluginVersion());

        Set<SegmentPlugin> segPlugins = pluginManager.getSegmentPlugins();
        assertEquals(1, segPlugins.size());
        Plugin segPlugin = segPlugins.iterator().next();
        assertEquals("Sample Segment Plugin", segPlugin.getPluginName());
        assertEquals("1.0", segPlugin.getPluginVersion());

        testConfig.delete();
    }
}
