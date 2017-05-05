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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.segment.model.CodeAtom;
import com.vistatec.ocelot.segment.model.PositionAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;

/**
 * Representation of source/target segment text in segment table view.
 * Handles the style of the text with Inline tags and the link between
 * the editor behavior and the underlying data structure.
 */
public class SegmentTextEditorCell extends SegmentTextCell {
    private static final long serialVersionUID = 1L;

    private static Logger LOG = LoggerFactory.getLogger(SegmentTextEditorCell.class);
    private SegmentVariant vOrig;
    
    private JFrame menuFrame;

    /**
     * Create an empty cell for the purpose of holding live content. This
     * cell contains style information and is linked to the document.
     * @return real cell
     */
    public static SegmentTextEditorCell createCell() {
        return new SegmentTextEditorCell(newStyles());
    }

    /**
     * Create an empty cell holding the specified content. This
     * cell contains style information and is linked to the document.
     * @param v
     * @param raw
     * @param isBidi whether the cell contains bidi content
     * @return
     */
    public static SegmentTextEditorCell createCell(int row, SegmentVariant v, boolean raw, boolean isBidi) {
        return new SegmentTextEditorCell(row, v, raw, isBidi);
    }

    private SegmentTextEditorCell(StyleContext styleContext) {
        super();
        setStyledDocument(new DefaultStyledDocument(styleContext));
        setEditController();
        addCaretListener(new TagSelectingCaretListener());
        setTransferHandler(new TagAwareTransferHandler());
        setDragEnabled(true);
        setDropMode(DropMode.INSERT);
        addMouseListener(new ContextMenuListener());
    }

    private SegmentTextEditorCell() {
        super();
    }

    private SegmentTextEditorCell(int row, SegmentVariant v, boolean raw, boolean isBidi) {
        this(newStyles());
        setVariant(row, v, raw);
        setBidi(isBidi);
    }

    /**
     * A caret listener that detects selections that encompass
     * only part of tags and automatically expand the selection
     * to include full tags.  This produces cascading CaretUpdate
     * events, but the cycle should stop after a single additional
     * update.
     */
    class TagSelectingCaretListener implements CaretListener {
        @Override
        public void caretUpdate(CaretEvent e) {
            if (e.getDot() != e.getMark()) {
                int origStart = Math.min(e.getDot(), e.getMark());
                int origEnd = Math.max(e.getDot(), e.getMark());
                int start = v.findSelectionStart(origStart);
                int end = v.findSelectionEnd(origEnd);
                if (start != origStart) {
                    setSelectionStart(start);
                }
                if (end != origEnd) {
                    setSelectionEnd(end);
                }
            }
        }
    }

    public final void setEditController() {
        StyledDocument styledDoc = getStyledDocument();
        if (styledDoc instanceof AbstractDocument) {
            AbstractDocument doc = (AbstractDocument)styledDoc;
            doc.setDocumentFilter(new SegmentFilter());
        }
    }

    public void setTextPane(List<String> styledText) {
        SegmentVariant tmp = v;
        try {
            // We temporarily set v to null here to get around the
            // SegmentFilter, which will prevent us from clearing the text if
            // there are tags.
            v = null;
            super.setTextPane(styledText);
        } finally {
            v = tmp;
        }
    }

    public final void setVariant(int row, SegmentVariant v, boolean raw) {
        this.vOrig = v.createCopy();
        super.setVariant(row, v, raw);
    }

	/**
     * Handles edit behavior in segment text cell.
     */
    public class SegmentFilter extends DocumentFilter {

        // This is also called when initially populating the table,
        // as swing will try to "remove" the old contents.
        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {

            // When composing text (CJK, etc., input) allow all edits to the
            // view only.
            if (getStyledDocument().getCharacterElement(offset).getAttributes()
                    .isDefined(StyleConstants.ComposedTextAttribute)) {
                fb.remove(offset, length);
                return;
            }
            if (v != null) {
                // Allow atomic tag deletions
                if (v.containsTag(offset, length)) {
                    int start = v.findSelectionStart(offset);
                    int end = v.findSelectionEnd(offset + length);
                    v.clearSelection(start, end);
                    super.remove(fb, start, end - start);
                } else {
                    // Remove from cell editor
                    super.remove(fb, offset, length);
    
                    // Remove from underlying segment structure
                    deleteChars(offset, length);
                }
            }
            else {
                // TODO: why does this correct the spacing issue?
                super.remove(fb, offset, length);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String str,
                AttributeSet a) throws BadLocationException {

            // When composing text (CJK, etc., input) allow all edits to the
            // view only.
            if (a.isDefined(StyleConstants.ComposedTextAttribute)) {
                fb.replace(offset, length, str, a);
                return;
            }

            if (length > 0) {
                if (v.containsTag(offset, length)) {
                    int start = v.findSelectionStart(offset);
                    int end = v.findSelectionEnd(offset + length);
                    v.clearSelection(start, end);
                    v.modifyChars(offset, 0, str);
                    super.replace(fb, start, end - start, str, a);
                } else {
                    // Remove from cell editor
                    super.replace(fb, offset, length, str, a);

                    // Remove from underlying segment structure
                    v.modifyChars(offset, length, str);
                }
            } else {
                if (v.canInsertAt(offset)) {
                    // Insert string into cell editor.
                    super.replace(fb, offset, length, str, a);

                    insertChars(str, offset);
                }
            }
        }

        public void deleteChars(int offset, int charsToRemove) {
            v.modifyChars(offset, charsToRemove, "");
        }

        public void insertChars(String insertText, int offset) {
            v.modifyChars(offset, 0, insertText);
        }
    }

