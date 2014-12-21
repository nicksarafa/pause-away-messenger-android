package com.pauselabs.pause.ui;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.pauselabs.R;

/**
 * Created by Passa on 12/7/14.
 */
public class PrivacyActivity extends ActivityGroup {

    private TabHost tabhost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.privacy_activity);

        tabhost = (TabHost) findViewById(R.id.tabhost);
        tabhost.setup(getLocalActivityManager());

        TabHost.TabSpec spec_a,spec_b;

        spec_a = tabhost.newTabSpec("ice");
        spec_a.setIndicator("",getResources().getDrawable(R.drawable.ice_tab_selector));
        spec_a.setContent(new Intent(this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabhost.addTab(spec_a);

        spec_b = tabhost.newTabSpec("whitelist");
        spec_b.setIndicator("",getResources().getDrawable(R.drawable.list_tab_selector));
        spec_b.setContent(new Intent(this, SettingsLayout.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabhost.addTab(spec_b);

        tabhost.setCurrentTabByTag("ice");

    }
}
