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
import javax.swing.table.TableColumn;

import com.vistatec.ocelot.tm.TmMatch;
import com.vistatec.ocelot.tm.gui.AbstractDetachableTmPanel;

public class TranslationsPanel extends AbstractDetachableTmPanel {

	
	
	private TmTable matchesTable;
	
	private TranslationsMatchTableModel tableModel;
	
	private JScrollPane scrollPanel;
	
	private JPanel infoPanel;
	
	/** The loading label. */
	private JLabel lblLoading;

	/** Label displayed when no matches are found. */
	private JLabel lblNoResults;
	
	public TranslationsPanel(final TmGuiMatchController controller) {
		
		super(controller);
	}
	
	@Override
	protected void makePanel() {
		
		configTable();
		JLabel lblTranslations = new JLabel("Translation Results");
		lblTranslations.setFont(lblTranslations.getFont().deriveFont(Font.BOLD,
				12));
		panel = new JPanel();
		panel.setName("Translations");
		JPanel translationsPanel = (JPanel) panel;
		translationsPanel.setLayout(new GridBagLayout());
		GridBagConstraints gridBag = new GridBagConstraints();
		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		gridBag.insets = new Insets(10, 5, 10, 0);
		translationsPanel.add(getPinComponent(), gridBag);
		gridBag.gridx = 1;
		gridBag.gridy = 0;
		gridBag.anchor = GridBagConstraints.NORTHWEST;
		gridBag.insets = new Insets(10, 0, 10, 0);
		translationsPanel.add(lblTranslations, gridBag);
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
		
		lblLoading = new JLabel("Loading...");
		lblNoResults = new JLabel("No results");
		infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
	}

	private void configTable() {
		
		tableModel = new TranslationsMatchTableModel(null);
		matchesTable = new TmTable();
		matchesTable.setModel(tableModel);
		matchesTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke('R', KeyEvent.CTRL_MASK),
				"replaceTarget");
		matchesTable.getActionMap().put("replaceTarget", new ReplaceTargetAction());
		TableColumn numCol = matchesTable.getColumnModel().getColumn(
				tableModel.getSegmentNumColumnIdx());
		numCol.setPreferredWidth(50);
		numCol.setMaxWidth(50);
		TableColumn scoreCol = matchesTable.getColumnModel().getColumn(
				tableModel.getMatchScoreColumnIdx());
		scoreCol.setCellRenderer(new MatchScoreRenderer());
		scoreCol.setPreferredWidth(50);
		scoreCol.setMaxWidth(50);
		
	}

	@Override
	protected void makeWindow() {
		
		window = new JFrame();
		((JFrame) window).setTitle("Traslation Results");
		window.setPreferredSize(new Dimension(800, 400));
		
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
	
	public void setLoading(){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				scrollPanel.setViewportView(infoPanel);
				infoPanel.add(lblLoading);
				scrollPanel.repaint();
			}
		});
	}
	
	public void setTranslationSearchResults(final List<TmMatch> matches){
		
		if(matches != null && !matches.isEmpty()){
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					tableModel.setModel(matches);
					scrollPanel.setViewportView(matchesTable);
					scrollPanel.repaint();
					matchesTable.getSelectionModel().setSelectionInterval(0, 0);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					infoPanel.removeAll();
					infoPanel.add(lblNoResults);
					infoPanel.repaint();
				}
			});
		}
	}

}
