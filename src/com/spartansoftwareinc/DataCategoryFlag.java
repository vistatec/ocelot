package com.spartansoftwareinc;

import java.awt.Color;
import javax.swing.border.Border;

/**
 * Interface for retrieving display properties in SegmentView Table.
 */
public interface DataCategoryFlag {
    public Color getFlagBackgroundColor();
    public Border getFlagBorder();
    public String getFlagText();
}
