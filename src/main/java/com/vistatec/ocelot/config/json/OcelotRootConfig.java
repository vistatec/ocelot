package com.vistatec.ocelot.config.json;

import java.util.ArrayList;
import java.util.List;

public class OcelotRootConfig implements RootConfig {

	private List<PluginConfig> plugins;
	private ProvenanceConfig userProvenance;
	private TmManagement tmManagement;
	private LayoutConfig layout;
	private LingoTekConfig lingoTek;
	private OcelotAzureConfig azure;
    private SpellingConfig spellingConfig;

	public OcelotRootConfig() {
		
		plugins = new ArrayList<PluginConfig>();
		userProvenance = new ProvenanceConfig();
		tmManagement = new TmManagement();
        spellingConfig = new SpellingConfig();
		
    }
	
	public void setPlugins(List<PluginConfig> plugins) {
		this.plugins = plugins;
	}

	public List<PluginConfig> getPlugins() {
		return plugins;
	}

	public void setUserProvenance(ProvenanceConfig userProvenance) {
		this.userProvenance = userProvenance;
	}

	public ProvenanceConfig getUserProvenance() {
		return userProvenance;
	}

	public void setTmManagement(TmManagement tmManagement) {
		this.tmManagement = tmManagement;
	}

	public TmManagement getTmManagement() {
		return tmManagement;
	}

    public void setSpellingConfig(SpellingConfig spellingConfig) {
        this.spellingConfig = spellingConfig;
    }

    public SpellingConfig getSpellingConfig() {
        return spellingConfig;
    }

	public void addPlugin(PluginConfig plugin) {

		if (plugin != null) {
			plugins.add(plugin);
		}
	}
	
	public void setLayout(LayoutConfig layout){
		this.layout = layout;
	}
	
	public LayoutConfig getLayout(){
		return layout;
	}
	
	public void setLingoTek(LingoTekConfig lingoTek){
		this.lingoTek = lingoTek;
	}
	
	public LingoTekConfig getLingoTek(){
		return lingoTek;
	}
	
	public void setAzure(OcelotAzureConfig azure) {
		this.azure = azure;
	}

	public OcelotAzureConfig getAzure() {
		return azure;
	}

	@Override
	public String toString() {

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Ocelot Configuration:\n");
		if (tmManagement != null) {
			strBuilder.append(tmManagement.toString());
			strBuilder.append("\n");
		}
		if (userProvenance != null) {
			strBuilder.append(userProvenance.toString());
			strBuilder.append("\n");
		}
		if (plugins != null) {
			for (PluginConfig plugin : plugins) {
				strBuilder.append(plugin.toString());
				strBuilder.append("\n");
			}
		}
		if(layout != null){
			strBuilder.append(layout.toString());
			strBuilder.append("\n");
		}
		return strBuilder.toString();
	}
}
