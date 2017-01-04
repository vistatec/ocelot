package com.vistatec.ocelot.config.json;

import java.util.ArrayList;
import java.util.List;

public class LqiGridConfig {
	
	private String name;
	
	private double threshold;
	
	private String supplier;
	
	private List<LQICategory> errorCategories;
	
	private List<LQISeverity> severities;
	
	@Override
	public String toString() {

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("name: ");
		strBuilder.append(name);
		strBuilder.append("\nthreshold: ");
		strBuilder.append(threshold);
		strBuilder.append("\nsupplier: ");
		strBuilder.append(supplier);
		strBuilder.append("\nerror categories: [\n");
		if(errorCategories != null){
			for(LQICategory cat: errorCategories){
				strBuilder.append(cat);
				strBuilder.append(",\n");
			}
		}
		strBuilder.append("]");
		strBuilder.append("\nseverities: [\n");
		if(severities != null ){
			for(LQISeverity sev: severities ){
				strBuilder.append(sev.toString());
				strBuilder.append(",\n");
			}
		}
		strBuilder.append("]");
		return strBuilder.toString();
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setSupplier(String supplier){
		this.supplier = supplier;
	}
	
	public String getSupplier(){
		return supplier;
	}
	
	public void setThreshold(double threshold){
		this.threshold = threshold;
	}
	
	public double getThreshold(){
		return threshold;
	}
	
	public void setErrorCategories(List<LQICategory> errorCategories ){
		this.errorCategories = errorCategories;
	}
	
	public List<LQICategory> getErrorCategories(){
		return errorCategories;
	}
	
	public void addErrorCategory(LQICategory errorCategory){
		if(errorCategories == null){
			errorCategories = new ArrayList<LQICategory>();
		}
		errorCategories.add(errorCategory);
	}
	
	public void setSeverities(List<LQISeverity> severities){
		this.severities = severities;
	}
	
	public List<LQISeverity> getSeverities(){
		return severities;
	}
	
	public void addSeverity(LQISeverity severity){
		if(severities == null ) {
			severities = new ArrayList<LqiGridConfig.LQISeverity>();
		}
		severities.add(severity);
	}
	
	
	
	public static class LQICategory {
		
		private String name;
		private int position;
		private double weight;
		private List<Shortcut> shortcuts;
		
		
		@Override
		public String toString() {
			
			StringBuilder strBuilder = new  StringBuilder();
			strBuilder.append("name: ");
			strBuilder.append(name);
			strBuilder.append("\nposition: ");
			strBuilder.append(position);
			strBuilder.append("weight: ");
			strBuilder.append(weight);
			strBuilder.append("\nshortcuts: [\n");
			if(shortcuts != null){
				for(Shortcut sc: shortcuts){
					strBuilder.append(sc.toString());
					strBuilder.append("\n");
				}
			}
			strBuilder.append("]\n");
		    return strBuilder.toString();
		}
		
		public void setName(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
		
		public void setPosition(int position){
			this.position = position;
		}
		
		public int getPosition(){
			return position;
		}

		public void setWeight(double weight){
			this.weight = weight;
		}
		
		public double getWeight(){
			return weight;
		}
		
		public void setShortcuts(List<Shortcut> shortcuts){
			this.shortcuts = shortcuts;
		}
		
		public List<Shortcut> getShortcuts(){
			return shortcuts;
		}
		
		public void addShortuct(Shortcut shortcut){
			if(shortcuts == null){
				shortcuts = new ArrayList<LqiGridConfig.LQICategory.Shortcut>();
			}
			shortcuts.add(shortcut);
		}
		
		public static class Shortcut {
			
			private String severity;
			private int keyCode;
			
			private String modifiers;
			
			public void setSeverity(String severity){
				this.severity = severity;
				
			}
			
			public String getSeverity(){
				return severity;
			}
			
			public void setKeyCode(int keyCode){
				this.keyCode = keyCode;
			}
			
			public int getKeyCode(){
				return keyCode;
			}
			
			public void setModifiers(String modifiers){
				this.modifiers = modifiers;
			}
			
			public String getModifiers(){
				return modifiers;
			}
			
			@Override
			public String toString() {

				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append("severity: ");
				strBuilder.append(severity);
				strBuilder.append("\nkeycode: \n");
				strBuilder.append(keyCode);
				strBuilder.append("\nmodifiers: ");
				strBuilder.append(modifiers);
				return strBuilder.toString();
			}
		}
		
		
	}
	
	public static class LQISeverity {
		
		private String name;
		
		private double score;
		
		public LQISeverity() {
        }
		
		public LQISeverity(String name, double score) {
			
			this.name = name;
			this.score = score;
        }
		
		public void setName(String name ){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
		public void setScore(double score){
			this.score = score;
		}
		
		public double getScore(){
			return score;
		}
		
		@Override
		public String toString() {

			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("name: ");
			strBuilder.append(name);
			strBuilder.append("\nscore: ");
			strBuilder.append(score);
			return strBuilder.toString();
		}
	}

	
}

