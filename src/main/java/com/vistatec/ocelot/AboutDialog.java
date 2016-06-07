package com.vistatec.ocelot;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.vistatec.ocelot.ui.ODialogPanel;

public class AboutDialog extends ODialogPanel {
    private static final long serialVersionUID = 1L;
    private JButton ok;

    public AboutDialog(Image icon) {
        super(new GridBagLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints c = new GridBagConstraints();
        c.gridheight = 2;
        c.insets = new Insets(10, 10, 10, 10);
        add(new JLabel(new ImageIcon(icon)), c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(10, 0, 0, 0);
        c.weightx = 1.0;
        c.weighty = 1.0;
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");        
        JEditorPane p = new JEditorPane("text/html","<html>" +
                        "<b>Okapi Ocelot " + Version.PROJECT_VERSION + 
                        " (Build " + Version.SOURCE_VERSION + ")</b><br>" +
                        "Using Java " + javaVersion + " on " + osName + " " + osVersion + " (" + osArch +")<br><br>" +
                        "Documentation is available on the <a href=\"http://open.vistatec.com/ocelot\">wiki</a>.<br>" +
                        "Report a bug in the <a href=\"https://ocelot.atlassian.net/browse/OC\">issue tracker</a> or " +
                        "get the code on <a href=\"https://github.com/vistatec/ocelot\">GitHub</a>.<br><br>" +
                        "Ocelot is open source software, released under the LGPLv3 license.<br>" +
                        "Ocelot is part of the <a href=\"http://okapiframework.org/\">Okapi Project</a>.<br>" +
                        "</html>");
        p.setEditable(false);
        p.addHyperlinkListener(new AboutLinkOpener());
        add(p, c);
        ok = new JButton("OK");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.insets = new Insets(10, 0, 0, 0);
        ok.setDefaultCapable(true);
        ok.addActionListener(new DisposeDialogListener());
        add(ok, c);
    }

    @Override
    public JButton getDefaultButton() {
        return ok;
    }

    @Override
    public void postInit() {
        getDialog().setLocationRelativeTo(null);
    }

    class AboutLinkOpener implements HyperlinkListener {
        @Override
        public void hyperlinkUpdate(HyperlinkEvent e) {
            Desktop desktop = Desktop.getDesktop();
            try {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    desktop.browse(e.getURL().toURI());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(AboutDialog.this,
                        "Unable to open link in your default browser.",
                        "Unable to open link.", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
