package com.pauselabs.pause.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.adapters.ContactsAdapter;
import com.pauselabs.pause.view.ContactListView;

import javax.inject.Inject;

/**
 * Created by tyndallm on 10/5/14.
 */
public class BlackListActivity extends ActionBarActivity {

    public ContactListView blackListActivityView;

    @Inject
    LayoutInflater inflater;

    ContactsAdapter contactsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        blackListActivityView = (ContactListView)inflater.inflate(R.layout.contact_list_view, null);
        setContentView(blackListActivityView);

        contactsAdapter = new ContactsAdapter(this);
        blackListActivityView.listView.setAdapter(contactsAdapter);


    }
}
