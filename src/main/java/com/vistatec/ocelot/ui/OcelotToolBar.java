package com.vistatec.ocelot.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.lqi.model.LQIGridConfiguration;

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

	/** The LQI Configurations tool. */
	private OcelotLQIConfTool lqiConfTool;

	/**
	 * Constructor.
	 * 
	 * @param ocelot
	 *            the ocelot main panel.
	 */
	public OcelotToolBar(final Ocelot ocelot,
	        final LQIGridConfigurations lqiGridConfigurations, OcelotEventQueue eventQueue) {

		makeToolBar(ocelot, lqiGridConfigurations, eventQueue);

	}

	/**
	 * Builds the tool bar.
	 * 
	 * @param ocelot
	 *            the Ocelot main panel
	 */
	private void makeToolBar(final Ocelot ocelot,
	        final LQIGridConfigurations lqiGridConfigurations, OcelotEventQueue eventQueue) {

		// set border and layout to the tool bar.
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		setLayout(new GridBagLayout());

		// builds the font tools.
		sourceFontTool = new OcelotFontTool(SOURCE_FONT_TOOL_NAME, ocelot);
		targetFontTool = new OcelotFontTool(TARGET_FONT_TOOL_NAME, ocelot);

		// builds the LQI configuration tool.
		lqiConfTool = new OcelotLQIConfTool(lqiGridConfigurations, eventQueue);

		insertComponents(null);

	}
	
	private void insertComponents(List<Component> widgets ){
		
		GridBagConstraints gridBag = new GridBagConstraints();

		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.weighty = 0.5;
		gridBag.anchor = GridBagConstraints.WEST;
		add(lqiConfTool, gridBag);

		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(5, 20));
		gridBag.insets = new Insets(5, 5, 5, 5);
		gridBag.gridx = 1;
		add(separator, gridBag);
		
		if(widgets != null){
			JPanel widgetPanel = new JPanel();
			widgetPanel.setLayout(new BoxLayout(widgetPanel, BoxLayout.X_AXIS));
			widgetPanel.setOpaque(false);
			for(Component comp: widgets){
				widgetPanel.add(comp);
				widgetPanel.add(Box.createHorizontalStrut(5));
			}
			gridBag.gridx = GridBagConstraints.RELATIVE;
			gridBag.insets = new Insets(0, 0, 0, 0);
			add(widgetPanel, gridBag);
			separator = new JSeparator(JSeparator.VERTICAL);
			separator.setPreferredSize(new Dimension(5, 20));
			gridBag.insets = new Insets(5, 0, 5, 5);
			gridBag.gridx = GridBagConstraints.RELATIVE;
			add(separator, gridBag);
		}
		
		gridBag.gridx = GridBagConstraints.RELATIVE;
		gridBag.weightx = 0.5;
		gridBag.insets = new Insets(0, 0, 0, 0);
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		add(panel, gridBag);
		
		// Inserting Source Font Label
		gridBag.gridx = GridBagConstraints.RELATIVE;
		gridBag.weightx = 0;
		gridBag.anchor = GridBagConstraints.CENTER;
		gridBag.fill = GridBagConstraints.NONE;
		add(sourceFontTool, gridBag);
		// Inserting a separator
		separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(5, 20));
		gridBag.insets = new Insets(5, 5, 5, 5);
		gridBag.gridx = GridBagConstraints.RELATIVE;
		add(separator, gridBag);
		// Inserting Target Font LAbel
		gridBag.gridx = GridBagConstraints.RELATIVE;
		gridBag.insets = new Insets(0, 0, 0, 0);
		add(targetFontTool, gridBag);

		gridBag.gridx = GridBagConstraints.RELATIVE;
		gridBag.weightx = 0.5;
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		panel = new JPanel();
		panel.setOpaque(false);
		add(panel, gridBag);
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

	/**
	 * Sets the available configurations for the LQI grid.
	 * 
	 * @param lqiConfigurations
	 *            the object listing the configurations for the LQI grid.
	 */
	public void setLQIConfigurations(LQIGridConfigurations lqiConfigurations) {

		lqiConfTool.setLQIConfigurations(lqiConfigurations);
	}

	/**
	 * Gets the selected configuration for the LQI grid.
	 * 
	 * @return the selected configuration for the LQI grid.
	 */
	public LQIGridConfiguration getSelectedLQIConfiguration() {
		
		return lqiConfTool.getSelectedConfiguration();
	}
	
	
	public void addPluginWidgets(List<Component> widgets){
		
		removeAll();
		insertComponents(widgets);
		revalidate();
	}
}
