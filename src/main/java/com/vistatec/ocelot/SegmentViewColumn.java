package com.vistatec.ocelot;

import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.segment.SegmentVariant;

public enum SegmentViewColumn {
    SegNum("#", true, Integer.class, false, -1),
    Source("Source", true, SegmentVariant.class, false, -1),
    Target("Target", true, SegmentVariant.class, false, -1),
    Original("Target Original", true, SegmentVariant.class, false, -1),
    Flag1("", true, ITSMetadata.class, true, 0),
    Flag2("", true, ITSMetadata.class, true, 1),
    Flag3("", true, ITSMetadata.class, true, 2),
    Flag4("", true, ITSMetadata.class, true, 3),
    Flag5("", true, ITSMetadata.class, true, 4);

    private String displayName;
    private boolean visibleByDefault, flagColumn;
    // XXX Hacky, this value is not applicable for non-flag columns
    private int flagIndex;
    private Class<?> datatype;

    SegmentViewColumn(String displayName, boolean visibleByDefault, Class<?> datatype, boolean flagColumn, int flagIndex) {
        this.displayName = displayName;
        this.visibleByDefault = visibleByDefault;
        this.flagColumn = flagColumn;
        this.flagIndex = flagIndex;
        this.datatype = datatype;
    }

    public String getName() {
        return displayName;
    }

    public boolean isVisibleByDefaut() {
        return visibleByDefault;
    }

    public Class<?> getDatatype() {
        return datatype;
    }

    public boolean isFlagColumn() {
        return flagColumn;
    }

    public int getFlagIndex() {
        return flagIndex;
    }

    public static int getFlagColumnCount() {
        int count = 0;
        for (SegmentViewColumn col : values()) {
            if (col.isFlagColumn()) {
                count += 1;
            }
        }
        return count;
    }
}
