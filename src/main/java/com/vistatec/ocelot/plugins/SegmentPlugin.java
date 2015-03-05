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
package com.vistatec.ocelot.plugins;

import com.vistatec.ocelot.segment.model.OcelotSegment;

/**
 * Segment Plugins are notified when a user enters/exits a target segment edit
 * and when a user opens/saves a file.
 */
public interface SegmentPlugin extends Plugin {

    /**
     * Called when a segment's target becomes editable.
     * @param seg 
     */
    public void onSegmentTargetEnter(OcelotSegment seg);

    /**
     * Called when a segment's target has finished editing.
     * @param seg 
     */
    public void onSegmentTargetExit(OcelotSegment seg);

    /**
     * Called when a file is opened.
     * @param filename 
     */
    public void onFileOpen(String filename);

    /**
     * Called when a file is saved.
     * @param filename 
     */
    public void onFileSave(String filename);
}
