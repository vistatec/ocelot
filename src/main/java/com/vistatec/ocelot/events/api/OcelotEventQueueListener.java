package com.vistatec.ocelot.events.api;

/**
 * Signifies that the implementing object is expecting to receive
 * {@link OcelotEvent}s to its appropriately annotated methods. Note: this only
 * ensures that objects that want to listen to events posted on the
 * {@link EventQueue} must be an implementation of this interface; this does
 * not mean all implementing objects will have methods that are listening for an
 * {@link OcelotEvent}
 */
public interface OcelotEventQueueListener {}