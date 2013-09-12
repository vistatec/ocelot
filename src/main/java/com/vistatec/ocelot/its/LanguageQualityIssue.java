package com.vistatec.ocelot.its;

import com.vistatec.ocelot.rules.DataCategoryField;
import com.vistatec.ocelot.rules.DataCategoryFlag;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import net.sf.okapi.common.annotation.GenericAnnotation;
import net.sf.okapi.common.annotation.GenericAnnotationType;

/**
 * Represents Language Quality Issue Data Category in the ITS 2.0 spec.
 */
public class LanguageQualityIssue extends DataCategoryFlag implements ITSMetadata {
    private String type, comment, issuesRef;
    private double severity;
    private URL profileReference;
    private boolean enabled;

    public LanguageQualityIssue() {}
    
    public LanguageQualityIssue(GenericAnnotation ga) {
        if (ga.getString(GenericAnnotationType.LQI_ISSUESREF) != null) {
            this.issuesRef = ga.getString(GenericAnnotationType.LQI_ISSUESREF);
            if (this.issuesRef.charAt(0) == '#') {
                this.issuesRef = this.issuesRef.substring(1);
            }
        }
        if (ga.getString(GenericAnnotationType.LQI_TYPE) != null) {
            this.type = ga.getString(GenericAnnotationType.LQI_TYPE);
        }
        if (ga.getDouble(GenericAnnotationType.LQI_SEVERITY) != null) {
            this.severity = ga.getDouble(GenericAnnotationType.LQI_SEVERITY);
        }
        if (ga.getString(GenericAnnotationType.LQI_COMMENT) != null) {
            this.comment = ga.getString(GenericAnnotationType.LQI_COMMENT);
        }
        if (ga.getString(GenericAnnotationType.LQI_PROFILEREF) != null) {
            try {
                this.profileReference = new URL(GenericAnnotationType.LQI_PROFILEREF);
            } catch (MalformedURLException ex) {
                // TODO: Handle url exception appropriately
                System.err.println(ex.getMessage());
            }
        }
        if (ga.getBoolean(GenericAnnotationType.LQI_ENABLED) != null) {
            this.enabled = ga.getBoolean(GenericAnnotationType.LQI_ENABLED);
        }
    }

    @Override
    public Map<DataCategoryField, Object> getFieldValues() {
    	Map<DataCategoryField, Object> map = 
    			new EnumMap<DataCategoryField, Object>(DataCategoryField.class);
    	map.put(DataCategoryField.LQI_TYPE, type);
    	map.put(DataCategoryField.LQI_COMMENT, comment);
    	map.put(DataCategoryField.LQI_SEVERITY, severity);
    	return map;
    }

    public String getType() {
        return this.type;
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

    public double getSeverity() {
        return severity;
    }

    public void setSeverity(double severity) {
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

    public String getIssuesRef() {
        return issuesRef;
    }

    public void setIssuesRef(String issuesRef) {
        this.issuesRef = issuesRef;
    }

    @Override
    public String toString() {
        return getType();
    }
}
