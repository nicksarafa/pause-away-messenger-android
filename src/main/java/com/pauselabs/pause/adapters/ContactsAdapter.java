package com.pauselabs.pause.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;

import com.pauselabs.pause.core.ContactsQuery;
import com.pauselabs.pause.model.Constants;

import java.util.HashSet;

/**
 * This is a subclass of CursorAdapter that supports binding Cursor columns to a view layout.
 * If those items are part of search results, the search string is marked by highlighting the
 * query text. An {@link AlphabetIndexer} is used to allow quicker navigation up and down the
 * ListView.
 */
public class ContactsAdapter extends CursorAdapter implements LoaderManager.LoaderCallbacks<Cursor> {

    protected LayoutInflater inflater; // Stores the layout inflater
    protected SharedPreferences prefs;

    protected Context c;

    public boolean usingIce;
    public HashSet<String> blackContacts;
    public HashSet<String> iceContacts;

    /**
     * Instantiates a new Contacts Adapter.
     * @param context A context that has access to the app's layout.
     */
    public ContactsAdapter(Context context, boolean usingIce) {
        super(context, null, true);

        c = context;

        // Stores inflater for use later
        inflater = LayoutInflater.from(context);

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        this.usingIce = usingIce;

        updatedContacts();
    }

    public void updatedContacts() {
        blackContacts = new HashSet<>(prefs.getStringSet(Constants.Settings.BLACKLIST, new HashSet<String>()));
        iceContacts = new HashSet<>(prefs.getStringSet(Constants.Settings.ICELIST, new HashSet<String>()));
    }

    /**
     * Overrides newView() to inflate the list item views.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {


        return null;
    }

    /**
     * Binds data from the Cursor to the provided view.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(c,
                ContactsQuery.CONTENT_URI,
                ContactsQuery.PROJECTION,
                ContactsQuery.SELECTION,
                null,
                ContactsQuery.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // This swaps the new cursor into the adapter.
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            // When the loader is being reset, clear the cursor from the adapter. This allows the
            // cursor resources to be freed.
            swapCursor(null);
        }
    }

}