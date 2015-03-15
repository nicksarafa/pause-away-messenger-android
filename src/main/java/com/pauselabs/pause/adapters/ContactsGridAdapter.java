package com.pauselabs.pause.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.pauselabs.R;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.ContactsQuery;
import com.pauselabs.pause.view.tabs.ContactsGridViewItem;

/**
 * Created by Passa on 3/13/15.
 */
public class ContactsGridAdapter extends ContactsAdapter {

    /**
     * Instantiates a new Contacts Adapter.
     *
     * @param context A context that has access to the app's layout.
     */
    public ContactsGridAdapter(Context context) {
        super(context);

        PauseApplication.pauseActivity.getSupportLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, this);
    }

    /**
     * Overrides newView() to inflate the list item views.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ContactsGridViewItem item = (ContactsGridViewItem) inflater.inflate(R.layout.contacts_grid_view_item,null);


        return item;
    }

    /**
     * Binds data from the Cursor to the provided view.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ContactsGridViewItem item = (ContactsGridViewItem)view;

        final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);
        final String contactId = cursor.getString(ContactsQuery.ID);

        item.nameField.setText(displayName);
        item.contactId = contactId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int numContacts = iceContacts.size();
        String[] contactsArray = iceContacts.toArray(new String[numContacts]);

        String SELECT;
        String[] SELECTARGS;
        if (numContacts < 1) {
            SELECT = "0";
            SELECTARGS = null;
        } else {
            SELECT = ContactsContract.Contacts._ID + " IN (" + makePlaceholders(numContacts) + ")";
            SELECTARGS = contactsArray;
        }

        return new CursorLoader(PauseApplication.pauseActivity,
                ContactsQuery.CONTENT_URI,
                ContactsQuery.PROJECTION,
                SELECT,
                SELECTARGS,
                ContactsQuery.SORT_ORDER);
    }

    private String makePlaceholders(int len) {
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }

}
