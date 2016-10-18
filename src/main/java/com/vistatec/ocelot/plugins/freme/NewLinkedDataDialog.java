package com.vistatec.ocelot.plugins.freme;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.vistatec.ocelot.events.ItsDocStatsRecalculateEvent;
import com.vistatec.ocelot.events.TextAnalysisAddedEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.model.EnrichmentMetaData;
import com.vistatec.ocelot.its.model.TextAnalysisMetaData;
import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.segment.model.enrichment.EntityEnrichment;

/**
 * This dialog let users manually insert a new linked data. It will be saved as
 * a Text Analysis annotation.
 */
public class NewLinkedDataDialog extends JDialog implements ActionListener,
        OcelotEventQueueListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3136216630678038859L;

	private static final int WIDTH = 500;

	private static final int HEIGHT = 200;

	private OcelotEventQueue eventQueue;

	private JTextField txtHlText;

	private JTextField txtLink;

	private JButton btnOK;

	private JButton btnCancel;

	private boolean target;

	private OcelotSegment segment;

	private String text;
	
	private int offset;

	public NewLinkedDataDialog(final Window owner, final String text, final int offset,
	        final OcelotSegment segment, final boolean target,
	        final OcelotEventQueue eventQueue) {

		super(owner);
		this.eventQueue = eventQueue;
		this.text = text;
		this.segment = segment;
		this.target = target;
		this.offset = offset;
		buildDialog();
	}

	private void buildDialog() {

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("New Linked Data");
		Dimension dim = new Dimension(WIDTH, HEIGHT);
		setSize(dim);
		setPreferredSize(dim);
		add(getMainPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
		setLocationRelativeTo(getOwner());

	}

	private Component getMainPanel() {

		JLabel lblText = new JLabel("Text");
		lblText.setHorizontalAlignment(SwingConstants.RIGHT);
		txtHlText = new JTextField();
		txtHlText.setText(text);
		txtHlText.setEditable(false);

		JLabel lblLink = new JLabel("Resource Link");
		lblLink.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLink = new JTextField();
		txtLink.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				System.out.println("caret listener - caret update");
				if (!txtLink.getText().isEmpty()) {
					btnOK.setEnabled(true);
				} else {
					btnOK.setEnabled(false);
				}
			}
		});

		txtLink.addActionListener(this);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.weighty = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(20, 20, 10, 10);
		mainPanel.add(lblText, c);

		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 20, 20, 10);
		mainPanel.add(lblLink, c);

		c.weightx = 0.1;
		c.gridy = 0;
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(20, 0, 10, 20);
		mainPanel.add(txtHlText, c);

		c.gridy = 1;
		c.insets = new Insets(0, 0, 20, 20);
		mainPanel.add(txtLink, c);

		return mainPanel;
	}

	private Component getBottomPanel() {

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		btnOK = new JButton("Ok");
		btnOK.setEnabled(false);
		btnOK.addActionListener(this);
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		bottomPanel.add(btnOK);
		bottomPanel.add(btnCancel);
		return bottomPanel;

	}

	private void cancel() {
		close();
	}

	private void close() {
		setVisible(false);
	}

	private void save() {

		TextAnalysisMetaData ta = new TextAnalysisMetaData();
		ta.setEntity(text);
		ta.setSegPart(target ? EnrichmentMetaData.TARGET
		        : EnrichmentMetaData.SOURCE);
		ta.setTaIdentRef(txtLink.getText());
		segment.addTextAnalysis(ta);
		
		EntityEnrichment entity = new EntityEnrichment(txtLink.getText());
		entity.setOffsetStartIdx(offset);
		entity.setOffsetEndIdx(offset + text.length());
		if(target){
			((BaseSegmentVariant)segment.getTarget()).addEnrichment(entity);
		} else {
			((BaseSegmentVariant)segment.getSource()).addEnrichment(entity);
		}
		
		eventQueue.post(new TextAnalysisAddedEvent());
		eventQueue.post(new ItsDocStatsRecalculateEvent(Arrays
		        .asList(new OcelotSegment[] { segment })));
		close();
	}

	public void open() {
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(btnOK)) {
			save();
		} else if (e.getSource().equals(btnCancel)) {
			cancel();
		} else if (e.getSource().equals(txtLink)) {
			System.out.println("txt link");
		}
	}

}
