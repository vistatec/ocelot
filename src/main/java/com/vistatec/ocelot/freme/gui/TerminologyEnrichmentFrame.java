package com.vistatec.ocelot.freme.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.vistatec.ocelot.segment.model.enrichment.TerminologyEnrichment;

/**
 * Frame displaying information about a terminology enrichment.
 */
public class TerminologyEnrichmentFrame extends JDialog implements Runnable {

	/** Serial version UID. */
	private static final long serialVersionUID = -8981314773682012696L;

	/** The main panel width constant. */
	private static final int MAIN_PANEL_WIDTH = 400;

	/** The main panel height constant. */
	private static final int MAIN_PANEL_HEIGHT = 250;

	/** The label width constant. */
	private static final int LABEL_WIDTH = 80;

	/** The label height constant. */
	private static final int LABEL_HEIGHT = 25;

	/** The text fields width constant. */
	private static final int TXT_WIDTH = 250;

	/** The text fields height constant. */
	private static final int TXT_HEIGHT = 25;

	/** The scroll pane width constant. */
	private static final int SCROLL_WIDTH = 250;

	/** The scroll pane height constant. */
	private static final int SCROLL_HEIGHT = 60;

	/** The terminology enrichment. */
	private TerminologyEnrichment termEnrichment;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner window.
	 * @param termEnrichment
	 *            the terminology enrichment.
	 */
	public TerminologyEnrichmentFrame(Window owner,
			TerminologyEnrichment termEnrichment) {

		super(owner);
		this.termEnrichment = termEnrichment;
		init();
	}

	/**
	 * Initializes the frame.
	 */
	private void init() {

		setTitle("Term Details");
		add(getMainPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(getOwner());
	}

	/**
	 * Gets the panel to be displayed in the center of the frame.
	 * 
	 * @return the frame main panel.
	 */
	private Component getMainPanel() {

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
		mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		mainPanel.setPreferredSize(new Dimension(MAIN_PANEL_WIDTH,
				MAIN_PANEL_HEIGHT));
		JLabel lblSource = new JLabel("Source");
		configLabel(lblSource);
		JTextField txtSource = new JTextField();
		configTextField(txtSource);
		txtSource.setText(termEnrichment.getSourceTerm());
		mainPanel.add(lblSource);
		mainPanel.add(txtSource);
		JLabel lblTarget = new JLabel("Target");
		configLabel(lblTarget);
		JTextField txtTarget = new JTextField();
		configTextField(txtTarget);
		txtTarget.setText(termEnrichment.getTargetTerm());
		mainPanel.add(lblTarget);
		mainPanel.add(txtTarget);
		JLabel lblDefinition = new JLabel("Definition");
		configLabel(lblDefinition);
		JTextArea txtDefinition = new JTextArea();
		configTextArea(txtDefinition);
		txtDefinition.setText(termEnrichment.getDefinition());
		txtDefinition.setCaretPosition(0);
		JScrollPane scrollDefinition = new JScrollPane(txtDefinition);
		configScrollPane(scrollDefinition);
		mainPanel.add(lblDefinition);
		mainPanel.add(scrollDefinition);
		JLabel lblDomain = new JLabel("Domain");
		configLabel(lblDomain);
		JTextArea txtDomain = new JTextArea();
		configTextArea(txtDomain);
		txtDomain.setText(termEnrichment.getSense());
		txtDomain.setCaretPosition(0);
		JScrollPane scrollDomain = new JScrollPane(txtDomain);
		configScrollPane(scrollDomain);
		mainPanel.add(lblDomain);
		mainPanel.add(scrollDomain);
		return mainPanel;
	}

	/**
	 * Configures a label.
	 * @param label the label.
	 */
	private void configLabel(JLabel label) {

		label.setSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
		label.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
		label.setHorizontalAlignment(SwingConstants.RIGHT);
	}

	/**
	 * Configures a text field.
	 * @param txtField the text field.
	 */
	private void configTextField(JTextField txtField) {

		txtField.setSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
		txtField.setPreferredSize(new Dimension(TXT_WIDTH, TXT_HEIGHT));
		txtField.setEditable(false);
		txtField.setBackground(SystemColor.control);
	}

	/**
	 * COnfigures a text area.
	 * @param txtArea the text area.
	 */
	private void configTextArea(JTextArea txtArea) {

		txtArea.setEditable(false);
		txtArea.setBackground(SystemColor.control);
		txtArea.setWrapStyleWord(true);
		txtArea.setLineWrap(true);
	}

	/**
	 * Configures a scroll pane.
	 * @param scrollPane the scroll pane.
	 */
	private void configScrollPane(JScrollPane scrollPane) {

		scrollPane.setSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));
		scrollPane.setPreferredSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	/**
	 * Gets the component to be displayed at the bottom of the frame.
	 * @return the bottom component.
	 */
	private Component getBottomPanel() {

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 45, 20));
		JButton btnClose = new JButton("Close");
		bottomPanel.add(btnClose);
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		return bottomPanel;
	}

	/**
	 * Opens the frame.
	 */
	public void open() {
		setVisible(true);
	}

	/**
	 * Closes the framne.
	 */
	public void close() {
		setVisible(false);
		dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		setVisible(true);
	}
}
