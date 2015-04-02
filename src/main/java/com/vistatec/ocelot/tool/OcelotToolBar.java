package com.vistatec.ocelot.tool;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

public class OcelotToolBar extends JToolBar {

	private static final long serialVersionUID = -3930943136468195673L;

	private static final int FONT_FAMILY_WIDTH = 150;
	private static final int FONT_FAMILY_MAX_WIDTH = 180;
	private static final int FONT_FAMILY_HEIGHT = 20;

	private static final int FONT_SIZE_WIDTH = 50;
	private static final int FONT_SIZE_MAX_WIDTH = 80;
	private static final int FONT_SIZE_HEIGHT = 20;

	private final Integer[] sizeModel = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22,
			24, 26, 28, 36, 48, 72 };

	public static final String SOURCE_FONT_FAMILY_COMBO_NAME = "Source.FontFamily.Combo";

	public static final String SOURCE_FONT_SIZE_COMBO_NAME = "Source.FontSize.Combo";

	public static final String TARGET_FONT_FAMILY_COMBO_NAME = "Target.FontFamily.Combo";

	public static final String TARGET_FONT_SIZE_COMBO_NAME = "Target.FontSize.Combo";

	private JComboBox<String> sourceFonts;

	private JComboBox<String> targetFonts;

	private JComboBox<Integer> sourceSize;

	private JComboBox<Integer> targetSize;

	// private Locale sourceLocale;
	//
	// private Locale targetLocale;

	public OcelotToolBar(final ItemListener listener) {

		this(null, null, listener);

	}

	public OcelotToolBar(final String sourceLanguage,
			final String targetLanguage, final ItemListener listener) {

		// this.sourceLocale = sourceLocale;
		// this.targetLocale = targetLocale;

		makeToolBar(sourceLanguage, targetLanguage, listener);
	}

	private void makeToolBar(final String sourceLanguage,
			final String targetLanguage, final ItemListener listener) {

		// setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buildComboes(listener);
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

		setLayout(new GridBagLayout());
		GridBagConstraints gridBag = new GridBagConstraints();

		// Inserting Source Font Label
		JLabel lblSourceFont = new JLabel("Source Font");
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.WEST;
		gridBag.insets = new Insets(5, 5, 5, 5);
		add(lblSourceFont, gridBag);
		// Inserting Source Font Family Combo
		gridBag.gridx = 1;
		gridBag.insets = new Insets(5, 0, 5, 0);
		add(sourceFonts, gridBag);
		// Inserting Source Font Size Combo
		gridBag.gridx = 2;
		gridBag.insets = new Insets(5, 0, 5, 5);
		add(sourceSize, gridBag);

		// Inserting a separator
		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		separator.setPreferredSize(new Dimension(5, 20));
		gridBag.insets = new Insets(5, 5, 5, 5);
		gridBag.gridx = 3;
		add(separator, gridBag);
		// Inserting Target Font LAbel
		JLabel lblTargetFont = new JLabel("Target Font");
		gridBag.gridx = 4;
		gridBag.insets = new Insets(5, 0, 5, 5);
		add(lblTargetFont, gridBag);
		// Inserting Target Font Family Combo
		gridBag.gridx = 5;
		gridBag.insets = new Insets(5, 0, 5, 0);
		add(targetFonts, gridBag);
		// Inserting Target Font Size Combo
		gridBag.gridx = 6;
		gridBag.insets = new Insets(5, 0, 5, 5);
		add(targetSize, gridBag);
		// Load combo
		loadFontsAndSizes(sourceLanguage, targetLanguage);
	}

	private void buildComboes(final ItemListener listener) {
		sourceFonts = new JComboBox<String>();
		sourceFonts.setName(SOURCE_FONT_FAMILY_COMBO_NAME);
		sourceFonts.addItemListener(listener);
		sourceFonts.setPreferredSize(new Dimension(FONT_FAMILY_WIDTH,
				FONT_FAMILY_HEIGHT));
		sourceFonts.setMinimumSize(new Dimension(FONT_FAMILY_WIDTH,
				FONT_FAMILY_HEIGHT));
		sourceFonts.setMaximumSize(new Dimension(FONT_FAMILY_MAX_WIDTH,
				FONT_FAMILY_HEIGHT));
		sourceSize = new JComboBox<Integer>();
		sourceSize.setName(SOURCE_FONT_SIZE_COMBO_NAME);
		sourceSize.addItemListener(listener);
		sourceSize.setPreferredSize(new Dimension(FONT_SIZE_WIDTH,
				FONT_SIZE_HEIGHT));
		sourceSize.setMinimumSize(new Dimension(FONT_SIZE_WIDTH,
				FONT_SIZE_HEIGHT));
		sourceSize.setMaximumSize(new Dimension(FONT_SIZE_MAX_WIDTH,
				FONT_SIZE_HEIGHT));
		sourceSize.setEditable(true);
		targetFonts = new JComboBox<String>();
		targetFonts.setName(TARGET_FONT_FAMILY_COMBO_NAME);
		targetFonts.addItemListener(listener);
		targetFonts.setPreferredSize(new Dimension(FONT_FAMILY_WIDTH,
				FONT_FAMILY_HEIGHT));
		targetFonts.setMinimumSize(new Dimension(FONT_FAMILY_WIDTH,
				FONT_FAMILY_HEIGHT));
		targetFonts.setMaximumSize(new Dimension(FONT_FAMILY_MAX_WIDTH,
				FONT_FAMILY_HEIGHT));
		targetSize = new JComboBox<Integer>();
		targetSize.setName(TARGET_FONT_SIZE_COMBO_NAME);
		targetSize.addItemListener(listener);
		targetSize.setPreferredSize(new Dimension(FONT_SIZE_WIDTH,
				FONT_SIZE_HEIGHT));
		targetSize.setMinimumSize(new Dimension(FONT_SIZE_WIDTH,
				FONT_SIZE_HEIGHT));
		targetSize.setMaximumSize(new Dimension(FONT_SIZE_MAX_WIDTH,
				FONT_SIZE_HEIGHT));
		targetSize.setEditable(true);

	}

	public void loadFontsAndSizes(final String sourceLanguage,
			final String targetLanguage) {

		if (sourceLanguage != null) {
			String[] sourceFontsModel = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(
							Locale.forLanguageTag(sourceLanguage));
			sourceFonts.setModel(new DefaultComboBoxModel<String>(
					sourceFontsModel));
			sourceFonts.setEnabled(true);
			sourceSize.setModel(new DefaultComboBoxModel<Integer>(sizeModel));
			sourceSize.setEnabled(true);
		} else {
			sourceFonts.setEnabled(false);
			sourceSize.setEnabled(false);
		}
		if (targetLanguage != null) {
			String[] targetFontsModel = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(
							Locale.forLanguageTag(targetLanguage));
			targetFonts.setModel(new DefaultComboBoxModel<String>(
					targetFontsModel));
			targetFonts.setEnabled(true);
			targetSize.setModel(new DefaultComboBoxModel<Integer>(sizeModel));
			targetSize.setEnabled(true);
		} else {
			targetFonts.setEnabled(false);
			targetSize.setEnabled(false);
		}
	}

	public void setSourceFont(final Font font) {

		sourceFonts.setSelectedItem(font.getFamily());
		sourceSize.setSelectedItem(font.getSize());
	}

	public void setTargetFont(final Font font) {

		targetFonts.setSelectedItem(font.getFamily());
		targetSize.setSelectedItem(font.getSize());
	}

	public Font getSelectedSourceFont() {

		Font font = null;
		if (sourceFonts != null && sourceFonts.getSelectedItem() != null
				&& sourceSize != null && sourceSize.getSelectedItem() != null) {
			font = new Font((String) sourceFonts.getSelectedItem(), Font.PLAIN,
					(Integer) sourceSize.getSelectedItem());
		}
		return font;
	}
	public Font getSelectedTargetFont() {

		Font font = null;
		if (targetFonts != null && targetFonts.getSelectedItem() != null
				&& targetSize != null && targetSize.getSelectedItem() != null) {
			font = new Font((String) targetFonts.getSelectedItem(), Font.PLAIN,
					(Integer) targetSize.getSelectedItem());
		}
		return font;
	}
}
