/*
 * Copyright (C) 2013-2015, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.segment.view;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.segment.model.CodeAtom;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;

/**
 * Representation of source/target segment text in segment table view. Handles
 * the style of the text with Inline tags and the link between the editor
 * behavior and the underlying data structure.
 * 
 * This class is for drawing only; see SegmentTextEditor cell for editing
 * functionality.
 */
public class SegmentTextCell extends JTextPane {
    private static final long serialVersionUID = 1L;

    private static Logger LOG = LoggerFactory.getLogger(SegmentTextCell.class);
    public static final String tagStyle = "tag", regularStyle = "regular",
            insertStyle = "insert", deleteStyle = "delete", enrichedStyle = "enriched", highlightStyle="highlight", currHighlightStyle="currHighlight";
    protected int row = -1;
    protected SegmentVariant v;
    protected boolean raw;
    
    protected static StyleContext newStyles() {
        StyleContext styles = new StyleContext();
        Style style = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style regular = styles.addStyle(regularStyle, style);

        Style s = styles.addStyle(tagStyle, regular);
        StyleConstants.setBackground(s, Color.LIGHT_GRAY);

        Style insert = styles.addStyle(insertStyle, s);
        StyleConstants.setForeground(insert, Color.BLUE);
        StyleConstants.setUnderline(insert, true);

        Style delete = styles.addStyle(deleteStyle, insert);
        StyleConstants.setForeground(delete, Color.RED);
        StyleConstants.setStrikeThrough(delete, true);
        StyleConstants.setUnderline(delete, false);
        
        Style highlight = styles.addStyle(highlightStyle, regular);
        StyleConstants.setBackground(highlight, Color.yellow);
        
        Style currHighlight = styles.addStyle(currHighlightStyle, regular);
        StyleConstants.setBackground(currHighlight, Color.green);
        return styles;
    }

    /**
     * Create a dummy cell for the purposes of cell sizing.  This cell
     * doesn't contain the style information and isn't linked to any of
     * the control logic.
     * @return dummy cell
     */
    public static SegmentTextCell createDummyCell() {
        return new SegmentTextCell();
    }

    /**
     * Create an empty cell for the purpose of holding live content. This
     * cell contains style information and is linked to the document.
     * @return real cell
     */
    public static SegmentTextCell createCell() {
        return new SegmentTextCell(newStyles());
    }

    /**
     * Create an empty cell holding the specified content. This
     * cell contains style information and is linked to the document.
     * @param v
     * @param raw
     * @param isBidi whether the cell contains bidi content
     * @return
     */
    public static SegmentTextCell createCell(int row, SegmentVariant v, boolean raw, boolean isBidi) {
        return new SegmentTextCell(row, v, raw, isBidi);
    }

    private SegmentTextCell(StyleContext styleContext) {
        super(new DefaultStyledDocument(styleContext));
    }

    protected SegmentTextCell() {
        super();
    }

    private SegmentTextCell(int row, SegmentVariant v, boolean raw, boolean isBidi) {
        this(newStyles());
        setVariant(row, v, raw);
        setBidi(isBidi);
    }

    public void setBidi(boolean isBidi) {
        if (isBidi) {
            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
    }

    public final void setDisplayCategories() {
        Style style = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyledDocument styleDoc = this.getStyledDocument();
        Style regular = styleDoc.addStyle(regularStyle, style);

        Style highlight = styleDoc.addStyle(highlightStyle, regular);
        StyleConstants.setBackground(highlight, Color.yellow);
        
        Style currHighlight = styleDoc.addStyle(currHighlightStyle, regular);
        StyleConstants.setBackground(currHighlight, Color.green);
        
        Style s = styleDoc.addStyle(tagStyle, regular);
        StyleConstants.setBackground(s, Color.LIGHT_GRAY);

        Style insert = styleDoc.addStyle(insertStyle, s);
        StyleConstants.setForeground(insert, Color.BLUE);
        StyleConstants.setUnderline(insert, true);

        Style delete = styleDoc.addStyle(deleteStyle, insert);
        StyleConstants.setForeground(delete, Color.RED);
        StyleConstants.setStrikeThrough(delete, true);
        StyleConstants.setUnderline(delete, false);
        
        Style enriched = styleDoc.addStyle(enrichedStyle, regular);
        StyleConstants.setForeground(enriched, Color.BLUE);
        StyleConstants.setUnderline(enriched, true);
        
    }

    public void setTextPane(List<String> styledText) {
        StyledDocument doc = this.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
            for (int i = 0; i < styledText.size(); i += 2) {
                doc.insertString(doc.getLength(), styledText.get(i),
                        doc.getStyle(styledText.get(i + 1)));
            }
        } catch (BadLocationException ex) {
            LOG.error("Error rendering text", ex);
        }
    }

    public SegmentVariant getVariant() {
        return this.v;
    }

    public void setVariant(int row, SegmentVariant v, boolean raw) {
        this.row = row;
        this.v = v;
        this.raw = raw;
        syncModelToView();
    }

    protected void syncModelToView() {
        if (v != null) {
            setTextPane(v.getStyleData(raw));
        } else {
            setTextPane(Collections.<String> emptyList());
        }
    }

    public void setTargetDiff(List<String> targetDiff) {
        setTextPane(targetDiff);
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        String text = getToolTipText(event.getPoint());
        return text == null ? super.getToolTipText(event) : text;
    }

    String getToolTipText(Point p) {
        return getToolTipText(viewToModel(p));
    }

    String getToolTipText(int offset) {
        if (v != null && v.containsTag(offset, 0)) {
            SegmentAtom atom = v.getAtomAt(offset);
            if (atom instanceof CodeAtom) {
                return ((CodeAtom) atom).getVerboseData();
            }
        }
        return null;
    }
}
