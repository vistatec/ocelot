package com.vistatec.ocelot.tm.gui.match;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.gui.AbstractDetachableTmPanel;
import com.vistatec.ocelot.tm.gui.constants.TmIconsConst;

/**
 * This panel implements the Concordance Search functionality. It is detachable,
 * i.e. by pressing the appropriate button the panel is detached from the Ocelot
 * main frame and it is displayed inside its own window.
 */
public class ConcordanceSearchPanel extends AbstractDetachableTmPanel {

	/** Search field width constant. */
	private static final int SEARCH_TXT_WIDTH = 300;

	/** Search field height constant. */
	private static final int SEARCH_TXT_HEIGHT = 25;

	/** Search button size. */
	private static final int SEARCH_BTN_SIZE = 25;

	/** The search text field. */
	private JTextField txtSearch;

	/** The search button. */
	private JButton btnSearch;

	/** The table displaying the concordance mathces. */
	private TmTable matchesTable;

	/** The loading label. */
	private JLabel lblLoading;

	/** Label displayed when no matches are found. */
	private JLabel lblNoResults;

	/** The info panel. */
	private JPanel infoPanel;

	/** The table scroll panel. */
	private JScrollPane scrollPanel;

	/** Cell editor for the target. */
	private TableCellEditor targetEditor;

	/** The table model. */
	private ConcordanceMatchTableModel tableModel;

	/** The table cell renderer assigned to the source column. */
	private ConcordanceCellRenderer sourceColRenderer;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            the controller.
	 */
	public ConcordanceSearchPanel(TmGuiMatchController controller) {

		super(controller);
	}

	/**
	 * Builds the components displayed in this panel.
	 */
	private void buildComponents() {

		// Build and configure the search text field.
		txtSearch = new JTextField();
		final Dimension txtDim = new Dimension(SEARCH_TXT_WIDTH,
		        SEARCH_TXT_HEIGHT);
		txtSearch.setPreferredSize(txtDim);
		txtSearch.setMinimumSize(txtDim);
		txtSearch.setMaximumSize(txtDim);
		txtSearch.addActionListener(this);
		// Build and configure the search button.
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
		// configure the table
		configTable();
		// Build and configure the info panel and its labels.
		lblLoading = new JLabel("Loading...");
		lblNoResults = new JLabel("No results");
		infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		infoPanel.add(lblLoading);
	}

