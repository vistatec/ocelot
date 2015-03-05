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

import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.model.Provenance;
import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.util.List;

/**
 * ITS Plugins are sent all of the ITS metadata attached to segments in an open
 * file on export.
 */
public interface ITSPlugin extends Plugin {
    /**
     * Send this plugin the ITS Language Quality Issue data for a segment from
     * the workbench.
     */
    public void sendLQIData(String sourceLang, String targetLang,
            OcelotSegment seg, List<LanguageQualityIssue> lqi);

    /**
     * Send this plugin the ITS Provenance data for a segment from the
     * workbench.
     */
    public void sendProvData(String sourceLang, String targetLang,
            OcelotSegment seg, List<Provenance> prov);
}
