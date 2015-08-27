package com.vistatec.ocelot.freme.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.vistatec.ocelot.segment.model.LinkEnrichment;
import com.vistatec.ocelot.segment.model.LinkInfoData;

public class LinkEnrichmentFrame extends JDialog implements Runnable {

	private static final int WIDTH = 500;

	private static final int HEIGHT = 400;

	private LinkEnrichment enrichment;

	private JButton btnClose;

	public LinkEnrichmentFrame(LinkEnrichment enrichment, Window ownerWindow) {
		super(ownerWindow);
		setModal(true);
		this.enrichment = enrichment;
		init();
	}

	private Component getTopPanel() {

		JPanel topPanel = new JPanel();
		// topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		JLabel entityLabel = new JLabel(enrichment.getEntityName().getValue());
		entityLabel.setFont(entityLabel.getFont().deriveFont(Font.BOLD, 14));
		topPanel.add(entityLabel);

		// JTextArea shortDescrTxt = new JTextArea();
		// shortDescrTxt.setText(enrichment.getShortDescription());
		// shortDescrTxt.setBackground(SystemColor.control);
		// // shortDescrTxt.setBorder(BorderFactory.createEmptyBorder());
		// shortDescrTxt.setWrapStyleWord(true);
		// shortDescrTxt.setLineWrap(true);
		// shortDescrTxt.setPreferredSize(new Dimension(450, 100));
		// shortDescrTxt.setSize(new Dimension(450, 100));
		// // JLabel shortDescrLabel = new
		// JLabel(enrichment.getShortDescription());
		// topPanel.add(shortDescrTxt);

		return topPanel;
	}

