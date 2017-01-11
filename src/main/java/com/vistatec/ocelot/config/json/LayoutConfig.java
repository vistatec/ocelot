package com.vistatec.ocelot.config.json;

public class LayoutConfig {

	private boolean showTranslations;

	private boolean showAttrsView;

	private boolean showDetailsView;

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

	@Override
	public String toString() {

		return "show Translations: " + showTranslations
		        + " - show Attributes View: " + showAttrsView
		        + " - show Details View: " + showDetailsView;
	}
}
