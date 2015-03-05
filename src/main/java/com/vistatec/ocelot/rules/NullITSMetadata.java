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
import java.util.Collections;
import java.util.Map;

import javax.swing.border.Border;

import com.vistatec.ocelot.its.model.ITSMetadata;

/**
 * Dummy ITS Metadata for empty columns.
 */
public class NullITSMetadata extends ITSMetadata { 
    private NullITSMetadata() {
        super();
    }

    private static final NullITSMetadata INSTANCE = new NullITSMetadata();

    public static NullITSMetadata getInstance() {
        return INSTANCE;
    }

    @Override
    public DataCategoryFlag getFlag() {
        return NullDataCategoryFlag.getInstance();
    }

    public static class NullDataCategoryFlag extends DataCategoryFlag {
        static final DataCategoryFlag NULL_FLAG = new NullDataCategoryFlag();

        public static DataCategoryFlag getInstance() {
            return NULL_FLAG;
        }

        @Override
        public Color getFill() {
            return null;
        }

        @Override
        public Border getBorder() {
            return null;
        }

        @Override
        public String getText() {
            return "";
        }

        @Override
        public String toString() {
            return "";
        }
    }

    @Override
    public Map<DataCategoryField, Object> getFieldValues() {
        return Collections.emptyMap();
    }
}