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

import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.okapi.Note;
import com.vistatec.ocelot.segment.model.okapi.Notes;
import com.vistatec.ocelot.xliff.XLIFFDocument;

import net.sf.okapi.common.LocaleId;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.ItsSelectionEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.LQISelectionEvent;
import com.vistatec.ocelot.events.OcelotEditingEvent;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentNoteUpdatedEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.events.SegmentTargetEnterEvent;
import com.vistatec.ocelot.events.SegmentTargetExitEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.SegmentTargetUpdateFromMatchEvent;
import com.vistatec.ocelot.ContextMenu;
import com.vistatec.ocelot.SegmentViewColumn;
import com.vistatec.ocelot.TextContextMenu;

import static com.vistatec.ocelot.SegmentViewColumn.*;

import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.model.ITSMetadata;
import com.vistatec.ocelot.rules.DataCategoryFlag;
import com.vistatec.ocelot.rules.DataCategoryFlagRenderer;
import com.vistatec.ocelot.rules.NullITSMetadata;
import com.vistatec.ocelot.rules.SegmentSelector;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RuleListener;
import com.vistatec.ocelot.rules.StateQualifier;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.vistatec.ocelot.events.SegmentTargetUpdateEvent;

/**
 * Table view containing the source and target segments extracted from the
 * opened file. Indicates attached LTS metadata as flags.
 */
