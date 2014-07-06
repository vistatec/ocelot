package com.vistatec.ocelot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.LQIDeselectionEvent;
import com.vistatec.ocelot.events.LQISelectionEvent;
import com.vistatec.ocelot.events.SegmentEditEvent;
import com.vistatec.ocelot.events.SegmentSelectionEvent;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.NewLanguageQualityIssueView;
import com.vistatec.ocelot.segment.Segment;

public class SegmentMenu {
    private JMenu menu;
    private JMenuItem menuAddIssue, menuRemoveIssue, menuRestoreTarget;
    private Segment selectedSegment;
    private LanguageQualityIssue selectedLQI;

    public SegmentMenu(EventBus eventBus) {
        menu = new JMenu("Segment");
        menuAddIssue = new JMenuItem("Add Issue");
        menuAddIssue.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                NewLanguageQualityIssueView addLQIView = new NewLanguageQualityIssueView();
                addLQIView.setSegment(selectedSegment);
                SwingUtilities.invokeLater(addLQIView);
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
                selectedSegment.resetTarget();
                menuRestoreTarget.setEnabled(false);
            }
        });
        menuRestoreTarget.setEnabled(false);
        menu.add(menuRestoreTarget);
        eventBus.register(this);
    }

    @Subscribe
    public void selectedSegment(SegmentSelectionEvent e) {
        menuAddIssue.setEnabled(true);
        menuRemoveIssue.setEnabled(false);
        menuRestoreTarget.setEnabled(e.getSegment().hasOriginalTarget());
        this.selectedSegment = e.getSegment();
    }

    @Subscribe
    public void segmentEdited(SegmentEditEvent e) {
        Segment seg = e.getSegment();
        if (seg.equals(selectedSegment)) {
            menuRestoreTarget.setEnabled(seg.hasOriginalTarget());
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
