package com.pauselabs.pause.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.pauselabs.R;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.ContactsQuery;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.SearchPrivacyListItem;

import java.util.HashSet;
import java.util.Locale;

/**
 * Created by Passa on 3/13/15.
 */
public class ContactsSearchAdapter extends ContactsAdapter {

    private TextAppearanceSpan highlightTextSpan; // Stores the highlight text appearance style
    private String mSearchTerm;

    /**
     * Instantiates a new Contacts Adapter.
     *
     * @param context A context that has access to the app's layout.
     */
    public ContactsSearchAdapter(Context context) {
        super(context);

        // Defines a span for highlighting the part of a display name that matches the search string
        highlightTextSpan = new TextAppearanceSpan(context, R.style.contactSearchTextHighlight);

        PauseApplication.pauseActivity.getSupportLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, this);
    }

    public void selectAll() {
        int count = getCount();
        for (int i = 0; i < count; i++) {
            SearchPrivacyListItem item = (SearchPrivacyListItem)getView(i,null,null).getTag();
            item.blackCheckbox.performClick();
        }
    }

    /**
     * Identifies the start of the search string in the display name column of a Cursor row.
     * E.g. If displayName was "Adam" and search query (mSearchTerm) was "da" this would
     * return 1.
     *
     * @param displayName The contact display name.
     * @return The starting position of the search string in the display name, 0-based. The
     * method returns -1 if the string is not found in the display name, or if the search
     * string is empty or null.
     */
    private int indexOfSearchQuery(String displayName) {
        if (!TextUtils.isEmpty(mSearchTerm)) {
            return displayName.toLowerCase(Locale.getDefault()).indexOf(mSearchTerm.toLowerCase(Locale.getDefault()));
        }
        return -1;
    }

    /**
     * Overrides newView() to inflate the list item views.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        final SearchPrivacyListItem item = (SearchPrivacyListItem) inflater.inflate(R.layout.search_privacy_list_item, null);;
        item.blackCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkbox = (CheckBox) v;

                if (checkbox.isChecked()) {
                    blackContacts.add(item.contactId);
                } else {
                    blackContacts.remove(item.contactId);
                }

                prefs.edit().putStringSet(Constants.Settings.BLACKLIST, new HashSet<>(blackContacts)).apply();

                PauseApplication.pauseActivity.privacyViewController.contactsGridAdapter.notifyDataSetChanged();
            }
        });
        item.iceCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkbox = (CheckBox) v;

                if (checkbox.isChecked()) {
                    iceContacts.add(item.contactId);
                } else {
                    iceContacts.remove(item.contactId);
                }

                prefs.edit().putStringSet(Constants.Settings.ICELIST, new HashSet<>(iceContacts)).apply();

                PauseApplication.pauseActivity.privacyViewController.contactsGridAdapter.notifyDataSetChanged();
            }
        });

        // Returns the item layout view
        return item;
    }

    /**
     * Binds data from the Cursor to the provided view.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Gets handles to individual view resources
        SearchPrivacyListItem item = (SearchPrivacyListItem) view;

        final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);
        final String contactId = cursor.getString(ContactsQuery.ID);

        final int startIndex = indexOfSearchQuery(displayName);

        if (startIndex == -1) {
            // If the user didn't do a search, or the search string didn't match a display
            // name, show the display name without highlighting
            item.contactNameField.setText(displayName);
//            holder.checkbox_added.setText(displayName);

        } else {
            // If the search string matched the display name, applies a SpannableString to
            // highlight the search string with the displayed display name

            // Wraps the display name in the SpannableString
            final SpannableString highlightedName = new SpannableString(displayName);

            // Sets the span to start at the starting point of the match and end at "length"
            // characters beyond the starting point
            highlightedName.setSpan(highlightTextSpan, startIndex, startIndex + mSearchTerm.length(), 0);

            // Binds the SpannableString to the display name View object
            item.contactNameField.setText(highlightedName);
//            holder.checkbox_ice.setText(highlightedName);
        }

        if(blackContacts.contains(contactId)){
            item.blackCheckbox.setChecked(true);
        } else {
            item.blackCheckbox.setChecked(false);
        }
        if(iceContacts.contains(contactId)){
            item.iceCheckbox.setChecked(true);
        } else {
            item.iceCheckbox.setChecked(false);
        }

        item.contactId = contactId;

    }

    public void updateSearchTerm(String term) {
        this.mSearchTerm = term;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contentUri;

        if (mSearchTerm == null) {
            contentUri = ContactsQuery.CONTENT_URI;
        } else {
            contentUri = Uri.withAppendedPath(ContactsQuery.FILTER_URI, Uri.encode(mSearchTerm));
        }

        return new CursorLoader(PauseApplication.pauseActivity,
                contentUri,
                ContactsQuery.PROJECTION,
                ContactsQuery.SELECTION,
                null,
                ContactsQuery.SORT_ORDER);
    }

}
