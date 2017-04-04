package com.vistatec.ocelot.findrep;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.TextAtom;

/**
 * This class provides methods for searching text occurrences in Ocelot
 * segments.
 */
public class WordFinder {

	/** The case sensitive option constant. */
	public static final int CASE_SENSITIVE_OPTION = 0;

	/** The whole word option constant. */
	public static final int WHOLE_WORD_OPTION = 1;

	/** The wrap search option constant. */
	public static final int WRAP_SEARCH_OPTION = 2;

	/** None scope constant. */
	public static final int SCOPE_NONE = 0;

	/** The source scope constant. */
	public static final int SCOPE_SOURCE = 1;

	/** The target scope constant. */
	public static final int SCOPE_TARGET = 2;

	/** The direction down constant. */
	public static final int DIRECTION_DOWN = 0;

	/** The direction up constant. */
	public static final int DIRECTION_UP = 1;

	/** The number of available options. */
	private static final int AVAILABLE_OPTIONS_COUNT = 3;

	/** An index is assigned this value when it is reset. */
	private static final int RESET_VALUE = -2;

	/** The list of options. */
	private boolean[] options;

	/** The search scope. */
	private int scope;

	/** The search direction. */
	private int direction;

	/** Current segment index. */
	private int currSegIndex;

	/** Current atom index. */
	private int currAtomIndex;

	/** Current offset index. */
	private int currOffsetIdx;

	/** previous text boundary. */
	private int prevBoundary;

	/** current text boundary. */
	private int currBoundary;

	/** first text boundary. */
	private int firstBoundary;

	/** whole word found so far. */
	private String currWholeWord = "";

	/** The break iterator finding word boundaries. */
	private BreakIterator breakIt;

	/** The list of results. */
	private List<FindResult> allResults;

	/** The index of the current result. */
	private int currResultIndex = -1;

	/**
	 * Constructor.
	 */
	public WordFinder() {

		options = new boolean[AVAILABLE_OPTIONS_COUNT];
	}

	/**
	 * Reset all the fields.
	 */
	public void reset() {

		System.out.println("RESET");
		goToStartOfDocument();
		allResults = null;
		currResultIndex = -1;
		breakIt = null;
		scope = SCOPE_NONE;
		direction = DIRECTION_DOWN;
		options = new boolean[AVAILABLE_OPTIONS_COUNT];
	}

	/**
	 * Goes to the start of the document. If the search direction is set to
	 * <code>DIRECTION_DOWN</code>, then the search will restart from the
	 * beginning of the document; if the direction is <code>DIRECTION_UP</code>,
	 * then the search will restart from the end of the document.
	 */
	public void goToStartOfDocument() {

		currSegIndex = RESET_VALUE;
		currAtomIndex = RESET_VALUE;
		currOffsetIdx = RESET_VALUE;
		resetBoundaries();
	}

	/**
	 * Resets the word boundaries.
	 */
	private void resetBoundaries() {
		currBoundary = RESET_VALUE;
		prevBoundary = -1;
		if (currWholeWord.isEmpty()) {
			firstBoundary = -1;
		}
	}

	/**
	 * Sets the search direction.
	 * 
	 * @param direction
	 *            the search direction. Available directions are:
	 *            <code>FindAndReplaceManager.DIRECTION_DOWN</code> and
	 *            <code>FindAndReplaceManager.DIRECTION_UP</code>.
	 */
	public void setDirection(int direction) {
		if (direction != DIRECTION_DOWN && direction != DIRECTION_UP) {
			throw new IllegalArgumentException("Invalid direction value: "
					+ direction + ". Accepted values are " + DIRECTION_DOWN
					+ " and " + DIRECTION_UP + ".");
		}
		this.direction = direction;
	}

	/**
	 * Enables/disables a specific option
	 * 
	 * @param optionType
	 *            the option
	 * @param enable
	 *            a boolean stating if the option has to be enabled or disabled.
	 */
	public void enableOption(int optionType, boolean enable) {
		if (optionType < AVAILABLE_OPTIONS_COUNT) {
			options[optionType] = enable;
		}
	}

