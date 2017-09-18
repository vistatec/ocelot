package com.vistatec.ocelot.spellcheck;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.config.json.SpellingConfig.SpellingDictionary;
import com.vistatec.ocelot.events.HighlightEvent;
import com.vistatec.ocelot.events.LQIAdditionEvent;
import com.vistatec.ocelot.events.LQIConfigurationSelectionChangedEvent;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.ReplaceDoneEvent;
import com.vistatec.ocelot.events.ReplaceEvent;
import com.vistatec.ocelot.events.SegmentRowsSortedEvent;
import com.vistatec.ocelot.events.SegmentTargetEditEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.findrep.FindResult;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;
import com.vistatec.ocelot.lqi.model.LQISeverity;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;

/**
 * Controller class supervising all the processes pertaining to spellchecking
 * functionality.
 */
public class SpellcheckController implements OcelotEventQueueListener {

    /** The logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpellcheckController.class);

	/** Result found constant. */
	public static final int RESULT_FOUND = 0;

	/** Result not found constant. */
	public static final int RESULT_NOT_FOUND = 1;

	/** Result end of document reached constant. */
	public static final int RESULT_END_OF_DOC_REACHED = 2;

    /**
     * LQI category name; must be consistent with
     * LQIConstants.LQI_CATEGORIES_LIST
     */
    public static final String LQI_CATEGORY_MISSPELLING = "misspelling";

	/** The Ocelot event queue. */
	private OcelotEventQueue eventQueue;

    private JsonConfigService cfgService;

	/** The find and replace dialog. */
	private SpellcheckDialog scDialog;

	/** The source locale in the opened XLIFF document. */
	private Locale sourceLocale;

	/** The target locale in the opened XLIFF document. */
	private Locale targetLocale;

	/** The list of Ocelot segments. */
	private List<OcelotSegment> segments;

	/** List of replaced results. */
	private List<Integer> replacedResIdxList;

    /** Worker for loading spellcheck results. */
    private SpellcheckWorker scWorker;

    private Spellchecker spellchecker;

    private LQIGridConfiguration lqiConfig;

	private int[] sortedIndexMap;

    private boolean dirty;

	/**
	 * Constructor.
	 *
	 * @param eventQueue
	 *            the event queue
	 */
    public SpellcheckController(OcelotEventQueue eventQueue, JsonConfigService cfgService) {

		this.eventQueue = eventQueue;
        this.cfgService = cfgService;
        spellchecker = new Spellchecker();
		replacedResIdxList = new ArrayList<Integer>();
	}

	/**
	 * Handles the event a new XLIFF file is opened.
	 *
	 * @param e
	 *            the open file event.
	 */
	@Subscribe
	public void fileOpened(OpenFileEvent e) {

		segments = e.getDocument().getSegments();
		sortedIndexMap = null;
        sourceLocale = Locale.forLanguageTag(e.getDocument().getSrcLocale()
				.getOriginalLocId());
        targetLocale = Locale.forLanguageTag(e.getDocument().getTgtLocale()
				.getOriginalLocId());
        spellchecker.setLocale(targetLocale);
		clear();
        closeDialog();
	}

	@Subscribe
	public void segmentsSorted(SegmentRowsSortedEvent e){

		this.sortedIndexMap = e.getSortedIndexMap();
		clear();
        closeDialog();
	}

	/**
	 * Clears the controller.
	 */
	private void clear() {
		replacedResIdxList.clear();
        spellchecker.reset();
        if (scWorker != null) {
            scWorker.cancel(true);
            scWorker = null;
        }
        dirty = false;
	}

	private List<OcelotSegment> getSortedSegmentList(){

		List<OcelotSegment> sortedList = null;
		if(sortedIndexMap != null){
			OcelotSegment[] sortedArray = new OcelotSegment[segments.size()];
			for(int i = 0; i<segments.size(); i++){
				sortedArray[sortedIndexMap[i]] = segments.get(i);
			}
			sortedList = Arrays.asList(sortedArray);
		} else {
			sortedList = segments;
		}
		return sortedList;
	}

    void checkSpelling() {
        if (scWorker != null) {
            scWorker.cancel(true);
        }
        scWorker = new SpellcheckWorker();
        scWorker.execute();
    }

    class SpellcheckWorker extends SwingWorker<Void, Integer> {

        @Override
        protected Void doInBackground() throws Exception {
            SwingUtilities.invokeLater(() -> {
                scDialog.setMessage(null);
                scDialog.setResult(null);
                scDialog.setProgressVisible(true);
            });
            spellchecker.spellcheck(getSortedSegmentList(), this::isCancelled, (n, total) -> publish(n),
                    cfgService.getSpellingConfig());
            return null;
        }

