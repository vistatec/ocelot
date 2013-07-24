package com.spartansoftwareinc.vistatec.rwb.its.stats;

/**
 * Used for displaying document level segment attribute statistics
 */
public interface ITSStats {

    /**
     * Returns the field value for Data Category in the doc stats table.
     */
    public String getDataCategory();

    /**
     * Returns the field value for Type in the doc stats table.
     */
    public String getType();

    /**
     * Returns the field value for Value in the doc stats table.
     */
    public String getValue();

    /**
     * Returns the number of records that match the above fields in the
     * doc stats table.
     */
    public Integer getCount();
    public void setCount(Integer count);
}
