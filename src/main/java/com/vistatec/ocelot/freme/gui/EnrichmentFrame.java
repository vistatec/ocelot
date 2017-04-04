package com.vistatec.ocelot.freme.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.vistatec.ocelot.segment.model.BaseSegmentVariant;
import com.vistatec.ocelot.segment.model.enrichment.Enrichment;

/**
 * Frame displaying enriched words highlighted in the text.
 */
public class EnrichmentFrame extends JDialog implements Runnable,
        ActionListener {

	/** The serial version UID. */
	private static final long serialVersionUID = -1930402687244966333L;

	/** The close button. */
	private JButton btnClose;

	/** The list of labels. */
	private List<JLabel> labels;

	/** The location where the frame is displayed. */
	private Point position;

	/**
	 * Constructor.
	 * 
	 * @param fragment
	 *            the Ocelot fragment.
	 * @param owner
	 *            the owner window.
	 * @param position
	 *            the position.
	 */
	public EnrichmentFrame(final BaseSegmentVariant fragment,
	        final Window owner, final Point position) {

		super(owner);
		buildLabels(fragment);
		this.position = position;
	}

	/**
	 * Constructor.
	 * 
	 * @param fragment
	 *            the Ocelot fragment.
	 * @param owner
	 *            the owner window.
	 */
	public EnrichmentFrame(final BaseSegmentVariant fragment, final Window owner) {
		this(fragment, owner, null);
	}

	private Component makeMainPanel() {

		JTextPane textPane = new JTextPane();

		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setPreferredSize(new Dimension(400, 200));
		textPane.setEditable(false);
		StyledDocument styleDoc = textPane.getStyledDocument();
		Style def = StyleContext.getDefaultStyleContext().getStyle(
		        StyleContext.DEFAULT_STYLE);
		Style regular = styleDoc.addStyle("regular", def);
		Style text = styleDoc.addStyle("regular", def);
		JLabel sampleLabel = new JLabel();
		StyleConstants.setFontSize(text, sampleLabel.getFont().getSize());
		StyleConstants.setFontFamily(text, sampleLabel.getFont().getFamily());
		StyleConstants.setBold(text, true);
		Style labelStyle = styleDoc.addStyle("label", regular);

		try {
			for (JLabel label : labels) {
				if (label instanceof EnrichedLabel) {
					StyleConstants.setComponent(labelStyle, label);
					styleDoc.insertString(styleDoc.getLength(), " ", labelStyle);
				} else {
					styleDoc.insertString(styleDoc.getLength(),
					        label.getText(), text);
				}
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scrollPane;

	}

	/**
	 * Makes the bottom panel.
	 * 
	 * @return the bottom panel
	 */
	private Component makeBottomPanel() {

		btnClose = new JButton("Close");
		btnClose.addActionListener(this);
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.add(btnClose);
		return panel;
	}

	/**
	 * Builds the labels from the text and the enrichments retrieved by the
	 * Ocelot fragment.
	 * 
	 * @param fragment
	 *            the Ocelot fragments.
	 */
	private void buildLabels(BaseSegmentVariant fragment) {

		labels = new ArrayList<JLabel>();
		if (fragment.getEnirchments() != null) {
			List<Enrichment> fragEnrichments = new ArrayList<>(fragment.getEnirchments());
			Collections.sort(fragEnrichments, new EnrichmentComparator());
			JLabel label = null;
			String fragPlainText = fragment.getPlainText();
			int startIndex = 0;
			EnrichedLabel lastEnrichedLabel = null;
			for (Enrichment e : fragEnrichments) {
				if (lastEnrichedLabel != null
				        && lastEnrichedLabel.containsOffset(e
				                .getOffsetNoTagsStartIdx())) {
					lastEnrichedLabel.addEnrichment(e);
					if (startIndex < e.getOffsetNoTagsEndIdx()) {
						startIndex = e.getOffsetNoTagsEndIdx();
					}
				} else {
					label = new JLabel(fragPlainText.substring(startIndex,
					        e.getOffsetNoTagsStartIdx()));
					labels.add(label);
					lastEnrichedLabel = new EnrichedLabel(
					        fragPlainText.substring(e.getOffsetNoTagsStartIdx(),
					                e.getOffsetNoTagsEndIdx()));
					lastEnrichedLabel.addEnrichment(e);
					labels.add(lastEnrichedLabel);
					startIndex = e.getOffsetNoTagsEndIdx();
				}
			}
			if (startIndex < fragPlainText.length()) {
				labels.add(new JLabel(fragPlainText.substring(startIndex)));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		setTitle("Enrichment");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		add(makeMainPanel());
		add(makeBottomPanel(), BorderLayout.SOUTH);
		pack();
		if (position != null) {
			setLocation(position);
		} else {
			setLocationRelativeTo(getOwner());
		}
		setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		close();

	}

	/**
	 * Closes the frame.
	 */
	private void close() {

		setVisible(false);
		dispose();
	}

}

/**
 * Comparator for enrichments.
 */
class EnrichmentComparator implements Comparator<Enrichment> {

	@Override
	public int compare(Enrichment o1, Enrichment o2) {

		int comparison = 0;
		if (o1.getOffsetNoTagsStartIdx() < o2.getOffsetNoTagsStartIdx()) {
			comparison = -1;
		} else if (o1.getOffsetNoTagsStartIdx() > o2.getOffsetNoTagsStartIdx()) {
			comparison = 1;
		} else if(o1.getOffsetEndIdx() > o2.getOffsetEndIdx()){		
			comparison = -1;		
		} else if(o1.getOffsetEndIdx() < o2.getOffsetEndIdx()){		
			comparison = 1;	
		}
		return comparison;
	}

}

/**
 * Label containing an enriched text.
 */
class EnrichedLabel extends JLabel implements ActionListener, MouseListener {

	/** The serial version UID. */
	private static final long serialVersionUID = -8815906147011864508L;

	/** The plain text assigned to this label. */
	private String plainText;

	/** The enrichments assigned to this label. */
	private List<Enrichment> enrichments;

	/** The start index for this label within the whole fragment text. */
	private int startIndex;

	/** The end index for this label within the whole fragment text. */
	private int endIndex;

	/**
	 * States if this label is disabled: an enriched label is disabled if and
	 * only if all the assigned enrichments are disabled.
	 */
	private boolean disabled;

	/** The balloon tip displayed for this label. */
	private BalloonTip tip;

	/**
	 * Constructor.
	 * 
	 * @param text
	 *            the text for this label.
	 */
	public EnrichedLabel(final String text) {

		this.plainText = text;
		startIndex = -1;
		endIndex = -1;
		initialize();

	}

	/**
	 * Initializes the label.
	 */
	private void initialize() {

		String enrichedText = "<html><font color=\"blue\"><u><b>" + plainText
		        + "</b></u></font></html>";
		FontMetrics metrics = getFontMetrics(getFont());
		final int width = metrics.charsWidth(plainText.toCharArray(), 0,
		        plainText.length());
		setMaximumSize(new Dimension(width, getHeight()));
		setMinimumSize(new Dimension(width, getHeight()));
		setAlignmentY(0.85f);
		setText(enrichedText);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		addMouseListener(this);
	}

	/**
	 * Adds an enrichment to this label.
	 * 
	 * @param enrichment
	 *            the enrichment.
	 */
	public void addEnrichment(final Enrichment enrichment) {

		if (enrichments == null) {
			enrichments = new ArrayList<Enrichment>();
		}
		enrichments.add(enrichment);
		if (startIndex == -1 || startIndex > enrichment.getOffsetNoTagsStartIdx()) {
			startIndex = enrichment.getOffsetNoTagsStartIdx();
		}

		if (endIndex == -1 || endIndex < enrichment.getOffsetNoTagsEndIdx()) {
			endIndex = enrichment.getOffsetNoTagsEndIdx();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (!disabled) {
			disabled = true;
			String enrichedText = "<html><font color=\"gray\"><u>" + plainText
			        + "</u></font></html>";
			setText(enrichedText);
		} else {
			disabled = false;
			String enrichedText = "<html><font color=\"blue\"><u><b>"
			        + plainText + "</b></u></font></html>";
			setText(enrichedText);
		}

	}

	/**
	 * Checks if this label is disabled.
	 * 
	 * @return <code>true</code> if the label is disabled; <code>false</code>
	 *         otherwise
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Checks if this label contains a specific offset index.
	 * 
	 * @param offestStartIdx
	 *            the offset index.
	 * @return <code>true</code> if it contains this index; <code>false</code>
	 *         otherwise
	 */
	public boolean containsOffset(final int offestStartIdx) {

		return offestStartIdx >= startIndex && offestStartIdx <= endIndex;
	}

	/**
	 * Handles the event the mouse has been clicked over this label: the balloon
	 * tool tip gets opened.
	 * 
	 * @param e
	 *            the mouse event
	 */
	private void handleMouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			final Window ancestor = SwingUtilities.getWindowAncestor(this);
			tip = new BalloonTip(SwingUtilities.getWindowAncestor(ancestor),
			        getDescriptionComponent(), getLocationOnScreen().x
			                + getWidth() / 2, getLocationOnScreen().y
			                + getHeight());
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					tip.makeUI();
					updateLabelStatus();

				}
			});
		}
	}

	/**
	 * Gets the description component to be displayed within the balloon tool
	 * tip.
	 * 
	 * @return the description component to be displayed within the balloon tool
	 *         tip.
	 */
	public Component getDescriptionComponent() {

		return new EnrichmentDetailsPanel(enrichments, SystemColor.info);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {

		handleMouseClicked(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		handleMouseClicked(e);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// does nothing.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// does nothing
	}

	/**
	 * Updates the status of the label depending on the status of the embedded
	 * enrichments.
	 */
	private void updateLabelStatus() {

		boolean newValue = true;
		for (Enrichment e : enrichments) {
			if (!e.isDisabled()) {
				newValue = false;
			}
		}
		if (newValue != disabled) {
			disabled = newValue;
			if (disabled) {
				String enrichedText = "<html><font color=\"gray\"><u>"
				        + plainText + "</u></font></html>";
				setText(enrichedText);
			} else {
				String enrichedText = "<html><font color=\"blue\"><u><b>"
				        + plainText + "</b></u></font></html>";
				setText(enrichedText);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {

		//does nothing
	}

}
