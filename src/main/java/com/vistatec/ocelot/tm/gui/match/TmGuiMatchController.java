package com.vistatec.ocelot.tm.gui.match;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextFragment;
import net.sf.okapi.tm.pensieve.common.TmHit;
import net.sf.okapi.tm.pensieve.common.TranslationUnit;
import net.sf.okapi.tm.pensieve.common.TranslationUnitVariant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.SegmentTargetUpdateFromMatchEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.enrichment.TranslationEnrichment;
import com.vistatec.ocelot.segment.model.okapi.TextContainerVariant;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.TmService;
import com.vistatec.ocelot.tm.okapi.PensieveTmMatch;
import com.vistatec.ocelot.xliff.XLIFFDocument;

/**
 * This class stands over all graphic processes pertaining the TM match
 * functionalities: translations matches and concordance search. It manages two
 * detachable panels: the <code>TranslationsPanel</code>.
 */
public class TmGuiMatchController implements OcelotEventQueueListener {
    private static final Logger LOG = LoggerFactory.getLogger(TmGuiMatchController.class);

	/**
	 * The TM service providing methods for translations match and concordance
	 * search.
	 */
	private TmService tmService;

	/** The Ocelot event queue. */
	private OcelotEventQueue eventQueue;

	/** The current selected segment in the Ocelot main grid. */
	private OcelotSegment currSelectedSegment;

	/** The concordance search panel. */
	private ConcordanceSearchPanel concordancePanel;

	/** The translations panel. */
	private TranslationsPanel translationsPanel;

	/** The tabbed pane containing translations and concordance search panel. */
	private JTabbedPane tmPanel;

	/** The component containing the TM tabbed panel in Ocelot main frame. */
	private Container tmPanelContainer;

	/** Current XLIFF document. **/
	private XLIFFDocument xliff;

	/**
	 * Constructor.
	 * 
	 * @param tmService
	 *            the TM service
	 * @param eventQueue
	 *            the Ocelot event queue.
	 */
	public TmGuiMatchController(final TmService tmService,
	        final OcelotEventQueue eventQueue) {

		this.tmService = tmService;
		this.eventQueue = eventQueue;
		eventQueue.registerListener(this);
	}

	@Subscribe
	public void openFile(OpenFileEvent e) {
	    this.xliff = e.getDocument();
	}

	/**
	 * Gets translations matches for the selected segment.
	 * 
	 * @param currentSelection
	 *            the list of segment atoms for the selected segment.
	 * @return the list of translation matches.
	 */
	public List<TmMatch> getFuzzyMatches(List<SegmentAtom> currentSelection) {
		List<TmMatch> matches = null;
		try {
			matches = tmService.getFuzzyTermMatches(currentSelection);
			Collections.shuffle(matches);
		} catch (IOException e) {
			LOG.trace("Error while retrieving fuzzy matches.", e);
		}
		if (matches != null) {
			Collections.sort(matches, new TmMatchComparator());
		}
		return matches;
	}

	/**
	 * Gets the results of the Concordance Search.
	 * 
	 * @param currentSelection
	 *            list of segment atoms containg the searched string.
	 * @return the segments matching the concordance string.
	 */
	public List<TmMatch> getConcordanceMatches(
	        List<SegmentAtom> currentSelection) {

		List<TmMatch> matches = null;
		try {
			matches = tmService.getConcordanceMatches(currentSelection);
		} catch (IOException e) {
			LOG.trace("Error while retrieving concordance search matches.", e);
			JOptionPane
			        .showMessageDialog(
			                concordancePanel.getAttachedComponent(),
			                "An error has occured while finding concordance search matches.",
			                "Concordance Search Error",
			                JOptionPane.ERROR_MESSAGE);
		}
		return matches;
	}

	/**
	 * Performs the concordance search for the string passed as parameter.
	 * 
	 * @param text
	 *            the text to be searched.
	 */
	public void performConcordanceSearch(final String text) {

		concordancePanel.setTextAndPerformConcordanceSearch(text);
	}

	/**
	 * Gets the concordance panel.
	 * 
	 * @return the concordance panel.
	 */
	public Component getConcordancePanel() {
		if (concordancePanel == null) {
			concordancePanel = new ConcordanceSearchPanel(this);
		}
		return concordancePanel.getAttachedComponent();
	}

	/**
	 * Replaces the whole content of the target in the selected segment in
	 * Ocelot main grid with the new target passed as parameter.
	 * 
	 * @param newTarget
	 *            the new target.
	 */
	public void replaceTarget(final SegmentVariant newTarget) {

		if (currSelectedSegment != null) {
			SegmentVariant textContainerVariant = new TextContainerVariant(
			        new TextContainer(newTarget.getDisplayText()));
			eventQueue.post(new SegmentTargetUpdateFromMatchEvent(xliff,
			        currSelectedSegment, textContainerVariant));
		}
	}

