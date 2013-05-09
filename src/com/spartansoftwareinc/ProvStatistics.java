package com.spartansoftwareinc;

/**
 * Aggregate data representation for Provenance displayed in SegmentAttributeTableView.
 */
public class ProvStatistics implements ITSStats {
    private String displayType, provType, value;
    private Double minRange, maxRange;
    private Integer count = 0;

    @Override
    public String getDataCategory() {
        return "Provenance";
    }

    @Override
    public String getType() {
        return displayType;
    }

    public void setType(String type) {
        this.displayType = type;
    }

    /**
     * Combination of "displayType:value" used to identify unique ProvStatistics.
     * Each ITSProvenance generates multiple ProvStatistics records for each
     * data attribute=value key-value pair.
     */
    public String getProvType() {
        return provType;
    }

    public void setProvType(String provType) {
        this.provType = provType;
    }

    @Override
    public String getValue() {
        if (minRange != null && maxRange != null) {
            return minRange + "-" + maxRange;
        } else if (value != null) {
            return value;
        }
        return null;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    @Override
    public void setCount(Integer count) {
        this.count = count;
    }

    public void setValue(String val) {
        this.value = val;
        count++;
    }
}
