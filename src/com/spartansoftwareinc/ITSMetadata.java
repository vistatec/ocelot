package com.spartansoftwareinc;

import java.util.Map;

public interface ITSMetadata {
    Map<DataCategoryField, Object> getFieldValues();

    public String getDataCategory();

    public String getType();

    public String getValue();
}
