package com.vistatec.ocelot.findrep;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.HighlightEvent;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.ReplaceDoneEvent;
import com.vistatec.ocelot.events.ReplaceEvent;
import com.vistatec.ocelot.events.SegmentRowsSortedEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.segment.model.OcelotSegment;

/**
 * Controller class supervising all the processes pertaining the Find and
 * Replace functionality.
 */
public class FindAndReplaceController implements OcelotEventQueueListener {

	/** Result found constant. */
	public static final int RESULT_FOUND = 0;

	/** Result not found constant. */
	public static final int RESULT_NOT_FOUND = 1;

	/** Result end of document reached constant. */
	public static final int RESULT_END_OF_DOC_REACHED = 2;

	/** The Ocelot event queue. */
	private OcelotEventQueue eventQueue;

	/** The word finder object. */
	private WordFinder wordFinder;

	/** The find and replace dialog. */
	private FindReplaceDialog frDialog;

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

	private boolean showNotTransSegments;

	/**
	 * Constructor.
	 * 
	 * @param eventQueue
	 *            the event queue
	 */
	public FindAndReplaceController(OcelotEventQueue eventQueue, boolean showNotTransSegments) {

		this.eventQueue = eventQueue;
		this.showNotTransSegments = showNotTransSegments;
		wordFinder = new WordFinder();
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
		sourceLocale = Locale.forLanguageTag(e.getDocument().getSrcLocale().toBCP47());
		targetLocale = Locale.forLanguageTag(e.getDocument().getTgtLocale().toBCP47());
		if (frDialog != null) {
			int selectedScope = frDialog.getSelectedScope();
			if (selectedScope == WordFinder.SCOPE_SOURCE) {
				setSourceScope();
			} else {
				setTargetScope();
			}
		}
		clear();

	}

