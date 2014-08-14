package com.pauselabs.pause;

import com.pauselabs.pause.core.PostFromAnyThreadBus;
import com.pauselabs.pause.services.PauseMessageReceivedService;
import com.pauselabs.pause.services.PauseSessionService;
import com.pauselabs.pause.ui.*;
import com.pauselabs.robolectric.DeckardActivity;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

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
                DeckardActivity.class,
                PauseFragmentActivity.class,
                SplashFragment.class,
                CreatePauseFragment.class,
                SettingsFragment.class,
                ScoreboardFragment.class,
                NavigationDrawerFragment.class,
                PauseMessageReceivedService.class,
                PauseSessionService.class,
                CameraFragment.class,
                CameraActivity.class,
                PreviewActivity.class,
                PreviewFragment.class,
                ScoreboardActivity.class
        }
)
public class PauseModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }
}