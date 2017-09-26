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

import static com.vistatec.ocelot.SegmentViewColumn.Original;
import static com.vistatec.ocelot.SegmentViewColumn.SegNum;
import static com.vistatec.ocelot.SegmentViewColumn.Source;
import static com.vistatec.ocelot.SegmentViewColumn.Target;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.HeadlessException;
import java.awt.Point;
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
import java.awt.print.PrinterException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.print.PrintService;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vistatec.ocelot.ContextMenu;
import com.vistatec.ocelot.OcelotApp;
import com.vistatec.ocelot.SegmentViewColumn;
import com.vistatec.ocelot.TextContextMenu;
import com.vistatec.ocelot.config.LqiJsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.events.GoToSegmentEvent;
import com.vistatec.ocelot.events.HighlightEvent;
import com.vistatec.ocelot.events.ItsSelectionEvent;
import com.vistatec.ocelot.events.LQIModificationEvent;
import com.vistatec.ocelot.events.LQISelectionEvent;
import com.vistatec.ocelot.events.OcelotEditingEvent;
import com.vistatec.ocelot.events.OpenFileEvent;
import com.vistatec.ocelot.events.RefreshSegmentView;
import com.vistatec.ocelot.events.ReplaceDoneEvent;
import com.vistatec.ocelot.events.ReplaceEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentNoteUpdatedEvent;
import com.vistatec.ocelot.events.SegmentRowsSortedEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.events.SegmentTargetEditEvent;
import com.vistatec.ocelot.events.SegmentTargetEnterEvent;
import com.vistatec.ocelot.events.SegmentTargetExitEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.SegmentTargetUpdateEvent;
import com.vistatec.ocelot.events.SegmentTargetUpdateFromMatchEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.findrep.FindResult;
import com.vistatec.ocelot.its.model.ITSMetadata;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.lqi.model.LQIGridConfigurations;
import com.vistatec.ocelot.rules.DataCategoryFlag;
import com.vistatec.ocelot.rules.DataCategoryFlagRenderer;
import com.vistatec.ocelot.rules.NullITSMetadata;
import com.vistatec.ocelot.rules.RuleConfiguration;
import com.vistatec.ocelot.rules.RuleListener;
import com.vistatec.ocelot.rules.SegmentSelector;
import com.vistatec.ocelot.rules.StateQualifier;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.HighlightData;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.segment.model.okapi.FragmentVariant;
import com.vistatec.ocelot.segment.model.okapi.Note;
import com.vistatec.ocelot.segment.model.okapi.Notes;
import com.vistatec.ocelot.xliff.XLIFFDocument;

import net.sf.okapi.common.LocaleId;

/**
 * Table view containing the source and target segments extracted from the
 * opened file. Indicates attached LTS metadata as flags.
 */
