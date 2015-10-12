package com.vistatec.ocelot.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.vistatec.ocelot.events.ItsDocStatsRecalculateEvent;
import com.vistatec.ocelot.events.RefreshSegmentView;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.EnrichmentMetaData;
import com.vistatec.ocelot.plugins.exception.FremeEnrichmentException;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;
import com.vistatec.ocelot.xliff.freme.EnrichmentConverter;

/**
 * Class managing calls to the FREME Plugin. It provides a pool of threads
 * invoking FREME services for Ocelot fragments.
 */
public class FremePluginManager {

	/** The logger for this class. */
	private final Logger logger = Logger.getLogger(FremePluginManager.class);

	/** Ideal segments number per call. */
	private static final int SEGNUM_PER_CALL = 20;

	/** Maximum number of threads allowed in the pool. */
	private static final int MAX_THREAD_NUM = 10;

	/** The Ocelot event queue. */
	private OcelotEventQueue eventQueue;

	/** The executor service. */
	private ExecutorService executor;

	/** List of segments currently opened in Ocelot. */
	private List<OcelotSegment> segments;

	/**
	 * Constructor.
	 * 
	 * @param eventQueue
	 *            the event queue.
	 */
	public FremePluginManager(final OcelotEventQueue eventQueue) {

		this.eventQueue = eventQueue;
		executor = Executors.newFixedThreadPool(MAX_THREAD_NUM);
	}

	/**
	 * Sets the segments list.
	 * 
	 * @param segments
	 *            the segments list.
	 */
	public void setSegments(List<OcelotSegment> segments) {
		this.segments = segments;
	}

	/**
	 * Enriches the segments opened in Ocelot by invoking the FREME plugin.
	 * 
	 * @param fremePlugin
	 *            the FREME plugin
	 */
	public void enrich(FremePlugin fremePlugin) {

		if (segments != null) {
			logger.info("Enriching Ocelot segments...");
			List<VariantWrapper> fragments = getFragments(segments);
			Collections.sort(fragments, new FragmentsComparator());
			int threadNum = findThreadNum(fragments.size());
			VariantWrapper[][] fragmentsArrays = new VariantWrapper[threadNum][(int) Math
			        .ceil((double) fragments.size() / threadNum)];
			int j = 0;
			for (int i = 0; i < fragments.size(); i = i + threadNum) {

				for (int arrayIdx = 0; arrayIdx < threadNum; arrayIdx++) {

					if (i + arrayIdx < fragments.size()) {
						fragmentsArrays[arrayIdx][j] = fragments.get(i
						        + arrayIdx);
					}
				}
				j++;
			}
			for (int i = 0; i < fragmentsArrays.length; i++) {
				executor.execute(new FremeEnricher(fragmentsArrays[i],
				        fremePlugin, eventQueue, segments));
			}
			WaitingThread waitingThread = new WaitingThread(executor, segments, eventQueue);
			waitingThread.start();
		}

	}

	/**
	 * Enriches a variant of an existing segment.
	 * 
	 * @param fremePlugin
	 *            the freme plugin
	 * @param variant
	 *            the variant to be enriched
	 * @param segNumber
	 *            the segment number.
	 */
	public void enrich(FremePlugin fremePlugin, BaseSegmentVariant variant,
	        int segNumber, boolean target) {
		if (!variant.isEnriched()) {
			logger.info("Enriching variant for segment " + segNumber + "...");
			VariantWrapper wrapper = new VariantWrapper(variant,
			        variant.getDisplayText(), segNumber, target);
			if (wrapper.getText() != null && !wrapper.getText().isEmpty()) {
				executor.execute(new FremeEnricher(
				        new VariantWrapper[] { wrapper }, fremePlugin,
				        eventQueue, segments));
			}
		}
	}

	/**
	 * Finds the optimal threads number depending on the number of segments to
	 * be enriched.
	 * 
	 * @param segmentsSize
	 *            the number of segments
	 * @return the optimal threads number.
	 */
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

	/**
	 * Gets the list of fragments to be enriched retrieved by the list of
	 * segments.
	 * 
	 * @param segments
	 *            the list of Ocelot segments.
	 * @return the list of fragments
	 */
	private List<VariantWrapper> getFragments(List<OcelotSegment> segments) {

		List<VariantWrapper> fragments = new ArrayList<VariantWrapper>();
		if (segments != null) {
			String text = null;
			for (OcelotSegment segment : segments) {
				if (segment.getSource() != null
				        && !((BaseSegmentVariant) segment.getSource())
				                .isEnriched()) {
					text = segment.getSource().getDisplayText();
					if (text != null && !text.isEmpty()) {
						fragments.add(new VariantWrapper(
						        (BaseSegmentVariant) segment.getSource(), text,
						        segment.getSegmentNumber(), false));
					}
				}
				if (segment.getTarget() != null
				        && !((BaseSegmentVariant) segment.getTarget())
				                .isEnriched()) {
					text = segment.getTarget().getDisplayText();
					if (text != null && !text.isEmpty()) {
						fragments.add(new VariantWrapper(
						        (BaseSegmentVariant) segment.getTarget(), text,
						        segment.getSegmentNumber(), true));
					}
				}
			}
		}
		return fragments;
	}

}

