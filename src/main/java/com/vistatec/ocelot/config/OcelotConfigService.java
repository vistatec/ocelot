package com.vistatec.ocelot.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.ConfigTransferService.TransferException;
import com.vistatec.ocelot.config.xml.OcelotRootConfig;
import com.vistatec.ocelot.config.xml.PluginConfig;
import com.vistatec.ocelot.config.xml.ProvenanceConfig;
import com.vistatec.ocelot.config.xml.TmManagement;
import com.vistatec.ocelot.config.xml.TmManagement.TmConfig;
import com.vistatec.ocelot.config.xml.TmManagement.TmConfig.TmxFiles;
import com.vistatec.ocelot.plugins.Plugin;

/**
 * Service for reading/saving configuration values.
 */
public class OcelotConfigService implements ConfigService {
    private static final Logger LOG = LoggerFactory.getLogger(OcelotConfigService.class);

    private final ConfigTransferService cfgXservice;
    private OcelotRootConfig config;

    public OcelotConfigService(ConfigTransferService cfgXService) throws ConfigTransferService.TransferException {
        this.cfgXservice = cfgXService;
        this.config = (OcelotRootConfig) cfgXService.parse();
    }

    @Override
    public void saveConfig() throws ConfigTransferService.TransferException {
        cfgXservice.save(config);
    }

    @Override
    public boolean wasPluginEnabled(Plugin plugin) {
        PluginConfig pcfg = findPluginConfig(plugin);
        return pcfg.getEnabled();
    }

    @Override
    public void enablePlugin(Plugin plugin, boolean enabled) {
        PluginConfig pcfg = findPluginConfig(plugin);
        pcfg.setEnabled(enabled);
    }

    @Override
    public void savePluginEnabled(Plugin plugin, boolean enabled) throws ConfigTransferService.TransferException {
        enablePlugin(plugin, enabled);
        cfgXservice.save(config);
    }

    @Override
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

    @Override
    public UserProvenance getUserProvenance() {
        return new UserProvenance(config.getUserProvenance().getRevPerson(),
                config.getUserProvenance().getRevOrganization(),
                config.getUserProvenance().getExternalReference());
    }

    @Override
    public void saveUserProvenance(UserProvenance prov) throws ConfigTransferService.TransferException {
        ProvenanceConfig pConfig = config.getUserProvenance();
        pConfig.setRevPerson(prov.getRevPerson());
        pConfig.setRevOrganization(prov.getRevOrg());
        pConfig.setExternalReference(prov.getProvRef());
        cfgXservice.save(config);
    }

    @Override
    public double getFuzzyThreshold() {
        return config.getTmManagement().getFuzzyThreshold();
    }

    @Override
    public void saveFuzzyThreshold(float threshold) throws ConfigTransferService.TransferException {
        config.getTmManagement().setFuzzyThreshold(threshold);
        cfgXservice.save(config);
    }

    @Override
    public int getMaxResults() {
        return config.getTmManagement().getMaxResults();
    }

    @Override
    public void saveMaxResults(int maxResults) throws ConfigTransferService.TransferException {
        config.getTmManagement().setMaxResults(maxResults);
        cfgXservice.save(config);
    }

    @Override
    public List<TmManagement.TmConfig> getTms() {
        return config.getTmManagement().getTms();
    }

    @Override
    public void saveTms(List<TmManagement.TmConfig> tmConfig) throws ConfigTransferService.TransferException {
        config.getTmManagement().setTm(tmConfig);
        cfgXservice.save(config);
    }

    @Override
    public TmManagement.TmConfig getTmConfig(String tmName) {
        for (TmManagement.TmConfig tm : config.getTmManagement().getTms()) {
            if (tm.getTmName().equals(tmName)) {
                return tm;
            }
        }
        return null;
    }

    @Override
    public void enableTm(String tmName, boolean enable) throws ConfigTransferService.TransferException {
        TmManagement.TmConfig tmConfig = getTmConfig(tmName);
        if (tmConfig == null) {
            LOG.error("Missing TM configuration for '{}'", tmName);
            throw new IllegalStateException("Missing TM configuration for '"+tmName+"'");
        }
        tmConfig.setEnabled(enable);
        cfgXservice.save(config);
    }

    @Override
    public void saveTmDataDir(TmManagement.TmConfig tm, String tmDataDir) throws ConfigTransferService.TransferException {
        tm.setTmDataDir(tmDataDir);
        cfgXservice.save(config);
    }

    @Override
    public TmManagement.TmConfig createNewTmConfig(String tmName, boolean enabled, String tmDataDir) throws ConfigTransferService.TransferException {
        TmManagement.TmConfig newTmConfig = new TmManagement.TmConfig();
        newTmConfig.setTmName(tmName);
        newTmConfig.setEnabled(enabled);
        newTmConfig.setTmDataDir(tmDataDir);
        config.getTmManagement().getTms().add(newTmConfig);
        cfgXservice.save(config);
        return newTmConfig;
    }
    
