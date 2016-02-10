package com.vistatec.ocelot.findrep;

import java.awt.Window;
import java.util.List;
import java.util.Locale;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.HighlightEvent;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.ReplaceEvent;
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

	/** The find and replace manager. */
	private FindAndReplaceManager frManager;

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

	/** States if at least an occurrence has been found for the current text. */
	private boolean atLeastOneFound;

	/**
	 * Constructor.
	 * 
	 * @param eventQueue
	 *            the event queue
	 */
	public FindAndReplaceController(OcelotEventQueue eventQueue) {

		this.eventQueue = eventQueue;
		frManager = new FindAndReplaceManager();
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
		sourceLocale = new Locale(e.getDocument().getSrcLocale()
				.getOriginalLocId());
		targetLocale = new Locale(e.getDocument().getTgtLocale()
				.getOriginalLocId());
		System.out.println(sourceLocale.toString());
		System.out.println(targetLocale.toString());
		if (frDialog != null) {
			int selectedScope = frDialog.getSelectedScope();
			if (selectedScope == FindAndReplaceManager.SCOPE_SOURCE) {
				setSourceScope();
			} else {
				setTargetScope();
			}
		}

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
				frManager.goToStartOfDocument();
				atLeastOneFound = false;
			}
			lastSearchedText = text;
			// if the text is found, then an event is sent for making the
			// segmentView highlight the text in the main grid
			if (frManager.findNextWord(text, segments)) {
				eventQueue
						.post(new HighlightEvent(
								frManager.getSegmentIndex(),
								frManager.getCurrAtomIndex(),
								frManager.getWordFirstIndex(),
								frManager.getWordLastIndex(),
								frManager.getScope() == FindAndReplaceManager.SCOPE_TARGET));
				frDialog.setResult(RESULT_FOUND);
				atLeastOneFound = true;
			} else if (atLeastOneFound) {
				// if the text wasn't found, but at least one occurrence was
				// found before, then a message is displayed informing the user
				// that the end (or beginning) of the document has been reached
				frDialog.setResult(RESULT_END_OF_DOC_REACHED);
			} else {
				// if neither text wasn't found and no occurrences were found
				// before, then a message is displayed informing the user that
				// the string was not found.
				frDialog.setResult(RESULT_NOT_FOUND);
			}
		}
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
		eventQueue.post(new ReplaceEvent(newString, new FindReplaceResult(
				frManager.getSegmentIndex(), frManager.getCurrAtomIndex(),
				frManager.getWordFirstIndex(), frManager.getWordLastIndex(),
				frManager.getScope())));
	}

	/**
	 * Set the scope to the source.
	 */
	public void setSourceScope() {
		if (checkDocumentOpened()) {
			frManager
					.setScope(FindAndReplaceManager.SCOPE_SOURCE, sourceLocale);
		}
	}

	/**
	 * Sets the scope to the target.
	 */
	public void setTargetScope() {
		if (checkDocumentOpened()) {
			frManager
					.setScope(FindAndReplaceManager.SCOPE_TARGET, targetLocale);
		}
	}

	/**
	 * Sets the "whole word" option.
	 * 
	 * @param wholeWord
	 *            if <true> the whole word option will be set.
	 */
	public void setWholeWord(boolean wholeWord) {
		frManager.enableOption(FindAndReplaceManager.WHOLE_WORD_OPTION,
				wholeWord);
	}

	/**
	 * Sets the "case sensitive" option.
	 * 
	 * @param caseSensitive
	 *            if <true> the case sensitive option will be set.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		frManager.enableOption(FindAndReplaceManager.CASE_SENSITIVE_OPTION,
				caseSensitive);
	}

	/**
	 * Sets the search direction to "down".
	 */
	public void setSearchDirectionDown() {
		frManager.setDirection(FindAndReplaceManager.DIRECTION_DOWN);
	}

	/**
	 * Sets the search direction to "up".
	 */
	public void setSearchDirectionUp() {
		frManager.setDirection(FindAndReplaceManager.DIRECTION_UP);
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

		frManager.reset();
		frDialog = null;
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

		frManager
				.enableOption(FindAndReplaceManager.WRAP_SEARCH_OPTION, enable);
	}
}
