package com.vistatec.ocelot.lqi.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Float document for text fields. It only allows float number being entered in
 * the text field.
 */
public class FloatDocument extends PlainDocument {
	
	/** the serial version UID. */
	private static final long serialVersionUID = -8026068657284310480L;

	/** The string containing allowed characters for a positive float number. */
	public static final String FLOAT = "0123456789.";

	/** The list of allowed chars. */
	protected String acceptedChars = null;

	/** States if a negative number is accepted. */
	protected boolean negativeAccepted = false;

	/**
	 * Constructor.
	 */
	public FloatDocument() {
		acceptedChars = FLOAT;
	}

	/**
	 * Sets the <code>negativeAccepted</code> field value.
	 * @param negativeaccepted the value to set.
	 */
	public void setNegativeAccepted(boolean negativeaccepted) {
		if (acceptedChars.equals(FLOAT)) {
			negativeAccepted = negativeaccepted;
			acceptedChars += "-";
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
	 */
	public void insertString(int offset, String str, AttributeSet attr)
	        throws BadLocationException {
		if (str == null)
			return;

		for (int i = 0; i < str.length(); i++) {
			if (acceptedChars.indexOf(String.valueOf(str.charAt(i))) == -1)
				return;
		}

		if (acceptedChars.equals(FLOAT)
		        || (acceptedChars.equals(FLOAT + "-") && negativeAccepted)) {
			if (str.indexOf(".") != -1) {
				if (getText(0, getLength()).indexOf(".") != -1) {
					return;
				}
			}
		}

		if (negativeAccepted && str.indexOf("-") != -1) {
			if (str.indexOf("-") != 0 || offset != 0) {
				return;
			}
		}

		super.insertString(offset, str, attr);
	}
}