	private Component getMainPanel() {

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Abstract",
		        new AbstractPanel(enrichment.getLongDescription().getValue()));
		tabbedPane.addTab("Info", new InfoPanel(enrichment.getInfoList()));
		tabbedPane.addTab("Image", getImagePanel());
		tabbedPane.addTab("Links", getLinksPanel());
		return tabbedPane;
	}

	private JScrollPane getImagePanel() {
		
		JLabel imageLabel = null;
		if(enrichment.getImage() != null){
			
			imageLabel = new JLabel(new ImageIcon(enrichment.getImage()));
		} else {
			imageLabel = new JLabel("No Image");
		}
		JScrollPane pane = new JScrollPane(imageLabel);
		return pane;
	}

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
							// TODO Auto-generated catch block
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

	private void init() {

		setTitle("e-Link Service Results");
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setSize(new Dimension(WIDTH, HEIGHT));
		add(getTopPanel(), BorderLayout.NORTH);
		add(getMainPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
	}

	public void open() {

		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	private void close() {
		setVisible(false);
		dispose();
	}

	@Override
	public void run() {

		open();
	}

	public static void main(String[] args) {

//		JFrame frame = new JFrame();
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
//
//		LinkEnrichment enrichment = new LinkEnrichment("sdfsdf#char=2,5");
//		enrichment.setEntityName("Naples");
//		enrichment
//		        .setShortDescription("Naples (/ˈneɪpəlz/; Italian: Napoli [ˈnaːpoli] (13px ), Neapolitan: Napule [ˈnɑːpələ]; Latin: Neapolis; Ancient Greek: Νεάπολις, meaning \"new city\") is the capital of the Italian region Campania and the third-largest municipality in Italy, after Rome and Milan. As of 2012, around 960,000 people live within the city's administrative limits. The Naples urban area has a population of between 3 million and 3.7 million, and is the 9th-most populous urban area in the European Union.");
//		enrichment
//		        .setLongDescription("Naples (/ˈneɪpəlz/; Italian: Napoli [ˈnaːpoli] (13px ), Neapolitan: Napule [ˈnɑːpələ]; Latin: Neapolis; Ancient Greek: Νεάπολις, meaning \"new city\") is the capital of the Italian region Campania and the third-largest municipality in Italy, after Rome and Milan. As of 2012, around 960,000 people live within the city's administrative limits. The Naples urban area has a population of between 3 million and 3.7 million, and is the 9th-most populous urban area in the European Union. Around 4 million people live in the Naples metropolitan area, one of the largest metropolises on the Mediterranean Sea.Naples is one of the oldest continuously inhabited cities in the world. Bronze Age Greek settlements were established in the Naples area in the second millennium BC. A larger colony – initially known as Parthenope, Παρθενόπη – developed on the Island of Megaride around the ninth century BC, at the end of the Greek Dark Ages. The city was refounded as Neápolis in the sixth century BC and became a lynchpin of Magna Graecia, playing a key role in the merging of Greek culture into Roman society and eventually becoming a cultural centre of the Roman Republic. Naples remained influential after the fall of the Western Roman Empire, serving as the capital city of the Kingdom of Naples between 1282 and 1816. Thereafter, in union with Sicily, it became the capital of the Two Sicilies until the unification of Italy in 1861. During the Neapolitan War of 1815, Naples strongly promoted Italian unification.Naples was the most-bombed Italian city during World War II. Much of the city's 20th-century periphery was constructed under Benito Mussolini's fascist government, and during reconstruction efforts after World War II. In recent decades, Naples has constructed a large business district, the Centro Direzionale, and has developed an advanced transport infrastructure, including an Alta Velocità high-speed rail link to Rome and Salerno, and an expanded subway network, which is planned to eventually cover half of the region. The city has experienced significant economic growth in recent decades, and unemployment levels in the city and surrounding Campania have decreased since 1999. However, Naples still suffers from political and economic corruption, and unemployment levels remain high.Naples has the fourth-largest urban economy in Italy, after Milan, Rome and Turin. It is the world's 103rd-richest city by purchasing power, with an estimated 2011 GDP of US$83.6 billion. The port of Naples is one of the most important in Europe, and has the world's second-highest level of passenger flow, after the port of Hong Kong. Numerous major Italian companies, such as MSC Cruises Italy S.p.A, are headquartered in Naples. The city also hosts NATO's Allied Joint Force Command Naples, the SRM Institution for Economic Research and the OPE Company and Study Centre. Naples is a full member of the Eurocities network of European cities. The city was selected to become the headquarters of the European institution ACP/UE and was named a City of Literature by UNESCO's Creative Cities Network. The Villa Rosebery, one of the three official residences of the President of Italy, is located in the city's Posillipo district.Naples' historic city centre is the largest in Europe, covering 1,700 hectares (4,200 acres) and enclosing 27 centuries of history, and is listed by UNESCO as a World Heritage Site. Naples has long been a major cultural centre with a global sphere of influence, particularly during the Renaissance and Enlightenment eras. In the immediate vicinity of Naples are numerous culturally and historically significant sites, including the Palace of Caserta and the Roman ruins of Pompeii and Herculaneum. Culinarily, Naples is synonymous with pizza, which originated in the city. Neapolitan music has furthermore been highly influential, credited with the invention of the romantic guitar and the mandolin, as well as notable contributions to opera and folk standards. Popular characters and historical figures who have come to symbolise the city include Januarius, the patron saint of Naples, the comic figure Pulcinella, and the Sirens from the Greek epic poem the Odyssey. According to CNN, the metro stop \"Toledo\" is the most beautiful in Europe and it won also the LEAF Award '2013 as \"Public building of the year\".Naples' sports scene is dominated by football and Serie A club S.S.C. Napoli, two-time Italian champions and winner of European trophies, who play at the San Paolo Stadium in the south-west of the city.");
//		List<LinkInfoData> infoDataList = new ArrayList<LinkInfoData>();
//		LinkInfoData data = new LinkInfoData("", "Area Total", String.class,
//		        "Km2");
//		data.setValue("117.27");
//		infoDataList.add(data);
//		data = new LinkInfoData("", "Population Total", Integer.class);
//		data.setValue("1061644");
//		infoDataList.add(data);
//		data = new LinkInfoData("", "Latitude", Float.class);
//		data.setValue("40.833332");
//		infoDataList.add(data);
//		data = new LinkInfoData("", "Longitude", Float.class);
//		data.setValue("14.250000");
//		infoDataList.add(data);
//		enrichment.setInfoList(infoDataList);
//
//		enrichment.setHomePage("http://www.comune.napoli.it");
//		enrichment.setWikiPage("http://en.wikipedia.org/wiki/Naples");
//		Image image = null;
//		try {
//			URL url = new URL(
//			        "http://commons.wikimedia.org/wiki/Special:FilePath/MontageofNaples.jpg?width=300");
//			while (image == null && url != null) {
//				image = ImageIO.read(url);
//				if (image == null) {
//					HttpURLConnection conn = (HttpURLConnection) url
//					        .openConnection();
//					HttpURLConnection.setFollowRedirects(false);
//					String urlStr = conn.getHeaderField("Location");
//					if (urlStr != null) {
//						url = new URL(urlStr);
//					} else {
//						url = null;
//					}
//				}
//			}
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		enrichment.setImage(image);
//		LinkEnrichmentFrame enrichFrame = new LinkEnrichmentFrame(enrichment,
//		        frame);
//		SwingUtilities.invokeLater(enrichFrame);
	}
}

class AbstractPanel extends JScrollPane {

	private static final long serialVersionUID = 695686169168388387L;

	public AbstractPanel(String abstractString) {
		makePanel(abstractString);
	}

	private void makePanel(String abstractString) {

		JTextArea txtArea = new JTextArea();
		txtArea.setText(abstractString);
		txtArea.setWrapStyleWord(true);
		txtArea.setLineWrap(true);
		setViewportView(txtArea);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
}

class InfoPanel extends JScrollPane {

	private static final long serialVersionUID = 5519240168871463019L;

	public InfoPanel(List<LinkInfoData> infoDataList) {

		makePanel(infoDataList);
	}

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
