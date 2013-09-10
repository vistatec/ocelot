package com.spartansoftwareinc.plugins;

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
		Set<ITSPlugin> plugins = pluginManager.getITSPlugins();
		assertEquals(1, plugins.size());
		Plugin plugin = plugins.iterator().next();
		assertEquals("Sample Plugin", plugin.getPluginName());
		assertEquals("1.0", plugin.getPluginVersion());
	}
}
