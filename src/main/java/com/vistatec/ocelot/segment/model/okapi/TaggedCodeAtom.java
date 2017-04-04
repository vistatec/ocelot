/*
 * Copyright (C) 2015, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.model.okapi;

import com.vistatec.ocelot.segment.model.CodeAtom;

import net.sf.okapi.lib.xliff2.core.Tag;

/**
 * XLIFF 2.0 CodeAtom, tracks which Okapi Tag the code atom was created from.
 */
public class TaggedCodeAtom extends CodeAtom {
    private Tag tag;

    public TaggedCodeAtom(Tag tag, String data, String verboseData) {
        super(tag.getId(), data, verboseData);
        this.tag = tag;
    }

    public Tag getTag() {
        return this.tag;
    }
}