public class SegmentView extends JScrollPane implements RuleListener,
        OcelotEventQueueListener {
	private static final long serialVersionUID = 1L;

	private static Logger LOG = LoggerFactory.getLogger(SegmentView.class);

    private static final String TAG_VALIDATION_ERROR_MESSAGE = "This segment's tags are in an "
            + "inconsistent state and can't be saved. Do you want to discard your work, or keep editing?";
    private static final String TAG_VALIDATION_ERROR_TITLE = "Tag Error";
    private static final String TAG_VALIDATION_BUTTON_CONTINUE = "Continue";
    private static final String TAG_VALIDATION_BUTTON_DISCARD = "Discard";

	protected SegmentTableModel segmentTableModel;
	protected SegmentViewTable sourceTargetTable;
	private TableColumnModel tableColumnModel;
	protected SegmentTableSorter sort;
	private boolean enabledTargetDiff = true;
	private OcelotApp ocelotApp;

	protected RuleConfiguration ruleConfig;
	private boolean showNotTransSegments;
	private final OcelotEventQueue eventQueue;

	private boolean targetChangedFromMatch;
        private LQIGridConfigurations lqiGrid;


	private XLIFFDocument xliff;
	private boolean isSourceBidi = false;
	private boolean isTargetBidi = false;

	private int editingRow = -1;

        private List<Integer> highlightedSegments;
	private BaseSegmentVariant currHLVariant;

	/**
	 * Table implementation that recalculates row heights when doLayout() is
	 * called. To try to minimize redraw time, we avoid recalculating the whole
	 * table unless recalculateAllRowHeights() has been called since the last
	 * call to doLayout().
	 */
	class SegmentViewTable extends JTable {
		private static final long serialVersionUID = 1L;
		private boolean requireFullRecalc = false;

		SegmentViewTable(AbstractTableModel model) {
			super(model);
		}

		@Override
		public void doLayout() {
			long start = System.currentTimeMillis();
			// The call to super will trigger any necessary recalculation of
			// margin sizes. We do this first to avoid rendering and then
			// immediately re-rendering with the updated column sizes (which
			// produces a noticeable UI flicker.)
			super.doLayout();
			int updatedRowCount = 0;
			for (int row = 0; row < getRowCount(); row++) {
				if (requireFullRecalc || editingRow == row) {
                    updateRowHeight(row, rowMargin);
					updatedRowCount++;
				}
			}
			requireFullRecalc = false;
			LOG.trace("doLayout() took {} ms for {} rows", (System.currentTimeMillis() - start), updatedRowCount);
		}

		// Dirty the whole layout
		public void recalculateAllRowHeights() {
			this.requireFullRecalc = true;
		}

        @Override
        public void columnMoved(TableColumnModelEvent e) {
            // Unilaterally cancel editing here; see SegmentEditor.stopCellEditing()
            if (isEditing()) {
                getCellEditor().cancelCellEditing();
            }
            super.columnMoved(e);
        }

        @Override
        public void columnMarginChanged(ChangeEvent e) {
            // Unilaterally cancel editing here; see SegmentEditor.stopCellEditing()
            if (isEditing()) {
                getCellEditor().cancelCellEditing();
            }
            super.columnMarginChanged(e);
        }

        @Override
        public boolean print(PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat,
                boolean showPrintDialog, PrintRequestAttributeSet attr, boolean interactive, PrintService service)
                throws PrinterException, HeadlessException {
            // Unilaterally cancel editing here; see SegmentEditor.stopCellEditing()
            if (isEditing()) {
                getCellEditor().cancelCellEditing();
            }
            return super.print(printMode, headerFormat, footerFormat, showPrintDialog, attr, interactive, service);
        }
        
		@Override
		public String getToolTipText(MouseEvent event) {
			int row = rowAtPoint(event.getPoint());
			int col = columnAtPoint(event.getPoint());
			Object o = getValueAt(row, col);
			if (o instanceof SegmentVariant) {
				// Table cells don't exist as components so we must materialize
				// an actual component in order to figure out where in the text
				// we are pointing.
				TableCellRenderer renderer = getCellRenderer(row, col);
				Component comp = renderer.getTableCellRendererComponent(this, o, false, false, row, col);
				if (comp instanceof SegmentTextCell) {
					SegmentTextCell cell = (SegmentTextCell) comp;
					// Set the bounds of the component to match where the table
					// cell was painted.
					Rectangle rect = getCellRect(row, col, true);
					cell.setBounds(rect);
					Point p = event.getPoint();
					// Convert from table coordinates to cell coordinates.
					p.translate(-rect.x, -rect.y);
					String tip = cell.getToolTipText(p);
					if (tip != null) {
						return tip;
					}
				}
			}
			return super.getToolTipText(event);
		}
	}

	@Inject
	public SegmentView(OcelotEventQueue eventQueue,
	        SegmentTableModel segmentTableModel, RuleConfiguration ruleConfig,
	        OcelotApp ocelotApp, LqiJsonConfigService lqiService) throws IOException, InstantiationException,
	        InstantiationException, IllegalAccessException, TransferException {
		this.eventQueue = eventQueue;
		this.segmentTableModel = segmentTableModel;
		this.ruleConfig = ruleConfig;
		this.ruleConfig.addRuleListener(this);
                this.lqiGrid = lqiService.readLQIConfig();
		this.ocelotApp = ocelotApp;
		UIManager.put("Table.focusCellHighlightBorder",
		        BorderFactory.createLineBorder(Color.BLUE, 2));
		initializeTable();
		eventQueue.registerListener(this);
		this.highlightedSegments = new ArrayList<Integer>();
	}

	@Subscribe
	public void onGoToSegmentEvent(GoToSegmentEvent event) {

		int tableIndex = segmentTableModel.getModelIndexBySegmentNumber(event.getSegmentNuber());
		int viewIndex = sort.convertRowIndexToView(tableIndex);
		sourceTargetTable.getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
		Rectangle segmentRect = sourceTargetTable.getCellRect(viewIndex,
				segmentTableModel.getSegmentTargetColumnIndex(), true);
		sourceTargetTable.scrollRectToVisible(segmentRect);
	}
	
	@Subscribe
	public void openFile(OpenFileEvent e) {
		isSourceBidi = LocaleId.isBidirectional(e.getDocument().getSrcLocale());
		isTargetBidi = LocaleId.isBidirectional(e.getDocument().getTgtLocale());
		xliff = e.getDocument();
	}

	@Subscribe
	public void highlightStrings(HighlightEvent e) {

		clearHighlightedSegments();
		List<FindResult> highlightResults = e.getHighlightDataList();
		if (highlightResults != null) {
			FindResult hr = null;
			int currSegmentIdx = -1;
			for (int i = 0; i < highlightResults.size(); i++) {
				hr = highlightResults.get(i);
				if (hr.getSegmentIndex() < segmentTableModel.getRowCount()) {
					OcelotSegment segment = segmentTableModel.getSegment(sort.convertRowIndexToModel(hr.getSegmentIndex()));
					BaseSegmentVariant variant = null;
					if (hr.isTargetScope()) {
						variant = (BaseSegmentVariant) segment.getTarget();
					} else {
						variant = (BaseSegmentVariant) segment.getSource();
					}
					if (variant != null) {
						HighlightData hlData = new HighlightData(
								hr.getAtomIndex(), new int[] {
										hr.getStringStartIndex(),
										hr.getStringEndIndex() });
						variant.addHighlightData(hlData);
						if (i == e.getCurrResultIndex()) {
							variant.setCurrentHighlightedIndex(variant
									.getHighlightDataList().indexOf(hlData));
							currHLVariant = variant;
							currSegmentIdx = hr.getSegmentIndex();
						}
						highlightedSegments.add(hr.getSegmentIndex());
						updateTableRow(hr.getSegmentIndex());
					}
				}
			}
			sourceTargetTable.scrollRectToVisible(sourceTargetTable
					.getCellRect(currSegmentIdx, 0, false));
		}

	}

	
	private void clearHighlightedSegments() {

		if (!highlightedSegments.isEmpty()) {
			OcelotSegment hlSegment = null;
			for (int segIndex : highlightedSegments) {
				if (segIndex < segmentTableModel.getRowCount()) {
					hlSegment = segmentTableModel.getSegment(sort.convertRowIndexToModel(segIndex));
					((BaseSegmentVariant) hlSegment.getSource())
							.clearHighlightedText();
					if (hlSegment.getTarget() != null) {
						((BaseSegmentVariant) hlSegment.getTarget())
								.clearHighlightedText();
					}
					updateTableRow(segIndex);
				}
			}
			highlightedSegments.clear();
		}
	}

	@Subscribe
	public void replace(ReplaceEvent e) {

		if (e.getAction() == ReplaceEvent.REPLACE && currHLVariant != null) {
            replaceTarget(e.getOldString(), e.getNewString(), e.getSegmentIndex());
		} else if (e.getAction() == ReplaceEvent.REPLACE_ALL) {

			OcelotSegment segment = null;
			int replacedOccNum = 0;
			for (int segIdx = 0; segIdx < segmentTableModel.getRowCount(); segIdx++) {

				segment = segmentTableModel.getSegment(segIdx);
				if(segment.isTranslatable() && segment.isEditable()){
                        replacedOccNum += replaceAllTarget((BaseSegmentVariant) segment.getTarget(), e.getOldString(),
                                e.getNewString(), segIdx);
				}
			}
			eventQueue.post(new ReplaceDoneEvent(replacedOccNum));
		}
	}

    private int replaceAllTarget(BaseSegmentVariant target, String oldString, String newString,
			int segIdx) {
		
		SegmentVariant updatedTarget = target.createCopy();
		int replacedOccNum = 0;
        List<HighlightData> hds = target.getHighlightDataList();
        if (hds != null) {
            // Iterate backwards because otherwise indices will be off for
            // subsequent replacements
            for (int i = hds.size() - 1; i >= 0; i--) {
                HighlightData hd = hds.get(i);
                String currText = target.getAtoms().get(hd.getAtomIndex()).getData()
                        .substring(hd.getHighlightIndices()[0], hd.getHighlightIndices()[1]);
                if (currText.equals(oldString)) {
                    int startOffset = 0;
                    for (int j = 0; j < hd.getAtomIndex(); j++) {
                        startOffset += target.getAtoms().get(j).getLength();
                    }
                    startOffset += hd.getHighlightIndices()[0];
                    int oldStringLength = hd.getHighlightIndices()[1] - hd.getHighlightIndices()[0];

                    updatedTarget.modifyChars(startOffset, oldStringLength, newString);
                    replacedOccNum++;
                }
			}
			target.clearHighlightedText();
			eventQueue.post(new SegmentTargetUpdateEvent(xliff,
					segmentTableModel.getSegment(segIdx),
					updatedTarget));
			updateTableRow(segIdx);
			
		}
		return replacedOccNum;
	}

	private void replaceTarget(String oldString, String newString, int segmentIndex) {
		
		OcelotSegment segment = segmentTableModel.getSegment(sort.convertRowIndexToModel(segmentIndex));
		if(segment.isTranslatable() && segment.isEditable()){
			SegmentVariant updatedTarget = currHLVariant.createCopy();
			if (currHLVariant.getHighlightDataList() != null
					&& currHLVariant.getCurrentHighlightedIndex() > -1
					&& currHLVariant.getCurrentHighlightedIndex() < currHLVariant
							.getHighlightDataList().size()) {
	
				if(updatedTarget instanceof FragmentVariant){
                    replaceTargetTextXliff20((FragmentVariant) updatedTarget, oldString, newString);
				} else {
                    replaceTargetTextXliff12((BaseSegmentVariant) updatedTarget, oldString, newString);
				}
				eventQueue.post(new SegmentTargetUpdateEvent(xliff,
						segment,
						updatedTarget));
				updateTableRow(segmentIndex);
				
			}
		}
	}

    private void replaceTargetTextXliff12(BaseSegmentVariant target, String oldString, String replaceString) {

		HighlightData hd = currHLVariant.getHighlightDataList().get(
				currHLVariant.getCurrentHighlightedIndex());
        String currText = currHLVariant.getAtoms().get(hd.getAtomIndex()).getData()
                .substring(hd.getHighlightIndices()[0], hd.getHighlightIndices()[1]);
        if (currText.equals(oldString)) {
            int startOffset = 0;
            for (int i = 0; i < hd.getAtomIndex(); i++) {
                startOffset += currHLVariant.getAtoms().get(i).getLength();
            }
            startOffset += hd.getHighlightIndices()[0];
            int oldStringLength = hd.getHighlightIndices()[1] - hd.getHighlightIndices()[0];
            target.modifyChars(startOffset, oldStringLength, replaceString);
            currHLVariant.replaced(replaceString);
        }
		target.setHighlightDataList(currHLVariant.getHighlightDataList());
	}

    private void replaceTargetTextXliff20(FragmentVariant target, String oldString, String replaceString) {
		
		HighlightData hd = currHLVariant.getHighlightDataList().get(
				currHLVariant.getCurrentHighlightedIndex());
		
		boolean replaced = false;
		if(target.getAtoms() != null && hd.getAtomIndex()<target.getAtoms().size()){
			if(target.getAtoms().get(hd.getAtomIndex()) instanceof TextAtom ) {
				TextAtom txtAtom = (TextAtom)target.getAtoms().get(hd.getAtomIndex());
                String existString = txtAtom.getData().substring(hd.getHighlightIndices()[0],
                        hd.getHighlightIndices()[1]);
                if (existString.equals(oldString)) {
                    String newText = txtAtom.getData().substring(0, hd.getHighlightIndices()[0]) + replaceString
                            + txtAtom.getData().substring(hd.getHighlightIndices()[1]);
                    TextAtom newTextAtom = new TextAtom(newText);
                    target.getAtoms().set(hd.getAtomIndex(), newTextAtom);
                    replaced = true;
                }
            }
        }
		target.setHighlightDataList(currHLVariant.getHighlightDataList());
		target.setCurrentHighlightedIndex(currHLVariant.getCurrentHighlightedIndex());
		if (replaced) {
		    target.replaced(replaceString);
        }
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
			public void columnRemoved(TableColumnModelEvent tcme) {
			}

			@Override
			public void columnMoved(TableColumnModelEvent tcme) {
			}

			@Override
			public void columnMarginChanged(ChangeEvent ce) {
				sourceTargetTable.recalculateAllRowHeights();
			}

			@Override
			public void columnSelectionChanged(ListSelectionEvent lse) {
			}
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
			sourceCol.setCellRenderer(new SegmentTextFontRenderer(
			        sourceTargetTable.getFont()));
			break;
		case Target:
			final TableColumn targetCol = tableColumnModel.getColumn(index);
			targetCol.setCellRenderer(new SegmentTextFontRenderer(
			        sourceTargetTable.getFont()));
			targetCol.setCellEditor(new SegmentEditor(sourceTargetTable
			        .getFont()));
			break;
		case Original:
			final TableColumn targetOriginalCol = tableColumnModel
			        .getColumn(index);
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
			public void componentRemoved(ContainerEvent e) {
			}

			@Override
			public void componentAdded(ContainerEvent e) {
				addListenersToComponents(e.getChild(), focusListener, this);
			}
		};
		addListenersToComponents(component, focusListener, containerListener);
	}

	private void addListenersToComponents(Component component,
	        FocusListener focusListener, ContainerListener containerListener) {
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
		final TableColumn currColumn = tableColumnModel
		        .getColumn(segmentTableModel.getSegmentNumColumnIndex());
		Font font = sourceTargetTable.getFont();
		if (currColumn.getCellRenderer() != null
		        && currColumn.getCellRenderer() instanceof SegmentTextFontRenderer) {
			font = ((SegmentTextFontRenderer) currColumn.getCellRenderer())
			        .getFont();
		}
		// Adjust the segment number column width
		currColumn.setPreferredWidth(this.getFontMetrics(font).stringWidth(
		        " " + segmentTableModel.getRowCount()));
		sourceTargetTable.recalculateAllRowHeights();
	}

	private void updateTableRow(int row) {
		segmentTableModel.fireTableRowsUpdated(row, row);
		updateRowHeight(row, sourceTargetTable.getIntercellSpacing().height);
	}

	public void requestFocusTable() {
		sourceTargetTable.requestFocus();
	}

	public void addFilters() {
		sort = new SegmentTableSorter(segmentTableModel);
		sourceTargetTable.setRowSorter(sort);
		sort.setRowFilter(new RowFilter<SegmentTableModel, Integer>() {
			private SegmentSelector selector = new SegmentSelector(ruleConfig, showNotTransSegments);
			
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
			adjustEditorInitialSize(row);
			return;
		}
		FontMetrics font = sourceTargetTable.getFontMetrics(sourceTargetTable
		        .getFont());
		int rowHeight = font.getHeight();
        rowHeight = getColumnHeight(SegNum, row, rowHeight);
        rowHeight = getColumnHeight(Source, row, rowHeight);
        rowHeight = getColumnHeight(Target, row, rowHeight);
        rowHeight = getColumnHeight(Original, row, rowHeight);
		sourceTargetTable.setRowHeight(row, rowHeight + intercellHeight);
	}

	private void adjustEditorInitialSize(int row) {
		final FontMetrics defFontMetrics = sourceTargetTable
		        .getFontMetrics(sourceTargetTable.getFont());
		final int defFontHeight = defFontMetrics.getHeight();
		final FontMetrics sourceFontMetrics = sourceTargetTable
		        .getFontMetrics(((SegmentTextFontRenderer) sourceTargetTable
		                .getColumnModel()
		                .getColumn(
		                        segmentTableModel.getSegmentSourceColumnIndex())
		                .getCellRenderer()).getFont());
		final int sourceFontHeight = sourceFontMetrics.getHeight();
		final FontMetrics targetFontMetrics = sourceTargetTable
		        .getFontMetrics(((SegmentTextFontRenderer) sourceTargetTable
		                .getColumnModel()
		                .getColumn(
		                        segmentTableModel.getSegmentTargetColumnIndex())
		                .getCellRenderer()).getFont());
		final int targetFontHeight = targetFontMetrics.getHeight();
		int height = Math.max(defFontHeight, sourceFontHeight);
		height = Math.max(height, targetFontHeight);
		sourceTargetTable.setRowHeight(row, height * 10);
	}

    private int getColumnHeight(SegmentViewColumn colData, int viewRow, int previousHeight) {
        int viewCol = segmentTableModel.getIndexForColumn(colData);
        if (viewCol == -1) {
            return previousHeight;
        }
        TableCellRenderer renderer = sourceTargetTable.getCellRenderer(viewRow, viewCol);
        Component comp = sourceTargetTable.prepareRenderer(renderer, viewRow, viewCol);
        int colWidth = tableColumnModel.getColumn(viewCol).getWidth();
        comp.setBounds(0, 0, colWidth, Integer.MAX_VALUE);
        return Math.max(previousHeight, comp.getPreferredSize().height + 5);
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
	
	public void toggleNotTranslatableSegments(boolean showNotTransSegs){
		this.showNotTransSegments = showNotTransSegs;
		reloadTable();
	}

	@Subscribe
	public void updateSegmentView(RefreshSegmentView event) {
		try {
			synchronized (segmentTableModel) {
				if (segmentTableModel.getRowCount() > 0 && event.getSegmentNumber() > -1) {
					segmentTableModel.fireTableRowsUpdated(
					        event.getSegmentNumber() - 1,
					        event.getSegmentNumber() - 1);
				} else {
					segmentTableModel.fireTableDataChanged();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			sourceTargetTable.recalculateAllRowHeights();
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
			sourceTargetTable.recalculateAllRowHeights();
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
        int row = sort.convertRowIndexToView(segmentTableModel.getModelIndexForSegment(event.getSegment()));
		updateTableRow(row);
        if (!event.isQuiet()) {
            sourceTargetTable.setRowSelectionInterval(row, row);
            eventQueue.post(new LQISelectionEvent(event.getLQI()));
            postSegmentSelection(event.getSegment());
            requestFocusTable();
        }
	}

	@Subscribe
	public void notifySegmentTargetReset(SegmentTargetResetEvent event) {
		segmentTableModel.fireTableDataChanged();
		sourceTargetTable.recalculateAllRowHeights();
	}

	@Subscribe
	public synchronized void handleTargetUpdatedFromMatch(
	        SegmentTargetUpdateFromMatchEvent event) {
		// int selRow = sourceTargetTable.getSelectedRow();
		// // segmentTableModel.fireTableRowsUpdated(selRow, selRow);
		// // updateRowHeights();
		// updateTableRow(selRow);
		// sourceTargetTable.requestFocusInWindow();
		targetChangedFromMatch = true;
	}

	@Subscribe
	public synchronized void handleTargetUpdatedFromMatch(SegmentEditEvent event) {
		if (targetChangedFromMatch) {
			int selRow = sourceTargetTable.getSelectedRow();
			// segmentTableModel.fireTableRowsUpdated(selRow, selRow);
			// updateRowHeights();
			updateTableRow(selRow);
			sourceTargetTable.requestFocusInWindow();
			targetChangedFromMatch = false;
		}

	}

	@Subscribe
	public void notifySegmentEdit(SegmentEditEvent event) {
		try{
		int segmentIndex = segmentTableModel.getModelIndexForSegment(event.getSegment());
		
//		int selRow = sourceTargetTable.getSelectedRow();
		updateTableRow(sort.convertRowIndexToView(segmentIndex));
		sourceTargetTable.requestFocusInWindow();
		}catch(Exception e){
			e.printStackTrace();
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
	
	private static Color getBackground(OcelotSegment seg, boolean isSelected, JTable table){
		return isSelected ? table
		        .getSelectionBackground() : (seg.isEditable() && seg.isTranslatable()) ?  table
		        .getBackground() : Color.LIGHT_GRAY;
	}
	
	private static Color getForeground(OcelotSegment seg, boolean isSelected, JTable table, boolean coloredBackground){
		return (seg.isEditable()  && seg.isTranslatable()) ? isSelected ? table
		        .getSelectionForeground() : table.getForeground()
		        : coloredBackground ? Color.darkGray : Color.GRAY;
	}

	
	/**
	 * TableCellRenderer for source/target text in the SegmentTableView.
	 */
	public class SegmentTextRenderer implements TableCellRenderer {
        private final SegmentTextCell renderTextPane = SegmentTextCell.createCell();

		@Override
		public Component getTableCellRendererComponent(JTable jtable, Object o,
		        boolean isSelected, boolean hasFocus, int row, int col) {
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
                    renderTextPane.setVariant(row, v, false);
				} else {
					renderTextPane.setTargetDiff(seg.getTargetDiff());
				}
				Color background = getBackground(seg, isSelected, jtable);
				Color foreground = getForeground(seg, isSelected, jtable, false);

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

		private final Color enrichedColor = new Color(5, 169, 71);

		private final Color sentToFremeColor = Color.red;

		public IntegerRenderer() {
			setOpaque(true);
		}

		private Color getSegmentColor(OcelotSegment seg) {
			StateQualifier sq = seg.getStateQualifier();
			if (sq != null) {
				Color sqColor = ruleConfig.getStateQualifierColor(sq);
				if (sqColor == null) {
//					LOG.debug("No UI color for state-qualifier '{}'", sq);
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
			boolean stateQualifierBackground = background != null;
			background = background != null ? background : SegmentView.getBackground(seg, isSelected, jtable);
			//TODO check what color the segment number should be. If grey, it is not visible on the state qualifier background 
//			Color foreground = isSelected ? jtable.getSelectionForeground() : jtable.getForeground();
			Color foreground = SegmentView.getForeground(seg, isSelected, jtable, stateQualifierBackground);

			if (seg.getSource() instanceof BaseSegmentVariant
			        && ((BaseSegmentVariant) seg.getSource()).isFremeSuccess()
			        && (((BaseSegmentVariant) seg.getTarget()).isFremeSuccess()
			                || seg.getTarget().getDisplayText() == null || seg
			                .getTarget().getDisplayText().isEmpty())) {
				foreground = enrichedColor;
			} else if (seg.getSource() instanceof BaseSegmentVariant
			        && ((BaseSegmentVariant) seg.getSource()).isSentToFreme()
			        && (((BaseSegmentVariant) seg.getTarget()).isSentToFreme())) {
				foreground = sentToFremeColor;
			}

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

        private final SegmentTextCell editorComponent = SegmentTextCell.createCell();

		@Override
		public Object getCellEditorValue() {

			return editorComponent.getVariant();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
		        Object value, boolean isSelected, int row, int column) {
			
			OcelotSegment seg = segmentTableModel.getSegment(sort
			        .convertRowIndexToModel(row));
            editorComponent.setVariant(row, seg.getSource().createCopy(), false);
            editorComponent.setBidi(isSourceBidi);
			editorComponent.setBackground(table.getSelectionBackground());
			editorComponent.setSelectionColor(Color.BLUE);
			editorComponent.setSelectedTextColor(Color.WHITE);
			editorComponent.setEditable(false);
			editorComponent.getCaret().setVisible(true);
			editorComponent.addMouseListener(new TextPopupMenuListener(false));
			editorComponent.getInputMap().put(
			        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "finish");
			editorComponent.getActionMap().put("finish", new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
            ToolTipManager.sharedInstance().registerComponent(editorComponent);
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

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            super.removeCellEditorListener(l);
            ToolTipManager.sharedInstance().unregisterComponent(editorComponent);
        }
	}

	public class SegmentEditor extends AbstractCellEditor implements
	        TableCellEditor {
		private static final long serialVersionUID = 1L;

        protected SegmentTextEditorCell editorComponent;
		protected JTextArea textArea;
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
                editorComponent = SegmentTextEditorCell.createCell(row, seg.getSource()
				        .createCopy(), false, isSourceBidi);
				editorComponent.setEditable(false);

			} else if (col == segmentTableModel.getSegmentTargetColumnIndex()) {
				editListener
				        .setBeginEdit(seg, seg.getTarget().getDisplayText());
                editorComponent = SegmentTextEditorCell.createCell(row, seg.getTarget()
				        .createCopy(), false, isTargetBidi);
				editorComponent.addMouseListener(new TextPopupMenuListener(true));
				editorComponent.getInputMap().put(
				        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "finish");
				editorComponent.getActionMap().put("finish",
				        new AbstractAction() {
					        private static final long serialVersionUID = 1L;

					        @Override
					        public void actionPerformed(ActionEvent e) {
                                if (stopCellEditing()) {
                                    fireEditingStopped();
                                    eventQueue.post(new OcelotEditingEvent(OcelotEditingEvent.Type.STOP_EDITING));
                                }
					        }
				        });
				editingRow = row;
				editorComponent.setFont(font);
                editorComponent.prepareEditingUI();
                ToolTipManager.sharedInstance().registerComponent(editorComponent);
			}
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

        @Override
        public boolean stopCellEditing() {
            if (!editorComponent.canStopEditing()) {
                // It seems that it's not good to make a blocking call here, as
                // e.g. window resize events can make us enter this method
                // multiple times and we get multiple dialogs. However there
                // seems to be no other way to prompt the user before committing
                // or cancelling changes, so instead we work around by
                // overriding JTable methods that might call this method to
                // first unilaterally cancel editing. See:
                // columnMoved(), columnMarginChanged(), print()
                int response = JOptionPane.showOptionDialog(SegmentView.this, TAG_VALIDATION_ERROR_MESSAGE,
                        TAG_VALIDATION_ERROR_TITLE, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
                        new String[] { TAG_VALIDATION_BUTTON_CONTINUE, TAG_VALIDATION_BUTTON_DISCARD },
                        TAG_VALIDATION_BUTTON_CONTINUE);
                if (response == 1) {
                    cancelCellEditing();
                }
                return false;
            }
            return super.stopCellEditing();
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            // This appears to be the only way for the TableCellEditor to find
            // out that editing has ended in the case of the user invoking the
            // "cancel" action (by e.g. pressing Esc): JTable.removeEditor() is
            // called directly in BasicTableUI.Actions.actionPerformed(). Thus
            // we do cleanup here.
            super.removeCellEditorListener(l);
            editorComponent.closeEditingUI();
            ToolTipManager.sharedInstance().unregisterComponent(editorComponent);
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
            eventQueue.post(new SegmentTargetEditEvent(xliff, seg,
			        updatedTarget));
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
				Note ocelotNote = ((Notes) value).getOcelotNote();
				if (ocelotNote != null) {
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
				Note ocelotNote = ((Notes) value).getOcelotNote();
				if (ocelotNote != null) {
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

		public void beginEditSegment(OcelotSegment seg) {
			this.seg = seg;
		}

		@Override
		public void editingStopped(ChangeEvent ce) {

			int row = sourceTargetTable.getSelectedRow();
			String noteContent = ((NotesCellEditor) ce.getSource()).txtArea
			        .getText();
			eventQueue
			        .post(new SegmentNoteUpdatedEvent(xliff, seg, noteContent));
			updateTableRow(row);
			// Restore row selection
			sourceTargetTable.setRowSelectionInterval(row, row);
			// SegmentVariant updatedTarget = ((SegmentEditor)
			// ce.getSource()).editorComponent
			// .getVariant();
			// int row = sourceTargetTable.getSelectedRow();
			// eventQueue.post(new SegmentTargetExitEvent(seg));
			// eventQueue.post(new SegmentTargetUpdateEvent(seg,
			// updatedTarget));
			// postSegmentSelection(seg);
			// updateTableRow(row);
			// // Restore row selection
			// sourceTargetTable.setRowSelectionInterval(row, row);
		}

		@Override
		public void editingCanceled(ChangeEvent ce) {
			// Cancel not supported.
		}
	}

	public class TextPopupMenuListener extends MouseAdapter {

		/** States if the event source cell is the target one. */		
		private boolean target;		
				
		public TextPopupMenuListener(boolean target) {		
			this.target = target;		
        }
		
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
				if (isPointInlcudedInSelection(e.getPoint(), sourceCell)) {
					TextContextMenu ctxMenu = new TextContextMenu(eventQueue,
					        sourceCell.getSelectedText(), target);
					int selectedRow = sourceTargetTable.getSelectedRow();
					OcelotSegment selSegment = segmentTableModel
					        .getSegment(selectedRow);
					List<JMenuItem> items = ocelotApp
					        .getSegmentTextContexPluginMenues(selSegment,
					                sourceCell.getSelectedText(), sourceCell.getCaret().getMark(), target,
					                SwingUtilities
					                        .getWindowAncestor(sourceCell));
					if (items != null) {
						for (JMenuItem item : items) {
							ctxMenu.add(item);
						}
					}
					ctxMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}
		
		private boolean isPointInlcudedInSelection(Point point,
		        SegmentTextCell cell) {

			boolean included = false;
			int pointPosInModel = cell.viewToModel(point);
			if (pointPosInModel < cell.getText().length()) {
				included = pointPosInModel < cell.getCaret().getDot()
				        && pointPosInModel >= cell.getCaret().getMark();
			} else {
				Rectangle dotRect;
				try {
					dotRect = cell.modelToView(cell.getCaret().getDot());
					included = point.x <= dotRect.x;
				} catch (BadLocationException e) {
					LOG.warn(
					        "Wrong model to view conversion, when checking if the clicked point falls ",
					        e);
				}
			}
			return included;
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
			BaseSegmentVariant variant = null;
			int r = sourceTargetTable.rowAtPoint(e.getPoint());
			if (r >= 0 && r < sourceTargetTable.getRowCount()) {
				sourceTargetTable.setRowSelectionInterval(r, r);
				//TODO 
//				seg = segmentTableModel.getSegment(r);
				seg = segmentTableModel.getSegment(sort.convertRowIndexToModel(r));
			}
			int c = sourceTargetTable.columnAtPoint(e.getPoint());
			LanguageQualityIssue selLqi = null;
			boolean target = false;
			if (c == segmentTableModel.getIndexForColumn(Source)) {
				variant = (BaseSegmentVariant) seg.getSource();
			} else if (c == segmentTableModel.getIndexForColumn(Target)) {
				variant = (BaseSegmentVariant) seg.getTarget();
				target = true;
			} else if ( segmentTableModel.getColumn(c).isFlagColumn()){
				Object obj = segmentTableModel.getValueAt(r, c);
				if(obj instanceof LanguageQualityIssue){
					selLqi = (LanguageQualityIssue) obj;
				}
			}
			

			if (seg != null) {
				ContextMenu menu = new ContextMenu(xliff, seg, variant, selLqi,
				        eventQueue, lqiGrid);
				List<JMenuItem> pluginsItems = ocelotApp
				        .getSegmentContexPluginMenues(seg, variant, target);
				if (pluginsItems != null) {
					for (JMenuItem item : pluginsItems) {
						menu.add(item);
					}
				}
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	class SegmentTableSorter extends TableRowSorter<SegmentTableModel>{
		
		
		public SegmentTableSorter(SegmentTableModel model) {
			super(model);
		}

		@Override
		public void allRowsChanged() {
			clearHighlightedSegments();
			super.allRowsChanged();
			int[] sortIndexMap = getSortIndexMap();
			eventQueue.post(new SegmentRowsSortedEvent(sortIndexMap));
		}
		
		@Override
		public void toggleSortOrder(int column) {
			clearHighlightedSegments();
			super.toggleSortOrder(column);
			int[] sortIndexMap = getSortIndexMap();
			eventQueue.post(new SegmentRowsSortedEvent(sortIndexMap));
			
		}
		
		private int[] getSortIndexMap() {
			int[] sortIndexMap = new int[segmentTableModel.getRowCount()];
			for(int i=0; i<segmentTableModel.getRowCount(); i++){
				sortIndexMap[i] = sort.convertRowIndexToView(i);
			}
			return sortIndexMap;
		}
	}

	public JTable getTable() {
		return sourceTargetTable;
	}
}