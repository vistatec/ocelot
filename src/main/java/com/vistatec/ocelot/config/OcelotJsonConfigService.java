package com.vistatec.ocelot.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.config.json.OcelotRootConfig;
import com.vistatec.ocelot.config.json.PluginConfig;
import com.vistatec.ocelot.config.json.ProvenanceConfig;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig;
import com.vistatec.ocelot.config.json.TmManagement.TmConfig.TmxFile;
import com.vistatec.ocelot.plugins.Plugin;

public class OcelotJsonConfigService implements JsonConfigService {

	private static final Logger LOG = LoggerFactory
	        .getLogger(OcelotJsonConfigService.class);
	private final ConfigTransferService cfgXservice;
	private OcelotRootConfig config;

	public OcelotJsonConfigService(ConfigTransferService cfgXservice)
	        throws TransferException {
		this.cfgXservice = cfgXservice;
		this.config = (OcelotRootConfig) cfgXservice.read();
	}

	@Override
	public void saveConfig() throws TransferException {

		cfgXservice.save(config);
	}

	@Override
	public boolean wasPluginEnabled(Plugin plugin) {
		PluginConfig pluginConf = findPluginConfig(plugin);
		return pluginConf.isEnabled();
	}

	@Override
	public void enablePlugin(Plugin plugin, boolean enabled) {
		PluginConfig pluginConf = findPluginConfig(plugin);
		pluginConf.setEnabled(enabled);

	}

	@Override
	public void savePluginEnabled(Plugin plugin, boolean enabled)
	        throws TransferException {

		enablePlugin(plugin, enabled);
		cfgXservice.save(config);

	}

	@Override
	public PluginConfig findPluginConfig(Plugin plugin) {

		PluginConfig pluginConf = null;
		if (config.getPlugins() != null) {
			Iterator<PluginConfig> plugIt = config.getPlugins().iterator();
			PluginConfig currPlugConf = null;
			while (plugIt.hasNext() && pluginConf == null) {
				currPlugConf = plugIt.next();
				if (currPlugConf.matches(plugin)) {
					pluginConf = currPlugConf;
				}
			}
		}
		if (pluginConf == null) {
			pluginConf = new PluginConfig(plugin, false);
			config.addPlugin(pluginConf);
		}
		return pluginConf;
	}

	@Override
	public UserProvenance getUserProvenance() {
		UserProvenance prov = null;
		if (config.getUserProvenance() != null) {
			prov = new UserProvenance(
			        config.getUserProvenance().getRevPerson(), config
			                .getUserProvenance().getRevOrg(), config
			                .getUserProvenance().getExtRef());
		}
		return prov;
	}

	@Override
	public void saveUserProvenance(UserProvenance prov)
	        throws TransferException {

		ProvenanceConfig provConf = new ProvenanceConfig();
		provConf.setRevPerson(prov.getRevPerson());
		provConf.setRevOrg(prov.getRevOrg());
		provConf.setExtRef(prov.getProvRef());
		config.setUserProvenance(provConf);
		cfgXservice.save(config);

	}

	@Override
	public double getFuzzyThreshold() {
		return config.getTmManagement().getFuzzyThreshold();
	}

	@Override
	public void saveFuzzyThreshold(float threshold) throws TransferException {

		config.getTmManagement().setFuzzyThreshold(threshold);
		cfgXservice.save(config);
	}

	@Override
	public int getMaxResults() {
		return config.getTmManagement().getMaxResults();
	}

	@Override
	public void saveMaxResults(int maxResults) throws TransferException {

		config.getTmManagement().setMaxResults(maxResults);
		cfgXservice.save(config);
	}

	@Override
	public List<TmConfig> getTms() {
		return config.getTmManagement().getTms();
	}

	@Override
	public void saveTms(List<TmConfig> tmConfigs) throws TransferException {

		config.getTmManagement().setTms(tmConfigs);
		cfgXservice.save(config);
	}

	@Override
	public TmConfig getTmConfig(String tmName) {

		TmConfig tm = null;
		for (TmConfig currTm : config.getTmManagement().getTms()) {
			if (currTm.getTmName().equals(tmName)) {
				tm = currTm;
				break;
			}
		}
		return tm;
	}

	@Override
	public void enableTm(String tmName, boolean enable)
	        throws TransferException {

		TmConfig tm = getTmConfig(tmName);
		if (tm == null) {
			LOG.error("Missing TM configuration for '{}'", tmName);
			throw new IllegalStateException("Missing TM configuration for '"
			        + tmName + "'");
		}
		tm.setEnabled(enable);
		cfgXservice.save(config);
	}

	@Override
	public void saveTmDataDir(TmConfig tmConf, String tmDataDir)
	        throws TransferException {

		tmConf.setTmDataDir(tmDataDir);
		cfgXservice.save(config);
	}

	@Override
	public TmConfig createNewTmConfig(String tmName, boolean enabled,
	        String tmDataDir) throws TransferException {
		return createNewTmConfig(tmName, enabled, tmDataDir, null);
	}

	@Override
	public TmConfig createNewTmConfig(String tmName, boolean enabled,
	        String tmDataDir, List<String> tmxFiles) throws TransferException {

		TmConfig newTmConf = new TmConfig();
		newTmConf.setTmName(tmName);
		newTmConf.setEnabled(enabled);
		newTmConf.setTmDataDir(tmDataDir);
		if (tmxFiles != null) {
			List<TmxFile> files = new ArrayList<TmxFile>();
			TmxFile file = null;
			for (String tmxFileName : tmxFiles) {
				file = new TmxFile();
				file.setName(tmxFileName);
				files.add(file);
			}
			newTmConf.setTmxFiles(files);
		}
		config.getTmManagement().addTm(newTmConf);
		cfgXservice.save(config);
		return newTmConf;
	}

	@Override
	public TmConfig createNewTmConfig(String tmName, boolean enabled,
	        List<String> tmxFiles) throws TransferException {
		return createNewTmConfig(tmName, enabled, null, tmxFiles);
	}

	@Override
    public boolean isTmPanelVisible() {
	    return config.getLayout().isShowTranslations();
    }

	@Override
    public boolean isAttributesViewVisible() {
	    return config.getLayout().isShowAttrsView();
    }

	@Override
    public boolean isDetailsViewVisible() {
	    return config.getLayout().isShowDetailsView();
    }

}
