package com.spartansoftwareinc.plugins.samples;

import com.spartansoftwareinc.plugins.Plugin;

/**
 * Sample plugin that does nothing.
 */
public class SamplePlugin implements Plugin {

	@Override
	public String getPluginName() {
		return "Sample Plugin";
	}
	
	@Override
	public String getPluginVersion() {
		return "1.0";
	}
}
