package com.vistatec.ocelot.di;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.vistatec.ocelot.events.api.EventBusWrapper;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.its.stats.ITSDocStats;

/**
 * Main Ocelot object dependency context module.
 */
public class OcelotModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OcelotEventQueue.class).to(EventBusWrapper.class).in(Scopes.SINGLETON);
        bind(ITSDocStats.class).in(Scopes.SINGLETON);
    }

}
