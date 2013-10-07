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
import javax.swing.border.Border;

/**
 * Display methods for flags in SegmentView Table.
 */
public class DataCategoryFlag {
    private boolean setFill = true, setBorder = true, setText = true;
    private Color fill = Color.gray;
    private Border border;
    private String text = "?";

    public Color getFill() {
        return fill;
    }

    public void setFill(Color f) {
        if (this.setFill) {
            this.fill = f;
            this.setFill = false;
        }
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border b) {
        if (this.setBorder) {
            this.border = b;
            this.setBorder = false;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String t) {
        if (this.setText) {
            this.text = t;
            this.setText = false;
        }
    }
}
