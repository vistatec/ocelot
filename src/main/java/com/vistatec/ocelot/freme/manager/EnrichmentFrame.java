package com.vistatec.ocelot.freme.manager;

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
import javax.swing.JFrame;
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

import com.vistatec.ocelot.segment.model.Enrichment;
import com.vistatec.ocelot.segment.model.EntityEnrichment;
import com.vistatec.ocelot.segment.model.SegmentAtom;
import com.vistatec.ocelot.segment.model.TextAtom;
import com.vistatec.ocelot.segment.model.okapi.FragmentVariant;

public class EnrichmentFrame extends JDialog implements Runnable, ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -1930402687244966333L;

    private JButton btnClose;

//    private JButton btnCancel;

    private List<JLabel> labels;

    private Point position;

    public EnrichmentFrame(final FragmentVariant fragment, final Window owner,
            final Point position) {

        super(owner);
        buildLabels(fragment);
        this.position = position;
    }

    public EnrichmentFrame(final FragmentVariant fragment, final Window owner) {
        this(fragment, owner, null);
    }

    private Component makeMainPanel() {

        JTextPane textPane = new JTextPane();
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(400,200));
        textPane.setEditable(false);
        StyledDocument styleDoc = textPane.getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().getStyle(
                StyleContext.DEFAULT_STYLE);
        Style regular = styleDoc.addStyle("regular", def);
        Style text =styleDoc.addStyle("regular", def);
        JLabel sampleLabel = new JLabel();
        StyleConstants.setFontSize(text, sampleLabel.getFont().getSize());
        StyleConstants.setFontFamily(text, sampleLabel.getFont().getFamily());
        StyleConstants.setBold(text, true);
        Style labelStyle = styleDoc.addStyle("label", regular);
        
        try {
            for (JLabel label : labels) {
                if(label instanceof EnrichedLabel){
                StyleConstants.setComponent(labelStyle, label);
                styleDoc.insertString(styleDoc.getLength(), " ",
                        labelStyle);
                } else {
                    styleDoc.insertString(styleDoc.getLength(), label.getText(),
                            text);
                }
            }
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return scrollPane;

    }

    private Component makeBottomPanel() {

        btnClose = new JButton("Close");
        btnClose.addActionListener(this);
//        btnCancel = new JButton("Cancel");
//        btnCancel.addActionListener(this);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel.add(btnClose);
//        panel.add(btnCancel);
        return panel;
    }

    private void buildLabels(FragmentVariant fragment) {

        Collections.sort(fragment.getEnirchments(), new EnrichmentComparator());
        labels = new ArrayList<JLabel>();
        if (fragment.getEnirchments() != null) {
            JLabel label = null;
            String fragDisplayText = fragment.getDisplayText();
            int startIndex = 0;
            EnrichedLabel lastEnrichedLabel = null;
            for (Enrichment e : fragment.getEnirchments()) {
                if (lastEnrichedLabel != null
                        && lastEnrichedLabel.containsOffset(e
                                .getOffsetStartIdx())) {
                    lastEnrichedLabel.addEnrichment(e);
                    if (startIndex < e.getOffsetEndIdx()) {
                        startIndex = e.getOffsetEndIdx();
                    }
                } else {
                    label = new JLabel(fragDisplayText.substring(startIndex,
                            e.getOffsetStartIdx()));
                    labels.add(label);
                    lastEnrichedLabel = new EnrichedLabel(
                            fragDisplayText.substring(e.getOffsetStartIdx(),
                                    e.getOffsetEndIdx()));
                    lastEnrichedLabel.addEnrichment(e);
                    labels.add(lastEnrichedLabel);
                    startIndex = e.getOffsetEndIdx();
                }
            }
            if (startIndex < fragDisplayText.length()) {
                labels.add(new JLabel(fragDisplayText.substring(startIndex)));
            }
        }
    }

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

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        SegmentAtom atom = new TextAtom("Welcome to Dublin! dfhgsd jh dsjfsjfg sjdgf sudgfsjkgfkjwge fkwjgef wkejfg wejgf wekjfg wejfgwejhgf lwjkef kjweg flwkjeg fl");
        List<SegmentAtom> atoms = new ArrayList<SegmentAtom>();
        atoms.add(atom);
        FragmentVariant fragment = new FragmentVariant(atoms, false);
        Enrichment enrich = new EntityEnrichment(
                "http://127.0.0.1:9995/spotlight#char=11,17",
                "http://dbpedia.org/resource/Dublin");
        fragment.addEnrichment(enrich);
        EnrichmentFrame enrichFrame = new EnrichmentFrame(fragment, frame);
        SwingUtilities.invokeLater(enrichFrame);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        close();
        
    }

    private void close() {

        setVisible(false);
        dispose();
    }

}

