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
package com.vistatec.ocelot.segment.editdistance;

import java.util.ArrayList;
import java.util.LinkedList;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.view.SegmentTextCell;

/**
 * Calculate the insertion/deletion difference between the target and target
 * original for segments.
 */
public class EditDistance {
    public static ArrayList<String> styleTextDifferences(SegmentVariant target, SegmentVariant original) {
        DiffMatchPatch dmp = new DiffMatchPatch();
        ArrayList<String> styledDiff = new ArrayList<String>();

        LinkedList<Diff> diffList = dmp.diffMain(original.getDisplayText(), target.getDisplayText());
        dmp.diffCleanupSemantic(diffList);
        for (Diff diff : diffList) {
            styledDiff.add(diff.text);
            switch (diff.operation) {
                case INSERT:
                    styledDiff.add(SegmentTextCell.insertStyle);
                    break;
                case DELETE:
                    styledDiff.add(SegmentTextCell.deleteStyle);
                    break;
                case EQUAL:
                    styledDiff.add(SegmentTextCell.regularStyle);
                    break;
            }
        }
        return styledDiff;
    }

    public static int calcEditDistance(SegmentVariant target, SegmentVariant tgtOriginal) {
        DiffMatchPatch dmp = new DiffMatchPatch();
        return dmp.diffLevenshtein(dmp.diffMain(tgtOriginal.getDisplayText(), target.getDisplayText()));
    }
}
