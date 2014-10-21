package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.adapters.ConversationAdapter;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.events.PauseMessageReceivedEvent;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.models.PauseSession;
import com.pauselabs.pause.views.AutoUpdatingTimerView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;

/**
 * This fragment is responsible for displaying the Pause Scoreboard during a Pause Session.
 */
public class ScoreboardFragment extends Fragment implements View.OnClickListener{

    @Inject
    protected Bus mBus;

    @InjectView(R.id.scoreboardList)
    ListView mScoreboardList;

    @InjectView(R.id.stopPauseSessionBtn)
    Button mStopPauseSessionBtn;

    @InjectView(R.id.editPauseSessionBtn)
    Button mEditPauseSessionBtn;

    TextView mScoreboardResponseCount;

    @InjectView(R.id.scoreboardBackgroundImage)
    ImageView mBackgroundImage;

    @InjectView(R.id.timerView)
    AutoUpdatingTimerView mTimerView;

    @InjectView(R.id.noMessagesContainer)
    LinearLayout mNoMessagesContainer;

    private ConversationAdapter mConversationAdapter;
    private SavedPauseDataSource datasource;
    private PauseBounceBackMessage mActivePauseBounceBack;
    private PauseSession mActiveSession;
    private Tracker mAnalyticsTracker;
    private Boolean attemptedToLoadScoreboard = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scoreboard_fragment, null);

        // Inject Butterknife views
        Views.inject(this, view);

        // Add header & footeer to listview
        LinearLayout listHeaderView = (LinearLayout) inflater.inflate(R.layout.scoreboard_header, null);
        mScoreboardResponseCount = (TextView) listHeaderView.findViewById(R.id.scoreboardTitle);

        mScoreboardList.addHeaderView(listHeaderView);
        mScoreboardList.setAdapter(mConversationAdapter);

        mStopPauseSessionBtn.setOnClickListener(this);
        mEditPauseSessionBtn.setOnClickListener(this);

        initScoreboardUI();

        mAnalyticsTracker.send(new HitBuilders.AppViewBuilder().build());

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        mConversationAdapter = new ConversationAdapter(getActivity());
        datasource = new SavedPauseDataSource(getActivity());
        datasource.open();

        mAnalyticsTracker =  PauseApplication.getTracker(PauseApplication.TrackerName.GLOBAL_TRACKER);
        mAnalyticsTracker.setScreenName("ScoreboardScreenView");
    }

    public void initScoreboardUI() {
        mActiveSession = PauseApplication.getCurrentSession();
        mActivePauseBounceBack = mActiveSession.getActiveBounceBackMessage();

        if(mActivePauseBounceBack.getPathToOriginal() != null && !mActivePauseBounceBack.getPathToOriginal().equals("")){
            BitmapDrawable pauseImage = new BitmapDrawable(getResources(), mActivePauseBounceBack.getPathToOriginal());
            mBackgroundImage.setImageDrawable(pauseImage);
        }

        if(mConversationAdapter != null){
            mConversationAdapter.updateAdapter(mActiveSession.getConversations());
        }

        mTimerView.setEndTime(mActivePauseBounceBack.getEndTime());
        mTimerView.startTimer();

        updateScoreboardUI();
        attemptedToLoadScoreboard = true;
    }

    public void updateScoreboardUI(){
        if(mActiveSession != null && mActiveSession.isActive() && mActivePauseBounceBack != null){
            // hide noMessagesContainer if we've received a message
            if(mActiveSession.getConversations().size() > 0){
                mNoMessagesContainer.setVisibility(View.GONE);
            }

            String responseMessagesSent = getString(R.string.response_count);
            mScoreboardResponseCount.setText(mActiveSession.getResponseCount() + " " + responseMessagesSent);
        }
        else{
            if(!attemptedToLoadScoreboard) {
                initScoreboardUI();
            }
        }
    }

    private void stopPauseSession() {
        PauseApplication.stopPauseService();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
        datasource.open();
        updateScoreboardUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
        datasource.close();
        mTimerView.stopTimer();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        Injector.inject(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.stopPauseSessionBtn:
                stopPauseSession();

                Intent createPauseIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(createPauseIntent);
                break;
            case R.id.editPauseSessionBtn:
                stopPauseSession();

                Intent editPauseIntent = new Intent(getActivity(), MainActivity.class);
                editPauseIntent.putExtra(Constants.Pause.EDIT_PAUSE_MESSAGE_ID_EXTRA, mActivePauseBounceBack.getId());
                startActivity(editPauseIntent);
                break;
        }
    }

    @Subscribe
    public void onPauseMessageReceivedEvent(PauseMessageReceivedEvent event) {
        mConversationAdapter.updateAdapter(mActiveSession.getConversations());
        updateScoreboardUI();
    }

}
