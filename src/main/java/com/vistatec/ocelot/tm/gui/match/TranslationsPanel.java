package com.vistatec.ocelot.tm.gui.match;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.gui.AbstractDetachableTmPanel;

/**
 * This panel implements the Translations match functionality. It is detachable,
 * i.e. by pressing the appropriate button the panel is detached from the Ocelot
 * main frame and it is displayed inside its own window.
 */
public class TranslationsPanel extends AbstractDetachableTmPanel {

	/** The detached component width constant. */
	private static final int DETACHED_COMP_WIDTH = 800;

	/** The detached component height constant. */
	private static final int DETACHED_COMP_HEIGHT = 400;

	/** The table displaying matches. */
	private TmTable matchesTable;

	/** The table data model. */
	private TranslationsMatchTableModel tableModel;

	/** The scroll panel containing the table. */
	private JScrollPane scrollPanel;

	private TableCellEditor targetEditor;
	
	/**
	 * The info panel displayed while loading matches or when the research
	 * produces no results.
	 */
	private JPanel infoPanel;

	/** The loading label. */
	private JLabel lblLoading;

	/** Label displayed when no matches are found. */
	private JLabel lblNoResults;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            the controller.
	 */
	public TranslationsPanel(final TmGuiMatchController controller) {

		super(controller);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.tm.gui.AbstractDetachableTmPanel#makePanel()
	 */
	@Override
	protected void makePanel() {

		// Configures the table.
		configTable();
		// Build the translation results label
		JLabel lblTranslations = new JLabel("Translation Results");
		lblTranslations.setFont(lblTranslations.getFont().deriveFont(Font.BOLD,
				12));
		// Build the panel
		panel = new JPanel();
		panel.setName("Translations");
		JPanel translationsPanel = (JPanel) panel;
		translationsPanel.setLayout(new GridBagLayout());
		GridBagConstraints gridBag = new GridBagConstraints();

		// Add the pin button.
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		gridBag.insets = new Insets(10, 5, 10, 0);
		translationsPanel.add(getPinComponent(), gridBag);

		// Add the translations label.
		gridBag.gridx = 1;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		gridBag.insets = new Insets(10, 0, 10, 0);
		translationsPanel.add(lblTranslations, gridBag);
		// Add the scroll panel containing the table
		gridBag.gridx = 0;
		gridBag.gridy = 1;
		gridBag.gridwidth = 2;
		gridBag.weighty = 1;
		gridBag.weightx = 1;
		gridBag.insets = new Insets(0, 0, 0, 0);
		gridBag.fill = GridBagConstraints.BOTH;
		gridBag.anchor = GridBagConstraints.NORTH;
		scrollPanel = new JScrollPane();
		translationsPanel.add(scrollPanel, gridBag);

		// Configure info panel and its labels.
		lblLoading = new JLabel("Loading...");
		lblNoResults = new JLabel("No results");
		infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	}

	/**
	 * Configures the table.
	 */
	private void configTable() {

		// Create the data model.
		tableModel = new TranslationsMatchTableModel(null);
		// Create and configure the table.
		matchesTable = new TmTable();
		matchesTable.setModel(tableModel);
		// Make the table react to the ALT+R keystroke.
		matchesTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_R, Ocelot.getPlatformKeyMask() ),
				"replaceTarget");
		matchesTable.getActionMap().put("replaceTarget",
				new ReplaceTargetAction());

		// Set size to the segment number column.
		TableColumn numCol = matchesTable.getColumnModel().getColumn(
				tableModel.getSegmentNumColumnIdx());
		numCol.setPreferredWidth(50);
		numCol.setMaxWidth(50);
		// Set renderer for source/match diff column.
		TableColumn diffCol = matchesTable.getColumnModel().getColumn(tableModel.getSourceColumnIdx());
		diffCol.setCellRenderer(new SegmentVariantDiffCellRenderer());
		// Set appropriate renderer to the match score column and set size
		TableColumn scoreCol = matchesTable.getColumnModel().getColumn(
				tableModel.getMatchScoreColumnIdx());
		scoreCol.setCellRenderer(new MatchScoreRenderer());
		scoreCol.setPreferredWidth(50);
		scoreCol.setMaxWidth(50);
		TableColumn targetCol = matchesTable.getColumnModel().getColumn(tableModel.getTargetColumnIdx());
		targetEditor = new ReadOnlyCellEditor();
		targetCol.setCellEditor(targetEditor);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.tm.gui.AbstractDetachableTmPanel#makeWindow()
	 */
	@Override
	protected void makeWindow() {

		window = new JFrame();
		((JFrame) window).setTitle("Traslation Results");
		window.setPreferredSize(new Dimension(DETACHED_COMP_WIDTH,
				DETACHED_COMP_HEIGHT));

	}

	/**
	 * This class implements the "Replace Target" action. The target of the
	 * segment currently selected in the main grid is replaced by the target of
	 * the selected match in the translation panel.
	 */
	public class ReplaceTargetAction extends AbstractAction {

		/** The serial version UID. */
		private static final long serialVersionUID = 7013423666969632441L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			final int selRow = matchesTable.getSelectedRow();
			if (selRow != -1) {
				TmMatch selMatch = tableModel.getElementAtRow(selRow);
				controller.replaceTarget(selMatch.getTarget());
			}

		}

	}

	/**
	 * Sets the panel in loading mode, by displaying the info panel.
	 */
	public void setLoading() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				scrollPanel.setViewportView(infoPanel);
				infoPanel.add(lblLoading);
				scrollPanel.repaint();
			}
		});
	}

	/**
	 * Displays the translation match results if any; otherwise it displays the
	 * info panel with "No Results" label.
	 * 
	 * @param matches
	 *            the translation matches.
	 */
	public void setTranslationSearchResults(final OcelotSegment segment, final List<TmMatch> matches) {

		if (matches != null && !matches.isEmpty()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					targetEditor.stopCellEditing();
					tableModel.setModel(segment, matches);
					scrollPanel.setViewportView(matchesTable);
					scrollPanel.repaint();
					matchesTable.getSelectionModel().setSelectionInterval(0, 0);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					targetEditor.stopCellEditing();
					infoPanel.removeAll();
					infoPanel.add(lblNoResults);
					infoPanel.repaint();
				}
			});
		}
	}
}
