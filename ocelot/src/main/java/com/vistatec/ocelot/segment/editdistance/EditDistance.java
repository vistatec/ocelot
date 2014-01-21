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

import com.vistatec.ocelot.segment.SegmentTextCell;
import java.util.ArrayList;
import java.util.LinkedList;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import net.sf.okapi.common.resource.TextContainer;

/**
 * Calculate the insertion/deletion difference between the target and target
 * original for segments.
 */
public class EditDistance {
    public static ArrayList<String> styleTextDifferences(TextContainer target, TextContainer original) {
        diff_match_patch dmp = new diff_match_patch();
        ArrayList<String> styledDiff = new ArrayList<String>();

        LinkedList<Diff> diffList = dmp.diff_main(original.getCodedText(), target.getCodedText());
        dmp.diff_cleanupSemantic(diffList);
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

    public static int calcEditDistance(TextContainer target, TextContainer tgtOriginal) {
        diff_match_patch dmp = new diff_match_patch();
        return dmp.diff_levenshtein(dmp.diff_main(tgtOriginal.getCodedText(), target.getCodedText()));
    }
}
