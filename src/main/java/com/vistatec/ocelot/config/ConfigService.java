package com.vistatec.ocelot.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.xml.RootConfig;
import com.vistatec.ocelot.config.xml.PluginConfig;

import com.vistatec.ocelot.config.xml.ProvenanceConfig;
import com.vistatec.ocelot.config.xml.TmConfig;
import com.vistatec.ocelot.plugins.Plugin;

/**
 * Service for reading/saving configuration values.
 */
public class ConfigService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigService.class);

    private final ConfigTransferService cfgXservice;
    private RootConfig config;

    public ConfigService(ConfigTransferService cfgXService) throws ConfigTransferService.TransferException {
        this.cfgXservice = cfgXService;
        this.config = cfgXService.parse();
    }

    public boolean wasPluginEnabled(Plugin plugin) {
        PluginConfig pcfg = findPluginConfig(plugin);
        return pcfg.getEnabled();
    }

    public void enablePlugin(Plugin plugin, boolean enabled) {
        PluginConfig pcfg = findPluginConfig(plugin);
        pcfg.setEnabled(enabled);
    }

    public void savePluginEnabled(Plugin plugin, boolean enabled) throws ConfigTransferService.TransferException {
        enablePlugin(plugin, enabled);
        cfgXservice.save(config);
    }

    public PluginConfig findPluginConfig(Plugin plugin) {
        PluginConfig foundPluginConfig = null;
        for (PluginConfig pcfg : config.getPlugins()) {
            if (pcfg.matches(plugin)) {
                foundPluginConfig = pcfg;
            }
        }

        if (foundPluginConfig == null) {
            foundPluginConfig = new PluginConfig(plugin, false);
            config.getPlugins().add(foundPluginConfig);
        }
        return foundPluginConfig;
    }

    public UserProvenance getUserProvenance() {
        return new UserProvenance(config.getUserProvenance().getRevPerson(),
                config.getUserProvenance().getRevOrganization(),
                config.getUserProvenance().getExternalReference());
    }

    public void saveUserProvenance(UserProvenance prov) throws ConfigTransferService.TransferException {
        ProvenanceConfig pConfig = config.getUserProvenance();
        pConfig.setRevPerson(prov.getRevPerson());
        pConfig.setRevOrganization(prov.getRevOrg());
        pConfig.setExternalReference(prov.getProvRef());
        cfgXservice.save(config);
    }

    public double getFuzzyThreshold() {
        return config.getTmManagement().getFuzzyThreshold();
    }

    public int getMaxResults() {
        return config.getTmManagement().getMaxResults();
    }

    public List<TmConfig.TmEnabled> getTms() {
        return config.getTmManagement().getTm();
    }

    public TmConfig.TmEnabled getTmConfig(String tmName) {
        for (TmConfig.TmEnabled tm : config.getTmManagement().getTm()) {
            if (tm.getTmName().equals(tmName)) {
                return tm;
            }
        }
        return null;
    }

    public void enableTm(String tmName, boolean enable) throws ConfigTransferService.TransferException {
        TmConfig.TmEnabled tmConfig = getTmConfig(tmName);
        if (tmConfig == null) {
            LOG.error("Missing TM configuration for '{}'", tmName);
            throw new IllegalStateException("Missing TM configuration for '"+tmName+"'");
        }
        tmConfig.setEnabled(enable);
        cfgXservice.save(config);
    }

    public void saveTmDataDir(String tmName, String tmDataDir) throws ConfigTransferService.TransferException {
        for (TmConfig.TmEnabled tm : config.getTmManagement().getTm()) {
            if (tm.getTmName().equals(tmName)) {
                tm.setTmDataDir(tmDataDir);
            }
        }
        cfgXservice.save(config);
    }

    public TmConfig.TmEnabled createNewTmConfig(String tmName, boolean enabled, String tmDataDir) throws ConfigTransferService.TransferException {
        TmConfig.TmEnabled newTmConfig = new TmConfig.TmEnabled();
        newTmConfig.setTmName(tmName);
        newTmConfig.setEnabled(enabled);
        newTmConfig.setTmDataDir(tmDataDir);
        config.getTmManagement().getTm().add(newTmConfig);
        cfgXservice.save(config);
        return newTmConfig;
    }

    public void saveConfig() throws ConfigTransferService.TransferException {
        cfgXservice.save(config);
    }
}
