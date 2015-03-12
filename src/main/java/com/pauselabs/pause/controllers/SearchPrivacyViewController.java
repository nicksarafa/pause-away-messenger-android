package com.pauselabs.pause.controllers;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.adapters.ContactsAdapter;
import com.pauselabs.pause.core.ContactsQuery;
import com.pauselabs.pause.view.tabs.PrivacyActionBtnView;
import com.pauselabs.pause.view.tabs.SearchPrivacyView;

import javax.inject.Inject;

/**
 * Created by Admin on 1/28/15.
 */
public class SearchPrivacyViewController implements View.OnClickListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor>, AbsListView.OnScrollListener {

    private final String TAG = SearchPrivacyViewController.class.getSimpleName();

    public SearchPrivacyView searchSearchPrivacyView;
    public PrivacyActionBtnView privacyBtns;

    private ContactsAdapter mAdapter;

    private String mSearchTerm;

    @Inject
    LayoutInflater inflater;

    public SearchPrivacyViewController() {
        Injector.inject(this);
        
        searchSearchPrivacyView = (SearchPrivacyView)inflater.inflate(R.layout.privacy_list_view,null);
        privacyBtns = (PrivacyActionBtnView)inflater.inflate(R.layout.privacy_action_view, null);

        // Create the main contacts adapter
        mAdapter = new ContactsAdapter(PauseApplication.pauseActivity);
        searchSearchPrivacyView.contactList.setAdapter(mAdapter);
        searchSearchPrivacyView.contactList.setOnItemClickListener(this);
        searchSearchPrivacyView.contactList.setOnScrollListener(this);

        searchSearchPrivacyView.selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.selectAll();
            }
        });

        searchSearchPrivacyView.contactSearchField.setOnQueryTextListener(this);

        // Set visibility to hidden by default
        privacyBtns.setVisibility(View.INVISIBLE);
        privacyBtns.atnBtn1.setOnClickListener(this);

        SearchManager searchManager = (SearchManager) PauseApplication.pauseActivity.getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(PauseApplication.pauseActivity.getComponentName());
        searchSearchPrivacyView.contactSearchField.setSearchableInfo(searchableInfo);
        searchSearchPrivacyView.contactSearchField.setOnQueryTextListener(this);

        searchSearchPrivacyView.contactSearchField.setQueryHint("Search Contacts");
        searchSearchPrivacyView.contactSearchField.setBackgroundColor(Color.WHITE);

        PauseApplication.pauseActivity.getSupportLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ice_atn_1:
                Log.i("IceController","Action Button 1 Pressed");

                break;
        }
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
        PauseApplication.pauseActivity.getSupportLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, this);

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // If this is the loader for finding contacts in the Contacts Provider
        // (the only one supported)
        if (id == ContactsQuery.QUERY_ID) {
            Uri contentUri;

            // There are two types of searches, one which displays all contacts and
            // one which filters contacts by a search query. If mSearchTerm is set
            // then a search query has been entered and the latter should be used.

            if (mSearchTerm == null) {
                // Since there's no search string, use the content URI that searches the entire
                // Contacts table
                contentUri = ContactsQuery.CONTENT_URI;
            } else {
                // Since there's a search string, use the special content Uri that searches the
                // Contacts table. The URI consists of a base Uri and the search string.
                contentUri = Uri.withAppendedPath(ContactsQuery.FILTER_URI, Uri.encode(mSearchTerm));
            }

            // Returns a new CursorLoader for querying the Contacts table. No arguments are used
            // for the selection clause. The search string is either encoded onto the content URI,
            // or no contacts search string is used. The other search criteria are constants. See
            // the ContactsQuery interface.
            return new CursorLoader(PauseApplication.pauseActivity,
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }

        Log.e(TAG, "onCreateLoader - incorrect ID provided (" + id + ")");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // This swaps the new cursor into the adapter.
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            // When the loader is being reset, clear the cursor from the adapter. This allows the
            // cursor resources to be freed.
            mAdapter.swapCursor(null);
        }
    }
}
