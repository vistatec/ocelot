package com.vistatec.ocelot.events.api;

/**
 * A thin wrapper of the Guava {@link com.google.common.eventbus.EventBus}
 * to allow for mock test event queues. May want to consider a stronger
 * abstraction in case the implementation is changed (relies on
 * {@link com.google.common.eventbus.Subscribe} annotations for the listeners).
 */
public interface OcelotEventQueue {

    public void post(OcelotEvent event);

    public void registerListener(OcelotEventQueueListener listener);

    public void unregisterListener(OcelotEventQueueListener listener);
}