	/**
	 * Sets the search scope.
	 * 
	 * @param scope
	 *            the search scope. Available values are
	 *            <code>FindAndReplaceManager.SCOPE_SOURCE</code> and
	 *            <code>FindAndReplaceManager.SCOPE_TARGET</code>
	 * @param locale
	 *            the locale related to the specific scope.
	 */
	public void setScope(int scope, Locale locale) {

		System.out.println("SCOPE");
		if (scope != SCOPE_SOURCE && scope != SCOPE_TARGET) {
			throw new IllegalArgumentException("Invalid scope value: " + scope
					+ ". Accepted values are " + SCOPE_SOURCE + " and "
					+ SCOPE_TARGET + ".");
		}
		if (this.scope != scope) {
			this.scope = scope;
			breakIt = BreakIterator.getWordInstance(locale);
			goToStartOfDocument();
			allResults = null;
			currResultIndex = -1;
		}
	}

	/**
	 * Finds all occurrences of a text in the Ocelot segments.
	 * 
	 * @param text
	 *            the text
	 * @param segments
	 *            the Ocelot segments
	 * @return the list of results.
	 */
	public List<FindResult> findWord(String text, List<OcelotSegment> segments) {

		allResults = new ArrayList<FindResult>();
		goToStartOfDocument();
		while (findNextWord(text, segments)) {
			allResults.add(getCurrentResult());
		}
		if (!allResults.isEmpty()) {
			if (direction == DIRECTION_DOWN) {
				currResultIndex = 0;
			} else {
				currResultIndex = allResults.size() - 1;
			}
		}
		return allResults;
	}

	/**
	 * Finds the next word in the Ocelot segments.
	 * 
	 * @param text
	 *            the string to be searched.
	 * @param segments
	 *            the Ocelot segments.
	 * @return <code>true</code> if the string has been found:
	 *         <code>false</code> otherwise.
	 */
	private boolean findNextWord(String text, List<OcelotSegment> segments) {
		boolean found = false;
		if (options[WHOLE_WORD_OPTION]) {
			found = findNextWholeWord(text, segments);
		} else {
			found = findNextOccurrence(text, segments);
		}

		return found;
	}

	/**
	 * Finds the next occurrence of a specific string
	 * 
	 * @param occurrence
	 *            the string to be found
	 * @param segments
	 *            the Ocelot segments.
	 * @return <code>true</code> if the occurrence has been found;
	 *         <code>false</code> otherwise.
	 */
	private boolean findNextOccurrence(String occurrence,
			List<OcelotSegment> segments) {
		boolean found = false;
		adjustSegIndex(segments);
		while (((direction == DIRECTION_DOWN && currSegIndex < segments.size()) || (direction == DIRECTION_UP && currSegIndex >= 0))
				&& !found) {
			List<SegmentAtom> atoms = getAtomsFromSegment(segments
					.get(currSegIndex));
			String text = null;
			if (atoms != null && !atoms.isEmpty()) {
				adjustAtomIndex(atoms);
				while (((direction == DIRECTION_DOWN && currAtomIndex < atoms
						.size()) || (direction == DIRECTION_UP && currAtomIndex >= 0))
						&& !found) {
					if (atoms.get(currAtomIndex) instanceof TextAtom) {
						text = atoms.get(currAtomIndex).getData();
						if (text != null) {
							adjustOffset(text);
							found = findNextOccurrence(occurrence, text);
						}
					}
					if (!found) {
						incrementAtomIndex();
						currOffsetIdx = RESET_VALUE;
						resetBoundaries();
					}
				}
			}
			if (text == null || !found) {
				incrementSegmentIndex(segments.size());
				currAtomIndex = RESET_VALUE;
				currOffsetIdx = RESET_VALUE;
				resetBoundaries();
			}
		}
		return found;
	}

	/**
	 * Adjust the offset value in case it was reset.
	 * 
	 * @param text
	 *            the text that the offset relates to
	 */
	private void adjustOffset(String text) {

		if (currOffsetIdx == RESET_VALUE) {
			if (direction == DIRECTION_DOWN || text.isEmpty()) {
				currOffsetIdx = 0;
			} else {
				currOffsetIdx = text.length() - 1;
			}
		}
	}

	/**
	 * Finds the next occurrence of a specific string in a text.
	 * 
	 * @param occurrence
	 *            the string to be found
	 * @param text
	 *            the text
	 * @return <code>true</code> if the occurrence has been found;
	 *         <code>false</code> otherwise.
	 */
	private boolean findNextOccurrence(String occurrence, String text) {
		boolean found = false;
		while (((direction == DIRECTION_DOWN && currOffsetIdx < text.length()) || (direction == DIRECTION_UP && currOffsetIdx >= 0))
				&& !found) {
			found = text.regionMatches(!options[CASE_SENSITIVE_OPTION],
					currOffsetIdx, occurrence, 0, occurrence.length());
			if (found) {
				if (direction == DIRECTION_DOWN) {
					firstBoundary = currOffsetIdx;
					currBoundary = currOffsetIdx + occurrence.length();
				} else {
					currBoundary = currOffsetIdx;
					firstBoundary = currOffsetIdx + occurrence.length();
				}
			}
			incrementOffsetIndex();
		}
		return found;
	}

