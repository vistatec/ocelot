package com.vistatec.ocelot.config;

import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.config.json.LQIGridRootConfig;
import com.vistatec.ocelot.config.json.LqiGridConfig;
import com.vistatec.ocelot.config.json.LqiGridConfig.LQICategory;
import com.vistatec.ocelot.config.json.LqiGridConfig.LQICategory.Shortcut;
import com.vistatec.ocelot.config.json.LqiGridConfig.LQISeverity;
import com.vistatec.ocelot.lqi.model.LQIErrorCategory;
import com.vistatec.ocelot.lqi.model.LQIGrid;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQIShortCut;

public class LqiJsonConfigService {

	private final ConfigTransferService cfgXservice;
	
	private LQIGridRootConfig rootConfig;
	
	public LqiJsonConfigService(ConfigTransferService cfgXservice) throws TransferException {
		
		this.cfgXservice = cfgXservice;
//		this.rootConfig = (LQIGridRootConfig) cfgXservice.read();
		rootConfig = new LQIGridRootConfig();
    }
	
	public void saveLQIConfig(LQIGrid lqiGrid) throws TransferException{
		
		if(lqiGrid != null){
			rootConfig.clear();
			rootConfig.setActiveConfName(lqiGrid.getActiveConfName());
			if(lqiGrid.getConfigurations() != null){
				LqiGridConfig confConfig = null;
				for(LQIGridConfiguration modelConf: lqiGrid.getConfigurations()){
					confConfig = new LqiGridConfig();
					confConfig.setName(modelConf.getName());
					confConfig.setSupplier(modelConf.getSupplier());
					confConfig.setThreshold(modelConf.getThreshold());
					if(modelConf.getErrorCategories() != null){
						LQICategory confErrCat = null;
						LQIErrorCategory modelCat = null;
						for(int i = 0; i<modelConf.getErrorCategories().size(); i++){
							confErrCat = new LQICategory();
							modelCat = modelConf.getErrorCategories().get(i);
							confErrCat.setName(modelCat.getName());
							confErrCat.setPosition(i+1);
							confErrCat.setWeight(modelCat.getWeight());
							if(modelCat.getShortcuts() != null){
								Shortcut configSc = null;
								for(LQIShortCut modelSc: modelCat.getShortcuts()){
									configSc = new Shortcut();
									configSc.setKeyCode(modelSc.getKeyCode());
									configSc.setModifiers(modelSc.getModifiersString());
									configSc.setSeverity(modelSc.getSeverity().getName());
									confErrCat.addShortuct(configSc);
								}
							}
							confConfig.addErrorCategory(confErrCat);
						}
						if(modelConf.getSeverities() != null){
							LQISeverity configSev = null;
							for(com.vistatec.ocelot.lqi.model.LQISeverity modelSev: modelConf.getSeverities()){
								configSev = new LQISeverity(modelSev.getName(), modelSev.getScore());
								confConfig.addSeverity(configSev);
							}
						}
					}
					rootConfig.addLqiConfiguration(confConfig);
				}
			}
			cfgXservice.save(rootConfig);
		}
	}
	
	public LQIGrid readLQIConfig() throws TransferException {
		
		LQIGrid modelRootGrid = null;
		rootConfig = (LQIGridRootConfig) cfgXservice.read();
		if(rootConfig != null ){
			modelRootGrid = new LQIGrid();
			modelRootGrid.setActiveConfName(rootConfig.getActiveConfName());
			if(rootConfig.getLqiConfigurations() != null){
				LQIGridConfiguration modelConfig = null;
				for(LqiGridConfig confConfig: rootConfig.getLqiConfigurations()){
					modelConfig = new LQIGridConfiguration();
					modelConfig.setName(confConfig.getName());
					modelConfig.setSupplier(confConfig.getSupplier());
					modelConfig.setThreshold(confConfig.getThreshold());
					if(confConfig.getSeverities() != null){
						com.vistatec.ocelot.lqi.model.LQISeverity modelSev = null;
						for(LQISeverity configSev: confConfig.getSeverities()){
							modelSev = new com.vistatec.ocelot.lqi.model.LQISeverity();
							modelSev.setName(configSev.getName());
							modelSev.setScore(configSev.getScore());
							modelConfig.addSeverity(modelSev);
						}
						modelConfig.sortSeverities();
					}
					if(confConfig.getErrorCategories() != null){
						LQIErrorCategory modelCat = null;
						LQICategory confCat = null;
						for(int i=0; i<confConfig.getErrorCategories().size(); i++){
							confCat = confConfig.getErrorCategories().get(i);
							modelCat = new LQIErrorCategory();
							modelCat.setName(confCat.getName());
							modelCat.setWeight(confCat.getWeight());
							if(confCat.getShortcuts() != null){
								List<LQIShortCut> modelShortcuts = new ArrayList<LQIShortCut>();
								for(Shortcut confSc: confCat.getShortcuts()){
									modelShortcuts.add(new LQIShortCut(modelConfig.getSeverity(confSc.getSeverity()), confSc.getKeyCode(), confSc.getModifiers()));
								}
								modelCat.setShortcuts(modelShortcuts);
							}
							modelConfig.addErrorCategory(modelCat, i);
						}
					}
					modelRootGrid.addConfiguration(modelConfig);
				}
				
			}
		}
		return modelRootGrid;
	}
}
