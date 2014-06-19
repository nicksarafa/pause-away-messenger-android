package com.pauselabs.pause;

import com.pauselabs.pause.ui.CreatePauseFragment;
import com.pauselabs.pause.ui.MainActivity;
import com.pauselabs.pause.ui.PauseFragmentActivity;
import com.pauselabs.pause.ui.SplashFragment;
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
                DeckardActivity.class,
                PauseFragmentActivity.class,
                SplashFragment.class,
                CreatePauseFragment.class
        }
)
public class PauseModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new Bus();
    }
}