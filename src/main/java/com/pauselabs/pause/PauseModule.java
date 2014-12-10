package com.pauselabs.pause;

import com.pauselabs.pause.core.PostFromAnyThreadBus;
import com.pauselabs.pause.models.PauseSession;
import com.pauselabs.pause.models.StringRandomizer;
import com.pauselabs.pause.services.PauseMessageReceivedService;
import com.pauselabs.pause.services.PauseSessionService;
import com.pauselabs.pause.ui.BlacklistActivity;
import com.pauselabs.pause.ui.BlacklistFragment;
import com.pauselabs.pause.ui.MainActivity;
import com.pauselabs.pause.ui.OnBoardingActivity;
import com.pauselabs.pause.ui.PrivacyActivity;
import com.pauselabs.pause.ui.SettingsActivity;
import com.pauselabs.robolectric.DeckardActivity;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
        complete = false,

        injects = {
                PauseApplication.class,
                MainActivity.class,
                SettingsActivity.class,
                PrivacyActivity.class,
                DeckardActivity.class,
                PauseMessageReceivedService.class,
                PauseSessionService.class,
                BlacklistActivity.class,
                BlacklistFragment.class,
                PauseSession.class,
                StringRandomizer.class,
                OnBoardingActivity.class
        }
)
public class PauseModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }
}