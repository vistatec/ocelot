package com.vistatec.ocelot.services;

import com.google.common.eventbus.Subscribe;
import com.vistatec.ocelot.events.UserProfileSaveEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

import com.google.inject.Inject;
import com.vistatec.ocelot.config.ConfigService;
import com.vistatec.ocelot.config.ConfigTransferService;

/**
 * Handle provenance related functionality.
 */
public class ProvenanceService implements OcelotEventQueueListener {
    private final OcelotEventQueue eventQueue;
    private final ConfigService cfgService;

    @Inject
    public ProvenanceService(OcelotEventQueue eventQueue, ConfigService cfgService) {
        this.eventQueue = eventQueue;
        this.cfgService = cfgService;
    }

    @Subscribe
    public void saveUserProvenance(UserProfileSaveEvent profileSave) {
        try {
            cfgService.saveUserProvenance(profileSave.getProfile());
            this.eventQueue.post(profileSave.new Success());

        } catch (ConfigTransferService.TransferException ex) {
            this.eventQueue.post(profileSave.new Failure(
                    "Failed to save user provenance. Check logs for details",
                    ex));
        }
    }
}
