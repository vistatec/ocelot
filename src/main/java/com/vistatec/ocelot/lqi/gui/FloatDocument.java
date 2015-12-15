package com.vistatec.ocelot.lqi.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class FloatDocument  extends PlainDocument {
	  /**
	 * 
	 */
    private static final long serialVersionUID = -8026068657284310480L;

	public static final String FLOAT = "0123456789.";

	  protected String acceptedChars = null;

	  protected boolean negativeAccepted = false;

	  public FloatDocument() {
	    acceptedChars = FLOAT;
	  }


	  public void setNegativeAccepted(boolean negativeaccepted) {
	    if (acceptedChars.equals(FLOAT)) {
	      negativeAccepted = negativeaccepted;
	      acceptedChars += "-";
	    }
	  }

	  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
	    if (str == null)
	      return;

	    for (int i = 0; i < str.length(); i++) {
	      if (acceptedChars.indexOf(String.valueOf(str.charAt(i))) == -1)
	        return;
	    }

	    if (acceptedChars.equals(FLOAT) || (acceptedChars.equals(FLOAT + "-") && negativeAccepted)) {
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
