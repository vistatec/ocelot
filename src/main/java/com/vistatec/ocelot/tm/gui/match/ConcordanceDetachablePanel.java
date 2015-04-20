package com.vistatec.ocelot.tm.gui.match;

import static com.vistatec.ocelot.SegmentViewColumn.Original;
import static com.vistatec.ocelot.SegmentViewColumn.SegNum;
import static com.vistatec.ocelot.SegmentViewColumn.Source;
import static com.vistatec.ocelot.SegmentViewColumn.Target;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.SegmentViewColumn;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.segment.view.SegmentTextCell;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.gui.AbstractDetachableTmPanel;
import com.vistatec.ocelot.tm.gui.constants.TmIconsConst;

/**
 * This panel implements the Concordance Search functionality. It is detachable,
 * i.e. by pressing the appropriate button the panel is detached from the Ocelot
 * main frame and it is displayed inside its own window.
 */
public class ConcordanceDetachablePanel extends AbstractDetachableTmPanel {

	/** Search field width constant. */
	private static final int SEARCH_TXT_WIDTH = 300;

	/** Search field height constant. */
	private static final int SEARCH_TXT_HEIGHT = 25;

	/** Search button size. */
	private static final int SEARCH_BTN_SIZE = 25;

	/** The controller. */
	TmGuiMatchController controller;

	/** The search text field. */
	private JTextField txtSearch;

	/** The search button. */
	private JButton btnSearch;

	/** The table displaying the concordance mathces. */
	private JTable matchesTable;

	/** The loading label. */
	private JLabel lblLoading;

	/** Label displayed when no matches are found. */
	private JLabel lblNoResults;

	/** The info panel. */
	private JPanel infoPanel;

	/** The table scroll panel. */
	private JScrollPane scrollPanel;

	/** The table model. */
	private ConcordanceMatchTableModel tableModel;
	
	private ConcordanceCellRenderer sourceColRenderer;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            the controller.
	 */
	public ConcordanceDetachablePanel(TmGuiMatchController controller) {
		this.controller = controller;
	}

