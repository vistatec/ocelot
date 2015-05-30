package com.vistatec.ocelot.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This document let you validate text inserted in any Swing text field. It
  * let user insert only integer value into the text field.
  */
 public class IntegerDocument extends PlainDocument {

     /** serial version uid. */
     private static final long serialVersionUID = -412079755824907144L;

     /**
      * Checks if the string resulting from the insertion of the parameter
      * string at the given offset is an integer. If it is the case, the
      * string is inserted at the right offset; otherwise the current string
      * does not change.
      * 
      * @see javax.swing.text.PlainDocument#insertString(int,
      *      java.lang.String, javax.swing.text.AttributeSet)
      */
     @Override
     public void insertString(int offs, String str, AttributeSet a)
             throws BadLocationException {

         if (str != null) {
             boolean ok = true;
             final String currText = getText(0, getLength());
             final String newText = currText.substring(0, offs) + str
                     + currText.substring(offs, getLength());
             try {
                 Integer.parseInt(newText);
             } catch (NumberFormatException e) {
                 ok = false;
             }
             if (ok) {
                 super.insertString(offs, str, a);
             }
         }
     }

 }