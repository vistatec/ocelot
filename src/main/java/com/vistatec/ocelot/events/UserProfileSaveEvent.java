package com.vistatec.ocelot.events;

import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.events.api.OcelotEvent;

public class UserProfileSaveEvent implements OcelotEvent {
    private final UserProvenance profile;

    public UserProfileSaveEvent(UserProvenance profile) {
        this.profile = profile;
    }

    public UserProvenance getProfile() {
        return this.profile;
    }

    public class Success implements OcelotEvent {}
    public class Failure implements OcelotEvent {
        public final String failureMsg;
        public final Exception ex;

        public Failure(String failureMsg, Exception ex) {
            this.failureMsg = failureMsg;
            this.ex = ex;
        }
    }
}