	/**
	 * Builds the components displayed in this panel.
	 */
	private void buildComponents() {

		txtSearch = new JTextField();
		final Dimension txtDim = new Dimension(SEARCH_TXT_WIDTH,
				SEARCH_TXT_HEIGHT);
		txtSearch.setPreferredSize(txtDim);
		txtSearch.setMinimumSize(txtDim);
		txtSearch.setMaximumSize(txtDim);
		txtSearch.addActionListener(this);
		Toolkit kit = Toolkit.getDefaultToolkit();
		ImageIcon icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(TmIconsConst.FIND_ICO)));
		btnSearch = new JButton(icon);
		btnSearch.addActionListener(this);
		btnSearch.setToolTipText("Search");
		final Dimension dim = new Dimension(SEARCH_BTN_SIZE, SEARCH_BTN_SIZE);
		btnSearch.setPreferredSize(dim);
		btnSearch.setMaximumSize(dim);
		btnSearch.setMinimumSize(dim);
		icon = new ImageIcon(kit.createImage(Ocelot.class
				.getResource(TmIconsConst.PIN_ICO)));

		configTable();
		lblLoading = new JLabel("Loading...");
		lblNoResults = new JLabel("No results");
		infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		infoPanel.add(lblLoading);
	}

	/**
	 * Makes the concordance search panel.
	 */
	protected void makePanel() {
		JLabel lblConcordance = new JLabel("Concordance Search");
		lblConcordance.setFont(lblConcordance.getFont().deriveFont(Font.BOLD,
				12));
		buildComponents();
		panel = new JPanel();
		JPanel concordancePanel = (JPanel) panel;
		concordancePanel.setLayout(new GridBagLayout());

		GridBagConstraints gridBag = new GridBagConstraints();
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		gridBag.insets = new Insets(10, 5, 0, 0);
		concordancePanel.add(getPinComponent(), gridBag);
		gridBag.gridx = 1;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		gridBag.insets = new Insets(10, 0, 0, 0);
		concordancePanel.add(lblConcordance, gridBag);

		gridBag.gridx = 0;
		gridBag.gridy = 1;
		gridBag.gridwidth = 2;
		gridBag.insets = new Insets(10, 0, 0, 0);
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		concordancePanel.add(txtSearch, gridBag);
		gridBag.gridx = 2;
		gridBag.gridwidth = 1;
		gridBag.insets = new Insets(10, 0, 0, 10);
		gridBag.fill = GridBagConstraints.NONE;
		concordancePanel.add(btnSearch, gridBag);
		gridBag.gridx = 0;
		gridBag.gridy = 2;
		gridBag.gridwidth = 3;
		gridBag.weighty = 1;
		gridBag.weightx = 1;
		gridBag.insets = new Insets(0, 0, 0, 0);
		gridBag.fill = GridBagConstraints.BOTH;
		gridBag.anchor = GridBagConstraints.NORTH;
		scrollPanel = new JScrollPane();
		concordancePanel.add(scrollPanel, gridBag);

	}

	/**
	 * Configures the table.
	 */
	private void configTable() {
		tableModel = new ConcordanceMatchTableModel(null);
		matchesTable = new JTable(tableModel);
		matchesTable.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		matchesTable.setDefaultRenderer(SegmentVariant.class,
				new SegmentVariantCellRenderer());
		matchesTable.setDefaultRenderer(String.class,
				new AlternateRowsColorRenderer());
		matchesTable.setTableHeader(null);

		matchesTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_MASK),
				"selectPreviousRow");
		matchesTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_MASK),
				"selectNextRow");
		matchesTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke("DOWN"), "none");
		matchesTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke("UP"), "none");

		matchesTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke('R', KeyEvent.CTRL_MASK),
				"replaceTarget");
		ReplaceTargetAction action = new ReplaceTargetAction();
		matchesTable.getActionMap().put("replaceTarget", action);
		

		TableColumn scoreCol = matchesTable.getColumnModel().getColumn(
				ConcordanceMatchTableModel.MATCH_SCORE_COL);
		scoreCol.setPreferredWidth(50);
		scoreCol.setMaxWidth(50);
		TableColumn tmCol = matchesTable.getColumnModel().getColumn(
				ConcordanceMatchTableModel.TM_NAME_COL);
		tmCol.setPreferredWidth(200);
		tmCol.setMaxWidth(200);
		TableColumn sourceCol = matchesTable.getColumnModel().getColumn(ConcordanceMatchTableModel.SOURCE_COL);
		sourceColRenderer = new ConcordanceCellRenderer();
		sourceCol.setCellRenderer(sourceColRenderer);
	}

	/**
	 * Performs the concordance search.
	 */
	private void performConcordanceSearch() {
		if (txtSearch.getText() != null && !txtSearch.getText().isEmpty()) {
			sourceColRenderer.setSearchedString(txtSearch.getText());
			SegmentAtom text = new TextAtom(txtSearch.getText());
			scrollPanel.setViewportView(infoPanel);
			// infoPanel.removeAll();
			infoPanel.add(lblLoading);
			scrollPanel.repaint();
			// infoPanel.repaint();
			// List<TmMatch> results =
			// controller.getConcordanceMatches(Arrays.asList(new
			// SegmentAtom[]{text}));
			List<TmMatch> results = controller.getFuzzyMatches(Arrays
					.asList(new SegmentAtom[] { text }));
			if (results != null && !results.isEmpty()) {
				tableModel.setModel(results);
				updateRowHeights();
				scrollPanel.setViewportView(matchesTable);
				scrollPanel.repaint();
				matchesTable.getSelectionModel().setSelectionInterval(0, 0);
			} else {
				infoPanel.removeAll();
				infoPanel.add(lblNoResults);
				infoPanel.repaint();
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vistatec.ocelot.tm.gui.AbstractDetachableTmPanel#actionPerformed(
	 * java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnSearch) || e.getSource().equals(txtSearch)) {
			performConcordanceSearch();
		} else {
			super.actionPerformed(e);
		}

	}

	/**
	 * Set the text into the search text field and then performs the concordance
	 * search.
	 * 
	 * @param text
	 *            the text form the concordance search.
	 */
	public void setTextAndPerformConcordanceSearch(final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				txtSearch.setText(text);
				performConcordanceSearch();
			}
		});
	}

	/**
	 * This class implements the "Replace Target" action. The target of the
	 * segment currently selected in the main grid is replaced by the target of
	 * the selected match in the concordance panel.
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
	 * Makes the window that will contain the detached component.
	 */
	@Override
	protected void makeWindow() {

		window = new JFrame();
		((JFrame) window).setTitle("Concordance Search");
		window.setPreferredSize(new Dimension(800, 400));
	}
	
	protected void updateRowHeights() {
//        if (matchesTable.getColumnModel().getColumnCount() != tableModel.getColumnCount()) {
//            // We haven't finished building the column model, so there's no point in calculating
//            // the row height yet.
//            return;
//        }
//        scrollPanel.setViewportView(null);
//        for (int viewRow = 0; viewRow < matchesTable.getRowCount(); viewRow++) {
//            FontMetrics font = matchesTable.getFontMetrics(matchesTable.getFont());
//            int rowHeight = font.getHeight();
//            rowHeight = getColumnHeight(ConcordanceMatchTableModel.SOURCE_COL, viewRow,
//                    tableModel.getElementAtRow(viewRow).getSource().getDisplayText(), rowHeight);
//            rowHeight = getColumnHeight(ConcordanceMatchTableModel.TARGET_COL, viewRow,
//            		tableModel.getElementAtRow(viewRow).getTarget().getDisplayText(), rowHeight);
//            matchesTable.setRowHeight(viewRow, rowHeight);
//        }
//        scrollPanel.setViewportView(matchesTable);
    }
	
//	private int getColumnHeight(int column, int viewRow, String text, int previousHeight) {
//        
//		JTextPane tex 
//        return Math.max(previousHeight, segmentCell.getPreferredSize().height);
//    }
}

class ConcordanceCellRenderer extends SegmentVariantCellRenderer {

	private static final long serialVersionUID = -4227851946202241316L;

	private String searchedString;

	private DefaultHighlightPainter painter = new DefaultHighlightPainter(
			Color.yellow);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		JTextPane textPane = (JTextPane) super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
		String text = textPane.getText();
		if (text != null && searchedString != null) {
			int strIndex = 0;
			while (strIndex != -1 && strIndex < text.length()) {
				strIndex = text.indexOf(searchedString, strIndex);
				if (strIndex != -1) {
					try {
						textPane.getHighlighter().addHighlight(strIndex,
								strIndex + searchedString.length(), painter);
						strIndex += searchedString.length();
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		return textPane;
	}

	
	
	public void setSearchedString(String searchedString) {
		this.searchedString = searchedString;

	}

}
