package com.pauselabs.pause;

import com.pauselabs.pause.controller.NoSessionViewController;
import com.pauselabs.pause.controller.SettingsViewController;
import com.pauselabs.pause.core.PostFromAnyThreadBus;
import com.pauselabs.pause.listeners.PauseSmsListener;
import com.pauselabs.pause.listeners.SilenceListener;
import com.pauselabs.pause.listeners.SpeechListener;
import com.pauselabs.pause.model.JsonReader;
import com.pauselabs.pause.model.PauseConversation;
import com.pauselabs.pause.model.PauseSession;
import com.pauselabs.pause.model.StringRandomizer;
import com.pauselabs.pause.view.NoSessionView;
import com.pauselabs.pause.view.SummaryCard;
import com.pauselabs.pause.services.PauseApplicationService;
import com.pauselabs.pause.services.PauseSessionService;
import com.pauselabs.pause.activity.BlacklistActivity;
import com.pauselabs.pause.activity.BlacklistFragment;
import com.pauselabs.pause.view.CustomPauseView;
import com.pauselabs.pause.activity.HomeActivity;
import com.pauselabs.pause.activity.OnBoardingActivity;
import com.pauselabs.pause.view.SettingsView;
import com.pauselabs.pause.view.SummaryView;
import com.pauselabs.pause.controller.SummaryViewController;
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

                OnBoardingActivity.class,
                HomeActivity.class,
                BlacklistActivity.class,
                BlacklistFragment.class,

                NoSessionViewController.class,
                NoSessionView.class,

                SummaryViewController.class,
                SummaryView.class,
                SummaryCard.class,

                CustomPauseView.class,

                SettingsViewController.class,
                SettingsView.class,

                PauseApplicationService.class,
                PauseSessionService.class,
                PauseSession.class,

                SpeechListener.class,
                SilenceListener.class,
                PauseSmsListener.class,

                StringRandomizer.class,
                JsonReader.class,
                PauseConversation.class

        }
)
public class PauseModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }
}