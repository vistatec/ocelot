package com.vistatec.ocelot.config;

import java.util.List;

import com.vistatec.ocelot.config.json.PluginConfig;
import com.vistatec.ocelot.config.json.TmManagement;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig;
import com.vistatec.ocelot.plugins.Plugin;

public interface JsonConfigService {
	
	public void saveConfig() throws TransferException;

    public boolean wasPluginEnabled(Plugin plugin);
    public void enablePlugin(Plugin plugin, boolean enabled);
    public void savePluginEnabled(Plugin plugin, boolean enabled) throws TransferException;

    public PluginConfig findPluginConfig(Plugin plugin);

    public UserProvenance getUserProvenance();
    public void saveUserProvenance(UserProvenance prov) throws TransferException;

    public double getFuzzyThreshold();
    public void saveFuzzyThreshold(float threshold) throws TransferException;
    public int getMaxResults();
    public void saveMaxResults(int maxResults) throws TransferException;

    public List<TmManagement.TmConfig> getTms();
    public void saveTms(List<TmManagement.TmConfig> tmConfigs) throws TransferException;
    public TmManagement.TmConfig getTmConfig(String tmName);

    public void enableTm(String tmName, boolean enable) throws TransferException;

    public void saveTmDataDir(TmManagement.TmConfig config, String tmDataDir) throws TransferException;

    public TmManagement.TmConfig createNewTmConfig(String tmName, boolean enabled, String tmDataDir) throws TransferException;
    
    public TmConfig createNewTmConfig(String tmName, boolean enabled, String tmDataDir,
	        List<String> tmxFiles) throws TransferException;
    
    public TmManagement.TmConfig createNewTmConfig(String tmName, boolean enabled, List<String> tmxFiles) throws TransferException;

}
