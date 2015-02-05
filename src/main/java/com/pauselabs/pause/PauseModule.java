package com.pauselabs.pause;

import com.pauselabs.pause.activity.PrivacylistActivity;
import com.pauselabs.pause.activity.PrivacylistFragment;
import com.pauselabs.pause.activity.MainActivity;
import com.pauselabs.pause.activity.OnBoardingActivity;
import com.pauselabs.pause.controllers.PrivacyViewController;
import com.pauselabs.pause.controllers.SettingsViewController;
import com.pauselabs.pause.controllers.messages.ASCIIDirectoryViewController;
import com.pauselabs.pause.controllers.messages.SummaryViewController;
import com.pauselabs.pause.controllers.CustomPauseViewController;
import com.pauselabs.pause.controllers.onboarding.GenderViewController;
import com.pauselabs.pause.controllers.onboarding.InteractiveViewController;
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
import com.pauselabs.pause.view.ASCIISquareView;
import com.pauselabs.pause.view.GenderView;
import com.pauselabs.pause.view.InteractiveView;
import com.pauselabs.pause.view.SummaryConversationCard;
import com.pauselabs.pause.view.SummaryReceivedCard;
import com.pauselabs.pause.view.SummarySentCard;
import com.pauselabs.pause.view.tabs.CustomPauseView;
import com.pauselabs.pause.view.tabs.ASCIIDirectoryView;
import com.pauselabs.pause.view.tabs.PrivacyView;
import com.pauselabs.pause.view.tabs.SettingsView;
import com.pauselabs.pause.view.tabs.SummaryView;
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
                MainActivity.class,
                PrivacylistActivity.class,
                PrivacylistFragment.class,

                GenderViewController.class,
                GenderView.class,
                InteractiveViewController.class,
                InteractiveView.class,

                ASCIIDirectoryViewController.class,
                ASCIIDirectoryView.class,

                CustomPauseViewController.class,
                CustomPauseView.class,

                ASCIIDirectoryViewController.class,
                ASCIIDirectoryView.class,
                ASCIISquareView.class,

                SettingsViewController.class,
                SettingsView.class,

                SummaryViewController.class,
                SummaryView.class,
                SummaryConversationCard.class,
                SummarySentCard.class,
                SummaryReceivedCard.class,

                PrivacyViewController.class,
                PrivacyView.class,

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