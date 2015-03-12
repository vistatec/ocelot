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
package com.vistatec.ocelot.plugins.samples.segmentplugin;

import com.vistatec.ocelot.plugins.SegmentPlugin;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import java.util.Date;

/**
 * Sample Segment Plugin.
 */
public class SampleSegmentPlugin implements SegmentPlugin {
    @Override
    public String getPluginName() {
        return "Sample Segment Plugin";
    }

    @Override
    public String getPluginVersion() {
        return "1.0";
    }

    @Override
    public void onSegmentTargetEnter(OcelotSegment seg) {
        System.out.println("TU enter segment #"+seg.getSegmentNumber()+": "+new Date());
    }

    @Override
    public void onSegmentTargetExit(OcelotSegment seg) {
        System.out.println("TU exit segment #"+seg.getSegmentNumber()+": "+new Date());
    }

    @Override
    public void onFileOpen(String filename) {
        System.out.println("Open file '"+filename+"'");
    }

    @Override
    public void onFileSave(String filename) {
        System.out.println("Save file '"+filename+"'");
    }
}
