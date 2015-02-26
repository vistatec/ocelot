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

import java.util.Objects;

/**
 * A rule element consisting of a field and an instance of a configured matcher 
 * appropriate for that field.
 */
public class RuleMatcher {

	private DataCategoryField field;
	private DataCategoryField.Matcher matcher;
	
	public RuleMatcher(DataCategoryField field, DataCategoryField.Matcher matcher) {
		this.field = field;
		this.matcher = matcher;
	}
		
	// Returns the field name that this matches against
	public DataCategoryField getField() {
		return field;
	}
	
	public boolean matches(Object o) {
		return matcher.matches(o);
	}

	@Override
	public String toString() {
		return field.toString() + "=>" + matcher;
	}

	@Override
	public boolean equals(Object o) {
	    if (o == this) return true;
        if (o == null || !(o instanceof RuleMatcher)) return false;
        RuleMatcher m = (RuleMatcher)o;
        return field.equals(m.field) &&
               matcher.equals(m.matcher);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(field, matcher);
	}
}
