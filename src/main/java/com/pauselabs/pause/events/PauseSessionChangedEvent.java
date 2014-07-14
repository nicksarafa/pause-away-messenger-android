package com.pauselabs.pause.events;

/**
 * Pub/Sub event used to communicate between fragment and activity.
 * Subscription occurs in the {@link com.pauselabs.pause.ui.MainActivity}
 */
public class PauseSessionChangedEvent {
    private int sessionState;

    public PauseSessionChangedEvent(int sessionState) {
        this.sessionState = sessionState;
    }

    public int getSessionState() {
        return sessionState;
    }
}
