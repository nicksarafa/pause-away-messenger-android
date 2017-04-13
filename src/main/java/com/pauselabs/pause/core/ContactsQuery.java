package com.pauselabs.pause.core;

import android.net.Uri;
import android.provider.ContactsContract.Contacts;

/**
 * This interface defines constants for the Cursor and CursorLoader, based on constants defined in
 * the {@link android.provider.ContactsContract.Contacts} class.
 */
public interface ContactsQuery {

  // An identifier for the loader
  static final int QUERY_ID = 1;

  // A content URI for the Contacts table
  static final Uri CONTENT_URI = Contacts.CONTENT_URI;

  // The search/filter query Uri
  static final Uri FILTER_URI = Contacts.CONTENT_FILTER_URI;

  // The selection clause for the CursorLoader query. The search criteria defined here
  // restrict results to contacts that have a display name and a phone number.
  static final String SELECTION =
      Contacts.DISPLAY_NAME_PRIMARY + "<>''" + " AND " + Contacts.HAS_PHONE_NUMBER;

  // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
  // sort key allows for localization. In earlier versions. use the display name as the sort
  // key.
  static final String SORT_ORDER = Contacts.SORT_KEY_PRIMARY;

  // The projection for the CursorLoader query. This is a list of columns that the Contacts
  // Provider should return in the Cursor.
  static final String[] PROJECTION = {

    // The contact's row id
    Contacts._ID,

    // A pointer to the contact that is guaranteed to be more permanent than _ID. Given
    // a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate
    // a "permanent" contact URI.
    Contacts.LOOKUP_KEY,

    // In platform version 3.0 and later, the Contacts table contains
    // DISPLAY_NAME_PRIMARY, which either contains the contact's displayable name or
    // some other useful identifier such as an email address. This column isn't
    // available in earlier versions of Android, so you must use Contacts.DISPLAY_NAME
    // instead.
    Contacts.DISPLAY_NAME_PRIMARY,

    // In Android 3.0 and later, the thumbnail image is pointed to by
    // PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct pointer; instead,
    // you generate the pointer from the contact's ID value and constants defined in
    // android.provider.ContactsContract.Contacts.
    Contacts.PHOTO_THUMBNAIL_URI,

    // The sort order column for the returned Cursor, used by the AlphabetIndexer
    SORT_ORDER,
  };

  // The query column numbers which map to each value in the projection
  static final int ID = 0;
  static final int LOOKUP_KEY = 1;
  static final int DISPLAY_NAME = 2;
  static final int PHOTO_THUMBNAIL_DATA = 3;
  static final int SORT_KEY = 4;
}
