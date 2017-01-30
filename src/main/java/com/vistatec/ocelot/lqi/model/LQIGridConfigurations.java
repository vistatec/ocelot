package com.vistatec.ocelot.lqi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The LQI grid object.
 */
public class LQIGridConfigurations {

	private List<LQIGridConfiguration> configurations;
	
	
	public void setConfigurations(List<LQIGridConfiguration> configurations){ 
		this.configurations = configurations;
	}
	
	public List<LQIGridConfiguration> getConfigurations(){
		return configurations;
	}
	
	public void addConfiguration(LQIGridConfiguration configuration){
		if(configurations == null){
			configurations = new ArrayList<LQIGridConfiguration>();
		}
		
		configurations.add(configuration);
	}
	
	public LQIGridConfiguration getActiveConfiguration(){
		
		LQIGridConfiguration activeConf = null;
		if(configurations != null){
			for(LQIGridConfiguration currConf: configurations){
				if(currConf.isActive()){
					activeConf = currConf;
					break;
				}
			}
		}
		return activeConf;
	}
	
	public void setActiveConfiguration(LQIGridConfiguration activeConf){
		
		if(configurations != null){
			for(LQIGridConfiguration conf: configurations ){
				conf.setActive(conf.equals(activeConf));
			}
		}
	}
	
	public boolean isEmpty(){
		
		return configurations == null || configurations.isEmpty();
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
	 
		LQIGridConfigurations lqiGrid = new LQIGridConfigurations();
		if(configurations != null){
			for(LQIGridConfiguration conf: configurations){
				lqiGrid.addConfiguration((LQIGridConfiguration)conf.clone());
			}
		}
	    return lqiGrid;
	}
//	public LQIGridConfiguration getConfigurationByName(String name){
//		
//		LQIGridConfiguration config = null;
//		
//		if(configurations != null){
//			for(LQIGridConfiguration conf: configurations){
//				if(conf.getName().equals(name)){
//					config = conf;
//					break;
//				}
//			}
//		}
//		return config;
//	}

	public void updateConfiguration(LQIGridConfiguration updatedConf) {

		if(configurations != null){
			for(int i = 0; i<configurations.size(); i++){
				if(configurations.get(i).getName().equals(updatedConf.getName())){
					configurations.set(i, updatedConf);
					break;
				}
			}
		}
    }
	
}
