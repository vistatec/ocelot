package com.vistatec.ocelot.rules;

import java.awt.Color;
import javax.swing.border.Border;

/**
 * Data Flag for segments that don't have ITS metadata in the specified column
 * in the segment table view.
 */
public class NullDataCategoryFlag extends DataCategoryFlag {
    
    @Override
    public Color getFill() {
        return null;
    }

    @Override
    public Border getBorder() {
        return null;
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public String toString() {
        return "";
    }
}
