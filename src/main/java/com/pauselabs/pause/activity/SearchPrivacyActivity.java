package com.pauselabs.pause.activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.adapters.contacts.ContactsSearchAdapter;
import com.pauselabs.pause.core.ContactsQuery;
import com.pauselabs.pause.view.tabs.SearchPrivacyView;

import javax.inject.Inject;

/**
 * Created by Admin on 1/28/15.
 */
public class SearchPrivacyActivity extends FragmentActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, AbsListView.OnScrollListener {

    private final String TAG = SearchPrivacyActivity.class.getSimpleName();

    public SearchPrivacyView searchPrivacyView;

    private ContactsSearchAdapter mAdapter;

    private String mSearchTerm;

    @Inject
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);

        searchPrivacyView = (SearchPrivacyView)inflater.inflate(R.layout.search_privacy_view,null);
        setContentView(searchPrivacyView);

        Intent sentIntent = getIntent();

        // Create the main contacts adapter
        mAdapter = new ContactsSearchAdapter(this, sentIntent.getBooleanExtra("usingIce",true));
        searchPrivacyView.contactList.setAdapter(mAdapter);
        searchPrivacyView.contactList.setOnItemClickListener(this);
        searchPrivacyView.contactList.setOnScrollListener(this);
        getSupportLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, mAdapter);

        ((TextView)searchPrivacyView.findViewById(R.id.search_type)).setText(mAdapter.usingIce ? "ice" : "black");

        searchPrivacyView.selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.selectAll();
            }
        });

        searchPrivacyView.contactSearchField.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchPrivacyView.contactSearchField.setSearchableInfo(searchableInfo);
        searchPrivacyView.contactSearchField.setOnQueryTextListener(this);

        searchPrivacyView.contactSearchField.setQueryHint("Search Contacts");
        searchPrivacyView.contactSearchField.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Gets the Cursor object currently bound to the ListView
        final Cursor cursor = mAdapter.getCursor();

        // Moves to the Cursor row corresponding to the ListView item that was clicked
        cursor.moveToPosition(position);

        // Creates a contact lookup Uri from contact ID and lookup_key
        final Uri uri = ContactsContract.Contacts.getLookupUri(cursor.getLong(ContactsQuery.ID), cursor.getString(ContactsQuery.LOOKUP_KEY));
    }

    @Override
    public boolean onQueryTextSubmit(String queryText) {
        // Nothing needs to happen when the user submits the search string
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Updates
        // the search filter, and restarts the loader to do a new query
        // using the new search string.
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

        // Don't do anything if the filter is empty
        if (mSearchTerm == null && newFilter == null) {
            return true;
        }

        // Don't do anything if the new filter is the same as the current filter
        if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
            return true;
        }

        // Updates current filter to new filter
        mSearchTerm = newFilter;
        mAdapter.updateSearchTerm(mSearchTerm);

        // Restarts the loader. This triggers onCreateLoader(), which builds the
        // necessary content Uri from mSearchTerm.
        getSupportLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, mAdapter);

        return true;
    }
}
