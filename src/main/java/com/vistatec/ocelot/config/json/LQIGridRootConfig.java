package com.vistatec.ocelot.config.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LQIGridRootConfig implements RootConfig {
	
	private String activeConfName;
	
	private List<LqiGridConfig> lqiConfigurations;
	
	public void setActiveConfName(String activeConfName){
		this.activeConfName = activeConfName;
	}
	
	public String getActiveConfName(){
		return activeConfName;
	}
	
	public void setLqiConfigurations(List<LqiGridConfig> lqiConfigurations){
		this.lqiConfigurations = lqiConfigurations;
	}
	
	public List<LqiGridConfig> getLqiConfigurations(){
		return lqiConfigurations;
	}
	
	public void addLqiConfiguration(LqiGridConfig LqiConfiguration ){
		if(lqiConfigurations == null){
			lqiConfigurations = new ArrayList<LqiGridConfig>();
		}
		
		lqiConfigurations.add(LqiConfiguration);
	}
	
	public void clear(){
		
		activeConfName = null;
		lqiConfigurations = null;
	}
	
	@JsonIgnore
	public LqiGridConfig getActiveConf(){
		
		LqiGridConfig activeConf = null;
		
		if(lqiConfigurations != null){
			for(LqiGridConfig conf: lqiConfigurations){
				if(conf.getName().equals(activeConfName)){
					activeConf = conf;
					break;
				}
			}
		}
		return activeConf;
	}
	
	@Override
	public String toString() {
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Active conf name: ");
		strBuilder.append(activeConfName);
		strBuilder.append("\nLQI Configurations: [\n");
		if(lqiConfigurations != null){
			for(LqiGridConfig conf: lqiConfigurations){
				
			strBuilder.append(conf.toString());
			strBuilder.append("\n");
			}
		}
		strBuilder.append("]");
	    return strBuilder.toString();
	}
	
}
