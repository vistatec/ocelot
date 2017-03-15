package com.vistatec.ocelot.config.json;

public class LayoutConfig {

	private boolean showTranslations;

	private boolean showAttrsView;

	private boolean showDetailsView;
	
	private boolean showManageConfs;
	
	private SegmentsGridConfig segmentsGrid;
	
	public static class SegmentsGridConfig {
		
		private boolean showSegNum;
		
		private boolean showSource;
		
		private boolean showTarget;
		
		private boolean showOriginalTarget;
		
		private boolean showNotes;
		
		private boolean showEditDist;
		
		private boolean[] showFlags;
		
		private boolean showNotTranslatableRows;

		public boolean isShowSegNum() {
			return showSegNum;
		}

		public void setShowSegNum(boolean showSegNum) {
			this.showSegNum = showSegNum;
		}

		public boolean isShowSource() {
			return showSource;
		}

		public void setShowSource(boolean showSource) {
			this.showSource = showSource;
		}

		public boolean isShowTarget() {
			return showTarget;
		}

		public void setShowTarget(boolean showTarget) {
			this.showTarget = showTarget;
		}

		public boolean isShowOriginalTarget() {
			return showOriginalTarget;
		}

		public void setShowOriginalTarget(boolean showOriginalTarget) {
			this.showOriginalTarget = showOriginalTarget;
		}

		public boolean isShowNotes() {
			return showNotes;
		}

		public void setShowNotes(boolean showNotes) {
			this.showNotes = showNotes;
		}

		public boolean isShowEditDist() {
			return showEditDist;
		}

		public void setShowEditDist(boolean showEditDist) {
			this.showEditDist = showEditDist;
		}

		public boolean[] getShowFlags() {
			return showFlags;
		}

		public void setShowFlags(boolean[] showFlags) {
			this.showFlags = showFlags;
		}
		
		public boolean isShowNotTranslatableRows() {
			return showNotTranslatableRows;
		}

		public void setShowNotTranslatableRows(boolean showNotTranslatableRows) {
			this.showNotTranslatableRows = showNotTranslatableRows;
		}

		@Override
		public String toString() {
		
			return "show Segment #: " + showSegNum + 
					" - show Source: " + showSource +
					" - show Target: "  + showTarget + 
					" - show Original Target: " + showOriginalTarget + 
					" - show Notes: " + showNotes +
					" - show Edit Distance: " + showEditDist + 
					" - show Flags: " + showFlags;
		}
	}

	public void setShowTranslations(boolean showTranslations) {

		this.showTranslations = showTranslations;
	}

	public boolean isShowTranslations() {
		return showTranslations;
	}

	public void setShowAttrsView(boolean showAttrsView) {
		this.showAttrsView = showAttrsView;
	}

	public boolean isShowAttrsView() {
		return showAttrsView;
	}

	public void setShowDetailsView(boolean showDetailsView) {
		this.showDetailsView = showDetailsView;
	}

	public boolean isShowDetailsView() {
		return showDetailsView;
	}

	public void setShowManageConfs(boolean showManageConfs){
		this.showManageConfs = showManageConfs;
	}
	
	public boolean isShowManageConfs(){
		return showManageConfs;
	}
	
	public void setSegmentsGrid(SegmentsGridConfig segmentsGrid) {
		this.segmentsGrid = segmentsGrid;
	}
	
	public SegmentsGridConfig getSegmentsGrid(){
		return segmentsGrid;
	}
	

	@Override
	public String toString() {

		return "show Translations: " + showTranslations
		        + " - show Attributes View: " + showAttrsView
		        + " - show Details View: " + showDetailsView
		        + " - show Manage Configs button: " + showManageConfs
		        + "Main Grid Configuration: [" + segmentsGrid.toString() + "]";
	}
}
