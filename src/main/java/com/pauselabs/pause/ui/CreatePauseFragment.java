package com.pauselabs.pause.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Create Pause Screen Fragment, this is the default view of the app
 */
public class CreatePauseFragment extends Fragment implements View.OnClickListener{

    @Inject
    protected Bus bus;

    @InjectView(R.id.startPauseSessionBtn)
    Button startPauseSessionBtn;

    @InjectView(R.id.stopPauseSessionBtn)
    Button stopPauseSessionBtn;

    @InjectView(R.id.sendMmsBtn)
    Button sendMmsBtn;

    @InjectView(R.id.sessionStateTxt)
    TextView sessionStateTextView;

    public CreatePauseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_pause_fragment, null);

        // Inject Butterknife views
        Views.inject(this, view);

        startPauseSessionBtn.setOnClickListener(this);
        stopPauseSessionBtn.setOnClickListener(this);
        sendMmsBtn.setOnClickListener(this);

        return view;

    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        Injector.inject(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.startPauseSessionBtn:
                PauseApplication.startPauseService();
                startPauseSessionBtn.setEnabled(false);
                stopPauseSessionBtn.setEnabled(true);
                sessionStateTextView.setText(getResources().getText(R.string.pauseSessionActive));
                break;
            case R.id.stopPauseSessionBtn:
                PauseApplication.stopPauseService();
                startPauseSessionBtn.setEnabled(true);
                stopPauseSessionBtn.setEnabled(false);
                sessionStateTextView.setText(getResources().getText(R.string.pauseSessionInactive));
                break;
            case R.id.sendMmsBtn:
                PauseApplication.sendMMSTestToSelf();
                break;


        }

    }
}