	/**
	 * Properly increments or decrements the offset index depending on the
	 * search direction.
	 */
	private void incrementOffsetIndex() {

		if (direction == DIRECTION_DOWN) {
			currOffsetIdx++;
		} else {
			currOffsetIdx--;
		}
	}

	/**
	 * Finds the next whole word in the Ocelot segments list.
	 * 
	 * @param word
	 *            the word to be searched
	 * @param segments
	 *            the Ocelot segments
	 * @return <code>true</code> if the word is found; <code>false</code>
	 *         otherwise
	 */
	private boolean findNextWholeWord(String word, List<OcelotSegment> segments ) {
		boolean found = false;
		adjustSegIndex(segments);
		while (((direction == DIRECTION_DOWN && currSegIndex < segments.size()) || (direction == DIRECTION_UP && currSegIndex >= 0))
				&& !found) {
			List<SegmentAtom> atoms = getAtomsFromSegment(segments
					.get(currSegIndex));
			String text = null;
			if (atoms != null) {
				adjustAtomIndex(atoms);
				while (((direction == DIRECTION_DOWN && currAtomIndex < atoms
						.size()) || (direction == DIRECTION_UP && currAtomIndex >= 0))
						&& !found) {
					if (atoms.get(currAtomIndex) instanceof TextAtom) {
						text = atoms.get(currAtomIndex).getData();
						if (text != null) {
							adjustBoundaries(text);
							found = findNextWholeWord(word, text);
						}
					}
					if (!found) {
						incrementAtomIndex();
						resetBoundaries();
					}
				}
			}
			if (text == null || !found) {
				currWholeWord = "";
				incrementSegmentIndex(segments.size());
				currAtomIndex = RESET_VALUE;
				resetBoundaries();
			}

		}
		return found;
	}

	/**
	 * Increments or decrements the segment index depending on the search
	 * direction
	 * 
	 * @param segCount
	 *            the number of segments.
	 */
	private void incrementSegmentIndex(int segCount) {

		if (direction == DIRECTION_DOWN) {
			currSegIndex++;
		} else {
			currSegIndex--;
		}

	}

	/**
	 * Increments or decrements the atom index depending on the search
	 * direction.
	 */
	private void incrementAtomIndex() {
		if (direction == DIRECTION_DOWN) {
			currAtomIndex++;
		} else {
			currAtomIndex--;
		}
	}

	/**
	 * Adjusts boundaries for the text in case they were reset.
	 * 
	 * @param text
	 *            the text
	 */
	private void adjustBoundaries(String text) {

		if (currBoundary == RESET_VALUE) {
			if (direction == DIRECTION_DOWN || text.isEmpty()) {
				currBoundary = 0;
			} else {
				currBoundary = text.length() - 1;
			}
		}
	}

	/**
	 * Finds the next whole word in a text.
	 * 
	 * @param word
	 *            the word to be searched
	 * @param text
	 *            the text
	 * @return <code>true</code> if the word has been found; <code>false</code>
	 *         otherwise
	 */
	private boolean findNextWholeWord(String word, String text) {
		boolean found = false;
		breakIt.setText(text);
		prevBoundary = currBoundary;
		if (direction == DIRECTION_DOWN) {
			if (prevBoundary == -1) {
				currBoundary = breakIt.first();
			} else {
				currBoundary = breakIt.following(prevBoundary);
			}
		} else {
			if (prevBoundary == -1) {
				currBoundary = breakIt.last();
			} else {
				currBoundary = breakIt.preceding(prevBoundary);
			}
		}
		while (currBoundary != BreakIterator.DONE && !found) {

			String subString = null;
			if (currWholeWord.isEmpty()) {
				firstBoundary = prevBoundary;
			}
			if (direction == DIRECTION_DOWN) {
				subString = text.substring(prevBoundary, currBoundary);
				currWholeWord += subString;
			} else {
				subString = text.substring(currBoundary, prevBoundary);
				currWholeWord = subString + currWholeWord;
			}
			found = checkCurrWordEqualsText(word, currWholeWord);
			if (!found) {
				if (!checkWholeWordSubstring(word)) {
					currWholeWord = "";
				}
				prevBoundary = currBoundary;
				if (direction == DIRECTION_DOWN) {
					currBoundary = breakIt.next();
				} else {
					currBoundary = breakIt.previous();
				}
			} else {
				currWholeWord = "";
			}

		}

		return found;
	}

