package com.vistatec.ocelot.findrep;

import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.TextAtom;

/**
 * This class provides methods for searching text occurrences in Ocelot
 * segments.
 */
public class FindAndReplaceManager {

	/** The case sensitive option constant. */
	public static final int CASE_SENSITIVE_OPTION = 0;

	/** The whole word option constant. */
	public static final int WHOLE_WORD_OPTION = 1;

	/** The wrap search option constant. */
	public static final int WRAP_SEARCH_OPTION = 2;

	/** The source scope constant. */
	public static final int SCOPE_SOURCE = 0;

	/** The target scope constant. */
	public static final int SCOPE_TARGET = 1;

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

	/**
	 * Constructor.
	 */
	public FindAndReplaceManager() {

		options = new boolean[AVAILABLE_OPTIONS_COUNT];
	}

	/**
	 * Reset all the fields.
	 */
	public void reset() {

		goToStartOfDocument();
		breakIt = null;
		scope = SCOPE_SOURCE;
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

		if (scope != SCOPE_SOURCE && scope != SCOPE_TARGET) {
			throw new IllegalArgumentException("Invalid scope value: " + scope
					+ ". Accepted values are " + SCOPE_SOURCE + " and "
					+ SCOPE_TARGET + ".");
		}

		this.scope = scope;
		breakIt = BreakIterator.getWordInstance(locale);
		goToStartOfDocument();
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
	public boolean findNextWord(String text, List<OcelotSegment> segments) {
		boolean found = false;
		currWholeWord = "";
		if (options[WHOLE_WORD_OPTION]) {
			found = findNextWholeWord(text, segments);
		} else {
			found = findNextOccurrence(text, segments);
		}

		return found;
	}

	// public List<FindReplaceResult> findAll(String text,
	// List<OcelotSegment> segments) {
	//
	// List<FindReplaceResult> results = new ArrayList<FindReplaceResult>();
	// goToStartOfDocument();
	// boolean found = true;
	// while (found) {
	// found = findNextWord(text, segments);
	// if (found) {
	// results.add(new FindReplaceResult(currSegIndex, currAtomIndex,
	// getFirstWordIndex(), getLastWordIndex(), scope));
	// }
	// }
	// return results;
	// }

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
			if (atoms != null) {
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
			// if (!found) {
			// System.out.println("End of document reached.");
			// } else {
			// System.out.println("Word found! Segment number: "
			// + currSegIndex + " - Boundaries: " + prevBoundary
			// + " - " + currBoundary);
			//
			// }
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
	private boolean findNextWholeWord(String word, List<OcelotSegment> segments) {
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
		// if (!found) {
		// System.out.println("End of document reached.");
		// } else {
		// System.out.println("Word found! Segment number: " + currSegIndex
		// + " - Boundaries: " + prevBoundary + " - " + currBoundary);
		//
		// }
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
			if (options[WRAP_SEARCH_OPTION]) {
				currSegIndex = currSegIndex % segCount;
			}
		} else {
			currSegIndex--;
			if (options[WRAP_SEARCH_OPTION] && currSegIndex == -1) {
				currSegIndex = segCount - 1;
			}
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
	 * Gets the current segment index.
	 * 
	 * @return the current segment index.
	 */
	public int getSegmentIndex() {
		return currSegIndex;
	}

	/**
	 * Gets the found word first index.
	 * 
	 * @return the found word first index.
	 */
	public int getWordFirstIndex() {
		if (direction == DIRECTION_DOWN) {
			// return prevBoundary;
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
	public int getWordLastIndex() {

		if (direction == DIRECTION_DOWN) {
			return currBoundary;
		} else {
			return firstBoundary;
			// return prevBoundary;
		}
	}

	/**
	 * Gets the current atom index.
	 * 
	 * @return the current atom index.
	 */
	public int getCurrAtomIndex() {
		return currAtomIndex;
	}

	/**
	 * Gets the scope.
	 * 
	 * @return the scope.
	 */
	public int getScope() {
		return scope;
	}
}
