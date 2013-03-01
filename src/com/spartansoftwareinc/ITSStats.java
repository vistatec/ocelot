package com.spartansoftwareinc;

/**
 * Used for displaying document level segment attribute statistics
 */
public interface ITSStats {
    public String getDataCategory();
    public String getType();
    public String getValue();
    public Integer getCount();
    public void setCount(Integer count);
}