	/**
	 * Checks if the current whole word found is a substring of the word to be
	 * found. It manages the case the "whole word" option is set and the user
	 * requests for finding a text composed by many whole words (ex.
	 * "word_1 word_2... word_n").
	 * 
	 * @param wordToFind
	 *            the word (or concatenation of words) to find.
	 * @return <code>true</code> if the found word is a substring of the word to
	 *         be searched; <code>false</code> otherwise
	 */
	private boolean checkWholeWordSubstring(String wordToFind) {

		boolean retValue = false;
		String currWholeWordCS = currWholeWord;
		if (options[CASE_SENSITIVE_OPTION]) {
			wordToFind = wordToFind.toLowerCase();
			currWholeWordCS = currWholeWord.toLowerCase();
		}
		if (direction == DIRECTION_DOWN) {
			retValue = wordToFind.startsWith(currWholeWordCS);
		} else {
			retValue = wordToFind.endsWith(currWholeWordCS);
		}
		return retValue;
	}

	/**
	 * Checks if the two strings passed as parameter are the same string.
	 * 
	 * @param text
	 *            the first string
	 * @param currWord
	 *            the second string
	 * @return <code>true</code> if they are the same string; <code>false</code>
	 *         otherwise
	 */
	private boolean checkCurrWordEqualsText(String text, String currWord) {

		boolean equal = false;
		if (!options[CASE_SENSITIVE_OPTION]) {
			equal = text.equalsIgnoreCase(currWord);
		} else {
			equal = text.equals(currWord);
		}

		return equal;

	}

	/**
	 * Adjusts the atom index in case it was reset.
	 * 
	 * @param atoms
	 *            the list of atoms.
	 */
	private void adjustAtomIndex(List<SegmentAtom> atoms) {

		if (currAtomIndex == RESET_VALUE) {
			if (direction == DIRECTION_DOWN || atoms.isEmpty()) {
				currAtomIndex = 0;
			} else {
				currAtomIndex = atoms.size() - 1;
			}
		} else {
			if (currAtomIndex == -1 && direction == DIRECTION_DOWN) {
				currAtomIndex++;
			} else if (currAtomIndex == atoms.size()
					&& direction == DIRECTION_UP) {
				currAtomIndex--;
			} else if (currAtomIndex == -1 && direction == DIRECTION_UP
					&& options[WRAP_SEARCH_OPTION]) {
				currAtomIndex = atoms.size() - 1;
			} else if (currAtomIndex == atoms.size()
					&& direction == DIRECTION_DOWN
					&& options[WRAP_SEARCH_OPTION]) {
				currAtomIndex = 0;
			}
		}
	}

	/**
	 * Adjusts the segment index in case it was reset.
	 * 
	 * @param segments
	 *            the list of segments.
	 */
	private void adjustSegIndex(List<OcelotSegment> segments) {

		if (currSegIndex == RESET_VALUE) {
			if (direction == DIRECTION_DOWN || segments == null
					|| segments.isEmpty()) {
				currSegIndex = 0;
			} else {
				currSegIndex = segments.size() - 1;
			}
		} else {
			if (currSegIndex == segments.size() && direction == DIRECTION_UP) {
				currSegIndex--;
			} else if (currSegIndex == -1 && direction == DIRECTION_DOWN) {
				currSegIndex++;
			} else if (currSegIndex == -1 && direction == DIRECTION_UP
					&& options[WRAP_SEARCH_OPTION]) {
				currSegIndex = segments.size() - 1;
			} else if (currSegIndex == segments.size()
					&& direction == DIRECTION_DOWN
					&& options[WRAP_SEARCH_OPTION]) {
				currSegIndex = 0;
			}
		}
	}

	/**
	 * Gets the proper list of atoms defined in a segment depending on the scope
	 * selected.
	 * 
	 * @param segment
	 *            the segment
	 * @return the propert list of atoms
	 */
	private List<SegmentAtom> getAtomsFromSegment(OcelotSegment segment) {

		List<SegmentAtom> atoms = null;
		if (scope == SCOPE_SOURCE) {
			atoms = segment.getSource().getAtoms();
		} else if (segment.getTarget() != null) {
			atoms = segment.getTarget().getAtoms();
		}
		return atoms;
	}

