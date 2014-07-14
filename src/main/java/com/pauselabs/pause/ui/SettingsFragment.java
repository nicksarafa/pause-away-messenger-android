package com.pauselabs.pause.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.SavedPauseDataSource;

/**
 * Temporary debugging convenience fragment
 */
public class SettingsFragment extends Fragment implements View.OnClickListener{

    @InjectView(R.id.sendMmsBtn)
    Button sendTestMmsBtn;

    @InjectView(R.id.clearDatabase)
    Button clearDatabaseBtn;

    private SavedPauseDataSource mDatasource;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, null);

        // Inject Butterknife views
        Views.inject(this, view);

        mDatasource = new SavedPauseDataSource(getActivity());
        mDatasource.open();

        sendTestMmsBtn.setOnClickListener(this);
        clearDatabaseBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        mDatasource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        mDatasource.close();
        super.onPause();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        Injector.inject(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.sendMmsBtn:
                PauseApplication.sendMMSTestToSelf();
                break;
            case R.id.clearDatabase:
                // make sure session is not currently running
                if(PauseApplication.getCurrentSession() != null && PauseApplication.getCurrentSession().isActive()){
                    PauseApplication.stopPauseService();
                }

                mDatasource.deleteAllSavedPauseMessages();
        }

    }
}
