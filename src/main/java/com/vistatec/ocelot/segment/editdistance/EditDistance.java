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