	/**
	 * Invoked when a segment is selected in the Ocelot main grid. It stores the
	 * selected segment in a class field and requests the translations matches
	 * for it.
	 * 
	 * @param selectedSegment
	 *            the selected segment in the Ocelot main grid.
	 */
	public void setSelectedSegment(OcelotSegment selectedSegment) {
		if (!selectedSegment.equals(currSelectedSegment)) {
			this.currSelectedSegment = selectedSegment;
			update();
		}
	}

	/**
	 * Updates the translation for a specific segment number.
	 * 
	 * @param segmentNumber
	 *            the segment number
	 */
	public void update(int segmentNumber) {

		if (currSelectedSegment != null
				&& currSelectedSegment.getSegmentNumber() == segmentNumber) {
			update();
		}
	}
	
	/**
	 * Updates the translations in the panel.
	 */
	private void update() {
		if (translationsPanel != null ) {
				selectTranslationsTab();
				translationsPanel.setLoading();
				List<TmMatch> translations = getFuzzyMatches(currSelectedSegment
				        .getSource().getAtoms());
			if (currSelectedSegment.getSource() instanceof BaseSegmentVariant) {
				TranslationEnrichment transEnrich = ((BaseSegmentVariant) currSelectedSegment
						.getSource()).getTranslationEnrichment();
				if (transEnrich != null) {
					TmHit hit = new TmHit();
					TranslationUnit tu = new TranslationUnit();
					TextFragment fragment = new TextFragment(
							transEnrich.getTranslation());
					TranslationUnitVariant tuVariant = new TranslationUnitVariant(
							new LocaleId(transEnrich.getLanguage()), fragment);
					tu.setTarget(tuVariant);
					tuVariant = new TranslationUnitVariant(null,
							new TextFragment(currSelectedSegment.getSource()
									.getDisplayText()));
					tu.setSource(tuVariant);
					hit.setTu(tu);
					hit.setScore(100f);
					TmMatch fremeMatch = new PensieveTmMatch(
							"FREME e-Translation", hit);

					if (translations == null) {
						translations = new ArrayList<TmMatch>();
			}
					translations.add(0, fremeMatch);
		}

	}
			translationsPanel.setTranslationSearchResults(currSelectedSegment, translations);
		}
	}

	/**
	 * Gets the tabbed pane containing the translations panel and the
	 * concordance search panel.
	 * 
	 * @return the TM tabbed pane
	 */
	public Component getTmPanel() {

		if (tmPanel == null) {
			tmPanel = new JTabbedPane();

		}
		if (translationsPanel == null) {
			translationsPanel = new TranslationsPanel(this);
		}
		tmPanel.add(translationsPanel.getAttachedComponent());
		if (concordancePanel == null) {
			concordancePanel = new ConcordanceSearchPanel(this);
		}
		tmPanel.add(concordancePanel.getAttachedComponent());
		tmPanel.addContainerListener(new ContainerListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ContainerListener#componentRemoved(java.awt.event
			 * .ContainerEvent)
			 */
			@Override
			public void componentRemoved(ContainerEvent e) {
				if (tmPanel.getTabCount() == 0) {
					System.out.println("0 tabs");
					tmPanelContainer = tmPanel.getParent();
					tmPanelContainer.remove(tmPanel);
					tmPanelContainer.repaint();
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ContainerListener#componentAdded(java.awt.event
			 * .ContainerEvent)
			 */
			@Override
			public void componentAdded(ContainerEvent e) {
				if (tmPanel.getTabCount() == 1) {
					tmPanelContainer.add(tmPanel);
					tmPanelContainer.repaint();
					tmPanelContainer = null;
				}
			}
		});
		return tmPanel;
	}

	/**
	 * Gets the tabbed panel.
	 * 
	 * @return the tabbed panel
	 */
	public Component getTabbedContainer() {
		return tmPanel;
	}

	/**
	 * Selects the concordance search tab.
	 */
	public void selectConcordanceTab() {
		try {
			tmPanel.setSelectedComponent(concordancePanel
			        .getAttachedComponent());
		} catch (IllegalArgumentException e) {
			// the translation panel has been detached. It's not contained an
		}
	}

	/**
	 * Selects the translations panel tab.
	 */
	public void selectTranslationsTab() {
		try {
			tmPanel.setSelectedComponent(translationsPanel
			        .getAttachedComponent());
		} catch (IllegalArgumentException e) {
			// the translation panel has been detached. It's not contained an
		}
	}

}

/**
 * Comparator class for TmMatch objects.
 */
class TmMatchComparator implements Comparator<TmMatch> {

	@Override
	public int compare(TmMatch o1, TmMatch o2) {

		int comp = 0;
		if (o1.getMatchScore() > o2.getMatchScore()) {
			comp = -1;
		} else if (o1.getMatchScore() < o2.getMatchScore()) {
			comp = 1;
		}
		return comp;
	}

}