    public TmConfig createNewTmConfig(String tmName, boolean enabled, String tmDataDir,
	        List<String> tmxFiles) throws TransferException {
		TmManagement.TmConfig newTmConfig = new TmManagement.TmConfig();
		newTmConfig.setTmName(tmName);
		newTmConfig.setEnabled(enabled);
		newTmConfig.setTmDataDir(tmDataDir);
		if (tmxFiles != null) {
			TmxFiles tmxFilesConf = new TmxFiles();
			tmxFilesConf.setTmxFile(tmxFiles);
			newTmConfig.setTmxFiles(tmxFilesConf);
		}
		config.getTmManagement().getTms().add(newTmConfig);
		cfgXservice.save(config);
		return newTmConfig;
	}
    

	@Override
	public TmConfig createNewTmConfig(String tmName, boolean enabled,
	        List<String> tmxFiles) throws TransferException {
		TmManagement.TmConfig newTmConfig = new TmManagement.TmConfig();
		newTmConfig.setTmName(tmName);
		newTmConfig.setEnabled(enabled);
		if (tmxFiles != null) {
			TmxFiles tmxFilesConf = new TmxFiles();
			tmxFilesConf.setTmxFile(tmxFiles);
			newTmConfig.setTmxFiles(tmxFilesConf);
		}
		config.getTmManagement().getTms().add(newTmConfig);
		cfgXservice.save(config);
		return newTmConfig;
	}

//	@Override
//    public void saveLQIConfig(LQIGrid lqiGrid) throws TransferException {
//	    
//		config.getLQIGrid().clear();
//		if(lqiGrid != null){
//			config.getLQIGrid().setMinor(lqiGrid.getMinorScore());
//			config.getLQIGrid().setSerious(lqiGrid.getSeriousScore());
//			config.getLQIGrid().setCritical(lqiGrid.getCriticalScore());
//			if(lqiGrid.getErrorCategories() != null && !lqiGrid.getErrorCategories().isEmpty()){
//				List<LQIGridConfig.LQICategory> lqiCategories = new ArrayList<LQIGridConfig.LQICategory>();
//				LQIGridConfig.LQICategory configCategory = null; 
//				LQIErrorCategory errorCat = null;
//				for(int i = 0; i<lqiGrid.getErrorCategories().size(); i++){
//					errorCat = lqiGrid.getErrorCategories().get(i);
//					configCategory = new LQIGridConfig.LQICategory();
//					configCategory.setName(errorCat.getName());
//					configCategory.setWeight(errorCat.getWeight());
//					configCategory.setPosition(i);
//					if (errorCat.getMinorShortcut() != null) {
//						Shortcut minor = new Shortcut();
//						minor.setKeyCode(errorCat.getMinorShortcut()
//						        .getKeyCode());
//						minor.setModifiers(errorCat.getMinorShortcut()
//						        .getModifiersString());
//						configCategory.setMinor(minor);
//					}
//					if (errorCat.getSeriousShortcut() != null) {
//						Shortcut serious = new Shortcut();
//						serious.setKeyCode(errorCat.getSeriousShortcut()
//						        .getKeyCode());
//						serious.setModifiers(errorCat.getSeriousShortcut()
//						        .getModifiersString());
//						configCategory.setSerious(serious);
//					}
//					if (errorCat.getCriticalShortcut() != null) {
//						Shortcut critical = new Shortcut();
//						critical.setKeyCode(errorCat.getCriticalShortcut()
//						        .getKeyCode());
//						critical.setModifiers(errorCat.getCriticalShortcut()
//						        .getModifiersString());
//						configCategory.setCritical(critical);
//					}
//					lqiCategories.add(configCategory);
//				}
//				config.getLQIGrid().setLqiCategories(lqiCategories);
//			}
//		} 
//		cfgXservice.save(config);
//    }

//	@Override
//    public LQIGrid readLQIConfig() throws TransferException {
//		LQIGridConfig confLqiGrid = config.getLQIGrid();
//		LQIGrid grid = null;
//		if(confLqiGrid != null){
//			grid = new LQIGrid();
//			grid.setCriticalScore(confLqiGrid.getCritical());
//			grid.setMinorScore(confLqiGrid.getMinor());
//			grid.setSeriousScore(confLqiGrid.getSerious());
//			if(confLqiGrid.getLqiCategories() != null){
//				Collections.sort(confLqiGrid.getLqiCategories(), new LQICategoriesComparator());
//				List<LQIErrorCategory> errCategories = new ArrayList<LQIErrorCategory>();
//				LQIErrorCategory errCat = null;
//				for (LQICategory cat : confLqiGrid.getLqiCategories()) {
//					errCat = new LQIErrorCategory(cat.getName());
//					errCat.setWeight(cat.getWeight());
//					if (cat.getMinor() != null) {
//						errCat.setMinorShortcut(new LQIShortCut(cat.getMinor()
//						        .getKeyCode(), cat.getMinor().getModifiers()));
//					}
//					if (cat.getSerious() != null) {
//						errCat.setSeriousShortcut(new LQIShortCut(cat
//						        .getSerious().getKeyCode(), cat.getSerious()
//						        .getModifiers()));
//					}
//					if (cat.getCritical() != null) {
//						errCat.setCriticalShortcut(new LQIShortCut(cat
//						        .getCritical().getKeyCode(), cat.getCritical()
//						        .getModifiers()));
//					}
//					errCategories.add(errCat);
//				}
//				grid.setErrorCategories(errCategories);
//			}
//		}
//	    return grid;
//    }
	
}


