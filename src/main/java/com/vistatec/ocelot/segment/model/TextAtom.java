package com.vistatec.ocelot.segment.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vistatec.ocelot.segment.model.TextAtom.HighlightBoundaries;
import com.vistatec.ocelot.segment.view.SegmentTextCell;

public class TextAtom implements SegmentAtom {
	private String text;

	private List<HighlightBoundaries> highlightBoundaryList;
	
	private int currentHLBoundaryIdx = -1;


	public static class HighlightBoundaries {

		private int firstIndex;

		private int lastIndex;

		public HighlightBoundaries(int firstIndex, int lastIndex) {

			this.firstIndex = firstIndex;
			this.lastIndex = lastIndex;
		}

		public int getFirstIndex() {
			return firstIndex;
		}

		public int getLastIndex() {
			return lastIndex;
		}
		
		public void setFirstIndex(int firstIndex){
			this.firstIndex = firstIndex;
		}
		
		
		public void setLastIndex(int lastIndex){
			this.lastIndex = lastIndex;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof HighlightBoundaries) {
				HighlightBoundaries hb = (HighlightBoundaries) obj;
				return hb.firstIndex == firstIndex && hb.lastIndex == lastIndex;
			} else {
				return super.equals(obj);
			}
		}

		@Override
		public int hashCode() {
			return 11 * Integer.valueOf(firstIndex).hashCode()
					* Integer.valueOf(lastIndex).hashCode();
		}
	}

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

	public void clearHighlights() {
		highlightBoundaryList = null;
		currentHLBoundaryIdx = -1;
	}


	public void addHighlightBoundary(HighlightBoundaries highlightBoundary) {
		if (highlightBoundaryList == null) {
			highlightBoundaryList = new ArrayList<HighlightBoundaries>();
		}
		if(!highlightBoundaryList.contains(highlightBoundary)){
			boolean inserted = false;
			for (HighlightBoundaries hb : highlightBoundaryList) {
				if (hb.getFirstIndex() > highlightBoundary.getFirstIndex()) {
					highlightBoundaryList.add(highlightBoundaryList.indexOf(hb),
							highlightBoundary);
					inserted = true;
					break;
				}
			}
			if(!inserted){
				highlightBoundaryList.add(highlightBoundary);
			}
		}
	}

	public void setHighlightBoundariesList(
			List<HighlightBoundaries> highlightBoundariesList) {
		this.highlightBoundaryList = highlightBoundariesList;
		Collections.sort(this.highlightBoundaryList,
				new HighlightBoundariesComparator());
	}

	public List<HighlightBoundaries> getHighlightBoundaries() {
		return highlightBoundaryList;
	}
	
	public void setCurrentHLBoundaryIdx(int boundaryIdx){
		this.currentHLBoundaryIdx = boundaryIdx;
	}
	
	public int getCurrentHLBoundaryIdx() {
		return currentHLBoundaryIdx;
	}
	
	public void removeHighlighBoundary(int startIndex, int endIndex){
		
		if(highlightBoundaryList != null){
			HighlightBoundaries hbToDelete = null;
			for(HighlightBoundaries hb: highlightBoundaryList){
				if(hb.getFirstIndex() == startIndex && hb.getLastIndex() == endIndex){
					hbToDelete = hb;
					break;
				}
			}
			if(currentHLBoundaryIdx == highlightBoundaryList.indexOf(hbToDelete)){
				currentHLBoundaryIdx = -1;
			}
			highlightBoundaryList.remove(hbToDelete);
		}
	}


	public String getHighlightStyle() {
		return SegmentTextCell.highlightStyle;
	}

	public String getCurrHighlightStyle(){
		return SegmentTextCell.currHighlightStyle;
	}
	
	public void replace(String newString) {

		if (highlightBoundaryList != null) {
			for (HighlightBoundaries hb : highlightBoundaryList) {
				text = text.substring(0, hb.getFirstIndex()) + newString
						+ text.substring(hb.getLastIndex());
			}
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

class HighlightBoundariesComparator implements Comparator<HighlightBoundaries> {

	@Override
	public int compare(HighlightBoundaries o1, HighlightBoundaries o2) {

		return Integer.compare(o1.getFirstIndex(), o2.getFirstIndex());
	}

}
