package com.pauselabs.pause;

import com.pauselabs.pause.activity.PauseActivity;
import com.pauselabs.pause.activity.StartActivity;
import com.pauselabs.pause.adapters.SavesAdapter;
import com.pauselabs.pause.adapters.UpgradeAdapter;
import com.pauselabs.pause.controllers.SavesDirectoryViewController;
import com.pauselabs.pause.controllers.PrivacyViewController;
import com.pauselabs.pause.activity.SearchPrivacyActivity;
import com.pauselabs.pause.controllers.SettingsViewController;
import com.pauselabs.pause.controllers.SummaryViewController;
import com.pauselabs.pause.controllers.TimeBankViewController;
import com.pauselabs.pause.controllers.UpgradeViewController;
import com.pauselabs.pause.controllers.start.GenderViewController;
import com.pauselabs.pause.controllers.start.OnboardingViewController;
import com.pauselabs.pause.core.PostFromAnyThreadBus;
import com.pauselabs.pause.listeners.PauseSmsListener;
import com.pauselabs.pause.listeners.SilenceListener;
import com.pauselabs.pause.listeners.SpeechListener;
import com.pauselabs.pause.model.JsonReader;
import com.pauselabs.pause.model.PauseConversation;
import com.pauselabs.pause.model.PauseSession;
import com.pauselabs.pause.model.StringRandomizer;
import com.pauselabs.pause.services.PauseApplicationService;
import com.pauselabs.pause.services.PauseSessionService;
import com.pauselabs.pause.view.SummaryConversationCard;
import com.pauselabs.pause.view.SummaryReceivedCard;
import com.pauselabs.pause.view.SummarySentCard;
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

                PauseApplicationService.class,
                PauseSessionService.class,
                PauseSession.class,

                SpeechListener.class,
                SilenceListener.class,
                PauseSmsListener.class,

                StartActivity.class,
                PauseActivity.class,

                GenderViewController.class,
                OnboardingViewController.class,

                SummaryViewController.class,
                SummaryConversationCard.class,
                SummarySentCard.class,
                SummaryReceivedCard.class,

                SavesDirectoryViewController.class,
                SavesAdapter.class,

                PrivacyViewController.class,
                SearchPrivacyActivity.class,

                TimeBankViewController.class,

                UpgradeViewController.class,
                UpgradeAdapter.class,

                SettingsViewController.class,

                StringRandomizer.class,
                JsonReader.class,
                PauseConversation.class,
        }
)
public class PauseModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }
}