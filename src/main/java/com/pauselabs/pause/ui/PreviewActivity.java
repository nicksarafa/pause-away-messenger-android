package com.pauselabs.pause.ui;

import android.os.Bundle;
import android.view.WindowManager;
import com.pauselabs.R;

/**
 * Activity responsible for displaying preview fragment
 */
public class PreviewActivity extends PauseFragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preview_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

    }
}
