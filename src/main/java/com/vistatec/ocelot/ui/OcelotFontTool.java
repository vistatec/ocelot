package com.vistatec.ocelot.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * This tool lets user to choose specific font family and size for a text to
 * display. The list of available font families depends on the specific
 * language.
 */
public class OcelotFontTool extends JPanel implements ItemListener {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Min value allowed for size combo. It is the min value used by MS Office
     * Word.
     */
    private static final int MIN_SIZE_VALUE = 1;

    /**
     * Max value allowed for size combo. It is the max value used by MS Office
     * Word.
     */
    private static final int MAX_SIZE_VALUE = 1638;

    /** Font family combo width. */
    private static final int FONT_FAMILY_WIDTH = 150;
    /** Font family combo max width. */
    private static final int FONT_FAMILY_MAX_WIDTH = 180;
    /** Font family combo height. */
    private static final int FONT_FAMILY_HEIGHT = 20;

    /** Font size combo width. */
    private static final int FONT_SIZE_WIDTH = 50;
    /** Font size combo max width. */
    private static final int FONT_SIZE_MAX_WIDTH = 80;
    /** Font size combo height. */
    private static final int FONT_SIZE_HEIGHT = 20;
    /** Model for the size combo box. */
    private final Integer[] sizeModel = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22,
            24, 26, 28, 36, 48, 72 };

    /** The font family combo. */
    private JComboBox<String> cmbFontFamily;

    /** The font size combo. */
    private JComboBox<Integer> cmbFontSize;

    /** The external listener to this tool events. */
    private ItemListener externalListener;

    /**
     * Constructor.
     * 
     * @param toolName
     *            this is the tool name. It will be displayed as the text of the
     *            label describing the tool.
     * @param listener
     *            the listener to this tool events.
     */
    public OcelotFontTool(final String toolName, final ItemListener listener) {

        this(toolName, null, listener);
    }

    /**
     * Constructor.
     * 
     * @param toolName
     *            this is the tool name. It will be displayed as the text of the
     *            label describing the tool.
     * @param languageTag
     *            the IETF BCP 47 language tag string used to detect the locale
     *            for retrieving the appropriate fonts list. It is used with
     *            this method {@link java.util.Locale#forLanguageTag(String)}
     * @param listener
     *            the listener to this tool events.
     */
    public OcelotFontTool(final String toolName, final String languageTag,
            final ItemListener listener) {

        this.externalListener = listener;
        makeTool(toolName, languageTag);
    }

    /**
     * Builds the tool.
     * 
     * @param toolName
     *            the tool name
     * @param languageTag
     *            the language tag
     */
    private void makeTool(String toolName, String languageTag) {

        // makes the tool transparent to make it appear perfectly embedded into
        // the tool bar
        setOpaque(false);
        // builds the tool components
        buildComponents(toolName);
        // sets the panel layout and adds the components into the tool panel.
        setLayout(new GridBagLayout());
        GridBagConstraints gridBag = new GridBagConstraints();
        // insert the label displaying the tool name
        JLabel lblSourceFont = new JLabel(toolName);
        gridBag.gridx = 0;
        gridBag.gridy = 0;
        gridBag.anchor = GridBagConstraints.WEST;
        gridBag.insets = new Insets(5, 5, 5, 5);
        add(lblSourceFont, gridBag);
        // Insert Source Font Family Combo
        gridBag.gridx = 1;
        gridBag.insets = new Insets(5, 0, 5, 0);
        add(cmbFontFamily, gridBag);
        // Insert Source Font Size Combo
        gridBag.gridx = 2;
        gridBag.insets = new Insets(5, 0, 5, 5);
        add(cmbFontSize, gridBag);
        // loads font and size lists.
        loadFontsAndSizes(languageTag);
    }

    /**
     * Loads the available fonts and sizes lists depending on the specific
     * language.
     * 
     * @param languageTag
     *            the IETF BCP 47 language tag.
     */
    public void loadFontsAndSizes(String languageTag) {
        if (languageTag != null) {
            // retrieves the available fonts for the language
            String[] sourceFontsModel = GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getAvailableFontFamilyNames(
                            Locale.forLanguageTag(languageTag));
            cmbFontFamily.setModel(new DefaultComboBoxModel<String>(
                    sourceFontsModel));
            cmbFontFamily.setEnabled(true);
            cmbFontSize.setModel(new DefaultComboBoxModel<Integer>(sizeModel));
            cmbFontSize.setEnabled(true);
        } else {
            // Disables the combo boxes if there is no language tag.
            cmbFontFamily.setEnabled(false);
            cmbFontSize.setEnabled(false);

        }

    }

    /**
     * Builds the tool components.
     * 
     * @param toolName
     *            the tool name
     */
    private void buildComponents(final String toolName) {
        // builds the font family combo
        cmbFontFamily = new JComboBox<String>();
        cmbFontFamily.setName(toolName);
        cmbFontFamily.addItemListener(this);
        cmbFontFamily.setPreferredSize(new Dimension(FONT_FAMILY_WIDTH,
                FONT_FAMILY_HEIGHT));
        cmbFontFamily.setMinimumSize(new Dimension(FONT_FAMILY_WIDTH,
                FONT_FAMILY_HEIGHT));
        cmbFontFamily.setMaximumSize(new Dimension(FONT_FAMILY_MAX_WIDTH,
                FONT_FAMILY_HEIGHT));
        // builds the font size combo.
        cmbFontSize = new JComboBox<Integer>();
        cmbFontSize.setName(toolName);
        cmbFontSize.addItemListener(this);
        cmbFontSize.setPreferredSize(new Dimension(FONT_SIZE_WIDTH,
                FONT_SIZE_HEIGHT));
        cmbFontSize.setMaximumSize(new Dimension(FONT_SIZE_MAX_WIDTH,
                FONT_SIZE_HEIGHT));
        // the font size is editable. Users can type the desired value into the
        // size combo. It will accept even those values not listed in the size
        // model.
        cmbFontSize.setEditable(true);
        // Sets a document to the size combo, in order to validate inserted text
        ((JTextComponent) cmbFontSize.getEditor().getEditorComponent())
                .setDocument(new IntegerDocument());

    }

    /**
     * Selects the appropriate font family and size according to the font passed
     * as parameter.
     * 
     * @param font
     *            a <code>java.awt.Font</code> object.
     */
    public void setSelectedFont(final Font font) {

        if (font != null) {
            cmbFontFamily.setSelectedItem(font.getFamily());
            cmbFontSize.setSelectedItem(font.getSize());
        }
    }

    /**
     * Gets the selected font.
     * 
     * @return a <code>java.awt.Font</code> object, built by using selected font
     *         family and size. The Font.PLAIN style is used.
     */
    public Font getSelectedFont() {

        Font font = null;
        if (cmbFontFamily != null && cmbFontFamily.getSelectedItem() != null
                && cmbFontSize != null && cmbFontSize.getSelectedItem() != null) {
            font = new Font((String) cmbFontFamily.getSelectedItem(),
                    Font.PLAIN, (Integer) cmbFontSize.getSelectedItem());
        }
        return font;
    }

    /**
     * Handles the event one of the combo has changed the selected item. If the
     * font family combo item changes, no checks take place; if the font size
     * selected item changes, it checks the new value falls in the allowed
     * interval. If all the checks are successful, then the external listener is
     * notified. (non-Javadoc)
     * 
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(ItemEvent e) {

        boolean canNotifyOutside = true;
        // This method is called twice: one for DESELECTED item and one for
        // SELECTED item.
        // Handles only the SELECTED item case.
        if (e.getStateChange() == ItemEvent.SELECTED) {
            // Make checks only if the size combo has changed
            if (e.getSource().equals(cmbFontSize)) {
                int selectedItem = (Integer) cmbFontSize.getSelectedItem();
                if (selectedItem < MIN_SIZE_VALUE
                        || selectedItem > MAX_SIZE_VALUE) {
                    canNotifyOutside = false;
                    // Display a message to the user
                    JOptionPane.showMessageDialog(this,
                            "Please, insert a value between " + MIN_SIZE_VALUE
                                    + " and " + MAX_SIZE_VALUE + ".", "Ocelot",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            // If the checks are successful, notify the external listener
            if (canNotifyOutside) {
                externalListener.itemStateChanged(e);
            }
        }
    }

    /**
     * This document let you validate text inserted in any Swing text field. It
     * let user insert only integer value into the text field.
     */
    private class IntegerDocument extends PlainDocument {

        /** serial version uid. */
        private static final long serialVersionUID = -412079755824907144L;

        /**
         * Checks if the string resulting from the insertion of the parameter
         * string at the given offset is an integer. If it is the case, the
         * string is inserted at the right offset; otherwise the current string
         * does not change.
         * 
         * @see javax.swing.text.PlainDocument#insertString(int,
         *      java.lang.String, javax.swing.text.AttributeSet)
         */
        @Override
        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {

            if (str != null) {
                boolean ok = true;
                final String currText = getText(0, getLength());
                final String newText = currText.substring(0, offs) + str
                        + currText.substring(offs, getLength());
                try {
                    Integer.parseInt(newText);
                } catch (NumberFormatException e) {
                    ok = false;
                }
                if (ok) {
                    super.insertString(offs, str, a);
                }
            }
        }

    }

}
