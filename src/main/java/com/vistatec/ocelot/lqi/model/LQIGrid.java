package com.vistatec.ocelot.lqi.model;

import java.util.ArrayList;
import java.util.List;

public class LQIGrid {

	private int minorScore;
	private int seriousScore;
	private int criticalScore;
	List<LQIErrorCategory> errorCategories;

	public int getMinorScore() {
		return minorScore;
	}

	public void setMinorScore(int minorScore) {
		this.minorScore = minorScore;
	}

	public int getSeriousScore() {
		return seriousScore;
	}

	public void setSeriousScore(int seriousScore) {
		this.seriousScore = seriousScore;
	}

	public int getCriticalScore() {
		return criticalScore;
	}

	public void setCriticalScore(int criticalScore) {
		this.criticalScore = criticalScore;
	}

	public List<LQIErrorCategory> getErrorCategories() {
		return errorCategories;
	}

	public void setErrorCategories(List<LQIErrorCategory> errorCategories) {
		this.errorCategories = errorCategories;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {

		LQIGrid clonedGrid = new LQIGrid();
		clonedGrid.setCriticalScore(criticalScore);
		clonedGrid.setMinorScore(minorScore);
		clonedGrid.setSeriousScore(seriousScore);
		if (errorCategories != null) {
			List<LQIErrorCategory> clonedCategories = new ArrayList<LQIErrorCategory>();
			for (LQIErrorCategory currCat : errorCategories) {
				clonedCategories.add((LQIErrorCategory) currCat.clone());
			}
			clonedGrid.setErrorCategories(clonedCategories);
		}
		return clonedGrid;
	}

	public boolean isEmpty() {

		return minorScore == 0 && seriousScore == 0 && criticalScore == 0
		        && (errorCategories == null || errorCategories.isEmpty());
	}
}
