package com.vistatec.ocelot.config;

import java.util.List;

import com.vistatec.ocelot.config.xml.PluginConfig;
import com.vistatec.ocelot.config.xml.TmConfig;
import com.vistatec.ocelot.plugins.Plugin;

/**
 * Service for reading/saving configuration values.
 */
public interface ConfigService {

    public void saveConfig() throws ConfigTransferService.TransferException;

    public boolean wasPluginEnabled(Plugin plugin);
    public void enablePlugin(Plugin plugin, boolean enabled);
    public void savePluginEnabled(Plugin plugin, boolean enabled) throws ConfigTransferService.TransferException;

    public PluginConfig findPluginConfig(Plugin plugin);

    public UserProvenance getUserProvenance();
    public void saveUserProvenance(UserProvenance prov) throws ConfigTransferService.TransferException;

    public double getFuzzyThreshold();
    public int getMaxResults();

    public List<TmConfig.TmEnabled> getTms();
    public TmConfig.TmEnabled getTmConfig(String tmName);

    public void enableTm(String tmName, boolean enable) throws ConfigTransferService.TransferException;

    public void saveTmDataDir(TmConfig.TmEnabled config, String tmDataDir) throws ConfigTransferService.TransferException;

    public TmConfig.TmEnabled createNewTmConfig(String tmName, boolean enabled, String tmDataDir) throws ConfigTransferService.TransferException;
}
