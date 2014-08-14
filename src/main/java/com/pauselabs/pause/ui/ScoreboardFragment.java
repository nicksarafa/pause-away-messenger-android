package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.adapters.ConversationAdapter;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.events.PauseMessageReceivedEvent;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.models.PauseSession;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import java.util.Date;

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

    @InjectView(R.id.timeRemaining)
    TextView mTimeRemainingView;

    @InjectView(R.id.noMessagesContainer)
    LinearLayout mNoMessagesContainer;

    private ConversationAdapter mConversationAdapter;
    private SavedPauseDataSource datasource;
    private PauseBounceBackMessage mActivePauseBounceBack;
    private PauseSession mActiveSession;

    private boolean mStophandler = false;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(!mStophandler) {
                updateTimeRemaining();
                handler.postDelayed(this, 1000);
            }
        }
    };

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

        handler.post(runnable);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        mConversationAdapter = new ConversationAdapter(getActivity());
        datasource = new SavedPauseDataSource(getActivity());
        datasource.open();

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

        updateScoreboardUI();
    }

    public void updateScoreboardUI(){
        if(mActiveSession != null && mActiveSession.isActive() && mActivePauseBounceBack != null){
            // hide noMessagesContainer if we've received a message
            if(mActiveSession.getConversations().size() > 0){
                mNoMessagesContainer.setVisibility(View.GONE);
            }

            String responseMessagesSent = getString(R.string.response_count);
            mScoreboardResponseCount.setText(mActiveSession.getResponseCount() + " " + responseMessagesSent);

            //mConversationAdapter.notifyDataSetChanged();
        }
        else{
            initScoreboardUI();
        }
    }

    public void updateTimeRemaining() {
        if(mActivePauseBounceBack != null){
            if(mActivePauseBounceBack.getEndTime() == 0){
                mTimeRemainingView.setText("Until stopped");
                mStophandler = true; // temporarily stop runnable
            }
            else{
                Date endDate = new Date(mActivePauseBounceBack.getEndTime());
                Date currentDate = new Date();

                if(currentDate.getTime() > endDate.getTime()){
                    // timer has expired
                    stopPauseSession();

                    // display results dialog
                    mStophandler = true;

                    mTimeRemainingView.setText("Session has ended");

                }
                else{
                    long diff = endDate.getTime() - currentDate.getTime();
                    long seconds = diff / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;
                    mTimeRemainingView.setText(hours % 24 + "h " + minutes % 60 + "m " + seconds % + 60 + "s");
                }


            }

        }
    }

    private void stopPauseSession() {
        PauseApplication.stopPauseService();
    }

    private void displayResultsDialog() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
        datasource.open();
        updateScoreboardUI();
        runnable.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
        datasource.close();
        handler.removeCallbacks(runnable);
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
                //mBus.post(new PauseSessionChangedEvent(Constants.Pause.PAUSE_SESSION_STATE_STOPPED));
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
