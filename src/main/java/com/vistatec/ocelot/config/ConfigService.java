package com.vistatec.ocelot.config;

import java.util.List;

import com.vistatec.ocelot.config.xml.RootConfig;
import com.vistatec.ocelot.config.xml.PluginConfig;

import com.vistatec.ocelot.config.xml.ProvenanceConfig;
import com.vistatec.ocelot.config.xml.TmConfig;
import com.vistatec.ocelot.plugins.Plugin;

/**
 * Service for reading/saving configuration values.
 */
public class ConfigService {

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

    public void enableTm(String tmName, boolean enable) throws ConfigTransferService.TransferException {
        boolean addTmConfig = true;
        for (TmConfig.TmEnabled tm : config.getTmManagement().getTm()) {
            if (tm.getTmName().equals(tmName)) {
                tm.setEnabled(enable);
                addTmConfig = false;
            }
        }
        if (addTmConfig) {
            TmConfig.TmEnabled newTmConfig = new TmConfig.TmEnabled();
            newTmConfig.setTmName(tmName);
            newTmConfig.setEnabled(enable);
            config.getTmManagement().getTm().add(newTmConfig);
        }
        cfgXservice.save(config);
    }

    public void saveTmConfig(TmConfig tmCfg) throws ConfigTransferService.TransferException {
        config.getTmManagement().setTm(tmCfg.getTm());
        config.getTmManagement().setFuzzyThreshold(tmCfg.getFuzzyThreshold());
        config.getTmManagement().setMaxResults(tmCfg.getMaxResults());
        cfgXservice.save(config);
    }
}