        @Override
        protected void process(List<Integer> chunks) {
            Collections.sort(chunks);
            scDialog.setProgress(chunks.get(chunks.size() - 1), segments.size());
        }

        @Override
        protected void done() {
            try {
                get();
                update();
                dirty = false;
            } catch (InterruptedException | CancellationException e) {
                LOGGER.trace("Exception occurred while performing spellcheck", e);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof LocaleNotSupportedException) {
                    scDialog.setResult(null);
                    scDialog.setMessage(e.getCause().getMessage());
                } else {
                    LOGGER.trace("Error occurred while performing spellcheck", e);
                    JOptionPane.showMessageDialog(scDialog, "An error has occurred while performing spellcheck.",
                            "Spellcheck Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void update() {
        if (spellchecker.hasResults()) {
            eventQueue.post(new HighlightEvent(new ArrayList<>(spellchecker.getAllResults()),
                    spellchecker.getCurrentResIndex()));
            scDialog.setResult(spellchecker.getCurrentResult());
            scDialog.setRemaining(spellchecker.getRemainingResults());
        } else {
            eventQueue.post(new HighlightEvent(null, -1));
            scDialog.setResult(null);
            scDialog.setMessage("No spelling issues");
        }
    }

	/**
	 * Sends the highlight event for current results.
	 *
	 * @param results
	 *            the find results.
	 */
	private void sendHighlightEvent(List<FindResult> results) {

		List<FindResult> resultsToSend = null;
		int currResIdx = -1;
		if (replacedResIdxList.isEmpty()) {
			resultsToSend = results;
            currResIdx = spellchecker.getCurrentResIndex();
		} else {
			resultsToSend = new ArrayList<FindResult>();
			for (int i = 0; i < results.size(); i++) {
				if (!replacedResIdxList.contains(i)) {
					resultsToSend.add(results.get(i));
				}
			}
            currResIdx = resultsToSend.indexOf(results.get(spellchecker
					.getCurrentResIndex()));
		}

		eventQueue.post(new HighlightEvent(resultsToSend, currResIdx));
	}

	/**
	 * Displays the find and replace dialog.
	 *
	 * @param owner
	 *            the owner window.
	 */
	public void displayDialog(Window owner) {
		if (scDialog == null) {
            dirty = false;
            scDialog = new SpellcheckDialog(owner, this, lqiConfig.getSeverities());
			scDialog.open();
            checkSpelling();
		} else {
			scDialog.requestFocus();
		}
	}

	/**
	 * Closes the find and replace dialog.
	 */
	public void closeDialog() {
        if (scDialog != null) {
            scDialog.dispose();
            scDialog = null;
        }
		// The Highlight event with null result, clear the table from the
		// highlighted values
		eventQueue.post(new HighlightEvent(null, -1));
	}

	/**
	 * Checks if there is a XLIFF document currently opened in Ocelot.
	 *
	 * @return <code>true</code> if a document is opened; <code>false</code>
	 *         otherwise.
	 */
	private boolean checkDocumentOpened() {

		return sourceLocale != null && targetLocale != null && segments != null;
	}

    /**
     * Replaces the string currently highlighted in the grid, with a new string.
     * This method simply sends an event and then the segment view will take
     * care of the text replacing.
     *
     * @param newString
     *            the new string.
     */
    public void replace(String newString) {
        boolean replace = true;
        if (newString.isEmpty()) {
            int option = JOptionPane.showConfirmDialog(scDialog,
                    "Do you want to replace the selected word with an empty string?", "Replace",
                    JOptionPane.YES_NO_OPTION);
            replace = option == JOptionPane.YES_OPTION;
        }
        if (replace) {
            if (spellchecker.hasResults()) {
                CheckResult res = spellchecker.getCurrentResult();
                eventQueue
                        .post(new ReplaceEvent(res.getWord(), newString, res.getSegmentIndex(), ReplaceEvent.REPLACE));
                spellchecker.replaced(newString);
                update();
            }
        }
    }

	/**
	 * Replaces all the highlighted strings with a specific text.
	 *
	 * @param text
	 *            the text
	 */
	public void replaceAll(String text) {

		boolean replace = true;
		if (text.isEmpty()) {
			int option = JOptionPane
					.showConfirmDialog(
							scDialog,
							"Do you want to replace all occurrences with an empty string?",
							"Replace", JOptionPane.YES_NO_OPTION);
			replace = option == JOptionPane.YES_OPTION;
		}
		if (replace) {
            eventQueue
                    .post(new ReplaceEvent(spellchecker.getCurrentResult().getWord(), text, ReplaceEvent.REPLACE_ALL));
            spellchecker.replacedAll(text);
            update();
		}
	}

	/**
	 * Once all occurrences have been replaced, it prompt a message to the user
	 * displaying the number of replaced occurrences.
	 *
	 * @param e
	 *            the replace done event.
	 */
	@Subscribe
	public void handleReplaceAllDone(ReplaceDoneEvent e) {

		if (scDialog != null) {
			JOptionPane.showMessageDialog(scDialog,
					"Replaced " + e.getReplacedOccurrencesNum()
							+ " occurrences.", "Replace All",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

    public void ignoreOne() {
        spellchecker.ignoreOne();
        update();
    }

    public void ignoreAll() {
        spellchecker.ignoreAll();
        update();
    }

    @Subscribe
    public void segmentTargetEdited(SegmentTargetEditEvent e) {
        // This partially duplicates logic in BaseSegment.updateTarget(), which
        // we don't want to use because it mutates the segment, and it is
        // properly called in SegmentServiceImpl.updateSegmentTarget(). We can't
        // consume the resulting event of the latter (SegmentEditEvent) because
        // it is too generic.
        SegmentVariant updatedTarget = e.getUpdatedTarget();
        dirty = !updatedTarget.getDisplayText().equals(e.getSegment().getTarget().getDisplayText());
    }

    public void segmentTargetReset(SegmentTargetResetEvent e) {
        dirty = true;
    }

    boolean segmentsWereModified() {
        return dirty;
    }

    public void learnWord(String word) {
        spellchecker.ignoreAll();
        update();
        String lang = spellchecker.getLanguage();
        SpellingDictionary dict = cfgService.getSpellingDictionary(lang);
        dict.getLearnedWords().add(word);
        try {
            cfgService.saveSpellingDictionary(lang, dict);
        } catch (TransferException e) {
            LOGGER.trace("Error while saving learned word " + word + " for language " + lang, e);
            JOptionPane.showMessageDialog(scDialog, "An error has occurred while saving settings.", "Spellcheck Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addLqi(LQISeverity severity) {
        LanguageQualityIssue lqi = new LanguageQualityIssue();
        lqi.setSeverity(severity.getScore());
        lqi.setType(LQI_CATEGORY_MISSPELLING);
        lqi.setSeverityName(severity.getName());
        CheckResult res = spellchecker.getCurrentResult();
        lqi.setComment(summarizeResult(res));
        if (res != null) {
            int idx = res.getSegmentIndex();
            if (idx >= 0 && idx < getSortedSegmentList().size()) {
                OcelotSegment seg = getSortedSegmentList().get(idx);
                eventQueue.post(new LQIAdditionEvent(lqi, seg, true));
                ignoreOne();
            }
        }
    }

    public void addLqiAll(LQISeverity severity) {
        if (spellchecker.hasResults()) {
            List<CheckResult> ignored = spellchecker.ignoreAll();
            for (CheckResult res : ignored) {
                LanguageQualityIssue lqi = new LanguageQualityIssue();
                lqi.setSeverity(severity.getScore());
                lqi.setType(LQI_CATEGORY_MISSPELLING);
                lqi.setSeverityName(severity.getName());
                lqi.setComment(summarizeResult(res));
                int idx = res.getSegmentIndex();
                if (idx >= 0 && idx < getSortedSegmentList().size()) {
                    OcelotSegment seg = getSortedSegmentList().get(idx);
                    eventQueue.post(new LQIAdditionEvent(lqi, seg, true));
                }
            }
            update();
        }
    }

    private String summarizeResult(CheckResult res) {
        List<String> suggestions = res.getSuggestions();
        if (suggestions.isEmpty()) {
            return String.format("Misspelled word: \"%s\"", res.getWord());
        }
        if (suggestions.size() > 3) {
            suggestions = suggestions.subList(0, 3);
        }
        return String.format("Misspelled word: \"%s\"; possible corrections: \"%s\"", res.getWord(),
                String.join("\", \"", suggestions));
    }

    @Subscribe
    public void lqiConfigChanged(LQIConfigurationSelectionChangedEvent evt) {
        lqiConfig = evt.getNewSelectedConfiguration();
        if (scDialog != null) {
            scDialog.setSeverities(lqiConfig == null ? Collections.emptyList() : lqiConfig.getSeverities());
        }
    }
}
