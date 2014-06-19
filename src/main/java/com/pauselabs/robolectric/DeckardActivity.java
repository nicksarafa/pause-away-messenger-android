package com.pauselabs.robolectric;

import android.app.Activity;
import android.os.Bundle;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class DeckardActivity extends Activity {

    @Inject
    protected Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deckard);

        Injector.inject(this);


    }
}
