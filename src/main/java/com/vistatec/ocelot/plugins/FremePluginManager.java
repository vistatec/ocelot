package com.vistatec.ocelot.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vistatec.ocelot.events.RefreshSegmentView;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.plugins.exception.FremeEnrichmentException;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.Enrichment;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.okapi.FragmentVariant;

public class FremePluginManager {

	private static final int SEGNUM_PER_CALL = 20;

	private static final int MAX_THREAD_NUM = 10;

	private OcelotEventQueue eventQueue;

	private ExecutorService executor;

	private FremePlugin fremePlugin;

	// private List<OcelotSegment> segments;

	public FremePluginManager(final FremePlugin fremePlugin,
			final OcelotEventQueue eventQueue) {

		this.eventQueue = eventQueue;
		this.fremePlugin = fremePlugin;
		executor = Executors.newFixedThreadPool(MAX_THREAD_NUM);
	}

	public void enrich(List<OcelotSegment> segments) {

		List<VariantWrapper> fragments = getFragments(segments);
		Collections.sort(fragments, new FragmentsComparator());
		int threadNum = findThreadNum(fragments.size());
		VariantWrapper[][] fragmentsArrays = new VariantWrapper[threadNum][(int) Math
				.ceil((double) fragments.size() / threadNum)];
		int j = 0;
		for (int i = 0; i < fragments.size(); i = i + threadNum) {

			for (int arrayIdx = 0; arrayIdx < threadNum; arrayIdx++) {

				if (i + arrayIdx < fragments.size()) {
					fragmentsArrays[arrayIdx][j] = fragments.get(i + arrayIdx);
				}
			}
			j++;
		}
		for (int i = 0; i < fragmentsArrays.length; i++) {
			executor.execute(new FremeEnricher(fragmentsArrays[i], fremePlugin, eventQueue));
		}

	}

	private int findThreadNum(int segmentsSize) {
		int threadNum = 2;
		boolean found = false;
		while (!found) {
			if (segmentsSize / threadNum <= SEGNUM_PER_CALL) {
				found = true;
			} else {
				threadNum++;
			}
		}
		return threadNum;
	}

	private List<VariantWrapper> getFragments(
			List<OcelotSegment> segments) {

		List<VariantWrapper> fragments = new ArrayList<VariantWrapper>();
		if (segments != null) {
			String text = null;
			for (OcelotSegment segment : segments) {
				if (segment.getSource() != null) {
					text = segment.getSource().getDisplayText();
					if (text != null && !text.isEmpty()) {
						fragments.add(new VariantWrapper(
								(BaseSegmentVariant)segment.getSource(), text,
								segment.getSegmentNumber()));
					}
				}
				if (segment.getTarget() != null) {
					text = segment.getTarget().getDisplayText();
					if (text != null && !text.isEmpty()) {
						fragments.add(new VariantWrapper(
								(BaseSegmentVariant) segment.getTarget(), text,
								segment.getSegmentNumber()));
					}
				}
			}
		}
		return fragments;
	}

}

class FragmentsComparator implements Comparator<VariantWrapper> {

	@Override
	public int compare(VariantWrapper o1, VariantWrapper o2) {

		int retValue = 0;
		if (o1.getText().length() > o2.getText().length()) {
			retValue = 1;
		} else if (o1.getText().length() < o2.getText().length()) {
			retValue = -1;
		}
		return retValue;
	}

}

class VariantWrapper {

	private BaseSegmentVariant variant;

	private String text;

	private int segNumber;

	public VariantWrapper(BaseSegmentVariant variant, String text,
			int segNumber) {
		this.variant = variant;
		this.text = text;
		this.segNumber = segNumber;
	}

	public BaseSegmentVariant getVariant() {
		return variant;
	}

	public String getText() {
		return text;
	}

	public int getSegNumber() {
		return segNumber;
	}

}

class FremeEnricher implements Runnable {

	private VariantWrapper[] fragments;

	private FremePlugin fremePlugin;
	
	private OcelotEventQueue eventQueue;

	public FremeEnricher(final VariantWrapper[] fragments,
			FremePlugin fremePlugin, OcelotEventQueue eventQueue) {
		this.fragments = fragments;
		this.fremePlugin = fremePlugin;
		this.eventQueue = eventQueue;
	}

	@Override
	public void run() {

		try {
			System.out.println("Enriching " + fragments.length + " fragments");
			for (VariantWrapper frag : fragments) {
				if (frag != null) {
					List<Enrichment> enrichments = fremePlugin
							.enrichContent(frag.getText());
					frag.getVariant().setEnrichments(enrichments);
					frag.getVariant().setEnriched(true);
					eventQueue.post(new RefreshSegmentView(frag.getSegNumber()));
					// FremeResponse response = chain.enrichContent(str);
					// response.getLinkStatements(response.getEntityStatements());
					// long end = System.currentTimeMillis();
					// total += (end - start);
				}
			}
		} catch (FremeEnrichmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}