class EnrichmentComparator implements Comparator<Enrichment> {

    @Override
    public int compare(Enrichment o1, Enrichment o2) {

        int comparison = 0;
        if (o1.getOffsetStartIdx() < o2.getOffsetStartIdx()) {
            comparison = -1;
        } else if (o1.getOffsetStartIdx() > o2.getOffsetStartIdx()) {
            comparison = 1;
        }
        return comparison;
    }

}

class EnrichedLabel extends JLabel implements ActionListener, MouseListener {

    /**
     * 
     */
    private static final long serialVersionUID = -8815906147011864508L;

    private String plainText;

    private List<Enrichment> enrichments;

    private int startIndex;

    private int endIndex;

    private boolean disabled;

    private BalloonTip tip;

    private int type;


    public EnrichedLabel(final String text) {

        this.plainText = text;
        startIndex = -1;
        endIndex = -1;
        // this.url = url;
        initialize();

    }

    private void initialize() {

//        Map<TextAttribute, ?> attributes = getFont().getAttributes();
//        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
//        setFont(getFont().deriveFont(Font.BOLD));
//        setFont(getFont().deriveFont());
        String enrichedText = "<html><font color=\"blue\"><u><b>" + plainText
                + "</b></u></font></html>";
        FontMetrics metrics = getFontMetrics(getFont());
        final int width = metrics.charsWidth(plainText.toCharArray(), 0,
                plainText.length());
        setMaximumSize(new Dimension(width, getHeight()));
        setMinimumSize(new Dimension(width, getHeight()));
//        setBorder(new LineBorder(Color.red));
        setAlignmentY(0.85f);
        setText(enrichedText);
        // setToolTipText(url);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(this);
        // addMouseListener(new MouseAdapter() {
        //
        // @Override
        // public void mouseClicked(MouseEvent e) {
        //
        // }
        //
        // });
    }

    public void addEnrichment(final Enrichment enrichment) {

        if (enrichments == null) {
            enrichments = new ArrayList<Enrichment>();
        }
        enrichments.add(enrichment);
        if (startIndex == -1 || startIndex > enrichment.getOffsetStartIdx()) {
            startIndex = enrichment.getOffsetStartIdx();
        }

        if (endIndex == -1 || endIndex < enrichment.getOffsetEndIdx()) {
            endIndex = enrichment.getOffsetEndIdx();
        }

    }

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

    public boolean isDisabled() {
        return disabled;
    }

    public boolean containsOffset(final int offestStartIdx) {

        return offestStartIdx >= startIndex && offestStartIdx <= endIndex;
    }

    private void handleMouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
//            if (Desktop.isDesktopSupported()) {
//                try {
//                    Desktop.getDesktop().browse(new URI(url));
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                } catch (URISyntaxException e1) {
//                    e1.printStackTrace();
//                }
//            }
            final Window ancestor = SwingUtilities.getWindowAncestor(this);
            tip = new BalloonTip(
                    SwingUtilities.getWindowAncestor(ancestor),
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
//        } else if (e.getButton() == MouseEvent.BUTTON3) {
//            contextMenu.show(
//                    EnrichedLabel.this,
//                    EnrichedLabel.this.getLocation().x,
//                    EnrichedLabel.this.getLocation().y
//                            + EnrichedLabel.this.getHeight());
        }
    }

    public Component getDescriptionComponent() {

        return new EnrichmentDetailsPanel(enrichments, SystemColor.info);

    }

    @Override
    public void mouseClicked(MouseEvent e) {

//        tooltipTimer.stop();
        handleMouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        tooltipTimer.stop();
        handleMouseClicked(e);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        final Window ancestor = SwingUtilities.getWindowAncestor(this);
//        tooltipTimer = new Timer(1000, new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                tooltipTimer.stop();
//                tip = new BalloonTip(
//                        SwingUtilities.getWindowAncestor(ancestor),
//                        getDescriptionComponent(), getLocationOnScreen().x
//                                + getWidth() / 2, getLocationOnScreen().y
//                                + getHeight());
//                SwingUtilities.invokeLater(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        tip.makeUI();
//                        updateLabelStatus();
//
//                    }
//                });
//            }
//        });
//        tooltipTimer.start();

    }

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

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
