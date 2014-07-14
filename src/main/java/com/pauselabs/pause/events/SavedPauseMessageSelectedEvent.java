package com.pauselabs.pause.events;

/**
 * Pub/Sub event used to communicate between NavigationDrawerFragment and CreatePauseFragment.
 * Subscription occurs in the {@link com.pauselabs.pause.ui.MainActivity}
 */
public class SavedPauseMessageSelectedEvent {

    private long savedMessageId;

    public SavedPauseMessageSelectedEvent(long savedMessageId) {
        this.savedMessageId = savedMessageId;
    }

    public long getSavedMessageId() {
        return savedMessageId;
    }
}
