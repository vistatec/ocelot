package com.vistatec.ocelot.services;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.events.UserProfileSaveEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

import java.io.IOException;

import com.google.inject.Inject;

/**
 * Handle provenance related functionality.
 */
public class ProvenanceService implements OcelotEventQueueListener {
    private final OcelotEventQueue eventQueue;
    private final ProvenanceConfig provConfig;

    @Inject
    public ProvenanceService(OcelotEventQueue eventQueue, ProvenanceConfig provConfig) {
        this.eventQueue = eventQueue;
        this.provConfig = provConfig;
    }

    @Subscribe
    public void saveUserProvenance(UserProfileSaveEvent profileSave) {
        try {
            provConfig.save(profileSave.getProfile());
            this.eventQueue.post(profileSave.new Success());

        } catch (IOException ex) {
            this.eventQueue.post(profileSave.new Failure(
                    "Failed to save user provenance. Check logs for details",
                    ex));
        }
    }
}
