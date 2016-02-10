package com.vistatec.ocelot.segment.model;

import com.vistatec.ocelot.segment.view.SegmentTextCell;

public class TextAtom implements SegmentAtom {
	private String text;
	private int[] highlightIndices;

	public TextAtom(String text) {
		this.text = text;
	}

	@Override
	public int getLength() {
		return text.length();
	}

	@Override
	public String getData() {
		return text;
	}

	@Override
	public String getTextStyle() {
		return SegmentTextCell.regularStyle;
	}

//	public void clearHighlights() {
//		highlightIndices = null;
//	}

	public void setHighlightIndices(int[] highlightIndices) {
		this.highlightIndices = highlightIndices;
	}

	public int[] getHighlightIndices() {
		return highlightIndices;
	}

	public String getHighlightStyle() {
		return SegmentTextCell.highlightStyle;
	}

	public void replace(String newString) {

		if (highlightIndices != null) {
			text = text.substring(0, highlightIndices[0]) + newString
					+ text.substring(highlightIndices[1]);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof TextAtom))
			return false;
		return text.equals(((TextAtom) o).text);
	}

	@Override
	public int hashCode() {
		return text.hashCode();
	}

	@Override
	public String toString() {
		return '[' + text + ']';
	}
}
