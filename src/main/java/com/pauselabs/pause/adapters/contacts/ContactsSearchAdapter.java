package com.pauselabs.pause.adapters.contacts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.pauselabs.R;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.ContactsQuery;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.SearchPrivacyListItem;
import java.util.HashSet;
import java.util.Locale;

/** Created by Passa on 3/13/15. */
public class ContactsSearchAdapter extends ContactsAdapter {

  private TextAppearanceSpan highlightTextSpan; // Stores the highlight text appearance style
  private String mSearchTerm;

  /**
   * Instantiates a new Contacts Adapter.
   *
   * @param context A context that has access to the app's layout.
   */
  public ContactsSearchAdapter(Context context, boolean usingIce) {
    super(context, usingIce);

    // Defines a span for highlighting the part of a display name that matches the search string
    highlightTextSpan = new TextAppearanceSpan(context, R.style.contactSearchTextHighlight);
  }

  public void selectAll() {
    int count = getCount();
    for (int i = 0; i < count; i++) {
      SearchPrivacyListItem item = (SearchPrivacyListItem) getView(i, null, null).getTag();
      item.contactCheckbox.performClick();
    }
  }

  /**
   * Identifies the start of the search string in the display name column of a Cursor row. E.g. If
   * displayName was "Adam" and search query (mSearchTerm) was "da" this would return 1.
   *
   * @param displayName The contact display name.
   * @return The starting position of the search string in the display name, 0-based. The method
   *     returns -1 if the string is not found in the display name, or if the search string is empty
   *     or null.
   */
  private int indexOfSearchQuery(String displayName) {
    if (!TextUtils.isEmpty(mSearchTerm)) {
      return displayName
          .toLowerCase(Locale.getDefault())
          .indexOf(mSearchTerm.toLowerCase(Locale.getDefault()));
    }
    return -1;
  }

  /** Overrides newView() to inflate the list item views. */
  @Override
  public View newView(Context context, final Cursor cursor, ViewGroup viewGroup) {
    final SearchPrivacyListItem item =
        (SearchPrivacyListItem) inflater.inflate(R.layout.search_privacy_list_item, null);

    item.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            item.contactCheckbox.setChecked(!item.contactCheckbox.isChecked());
          }
        });
    item.contactCheckbox.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            CheckBox checkbox = (CheckBox) buttonView;

            Log.i("ON CHECKED", "id: " + item.contactId);

            if (checkbox.isChecked()) {
              if (usingIce) iceContacts.add(item.contactId);
              else blackContacts.add(item.contactId);
            } else {
              if (usingIce) iceContacts.remove(item.contactId);
              else blackContacts.remove(item.contactId);
            }

            if (usingIce)
              prefs
                  .edit()
                  .putStringSet(Constants.Settings.ICELIST, new HashSet<>(iceContacts))
                  .apply();
            else
              prefs
                  .edit()
                  .putStringSet(Constants.Settings.BLACKLIST, new HashSet<>(blackContacts))
                  .apply();

            PauseApplication.pauseActivity.updateUI();
          }
        });

    // Returns the item layout view
    return item;
  }

  /** Binds data from the Cursor to the provided view. */
  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);
    final String contactId = cursor.getString(ContactsQuery.ID);

    final SearchPrivacyListItem item = (SearchPrivacyListItem) view;
    item.contactId = contactId;

    if (usingIce) {
      if (iceContacts.contains(contactId)) item.contactCheckbox.setChecked(true);
      else item.contactCheckbox.setChecked(false);
    } else {
      if (blackContacts.contains(contactId)) item.contactCheckbox.setChecked(true);
      else item.contactCheckbox.setChecked(false);
    }

    final int startIndex = indexOfSearchQuery(displayName);

    if (startIndex == -1) {
      // If the user didn't do a search, or the search string didn't match a display
      // name, show the display name without highlighting
      item.contactNameField.setText(displayName);
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

    return new CursorLoader(
        c,
        contentUri,
        ContactsQuery.PROJECTION,
        ContactsQuery.SELECTION,
        null,
        ContactsQuery.SORT_ORDER);
  }
}