public class SegmentView extends JScrollPane implements RuleListener,
        OcelotEventQueueListener {
	private static final long serialVersionUID = 1L;

	private static Logger LOG = Logger.getLogger(SegmentView.class);

	protected SegmentTableModel segmentTableModel;
	protected JTable sourceTargetTable;
	private TableColumnModel tableColumnModel;
	protected TableRowSorter<SegmentTableModel> sort;
	private boolean enabledTargetDiff = true;

	protected RuleConfiguration ruleConfig;
	private final OcelotEventQueue eventQueue;

	private boolean targetChangedFromMatch;
	private XLIFFDocument xliff;
	private boolean isSourceBidi = false;
	private boolean isTargetBidi = false;

	private int editingRow = -1;

	/**
	 * Table implementation that recalculates row heights when doLayout()
	 * is called.
	 */
	class SegmentViewTable extends JTable {
        private static final long serialVersionUID = 1L;
        SegmentViewTable(AbstractTableModel model) {
	        super(model);
	    }
	    @Override
	    public void doLayout() {
	        long start = System.currentTimeMillis();
            for (int row = 0; row < getRowCount(); row++) {
                updateRowHeight(row, getIntercellSpacing().height);
            }
            super.doLayout();
            LOG.warn("doLayout() took " + (System.currentTimeMillis() - start) + "ms");
	    }
	}

	@Inject
	public SegmentView(OcelotEventQueue eventQueue,
	        SegmentTableModel segmentTableModel, RuleConfiguration ruleConfig)
	        throws IOException, InstantiationException, InstantiationException,
	        IllegalAccessException {
		this.eventQueue = eventQueue;
		this.segmentTableModel = segmentTableModel;
		this.ruleConfig = ruleConfig;
		this.ruleConfig.addRuleListener(this);
		UIManager.put("Table.focusCellHighlightBorder",
		        BorderFactory.createLineBorder(Color.BLUE, 2));
		initializeTable();
		eventQueue.registerListener(this);
	}

	@Subscribe
	public void openFile(OpenFileEvent e) {
		isSourceBidi = LocaleId.isBidirectional(e.getDocument().getSrcLocale());
		isTargetBidi = LocaleId.isBidirectional(e.getDocument().getTgtLocale());
		xliff = e.getDocument();
	}

	public final void initializeTable() {
		sourceTargetTable = new SegmentViewTable(segmentTableModel);
		sourceTargetTable.getTableHeader().setReorderingAllowed(false);

		ListSelectionListener selectSegmentHandler = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				selectedSegment();
			}
		};
		ListSelectionModel tableSelectionModel = sourceTargetTable
		        .getSelectionModel();
		tableSelectionModel
		        .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableSelectionModel.addListSelectionListener(selectSegmentHandler);

		sourceTargetTable.setDefaultRenderer(Integer.class,
		        new IntegerRenderer());
		sourceTargetTable.setDefaultRenderer(ITSMetadata.class,
		        new ITSMetadataRenderer());
		sourceTargetTable.setDefaultRenderer(SegmentVariant.class,
		        new SegmentTextRenderer());
		sourceTargetTable.setDefaultRenderer(String.class,
		        new NotesCellRenderer());

		// Install our custom edit behavior: hitting 'enter' anywhere inside the
		// row will open the target cell for editing. Double-clicking will also
		// work, but this is handled separately (see
		// SegmentEditor.isCellEditable()).
		InputMap inputMap = sourceTargetTable
		        .getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap actionMap = sourceTargetTable.getActionMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
		        "myStartEditing");
		final Action startEditAction = actionMap.get("startEditing");
		actionMap.put("myStartEditing", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				sourceTargetTable.changeSelection(
				        sourceTargetTable.getSelectedRow(),
				        segmentTableModel.getSegmentTargetColumnIndex(), false,
				        false);
				startEditAction.actionPerformed(e);
				// Ensure the editor is focused. XXX It's wrapped in a
				// scrollpane, so we need
				// to drill down to get the SegmentTextCell instance.
				JScrollPane scrollPane = (JScrollPane) sourceTargetTable
				        .getEditorComponent();
				if (scrollPane != null) {
					scrollPane.getViewport().getView().requestFocus();
				}
			}
		});

		tableColumnModel = sourceTargetTable.getColumnModel();
		configureColumns();
		tableColumnModel.getSelectionModel().addListSelectionListener(
		        selectSegmentHandler);
		tableColumnModel.addColumnModelListener(new TableColumnModelListener() {
			@Override
			public void columnAdded(TableColumnModelEvent tcme) {
				// Set the preferred column width when we add a column back
				// (eg, when we're changing column setup)
				int index = tcme.getToIndex();
				configureColumn(segmentTableModel.getColumn(index), index);
			}

			@Override
			public void columnRemoved(TableColumnModelEvent tcme) {}

			@Override
			public void columnMoved(TableColumnModelEvent tcme) {}

			@Override
			public void columnMarginChanged(ChangeEvent ce) {}

			@Override
			public void columnSelectionChanged(ListSelectionEvent lse) {}
		});

		addFilters();

		sourceTargetTable.addMouseListener(new SegmentPopupMenuListener());
		setViewportView(sourceTargetTable);
		addEditingListeners(sourceTargetTable);
	}

	private void configureColumns() {
		for (SegmentViewColumn col : SegmentViewColumn.values()) {
			configureColumn(col, segmentTableModel.getIndexForColumn(col));
		}
	}

	private void configureColumn(SegmentViewColumn col, int index) {
		if (!segmentTableModel.isColumnEnabled(col)) {
			return;
		}
		switch (col) {
		case SegNum:
			tableColumnModel.getColumn(index).setMinWidth(20);
			tableColumnModel.getColumn(index).setPreferredWidth(25);
			tableColumnModel.getColumn(index).setMaxWidth(50);
			break;
		case Source:
		    final TableColumn sourceCol = tableColumnModel.getColumn(index);
			sourceCol.setCellEditor(new ReadOnlySegmentEditor());
			sourceCol.setCellRenderer(new SegmentTextFontRenderer(sourceTargetTable.getFont()));
			break;
        case Target:
            final TableColumn targetCol = tableColumnModel.getColumn(index);
            targetCol.setCellRenderer(new SegmentTextFontRenderer(sourceTargetTable.getFont()));
            targetCol.setCellEditor(new SegmentEditor(sourceTargetTable.getFont()));
            break;
		case Original:
		    final TableColumn targetOriginalCol = tableColumnModel.getColumn(index);
		    targetOriginalCol.setCellRenderer(new SegmentTextFontRenderer(
		            sourceTargetTable.getFont()));
		    break;
		case EditDistance:
			tableColumnModel.getColumn(index).setMinWidth(25);
			tableColumnModel.getColumn(index).setPreferredWidth(25);
			tableColumnModel.getColumn(index).setMaxWidth(40);
			break;
		case Notes:
			tableColumnModel.getColumn(index).setCellEditor(
			        new NotesCellEditor());
			break;
		case Flag1:
		case Flag2:
		case Flag3:
		case Flag4:
		case Flag5:
			tableColumnModel.getColumn(index).setMinWidth(15);
			tableColumnModel.getColumn(index).setPreferredWidth(20);
			tableColumnModel.getColumn(index).setMaxWidth(20);
			break;
		default:
			break;
		}
	}

   private void addEditingListeners(Component component) {
        final FocusListener focusListener = new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                eventQueue.post(new OcelotEditingEvent(
                        OcelotEditingEvent.Type.STOP_EDITING));
            }

            @Override
            public void focusGained(FocusEvent e) {
                eventQueue.post(new OcelotEditingEvent(
                        OcelotEditingEvent.Type.START_EDITING));
            }
        };

        final ContainerListener containerListener = new ContainerListener() {
            @Override
            public void componentRemoved(ContainerEvent e) { }

            @Override
            public void componentAdded(ContainerEvent e) {
                addListenersToComponents(e.getChild(), focusListener, this);
            }
        };
        addListenersToComponents(component, focusListener, containerListener);
    }

    private void addListenersToComponents(Component component,
            FocusListener focusListener, ContainerListener containerListener ) {
        if (component instanceof JTextComponent) {
            component.addFocusListener(focusListener);
        } else if (component instanceof Container) {
            Container container = (Container) component;
            container.addContainerListener(containerListener);
            if (container.getComponentCount() > 0) {
                for (int i = 0; i < container.getComponentCount(); i++) {
                    addListenersToComponents(container.getComponent(i),
                            focusListener, containerListener);
                }
            }
        }
    }

	public SegmentTableModel getTableModel() {
		return segmentTableModel;
	}

	public void clearTable() {
		sourceTargetTable.clearSelection();
		sourceTargetTable.setRowSorter(null);
		setViewportView(null);
	}

	public void reloadTable() {
		clearTable();
		setViewportView(sourceTargetTable);
		addFilters();
		segmentTableModel.fireTableDataChanged();
        final TableColumn currColumn = tableColumnModel.getColumn(
                segmentTableModel.getSegmentNumColumnIndex());
        Font font = sourceTargetTable.getFont();
        if(currColumn.getCellRenderer() != null && 
                currColumn.getCellRenderer() instanceof SegmentTextFontRenderer) {
            font = ((SegmentTextFontRenderer)currColumn.getCellRenderer()).getFont();
        }
		// Adjust the segment number column width
		currColumn.setPreferredWidth(
		                this.getFontMetrics(font).stringWidth(
		                        " " + segmentTableModel.getRowCount()));
	}

	private void updateTableRow(int row) {
		segmentTableModel.fireTableRowsUpdated(row, row);
		updateRowHeight(row, sourceTargetTable.getIntercellSpacing().height);
	}

	public void requestFocusTable() {
		sourceTargetTable.requestFocus();
	}

	public void addFilters() {
		sort = new TableRowSorter<SegmentTableModel>(segmentTableModel);
		sourceTargetTable.setRowSorter(sort);
		sort.setRowFilter(new RowFilter<SegmentTableModel, Integer>() {
			private SegmentSelector selector = new SegmentSelector(ruleConfig);

			@Override
			public boolean include(
			        RowFilter.Entry<? extends SegmentTableModel, ? extends Integer> entry) {
				return selector.matches(entry.getModel().getSegment(
				        entry.getIdentifier()));
			}
		});
	}

	private void updateRowHeight(int row, int intercellHeight) {
	    if (row == editingRow) {
	        adjustEditorInitialSize(sourceTargetTable, row);
	        return;
	    }
		int modelRow = sort.convertRowIndexToModel(row);
		FontMetrics font = sourceTargetTable.getFontMetrics(sourceTargetTable
		        .getFont());
		int rowHeight = font.getHeight();
		rowHeight = getColumnHeight(SegNum, row, "1", rowHeight, sourceTargetTable.getFont());
        OcelotSegment segment = segmentTableModel.getSegment(modelRow);
        Font sourceFont = getFontForColumn(Source);
		rowHeight = getColumnHeight(Source, row,
		        segment.getSource().getDisplayText(), rowHeight, sourceFont);
		Font targetFont = getFontForColumn(Target);
		rowHeight = getColumnHeight(Target, row,
		        segment.getTarget().getDisplayText(), rowHeight, targetFont);
		rowHeight = getColumnHeight(Original, row,
		        getOriginalTargetText(modelRow), rowHeight, targetFont);
	    sourceTargetTable.setRowHeight(row, rowHeight + intercellHeight);
	}

    private void adjustEditorInitialSize(JTable jtable, int row) {
        final FontMetrics defFontMetrics = sourceTargetTable
                .getFontMetrics(sourceTargetTable.getFont());
        final int defFontHeight = defFontMetrics.getHeight();
        final FontMetrics sourceFontMetrics = sourceTargetTable
                .getFontMetrics(((SegmentTextFontRenderer) sourceTargetTable
                        .getColumnModel()
                        .getColumn(
                                segmentTableModel
                                        .getSegmentSourceColumnIndex())
                        .getCellRenderer()).getFont());
        final int sourceFontHeight = sourceFontMetrics.getHeight();
        final FontMetrics targetFontMetrics = sourceTargetTable
                .getFontMetrics(((SegmentTextFontRenderer) sourceTargetTable
                        .getColumnModel()
                        .getColumn(
                                segmentTableModel
                                        .getSegmentTargetColumnIndex())
                        .getCellRenderer()).getFont());
        final int targetFontHeight = targetFontMetrics.getHeight();
        int height = Math.max(defFontHeight, sourceFontHeight);
        height = Math.max(height, targetFontHeight);
        jtable.setRowHeight(row, height * 10);
    }

    private Font getFontForColumn(SegmentViewColumn col) {
        return ((SegmentTextFontRenderer)tableColumnModel.getColumn(col.ordinal()).getCellRenderer()).getFont();
    }

	// TODO: move elsewhere
	private String getOriginalTargetText(int modelRow) {
		if (enabledTargetDiff) {
			List<String> textDiff = segmentTableModel.getSegment(modelRow)
			        .getTargetDiff();
			StringBuilder displayText = new StringBuilder();
			for (int i = 0; i < textDiff.size(); i += 2) {
				displayText.append(textDiff.get(i));
			}
			return displayText.toString();
		} else {
			return segmentTableModel.getSegment(modelRow).getOriginalTarget()
			        .getDisplayText();
		}
	}

	private int getColumnHeight(SegmentViewColumn colData, int viewRow,
	        String text, int previousHeight, Font font) {
		if (!segmentTableModel.isColumnEnabled(colData)) {
			return previousHeight;
		}
		SegmentTextCell segmentCell = SegmentTextCell.createDummyCell();
		segmentCell.setBorder(UIManager
		        .getBorder("Table.focusCellHighlightBorder"));
		segmentCell.setFont(font);
		int col = segmentTableModel.getIndexForColumn(colData);
		int width = sourceTargetTable.getColumnModel().getColumn(col)
		        .getWidth();
		segmentCell.setText(text);
		// Need to set width to force text area to calculate a pref height
		segmentCell.setSize(new Dimension(width, sourceTargetTable
		        .getRowHeight(viewRow)));
		return Math.max(previousHeight, segmentCell.getPreferredSize().height);
	}

	public OcelotSegment getSelectedSegment() {
		OcelotSegment selectedSeg = null;
		if (sourceTargetTable.getSelectedRow() >= 0) {
			selectedSeg = segmentTableModel
			        .getSegment(sort.convertRowIndexToModel(sourceTargetTable
			                .getSelectedRow()));
		}
		return selectedSeg;
	}

	public void selectedSegment() {
		OcelotSegment seg = getSelectedSegment();
		if (seg != null) {
			int colIndex = sourceTargetTable.getSelectedColumn();
			SegmentViewColumn col = segmentTableModel.getColumn(colIndex);
			if (col != null && col.isFlagColumn()) {
				ITSMetadata its = ruleConfig.getTopDataCategory(seg,
				        col.getFlagIndex());
				if (its != null) {
					eventQueue.post(new ItsSelectionEvent(its));
				}
			}
		}
		postSegmentSelection(seg);
	}

	public boolean getEnabledTargetDiff() {
		return enabledTargetDiff;
	}

	public void setEnabledTargetDiff(boolean enabled) {
		this.enabledTargetDiff = enabled;
		reloadTable();
	}

    public Font getSourceFont() {

        Font font = null;
        if (sourceTargetTable != null) {
            font = ((SegmentTextFontRenderer) sourceTargetTable
                    .getColumnModel()
                    .getColumn(segmentTableModel.getSegmentSourceColumnIndex())
                    .getCellRenderer()).getFont();
        }
        return font;
    }

    public Font getTargetFont() {

        Font font = null;
        if (sourceTargetTable != null) {
            font = ((SegmentTextFontRenderer) sourceTargetTable
                    .getColumnModel()
                    .getColumn(segmentTableModel.getSegmentTargetColumnIndex())
                    .getCellRenderer()).getFont();
        }
        return font;
    }

    public void setSourceFont(final Font font) {

        if (sourceTargetTable != null) {
            ((SegmentTextFontRenderer) sourceTargetTable.getColumnModel()
                    .getColumn(segmentTableModel.getSegmentSourceColumnIndex())
                    .getCellRenderer()).setFont(font);
            sourceTargetTable.revalidate();
            sourceTargetTable.repaint();
            segmentTableModel.fireTableDataChanged();
        }
    }

    public void setTargetFont(final Font font) {

        if (sourceTargetTable != null) {
            ((SegmentTextFontRenderer) sourceTargetTable.getColumnModel()
                    .getColumn(segmentTableModel.getSegmentTargetColumnIndex())
                    .getCellRenderer()).setFont(font);
            ((SegmentTextFontRenderer) sourceTargetTable
                    .getColumnModel()
                    .getColumn(
                            segmentTableModel
                                    .getSegmentTargetOriginalColumnIndex())
                    .getCellRenderer()).setFont(font);
            ((SegmentEditor) sourceTargetTable.getColumnModel()
                    .getColumn(segmentTableModel.getSegmentTargetColumnIndex())
                    .getCellEditor()).setFont(font);
            segmentTableModel.fireTableDataChanged();
        }
    }

	@Subscribe
	public void notifyModifiedLQI(LQIModificationEvent event) {
		int selectedRow = sourceTargetTable.getSelectedRow();
		updateTableRow(selectedRow);
		sourceTargetTable.setRowSelectionInterval(selectedRow, selectedRow);
		eventQueue.post(new LQISelectionEvent(event.getLQI()));
		postSegmentSelection(event.getSegment());
		requestFocusTable();
	}

	@Subscribe
	public void notifySegmentTargetReset(SegmentTargetResetEvent event) {
		segmentTableModel.fireTableDataChanged();
	}

	@Subscribe
	public synchronized void handleTargetUpdatedFromMatch(
	        SegmentTargetUpdateFromMatchEvent event) {
		// int selRow = sourceTargetTable.getSelectedRow();
		// // segmentTableModel.fireTableRowsUpdated(selRow, selRow);
		// updateTableRow(selRow);
		// sourceTargetTable.requestFocusInWindow();
		targetChangedFromMatch = true;
	}

	@Subscribe
	public synchronized void handleTargetUpdatedFromMatch(SegmentEditEvent event) {
		if (targetChangedFromMatch) {
			int selRow = sourceTargetTable.getSelectedRow();
			// segmentTableModel.fireTableRowsUpdated(selRow, selRow);
			updateTableRow(selRow);
			sourceTargetTable.requestFocusInWindow();
			targetChangedFromMatch = false;
		}
	}

	@Override
	public void enabledRule(String ruleLabel, boolean enabled) {
		reloadTable();
	}

	@Override
	public void setFilterMode(RuleConfiguration.FilterMode mode) {
		reloadTable();
	}

	@Override
	public void setStateQualifierMode(RuleConfiguration.StateQualifierMode mode) {
		reloadTable();
	}

	private void postSegmentSelection(OcelotSegment seg) {
		if (seg != null) {
			eventQueue.post(new SegmentSelectionEvent(xliff, seg));
		}
	}

	/**
	 * TableCellRenderer for source/target text in the SegmentTableView.
	 */
	public class SegmentTextRenderer implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object o,
		        boolean isSelected, boolean hasFocus, int row, int col) {
			SegmentTextCell renderTextPane = SegmentTextCell.createCell();
			if (segmentTableModel.getRowCount() > row) {
				OcelotSegment seg = segmentTableModel.getSegment(sort
				        .convertRowIndexToModel(row));
				SegmentVariant v = null;
				if (segmentTableModel.getSegmentSourceColumnIndex() == col) {
					v = seg.getSource();
					renderTextPane.setBidi(isSourceBidi);
				} else if (segmentTableModel.getSegmentTargetColumnIndex() == col) {
					v = seg.getTarget();
					renderTextPane.setBidi(isTargetBidi);
				} else if (segmentTableModel
				        .getSegmentTargetOriginalColumnIndex() == col) {
                    renderTextPane.setBidi(isTargetBidi);
					if (!enabledTargetDiff) {
						v = seg.getOriginalTarget();
					}
				}
				if (v != null) {
					renderTextPane.setVariant(v, false);
				} else {
					renderTextPane.setTargetDiff(seg.getTargetDiff());
				}
				Color background = isSelected ? seg.isEditable() ? jtable
				        .getSelectionBackground() : Color.LIGHT_GRAY : jtable
				        .getBackground();

				Color foreground = seg.isEditable() ? isSelected ? jtable
				        .getSelectionForeground() : jtable.getForeground()
				        : Color.GRAY;

				renderTextPane.setBackground(background);
				renderTextPane.setForeground(foreground);
				renderTextPane.setBorder(hasFocus ? UIManager
				        .getBorder("Table.focusCellHighlightBorder") : jtable
				        .getBorder());
			}

			return renderTextPane;
		}
	}

    public class SegmentTextFontRenderer extends SegmentTextRenderer {

        private Font font;

        public SegmentTextFontRenderer(final Font font) {

            this.font = font;
        }

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o,
                boolean isSelected, boolean hasFocus, int row, int col) {

            Component component = super.getTableCellRendererComponent(jtable,
                    o, isSelected, hasFocus, row, col);

            component.setFont(font);
            return component;
        }

        public Font getFont() {
            return font;
        }

        public void setFont(final Font font) {
            this.font = font;
        }

    }

	static class ITSMetadataRenderer extends DataCategoryFlagRenderer {
		private static final long serialVersionUID = 1L;

		public ITSMetadataRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable,
		        Object obj, boolean isSelected, boolean hasFocus, int row,
		        int col) {
			ITSMetadata its = (obj != null) ? ((ITSMetadata) obj)
			        : NullITSMetadata.getInstance();
			DataCategoryFlag flag = its.getFlag();
			flag = (flag != null) ? flag : DataCategoryFlag.getDefault();
			return super.getTableCellRendererComponent(jtable, flag,
			        isSelected, hasFocus, row, col);
		}
	}

	public class IntegerRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public IntegerRenderer() {
			setOpaque(true);
		}

		private Color getSegmentColor(OcelotSegment seg) {
			StateQualifier sq = seg.getStateQualifier();
			if (sq != null) {
				Color sqColor = ruleConfig.getStateQualifierColor(sq);
				if (sqColor == null) {
					LOG.debug("No UI color for state-qualifier '" + sq + "'");
				}
				return sqColor;
			}
			return null;
		}

		@Override
		public Component getTableCellRendererComponent(JTable jtable,
		        Object obj, boolean isSelected, boolean hasFocus, int row,
		        int col) {
			Integer segNum = (Integer) obj;
			OcelotSegment seg = segmentTableModel.getSegment(sort
			        .convertRowIndexToModel(row));

			Color background = getSegmentColor(seg);
			background = background != null ? background : isSelected ? seg
			        .isEditable() ? jtable.getSelectionBackground()
			        : Color.LIGHT_GRAY : jtable.getBackground();

			Color foreground = seg.isEditable() ? isSelected ? jtable
			        .getSelectionForeground() : jtable.getForeground()
			        : Color.GRAY;

			setHorizontalAlignment(JLabel.LEFT);
			setVerticalAlignment(JLabel.TOP);
			setBackground(background);
			setForeground(foreground);
			setBorder(hasFocus ? UIManager
			        .getBorder("Table.focusCellHighlightBorder") : jtable
			        .getBorder());
			if (segNum != null) {
				setText(segNum.toString());
			}
			return this;
		}
	}

	public class ReadOnlySegmentEditor extends AbstractCellEditor implements
	        TableCellEditor {
		private static final long serialVersionUID = -591391978033697647L;

		private SegmentTextCell editorComponent;

		@Override
		public Object getCellEditorValue() {

			return editorComponent.getVariant();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
		        Object value, boolean isSelected, int row, int column) {
			OcelotSegment seg = segmentTableModel.getSegment(sort
			        .convertRowIndexToModel(row));
			editorComponent = SegmentTextCell.createCell(seg.getSource().createCopy(),
			        false, isSourceBidi);
			editorComponent.setBackground(table.getSelectionBackground());
			editorComponent.setSelectionColor(Color.BLUE);
			editorComponent.setSelectedTextColor(Color.WHITE);
			editorComponent.setEditable(false);
			editorComponent.getCaret().setVisible(true);
			editorComponent.addMouseListener(new TextPopupMenuListener());
			editorComponent.getInputMap().put(
			        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "finish");
			editorComponent.getActionMap().put("finish", new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});

			return editorComponent;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			if (anEvent instanceof MouseEvent) {
				// Override normal behavior and only allow double-click to edit
				// the
				// cell
				return ((MouseEvent) anEvent).getClickCount() >= 2;
			}
			if (anEvent instanceof ActionEvent) {
				return true;
			}
			return false;
		}

	}

	public class SegmentEditor extends AbstractCellEditor implements
	        TableCellEditor {
		private static final long serialVersionUID = 1L;

		protected SegmentTextCell editorComponent;
		protected SegmentCellEditorListener editListener;
        private Font font;

		public SegmentEditor(final Font font) {
			editListener = new SegmentCellEditorListener();
			addCellEditorListener(editListener);
			this.font = font;
		}

		@Override
		public Component getTableCellEditorComponent(JTable jtable,
		        Object value, boolean isSelected, int row, int col) {
			OcelotSegment seg = segmentTableModel.getSegment(sort
			        .convertRowIndexToModel(row));
			if (col == segmentTableModel.getSegmentSourceColumnIndex()) {
				editorComponent = SegmentTextCell.createCell(seg.getSource()
				        .createCopy(), false, isSourceBidi);
				editorComponent.setEditable(false);

			} else if (col == segmentTableModel.getSegmentTargetColumnIndex()) {
				editListener
				        .setBeginEdit(seg, seg.getTarget().getDisplayText());
				editorComponent = SegmentTextCell.createCell(seg.getTarget()
				        .createCopy(), false, isTargetBidi);
				editorComponent.getInputMap().put(
				        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "finish");
				editorComponent.getActionMap().put("finish",
				        new AbstractAction() {
					        private static final long serialVersionUID = 1L;

					        @Override
					        public void actionPerformed(ActionEvent e) {
				                fireEditingStopped();
						        eventQueue.post(new OcelotEditingEvent(
						                OcelotEditingEvent.Type.STOP_EDITING));
					        }
				        });
				editingRow = row;
				editorComponent.setFont(font);
			}
			eventQueue.post(new OcelotEditingEvent(
			        OcelotEditingEvent.Type.START_EDITING));
			return new JScrollPane(editorComponent);
		}

        public void setFont(final Font font) {
            this.font = font;
        }

		@Override
		public Object getCellEditorValue() {
			return editorComponent.getVariant();
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			if (anEvent instanceof MouseEvent) {
				// Override normal behavior and only allow double-click to edit
				// the
				// cell
				return ((MouseEvent) anEvent).getClickCount() >= 2;
			}
			if (anEvent instanceof ActionEvent) {
				// I could further restrict this to "startEditing" event,
				// but I don't think it's necessary
				return true;
			}
			return false;
		}

	}

	public class SegmentCellEditorListener implements CellEditorListener {
		private OcelotSegment seg;

		public void setBeginEdit(OcelotSegment seg, String codedText) {
			this.seg = seg;
			eventQueue.post(new SegmentTargetEnterEvent(xliff, seg));
		}

		@Override
		public void editingStopped(ChangeEvent ce) {
			SegmentVariant updatedTarget = ((SegmentEditor) ce.getSource()).editorComponent
			        .getVariant();
			int row = sourceTargetTable.getSelectedRow();
			eventQueue.post(new SegmentTargetExitEvent(xliff, seg));
			eventQueue.post(new SegmentTargetUpdateEvent(xliff, seg, updatedTarget));
			postSegmentSelection(seg);
			editingRow = -1;
			updateTableRow(row);
			// Restore row selection
			sourceTargetTable.setRowSelectionInterval(row, row);
		}

		@Override
		public void editingCanceled(ChangeEvent ce) {
			// Cancel not supported.
		}
	}

	public class NotesCellRenderer implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
		        Object value, boolean isSelected, boolean hasFocus, int row,
		        int column) {

			JTextArea txtArea = new JTextArea();
			if (value != null && value instanceof Notes) {
				Note ocelotNote = ((Notes)value).getOcelotNote();
				if(ocelotNote != null){
					txtArea.setText(ocelotNote.getContent());
				}
			}
			if (isSelected) {
				txtArea.setBackground(table.getSelectionBackground());
			} else {
				txtArea.setBackground(table.getBackground());
			}
			txtArea.setWrapStyleWord(true);
			txtArea.setLineWrap(true);
			JScrollPane pane = new JScrollPane(txtArea);
			pane.setBorder(BorderFactory.createEmptyBorder());
			pane.setSize(new Dimension(table.getColumnModel().getColumn(column)
			        .getWidth(), table.getRowHeight(row)));
			pane.setBorder(hasFocus ? UIManager
			        .getBorder("Table.focusCellHighlightBorder") : table
			        .getBorder());
			return pane;
		}

	}

	public class NotesCellEditor extends AbstractCellEditor implements
	        TableCellEditor {
		
        private static final long serialVersionUID = 1L;

        private JTextArea txtArea;
		
		private NotesCellEditorListener listener;

		public NotesCellEditor() {
	     
			listener = new NotesCellEditorListener();
			addCellEditorListener(listener);
        }

		@Override
		public Object getCellEditorValue() {
			return txtArea.getText();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
		        Object value, boolean isSelected, int row, int column) {

			OcelotSegment seg = segmentTableModel.getSegment(sort
			        .convertRowIndexToModel(row));
			listener.beginEditSegment(seg);
			txtArea = new JTextArea();
			if (value != null && value instanceof Notes) {
				Note ocelotNote = ((Notes)value).getOcelotNote();
				if(ocelotNote != null){
					txtArea.setText(ocelotNote.getContent());
				}
			}
			if (isSelected) {
				txtArea.setBackground(table.getSelectionBackground());
			} else {
				txtArea.setBackground(table.getBackground());
			}
			txtArea.setWrapStyleWord(true);
			txtArea.setLineWrap(true);
			txtArea.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						stopCellEditing();
						
					}
				}
			});
			JScrollPane scrollPane = new JScrollPane(txtArea);
			scrollPane.setPreferredSize(new Dimension(table.getColumnModel()
			        .getColumn(column).getWidth(), table.getRowHeight(row)));
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			eventQueue.post(new OcelotEditingEvent(
			        OcelotEditingEvent.Type.START_EDITING));
			return scrollPane;
		}

		@Override
		public boolean isCellEditable(EventObject e) {

			return e instanceof MouseEvent
			        && ((MouseEvent) e).getButton() == MouseEvent.BUTTON1
			        && ((MouseEvent) e).getClickCount() >= 2;
		}

		@Override
		public boolean stopCellEditing() {
			eventQueue.post(new OcelotEditingEvent(
			        OcelotEditingEvent.Type.STOP_EDITING));
		    return super.stopCellEditing();
		}
	}
	
	public class NotesCellEditorListener implements CellEditorListener {
	
		private OcelotSegment seg;

		public void beginEditSegment(OcelotSegment seg){
			this.seg = seg;
		}
		
		@Override
		public void editingStopped(ChangeEvent ce) {

			int row = sourceTargetTable.getSelectedRow();
			String noteContent = ((NotesCellEditor)ce.getSource()).txtArea.getText();
			eventQueue.post(new SegmentNoteUpdatedEvent(xliff, seg, noteContent));
			updateTableRow(row);
			// Restore row selection
			sourceTargetTable.setRowSelectionInterval(row, row);
//			SegmentVariant updatedTarget = ((SegmentEditor) ce.getSource()).editorComponent
//			        .getVariant();
//			int row = sourceTargetTable.getSelectedRow();
//			eventQueue.post(new SegmentTargetExitEvent(seg));
//			eventQueue.post(new SegmentTargetUpdateEvent(seg, updatedTarget));
//			postSegmentSelection(seg);
//			updateTableRow(row);
//			// Restore row selection
//			sourceTargetTable.setRowSelectionInterval(row, row);
		}

		@Override
		public void editingCanceled(ChangeEvent ce) {
			// Cancel not supported.
		}
	}

	public class TextPopupMenuListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.isPopupTrigger()) {
				displayTextPopupMenu(e);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				displayTextPopupMenu(e);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				displayTextPopupMenu(e);
			}
		}

		private void displayTextPopupMenu(MouseEvent e) {

			SegmentTextCell sourceCell = (SegmentTextCell) e.getSource();
			if (sourceCell.getSelectedText() != null) {
				try {
					Rectangle markRect = sourceCell.modelToView(sourceCell
					        .getCaret().getMark());
					Rectangle dotRect = sourceCell.modelToView(sourceCell
					        .getCaret().getDot());
					Rectangle rect = markRect.union(dotRect);
					if (rect.contains(e.getPoint())) {
						TextContextMenu ctxMenu = new TextContextMenu(
						        eventQueue, sourceCell.getSelectedText());
						ctxMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

	public class SegmentPopupMenuListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				displayContextMenu(e);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				displayContextMenu(e);
			}
		}

		public void displayContextMenu(MouseEvent e) {

			OcelotSegment seg = null;
			int r = sourceTargetTable.rowAtPoint(e.getPoint());
			if (r >= 0 && r < sourceTargetTable.getRowCount()) {
				sourceTargetTable.setRowSelectionInterval(r, r);
				seg = segmentTableModel.getSegment(r);

			}

			if (seg != null) {
				ContextMenu menu = new ContextMenu(xliff, seg, eventQueue);
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	public JTable getTable() {
		return sourceTargetTable;
	}
}
