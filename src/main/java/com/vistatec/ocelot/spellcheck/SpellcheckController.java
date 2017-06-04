package com.vistatec.ocelot.spellcheck;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.HighlightEvent;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.ReplaceDoneEvent;
import com.vistatec.ocelot.events.ReplaceEvent;
import com.vistatec.ocelot.events.SegmentRowsSortedEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.findrep.FindResult;
import com.vistatec.ocelot.segment.model.OcelotSegment;

/**
 * Controller class supervising all the processes pertaining to spellchecking
 * functionality.
 */
public class SpellcheckController implements OcelotEventQueueListener {

	/** Result found constant. */
	public static final int RESULT_FOUND = 0;

	/** Result not found constant. */
	public static final int RESULT_NOT_FOUND = 1;

	/** Result end of document reached constant. */
	public static final int RESULT_END_OF_DOC_REACHED = 2;

	/** The Ocelot event queue. */
	private OcelotEventQueue eventQueue;

	/** The word finder object. */
	private Spellchecker spellchecker;

	/** The find and replace dialog. */
	private SpellcheckDialog scDialog;

	/** The source locale in the opened XLIFF document. */
	private Locale sourceLocale;

	/** The target locale in the opened XLIFF document. */
	private Locale targetLocale;

	/** The list of Ocelot segments. */
	private List<OcelotSegment> segments;

	/** Last searched text. */
	private String lastSearchedText;

	/** List of replaced results. */
	private List<Integer> replacedResIdxList;
	
	private int[] sortedIndexMap;

	/**
	 * Constructor.
	 * 
	 * @param eventQueue
	 *            the event queue
	 */
	public SpellcheckController(OcelotEventQueue eventQueue) {

		this.eventQueue = eventQueue;
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
        setTargetScope();
		clear();
	}
	
	@Subscribe
	public void segmentsSorted(SegmentRowsSortedEvent e){
		
		this.sortedIndexMap = e.getSortedIndexMap();
		clear();
	}

	/**
	 * Clears the controller.
	 */
	private void clear() {
		lastSearchedText = null;
		replacedResIdxList.clear();
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
	
	/**
	 * Finds the next occurrence of the text.
	 * 
	 * @param text
	 *            the text to be searched.
	 */
	public void findNext(String text) {

		if (checkDocumentOpened()) {
			// if the text to be searched has changed, then start to search from
			// the beginning (or the end) of the document
			if (lastSearchedText == null || !text.equals(lastSearchedText)) {
				lastSearchedText = text;
				replacedResIdxList.clear();
				spellchecker.goToStartOfDocument();
				spellchecker.clearAllResults();
				List<FindResult> results = spellchecker.findWord(text, getSortedSegmentList());
				if (results != null && !results.isEmpty()) {
					sendHighlightEvent(results);
				} else {
					eventQueue.post(new HighlightEvent(null, -1));
				}
				// if a list of results already exists, then go to the next
				// result
			} else if (spellchecker.getAllResults() != null
					&& !spellchecker.getAllResults().isEmpty()) {
				do {
					spellchecker.goToNextResult();
				} while (replacedResIdxList.contains(spellchecker
						.getCurrentResIndex()));
				if (spellchecker.getCurrentResIndex() != -1
						&& spellchecker.getCurrentResIndex() != spellchecker
								.getAllResults().size()) {
					sendHighlightEvent(spellchecker.getAllResults());
				} else {
				}
			}
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
			int option = JOptionPane
					.showConfirmDialog(
							scDialog,
							"Do you want to replace the selected occurrence with an empty string?",
							"Replace", JOptionPane.YES_NO_OPTION);
			replace = option == JOptionPane.YES_OPTION;
		}
		if (replace) {
			if (spellchecker.getAllResults() != null
					&& !spellchecker.getAllResults().isEmpty()) {
				int indexToReplace = spellchecker.getCurrentResIndexForReplace();
				if (replacedResIdxList.contains(indexToReplace)) {
					findNext(lastSearchedText);
				} else if (indexToReplace != -1) {
					eventQueue.post(new ReplaceEvent(newString, spellchecker
							.getAllResults().get(indexToReplace)
							.getSegmentIndex(), ReplaceEvent.REPLACE));
					spellchecker.replacedString(newString);
					replacedResIdxList.add(indexToReplace);
					if (replacedResIdxList.size() == spellchecker.getAllResults()
							.size()) {
						spellchecker.clearAllResults();
						clear();
					}
				}
			}
		}
	}

	/**
	 * Set the scope to the source.
	 */
	public void setSourceScope() {
		spellchecker.setScope(Spellchecker.SCOPE_SOURCE, sourceLocale);
		clear();
	}

	/**
	 * Sets the scope to the target.
	 */
	public void setTargetScope() {
		spellchecker.setScope(Spellchecker.SCOPE_TARGET, targetLocale);
		clear();
	}

	/**
	 * Sets the "whole word" option.
	 * 
	 * @param wholeWord
	 *            if <true> the whole word option will be set.
	 */
	public void setWholeWord(boolean wholeWord) {
		spellchecker.enableOption(Spellchecker.WHOLE_WORD_OPTION, wholeWord);
		clear();
	}

	/**
	 * Sets the "case sensitive" option.
	 * 
	 * @param caseSensitive
	 *            if <true> the case sensitive option will be set.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		spellchecker
				.enableOption(Spellchecker.CASE_SENSITIVE_OPTION, caseSensitive);
		clear();
	}

	/**
	 * Sets the search direction to "down".
	 */
	public void setSearchDirectionDown() {
		spellchecker.setDirection(Spellchecker.DIRECTION_DOWN);
	}

	/**
	 * Sets the search direction to "up".
	 */
	public void setSearchDirectionUp() {
		spellchecker.setDirection(Spellchecker.DIRECTION_UP);
	}

	/**
	 * Displays the find and replace dialog.
	 * 
	 * @param owner
	 *            the owner window.
	 */
	public void displayDialog(Window owner) {
		if (scDialog == null) {
			scDialog = new SpellcheckDialog(owner, this);
			scDialog.open();
		} else {
			scDialog.requestFocus();
		}
	}

	/**
	 * Closes the find and replace dialog.
	 */
	public void closeDialog() {

		spellchecker.reset();
		scDialog = null;
		clear();
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
	 * Enable/Disable the wrap search option.
	 * 
	 * @param enable
	 *            if <code>true</code> the wrap search is set.
	 */
	public void setWrapSearch(boolean enable) {

		spellchecker.enableOption(Spellchecker.WRAP_SEARCH_OPTION, enable);
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
			eventQueue.post(new ReplaceEvent(text, ReplaceEvent.REPLACE_ALL));
			spellchecker.clearAllResults();
			clear();
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
}
