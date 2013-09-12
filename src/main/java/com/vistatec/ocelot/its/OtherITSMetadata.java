package com.vistatec.ocelot.its;

import com.vistatec.ocelot.rules.DataCategoryField;
import com.vistatec.ocelot.rules.DataCategoryFlag;
import java.util.EnumMap;
import java.util.Map;

/**
 * Represents ITS metadata in the ITS 2.0 spec that are simple key-value pairs.
 * For example, MT confidence.
 */
public class OtherITSMetadata extends DataCategoryFlag implements ITSMetadata {
    private DataCategoryField itsType;
    private Object value;

    public OtherITSMetadata(DataCategoryField itsType, Object value) {
        this.itsType = itsType;
        this.value = value;
    }

    public DataCategoryField getType() {
        return this.itsType;
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    public Map<DataCategoryField, Object> getFieldValues() {
        Map<DataCategoryField, Object> map =
                new EnumMap<DataCategoryField, Object>(DataCategoryField.class);
        map.put(getType(), getValue());
        return map;
    }
}
