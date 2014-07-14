package com.pauselabs.pause.ui;

import android.app.Activity;
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
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.adapters.ConversationAdapter;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.events.PauseMessageReceivedEvent;
import com.pauselabs.pause.events.PauseSessionChangedEvent;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.models.PauseSession;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

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

    @InjectView(R.id.scoreboardBackgroundImage)
    ImageView mBackgroundImage;

    private ConversationAdapter mConversationAdapter;
    private SavedPauseDataSource datasource;
    private PauseBounceBackMessage mActivePause;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scoreboard_fragment, null);

        // Inject Butterknife views
        Views.inject(this, view);

        // Add header & footeer to listview
        LinearLayout listHeaderView = (LinearLayout) inflater.inflate(R.layout.scoreboard_header, null);

        mScoreboardList.addHeaderView(listHeaderView);
        mScoreboardList.setAdapter(mConversationAdapter);

        mStopPauseSessionBtn.setOnClickListener(this);

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


    public void updateScoreboardUI(){
        // Retrieve active Pause Session
        PauseSession currentSession = PauseApplication.getCurrentSession();
        if(currentSession != null && currentSession.isActive()){
            PauseBounceBackMessage currentBounceBackMessage = currentSession.getActiveBounceBackMessage();
            BitmapDrawable pauseImage = new BitmapDrawable(getResources(), currentBounceBackMessage.getPathToImage());
            mBackgroundImage.setImageDrawable(pauseImage);
            mConversationAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
        datasource.open();

    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
        datasource.close();
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
                mBus.post(new PauseSessionChangedEvent(Constants.Pause.PAUSE_SESSION_STATE_STOPPED));
                break;
        }
    }

    @Subscribe
    public void onPauseMessageReceivedEvent(PauseMessageReceivedEvent event) {
        mConversationAdapter.updateAdapter(PauseApplication.getCurrentSession().getConversations());
    }
}
