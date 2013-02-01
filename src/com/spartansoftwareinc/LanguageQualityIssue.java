package com.spartansoftwareinc;

import java.awt.Color;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Represents Language Quality Issue Data Category in the ITS 2.0 spec.
 */
public class LanguageQualityIssue implements ITSMetadata, DataCategoryFlag {
    private String type, comment;
    private int severity;
    private URL profileReference;
    private boolean enabled;

    @Override
    public Map<DataCategoryField, Object> getFieldValues() {
    	Map<DataCategoryField, Object> map = 
    			new EnumMap<DataCategoryField, Object>(DataCategoryField.class);
    	map.put(DataCategoryField.LQI_TYPE, type);
    	map.put(DataCategoryField.LQI_COMMENT, comment);
    	map.put(DataCategoryField.LQI_SEVERITY, severity);
    	return map;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public URL getProfileReference() {
        return profileReference;
    }

    public void setProfileReference(URL profileReference) {
        this.profileReference = profileReference;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**************************************************
     * ITSMetadata methods used to render a row in the
     * SegmentAttributeTableView
     **************************************************/
    @Override
    public String getDataCategory() {
        return "LQI";
    }

    @Override
    public String getValue() {
        return getSeverity()+"";
    }

    @Override
    public String getType() {
        return type;
    }

    /*******************************************************************
     * DataCategoryFlag methods used to display flag indicators next to
     * segments in the segment table.
     *******************************************************************/
    @Override
    public Color getFlagBackgroundColor() {
        if (severity < 33) {
            return Color.YELLOW;
        } else if (severity > 66) {
            return Color.RED;
        } else {
            return Color.ORANGE;
        }
    }

    @Override
    public Border getFlagBorder() {
        return BorderFactory.createLineBorder(Color.BLACK);
    }

    @Override
    public String getFlagText() {
        return getType().substring(0,1);
    }

    @Override
    public String toString() {
        return getType();
    }
}
