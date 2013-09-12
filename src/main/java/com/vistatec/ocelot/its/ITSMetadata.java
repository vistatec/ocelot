package com.vistatec.ocelot.its;

import com.vistatec.ocelot.rules.DataCategoryField;
import java.awt.Color;
import java.util.Map;
import javax.swing.border.Border;

public interface ITSMetadata {
    Map<DataCategoryField, Object> getFieldValues();

    public Color getFill();

    public void setFill(Color f);

    public Border getBorder();

    public void setBorder(Border b);

    public String getText();

    public void setText(String t);
}
