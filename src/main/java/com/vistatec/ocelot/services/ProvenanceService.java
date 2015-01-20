package com.vistatec.ocelot.services;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.events.UserProfileSaveEvent;
import java.io.IOException;

/**
 * Handle provenance related functionality.
 */
public class ProvenanceService {
    private final EventBus eventBus;
    private final ProvenanceConfig provConfig;

    public ProvenanceService(EventBus eventBus, ProvenanceConfig provConfig) {
        this.eventBus = eventBus;
        this.provConfig = provConfig;
    }

    @Subscribe
    public void saveUserProvenance(UserProfileSaveEvent profileSave) {
        try {
            provConfig.save(profileSave.getProfile());
            this.eventBus.post(profileSave.new Success());

        } catch (IOException ex) {
            this.eventBus.post(profileSave.new Failure(
                    "Failed to save user provenance. Check logs for details",
                    ex));
        }
    }
}
