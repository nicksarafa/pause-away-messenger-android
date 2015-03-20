package com.pauselabs.pause.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.pauselabs.R;
import com.pauselabs.pause.core.ContactsQuery;
import com.pauselabs.pause.view.tabs.PrivacyListItemView;

import java.util.Random;

/**
 * Created by Passa on 3/13/15.
 */
public class ContactsGridAdapter extends ContactsAdapter {

    private final String TAG = ContactsGridAdapter.class.getSimpleName();

    /**
     * Instantiates a new Contacts Adapter.
     *
     * @param context A context that has access to the app's layout.
     */
    public ContactsGridAdapter(Context context, boolean usingIce) {
        super(context, usingIce);


    }

    /**
     * Overrides newView() to inflate the list item views.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        PrivacyListItemView item = (PrivacyListItemView) inflater.inflate(R.layout.privacy_list_item_view,null);

        return item;
    }

    /**
     * Binds data from the Cursor to the provided view.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        PrivacyListItemView item = (PrivacyListItemView)view;

        final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);
        final String contactId = cursor.getString(ContactsQuery.ID);

        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        int randomColor = Color.rgb(r,g,b);

        // Set Text Drawable Initial Extraction

        String contactInitials = "";
        char firstInitial = displayName.charAt(0);

        if ((firstInitial >= 65 && firstInitial <= 90) || (firstInitial >= 97 && firstInitial <= 122)) {

            String[] nameArray = displayName.split(" ");

            contactInitials = nameArray[0].substring(0,1);

            if(nameArray.length > 1) {

                contactInitials += nameArray[nameArray.length - 1].substring(0,1);

            }

        } else if (firstInitial >= 48 && firstInitial <= 57) {

            contactInitials = "#";

        } else {

            contactInitials = "@";
        }

        // Set Text Drawable to PrivacyListItem & customize

        TextDrawable contactInitialsDrawable = TextDrawable.builder()
                .beginConfig()
                    .textColor(Color.WHITE)
                    .toUpperCase()
                .endConfig()

        .buildRound(contactInitials, randomColor);

        item.privacyContactInitials.setImageDrawable(contactInitialsDrawable);
        item.privacyListItemContactName.setText(displayName);
        item.contactId = contactId;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int numContacts;
        String[] contactsArray;

        if (usingIce) {
            numContacts = iceContacts.size();
            contactsArray = iceContacts.toArray(new String[numContacts]);
        } else {
            numContacts = blackContacts.size();
            contactsArray = blackContacts.toArray(new String[numContacts]);
        }

        String SELECT;
        String[] SELECTARGS;
        if (numContacts == 0) {
            SELECT = "0";
            SELECTARGS = null;
        } else {
            SELECT = ContactsContract.Contacts._ID + " IN (" + makePlaceholders(numContacts) + ")";
            SELECTARGS = contactsArray;
        }

        return new CursorLoader(c,
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