	/**
	 * Gets the found word first index.
	 * 
	 * @return the found word first index.
	 */
	private int getWordFirstIndex() {
		if (direction == DIRECTION_DOWN) {
			return firstBoundary;
		} else {
			return currBoundary;
		}
	}

	/**
	 * Gets the found word last index.
	 * 
	 * @return the found word last index.
	 */
	private int getWordLastIndex() {

		if (direction == DIRECTION_DOWN) {
			return currBoundary;
		} else {
			return firstBoundary;
		}
	}

	/**
	 * Gets the current result.
	 * 
	 * @return the current result.
	 */
	public FindResult getCurrentResult() {

		return new FindResult(currSegIndex, currAtomIndex, getWordFirstIndex(),
				getWordLastIndex(), scope == SCOPE_TARGET);
	}

	/**
	 * Gets all the results.
	 * 
	 * @return the list of results.
	 */
	public List<FindResult> getAllResults() {
		return allResults;
	}

	/**
	 * Clears the list of results.
	 */
	public void clearAllResults() {
		allResults = null;
	}

	/**
	 * Gets the index in the list of the current result.
	 * 
	 * @return the index in the list of the current result.
	 */
	public int getCurrentResIndex() {

		return currResultIndex;
	}
	
	public int getCurrentResIndexForReplace(){
		
		int resIdxForReplace = currResultIndex;
		if(allResults != null ){
			if(resIdxForReplace == -1 && direction == DIRECTION_UP){
				resIdxForReplace = 0;
			} else if (resIdxForReplace == allResults.size() && direction == DIRECTION_DOWN){
				resIdxForReplace = allResults.size() - 1;
			}
		}
		return resIdxForReplace;
	}

	/**
	 * Move the index to the next result.
	 */
	public void goToNextResult() {

		if (allResults != null) {
			if (currResultIndex == -1 && direction == DIRECTION_UP
					&& options[WRAP_SEARCH_OPTION]) {
				currResultIndex = allResults.size() - 1;
			} else if (currResultIndex == allResults.size()
					&& direction == DIRECTION_DOWN
					&& options[WRAP_SEARCH_OPTION]) {
				currResultIndex = 0;
			} else if (currResultIndex == -1 && direction == DIRECTION_DOWN) {
				currResultIndex++;
			} else if (currResultIndex == allResults.size()
					&& direction == DIRECTION_UP) {
				currResultIndex--;
			} else if (currResultIndex > -1
					&& currResultIndex < allResults.size()) {
				if (direction == DIRECTION_DOWN) {
					if (options[WRAP_SEARCH_OPTION]) {
						currResultIndex = (currResultIndex + 1)
								% allResults.size();
					} else {
						currResultIndex++;
					}
				} else {
					currResultIndex--;
					if (options[WRAP_SEARCH_OPTION] && currResultIndex == -1) {
						currResultIndex = allResults.size() - 1;
					}
				}
			}
		} else {
			currResultIndex = -1;
		}
	}

	/**
	 * Removes the current result from the list.
	 */
	public void removeCurrentResult() {

		if (allResults != null && currResultIndex > -1) {
			allResults.remove(currResultIndex);
			currResultIndex = -1;
		}
	}

	/**
	 * Adjusts results boundaries when the current occurrence is replaced by a
	 * new string.
	 * 
	 * @param newString
	 *            the new string.
	 */
	public void replacedString(String newString) {

		if (allResults != null && currResultIndex > -1
				&& currResultIndex < allResults.size()) {
			FindResult currRes = allResults.get(currResultIndex);
			int resIdx = currResultIndex + 1;
			FindResult nextRes = null;
			int delta = newString.length()
					- (currRes.getStringEndIndex() - currRes
							.getStringStartIndex());
			while (resIdx < allResults.size()) {
				nextRes = allResults.get(resIdx++);
				if (nextRes.getSegmentIndex() == currRes.getSegmentIndex()
						&& nextRes.getAtomIndex() == currRes.getAtomIndex()) {

					nextRes.setStringStartIndex(nextRes.getStringStartIndex()
							+ delta);
					nextRes.setStringEndIndex(nextRes.getStringEndIndex()
							+ delta);
				}
			}
		}
	}
}
