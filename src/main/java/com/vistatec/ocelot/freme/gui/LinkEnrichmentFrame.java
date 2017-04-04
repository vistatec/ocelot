package com.vistatec.ocelot.freme.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.vistatec.ocelot.segment.model.enrichment.LinkEnrichment;
import com.vistatec.ocelot.segment.model.enrichment.LinkInfoData;

/**
 * Dialog displaying link enrichments information.
 */
public class LinkEnrichmentFrame extends JDialog implements Runnable {

	/** The serial version UID. */
	private static final long serialVersionUID = -312256326267527339L;

	/** The dialog width. */
	private static final int WIDTH = 500;

	/** The dialog height. */
	private static final int HEIGHT = 400;

	/** The link enrichment. */
	private LinkEnrichment enrichment;

	/** The close button. */
	private JButton btnClose;

	/**
	 * Constructor.
	 * 
	 * @param enrichment
	 *            the link enrichment.
	 * @param ownerWindow
	 *            the owner window.
	 */
	public LinkEnrichmentFrame(LinkEnrichment enrichment, Window ownerWindow) {
		super(ownerWindow);
		setModal(true);
		this.enrichment = enrichment;
		init();
	}

	/**
	 * Gets the dialog top panel.
	 * 
	 * @return the top panel.
	 */
	private Component getTopPanel() {

		JPanel topPanel = new JPanel();
		JLabel entityLabel = new JLabel(enrichment.getEntityName().getValue());
		entityLabel.setFont(entityLabel.getFont().deriveFont(Font.BOLD, 14));
		topPanel.add(entityLabel);
		return topPanel;
	}

	/**
	 * Gets the dialog main panel.
	 * 
	 * @return the main panel.
	 */
	private Component getMainPanel() {

		JTabbedPane tabbedPane = new JTabbedPane();
		if (enrichment.getLongDescription() != null) {
			tabbedPane.addTab("Abstract", new AbstractPanel(enrichment
			        .getLongDescription().getValue()));
		}
		tabbedPane.addTab("Info", new InfoPanel(enrichment.getInfoList()));
		tabbedPane.addTab("Image", getImagePanel());
		tabbedPane.addTab("Links", getLinksPanel());
		return tabbedPane;
	}

	/**
	 * Gets the image panel.
	 * 
	 * @return the image panel.
	 */
	private JScrollPane getImagePanel() {

		JLabel imageLabel = null;
		if (enrichment.getImage() != null) {

			imageLabel = new JLabel(new ImageIcon(enrichment.getImage()));
		} else {
			imageLabel = new JLabel("No Image");
		}
		JScrollPane pane = new JScrollPane(imageLabel);
		return pane;
	}

	/**
	 * Gets the useful links panel.
	 * 
	 * @return the useful links panel.
	 */
	private JPanel getLinksPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		if (enrichment.getLinks() != null) {
			ActionListener listener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					JButton btn = (JButton) e.getSource();
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(new URI(btn.getText()));
						} catch (IOException ex) { /* TODO: error handling */
							ex.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			};
			for (String link : enrichment.getLinks()) {
				JButton button = new JButton(link);
				button.setHorizontalAlignment(SwingConstants.LEFT);
				button.setBorderPainted(false);
				button.setOpaque(false);
				button.setBackground(Color.WHITE);
				button.setToolTipText(link);
				button.addActionListener(listener);
				button.setCursor(new Cursor(Cursor.HAND_CURSOR));
				button.setForeground(Color.BLUE);
				panel.add(button);
			}
		}

		return panel;
	}

	/**
	 * Gets the bottom panel.
	 * 
	 * @return the bottom panel.
	 */
	private Component getBottomPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		panel.add(btnClose);
		return panel;
	}

	/**
	 * Initializes the dialog.
	 */
	private void init() {

		setTitle("e-Link Service Results");
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setSize(new Dimension(WIDTH, HEIGHT));
		add(getTopPanel(), BorderLayout.NORTH);
		add(getMainPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Opens the dialog.
	 */
	public void open() {

		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	/**
	 * Closes the dialog.
	 */
	private void close() {
		setVisible(false);
		dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		open();
	}

}

/**
 * The panel displaying the abstract information from the link enrichment.
 */
class AbstractPanel extends JScrollPane {

	/** The serial version UID. */
	private static final long serialVersionUID = 695686169168388387L;

	public AbstractPanel(String abstractString) {
		makePanel(abstractString);
	}

	/**
	 * Makes the panel.
	 * 
	 * @param abstractString
	 *            the abstract string.
	 */
	private void makePanel(String abstractString) {

		JTextArea txtArea = new JTextArea();
		txtArea.setText(abstractString);
		txtArea.setWrapStyleWord(true);
		txtArea.setLineWrap(true);
		setViewportView(txtArea);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
}

/**
 * The panel displaying general info about the link enrichment.
 */
class InfoPanel extends JScrollPane {

	/** The serial version UID. */
	private static final long serialVersionUID = 5519240168871463019L;

	/**
	 * Constructor.
	 * 
	 * @param infoDataList
	 *            the list of info data.
	 */
	public InfoPanel(List<LinkInfoData> infoDataList) {

		makePanel(infoDataList);
	}

	/**
	 * Make the panel.
	 * 
	 * @param infoDataList
	 *            the list of info data to be displayed.
	 */
	private void makePanel(List<LinkInfoData> infoDataList) {

		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		setViewportView(container);

		for (LinkInfoData data : infoDataList) {
			container.add(new JLabel(data.getDisplayString()));
		}
	}

}
