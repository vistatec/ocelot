package com.vistatec.ocelot.plugins.freme;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.vistatec.ocelot.events.DisplayLeftComponentEvent;
import com.vistatec.ocelot.events.EnrichingStartedStoppedEvent;
import com.vistatec.ocelot.events.EnrichmentViewEvent;
import com.vistatec.ocelot.events.ItsDocStatsRecalculateEvent;
import com.vistatec.ocelot.events.RefreshSegmentView;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.model.EnrichmentMetaData;
import com.vistatec.ocelot.plugins.exception.FremeEnrichmentException;
import com.vistatec.ocelot.plugins.exception.UnknownServiceException;
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
	private final Logger logger = LoggerFactory.getLogger(FremePluginManager.class);

	/** Ideal segments number per call. */
	private static final int SEGNUM_PER_CALL = 20;

	/** Maximum number of threads allowed in the pool. */
	private static final int MAX_THREAD_NUM = 10;

	public static final int OVERRIDE_ENRICHMENTS = 0;

	public static final int MERGE_ENRICHMENTS = 1;

	/** The Ocelot event queue. */
	private OcelotEventQueue eventQueue;

	/** The executor service. */
	private ExecutorService executor;

	/** List of segments currently opened in Ocelot. */
	private List<OcelotSegment> segments;

	/** States if the FREME plugin is enriching. */
	private boolean enriching;

	/** The FREME menu to be displayed in the Ocelot menu bar */
	private JMenu fremeMenu;

	/** The FREME menu item to be displayed in segment view context menu. */
	private JMenuItem fremeMenuItem;
	
	private JMenuItem viewGraphMenuItem;

	/**
	 * Constructor.
	 * 
	 * @param eventQueue
	 *            the event queue.
	 */
	public FremePluginManager(final OcelotEventQueue eventQueue) {

		this.eventQueue = eventQueue;
		createExecutor();
	}

	private void createExecutor() {
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
	public void enrich(FremePlugin fremePlugin, int action) {

		if (segments != null) {
			if (action == OVERRIDE_ENRICHMENTS && existEnrichments()) {
				resetSegments();
			}
			logger.info("Enriching Ocelot segments...");
			List<VariantWrapper> fragments = getFragments(segments);
			Collections.sort(fragments, new FragmentsComparator());
			List<VariantWrapper> fragmentsToDelete = new ArrayList<VariantWrapper>();
			int threadNum = findThreadNum(fragments.size());
			VariantWrapper[][] fragmentsArrays = new VariantWrapper[threadNum][(int) Math
					.ceil((double) fragments.size() / threadNum)];
			int j = 0;
			for (int i = 0; i < fragments.size(); i = i + threadNum) {

				for (int arrayIdx = 0; arrayIdx < threadNum; arrayIdx++) {

					if (i + arrayIdx < fragments.size()) {
						fragmentsArrays[arrayIdx][j] = fragments.get(i
								+ arrayIdx);
						fragmentsToDelete.add(fragments.get(i + arrayIdx));
					}
				}
				j++;
			}
			System.out.println("Fragments size = " + fragments.size());
			int count = 0;
			for (int i = 0; i < fragmentsArrays.length; i++) {
				count += fragmentsArrays[i].length;
			}
			System.out.println("Total fragments for FREME = " + count);
			fragments.removeAll(fragmentsToDelete);
			System.out.println(fragments.size());
			eventQueue.post(new EnrichingStartedStoppedEvent(
					EnrichingStartedStoppedEvent.STARTED));
			addTasksToExecutor(fragmentsArrays, fremePlugin);
			WaitingThread waitingThread = new WaitingThread(executor, segments,
					eventQueue);
			waitingThread.start();
		}

	}

	/**
	 * Resets all segments enrichments.
	 */
	private void resetSegments() {

		logger.debug("Resetting segments before enrichment.");
		for (OcelotSegment segment : segments) {

			if (segment.getSource() instanceof BaseSegmentVariant) {
				resetVariant(segment, (BaseSegmentVariant) segment.getSource(), false);

			}
			if (segment.getTarget() != null
					&& segment.getTarget() instanceof BaseSegmentVariant) {
				resetVariant(segment, (BaseSegmentVariant) segment.getTarget(), true);
			}
		}
		eventQueue.post(new RefreshSegmentView(-1));
		eventQueue.post(new ItsDocStatsRecalculateEvent(segments));
	}

	private void resetVariant(OcelotSegment segment, BaseSegmentVariant variant, boolean target) {

		EnrichmentConverter.removeEnrichmentMetaData(segment, variant, target);
		variant.clearEnrichments();
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
			int segNumber, boolean target, int action) {

		if (action == OVERRIDE_ENRICHMENTS) {
			resetVariant(getSegmentBySegNum(segNumber), variant, target);
		} else {
			variant.setEnriched(false);
		}
		eventQueue.post(new RefreshSegmentView(segNumber));
		logger.info("Enriching variant for segment {}...", segNumber);
		VariantWrapper wrapper = new VariantWrapper(variant,
				variant.getDisplayText(), segNumber, target);
		if (wrapper.getText() != null && !wrapper.getText().isEmpty()) {
			eventQueue.post(new EnrichingStartedStoppedEvent(
					EnrichingStartedStoppedEvent.STARTED));
			addTasksToExecutor(new VariantWrapper[][] { { wrapper } },
					fremePlugin);
			WaitingThread waitingThread = new WaitingThread(executor, segments,
					eventQueue);
			waitingThread.start();
		}
	}

	private OcelotSegment getSegmentBySegNum(int segNumber) {

		OcelotSegment segment = null;
		int i = 0;
		while (i < segments.size() && segment == null) {
			if (segments.get(i).getSegmentNumber() == segNumber) {
				segment = segments.get(i);
			}
			i++;
		}
		return segment;
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
				/*
				 * && !((BaseSegmentVariant) segment.getSource()) .isEnriched()
				 */) {
					text = segment.getSource().getDisplayText();
					if (text != null && !text.isEmpty()) {
						fragments.add(new VariantWrapper(
								(BaseSegmentVariant) segment.getSource(), text,
								segment.getSegmentNumber(), false));
					}
				}
				if (segment.getTarget() != null
				/*
				 * && !((BaseSegmentVariant) segment.getTarget()) .isEnriched()
				 */) {
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

	ExecutorService getexecutor() {
		return executor;
	}

	List<OcelotSegment> getSegments() {
		return segments;
	}

	OcelotEventQueue getEventQueue() {
		return eventQueue;
	}

	public boolean existEnrichments() {

		boolean exist = false;
		if(segments != null ){
			Iterator<OcelotSegment> segIterator = segments.iterator();
			OcelotSegment segment = null;
			BaseSegmentVariant source = null;
			BaseSegmentVariant target = null;
			while (segIterator.hasNext() && !exist) {
				segment = segIterator.next();
				if (segment.getSource() instanceof BaseSegmentVariant) {
					source = (BaseSegmentVariant) segment.getSource();
				}
				if (segment.getTarget() != null
				        && segment.getTarget() instanceof BaseSegmentVariant) {
					target = (BaseSegmentVariant) segment.getTarget();
				}
				exist = (source != null && source.isEnriched())
				        || (target != null && target.isEnriched());
			}
		}
		return exist;
	}

	public void setEnriching(boolean enriching) {
		this.enriching = enriching;
	}

	public boolean isEnriching() {
		return enriching;
	}

	private synchronized void addTasksToExecutor(
			VariantWrapper[][] fragmentsArrays, FremePlugin fremePlugin) {
		if (executor.isShutdown()) {
			createExecutor();
		}
		for (int i = 0; i < fragmentsArrays.length; i++) {
			executor.execute(new FremeEnricher(fragmentsArrays[i], fremePlugin,
					eventQueue, segments));
		}
	}

	public JMenu getFremeMenu(final FremePlugin fremePlugin) {

		if (fremeMenu == null) {
			ActionListener listener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (fremePlugin != null) {
						FremeMenuItem menuItem = (FremeMenuItem) e.getSource();
						if (menuItem.getMenuType() == FremeMenu.CONFIG_MENU) {
							Window containerWindow = SwingUtilities
									.getWindowAncestor(fremeMenu);
							fremePlugin.configureServiceChain(containerWindow);
						} else if (menuItem.getMenuType() == FremeMenu.FILTER_MENU) {
							eventQueue.post(new DisplayLeftComponentEvent(
									fremePlugin.getCategoryFilterPanel()));
						} else if (menuItem.getMenuType() == FremeMenu.ENRICH_MENU) {
							if (existEnrichments()) {
								Window containerWindow = SwingUtilities
										.getWindowAncestor(fremeMenu);
								int option = FremeEnrichmentOptions
										.showConfirmDialog(containerWindow);
								if (option == FremeEnrichmentOptions.DELETE_OPTION) {
									enrich(fremePlugin,
											FremePluginManager.OVERRIDE_ENRICHMENTS);
								} else if (option == FremeEnrichmentOptions.MERGE_OPTION) {
									enrich(fremePlugin,
											FremePluginManager.MERGE_ENRICHMENTS);
								}
							} else {
								enrich(fremePlugin,
										FremePluginManager.OVERRIDE_ENRICHMENTS);
							}
						}
					}
				}
			};

			ItemListener itemListener = new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					FremeEServiceMenuItem menuItem = (FremeEServiceMenuItem) e
							.getItemSelectable();
					try {
						if (fremePlugin != null) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								fremePlugin.turnOnService(menuItem
										.getServiceType());
							} else {
								fremePlugin.turnOffService(menuItem
										.getServiceType());
							}
						} 
					} catch (UnknownServiceException exc) {
						logger.trace(
								"Error while turning on/off the service with type: "
										+ menuItem.getServiceType(), exc);
						JOptionPane
								.showMessageDialog(
										null,
										"An error has occurred while turning on/off the service.",
										"Freme e-Service",
										JOptionPane.ERROR_MESSAGE);
					}

				}
			};
			fremeMenu = new FremeMenu(itemListener, listener);
		}
		// boolean enableMenu = false;
		// for (Entry<FremePlugin, Boolean> fremePlugin : fremePlugins
		// .entrySet()) {
		// if (fremePlugin.getValue()) {
		// enableMenu = true;
		// break;
		// }
		// }
		// fremeMenu.setEnabled(enableMenu);
		return fremeMenu;
	}

	public void setFremeMenuEnabled(boolean enabled) {
		if (fremeMenu != null) {
			fremeMenu.setEnabled(enabled);
		}
	}

	public synchronized List<JMenuItem> getSegmentContextMenuItems(
			final FremePlugin fremePlugin, final OcelotSegment segment,
			final BaseSegmentVariant variant, final boolean target) {

		List<JMenuItem> items = new ArrayList<JMenuItem>();
		viewGraphMenuItem = new JMenuItem("View Enrichments Graph");
		fremeMenuItem = new JMenuItem("Enrich");
		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource().equals(fremeMenuItem)){
					if (existEnrichments()) {
						Window containerWindow = SwingUtilities
								.getWindowAncestor(fremeMenuItem);
						int option = FremeEnrichmentOptions
								.showConfirmDialog(containerWindow);
						if (option == FremeEnrichmentOptions.DELETE_OPTION) {
							enrich(fremePlugin, variant,
									segment.getSegmentNumber(), target,
									FremePluginManager.OVERRIDE_ENRICHMENTS);
						} else if (option == FremeEnrichmentOptions.MERGE_OPTION) {
							enrich(fremePlugin, variant,
									segment.getSegmentNumber(), target,
									FremePluginManager.MERGE_ENRICHMENTS);
						}
					} else {
						enrich(fremePlugin, variant, segment.getSegmentNumber(),
								target, FremePluginManager.OVERRIDE_ENRICHMENTS);
					}
				} else if (e.getSource().equals(viewGraphMenuItem)) {
					eventQueue.post(new EnrichmentViewEvent(variant,
					        segment.getSegmentNumber(),
					        EnrichmentViewEvent.GRAPH_VIEW,
					        variant.equals(segment.getTarget())));
				}
			}
		};
		viewGraphMenuItem.addActionListener(listener);
		viewGraphMenuItem.setEnabled(!enriching && enableGraphMenu(variant));
		items.add(viewGraphMenuItem);

		fremeMenuItem.addActionListener(listener);
		if (enriching) {
			fremeMenuItem.setEnabled(false);
		}
		items.add(fremeMenuItem);
		
		return items;
	}
	
	private boolean enableGraphMenu(BaseSegmentVariant segVariant) {

		boolean enable = false;
		if (segVariant.getEnirchments() != null) {
			Iterator<Enrichment> enrichIt = segVariant.getEnirchments()
			        .iterator();
			while (enrichIt.hasNext() && !enable) {
				enable = enrichIt.next().getType()
				        .equals(Enrichment.ENTITY_TYPE);
			}
		}
		return enable;
	}

	public synchronized void setContextMenuItemEnabled(boolean enabled) {

		if (fremeMenuItem != null) {
			fremeMenuItem.setEnabled(enabled);
		}
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

		synchronized (executor) {
			executor.shutdown();
		}
		try {
			executor.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		eventQueue.post(new ItsDocStatsRecalculateEvent(segments));
		eventQueue.post(new EnrichingStartedStoppedEvent(
				EnrichingStartedStoppedEvent.STOPPED));

		System.out.println("-----------------------------------");
		System.out.println("---------ENRICHED SEGMENTS---------");
		System.out.println("-----------------------------------");
		int count = 0;
		for (OcelotSegment segment : segments) {
			if (((BaseSegmentVariant) segment.getSource()).isEnriched()
					&& ((BaseSegmentVariant) segment.getTarget()).isEnriched()) {
				System.out.println(segment.getSegmentNumber());
				count++;
			}
		}

		System.out.println("TOTAL: " + count);
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
	private final Logger logger = LoggerFactory.getLogger(FremeEnricher.class);

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

		logger.debug("Enriching {} variants", variants.length);
		for (VariantWrapper frag : variants) {
			if (frag != null) {
				try {
					List<Enrichment> enrichments = null;
					String sourceTarget = null;
					frag.getVariant().setSentToFreme(true);
					if (frag.isTarget()) {
						Model model = fremePlugin.enrichTargetContent(frag
								.getText());
						frag.getVariant().setTripleModel(model);
						enrichments = fremePlugin.getEnrichmentFromModel(model, true);
						sourceTarget = EnrichmentMetaData.TARGET;
					} else {
						Model model = fremePlugin.enrichSourceContent(frag
								.getText());
						frag.getVariant().setTripleModel(model);
						enrichments = fremePlugin.getEnrichmentFromModel(model, false);
						sourceTarget = EnrichmentMetaData.SOURCE;
					}
					frag.getVariant().setEnrichments(
							new HashSet<Enrichment>(enrichments));
					frag.getVariant().setEnriched(true);
					OcelotSegment segment = findSegmentBySegNumber(frag
							.getSegNumber());
					if (segment != null) {
						EnrichmentConverter.convertEnrichment2ITSMetaData(
								segment, frag.getVariant(), sourceTarget);
					}

				} catch (FremeEnrichmentException e) {
					logger.error("Error while enriching the variant "
							+ frag.getVariant().getDisplayText(), e);
				} finally {
					eventQueue
					.post(new RefreshSegmentView(frag.getSegNumber()));
				}
			}
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