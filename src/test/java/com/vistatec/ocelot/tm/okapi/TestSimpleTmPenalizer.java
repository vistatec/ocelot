package com.vistatec.ocelot.tm.okapi;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.vistatec.ocelot.config.json.TmManagement;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.tm.TmManager;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.penalty.SimpleTmPenalizer;

public class TestSimpleTmPenalizer {
    private final Mockery mockery = new Mockery();

    private SimpleTmPenalizer penalizer;
    private final TmManager manager = mockery.mock(TmManager.class);

    @Before
    public void setup() {
        this.penalizer = new SimpleTmPenalizer(manager);
    }

    @Test
    public void testApplyPenalty() {
        final TmManagement.TmConfig config = new TmManagement.TmConfig();
        config.setPenalty(2.0f);
        mockery.checking(new Expectations() {
                {
                    allowing(manager).fetchTm(with(any(String.class)));
                        will(returnValue(config));
                }
        });

        List<TmMatch> testMatches = new ArrayList<>();
        testMatches.add(new TestTmMatch("1", 95.0f, null, null));

        List<TmMatch> penalizedMatches = penalizer.applyPenalties(testMatches);

        assertTrue((93.0f - penalizedMatches.get(0).getMatchScore()) == 0);
    }

    @Test
    public void testApplyMissingConfigNoOpPenalty() {
        mockery.checking(new Expectations() {
                {
                    allowing(manager).fetchTm(with(any(String.class)));
                        will(returnValue(null));
                }
        });
        List<TmMatch> testMatches = new ArrayList<>();
        testMatches.add(new TestTmMatch("1", 95.0f, null, null));

        List<TmMatch> penalizedMatches = penalizer.applyPenalties(testMatches);

        assertTrue((95.0f - penalizedMatches.get(0).getMatchScore()) == 0);
    }

    private class TestTmMatch implements TmMatch {
        private final String origin;
        private final float score;
        private final SegmentVariant source, target;

        public TestTmMatch(String origin, float score, SegmentVariant source, SegmentVariant target) {
            this.origin = origin;
            this.score = score;
            this.source = source;
            this.target = target;
        }

        @Override
        public String getTmOrigin() {
            return origin;
        }

        @Override
        public float getMatchScore() {
            return score;
        }

        @Override
        public SegmentVariant getSource() {
            return source;
        }

        @Override
        public SegmentVariant getTarget() {
            return target;
        }
    }
}
