package com.vistatec.ocelot;

import com.vistatec.ocelot.its.model.ITSMetadata;
import com.vistatec.ocelot.segment.model.SegmentVariant;

public enum SegmentViewColumn {
    SegNum("#", "Segment Number", true, Integer.class, false, -1),
//    Freme("F", "Freme", false, ImageIcon.class, false, -1),
    Source("Source", "Source", true, SegmentVariant.class, false, -1),
    Target("Target", "Target", true, SegmentVariant.class, false, -1),
    Original("Original Target", "Original Target", true, SegmentVariant.class, false, -1),
    Notes("Notes", "Notes", false, String.class, false, -1),
    EditDistance("Δ", "Edit Distance (Δ)", false, Integer.class, false, -1),
    Flag1("", "Flag #1", true, ITSMetadata.class, true, 0),
    Flag2("", "Flag #2", true, ITSMetadata.class, true, 1),
    Flag3("", "Flag #3", true, ITSMetadata.class, true, 2),
    Flag4("", "Flag #4", true, ITSMetadata.class, true, 3),
    Flag5("", "Flag #5", true, ITSMetadata.class, true, 4);

    private String displayName, fullName;
    private boolean visibleByDefault, flagColumn;
    // XXX Hacky, this value is not applicable for non-flag columns
    private int flagIndex;
    private Class<?> datatype;

    SegmentViewColumn(String displayName, String fullName, boolean visibleByDefault,
                      Class<?> datatype, boolean flagColumn, int flagIndex) {
        this.displayName = displayName;
        this.fullName = fullName;
        this.visibleByDefault = visibleByDefault;
        this.flagColumn = flagColumn;
        this.flagIndex = flagIndex;
        this.datatype = datatype;
    }

    public String getName() {
        return displayName;
    }

    public String getFullName() {
        return fullName;
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