    static class TagAwareTransferHandler extends TransferHandler {

        private static final long serialVersionUID = 1L;
        private boolean shouldRemove;

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            SegmentTextEditorCell cell = (SegmentTextEditorCell) c;
            shouldRemove = true;
            return new SegmentVariantTransferable("" + cell.row, cell.v, cell.getSelectionStart(),
                    cell.getSelectionEnd());
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            SegmentTextEditorCell cell = (SegmentTextEditorCell) source;
            if (shouldRemove && action == TransferHandler.MOVE) {
                // Only clear the original selection here if we didn't
                // already handle it in importData().
                ((SegmentVariantTransferable) data).removeText();
                cell.syncModelToView();
            }
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(SELECTION_FLAVOR)
                    || support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            SegmentTextEditorCell cell = (SegmentTextEditorCell) support.getComponent();
            if (support.isDataFlavorSupported(SELECTION_FLAVOR)) {
                return importSegmentVariantSelection(cell, support);
            } else if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return importString(cell, support);
            }
            return false;
        }

        private boolean importSegmentVariantSelection(SegmentTextEditorCell cell, TransferSupport support) {
            try {
                Transferable trfr = support.getTransferable();
                SegmentVariantSelection sel = (SegmentVariantSelection) trfr.getTransferData(SELECTION_FLAVOR);
                // Check to make sure we're pasting from the same row and the
                // same variant type.
                if (sel.getId().equals("" + cell.row) && cell.v.getClass().equals(sel.getVariant().getClass())) {
                    int start, end;
                    if (support.isDrop()) {
                        Point p = support.getDropLocation().getDropPoint();
                        start = end = cell.viewToModel(p);
                    } else {
                        start = cell.getSelectionStart();
                        end = cell.getSelectionEnd();
                    }
                    // Check to make sure we're not pasting into any tags
                    if (cell.v.containsTag(start, end - start)) {
                        return false;
                    }
                    boolean isDragMove = support.isDrop() && support.getDropAction() == TransferHandler.MOVE;
                    if (isDragMove && start > sel.getSelectionStart() && start < sel.getSelectionEnd()) {
                        // Don't remove source text if we are dropping within
                        // the initial selection.
                        shouldRemove = false;
                    }
                    cell.v.replaceSelection(start, end, sel);
                    cell.syncModelToView();
                    return true;
                } else if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    // We're not pasting from the same row so it's not safe to
                    // import tags. We can import plain text instead.
                    return importString(cell, support);
                }
            } catch (UnsupportedFlavorException | IOException e) {
                LOG.info("", e);
            }
            return false;
        }

        private boolean importString(SegmentTextEditorCell cell, TransferSupport support) {
            try {
                Transferable trfr = support.getTransferable();
                String str = trfr.getTransferData(DataFlavor.stringFlavor).toString();
                int start, end;
                if (support.isDrop()) {
                    Point p = support.getDropLocation().getDropPoint();
                    start = end = cell.viewToModel(p);
                } else {
                    start = cell.getSelectionStart();
                    end = cell.getSelectionEnd();
                }
                // Check to make sure we're not pasting into any tags
                if (cell.v.containsTag(start, end - start)) {
                    return false;
                }
                // Rely on SegmentFilter to do the dirty work.
                cell.replaceSelection(str);
                return true;
            } catch (UnsupportedFlavorException | IOException e) {
                LOG.debug("", e);
            }
            return false;
        }
    }

    static final DataFlavor SELECTION_FLAVOR = new DataFlavor(SegmentVariantSelection.class,
            SegmentVariantSelection.class.getSimpleName());

    static class SegmentVariantTransferable implements Transferable {

        private static final DataFlavor[] FLAVORS = { SELECTION_FLAVOR, DataFlavor.stringFlavor };

        private final SegmentVariant source;
        private final PositionAtom start;
        private final PositionAtom end;
        private final SegmentVariantSelection selection;

        public SegmentVariantTransferable(String id, SegmentVariant source, int start, int end) {
            this.source = source;
            this.start = source.createPosition(start);
            this.end = source.createPosition(end);
            this.selection = new SegmentVariantSelection(id, source.createCopy(), start, end);
        }

        void removeText() {
            source.clearSelection(start.getPosition(), end.getPosition());
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return FLAVORS;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return Arrays.asList(FLAVORS).contains(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (SELECTION_FLAVOR.equals(flavor)) {
                return selection;
            } else if (DataFlavor.stringFlavor.equals(flavor)) {
                return selection.getDisplayText();
            }
            throw new UnsupportedFlavorException(flavor);
        }

    }

    public boolean canStopEditing() {
        return v == null || !v.needsValidation() || v.validateAgainst(vOrig);
    }

    class ContextMenuListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doContextPopup(e.getComponent(), e.getPoint());
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doContextPopup(e.getComponent(), e.getPoint());
            }
        }

        void doContextPopup(Component c, Point p) {
            JPopupMenu menu = makeContextPopup(viewToModel(p));
            menu.show(c, p.x, p.y);
        }
    }

    JPopupMenu makeContextPopup(int insertionPoint) {
        final List<CodeAtom> missing = v.getMissingTags(vOrig);
        final int correctedPoint = v.findSelectionEnd(insertionPoint);
        JPopupMenu menu = new JPopupMenu();
        for (final CodeAtom atom : missing) {
            JMenuItem restoreOneItem = menu.add("Restore Missing Tag: " + atom.getData());
            restoreOneItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    v.replaceSelection(correctedPoint, correctedPoint, Arrays.asList(atom));
                    syncModelToView();
                }
            });
        }
        if (missing.size() != 1) {
            // Only offer Restore All if there are zero tags (to make the
            // feature more visible) or if there are multiple tags. The
            // single-tag case is handled above.
            JMenuItem restoreAllItem = menu.add("Restore All Missing Tags");
            restoreAllItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    v.replaceSelection(correctedPoint, correctedPoint, missing);
                    syncModelToView();
                }
            });
            restoreAllItem.setEnabled(!missing.isEmpty());
        }
        return menu;
    }

    void prepareEditingUI() {
        menuFrame = new JFrame();
        menuFrame.setUndecorated(true);
        menuFrame.setAlwaysOnTop(true);
        menuFrame.setAutoRequestFocus(false);
        menuFrame.setFocusable(false);
        Container c = menuFrame.getContentPane();
        c.setLayout(new BorderLayout());
        final JButton button = new JButton();
        button.setFocusable(false);
        button.setBorder(new EmptyBorder(4, 4, 4, 4));
        Toolkit kit = Toolkit.getDefaultToolkit();
        ImageIcon icon = new ImageIcon(kit.getImage(Ocelot.class.getResource("ic_settings_black_14px.png")));
        button.setIcon(icon);
        ImageIcon pressedIcon = new ImageIcon(kit.getImage(Ocelot.class.getResource("ic_settings_white_14px.png")));
        button.setPressedIcon(pressedIcon);
        c.add(button, BorderLayout.CENTER);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component c = ((Component) e.getSource());
                Point p = c.getLocation();
                p.translate(0, c.getHeight());
                makeContextPopup(getCaretPosition()).show(c, p.x, p.y);
            }
        });
        menuFrame.pack();
        addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                if (e.isTemporary()) {
                    // Loss is temporary when e.g. the program is no longer the
                    // active one.
                    menuFrame.setVisible(false);
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                setFrameLocation();
            }
        });
        addHierarchyBoundsListener(new HierarchyBoundsListener() {
            @Override
            public void ancestorMoved(HierarchyEvent e) {
                setFrameLocation();
            }

            @Override
            public void ancestorResized(HierarchyEvent e) {
                setFrameLocation();
            }
        });
    }
    
    void setFrameLocation() {
        Rectangle visible = getVisibleRect();
        boolean showFrame = isShowing() && !visible.isEmpty() && visible.y == 0
                && visible.height >= menuFrame.getHeight();
        menuFrame.setVisible(showFrame);
        if (showFrame) {
            Container parent = getParent();
            Point p = parent.getLocationOnScreen();
            p.translate(parent.getWidth() + 4, 0);
            menuFrame.setLocation(p);
        }
    }

    void closeEditingUI() {
        if (menuFrame != null) {
            menuFrame.dispose();
        }
    }
}
