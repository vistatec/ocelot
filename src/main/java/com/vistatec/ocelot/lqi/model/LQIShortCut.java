package com.vistatec.ocelot.lqi.model;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/**
 * The LQI shortcut.
 */
public class LQIShortCut {

	/** The related severity. */
	private LQISeverity severity;

	/** The key code. */
	private int keyCode;

	/** The modifiers. */
	private int[] modifiers;

	/** The shortcut. */
	private String shortCut;

	/**
	 * Constructor.
	 * 
	 * @param severity
	 *            the severity
	 * @param keyCode
	 *            the key code
	 * @param modifiers
	 *            the modifiers.
	 */
	public LQIShortCut(LQISeverity severity, int keyCode, int[] modifiers) {

		this.severity = severity;
		setShortCut(keyCode, modifiers);
	}

	/**
	 * Constructor.
	 * 
	 * @param severity
	 *            the severity
	 * @param keyCode
	 *            the key code
	 * @param modifiersString
	 *            the modifier string
	 */
	public LQIShortCut(LQISeverity severity, int keyCode, String modifiersString) {

		this.severity = severity;
		this.keyCode = keyCode;
		buildModifiersFromString(modifiersString);
	}

	/**
	 * Gets the key code.
	 * 
	 * @return the key code.
	 */
	public int getKeyCode() {
		return keyCode;
	}

	/**
	 * Sets the shortcut by setting the key code and the modifiers.
	 * 
	 * @param keyCode
	 *            the key code.
	 * @param modifiers
	 *            the modifiers.
	 */
	public final void setShortCut(int keyCode, int[] modifiers) {

		this.keyCode = keyCode;
		this.modifiers = modifiers;
		buildShortCut();

	}

	/**
	 * Gets the modifiers.
	 * 
	 * @return the modifiers.
	 */
	public int[] getModifiers() {
		return modifiers;
	}

	/**
	 * Gets the key stroke.
	 * 
	 * @return the key stroke.
	 */
	public KeyStroke getKeyStroke() {

		return KeyStroke.getKeyStroke(keyCode, getModifiersSum());
	}

	public void setSeverity(LQISeverity severity) {
		this.severity = severity;
	}

	public LQISeverity getSeverity() {
		return severity;
	}

	public LQIShortCut clone(LQISeverity severity) {

		return new LQIShortCut(severity, keyCode, modifiers);
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

	private void buildShortCut() {

		StringBuilder shortcutBuilder = new StringBuilder();
		shortcutBuilder.append(KeyEvent.getModifiersExText(getModifiersSum()));
		if (shortcutBuilder.length() > 0) {
			shortcutBuilder.append("+");
		}
		shortcutBuilder.append(KeyEvent.getKeyText(keyCode));

		shortCut = shortcutBuilder.toString();
	}

	/**
	 * Gets the shortcut string
	 * 
	 * @return the sshortcut string.
	 */
	public String getShortCut() {

		return shortCut;

	}

	/**
	 * Gets the modifiers string.
	 * 
	 * @return the modifiers string.
	 */
	public String getModifiersString() {

		return KeyEvent.getModifiersExText(getModifiersSum());
	}

	/**
	 * Builds the modifiers from the modifiers string.
	 * 
	 * @param modifiersString
	 *            the modifiers string.
	 */
	private void buildModifiersFromString(String modifiersString) {

		if (modifiersString != null && !modifiersString.isEmpty()) {
			String[] modStringSplit = modifiersString.split("\\+");
			modifiers = new int[modStringSplit.length];
			for (int i = 0; i < modStringSplit.length; i++) {
				if (modStringSplit[i].equals(KeyEvent
				        .getModifiersExText(KeyEvent.CTRL_DOWN_MASK))) {
					modifiers[i] = KeyEvent.CTRL_DOWN_MASK;
				} else if (modStringSplit[i].equals(KeyEvent
				        .getModifiersExText(KeyEvent.ALT_DOWN_MASK))) {
					modifiers[i] = KeyEvent.ALT_DOWN_MASK;
				} else if (modStringSplit[i].equals(KeyEvent
				        .getModifiersExText(KeyEvent.SHIFT_DOWN_MASK))) {
					modifiers[i] = KeyEvent.SHIFT_DOWN_MASK;
				}
			}
			shortCut = modifiersString + "+" + KeyEvent.getKeyText(keyCode);
		} else {
			shortCut = KeyEvent.getKeyText(keyCode);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return shortCut;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		boolean retValue = false;
		if (obj instanceof LQIShortCut) {
			LQIShortCut shotrcutObj = (LQIShortCut) obj;
			retValue = keyCode == shotrcutObj.getKeyCode()
			        && shotrcutObj.getModifiersSum() == getModifiersSum();
		} else {
			retValue = super.equals(obj);
		}
		return retValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return keyCode + getModifiersSum();
	}
}