class WaitingThread extends Thread {

	private ExecutorService executor;

	private List<OcelotSegment> segments;

	private OcelotEventQueue eventQueue;

	public WaitingThread(ExecutorService executor,
	        List<OcelotSegment> segments, OcelotEventQueue eventQueue) {
		this.eventQueue = eventQueue;
		this.executor = executor;
		this.segments = segments;
	}

	@Override
	public void run() {

		executor.shutdown();
		try {
	        executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		eventQueue.post(new ItsDocStatsRecalculateEvent(segments));
	}

}

/**
 * Comparator for fragments. A fragment is smaller than another one, if its text
 * is shorter than the other's text.
 */
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

/**
 * Wrapper class for variant objects.
 */
class VariantWrapper {

	/** The variant. */
	private BaseSegmentVariant variant;

	/** The text contained into the variant. */
	private String text;

	/** The owner segment number. */
	private int segNumber;

	private boolean target;

	/**
	 * Constructor.
	 * 
	 * @param variant
	 *            the variant
	 * @param text
	 *            the text
	 * @param segNumber
	 *            the segment number
	 */
	public VariantWrapper(BaseSegmentVariant variant, String text,
	        int segNumber, boolean target) {
		this.variant = variant;
		this.text = text;
		this.segNumber = segNumber;
		this.target = target;
	}

	/**
	 * Gets the variant.
	 * 
	 * @return the variant.
	 */
	public BaseSegmentVariant getVariant() {
		return variant;
	}

	/**
	 * Gets the text.
	 * 
	 * @return the text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Gets the segment number.
	 * 
	 * @return the segment number.
	 */
	public int getSegNumber() {
		return segNumber;
	}

	public boolean isTarget() {
		return target;
	}

}

/**
 * Runnable class performing the enrichment of an array of variants.
 */
class FremeEnricher implements Runnable {

	/** The logger for this class. */
	private final Logger logger = Logger.getLogger(FremeEnricher.class);

	/** The array of variants. */
	private VariantWrapper[] variants;

	/** The FREME plugin. */
	private FremePlugin fremePlugin;

	/** The event queue. */
	private OcelotEventQueue eventQueue;

	private List<OcelotSegment> segments;

	/**
	 * Constructor.
	 * 
	 * @param variants
	 *            the array of variants to be enriched.
	 * @param fremePlugin
	 *            the FREME plugin
	 * @param eventQueue
	 *            the event queue
	 */
	public FremeEnricher(final VariantWrapper[] variants,
	        FremePlugin fremePlugin, OcelotEventQueue eventQueue,
	        List<OcelotSegment> segments) {
		this.variants = variants;
		this.fremePlugin = fremePlugin;
		this.eventQueue = eventQueue;
		this.segments = segments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try {
			logger.debug("Enriching " + variants.length + " variants");
			for (VariantWrapper frag : variants) {
				if (frag != null) {
					List<Enrichment> enrichments = null;
					String sourceTarget = null;
					if (frag.isTarget()) {
						enrichments = fremePlugin.enrichTargetContent(frag
						        .getText());
						sourceTarget = EnrichmentMetaData.TARGET;
					} else {
						enrichments = fremePlugin.enrichSourceContent(frag
						        .getText());
						sourceTarget = EnrichmentMetaData.SOURCE;
					}
					frag.getVariant().setEnrichments(enrichments);
					frag.getVariant().setEnriched(true);
					OcelotSegment segment = findSegmentBySegNumber(frag
					        .getSegNumber());
					if (segment != null) {
						EnrichmentConverter.convertEnrichment2ITSMetaData(
						        segment, frag.getVariant(), sourceTarget);
					}

					eventQueue
					        .post(new RefreshSegmentView(frag.getSegNumber()));
				}
			}

		} catch (FremeEnrichmentException e) {
			logger.error("Error while enriching the variants", e);
		}
	}

	/**
	 * Finds the segment having the specified segment number.
	 * 
	 * @param segNum
	 *            the segment number.
	 * @return the Ocelot segment.
	 */
	private OcelotSegment findSegmentBySegNumber(int segNum) {

		OcelotSegment segment = null;
		Iterator<OcelotSegment> segmIt = segments.iterator();
		OcelotSegment currSegm = null;
		while (segmIt.hasNext() && segment == null) {
			currSegm = segmIt.next();
			if (currSegm.getSegmentNumber() == segNum) {
				segment = currSegm;
			}
		}
		return segment;
	}

}