	/**
	 * Makes the concordance search panel.
	 */
	protected void makePanel() {
		// Creates the Concordance Search label.
		JLabel lblConcordance = new JLabel("Concordance Search");
		lblConcordance.setFont(lblConcordance.getFont().deriveFont(Font.BOLD,
		        12));
		// builds panel components.
		buildComponents();
		// Create the panel.
		panel = new JPanel();
		panel.setName("Concordance Search");
		JPanel concordancePanel = (JPanel) panel;
		concordancePanel.setLayout(new GridBagLayout());
		GridBagConstraints gridBag = new GridBagConstraints();
		// Add the pin button
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		gridBag.insets = new Insets(10, 5, 0, 0);
		concordancePanel.add(getPinComponent(), gridBag);
		// Add the concordance label
		gridBag.gridx = 1;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		gridBag.insets = new Insets(10, 0, 0, 0);
		concordancePanel.add(lblConcordance, gridBag);

		// Add the search text field.
		gridBag.gridx = 0;
		gridBag.gridy = 1;
		gridBag.gridwidth = 2;
		gridBag.insets = new Insets(10, 0, 0, 0);
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		concordancePanel.add(txtSearch, gridBag);
		// add the search button
		gridBag.gridx = 2;
		gridBag.gridwidth = 1;
		gridBag.insets = new Insets(10, 0, 0, 10);
		gridBag.fill = GridBagConstraints.NONE;
		concordancePanel.add(btnSearch, gridBag);
		// Add the scroll panel containing the table.
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
		// creates the data model
		tableModel = new ConcordanceMatchTableModel(null);
		// creates the table.
		matchesTable = new TmTable();
		matchesTable.setModel(tableModel);
		// Configure the action "replaceTarget" for the keystroke ALT+R
		matchesTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
		        KeyStroke.getKeyStroke(KeyEvent.VK_R, Ocelot.getPlatformKeyMask()), "replaceTarget");
		matchesTable.getActionMap().put("replaceTarget",
		        new ReplaceTargetAction());
		// Assign the match score renderer to the match score column, and set
		// size
		TableColumn scoreCol = matchesTable.getColumnModel().getColumn(
		        tableModel.getMatchScoreColumnIdx());
		scoreCol.setCellRenderer(new MatchScoreRenderer());
		scoreCol.setPreferredWidth(50);
		scoreCol.setMaxWidth(50);
		// Set size to the TM column
		TableColumn tmCol = matchesTable.getColumnModel().getColumn(
		        tableModel.getTmColumnIdx());
		tmCol.setPreferredWidth(200);
		tmCol.setMaxWidth(200);
		// Assign the Concordance cell renderer to the source column
		TableColumn sourceCol = matchesTable.getColumnModel().getColumn(
		        tableModel.getSourceColumnIdx());
		sourceColRenderer = new ConcordanceCellRenderer();
		sourceCol.setCellRenderer(sourceColRenderer);
		targetEditor = new ReadOnlyCellEditor();
		TableColumn targetCol = matchesTable.getColumnModel().getColumn(
		        tableModel.getTargetColumnIdx());
		targetCol.setCellEditor(targetEditor);
	}

	/**
	 * Performs the concordance search.
	 */
	private void performConcordanceSearch() {

		if (txtSearch.getText() != null && !txtSearch.getText().isEmpty()) {
			setLoading();
			sourceColRenderer.setSearchedString(txtSearch.getText());

			SegmentAtom text = new TextAtom(txtSearch.getText());
			final List<TmMatch> results = controller
			        .getConcordanceMatches(Arrays
			                .asList(new SegmentAtom[] { text }));

			if (results != null && !results.isEmpty()) {
				targetEditor.stopCellEditing();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						tableModel.setModel(results);
						scrollPanel.setViewportView(matchesTable);
						scrollPanel.repaint();
						matchesTable.getSelectionModel().setSelectionInterval(
						        0, 0);

					}
				});
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						infoPanel.removeAll();
						infoPanel.add(lblNoResults);
						infoPanel.repaint();
						scrollPanel.setViewportView(infoPanel);
						scrollPanel.repaint();
					}
				});
			}

		} else {
			targetEditor.stopCellEditing();
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					infoPanel.removeAll();
					infoPanel.add(lblNoResults);
					infoPanel.repaint();
					scrollPanel.setViewportView(infoPanel);
					scrollPanel.repaint();
				}
			});
		}
	}

	/**
	 * Displays the info panel with the loading label.
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
		controller.selectConcordanceTab();
		txtSearch.setText(text);
		performConcordanceSearch();
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

}

/**
 * Table cell renderer for concordance highlights. By setting the searched
 * string, this renderer highlights all words in the rendered column matching
 * the concordance string.
 */
class ConcordanceCellRenderer extends SegmentVariantCellRenderer {

	/** The serial version UID. */
	private static final long serialVersionUID = -4227851946202241316L;

	/** The searched string. */
	private String searchedString;

	/** The highlight painter. */
	private DefaultHighlightPainter painter = new DefaultHighlightPainter(
	        Color.yellow);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vistatec.ocelot.tm.gui.match.SegmentVariantCellRenderer#
	 * getTableCellRendererComponent(javax.swing.JTable, java.lang.Object,
	 * boolean, boolean, int, int)
	 */
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

	/**
	 * Sets the searched string.
	 * 
	 * @param searchedString
	 *            the searched string.
	 */
	public void setSearchedString(String searchedString) {
		this.searchedString = searchedString;
	}

}
