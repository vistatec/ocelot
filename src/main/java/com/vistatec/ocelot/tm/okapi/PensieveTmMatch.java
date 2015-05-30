package com.vistatec.ocelot.tm.okapi;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.okapi.OkapiSegmentVariant;
import com.vistatec.ocelot.tm.TmMatch;

import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;

/**
 * Massage a Pensieve TmHit to the Ocelot TmMatch format.
 */
public class PensieveTmMatch implements TmMatch {
    private final String tmOrigin;
    private final TmHit hit;

    public PensieveTmMatch(String tmOrigin, TmHit hit) {
        this.tmOrigin = tmOrigin;
        this.hit = hit;
    }

    @Override
    public String getTmOrigin() {
        return tmOrigin;
    }

    @Override
    public float getMatchScore() {
        return hit.getScore();
    }

    @Override
    public SegmentVariant getSource() {
        TranslationUnit tu = hit.getTu();
        return new PensieveSegmentVariant(tu.getSource());
    }

    @Override
    public SegmentVariant getTarget() {
        TranslationUnit tu = hit.getTu();
        return new PensieveSegmentVariant(tu.getTarget());
    }

    /**
     * Shares the same parsing semantics as TextContainerVariant, due to both
     * having an Okapi TextFragment as their content.
     */
    public static class PensieveSegmentVariant extends OkapiSegmentVariant {
        private List<SegmentAtom> atoms;

        public PensieveSegmentVariant() {
            this.atoms = new ArrayList<>();
        }

        public PensieveSegmentVariant(TranslationUnitVariant tuv) {
            this.atoms = convertTextFragment(tuv.getContent());
        }

        @Override
        public List<SegmentAtom> getAtoms() {
            return this.atoms;
        }

        @Override
        protected void setAtoms(List<SegmentAtom> atoms) {
            this.atoms = atoms;
        }

        @Override
        public SegmentVariant createEmptyTarget() {
            return new PensieveSegmentVariant();
        }

        @Override
        public SegmentVariant createCopy() {
            PensieveSegmentVariant copy = new PensieveSegmentVariant();
            copy.setAtoms(Lists.newCopyOnWriteArrayList(atoms));
            return copy;
        }

        @Override
        public void setContent(SegmentVariant variant) {
            PensieveSegmentVariant copy = (PensieveSegmentVariant) variant.createCopy();
            this.atoms = copy.getAtoms();
        }

    }
}
