package com.vistatec.ocelot;

import com.vistatec.ocelot.its.ITSMetadata;
import com.vistatec.ocelot.its.LanguageQualityIssue;
import com.vistatec.ocelot.its.NewLanguageQualityIssueView;
import com.vistatec.ocelot.segment.Segment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * ITS Metadata context menu.
 */
public class ContextMenu extends JPopupMenu implements ActionListener {
    /**
     * Default serial ID
     */
    private static final long serialVersionUID = 2L;
    private JMenuItem addLQI, removeLQI, resetTarget;
    private Segment selectedSeg;
    private LanguageQualityIssue selectedLQI;

    public ContextMenu(Segment selectedSeg) {
        this.selectedSeg = selectedSeg;

        addLQI = new JMenuItem("Add Issue");
        addLQI.addActionListener(this);
        addLQI.setEnabled(selectedSeg.isEditablePhase());
        add(addLQI);

        resetTarget = new JMenuItem("Reset Target");
        resetTarget.addActionListener(this);
        resetTarget.setEnabled(true);
        add(resetTarget);
    }

    public ContextMenu(Segment selectedSeg, LanguageQualityIssue selectedLQI) {
        this(selectedSeg);
        this.selectedLQI = selectedLQI;

        removeLQI = new JMenuItem("Remove Issue");
        removeLQI.addActionListener(this);
        add(removeLQI);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addLQI) {
            NewLanguageQualityIssueView addLQIView = new NewLanguageQualityIssueView();
            addLQIView.setSegment(selectedSeg);
            SwingUtilities.invokeLater(addLQIView);
        } else if (e.getSource() == removeLQI) {
            selectedSeg.removeLQI(selectedLQI);
        } else if (e.getSource() == resetTarget) {
            selectedSeg.resetTarget();
        }
    }
}
