package com.spartansoftwareinc.vistatec.rwb.rules;

import java.awt.Color;
import javax.swing.border.Border;

/**
 * Display methods for flags in SegmentView Table.
 */
public class DataCategoryFlag {
    private boolean setFill = true, setBorder = true, setText = true;
    private Color fill = Color.gray;
    private Border border;
    private String text = "?";

    public Color getFill() {
        return fill;
    }

    public void setFill(Color f) {
        if (this.setFill) {
            this.fill = f;
            this.setFill = false;
        }
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border b) {
        if (this.setBorder) {
            this.border = b;
            this.setBorder = false;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
        if (this.setText) {
            this.text = t;
            this.setText = false;
        }
    }
}
