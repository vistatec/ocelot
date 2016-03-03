package com.vistatec.ocelot.config.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class LQIGridConfig {

	private int minor;
	private int serious;
	private int critical;
	private List<LQICategory> lqiCategories;
	
	@XmlAttribute
	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	@XmlAttribute
	public int getSerious() {
		return serious;
	}

	public void setSerious(int serious) {
		this.serious = serious;
	}

	@XmlAttribute
	public int getCritical() {
		return critical;
	}

	public void setCritical(int critical) {
		this.critical = critical;
	}

	@XmlElement
	public List<LQICategory> getLqiCategories() {
		return lqiCategories;
	}

	public void setLqiCategories(List<LQICategory> lqiCategories) {
		this.lqiCategories = lqiCategories;
	}

	public void clear(){
		lqiCategories = null;
		minor = 0;
		serious = 0;
		critical = 0;
	}
	
	public static class LQICategory {
		
		private String name;
		private Shortcut minor;
		private Shortcut serious;
		private Shortcut critical;
		private int position;
		private float weight;
		
		@XmlElement
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		@XmlElement
		public int getPosition(){
			return position;
		}
		
		public void setPosition(int position){
			this.position = position;
		}
		
		@XmlElement
		public Shortcut getMinor() {
			return minor;
		}
		public void setMinor(Shortcut minor) {
			this.minor = minor;
		}
		
		@XmlElement
		public Shortcut getSerious() {
			return serious;
		}
		public void setSerious(Shortcut serious) {
			this.serious = serious;
		}
		
		@XmlElement
		public Shortcut getCritical() {
			return critical;
		}
		public void setCritical(Shortcut critical) {
			this.critical = critical;
		}
		
		@XmlElement
		public float getWeight() {
			 return weight;
		}
		
		public void setWeight(float weight) {
			this.weight = weight;
		}
		
	}
	
	
	public static class Shortcut{
		
		private int keyCode;
		
		private String modifiers;
		
		

		@XmlElement
		public int getKeyCode() {
			return keyCode;
		}

		public void setKeyCode(int keyCode) {
			this.keyCode = keyCode;
		}

		@XmlElement
		public String getModifiers() {
			return modifiers;
		}

		public void setModifiers(String modifiers) {
			this.modifiers = modifiers;
		}
		
	}
	
}
