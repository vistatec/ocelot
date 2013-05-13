package com.spartansoftwareinc.plugins;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestPluginManager {

	@Test
	public void testPluginManager() throws Exception {
		URL url = getClass().getResource("/resources/");
		// If this assertion fails, it's probably something related
		// to the build environment
		assertNotNull(url);
		
		File pluginDir = new File(url.toURI());
		PluginManager pluginManager = new PluginManager();
		pluginManager.discover(pluginDir);
		List<Plugin> plugins = pluginManager.getPlugins();
		assertEquals(1, plugins.size());
		assertEquals("Sample Plugin", plugins.get(0).getPluginName());
		assertEquals("1.0", plugins.get(0).getPluginVersion());
	}
}