	@Subscribe
	public void segmentsSorted(SegmentRowsSortedEvent e) {

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

	private List<OcelotSegment> getSortedSegmentList() {

		List<OcelotSegment> sortedList = new ArrayList<>();
		if (sortedIndexMap != null) {
			OcelotSegment[] sortedArray = new OcelotSegment[segments.size()];
			int newIndex = -1;
			for (int i = 0; i < segments.size(); i++) {
				newIndex = sortedIndexMap[i];
				if (newIndex > -1) {
					sortedArray[newIndex] = segments.get(i);
				}
			}
			if (showNotTransSegments) {
				sortedList = Arrays.asList(sortedArray);
			} else {
				
				sortedList = Arrays.asList(sortedArray).stream()
						.filter(seg -> seg != null)
						.collect(Collectors.toList());
			}
		} else {
			if (showNotTransSegments) {
				sortedList = segments;
			} else {
				sortedList = segments.stream()
						.filter(s -> s.isTranslatable())
						.collect(Collectors.toList());
			}
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
				wordFinder.goToStartOfDocument();
				wordFinder.clearAllResults();
				List<FindResult> results = wordFinder.findWord(text, getSortedSegmentList());
				if (results != null && !results.isEmpty()) {
					frDialog.displayOccurrenceNum(results.size());
					sendHighlightEvent(results);
				} else {
					frDialog.setResult(RESULT_NOT_FOUND);
					eventQueue.post(new HighlightEvent(null, -1));
				}
				// if a list of results already exists, then go to the next
				// result
			} else if (wordFinder.getAllResults() != null && !wordFinder.getAllResults().isEmpty()) {
				do {
					wordFinder.goToNextResult();
				} while (replacedResIdxList.contains(wordFinder.getCurrentResIndex()));
				if (wordFinder.getCurrentResIndex() != -1
						&& wordFinder.getCurrentResIndex() != wordFinder.getAllResults().size()) {
					sendHighlightEvent(wordFinder.getAllResults());
					frDialog.setResult(RESULT_FOUND);
				} else {
					frDialog.setResult(RESULT_END_OF_DOC_REACHED);
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
			currResIdx = wordFinder.getCurrentResIndex();
		} else {
			resultsToSend = new ArrayList<FindResult>();
			for (int i = 0; i < results.size(); i++) {
				if (!replacedResIdxList.contains(i)) {
					resultsToSend.add(results.get(i));
				}
			}
			currResIdx = resultsToSend.indexOf(results.get(wordFinder.getCurrentResIndex()));
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
			int option = JOptionPane.showConfirmDialog(frDialog,
					"Do you want to replace the selected occurrence with an empty string?", "Replace",
					JOptionPane.YES_NO_OPTION);
			replace = option == JOptionPane.YES_OPTION;
		}
		if (replace) {
			frDialog.hideOccNumber();
			if (wordFinder.getAllResults() != null && !wordFinder.getAllResults().isEmpty()) {
				int indexToReplace = wordFinder.getCurrentResIndexForReplace();
				if (replacedResIdxList.contains(indexToReplace)) {
					findNext(lastSearchedText);
				} else if (indexToReplace != -1) {
					eventQueue.post(new ReplaceEvent(lastSearchedText, newString,
							wordFinder.getAllResults().get(indexToReplace).getSegmentIndex(), ReplaceEvent.REPLACE));
					wordFinder.replacedString(newString);
					replacedResIdxList.add(indexToReplace);
					if (replacedResIdxList.size() == wordFinder.getAllResults().size()) {
						wordFinder.clearAllResults();
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
		wordFinder.setScope(WordFinder.SCOPE_SOURCE, sourceLocale);
		clear();
	}

	/**
	 * Sets the scope to the target.
	 */
	public void setTargetScope() {
		wordFinder.setScope(WordFinder.SCOPE_TARGET, targetLocale);
		clear();
	}

	/**
	 * Sets the "whole word" option.
	 * 
	 * @param wholeWord
	 *            if <true> the whole word option will be set.
	 */
	public void setWholeWord(boolean wholeWord) {
		wordFinder.enableOption(WordFinder.WHOLE_WORD_OPTION, wholeWord);
		clear();
	}

	/**
	 * Sets the "case sensitive" option.
	 * 
	 * @param caseSensitive
	 *            if <true> the case sensitive option will be set.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		wordFinder.enableOption(WordFinder.CASE_SENSITIVE_OPTION, caseSensitive);
		clear();
	}

	/**
	 * Sets the search direction to "down".
	 */
	public void setSearchDirectionDown() {
		wordFinder.setDirection(WordFinder.DIRECTION_DOWN);
	}

	/**
	 * Sets the search direction to "up".
	 */
	public void setSearchDirectionUp() {
		wordFinder.setDirection(WordFinder.DIRECTION_UP);
	}

	/**
	 * Displays the find and replace dialog.
	 * 
	 * @param owner
	 *            the owner window.
	 */
	public void displayDialog(Window owner) {
		if (frDialog == null) {
			frDialog = new FindReplaceDialog(owner, this);
			frDialog.open();
		} else {
			frDialog.requestFocus();
		}
	}

	/**
	 * Closes the find and replace dialog.
	 */
	public void closeDialog() {

		wordFinder.reset();
		frDialog = null;
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

		wordFinder.enableOption(WordFinder.WRAP_SEARCH_OPTION, enable);
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
			int option = JOptionPane.showConfirmDialog(frDialog,
					"Do you want to replace all occurrences with an empty string?", "Replace",
					JOptionPane.YES_NO_OPTION);
			replace = option == JOptionPane.YES_OPTION;
		}
		if (replace) {
			eventQueue.post(new ReplaceEvent(lastSearchedText, text, ReplaceEvent.REPLACE_ALL));
			wordFinder.clearAllResults();
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

		if (frDialog != null) {
			JOptionPane.showMessageDialog(frDialog, "Replaced " + e.getReplacedOccurrencesNum() + " occurrences.",
					"Replace All", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void setShowNotTransSegments(boolean showNotTransSegments) {
		this.showNotTransSegments = showNotTransSegments;
	}
}
