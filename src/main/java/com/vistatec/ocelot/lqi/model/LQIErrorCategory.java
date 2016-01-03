package com.vistatec.ocelot.lqi.model;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

public class LQIErrorCategory {

	private String name;
	private LQIShortCut minorShortcut;
	private LQIShortCut seriousShortcut;
	private LQIShortCut criticalShortcut;
	private String comment;
	private float weight;

	public LQIErrorCategory() {
	}

	public LQIErrorCategory(final String name) {

		this(name, 0f, null, null, null);
	}

	public LQIErrorCategory(final String name, final float weight, final LQIShortCut minorShortcut,
	        final LQIShortCut seriousShortcut, final LQIShortCut criticalShortcut) {
		this.name = name;
		this.weight = weight;
		this.minorShortcut = minorShortcut;
		this.criticalShortcut = criticalShortcut;
		this.seriousShortcut = seriousShortcut;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LQIShortCut getMinorShortcut() {
		return minorShortcut;
	}

	public void setMinorShortcut(LQIShortCut minorShortcut) {
		this.minorShortcut = minorShortcut;
	}

	public LQIShortCut getSeriousShortcut() {
		return seriousShortcut;
	}

	public void setSeriousShortcut(LQIShortCut seriousShortcut) {
		this.seriousShortcut = seriousShortcut;
	}

	public LQIShortCut getCriticalShortcut() {
		return criticalShortcut;
	}

	public void setCriticalShortcut(LQIShortCut criticalShortcut) {
		this.criticalShortcut = criticalShortcut;
	}
	
	public void setComment(String comment){
		this.comment = comment;
	}
	
	public String getComment(){
		return comment;
	}
	
	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		return new LQIErrorCategory(name, weight, minorShortcut, seriousShortcut,
		        criticalShortcut);

	}
	
	public static class LQIShortCut {

		private int keyCode;

		private int[] modifiers;

		private String shortCut;
		
		public LQIShortCut(int keyCode, int[] modifiers) {

			setShortCut(keyCode, modifiers);
		}
		
		public LQIShortCut(int keyCode, String modifiersString) {

			this.keyCode = keyCode;
			buildModifiersFromString(modifiersString);
		}
		

		public int getKeyCode() {
			return keyCode;
		}

		public final void setShortCut(int keyCode, int[] modifiers) {
			
			this.keyCode = keyCode;
			this.modifiers = modifiers;
			buildShortCut();

		}
		
		public int[] getModifiers() {
			return modifiers;
		}

		public KeyStroke getKeyStroke() {

			return KeyStroke.getKeyStroke(keyCode, getModifiersSum());
		}

		private int getModifiersSum() {
			int modifiersSum = 0;
			if (modifiers != null) {
				for (int modifier : modifiers) {
					modifiersSum += modifier;
				}
			}
			return modifiersSum;
		}

		private void buildShortCut(){
			
			StringBuilder shortcutBuilder = new StringBuilder();
			shortcutBuilder.append(KeyEvent.getModifiersExText(getModifiersSum()));
			if (shortcutBuilder.length() > 0) {
				shortcutBuilder.append("+");
			}
			shortcutBuilder.append(KeyEvent.getKeyText(keyCode));

			shortCut = shortcutBuilder.toString();
		}
		
		public String getShortCut() {
			
			return shortCut;
			
		}
		
		public String getModifiersString(){
			
			return KeyEvent.getModifiersExText(getModifiersSum());
		}

		private void buildModifiersFromString(String modifiersString){
			
			if(modifiersString != null && !modifiersString.isEmpty()){
				String[] modStringSplit = modifiersString.split("\\+");
				modifiers = new int[modStringSplit.length];
				for(int i = 0; i<modStringSplit.length; i++){
					if(modStringSplit[i].equals(KeyEvent.getModifiersExText(KeyEvent.CTRL_DOWN_MASK))){
						modifiers[i] = KeyEvent.CTRL_DOWN_MASK;
					} else if(modStringSplit[i].equals(KeyEvent.getModifiersExText(KeyEvent.ALT_DOWN_MASK))){
						modifiers[i] = KeyEvent.ALT_DOWN_MASK;
					} else if(modStringSplit[i].equals(KeyEvent.getModifiersExText(KeyEvent.SHIFT_DOWN_MASK))){
						modifiers[i] = KeyEvent.SHIFT_DOWN_MASK;
					}
				}
				shortCut = modifiersString + "+" + KeyEvent.getKeyText(keyCode);
			} else {
				shortCut = KeyEvent.getKeyText(keyCode);
			}
			
		}
		
		@Override
		public String toString() {
		    
		    return shortCut;
		}
		
		@Override
		public boolean equals(Object obj) {
		 
			boolean retValue = false;
			if(obj instanceof LQIShortCut){
				LQIShortCut shotrcutObj = (LQIShortCut)obj;
				retValue = keyCode == shotrcutObj.getKeyCode() && shotrcutObj.getModifiersSum() == getModifiersSum();
			} else {
				retValue = super.equals(obj);
			}
		    return retValue;
		}
		
		@Override
		public int hashCode() {
		 
		    return keyCode + getModifiersSum();
		}
	}

}

