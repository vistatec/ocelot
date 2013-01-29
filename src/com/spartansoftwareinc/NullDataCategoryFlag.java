package com.spartansoftwareinc;

import java.awt.Color;
import javax.swing.border.Border;

/**
 * Data Flag for segments that don't have ITS metadata in the specified column
 * in the segment table view.
 */
public class NullDataCategoryFlag implements DataCategoryFlag {

    @Override
    public Color getFlagBackgroundColor() {
        return null;
    }

    @Override
    public Border getFlagBorder() {
        return null;
    }

    @Override
    public String getFlagText() {
        return "";
    }

    @Override
    public String toString() {
        return "";
    }
}
