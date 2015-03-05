/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.its.stats.model;

/**
 * Used for displaying document level segment attribute statistics
 */
public interface ITSStats {

    /**
     * Returns the key used to identify this statistics category.
     */
    public String getKey();

    /**
     * Add the specified stats object to this object.  The specified
     * object should be of the same type as this object (or at least,
     * it will contain a matching key).
     */
    public void combine(ITSStats stats);

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
