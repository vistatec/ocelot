package com.vistatec.ocelot.tm.gui.match;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import com.vistatec.ocelot.Ocelot;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.SegmentVariant;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.tm.TmMatch;

public class ConcordanceSearchPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7513496529618290009L;

	private static final int SEARCH_TXT_WIDTH = 300;

	private static final int SEARCH_TXT_HEIGHT = 25;

	private static final int SEARCH_BTN_SIZE = 25;
	private static final String FIND_ICO = "find.png";

	private TmGuiMatchController controller;

	private JTextField txtSearch;

	private JButton btnSearch;

	private JTable matchesTable;

	private JLabel lblLoading;

	private JLabel lblNoResults;

	private JPanel infoPanel;

	private JScrollPane scrollPanel;

	private ConcordanceMatchTableModel tableModel;

	public ConcordanceSearchPanel(final TmGuiMatchController controller) {
		this.controller = controller;
		makePanel();
	}

	private void makePanel() {
		JLabel lblConcordance = new JLabel("Concordance Search");
		lblConcordance.setFont(lblConcordance.getFont().deriveFont(Font.BOLD,
				12));
		buildComponents();
		setLayout(new GridBagLayout());

		GridBagConstraints gridBag = new GridBagConstraints();
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		gridBag.insets = new Insets(10, 5, 0, 0);
		add(lblConcordance, gridBag);

		// gridBag.weightx = 0.1;
		gridBag.gridx = 0;
		gridBag.gridy = 1;
		gridBag.insets = new Insets(10, 0, 0, 0);
		// gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		add(txtSearch, gridBag);
		gridBag.gridx = 1;
		// gridBag.weightx = 0;
		gridBag.insets = new Insets(10, 0, 0, 10);
		gridBag.fill = GridBagConstraints.NONE;
		// gridBag.anchor = GridBagConstraints.NORTHWEST;
		add(btnSearch, gridBag);
		gridBag.gridx = 0;
		gridBag.gridy = 2;
		gridBag.gridwidth = 2;
		gridBag.weighty = 1;
		gridBag.weightx = 1;
		gridBag.insets = new Insets(0, 0, 0, 0);
		gridBag.fill = GridBagConstraints.BOTH;
		gridBag.anchor = GridBagConstraints.NORTH;
		scrollPanel = new JScrollPane();
		add(scrollPanel, gridBag);

	}

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
				.getResource(FIND_ICO)));
		btnSearch = new JButton(icon);
		btnSearch.addActionListener(this);
		btnSearch.setToolTipText("Search");
		final Dimension btnSearchDim = new Dimension(SEARCH_BTN_SIZE,
				SEARCH_BTN_SIZE);
		btnSearch.setPreferredSize(btnSearchDim);
		btnSearch.setMaximumSize(btnSearchDim);
		btnSearch.setMinimumSize(btnSearchDim);
		
		configTable();
		lblLoading = new JLabel("Loading...");
		lblNoResults = new JLabel("No results");
		infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		infoPanel.add(lblLoading);
	}
	
	private void configTable(){
		tableModel = new ConcordanceMatchTableModel(null);
		matchesTable = new JTable(tableModel);
		matchesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

		matchesTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('R', KeyEvent.CTRL_MASK), "replaceTarget");
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
	}

	public void displayMatches(final List<TmMatch> matches) {

	}

	public static void main(String[] args) {

		// JFrame frame = new JFrame();
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.add(new ConcordanceSearchPanel(), BorderLayout.CENTER);
		// frame.pack();
		// frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		performConcordanceSearch();

	}

	public void setTextAndPerformConcordanceSearch(final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				txtSearch.setText(text);
				performConcordanceSearch();
			}
		});
	}

	private void performConcordanceSearch() {
		if (txtSearch.getText() != null && !txtSearch.getText().isEmpty()) {
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

	public void clear() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				txtSearch.setText("");
				tableModel.setModel(null);
			}
		});

	}
	
	public class ReplaceTargetAction extends AbstractAction{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7013423666969632441L;

		@Override
		public void actionPerformed(ActionEvent e) {
			
			final int selRow = matchesTable.getSelectedRow();
			if( selRow != -1){
				TmMatch selMatch = tableModel.getElementAtRow(selRow);
				controller.replaceTarget(selMatch.getTarget());
			}
			
		}
		
	}

}
