package com.vistatec.ocelot.services;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vistatec.ocelot.config.JsonConfigService;
import com.vistatec.ocelot.config.TransferException;
import com.vistatec.ocelot.events.UserProfileSaveEvent;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.events.api.OcelotEventQueueListener;

/**
 * Handle provenance related functionality.
 */
public class ProvenanceService implements OcelotEventQueueListener {
    private final OcelotEventQueue eventQueue;
    private final JsonConfigService cfgService;

    @Inject
    public ProvenanceService(OcelotEventQueue eventQueue, JsonConfigService cfgService) {
        this.eventQueue = eventQueue;
        this.cfgService = cfgService;
    }

    @Subscribe
    public void saveUserProvenance(UserProfileSaveEvent profileSave) {
        try {
            cfgService.saveUserProvenance(profileSave.getProfile());
            this.eventQueue.post(profileSave.new Success());

        } catch (TransferException ex) {
            this.eventQueue.post(profileSave.new Failure(
                    "Failed to save user provenance. Check logs for details",
                    ex));
        }
    }
}
