package com.vistatec.ocelot.tool;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import com.vistatec.ocelot.Ocelot;

/**
 * Ocelot Tool bar. It displays all the tools available for Ocelot users.
 */
public class OcelotToolBar extends JToolBar {

	/** Serial version UID. */
	private static final long serialVersionUID = -3930943136468195673L;

	/** Source font tool name. */
	public static final String SOURCE_FONT_TOOL_NAME = "Source Font";

	/** Target font tool name. */
	public static final String TARGET_FONT_TOOL_NAME = "Target Font";

	/** The source font tool. */
	private OcelotFontTool sourceFontTool;

	/** The target font tool. */
	private OcelotFontTool targetFontTool;

	/**
	 * Constructor.
	 * 
	 * @param ocelot
	 *            the ocelot main panel.
	 */
	public OcelotToolBar(final Ocelot ocelot) {

		makeToolBar(ocelot);

	}

	/**
	 * Builds the tool bar.
	 * 
	 * @param ocelot
	 *            the Ocelot main panel
	 */
	private void makeToolBar(final Ocelot ocelot) {

		// set border and layout to the tool bar.
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		setLayout(new GridBagLayout());

		// builds the font tools.
		sourceFontTool = new OcelotFontTool(SOURCE_FONT_TOOL_NAME, ocelot);
		targetFontTool = new OcelotFontTool(TARGET_FONT_TOOL_NAME, ocelot);

		GridBagConstraints gridBag = new GridBagConstraints();
		// Inserting Source Font Label
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.WEST;
		add(sourceFontTool, gridBag);
		// Inserting a separator
		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(5, 20));
		gridBag.insets = new Insets(5, 5, 5, 5);
		gridBag.gridx = 1;
		add(separator, gridBag);
		// Inserting Target Font LAbel
		gridBag.gridx = 2;
		gridBag.insets = new Insets(0, 0, 0, 0);
		add(targetFontTool, gridBag);

	}

	/**
	 * Loads appropriate font families and sizes depending on source and target
	 * languages.
	 * 
	 * @param sourceLanguage
	 *            the source language
	 * @param targetLanguage
	 *            the target language
	 */
	public void loadFontsAndSizes(final String sourceLanguage,
			final String targetLanguage) {

		sourceFontTool.loadFontsAndSizes(sourceLanguage);
		targetFontTool.loadFontsAndSizes(targetLanguage);

	}

	/**
	 * Makes the source font tool select the font passed as parameter.
	 * 
	 * @param font
	 *            the font to be selected into the source font tool.
	 */
	public void setSourceFont(final Font font) {

		sourceFontTool.setSelectedFont(font);
	}

	/**
	 * Makes the target font tool select the font passed as parameter.
	 * 
	 * @param font
	 *            the font to be selected into the target font tool.
	 */
	public void setTargetFont(final Font font) {

		targetFontTool.setSelectedFont(font);
	}

	/**
	 * Gets the selected font by the source font tool.
	 * 
	 * @return the selected source font.
	 */
	public Font getSelectedSourceFont() {

		return sourceFontTool.getSelectedFont();
	}

	/**
	 * Gets the selected font by the target font tool.
	 * 
	 * @return the selected target font.
	 */
	public Font getSelectedTargetFont() {

		return targetFontTool.getSelectedFont();
	}
}
