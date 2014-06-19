package com.pauselabs.pause.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import butterknife.Views;
import com.pauselabs.pause.Injector;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Base class for all Pause Activities that need fragments
 */
public class PauseFragmentActivity extends ActionBarActivity{

    @Inject
    protected Bus eventBus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public void setContentView(final int layoutResId) {
        super.setContentView(layoutResId);

        Views.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }
}
