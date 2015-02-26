/*
 * Copyright (C) 2013, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.rules;

import java.awt.Color;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Display methods for flags in SegmentView Table.
 */
public class DataCategoryFlag {
    private Color fill = Color.gray;
    private Color borderColor = Color.gray;
    private Border border = BorderFactory.createEmptyBorder();
    private String text = "?";

    private static final DataCategoryFlag DEFAULT = new DataCategoryFlag();

    public static DataCategoryFlag getDefault() {
        return DEFAULT;
    }

    public Color getFill() {
        return fill;
    }

    public void setFill(Color f) {
            this.fill = f;
    }

    public Border getBorder() {
        return border;
    }

    /**
     * Set border color instead of border, so that we can centralize
     * border styling and also because Border does not implement equals().
     */
    public void setBorderColor(Color color) {
        this.borderColor = color;
        this.border = BorderFactory.createLineBorder(color);
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
        this.text = t;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof DataCategoryFlag)) return false;
        DataCategoryFlag f = (DataCategoryFlag)o;
        return text.equals(f.text) && fill.equals(f.fill) &&
               borderColor.equals(f.borderColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, fill, border);
    }
}
