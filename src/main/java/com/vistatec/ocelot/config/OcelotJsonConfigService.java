package com.vistatec.ocelot.config;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.SegmentViewColumn;
import com.vistatec.ocelot.config.json.LingoTekConfig;
import com.vistatec.ocelot.config.json.OcelotAzureConfig;
import com.vistatec.ocelot.config.json.OcelotRootConfig;
import com.vistatec.ocelot.config.json.PluginConfig;
import com.vistatec.ocelot.config.json.ProvenanceConfig;
import com.vistatec.ocelot.config.json.SpellingConfig;
import com.vistatec.ocelot.config.json.SpellingConfig.SpellingDictionary;
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
			                .getUserProvenance().getExtRef(), config
			                .getUserProvenance().getLangCode());
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
		provConf.setLangCode(prov.getLangCode());
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
    public SpellingConfig getSpellingConfig() {
        return config.getSpellingConfig();
    }

    @Override
    public SpellingDictionary getSpellingDictionary(String language) {
        return config.getSpellingConfig().getDictionary(language);
    }

    @Override
    public void saveSpellingDictionary(String language, SpellingDictionary dictionary) throws TransferException {
        config.getSpellingConfig().setDictionary(language, dictionary);
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
	
	@Override
	public boolean canShowManageConfsButton(){
		return config.getLayout().isShowManageConfs();
	}
	
public boolean isColumnEnabled(SegmentViewColumn column){
		
		boolean enabled = false;
		switch (column) {
		case SegNum:
			enabled = config.getLayout().getSegmentsGrid().isShowSegNum();
			break;
		case EditDistance:
			enabled = config.getLayout().getSegmentsGrid().isShowEditDist();
			break;
		case Notes:
			enabled = config.getLayout().getSegmentsGrid().isShowNotes();
			break;
		case Original:
			enabled = config.getLayout().getSegmentsGrid().isShowOriginalTarget();
			break;
		case Source:
			enabled = config.getLayout().getSegmentsGrid().isShowSource();
			break;
		case Target:
			enabled = config.getLayout().getSegmentsGrid().isShowTarget();
			break;
		default:
			if(column.isFlagColumn() ){
				enabled = isFlagColumnEnabled(column);
			}
			break;
		}
		return enabled;
	}

	private boolean isFlagColumnEnabled(SegmentViewColumn flagColumn){
		
		boolean enabled = false;
		boolean[] showFlags = config.getLayout().getSegmentsGrid().getShowFlags();
		if(showFlags != null && flagColumn.getFlagIndex() < showFlags.length){
			enabled = showFlags[flagColumn.getFlagIndex()];
		}
		return enabled;
	}
	
	@Override
	public void saveColumnConfiguration(EnumMap<SegmentViewColumn, Boolean> enabledColumns)
			throws TransferException {

		boolean[] flagColsEnabled = new boolean[SegmentViewColumn.getFlagColumnCount()];
		for(SegmentViewColumn col: SegmentViewColumn.values()){
			switch (col) {
			case EditDistance:
				config.getLayout().getSegmentsGrid().setShowEditDist(enabledColumns.get(col));
				break;
			case Notes:
				config.getLayout().getSegmentsGrid().setShowNotes(enabledColumns.get(col));
				break;
			case Original:
				config.getLayout().getSegmentsGrid().setShowOriginalTarget(enabledColumns.get(col));
				break;
			case SegNum:
				config.getLayout().getSegmentsGrid().setShowSegNum(enabledColumns.get(col));
				break;
			case Source:
				config.getLayout().getSegmentsGrid().setShowSource(enabledColumns.get(col));
				break;
			case Target:
				config.getLayout().getSegmentsGrid().setShowTarget(enabledColumns.get(col));
				break;
			default:
				if(col.isFlagColumn()){
					flagColsEnabled[col.getFlagIndex()] = enabledColumns.get(col);
				}
				break;
			}
		}
		config.getLayout().getSegmentsGrid().setShowFlags(flagColsEnabled);
		cfgXservice.save(config);
		
	}

	@Override
	public LingoTekConfig getLingoTekConfigurationParams() {
		return config.getLingoTek();
	}

	@Override
	public boolean isLingoTekConfigured() {
		return config.getLingoTek() != null && config.getLingoTek().isComplete();
	}
	
	@Override
	public OcelotAzureConfig getOcelotAzureConfiguration() {
		return config.getAzure();
	}

	@Override
	public boolean isShowNotTranslatableRows() {
		return config.getLayout().getSegmentsGrid().isShowNotTranslatableRows();
	}

	@Override
	public void saveNotTransRowConfig(boolean showNotTransRows) throws TransferException {
		config.getLayout().getSegmentsGrid().setShowNotTranslatableRows(showNotTransRows);
		cfgXservice.save(config);
	}

}
