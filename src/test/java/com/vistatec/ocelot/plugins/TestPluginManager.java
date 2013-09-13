package com.vistatec.ocelot.plugins;

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
        PluginManager pluginManager = new PluginManager();
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
    }
}
