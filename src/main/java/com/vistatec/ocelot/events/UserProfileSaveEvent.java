package com.vistatec.ocelot.events;

import com.vistatec.ocelot.config.UserProvenance;

public class UserProfileSaveEvent {
    private final UserProvenance profile;

    public UserProfileSaveEvent(UserProvenance profile) {
        this.profile = profile;
    }

    public UserProvenance getProfile() {
        return this.profile;
    }

    public class Success {}
    public class Failure {
        public final String failureMsg;
        public final Exception ex;

        public Failure(String failureMsg, Exception ex) {
            this.failureMsg = failureMsg;
            this.ex = ex;
        }
    }
}
