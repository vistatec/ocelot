package com.vistatec.ocelot.events.api;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

/**
 * Wraps a Guava {@link com.google.common.eventbus.EventBus}.
 */
public class EventBusWrapper implements OcelotEventQueue {
    private final EventBus eventBus;

    @Inject
    public EventBusWrapper(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void post(OcelotEvent event) {
        this.eventBus.post(event);
    }

    @Override
    public void registerListener(OcelotEventQueueListener listener) {
        this.eventBus.register(listener);
    }

    @Override
    public void unregisterListener(OcelotEventQueueListener listener) {
        this.eventBus.unregister(listener);
    }

}
