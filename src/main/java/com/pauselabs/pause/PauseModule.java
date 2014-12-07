package com.pauselabs.pause;

import com.pauselabs.pause.core.PostFromAnyThreadBus;
import com.pauselabs.pause.models.PauseSession;
import com.pauselabs.pause.models.StringRandomizer;
import com.pauselabs.pause.services.PauseMessageReceivedService;
import com.pauselabs.pause.services.PauseSessionService;
import com.pauselabs.pause.ui.BlacklistActivity;
import com.pauselabs.pause.ui.BlacklistFragment;
import com.pauselabs.pause.ui.CameraActivity;
import com.pauselabs.pause.ui.CameraFragment;
import com.pauselabs.pause.ui.CreatePauseFragment;
import com.pauselabs.pause.ui.MainActivity;
import com.pauselabs.pause.ui.NavigationDrawerFragment;
import com.pauselabs.pause.ui.PauseFragmentActivity;
import com.pauselabs.pause.ui.PreviewActivity;
import com.pauselabs.pause.ui.PreviewFragment;
import com.pauselabs.pause.ui.ScoreboardActivity;
import com.pauselabs.pause.ui.ScoreboardFragment;
import com.pauselabs.pause.ui.SettingsActivity;
import com.pauselabs.pause.ui.SettingsFragment;
import com.pauselabs.pause.ui.SplashFragment;
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
                ScoreboardActivity.class,
                BlacklistActivity.class,
                BlacklistFragment.class,
                PauseSession.class,
                StringRandomizer.class
        }
)
public class PauseModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }
}