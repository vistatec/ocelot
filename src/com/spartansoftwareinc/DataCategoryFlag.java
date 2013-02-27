package com.spartansoftwareinc;

import java.awt.Color;
import javax.swing.border.Border;

/**
 * Display methods for flags in SegmentView Table.
 */
public class DataCategoryFlag {
    private Color fill = Color.gray;
    private Border border;
    private String text = "?";

    public Color getFill() {
        return fill;
    }

    public void setFill(Color f) {
        this.fill = f;
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border b) {
        this.border = b;
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
        this.text = t;
    }
}
