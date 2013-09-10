package com.spartansoftwareinc.plugins;

public interface Plugin {

    /**
     * Get the name of this plugin, for display in the Workbench UI.
     * @return plugin name
     */
    public String getPluginName();

    /**
     * Get the version of this plugin, for display in the Workbench UI.
     * @return plugin version
     */
    public String getPluginVersion();
}
