package com.vistatec.ocelot.freme.manager;

import java.awt.Window;

import javax.swing.JFrame;

import com.vistatec.ocelot.events.SegmentVariantSelectionEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;
import com.vistatec.ocelot.segment.model.okapi.FragmentVariant;

public class EnrichmentManager implements OcelotEventQueueListener{
    
    private FragmentVariant selectedSegmentVariant;
    
//    private E
    private JFrame mainFrame;
    
public EnrichmentManager(final JFrame ocelotMainFrame) {
    
    this.mainFrame = ocelotMainFrame;
    
}
    
    public void handleSegmentVariantSelected(SegmentVariantSelectionEvent e)
 {

        if (e.getSegmentVariant() instanceof FragmentVariant
                && (selectedSegmentVariant == null || !selectedSegmentVariant
                        .equals(e.getSegmentVariant()))) {
            selectedSegmentVariant = (FragmentVariant) e.getSegmentVariant();
            enrichContent(selectedSegmentVariant);
        }
    }

    private void enrichContent(FragmentVariant selectedSegmentVariant2) {

        
    }

}
