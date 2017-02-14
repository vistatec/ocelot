package com.vistatec.ocelot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.LqiJsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.events.LQIDeselectionEvent;
import com.vistatec.ocelot.events.LQISelectionEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.events.SegmentTargetResetEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.its.model.LanguageQualityIssue;
import com.vistatec.ocelot.its.view.LanguageQualityIssuePropsPanel;
import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;

public class SegmentMenu implements OcelotEventQueueListener {
    private static final Logger LOG = LoggerFactory.getLogger(SegmentMenu.class);

    private JMenu menu;
    private JMenuItem menuAddIssue, menuRemoveIssue, menuRestoreTarget;
    private OcelotSegment selectedSegment;
    private XLIFFDocument xliff;
    private LanguageQualityIssue selectedLQI;

    private LanguageQualityIssuePropsPanel addLQIView = null;

    public SegmentMenu(final OcelotEventQueue eventQueue, int platformKeyMask, final LqiJsonConfigService lqiService) {
        menu = new JMenu("Segment");
        menuAddIssue = new JMenuItem("Add Issue");
        menuAddIssue.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, platformKeyMask));
        menuAddIssue.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                /**
                 * This is a gross workaround for a bizarre bug in the 1.7 Mac
                 * JRE, which results in the VK_EQUALS key event being sent
                 * 3 separate times. In order to prevent 3 separate dialogs
                 * from opening, we trap the extraneous events.
                 * This seems to be fixed in the 1.8 runtime.  See OC-41 for more.
                 */
                if (addLQIView == null) {
                    try {
                        addLQIView = new LanguageQualityIssuePropsPanel(eventQueue, lqiService.readLQIConfig());
                        addLQIView.setWindowListener(new AddLQIViewWindowListener());
                        addLQIView.setSegment(selectedSegment);
                        SwingUtilities.invokeLater(addLQIView);
                    } catch (TransferException e1) {
                        LOG.warn("Unable to parse LQI configuration: {}", e1.getMessage());
                    }
                }
            }
        });
        menuAddIssue.setEnabled(false);
        menu.add(menuAddIssue);
        menuRemoveIssue = new JMenuItem("Remove Issue");
        menuRemoveIssue.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                selectedSegment.removeLQI(selectedLQI);
            }
        });
        menuRemoveIssue.setEnabled(false);
        menu.add(menuRemoveIssue);
        menuRestoreTarget = new JMenuItem("Reset Target");
        menuRestoreTarget.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                eventQueue.post(new SegmentTargetResetEvent(xliff, selectedSegment));
            }
        });
        menuRestoreTarget.setEnabled(false);
        menu.add(menuRestoreTarget);
    }

    class AddLQIViewWindowListener extends WindowAdapter {
        @Override
        public void windowClosed(WindowEvent e) {
            addLQIView = null;
        }
    }

    @Subscribe
    public void selectedSegment(SegmentSelectionEvent e) {
        menuAddIssue.setEnabled(true);
        menuRemoveIssue.setEnabled(false);
        menuRestoreTarget.setEnabled(e.getSegment().hasOriginalTarget());
        this.selectedSegment = e.getSegment();
        this.xliff = e.getDocument();
    }

    @Subscribe
    public void segmentEdited(SegmentEditEvent e) {
        OcelotSegment seg = e.getSegment();
        if (seg.equals(selectedSegment)) {
            menuRestoreTarget.setEnabled(seg.hasOriginalTarget() &&
                    seg.getTarget().getDisplayText().equals(
                            seg.getOriginalTarget().getDisplayText()));
        }
    }

    @Subscribe
    public void selectedLQI(LQISelectionEvent e) {
        menuRemoveIssue.setEnabled(e.getLQI() != null);
        this.selectedLQI = e.getLQI();
    }

    @Subscribe
    public void deselectedLQI(LQIDeselectionEvent e) {
        selectedLQI = null;
        menuRemoveIssue.setEnabled(false);
    }

    public JMenu getMenu() {
        return menu;
    }
}
